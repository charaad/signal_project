package com.alerts;

import java.util.List;
import java.util.LinkedList;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class ECGDataAlert implements AlertStrategy {

    private static final int WINDOW_SIZE = 250; // Adjust based on sampling rate
    private static final double THRESHOLD_MULTIPLIER = 2.5;

    public Alert evaluate(Patient patient, long startTime, long endTime) {
        
        int patientID = patient.getPatientId();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        if (records.size() < WINDOW_SIZE) {
            return null;
        }

        LinkedList<Double> window = new LinkedList<>();
        double sum = 0.0;

        for (int i = 0; i < records.size(); i++) {
            double value = records.get(i).getMeasurementValue();

            // Update sliding window
            if (window.size() == WINDOW_SIZE) {
                sum -= window.removeFirst();
            }
            window.addLast(value);
            sum += value;

            if (window.size() < WINDOW_SIZE) continue;

            double average = sum / WINDOW_SIZE;

            if (value > average * THRESHOLD_MULTIPLIER) {
                Alert ecgPeakAlert = new Alert(patientID, "Abnormal ECG Peak Detected", endTime);
                return ecgPeakAlert;
            }
        }

        return null;
    }
}
