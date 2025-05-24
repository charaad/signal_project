package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class RapidBloodSatAlert implements AlertStrategy {

    public Alert evaluate(Patient patient, long startTime, long endTime) {
        // O2 saturation drops >= 5% in 10 mins
        int patientID = patient.getPatientId();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        if (records.size() < 2) {
            return null;
        }

        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (PatientRecord record : records) {
            if ("BloodOxygen".equalsIgnoreCase(record.getRecordType())) {     
                double value = record.getMeasurementValue();
                if (value > max) max = value;
                if (value < min) min = value;
            }
        }

        double drop = max - min;
        double mins = (endTime - startTime) / 60000.0;

        if (mins <= 10 && drop >= 0.05) {
            return new Alert(patientID, "Rapid Drop in Blood Saturation Alert", endTime);
        }
        return null;
    }
}
