package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodSaturationAlerts {

    public Alert lowSaturation(Patient patient, long startTime, long endTime) {
      
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

    public Alert rapidDrop(Patient patient, long startTime, long endTime) {
    //O2 saturation drops >= 5% in 10 mins

        int patientID = patient.getPatientId();
        List <PatientRecord> records = patient.getRecords(startTime, endTime);
        double mins =  (endTime - startTime) / 60000.0;
        
        if (records.size() < 2) {
            return null; 
        }

        for (int i = 0; i < records.size(); i++) {
            double reading1 = records.get(i).getMeasurementValue();
            double reading2 = records.get(i - 1).getMeasurementValue();
            double diff = Math.abs(reading1 - reading2); //assume chronological

            if (mins <= 10 && diff >= 0.05) { 
                //trigger alert
                Alert rapidDropAlert = new Alert(patientID, "Rapid Drop in Blood Saturation Alert", endTime);
                return rapidDropAlert;
            }      
        }
      return null;
    }

}
