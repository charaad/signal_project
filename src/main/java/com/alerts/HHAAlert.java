package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HHAAlert {

    public boolean isSystolicDanger(Patient patient, long startTime, long endTime) {
        int patientID = patient.getPatientId();
        List <PatientRecord> records = patient.getRecords(startTime, endTime);

        for (int i = 0; i < records.size(); i++) {
            double reading = records.get(i).getMeasurementValue();
            if(reading < 90) { 
                //trigger alert
                return true; 
            }
       }
    return false;
    } 

    public boolean isBloodOxygenDanger(Patient patient, long startTime, long endTime) {
        int patientID = patient.getPatientId();
        List <PatientRecord> records = patient.getRecords(startTime, endTime);

        for (int i = 0; i < records.size(); i++) {
            double reading = records.get(i).getMeasurementValue();
            if(reading < 0.92) { 
                //trigger alert
                return true; 
            }
        }
    return false;
    }

    public Alert HypotensiveHypoxemiaAlert(boolean isSystolicDanger, boolean isBloodOxygenDanger, int patientID, long endTime) {
        if (isSystolicDanger && isBloodOxygenDanger) {
            // Trigger alert
            Alert combinedAlert = new Alert(patientID, "Hypotensive Hypoxemia ALERT", endTime); 
            return combinedAlert;
        }
    return null;
    }

    //Alert BIGCombinedAlert = new Alert(patientID, "Hypotensive Hypoxemia ALERT", 
    //Systolic blood pressure is below 90 mmHg.
    //Blood oxygen saturation falls below 92%.

}
