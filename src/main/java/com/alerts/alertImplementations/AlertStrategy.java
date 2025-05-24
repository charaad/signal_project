package com.alerts.alertImplementations;

import com.alerts.Alert;
import com.data_management.Patient;

public interface AlertStrategy {
    Alert evaluate(Patient patient, long startTime, long endTime);
}

//use interface to extend to more alerts
