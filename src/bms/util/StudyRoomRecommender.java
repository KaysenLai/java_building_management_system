package bms.util;

import bms.building.Building;
import bms.floor.Floor;
import bms.room.Room;
import bms.room.RoomState;
import bms.room.RoomType;
import bms.sensors.ComfortSensor;
import bms.sensors.Sensor;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class that provides a recommendation for a study room
 * in a building.
 */
public class StudyRoomRecommender {

    /**
     * Returns a room in the given building that is most suitable for study
     * purposes.
     * Any given room's suitability for study is based on several criteria,
     * including:
     *
     * the room's type - it must be a study room (see RoomType)
     * the room's status - it must be open, not being evacuated or in
     * maintenance (see Room.evaluateRoomState())
     * the room's comfort level based on its available sensors (see
     * ComfortSensor.getComfortLevel())
     *
     * which floor the room is on - rooms on lower floors are better
     * Since travelling up the floors of a building often requires walking up
     * stairs, the process for choosing a study room begins by looking for
     * rooms on the first floor, and only considers higher floors if doing
     * so would improve the comfort level of the room chosen. Similarly, once
     * on a floor, walking back down more than one floor to a previously
     * considered study room is also not optimal. If there are no rooms on
     * the first floor of a building that meet the basic criteria, then the
     * algorithm should recommend that the building be avoided entirely, even
     * if there are suitable rooms on higher floors.
     *
     * Based on these requirements, the algorithm for determining the most
     * suitable study room is as follows:
     *
     * If there are no rooms in the building, return null.
     *
     * Consider only rooms on the first floor.
     *
     * Eliminate any rooms that are not study rooms or are not open. If
     * there are no remaining candidate rooms, return the room with the
     * highest comfort level on the previous floor, or null if there is
     * no previous floor.
     *
     * Calculate the comfort level of all remaining rooms on this floor,
     * using the average of the comfort levels of each room's available
     * comfort sensors. If a room has no comfort sensors, its comfort level
     * should be treated as 0.
     *
     * Keep a reference to the room with the highest comfort level on this
     * floor based on the calculation in the previous step. If there is a
     * tie between two or more rooms, any of these may be chosen.
     * If the highest comfort level of any room on this floor is less than
     * or equal to the highest comfort level of any room on the previous
     * floor, return the room on the previous floor with the highest comfort
     * level.
     *
     * If this is the top floor of the building, return the room found in
     * step 5. Otherwise, repeat steps 2-7 for the next floor up.
     *
     * @param  building - building in which to search for a study room
     * @return the most suitable study room in the building; null if there are none
     */
    public static Room recommendStudyRoom(Building building) {

        int numberOfRooms = 0;
        for (Floor floor : building.getFloors()) {
            numberOfRooms += floor.getRooms().size();
        }
        if (numberOfRooms == 0 )
            return null;

        if (building.getFloorByNumber(1) == null)
            return null;

        int currentFloorNumber = 0;
        Floor currentFloor;
        Floor previousFloor;

        HashMap<Integer, Room> candidateRooms = new HashMap<>();

        while (currentFloorNumber < building.getFloors().size()) {

            previousFloor = building.getFloorByNumber(currentFloorNumber);

            currentFloorNumber++;
            currentFloor = building.getFloorByNumber(currentFloorNumber);
            Room highestRoom = null;

            for (Room room : currentFloor.getRooms()) {
                if (room.getType() == RoomType.STUDY &&
                        room.evaluateRoomState() == RoomState.OPEN &&
                        getRoomComfortLevel(room) >
                                getRoomComfortLevel(highestRoom)
                ) {
                    highestRoom = room;
                }
            }

            if (previousFloor == null) {
                if (highestRoom == null)
                    return null;
                candidateRooms.put(currentFloorNumber, highestRoom);
                continue;
            }

            Room previousHighestRoom = candidateRooms.get(
                    previousFloor.getFloorNumber());

            if (highestRoom == null) {
                return previousHighestRoom;
            } else {
                if (getRoomComfortLevel(previousHighestRoom) >=
                        getRoomComfortLevel(highestRoom))
                    return previousHighestRoom;
                candidateRooms.put(currentFloorNumber, highestRoom);
            }
        }
        return candidateRooms.get(building.getFloors().size());
    }

    /**
     * Returns true if and only if this temperature sensor is equal to the
     * other given sensor.
     * For two temperature sensors to be equal, they must have the same:
     *
     * ·sensor readings array (in the same order)
     *
     * @return  true if equal, false otherwise
     */
    private static int getRoomComfortLevel (Room room) {
        if (room == null)
            return 0;

        int totalComfort = 0;
        List<Sensor> sensors = room.getSensors();
        if (sensors.size() == 0) {
            return 0;
        }
        for (Sensor sensor : sensors) {
            totalComfort += ((ComfortSensor) sensor).getComfortLevel();
        }
        return totalComfort / sensors.size();
    }

}
