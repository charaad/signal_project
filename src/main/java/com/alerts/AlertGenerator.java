package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
    }


    public Alert bloddPressureAlert(Patient patient, long startTime, long endTime) {
        
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
                }
            }
        }
        return null;
    }

    public Alert criticalThresholdAlert(Patient patient) {
        //systolic blood pressure > 180 mmHg || < 90 mmHg
        //diastolic blood pressure > 120 mmHg || < 60 mmHg
        return null;
    }

}
