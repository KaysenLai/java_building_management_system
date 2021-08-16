package bms.hazardevaluation;

import bms.sensors.HazardSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the hazard level of a location using weightings for the sensor values.
 * The sum of the weightings of all sensors must equal 100.
 */
public class WeightingBasedHazardEvaluator implements HazardEvaluator {

    /**
     * A HashMap contains senor and its weighting.
     */
    private HashMap<HazardSensor,Integer> hazardSensors = new HashMap<>();

    /**
     * Creates a new weighting-based hazard evaluator with the given sensors
     * and weightings.
     * Each weighting must be between 0 and 100 inclusive, and the total sum
     * of all weightings must equal 100.
     *
     * @param  sensors - mapping of sensors to their respective weighting
     * @throws  IllegalArgumentException - if any weighting is below 0 or above
     *          100; or if the sum of all weightings is not equal to 100
     */
    public WeightingBasedHazardEvaluator(Map<HazardSensor,Integer> sensors) {
        hazardSensors.putAll(sensors);

        int sumOfWeighting = 0;
        for(int weighting : hazardSensors.values()){
            if (weighting < 0 || weighting >100) {
                throw new IllegalArgumentException("Weightings must not " +
                        "be below 0 or above 100;");
            }
            sumOfWeighting += weighting;
        }
        if (sumOfWeighting != 100) {
            throw new IllegalArgumentException("The sum of all weightings " +
                    "is not equal to 100");
        }
    }

    /**
     * public int evaluateHazardLevel()
     * Returns the weighted average of the current hazard levels of all
     * sensors in the map passed to the constructor.
     * The weightings given in the constructor should be used. The final
     * evaluated hazard level should be rounded to the nearest integer between
     * 0 and 100.
     *
     * For example, given the following sensors and weightings, this method
     * should return a value of 28.
     *
     * WeightingBasedHazardEvaluator example
     * Sensor Type  --  Current Hazard Level  --  Weighting
     * OccupancySensor  --  40  --  20
     * NoiseSensor  --  65  --  30
     * TemperatureSensor  --  0  --  50
     *
     * @return weighted average of current sensor hazard levels
     */
    public int evaluateHazardLevel() {
        int weightedSumOfHazardLevel = 0;
        for(Map.Entry<HazardSensor,Integer> entry : hazardSensors.entrySet()){

            HazardSensor hazardSensor = entry.getKey();
            int weighting = entry.getValue();

            weightedSumOfHazardLevel +=
                    hazardSensor.getHazardLevel() * weighting;
        }
        return  (int) Math.round(weightedSumOfHazardLevel / 100.0);
    }

    /**
     * Returns a list containing the weightings associated with all of the
     * sensors monitored by this hazard evaluator.
     *
     * @return weightings
     */
    public List<Integer> getWeightings() {
        return new ArrayList<>(hazardSensors.values());
    }

    /**
     * Returns the string representation of this hazard evaluator.
     * The format of the string to return is simply "WeightingBased" without
     * the double quotes.
     *
     * See the demo save file for an example (uqstlucia.txt).
     *
     * @return weightings
     */
    public String toString() {
        return "WeightingBased";
    }
}
