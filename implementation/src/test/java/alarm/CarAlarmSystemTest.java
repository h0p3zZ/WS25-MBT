package alarm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CarAlarmSystemTest {

    @Test
    void testMethod() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
        sut.tick(30);
        assertEquals(sut.getState(), "Flash");
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && sut.isOpen());
    }

    @Test
    void testMethod1() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
        sut.lockLuggage();
    }

    @Test
    void testMethod2() {
        CarAlarmSystem sut = new CarAlarmSystem();

        sut.lock();
        assertTrue(sut.isLocked() && sut.isOpen());
        sut.unlockVehicle(1230);
        assertTrue(sut.isLocked());
        sut.unlockVehicle(1230);
        assertTrue(sut.isLocked());
        sut.unlockVehicle(1230);
        assertTrue(sut.isLocked());
        sut.closeDoor(0);
        sut.closeDoor(1);
        sut.closeDoor(2);
        sut.closeDoor(3);
        sut.closeBonnet();
        sut.closeLuggage();
        assertTrue(sut.isLocked() && !sut.isOpen());
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockVehicle(1230);
        sut.unlockVehicle(1230); //added
        sut.unlockVehicle(1230); //added
        assertEquals(sut.getState(), "FlashAndsound");
        assertTrue(sut.isLocked());
    }

    @Test
    void testMethod3() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && !sut.isOpen());
    }

    @Test
    void testMethod4() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
        sut.openLuggage();
        assertTrue(sut.isArmed());
        assertTrue(sut.luggageOpen());
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && !sut.isOpen());
    }

    @Test
    void testMethod5() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
        sut.openLuggage();
        assertTrue(sut.isArmed());
        assertTrue(sut.luggageOpen());
        sut.closeLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
    }

    @Test
    void testMethod6() {
        CarAlarmSystem sut = new CarAlarmSystem();

        sut.lock();
        assertTrue(sut.isLocked() && sut.isOpen());
    }

    @Test
    void testMethod7() {
        CarAlarmSystem sut = new CarAlarmSystem();
        sut.closeDoor(0);
        sut.closeDoor(1);
        sut.closeDoor(2);
        sut.closeDoor(3);
        sut.closeBonnet();
        sut.closeLuggage();
        assertTrue(!sut.isLocked() && !sut.isOpen());

    }

    @Test
    void testMethod8() {
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
    void testMethod9() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
    }

    @Test
    void testMethod10() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
    }

    @Test
    void testMethod11() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
        sut.tick(30);
        assertEquals(sut.getState(), "Flash");
        sut.tick(270);
        assertEquals(sut.getState(), "SilentAndOpen");
    }

    @Test
    void testMethod12() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.unlockLuggage();
        assertTrue(sut.isArmed());
        assertFalse(sut.luggageOpen());
        sut.openLuggage();
        assertTrue(sut.isArmed());
        assertTrue(sut.luggageOpen());
    }

    @Test
    void testMethod13() {
        CarAlarmSystem sut = new CarAlarmSystem();
        sut.setPinCode(1230, 1230);
        assertEquals(sut.getPinCode(), 1234);
    }

    @Test
    void testMethod14() {
        CarAlarmSystem sut = new CarAlarmSystem();

        sut.setPinCode(1237, 1234);
        assertEquals(sut.getPinCode(), 1234);
        sut.setPinCode(1232, 1230);
        assertEquals(sut.getPinCode(), 1234);
        sut.setPinCode(1236, 1231);
        assertEquals(sut.getPinCode(), 1234);
        sut.closeLuggage();
        sut.setPinCode(1234, 1232);
        assertEquals(sut.getPinCode(), 1232);
        sut.setPinCode(1238, 1242);
        assertEquals(sut.getPinCode(), 1232);
    }

    @Test
    void testMethod15() {
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
        sut.openDoor(0);
        assertTrue(sut.isLocked() && sut.isOpen());
    }

    @Test
    void testMethod16() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
        sut.tick(30);
        assertEquals(sut.getState(), "Flash");
        sut.tick(270);
        assertEquals(sut.getState(), "SilentAndOpen");
        sut.closeDoor(0);
        assertTrue(sut.isArmed());
    }

    @Test
    void testMethod17() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
        sut.tick(30);
        assertEquals(sut.getState(), "Flash");
        sut.tick(270);
        assertEquals(sut.getState(), "SilentAndOpen");
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && sut.isOpen());
    }

    @Test
    void testMethod18() {
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
        sut.tick(20);
        assertTrue(sut.isArmed());
        sut.openDoor(0);
        assertEquals(sut.getState(), "FlashAndsound");
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && sut.isOpen());
    }

    @Test
    void testMethod19() {
        CarAlarmSystem sut = new CarAlarmSystem();
        sut.closeDoor(0);
        sut.closeDoor(1);
        sut.closeDoor(2);
        sut.closeDoor(3);
        sut.closeBonnet();
        sut.closeLuggage();
        assertTrue(!sut.isLocked() && !sut.isOpen());
        sut.lock();
        assertTrue(sut.isLocked() && !sut.isOpen());
    }

    @Test
    void testMethod20() {
        CarAlarmSystem sut = new CarAlarmSystem();

        sut.lock();
        assertTrue(sut.isLocked() && sut.isOpen());
        sut.unlockVehicle(1234);
        assertTrue(!sut.isLocked() && sut.isOpen());
    }


}
