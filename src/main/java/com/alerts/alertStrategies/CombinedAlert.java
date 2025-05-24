package com.alerts.alertImplementations;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class CombinedAlert implements AlertStrategy {
    
@Override
   public Alert evaluate(Patient patient, long startTime, long endTime) {
        boolean systolicDanger = false;
        boolean bloodOxygenDanger = false;
        int patientID = patient.getPatientId();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();

            if ("SystolicBloodPressure".equalsIgnoreCase(type) && value < 90) {
                systolicDanger = true;
            }
            if ("BloodOxygen".equalsIgnoreCase(type) && value < 0.92) {
                bloodOxygenDanger = true;
            }
        }

        if (systolicDanger && bloodOxygenDanger) {
            return new Alert(patientID, "Hypotensive Hypoxemia alert - IMMEDIATE ATTENTION", endTime);
        }
        return null;
    }

}
