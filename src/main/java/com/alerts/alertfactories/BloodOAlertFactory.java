package com.alerts.alertfactories;

import com.alerts.Alert;

public class BloodOAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new Alert(patientId, "Blood Oxygen: " + condition, timestamp);
    }
}