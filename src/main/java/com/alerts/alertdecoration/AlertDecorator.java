package com.alerts.alertdecoration;

public abstract class AlertDecorator implements IAlertDeco {
    protected IAlertDeco decoratedAlert;

    public AlertDecorator(IAlertDeco alert) {
        this.decoratedAlert = alert;
    }

    @Override
    public int getPatientId() { return decoratedAlert.getPatientId(); }
    @Override
    public String getCondition() { return decoratedAlert.getCondition(); }
    @Override
    public long getTimestamp() { return decoratedAlert.getTimestamp(); }
    @Override
    public String getDetails() { return decoratedAlert.getDetails(); }
}