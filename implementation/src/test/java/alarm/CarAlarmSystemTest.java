package alarm;
import alarm.CarAlarmSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CarAlarmSystemTest {

    @Test
    void testcase003_lockAndCloseAllEntryPoints_thenArmsAfter20Seconds() {
        CarAlarmSystem sut = new CarAlarmSystem();

        // UPPAAL step 1: sut.lock();
        sut.lock();
        assertTrue(sut.isLocked(), "After lock(), car must be locked");

        // UPPAAL steps 2-5: sut.closeDoor(id); four times
        // Concrete assumption: id ranges over the 4 doors (0..3)
        sut.closeDoor(0);
        sut.closeDoor(1);
        sut.closeDoor(2);
        sut.closeDoor(3);

        // UPPAAL step 6: sut.closeBonnet()
        sut.closeBonnet();

        // UPPAAL step 7: stu.closeLuggage()  (typo fixed to sut)
        sut.closeLuggage();

        // The UPPAAL trace contains no explicit delay, but R1 requires 20s to arm.
        sut.tick(19);
        assertFalse(sut.isArmed(), "Must not be armed before 20 seconds elapsed");

        sut.tick(1);
        assertTrue(sut.isArmed(), "Must be armed after 20 seconds when locked and all entry points are closed");
    }

    @Test
    void TEST_FILENAME() {
        CarAlarmSystem sut = new CarAlarmSystem();
        sut.lock();
        assertTrue(sut.isLocked() && sut.isOpen());

        sut.closeDoor(0);
        sut.closeDoor(1);
        sut.closeDoor(2);
        sut.closeDoor(3);
        sut.closeBonnet();
        sut.closeLuggage();
        assertTrue(sut.isLocked() && !sut.isOpen());

    }

    @Test
    void TEST_FILENAME2() {
        CarAlarmSystem sut = new CarAlarmSystem();
        sut.closeDoor(1);
        sut.closeDoor(0);
        sut.openDoor(0);
        sut.closeBonnet();
        sut.unlockVehicle(0);
        sut.openBonnet();
        sut.openDoor(1);
        sut.closeDoor(0);
        sut.closeLuggage();
        sut.unlockVehicle(1233);
        sut.unlockVehicle(1230);
        sut.closeDoor(1);
        sut.closeDoor(3);
        sut.openDoor(0);
        sut.closeDoor(0);
    }

    @Test
    void TEST_FILENAME3() {
        CarAlarmSystem sut = new CarAlarmSystem();

        sut.lock();





        assertTrue(sut.isLocked() && sut.isOpen());

        sut.unlockVehicle(0);







    }
}
