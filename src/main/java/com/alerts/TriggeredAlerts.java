package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class TriggeredAlerts implements AlertStrategy {

    public Alert evaluate(Patient patient, long startTime, long endTime) {
        // Look for a PatientRecord with recordType "ManualTrigger" in the time window
        List<PatientRecord> records = patient.getRecords(startTime, endTime);
        for (PatientRecord record : records) {
            if ("ManualTrigger".equalsIgnoreCase(record.getRecordType())) {
                return new Alert(patient.getPatientId(), "Manual Alert Triggered", record.getTimestamp());
            }
        }
        return null;
    }
}