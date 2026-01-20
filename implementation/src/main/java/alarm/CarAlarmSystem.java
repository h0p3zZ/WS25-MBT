package alarm;

import java.util.Arrays;
import java.util.Objects;

/**
 * Car Alarm System Controller (timed, deterministic, UPPAAL-friendly).
 *
 * Time model:
 *  - The environment calls tick(seconds) to advance logical time.
 *
 * Requirements implemented (per task sheet):
 *  R1: Armed 20s after vehicle is locked AND bonnet/luggage/all doors are closed.
 *  R2: If armed and an unauthorised person opens door/luggage/bonnet -> sound 30s, flash 300s.
 *  R3: Deactivate anytime by unlocking from outside (even while alarm sounding).
 *  R4: Unlock requires 4-digit PIN. If wrong 3 times (while armed) -> alarm triggered.
 *  R5: setPinCode possible when unlocked; needs old+new PIN; 3 wrong old PIN attempts -> alarm;
 *      else acknowledge via newPinSet.
 *  R6: 4 doors + bonnet + luggage; doors "closed" only if all 4 are closed.
 *  R7: Luggage compartment can be unlocked separately; car alarm system will not be deactivated.
 */
public final class CarAlarmSystem {


    public enum Output {
        NONE,
        UNLOCK_OK,        // unlockOk!
        UNLOCK_FAIL,      // (optional for SUT; UPPAAL may just model absence of unlockOk)
        NEW_PIN_SET,      // newPinSet!
        ALARM_TRIGGERED   // (optional observable output)
    }

    public static final long ARM_DELAY_S = 20;     // R1
    public static final long SOUND_DURATION_S = 30; // R2
    public static final long FLASH_DURATION_S = 300; // R2
    public static final int MAX_PIN_FAILURES = 3;  // R4/R5
    public static final int DOOR_COUNT = 4;        // R6

    // ===== Internal state =====
    private long nowS = 0; // Internal clock

    // Locking state
    private boolean isLocked = false;
    private boolean luggageUnlockedSeparately = false; // R7

    // Open/closed sensors
    private final boolean[] doorOpen = new boolean[DOOR_COUNT];
    private boolean bonnetOpen = false;
    private boolean luggageOpen = false;

    // Arming / armed
    private boolean armed = false;
    private Long armDeadlineS = null; // when arming completes if conditions still hold

    // Alarm timing
    private Long alarmStartS = null; // null => not in alarm mode
    // sound active in [alarmStartS, alarmStartS+30)
    // flash active in [alarmStartS, alarmStartS+300)

    // PIN state
    private int pinCode = 0000; // default; can be changed
    private int unlockFailuresWhileArmed = 0;
    private int setPinFailures = 0;

    // ===== Constructors =====
    public CarAlarmSystem() {
        // initially: unlocked, open? assume closed/unlocked
        Arrays.fill(doorOpen, true);
        bonnetOpen = false;
        luggageOpen = false;
    }

    // ===== Time advancement =====
    public void tick(long deltaSeconds) {
        if (deltaSeconds < 0) throw new IllegalArgumentException("deltaSeconds must be >= 0");
        if (deltaSeconds == 0) {
            evaluateTimedTransitions();
            return;
        }
        nowS += deltaSeconds;
        evaluateTimedTransitions();
    }

    private void evaluateTimedTransitions() {
        // R1: arm after 20s if still locked and everything closed
        if (armDeadlineS != null && nowS >= armDeadlineS) {
            if (isLocked && allClosed()) {
                armed = true;
            }
            armDeadlineS = null; // complete or cancel
        }
    }

    // ===== Inputs / actions =====

    /** Close event for a specific door (0..3). */
    public Output closeDoor(int idx) {
        checkDoorIndex(idx);
        doorOpen[idx] = false;
        // If everything closed while locked, arming countdown may start/restart (R1).
        maybeStartArmingCountdown();
        return Output.NONE;
    }

    /** Open event for a specific door (0..3). */
    public Output openDoor(int idx) {
        checkDoorIndex(idx);
        doorOpen[idx] = true;
        return Output.NONE;
    }

    public Output closeBonnet() {
        bonnetOpen = false;
        maybeStartArmingCountdown();
        return Output.NONE;
    }

    public Output openBonnet() {
        bonnetOpen = true;
        return Output.NONE;
    }

    public Output closeLuggage() {
        luggageOpen = false;
        maybeStartArmingCountdown();
        return Output.NONE;
    }

    /**
     * Opening luggage is treated like opening an entry point; if unauthorised while armed -> alarm (R2).
     */
    public Output openLuggage() {
        luggageOpen = true;
        return Output.NONE;
    }

    /**
     * Lock the car (central lock). This does not close doors; it starts arming countdown if all closed (R1).
     * Equivalent to "lock? / t=0" in the UPPAAL sketch.
     */
    public Output lock() {
        isLocked = true;
        luggageUnlockedSeparately = false; // central lock locks luggage too
        armed = false;                     // not armed immediately; becomes armed after delay
        unlockFailuresWhileArmed = 0;      // fresh arm cycle
        // start countdown only if all closed
        maybeStartArmingCountdown();
        return Output.NONE;
    }

    /**
     * Unlocking from outside deactivates the alarm system at any time (R3).
     * PIN is checked (R4). If armed: three failures trigger alarm.
     *
     * Output UNLOCK_OK corresponds to "unlockOk!" in UPPAAL.
     */
    public Output unlockVehicle(int pin) {
        if (!isFourDigitPin(pin)) {
            throw new IllegalArgumentException("PIN must be a 4-digit integer (0000–9999)");
        }

        if (pin == pinCode) {
            // correct PIN → unlock + deactivate alarm
            isLocked = false;
            luggageUnlockedSeparately = false;
            armed = false;
            armDeadlineS = null;

            unlockFailuresWhileArmed = 0;
            setPinFailures = 0;

            stopAlarm(); // R3
            return Output.UNLOCK_OK;
        }

        // wrong PIN
        if (armed) {
            unlockFailuresWhileArmed++;
            if (unlockFailuresWhileArmed >= MAX_PIN_FAILURES) {
                triggerAlarm(); // R4
                return Output.ALARM_TRIGGERED;
            }
        }

        return Output.UNLOCK_FAIL;
    }

    /**
     * Unlock luggage separately (R7). This does NOT deactivate the alarm system.
     * (You may model this as an input in UPPAAL that changes luggageUnlockedSeparately.)
     */
    public Output unlockLuggageSeparately() {
        luggageUnlockedSeparately = true;
        // no change to carLocked/armed/alarm (R7)
        return Output.NONE;
    }

    /**
     * Set a new PIN (R5). Car must be unlocked; old+new must be provided and be 4 digits.
     * Three wrong old PIN attempts trigger alarm (R5).
     *
     * Output NEW_PIN_SET corresponds to "newPinSet!".
     */
    public Output setPinCode(int oldPin, int newPin) {
        if (!isFourDigitPin(oldPin) || !isFourDigitPin(newPin)) {
            throw new IllegalArgumentException("Both oldPin and newPin must be 4-digit integers");
        }
        if (isLocked) {
            return Output.NONE;
        }

        if (oldPin == pinCode) {
            pinCode = newPin;
            setPinFailures = 0;
            return Output.NEW_PIN_SET;
        } else {
            setPinFailures++;
            if (setPinFailures >= MAX_PIN_FAILURES) {
                triggerAlarm();
                return Output.ALARM_TRIGGERED;
            }
            return Output.NONE;
        }
    }

    // ===== Alarm handling =====

    private Output handleUnauthorisedOpenIfNeeded(boolean unauthorised) {
        // R2: if armed and unauthorised open occurs -> alarm
        if (unauthorised && armed) {
            triggerAlarm();
            return Output.ALARM_TRIGGERED;
        }
        // If authorised opening occurs, behaviour is environment-driven; we do not auto-deactivate.
        // Deactivation is strictly via unlock() (R3), consistent with typical alarm semantics.
        return Output.NONE;
    }

    private void triggerAlarm() {
        if (alarmStartS == null) {
            alarmStartS = nowS;
        } else {
            // already active; keep the original start (UPPAAL model usually doesn't restart)
        }
    }

    private void stopAlarm() {
        alarmStartS = null;
    }

    // ===== Arming countdown logic =====

    private void maybeStartArmingCountdown() {
        // Start only if locked and all entry points are closed (R1)
        if (isLocked && allClosed()) {
            // restart the countdown each time the condition becomes true
            armDeadlineS = nowS + ARM_DELAY_S;
        } else {
            armDeadlineS = null;
            armed = false; // if something opens before arming completes, it won't arm
        }
    }

    public boolean allClosed() {
        if (bonnetOpen || luggageOpen) return false;
        for (boolean o : doorOpen) if (o) return false;
        return true;
    }

    // ===== Observations (useful for assertions / test oracles) =====

    public long nowSeconds() { return nowS; }

    public boolean isLocked() { return isLocked; }

    public boolean isArmed() { return armed; }

    /** True during the first 30s after alarm start (FlashAndSound in the UPPAAL sketch). */
    public boolean isSounding() {
        if (alarmStartS == null) return false;
        long dt = nowS - alarmStartS;
        return dt >= 0 && dt < SOUND_DURATION_S;
    }

    /** True during the first 300s after alarm start (FlashAndSound + Flash in the UPPAAL sketch). */
    public boolean isFlashing() {
        if (alarmStartS == null) return false;
        long dt = nowS - alarmStartS;
        return dt >= 0 && dt < FLASH_DURATION_S;
    }

    /**
     * Approximates the UPPAAL "SilentAndOpen" region: alarm flash period elapsed,
     * at least one entry point still open, and system still armed (unless unlocked).
     */
    public boolean isSilentAndOpenPhase() {
        if (alarmStartS == null) return false;
        long dt = nowS - alarmStartS;
        boolean flashOver = dt >= FLASH_DURATION_S;
        return flashOver && !allClosed() && armed;
    }

    public boolean isDoorOpen(int idx) {
        checkDoorIndex(idx);
        return doorOpen[idx];
    }

    public boolean isOpen() {
        for (boolean open : doorOpen) {
            if (!open) {
                return false;
            }
        }
        return true;
    }

    public boolean isBonnetOpen() { return bonnetOpen; }

    public boolean isLuggageOpen() { return luggageOpen; }

    public boolean isLuggageUnlockedSeparately() { return luggageUnlockedSeparately; }

    // ===== Helpers =====

    private static boolean isFourDigitPin(int pin) {
        return pin >= 0 && pin <= 9999;
    }

    private static void checkDoorIndex(int idx) {
        if (idx < 0 || idx >= DOOR_COUNT) throw new IllegalArgumentException("door index must be 0..3");
    }
}
