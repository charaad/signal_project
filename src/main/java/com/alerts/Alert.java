package com.alerts;

import com.alerts.alertdecoration.IAlertDeco;

public class Alert implements IAlertDeco{
    private int patientId;
    private String condition;
    private long timestamp;

    public Alert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDetails() {
      return getCondition();
    }
}
