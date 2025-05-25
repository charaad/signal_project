package com.alerts.alertStrategies;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BPAlert implements AlertStrategy {

    @Override
     public Alert evaluate(Patient patient, long startTime, long endTime) {
        
        List<PatientRecord> records = patient.getRecords(startTime, endTime);
        int patientID = patient.getPatientId();

        //make sure min 3 recs exist
        if (records.size() < 3) {
            return null; 
        }

        for (int i = 2; i < records.size(); i++) {
      //get lat 3 records of any type bp
            double reading1 = records.get(i).getMeasurementValue();
            double reading2 = records.get(i - 1).getMeasurementValue();
            double reading3 = records.get(i - 2).getMeasurementValue();
      //Assume chronological order
            double diff1 = reading1 - reading2;
            double diff2 = reading2 - reading3;
      //check if they're increasing or decreasing consecutively
            boolean isConsutive = false;
            if(diff1 > 0 && diff2 > 0) {
                isConsutive = true;
            } else if (diff1 < 0 && diff2 < 0) {
                isConsutive = true;
            }
       //if changes are by 10 or more
            if (isConsutive) {  
                if(Math.abs(diff1) > 10 && Math.abs(diff2) > 10) {  
                    //trigger alert
                    Alert alertTrendBP = new Alert(patientID, "Blood Pressure Alert", endTime);
                    return alertTrendBP;
                }
            }
        }
        return null;
    }

}
