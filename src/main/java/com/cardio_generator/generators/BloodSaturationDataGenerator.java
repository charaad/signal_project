package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/** 
 * Initialises blood saturation data generator.
 * Calculates blood saturation values based on previous values.
 * Implements PatientDataGenerator interface.
 * Uses unique patient IDs.
 * Ensures saturation values stay in a good range.
*/
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /** 
     * Constructor for BloodSaturationDataGenerator.
     * @param patientCount as the number of patients to be simulated. 
     * Initializes the lastSaturationValues array with baseline saturation values  between 95 and 100.
    */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /** 
     * Generates blood saturation data based on previous values.
     * @param patientId as the unique identifier for the patient.
     * @param outputStrategy as the strategy for outputting data.
     * @throws Exception for error handling and helps navigate to error.
    */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
