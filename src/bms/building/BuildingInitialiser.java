package bms.building;

import bms.exceptions.*;
import bms.floor.Floor;
import bms.hazardevaluation.RuleBasedHazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.room.Room;
import bms.room.RoomType;
import bms.sensors.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class which manages the initialisation and saving of buildings by reading
 * and writing data to a file.
 * Note: the Javadoc includes the default constructor, BuildingInitialiser(),
 * however you do not need to write this method in your assignment as it will
 * be automatically generated.
 */
public class BuildingInitialiser {

    public BuildingInitialiser(){}

    /**
     * Loads a list of buildings from a save file with the given filename.
     * Save files have the following structure. Square brackets indicate that
     * the data inside them is optional. See the demo save file for an example
     * (uqstlucia.txt).
     *
     *  buildingName
     *  numFloors
     *  floorNumber:floorWidth:floorLength:numRooms[:rooms,in,maintenance,
     *  schedule]
     *  roomNumber:ROOM_TYPE:roomArea:numSensors[:hazardEvalType]
     *  sensorType:list,of,sensor,readings[:sensorAttributes...][@weighting]
     *  ...       (more sensors)
     *  ...     (more rooms)
     *  ...   (more floors)
     *  ... (more buildings)
     *
     * A save file is invalid if any of the following conditions are true:
     *
     * The number of floors specified for a building is not equal to the actual
     * number of floors read from the file for that building.
     * The number of rooms specified for a floor is not equal to the actual
     * number of rooms read from the file for that floor.
     * The number of sensors specified for a room is not equal to the number
     * of sensors read from the file for that room.
     * A floor's maintenance schedule contains a room number that does not
     * correspond to a room with the same number on that floor.
     * A floor's maintenance schedule is invalid according to
     * Floor.createMaintenanceSchedule(List).
     * A building has two floors with the same floor number (a duplicate floor).
     * A floor's length or width is less than the minimum length or width,
     * respectively, for a floor.
     * A floor has no floor below to support the floor.
     * A floor is too large to fit on top of the floor below.
     * A floor has two rooms with the same room number (a duplicate room).
     * A room cannot be added to its floor because there is insufficient
     * unoccupied space on the floor.
     * A room's type is not one of the types listed in RoomType. Room types
     * are case-sensitive.
     * A room's area is less than the minimum area for a room.
     * A room's hazard evaluator type is invalid.
     * A room's weighting-based hazard evaluator weightings are invalid
     * according to WeightingBasedHazardEvaluator(Map).
     * A room has two sensors of the same type (a duplicate sensor).
     * A sensor's type does not match one of the concrete sensor types
     * (e.g. NoiseSensor, OccupancySensor, ...).
     * A sensor's update frequency does not meet the restrictions
     * outlined in TimedSensor(int[], int).
     * A carbon dioxide sensor's variation limit is greater than its ideal
     * CO2 value.
     * Any numeric value that should be non-negative is less than zero. This
     * includes:
     * the number of floors in a building
     * the number of rooms on a floor
     * the number of sensors in room
     * sensor readings
     * occupancy sensor capacity
     * carbon dioxide sensor ideal CO2 level
     * carbon dioxide sensor variation limit
     * Any numeric value that should be positive is less than or equal to zero.
     * This includes:
     * floor numbers
     * The colon-delimited format is violated, i.e. there are more/fewer colons
     * than expected.
     * Any numeric value fails to be parsed.
     * An empty line occurs where a non-empty line is expected.
     * @param filename  path of the file from which to load a list of buildings
     * @return  a list containing all the buildings loaded from the file
     *
     * @throws IOException  if an IOException is encountered when calling any
     *          IO methods
     * @throws FileFormatException  if the file format of the given file is
     *          invalid according to the rules above
     */
    public static List<Building> loadBuildings(String filename)
            throws IOException, FileFormatException {

        List<Building> buildings = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new FileReader(filename));

        String buildingName = reader.readLine();

        while (buildingName != null ) {
            buildings.add(readBuilding(reader, buildingName));
            buildingName = reader.readLine();
        }

        return buildings;
    }

    /**
     * Read a building in the file and get a Building Object.
     *
     * @param reader  the BufferedReader to read file
     * @param buildingName  the read name of the building
     * @return a Building Object read from the file.
     *
     * @throws IOException  if an IOException is encountered when calling any
     *          IO methods
     * @throws FileFormatException  if the file format of the given file is
     *          invalid according to the rules above
     */
    private static Building readBuilding(BufferedReader reader,
                                         String buildingName)
            throws IOException, FileFormatException {

        Building building = new Building(buildingName);

        int numFloors;

        try {
            numFloors = Integer.parseInt(reader.readLine());
        } catch (IllegalArgumentException e) {
            throw new FileFormatException();
        }

        if (numFloors < 0)
            throw new FileFormatException();

        for (int i = 0; i < numFloors; ++i) {
            Floor floor;

            try {
                floor = readFloor(reader);
                building.addFloor(floor);
            } catch (NoFloorBelowException
                    | DuplicateFloorException
                    | FloorTooSmallException
                    | FileFormatException e) {
                throw new FileFormatException();
            }

        }
        return building;
    }

    /**
     * Read a floor in the file and get a Floor Object.
     *
     * @param reader  the BufferedReader to read file
     * @return a Floor Object read from the file.
     *
     * @throws IOException  if an IOException is encountered when calling any
     *          IO methods
     * @throws FileFormatException  if the file format of the given file is
     *          invalid according to the rules above
     */
    private static Floor readFloor(BufferedReader reader)
            throws IOException, FileFormatException {

        String floorLine = reader.readLine();
        String[] floorParts = floorLine.split(":");
        if (floorParts.length > 5)
            throw new FileFormatException();

        int floorNumber, numRooms;
        double floorWidth, floorLength;

        try {
            floorNumber = Integer.parseInt(floorParts[0]);
            floorWidth = Double.parseDouble(floorParts[1]);
            floorLength = Double.parseDouble(floorParts[2]);
            numRooms = Integer.parseInt(floorParts[3]);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new FileFormatException();
        }

        if (numRooms < 0 ||
                floorNumber <= 0 ||
                floorWidth < Floor.getMinWidth() ||
                floorLength < Floor.getMinLength())
            throw new FileFormatException();

        Floor floor = new Floor(floorNumber, floorWidth, floorLength);

        for (int i = 0; i < numRooms; ++i) {
            Room room;
            try {
                room = readRoom(reader);
                floor.addRoom(room);
            } catch (InsufficientSpaceException
                    | DuplicateRoomException e) {
                throw new FileFormatException();
            }
        }

        if (floor.getRooms().size() != numRooms) {
            throw new FileFormatException();
        }

        if (floorParts.length == 5) {
            List<Room> roomOrder= new ArrayList<>();
            int[] maintenanceRooms = stringArrayToIntArray(
                    floorParts[4].split(","));

            for (int maintenanceRoomNumber : maintenanceRooms) {
                Room maintenanceRoom =
                        floor.getRoomByNumber(maintenanceRoomNumber);
                if (maintenanceRoom == null) {
                    throw new FileFormatException();
                }
                roomOrder.add(maintenanceRoom);
            }
            if (roomOrder.size() == 0) {
                throw new FileFormatException();
            }
            try {
                floor.createMaintenanceSchedule(roomOrder);
            } catch (IllegalArgumentException e) {
                throw new FileFormatException();
            }

        }
        return floor;
    }


    /**
     * Read a room in the file and get a Room Object.
     *
     * @param reader  the BufferedReader to read file
     * @return a Room Object read from the file.
     *
     * @throws IOException  if an IOException is encountered when calling any
     *          IO methods
     * @throws FileFormatException  if the file format of the given file is
     *          invalid according to the rules above
     */
    private static Room readRoom(BufferedReader reader)
            throws IOException, FileFormatException {

        String roomLine = reader.readLine();
        String[] roomParts = roomLine.split(":");
        if (roomParts.length > 5)
            throw new FileFormatException();

        int roomNumber, numSensors;
        RoomType roomType;
        double roomArea;
        boolean hasRuleEvaluator = false;
        boolean hasWeightEvaluator = false;

        try {
            roomNumber = Integer.parseInt(roomParts[0]);
            roomType = RoomType.valueOf(roomParts[1]);
            roomArea = Double.parseDouble(roomParts[2]);
            numSensors = Integer.parseInt(roomParts[3]);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new FileFormatException();
        }

        if (roomNumber < 0 || numSensors < 0 || roomArea < Room.getMinArea())
            throw new FileFormatException();

        if (roomParts.length == 5 &&
                roomParts[4].equals("RuleBased"))
            hasRuleEvaluator = true;

        if (roomParts.length == 5 &&
                roomParts[4].equals("WeightingBased"))
            hasWeightEvaluator = true;

        if (roomParts.length == 5 && !hasRuleEvaluator && !hasWeightEvaluator)
            throw new FileFormatException();

        Room room = new Room(roomNumber, roomType, roomArea);
        Map<HazardSensor, Integer> weightSensors = new HashMap<>();

        for (int i = 0; i < numSensors; ++i) {
            String sensorLine = reader.readLine();
            String[] sensorParts = sensorLine.split(":");

            if (hasWeightEvaluator)
                sensorParts = handleWeightLine(sensorParts);

            HazardSensor sensor = readSensor(sensorParts, hasWeightEvaluator);

            try {
                room.addSensor(sensor);
            } catch (DuplicateSensorException e) {
                throw new FileFormatException();
            }

            if (hasWeightEvaluator) {

                int weighting;
                try {
                    weighting = Integer.parseInt(
                            sensorParts[sensorParts.length - 1]);
                } catch (IllegalArgumentException
                        | IndexOutOfBoundsException e) {
                    throw new FileFormatException();
                }
                weightSensors.put(sensor, weighting);
            }
        }

        if (hasRuleEvaluator) {
            List<HazardSensor> hazardSensors = new ArrayList<>();
            for (Sensor sensor : room.getSensors()) {
                if (sensor instanceof HazardSensor) {
                    hazardSensors.add((HazardSensor) sensor);
                }
            }

            RuleBasedHazardEvaluator evaluator =
                    new RuleBasedHazardEvaluator(hazardSensors);
            room.setHazardEvaluator(evaluator);
        }

        if (hasWeightEvaluator) {
            try {
                room.setHazardEvaluator(
                        new WeightingBasedHazardEvaluator(weightSensors));
            } catch (IllegalArgumentException e) {
                throw new FileFormatException();
            }
        }

        return room;
    }

    /**
     * Read a sensor in the file and get a Sensor Object.
     *
     * @param sensorParts  the line parts contains params of a sensor
     * @param hasWeight  whether the room has a weight based evaluator
     * @return a Sensor Object read from the file.
     *
     * @throws IOException  if an IOException is encountered when calling any
     *          IO methods
     * @throws FileFormatException  if the file format of the given file is
     *          invalid according to the rules above
     */
    private static HazardSensor readSensor(String[] sensorParts,
                                           boolean hasWeight)
            throws FileFormatException {

        HazardSensor sensor;
        String sensorType = sensorParts[0];

        int weightExtraLength = 0;
        if(hasWeight)
            weightExtraLength = 1;

        int[] sensorReadings = stringArrayToIntArray(
                sensorParts[1].split(","));

        switch (sensorType) {
            case "CarbonDioxideSensor" -> {
                if (sensorParts.length > 5 + weightExtraLength)
                    throw new FileFormatException();

                int updateFrequency, idealValue, variationLimit;
                try {
                    updateFrequency = Integer.parseInt(sensorParts[2]);
                    idealValue = Integer.parseInt(sensorParts[3]);
                    variationLimit = Integer.parseInt(sensorParts[4]);
                    sensor = new CarbonDioxideSensor(
                            sensorReadings, updateFrequency,
                            idealValue, variationLimit);
                } catch (IllegalArgumentException
                        | IndexOutOfBoundsException e) {
                    throw new FileFormatException();
                }
            }
            case "NoiseSensor" -> {
                if (sensorParts.length > 3 + weightExtraLength)
                    throw new FileFormatException();

                int updateFrequency;
                try {
                    updateFrequency = Integer.parseInt(sensorParts[2]);
                    sensor = new NoiseSensor(sensorReadings, updateFrequency);
                } catch (IllegalArgumentException
                        | IndexOutOfBoundsException e) {
                    throw new FileFormatException();
                }
            }
            case "OccupancySensor" -> {
                if (sensorParts.length > 4 + weightExtraLength)
                    throw new FileFormatException();

                int updateFrequency, capacity;
                try {
                    updateFrequency = Integer.parseInt(sensorParts[2]);
                    capacity = Integer.parseInt(sensorParts[3]);
                    sensor = new OccupancySensor(
                            sensorReadings, updateFrequency, capacity);
                } catch (IllegalArgumentException
                        | IndexOutOfBoundsException e) {
                    throw new FileFormatException();
                }
            }
            case "TemperatureSensor" ->{
                if (sensorParts.length > 2 + weightExtraLength)
                    throw new FileFormatException();

                try {
                    sensor = new TemperatureSensor(sensorReadings);
                } catch (IllegalArgumentException e) {
                    throw new FileFormatException();
                }
            }
            default -> throw new FileFormatException();
        }
        return sensor;
    }

    /**
     * Handle the situation when having a weight based evaluator.
     * Split the "@" in the sensor parts
     *
     * @param sensorParts  the line parts contains params of a sensor
     * @return a new sensor parts after splitting "@".
     *
     */
    private static String[] handleWeightLine(String[] sensorParts) {
        String[] newSensorParts = null;
        for (int i = 0; i < sensorParts.length; i++) {
            if (sensorParts[i].contains("@")) {
                String[] weightingParts =
                        sensorParts[i].split("@");

                newSensorParts= Arrays.copyOf(sensorParts,
                        sensorParts.length + 1);
                newSensorParts[i] = weightingParts[0];
                newSensorParts[i+1] = weightingParts[1];
            }
        }
        return newSensorParts;
    }

    /**
     * Read a sensor in the file and get a Sensor Object.
     *
     * @param stringArray  the string array contains strings of integer
     * @return a int array contains integers
     *
     * @throws FileFormatException  the integer string cannot be parsed to
     *          integer.
     */
    private static int[] stringArrayToIntArray(String[] stringArray)
            throws FileFormatException {
        int[] intArray = new int[stringArray.length];
        for(int i = 0; i < stringArray.length; ++i){
            try {
                intArray[i] = Integer.parseInt(stringArray[i]);
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                throw new FileFormatException();
            }
        }
        return intArray;
    }
}
