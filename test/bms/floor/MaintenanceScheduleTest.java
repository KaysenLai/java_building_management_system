package bms.floor;

import bms.exceptions.DuplicateSensorException;
import bms.room.Room;
import bms.room.RoomState;
import bms.room.RoomType;
import bms.sensors.TemperatureSensor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/** Use JUnit Test 4 to test the MaintenanceSchedule class.*/
public class MaintenanceScheduleTest {

    private Room room1, room2, room3;
    private List<Room> roomOrder = new ArrayList<>();
    private MaintenanceSchedule MS;

    @Before
    public void setup() {
        room1 = new Room(101, RoomType.STUDY, 5);
        room2 = new Room(102, RoomType.OFFICE, 15.786);
        room3 = new Room(103, RoomType.LABORATORY, 20.25);

        roomOrder.add(room1);
        roomOrder.add(room2);
        roomOrder.add(room3);

        MS = new MaintenanceSchedule(roomOrder);
    }

    @Test
    public void testConstructor() {
        Assert.assertSame(room1.evaluateRoomState(), RoomState.MAINTENANCE);
        Assert.assertNotSame(room2.evaluateRoomState(), RoomState.MAINTENANCE);
        Assert.assertNotSame(room3.evaluateRoomState(), RoomState.MAINTENANCE);
    }

    @Test
    public void testGetMaintenanceTimeSTUDY() {
        Assert.assertEquals(5, MS.getMaintenanceTime(room1));
    }

    @Test
    public void testGetMaintenanceTimeLABORATORY() {
        Assert.assertEquals(11, MS.getMaintenanceTime(room2));
    }

    @Test
    public void testGetMaintenanceTimeStudy() {
        Assert.assertEquals(16, MS.getMaintenanceTime(room3));
    }

    @Test
    public void testGetCurrentRoom() {
        Assert.assertEquals(room1, MS.getCurrentRoom());
    }

    @Test
    public void testGetTimeElapsedCurrentRoom() {
        Assert.assertEquals(0, MS.getTimeElapsedCurrentRoom());
    }



    @Test
    public void testElapseOneMinuteEVACUATE() {
        TemperatureSensor t1 = new TemperatureSensor(new int[]{85});
        try {
            room1.addSensor(t1);
        } catch (DuplicateSensorException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 12; i++) {
            MS.elapseOneMinute();
        }
        Assert.assertEquals(room1, MS.getCurrentRoom());
        Assert.assertEquals(0, MS.getTimeElapsedCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteElapse() {
        for (int i = 0; i < 2; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room1, MS.getCurrentRoom());
        Assert.assertEquals(2, MS.getTimeElapsedCurrentRoom());
    }


    @Test
    public void testElapseOneMinuteNextRoom() {
        for (int i = 0; i < 5; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room2, MS.getCurrentRoom());
        Assert.assertEquals(0, MS.getTimeElapsedCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteSkip() {
        for (int i = 0; i < 18; i++)
            MS.elapseOneMinute();
        MS.skipCurrentMaintenance();
        Assert.assertEquals(room1, MS.getCurrentRoom());
        Assert.assertEquals(0, MS.getTimeElapsedCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteSkipAndElapse() {
        for (int i = 0; i < 9; i++)
            MS.elapseOneMinute();
        MS.skipCurrentMaintenance();
        for (int i = 0; i < 3; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room3, MS.getCurrentRoom());
        Assert.assertEquals(3, MS.getTimeElapsedCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteElapseNext() {
        for (int i = 0; i < 5; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room2, MS.getCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteWarpBack() {
        for (int i = 0; i < 32; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room1, MS.getCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteWarpBackTwice() {
        for (int i = 0; i < 64; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(room1, MS.getCurrentRoom());
    }

    @Test
    public void testElapseOneMinuteSetFalse() {
        for (int i = 0; i < 5; i++)
            MS.elapseOneMinute();
        Assert.assertFalse(room1.maintenanceOngoing());
    }

    @Test
    public void testElapseOneMinuteSetTrue() {
        for (int i = 0; i < 5; i++)
            MS.elapseOneMinute();
        Assert.assertTrue(room2.maintenanceOngoing());
        Assert.assertFalse(room1.maintenanceOngoing());
        Assert.assertFalse(room3.maintenanceOngoing());
    }

    @Test
    public void testSkipCurrentMaintenance() {
        MS.skipCurrentMaintenance();
        Assert.assertEquals(room2, MS.getCurrentRoom());
    }

    @Test
    public void testSkipCurrentMaintenanceSkipTwice() {
        MS.skipCurrentMaintenance();
        MS.skipCurrentMaintenance();
        Assert.assertEquals(room3, MS.getCurrentRoom());
    }

    @Test
    public void testSkipCurrentMaintenanceSkipAndWrapBack() {
        MS.skipCurrentMaintenance();
        MS.skipCurrentMaintenance();
        MS.skipCurrentMaintenance();
        Assert.assertEquals(room1, MS.getCurrentRoom());
    }

    @Test
    public void testToString() {
        String expected = "MaintenanceSchedule: " +
                "currentRoom=#101, currentElapsed=0";
        Assert.assertEquals(expected, MS.toString());
    }

    @Test
    public void testToStringElapse() {
        String expected = "MaintenanceSchedule: " +
                "currentRoom=#102, currentElapsed=45";
        for (int i = 0; i < 45; i++)
            MS.elapseOneMinute();
        Assert.assertEquals(expected, MS.toString());
    }

    @Test
    public void testEncode() {
        String expected = "101,102,103";
        Assert.assertEquals(expected, MS.encode());
    }
}


