package com.alerts.alertfactories;

import com.alerts.Alert;

public class TriggeredAlertsFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new Alert(patientId, "Manual Trigger: " + condition, timestamp);
    }
}