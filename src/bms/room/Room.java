package bms.room;

import bms.exceptions.DuplicateSensorException;
import bms.hazardevaluation.HazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.sensors.Sensor;
import bms.sensors.TemperatureSensor;
import bms.sensors.TimedSensor;
import bms.util.Encodable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a room on a floor of a building.
 * <p>
 * Each room has a room number (unique for this floor, ie. no two rooms on the
 * same floor can have the same room number), a type to indicate its intended
 * purpose, and a total area occupied by the room in square metres.
 * <p>
 * Rooms also need to record whether a fire drill is currently taking place in
 * the room.
 * <p>
 * Rooms can have one or more sensors to monitor hazard levels
 * in the room.
 * @ass1
 */
public class Room implements Encodable {

    /**
     * Unique room number for this floor.
     */
    private int roomNumber;

    /**
     * The type of room. Different types of rooms can be used for different
     * activities.
     */
    private RoomType type;

    /**
     * List of sensors located in the room. Rooms may only have up to one of
     * each type of sensor. Alphabetically sorted by class name.
     */
    private List<Sensor> sensors;

    /**
     * Area of the room in square metres.
     */
    private double area;

    /**
     * Minimum area of all rooms, in square metres.
     * (Note that dimensions of the room are irrelevant).
     * Defaults to 5.
     */
    private static final int MIN_AREA = 5;

    /**
     * Records whether there is currently a fire drill.
     */
    private boolean fireDrill;

    private boolean maintenance;
    private HazardEvaluator hazardEvaluator;

    /**
     * Creates a new room with the given room number.
     *
     * @param roomNumber the unique room number of the room on this floor
     * @param type the type of room
     * @param area the area of the room in square metres
     * @ass1
     */
    public Room(int roomNumber, RoomType type, double area) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.area = area;

        this.sensors = new ArrayList<>();
        this.fireDrill = false;

        this.maintenance = false;
        this.hazardEvaluator = null;
    }

    /**
     * Returns room number of the room.
     *
     * @return the room number on the floor
     * @ass1
     */
    public int getRoomNumber() {
        return this.roomNumber;
    }

    /**
     * Returns area of the room.
     *
     * @return the room area in square metres
     * @ass1
     */
    public double getArea() {
        return this.area;
    }

    /**
     * Returns the minimum area for all rooms.
     * <p>
     * Rooms must be at least 5 square metres in area.
     *
     * @return the minimum room area in square metres
     * @ass1
     */
    public static int getMinArea() {
        return MIN_AREA;
    }

    /**
     * Returns the type of the room.
     *
     * @return the room type
     * @ass1
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Returns whether there is currently a fire drill in progress.
     *
     * @return current status of fire drill
     * @ass1
     */
    public boolean fireDrillOngoing() {
        return this.fireDrill;
    }

    /**
     * Returns whether there is currently maintenance in progress.
     *
     * @return  current status of maintenance
     */
    public boolean maintenanceOngoing() {
        return this.maintenance;
    }

    /**
     * Returns this room's hazard evaluator, or null if none exists.
     *
     * @return  room's hazard evaluator
     */
    public HazardEvaluator getHazardEvaluator() {
        return hazardEvaluator;
    }

    /**
     * Sets the room's hazard evaluator to a new hazard evaluator.
     *
     * @param hazardEvaluator - new hazard evaluator for the room to use
     */
    public void setHazardEvaluator(HazardEvaluator hazardEvaluator) {
        this.hazardEvaluator = hazardEvaluator;

    }

    /**
     * Returns the list of sensors in the room.
     * <p>
     * The list of sensors stored by the room should always be in alphabetical
     * order, by the sensor's class name.
     * <p>
     * Adding or removing sensors from this list should not affect the room's
     * internal list of sensors.
     *
     * @return list of all sensors in alphabetical order of class name
     * @ass1
     */
    public List<Sensor> getSensors() {
        return new ArrayList<>(this.sensors);
    }

    /**
     * Change the status of the fire drill to the given value.
     *
     * @param fireDrill whether there is a fire drill ongoing
     * @ass1
     */
    public void setFireDrill(boolean fireDrill) {
        this.fireDrill = fireDrill;
    }

    /**
     * Change the status of maintenance to the given value.
     *
     * @param maintenance - whether there is maintenance ongoing
     */
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    /**
     * Return the given type of sensor if there is one in the list of sensors;
     * return null otherwise.
     *
     * @param sensorType the type of sensor which matches the class name
     *                   returned by the getSimpleName() method,
     *                   e.g. "NoiseSensor" (no quotes)
     * @return the sensor in this room of the given type; null if none found
     * @ass1
     */
    public Sensor getSensor(String sensorType) {
        for (Sensor s : this.getSensors()) {
            if (s.getClass().getSimpleName().equals(sensorType)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Adds a sensor to the room if a sensor of the same type is not
     * already in the room.
     * <p>
     * The list of sensors should be sorted after adding the new sensor, in
     * alphabetical order by simple class name ({@link Class#getSimpleName()}).
     *
     * @param sensor the sensor to add to the room
     * @throws DuplicateSensorException if the sensor to add is of the
     * same type as a sensor already in this room
     * @ass1
     */
    public void addSensor(Sensor sensor)
            throws DuplicateSensorException {
        for (Sensor s : sensors) {
            if (s.getClass().equals(sensor.getClass())) {
                throw new DuplicateSensorException(
                        "Duplicate sensor of type: "
                                + s.getClass().getSimpleName());
            }
        }
        sensors.add(sensor);
        sensors.sort(Comparator.comparing(s -> s.getClass().getSimpleName()));
        hazardEvaluator = null;
    }

    /**
     * Evaluates the room status based upon current information.
     * A priority list below is used to determine what the returned status
     * must be. Higher in the list has higher priority.
     *
     * ·If there is a TemperatureSensor in the room and it reports a hazard
     *      level of 100, the room state is always EVACUATE since the room is on
     *      fire.
     *
     * ·If there is currently a fire drill in progress, the room state is always
     *      EVACUATE.
     *
     * ·If there is maintenance in progress and there is no fire drill or fire
     *      alarm, the room state is MAINTENANCE.
     *
     * ·Otherwise, the room state is OPEN.
     *
     * @return current room status
     */
    public RoomState evaluateRoomState() {
        TemperatureSensor TemperatureSensor =
                (TemperatureSensor) this.getSensor("TemperatureSensor");
        if (TemperatureSensor != null &&
                TemperatureSensor.getHazardLevel() == 100) {
            return RoomState.EVACUATE;
        }
        if (this.fireDrillOngoing()) {
            return RoomState.EVACUATE;
        }
        if (this.maintenanceOngoing()) {
            return RoomState.MAINTENANCE;
        }
        return RoomState.OPEN;
    }

    /**
     * Returns true if and only if this room is equal to the other given room.
     * For two rooms to be equal, they must have the same:
     *
     *  ·room number
     *  ·type
     *  ·area (within an error delta of ±0.001 inclusive)
     *  ·number of sensors
     *  ·sensors (in any order). Comparison should either directly or indirectly
     *      make use of each sensor's equals() method.
     *
     * @param obj  other object to compare equality
     * @return  true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Room castObj = (Room) obj;

        BigDecimal thisArea =
                new BigDecimal(area).setScale(3, RoundingMode.UP);
        BigDecimal objArea =
                new BigDecimal(castObj.area).setScale(3, RoundingMode.UP);

        for(Sensor sensor : this.getSensors()) {
            String sensorName = sensor.getClass().getSimpleName();
            Sensor objSensor = castObj.getSensor(sensorName);
            if (!sensor.equals(objSensor))
                return false;
        }

        return this.getRoomNumber() == castObj.getRoomNumber() &&
                this.getType() == castObj.getType() &&
                thisArea.equals(objArea) &&
                this.getSensors().size() == castObj.getSensors().size();
    }

    /**
     * Returns the hash code of this room.
     * Two rooms that are equal according to equals(Object) should have
     * the same hash code.
     *
     * @return  hash code of this room
     */
    @Override
    public int hashCode() {

        final int typePrime;
        int sensorsHashCode = 0;

        switch (getType()){
            case LABORATORY -> typePrime = 3;
            case OFFICE -> typePrime = 5;
            case STUDY -> typePrime = 7;
            default -> typePrime = 1;
        }

        for(Sensor sensor : getSensors()) {
            sensorsHashCode += sensor.hashCode();
        }

        int result = 1;
        result = result * typePrime + roomNumber;
        result = result * typePrime + typePrime;
        result = result * typePrime + getSensors().size();
        result = result * typePrime + (int) (area * 1000);
        result = result * typePrime + sensorsHashCode;

        return result;
    }

    /**
     * Returns the human-readable string representation of this room.
     * <p>
     * The format of the string to return is
     * "Room #'roomNumber': type='roomType', area='roomArea'm^2,
     * sensors='numSensors'"
     * without the single quotes, where 'roomNumber' is the room's unique
     * number, 'roomType' is the room's type, 'area' is the room's type,
     * 'numSensors' is the number of sensors in the room.
     * <p>
     * The room's area should be formatted to two (2) decimal places.
     * <p>
     * For example:
     * "Room #42: type=STUDY, area=22.50m^2, sensors=2"
     *
     * @return string representation of this room
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("Room #%d: type=%s, area=%.2fm^2, sensors=%d",
                this.roomNumber,
                this.type,
                this.area,
                this.sensors.size());
    }

    /**
     * Returns the machine-readable string representation of this room and all
     * of its sensors.
     * The format of the string to return is:
     *
     *  roomNumber:type:area:numSensors
     *  encodedSensor1
     *  encodedSensor2
     *  ...
     *  encodedSensorN
     *
     * where 'roomNumber' is the room's room number, 'type' is the room's
     * RoomType, 'area' is the room's area in square metres, 'numSensors' is
     * the number of sensors in the room, and 'encodedSensorX' is the encoded
     * string representation of the room's Xth sensor, sorted in alphabetical
     * order of the sensor's simple class name from 1 to N where N is the
     * number of sensors in the room. See TimedSensor.encode().
     * If this room has a hazard evaluator, then the first line of the returned
     * string should instead follow the format:
     *
     *  roomNumber:type:area:numSensors:encodedHazardEvaluator
     *
     * where 'encodedHazardEvaluator' is the toString() representation of the
     * room's hazard evaluator.
     * Additionally, if the room's hazard evaluator is a weighting based hazard
     * evaluator, then the lines representing the encoded sensors should
     * instead follow the format:
     *
     *  encodedSensor1@weighting1
     *  encodedSensor2@weighting2
     *  ...
     *  encodedSensorN@weightingN
     *
     * where 'weightingX' is the weighting (0-100) associated with the Xth
     * sensor in the alphabetically sorted list of this room's sensors, from
     * 1 to N.
     * You can assume that the order of the weightings returned by
     * WeightingBasedHazardEvaluator.getWeightings() is the same as the order
     * of sensors returned by getSensors().
     *
     * System.lineSeparator() should be used to separate lines. There should
     * be no newline at the end of the string.
     *
     * The room's area should be formatted to two decimal places.
     *
     * See the demo save file for an example (uqstlucia.txt).
     *
     * @return encoded string representation of this room
     */
    @Override
    public String encode() {
        List<Sensor> sensors = getSensors();

        String roomEncode = String.join(":",
                String.valueOf(getRoomNumber()),
                String.valueOf(getType()),
                String.format("%.2f", getArea()),
                String.valueOf(sensors.size())
        );

        if (getHazardEvaluator() != null) {
            roomEncode = String.join(":", roomEncode,
                    getHazardEvaluator().toString());
        } 

        for (int i = 0; i < sensors.size(); ++i) {
            String oneSensorEncode = ((TimedSensor) sensors.get(i)).encode();

            if (getHazardEvaluator() instanceof WeightingBasedHazardEvaluator) {
                List<Integer> weightings =
                        ((WeightingBasedHazardEvaluator)
                                getHazardEvaluator()).getWeightings();
                oneSensorEncode = String.join("@", oneSensorEncode,
                        String.valueOf(weightings.get(i)));
            }

            roomEncode = String.join(System.lineSeparator(),
                    roomEncode, oneSensorEncode);
        }
        return roomEncode;
    }
}
