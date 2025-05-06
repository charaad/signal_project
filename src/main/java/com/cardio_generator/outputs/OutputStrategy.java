package com.cardio_generator.outputs;

/**
 * Interface, that outputs data of a patient.
 * It is implemented by classes which specify the way a patient's data is being output.
 */
public interface OutputStrategy {

    /**
     * Outputs data for a specific patient
     * 
     * @param patientId ID of the patient
     * @param timestamp time
     * @param label name of type of data
     * @param data the data
     */
    void output(int patientId, long timestamp, String label, String data);
}
