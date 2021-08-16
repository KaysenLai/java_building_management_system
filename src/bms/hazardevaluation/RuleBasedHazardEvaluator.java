package bms.hazardevaluation;

import bms.sensors.HazardSensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates the hazard level of a location using a rule based system.
 */
public class RuleBasedHazardEvaluator implements HazardEvaluator {

    /**
     * A list contains hazardSensors.
     */
    private List<HazardSensor> hazardSensors;

    /**
     * Creates a new rule-based hazard evaluator with the given list of sensors.
     *
     * @param  sensors - sensors to be used in the hazard level calculation
     */
    public RuleBasedHazardEvaluator(List<HazardSensor> sensors) {
        this.hazardSensors = new ArrayList<>(sensors);
    }

    /**
     * Returns a calculated hazard level based on applying a set of rules to
     * the list of sensors passed to the constructor.
     * The rules to be applied are as follows. Note that square brackets []
     * have been used to indicate mathematical grouping.
     *
     * ·If there are no sensors, return 0.
     * ·If there is only one sensor, return that sensor's current hazard level
     * as per HazardSensor.getHazardLevel().
     * ·If there is more than one sensor:
     *      ·If any sensor that is not an OccupancySensor has a hazard
     *              level of 100, return 100.
     *      ·Calculate the average hazard level of all sensors that are not an
     *              OccupancySensor. Floating point division should be used
     *              when finding the average.
     *      ·If there is an OccupancySensor in the list, multiply the average
     *              calculated in the previous step by [the occupancy sensor's
     *              current hazard level divided by 100, using floating point
     *              division].
     *      ·Return the final average rounded to the nearest integer between 0
     *              and 100.
     * You can assume that there is no more than one OccupancySensor in
     * the list passed to the constructor.
     *
     * @return calculated hazard level according to a set of rules
     */
    public int evaluateHazardLevel() {
        if (hazardSensors.isEmpty()) {
            return 0;
        }

        if (hazardSensors.size() == 1) {
            return hazardSensors.get(0).getHazardLevel();
        }

        boolean hasOccupancySensor = false;
        int sumHazardLevel = 0;
        double averageHazardLevel;
        int OccupancySensorHazardLevel = 0;
        double result;

        for (HazardSensor sensor : hazardSensors) {
            if (sensor.getClass().getSimpleName().equals("OccupancySensor")) {
                hasOccupancySensor = true;
                OccupancySensorHazardLevel = sensor.getHazardLevel();
                continue;
            } else if (sensor.getHazardLevel() == 100) {
                return 100;
            }
            sumHazardLevel += sensor.getHazardLevel();
        }

        if (hasOccupancySensor) {
            averageHazardLevel = (double) sumHazardLevel /
                                 (double) (hazardSensors.size() - 1);
            result = ((double) OccupancySensorHazardLevel / 100.0)
                      * averageHazardLevel;
        } else {
            averageHazardLevel = (double) sumHazardLevel /
                                 (double) hazardSensors.size();
            result = averageHazardLevel;
        }

        return (int) Math.round(result);
    }

    /**
     * Returns the string representation of this hazard evaluator.
     * The format of the string to return is simply "RuleBased" without double quotes.
     *
     * See the demo save file for an example (uqstlucia.txt).
     */
    public String toString() {
        return "RuleBased";
    }

}
