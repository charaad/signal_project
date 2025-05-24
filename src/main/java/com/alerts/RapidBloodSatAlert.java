package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class RapidBloodSatAlert implements AlertStrategy {

        public Alert evaluate(Patient patient, long startTime, long endTime) {
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
