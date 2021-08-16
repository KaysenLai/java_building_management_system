package bms.hazardevaluation;

/**
 * A component which takes all available hazard sensor readings and returns
 * a single hazard level.
 */
public interface HazardEvaluator {
    /**
     * Calculates a hazard level between 0 and 100.
     * Indicates the hazard level in a location based on information available
     * from multiple hazard sensors at the location.
     */
    int evaluateHazardLevel();
}
