package com.alerts.alertfactories;

import com.alerts.Alert;

public class BPAlertFactory implements AlertFactory {
   @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new Alert(patientId, "Blood Pressure: " + condition, timestamp);
    }
}
