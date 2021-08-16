package bms.floor;

import bms.exceptions.FloorTooSmallException;
import bms.room.Room;
import bms.room.RoomState;
import bms.util.Encodable;
import bms.util.TimedItem;
import bms.util.TimedItemManager;

import java.util.Arrays;
import java.util.List;

/**
 *  Carries out maintenance on a list of rooms in a given floor.
 *  The maintenance time for each room depends on the type of the room and its
 *  area. Maintenance cannot progress whilst an evacuation is in progress.
 */
public class MaintenanceSchedule implements TimedItem, Encodable {

    /** The time elapsed */
    private int timeElapsed;

    /** The total time of rooms have been cleaned */
    private int cleanedRoomsTime;

    /** The clean time in the current room */
    private int currentCleanTime;

    /** The index of the current room */
    private int currentRoomIndex;

    /** A list contains rooms in order */
    private final List<Room> roomOrder;

    /**
     * Creates a new maintenance schedule for a floor's list of rooms.
     * In this constructor, the new maintenance schedule should be registered
     * as a timed item with the timed item manager.
     *
     * The first room in the given order should be set to "in maintenance",
     * see Room.setMaintenance(boolean).
     *
     * @param roomOrder - list of rooms on which to perform maintenance, in order
     */
    public MaintenanceSchedule(List<Room> roomOrder) {
        this.roomOrder = roomOrder;
        TimedItemManager.getInstance().registerTimedItem(this);
        if (roomOrder.size() > 0) {
            roomOrder.get(0).setMaintenance(true);
        }
        this.currentRoomIndex = 0;
        this.cleanedRoomsTime = 0;
    }

    /**
     * Returns the time taken to perform maintenance on the given room, in minutes.
     * The maintenance time for a given room depends on its size (larger rooms take longer to maintain) and its room type (rooms with more furniture and equipment take take longer to maintain).
     *
     * The formula for maintenance time is calculated as the room's base maintenance time multiplied by its room type multiplier, and finally rounded to the nearest integer. Floating point operations should be used during all steps of the calculation, until the final rounding to integer.
     *
     * Rooms with an area of Room.getMinArea() have a base maintenance time of 5.0 minutes.
     *
     * Rooms with areas greater than Room.getMinArea() have a base maintenance time of 5.0 minutes, plus 0.2 minutes for every square metre the room's area is over Room.getMinArea().
     *
     * A room's room type multiplier is given in the table below.
     *
     *      Room Type STUDY	   ->   Room Type Multiplier   1
     *      Room Type OFFICE	 ->     Room Type Multiplier   1.5
     *      Room Type LABORATORY   ->     Room Type Multiplier    2
     *
     * For example, if Room.getMinArea() is 2 square metres, an OFFICE room
     * with an area of 25.6 square metres would have a base maintenance time
     * of 9.72 minutes, a room type multiplier of 1.5 and a final maintenance
     * time of 15 minutes.
     *
     * @param room - room on which to perform maintenance
     */
    public int getMaintenanceTime(Room room) {
        double roomTypeMultiplier;
        double baseMaintenanceTime =
                (room.getArea() -  Room.getMinArea()) * 0.2 + 5.0;

        switch (room.getType()){
            case STUDY -> roomTypeMultiplier = 1.0;
            case OFFICE -> roomTypeMultiplier = 1.5;
            case LABORATORY -> roomTypeMultiplier = 2.0;
            default -> roomTypeMultiplier = 0;
        }
        return (int) Math.round(baseMaintenanceTime * roomTypeMultiplier);
    }

    /**
     * Returns the room which is currently in the process of being maintained.
     *
     * @return  room currently in maintenance
     */
    public Room getCurrentRoom(){
        return roomOrder.get(currentRoomIndex);
    }

    /**
     * Returns the number of minutes that have elapsed while maintaining the
     * current room (getCurrentRoom()).
     *
     * @return  time elapsed maintaining current room
     */
    public int getTimeElapsedCurrentRoom() {
        return this.currentCleanTime;
    }

    /**
     * Progresses the maintenance schedule by one minute.
     * If the room currently being maintained has a room state of EVACUATE,
     * then no action should occur.
     *
     * If enough time has elapsed such that the room currently being maintained
     * has completed its maintenance (according to getMaintenanceTime(Room)),
     * then:
     *
     * ·the current room should have its maintenance status set to false ( see
     *      Room.setMaintenance(boolean))
     *
     * ·the next room in the list passed to the constructor should be set as
     *      the new current room. If the end of the list has been reached, the new
     *      current room should "wrap around" to the first room in the list.
     *
     * ·the new current room should have its maintenance status set to true
     */
    public void elapseOneMinute() {
        if (getCurrentRoom().evaluateRoomState() == RoomState.EVACUATE) {
            return;
        }

        this.timeElapsed++;
        this.currentCleanTime++;

        int currentRoomTime = getMaintenanceTime(getCurrentRoom());

        if ((timeElapsed - cleanedRoomsTime) / currentRoomTime > 0) {
            int oldCurrentRoomIndex = currentRoomIndex;

            currentRoomIndex = (oldCurrentRoomIndex + 1) % roomOrder.size();
            cleanedRoomsTime += currentRoomTime;

            roomOrder.get(oldCurrentRoomIndex).setMaintenance(false);
            getCurrentRoom().setMaintenance(true);
            currentCleanTime = 0;
        }
    }

    /**
     * Stops the in-progress maintenance of the current room and progresses
     * to the next room.
     *
     * The same steps should be undertaken as described in the dot point list
     * in elapseOneMinute().
     */
    public void skipCurrentMaintenance() {
        cleanedRoomsTime += currentCleanTime;
        roomOrder.get(currentRoomIndex).setMaintenance(false);

        currentRoomIndex = (currentRoomIndex + 1) % roomOrder.size();
        getCurrentRoom().setMaintenance(true);
        currentCleanTime = 0;
    }

    /**
     * Returns the human-readable string representation of this maintenance schedule.
     * The format of the string to return is
     *
     * MaintenanceSchedule: currentRoom=#currentRoomNumber, currentElapsed=elapsed
     * where 'currentRoomNumber' is the room number of the room currently being maintained, and 'elapsed' is the number of minutes that have elapsed while maintaining the current room.
     * For example:
     *
     * MaintenanceSchedule: currentRoom=#108, currentElapsed=3
     *
     * @return string representation of this maintenance schedule
     */
    @Override
    public String toString() {
        return String.format("MaintenanceSchedule: " +
                        "currentRoom=#%d, currentElapsed=%d",
                this.getCurrentRoom().getRoomNumber(),
                this.timeElapsed);
    }

    /**
     * Returns the machine-readable string representation of this maintenance schedule.
     * The format of the string to return is:
     *
     *  roomNumber1,roomNumber2,...,roomNumberN
     *
     * where 'roomNumberX' is the room number of the Xth room in this maintenance schedule's room order, from 1 to N where N is the number of rooms in the maintenance order.
     * There should be no newline at the end of the string.
     *
     * See the demo save file for an example (uqstlucia.txt).
     */
    @Override
    public String encode() {
        int[] roomNumbers = new int[roomOrder.size()];
        for (int i = 0; i < roomOrder.size(); ++i) {
            roomNumbers[i] = roomOrder.get(i).getRoomNumber();
        }

        return String.join(",", Arrays.stream(roomNumbers)
                .mapToObj(String::valueOf)
                .toArray(String[]::new));
    }
}
