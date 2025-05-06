package com.cardio_generator.generators;

// Removed blank line between import statements
//Added an import for error handling
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;
import java.util.logging.Logger;

public class AlertGenerator implements PatientDataGenerator {

    //Added a logger for error handling
    private static final Logger logger = Logger.getLogger(AlertGenerator.class.getName());
    public static final Random randomGenerator = new Random();
    // Changed variable name to camelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    //Added method comment
    /**
     * Generates or resolves an alert and handles the result according to the output strategy
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
