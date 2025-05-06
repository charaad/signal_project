package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface, which generates data for patients.
 * It is being implemented by classes that generate patient data
 * and handle the data with an output strategy.
 */
public interface PatientDataGenerator {

    /**
     * Generates data for a patient.
     * 
     * @param patientId ID of the patient
     * @param outputStrategy strategy to output the data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
