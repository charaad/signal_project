package com.alerts.alertImplementations;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodSaturationAlert implements AlertStrategy {

    @Override
    public Alert evaluate(Patient patient, long startTime, long endTime) {
      
        int patientID = patient.getPatientId();
        List <PatientRecord> records = patient.getRecords(startTime, endTime);

        for(int i = 0; i < records.size(); i++) {
            double reading = records.get(i).getMeasurementValue();
            if(reading < 0.92) {  //assume records come in 0-1
                //trigger alert
                Alert lowBloodSatAlert = new Alert(patientID, "Low Blood Saturation Alert", endTime);
                return lowBloodSatAlert; } 
        }
        return null; // if no issue
    }


}
