package com.cardio_generator.generators;

// Removed blank line between import statements
//Added an import for error handling
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;
import java.util.logging.Logger;

/**
 * Generates an alert, indicating if an alert is being triggered or resolved.
 * The process of determining the alert for the specified patient is randomized
 * and is output by a given strategy.
 * Each patient has and initial alert state that influences the chances of it changing.
 */
public class AlertGenerator implements PatientDataGenerator {

    //Added a logger for error handling
    private static final Logger logger = Logger.getLogger(AlertGenerator.class.getName());

    /** Random number generator. */
    public static final Random randomGenerator = new Random();

    // Changed variable name to camelCase
    /** Stores the initial alert state of each patient. */
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructor that generates initial alert states for a specific number of patients.
     * 
     * @param patientCount number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates or resolves an alert and handles the result according to the output strategy.
     * If the given patient has an alert activated, there is a 90% chance of it being resolved.
     * If not, there is a chance of an alert getting activated
     * according to a poisson distribution model.
     * 
     * @param patientId ID of patient
     * @param outputStrategy strategy used to output the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false; 
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to camelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            //Changed exception handling
            logger.severe("An error occurred while generating alert data for patient " + patientId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
