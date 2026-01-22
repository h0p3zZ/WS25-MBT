package alarm;

import java.util.Arrays;

public final class CarAlarmSystem {

    //Requirements timing constants
    public static final long ARM_DELAY_S = 20;        // R1
    public static final long SOUND_DURATION_S = 30;   // R2
    public static final long FLASH_DURATION_S = 300;  // R2
    public static final int MAX_PIN_FAILURES = 3;     // R4/R5
    public static final int DOOR_COUNT = 4;           // R6

    public enum State {
        UNLOCKED_DISARMED,
        LOCKED_DISARMED,
        ARMING,
        ARMED,
        FLASH_AND_SOUND,
        FLASH,
        SILENT_AND_OPEN
    }

    //Time
    private long nowS = 0;

    //Sensors / entry points
    private final boolean[] doorOpen = new boolean[DOOR_COUNT];
    private boolean bonnetOpen = true;
    private boolean luggageOpen = true;

    //Lock / luggage separate unlock
    private boolean carLocked;
    private boolean luggageUnlockedSeparately = false;

    //PIN
    private int pinCode = 1234;
    private int wrongUnlockAttemptsWhileArmed = 0;
    private int wrongOldPinAttempts = 0;

    // ===== State + timers
    private State state;

    private Long armingDeadlineS = null; // time when ARMING completes
    private Long alarmStartS = null;     // time when alarm triggered

    public CarAlarmSystem() {
        Arrays.fill(doorOpen, true);

        luggageOpen = true;
        carLocked = false;
        state = State.UNLOCKED_DISARMED;

        nowS = 0;
        armingDeadlineS = null;
        alarmStartS = null;
    }

    public String getState() {
        switch (state) {
            case ARMED -> {
                return "Armed";
            } case ARMING -> {
                return "Arming";
            } case UNLOCKED_DISARMED -> {
                return "UnlockedDisarmed";
            } case LOCKED_DISARMED -> {
                return "LockedDisarmed";
            } case SILENT_AND_OPEN -> {
                return "SilentAndOpen";
            } case FLASH -> {
                return "Flash";
            } case FLASH_AND_SOUND -> {
                return "FlashAndsound";
            }
        }
        return "";
    }

    public boolean isLocked() {
        return carLocked;
    }

    /**
     * "Armed" in the requirements sense: system is armed OR an alarm phase is ongoing.
     */
    public boolean isArmed() {
        return state == State.ARMED
                || state == State.FLASH_AND_SOUND
                || state == State.FLASH
                || state == State.SILENT_AND_OPEN;
    }

    public int getPinCode() {
        return pinCode;
    }

    public boolean isOpen() {
        for (boolean o : doorOpen) {
            if (o) return true;
        }
        return false;
    }

    public boolean luggageOpen() {
        return luggageOpen;
    }

    public boolean bonnetOpen() {
        return bonnetOpen;
    }


    // Time progression
    public void tick(long deltaSeconds) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds must be >= 0");
        }
        nowS += deltaSeconds;
        evaluateTimedTransitions();
    }

    private void evaluateTimedTransitions() {
        // R1: ARMING -> ARMED after 20s if still locked and all closed
        if (state == State.ARMING && armingDeadlineS != null && nowS >= armingDeadlineS) {
            armingDeadlineS = null;
            if (carLocked && allClosed()) {
                state = State.ARMED;
                wrongUnlockAttemptsWhileArmed = 0;
            } else {
                state = carLocked ? State.LOCKED_DISARMED : State.UNLOCKED_DISARMED;
            }
        }

        // R2: Alarm timing progression
        if (alarmStartS != null) {
            long dt = nowS - alarmStartS; // seconds since alarm triggered

            if (state == State.FLASH_AND_SOUND && dt >= SOUND_DURATION_S) {
                state = State.FLASH;
            }

            if (state == State.FLASH && dt >= FLASH_DURATION_S) {
                alarmStartS = null; // end timed alarm phase
                if (!allClosed()) {
                    state = State.SILENT_AND_OPEN;
                } else {
                    state = carLocked ? State.ARMED : State.UNLOCKED_DISARMED;
                }
            }
        }
    }

    // Actions

    /**
     * Lock vehicle (central lock). R1: starts ARMING if all closed.
     */
    public void lock() {
        carLocked = true;
        luggageUnlockedSeparately = false;
        wrongUnlockAttemptsWhileArmed = 0;

        if (allClosed()) {
            state = State.ARMING;
            armingDeadlineS = nowS + ARM_DELAY_S;
        } else {
            state = State.LOCKED_DISARMED;
            armingDeadlineS = null;
        }
    }

    /**
     * Unlock from outside with PIN. R3: deactivates anytime on correct PIN.
     * R4: if wrong PIN 3 times while armed => alarm.
     */
    public void unlockVehicle(int pin) {
        //requireFourDigitPin(pin);

        if (pin == pinCode) {
            carLocked = false;
            luggageUnlockedSeparately = false;
            armingDeadlineS = null;
            wrongUnlockAttemptsWhileArmed = 0;
            wrongOldPinAttempts = 0;

            stopAlarm();
            state = State.UNLOCKED_DISARMED;
            return;
        }

        // Wrong PIN
        if (state == State.ARMED || state == State.ARMING || state == State.FLASH_AND_SOUND || state == State.FLASH || state == State.SILENT_AND_OPEN) {
            wrongUnlockAttemptsWhileArmed++;
            if (wrongUnlockAttemptsWhileArmed >= MAX_PIN_FAILURES) {
                triggerAlarm();
            }
        }
        // If not armed, wrong unlock has no special effect
    }

    // ---- Doors (R6: 4 doors) ----
    public void openDoor(int idx) {
        //checkDoorIndex(idx);
        doorOpen[idx] = true;
        onEntryPointOpened();
    }

    public void closeDoor(int idx) {
        //checkDoorIndex(idx);
        doorOpen[idx] = false;
        onEntryPointClosed();
    }

    // ---- Bonnet ----
    public void openBonnet() {
        bonnetOpen = true;
        onEntryPointOpened();
    }

    public void closeBonnet() {
        bonnetOpen = false;
        onEntryPointClosed();
    }

    // ---- Luggage ----
    public void openLuggage() {
        luggageOpen = true;
        onEntryPointOpened();
    }

    public void closeLuggage() {
        luggageOpen = false;
        onEntryPointClosed();
    }

    /**
     * R7: luggage can be unlocked separately without deactivating the alarm system.
     * This is "unlock" only, not "open".
     */
    public void unlockLuggage() {
        luggageUnlockedSeparately = true;
    }

    public void lockLuggage() {
        luggageUnlockedSeparately = false;
        // no other state change
    }

    /**
     * R5: setPinCode requires (oldPin,newPin) and vehicle must be unlocked.
     * 3 wrong oldPin attempts -> alarm.
     */
    public void setPinCode(int oldPin, int newPin) {
        //requireFourDigitPin(oldPin);
        //requireFourDigitPin(newPin);

        if (carLocked) {
            // Only when unlocked (R5)
            return;
        }

        if (oldPin == pinCode) {
            pinCode = newPin;
            wrongOldPinAttempts = 0;
            return;
        }

        wrongOldPinAttempts++;
        if (wrongOldPinAttempts >= MAX_PIN_FAILURES) {
            triggerAlarm();
        }
    }

    // Internal transition helpers
    private void onEntryPointOpened() {
        //R2: If armed and someone opens any entry point => alarm
        if (state == State.ARMED) {
            triggerAlarm();
            return;
        }

        // If arming and something opens => cancel arming
        if (state == State.ARMING) {
            armingDeadlineS = null;
            state = State.LOCKED_DISARMED;
        }
    }

    private void onEntryPointClosed() {
        if (carLocked) {
            if (allClosed()) {
                if (state == State.LOCKED_DISARMED) {
                    state = State.ARMING;
                    armingDeadlineS = nowS + ARM_DELAY_S;
                } else if (state == State.SILENT_AND_OPEN) {
                    state = State.ARMED;
                }
            } else {
                if (state == State.ARMING) {
                    armingDeadlineS = null;
                    state = State.LOCKED_DISARMED;
                }
            }
        } else {

            if (state == State.LOCKED_DISARMED || state == State.ARMING || state == State.ARMED) {
                state = State.UNLOCKED_DISARMED;
                armingDeadlineS = null;
            }
        }
    }

    private void triggerAlarm() {
        alarmStartS = nowS;
        state = State.FLASH_AND_SOUND;
    }

    private void stopAlarm() {
        alarmStartS = null;
    }

    private boolean allClosed() {
        if (bonnetOpen || luggageOpen) return false;
        for (boolean o : doorOpen) if (o) return false;
        return true;
    }

    /*private static void checkDoorIndex(int idx) {
        if (idx < 0 || idx >= DOOR_COUNT) {
            throw new IllegalArgumentException("door index must be 0..3");
        }
    }*/

    /*private static void requireFourDigitPin(int pin) {
        if (pin < 0 || pin > 9999) {
            throw new IllegalArgumentException("PIN must be in range 0000..9999");
        }
    }*/
}
