package com.alerts.alertfactories;

import com.alerts.Alert;

public interface AlertFactory {
    Alert createAlert(int patientId, String condition, long timestamp);
}
