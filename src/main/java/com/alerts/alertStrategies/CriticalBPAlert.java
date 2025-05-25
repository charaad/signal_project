package com.alerts.alertStrategies;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class CriticalBPAlert implements AlertStrategy {

@Override
    public Alert evaluate(Patient patient, long startTime, long endTime) {
        List<PatientRecord> records = patient.getRecords(startTime, endTime);
        int patientID = patient.getPatientId();

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double reading = record.getMeasurementValue();

            if ("SystolicBloodPressure".equalsIgnoreCase(type)) {
                if (reading > 180 || reading < 90) {
                    return new Alert(patientID, "Critical Systolic Blood Pressure Alert", record.getTimestamp());
                }
            } else if ("DiastolicBloodPressure".equalsIgnoreCase(type)) {
                if (reading > 120 || reading < 60) {
                    return new Alert(patientID, "Critical Diastolic Blood Pressure Alert", record.getTimestamp());
                }
            }
        }
        return null;
    }
}
