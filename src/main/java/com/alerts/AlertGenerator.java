package com.alerts;

import com.alerts.alertStrategies.AlertStrategy;
import com.alerts.alertStrategies.BPAlert;
import com.alerts.alertStrategies.BloodSaturationAlert;
import com.alerts.alertStrategies.CombinedAlert;
import com.alerts.alertStrategies.CriticalBPAlert;
import com.alerts.alertStrategies.ECGDataAlert;
import com.alerts.alertStrategies.RapidBloodSatAlert;
import com.alerts.alertStrategies.TriggeredAlerts;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertStrategy> strategies;

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
        this.strategies =  List.of(
        new BPAlert(),
        new CriticalBPAlert(),
        new BloodSaturationAlert(),
        new CombinedAlert(),
        new TriggeredAlerts(),
        new RapidBloodSatAlert(),
        new ECGDataAlert()
        );  //factory method to initialize alert strategies
    }

    public List<Alert> evaluateAll(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        for (AlertStrategy strategy : strategies) {
            Alert alert = strategy.evaluate(patient, startTime, endTime);
            if (alert != null) {
                alerts.add(alert);
            }
        }
        return alerts;
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
       long now = System.currentTimeMillis();
    long tenMinutesAgo = now - 10 * 60 * 1000; // Example: last 10 minutes

    if (strategies != null) {
        for (AlertStrategy strategy : strategies) {
            Alert alert = strategy.evaluate(patient, tenMinutesAgo, now);
            if (alert != null) {
                triggerAlert(alert);
            }
        }
    }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    protected void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
    }



}
