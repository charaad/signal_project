package com.alerts.alertfactories;

import com.alerts.Alert;

public class CombinedAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new Alert(patientId, "Combined: " + condition, timestamp);
    }
}