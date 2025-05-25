package com.alerts.alertdecoration;

/**
 * Decorator that adds repetition information to an alert.
 * This decorator is used for alerts that need to be checked and re-checked
 * over a set interval, adding this information to the alert details.
 */
public class RepeatedAlertDeco extends AlertDecorator {
    private int repeatInterval; // in minutes
    private int repeatCount;

    /**
     * Creates a new RepeatedAlertDeco.
     * @param alert The alert to be decorated
     * @param repeatInterval The interval in minutes between repeats
     * @param repeatCount The number of times the alert should be repeated
     */
    public RepeatedAlertDeco(IAlertDeco alert, int repeatInterval, int repeatCount) {
        super(alert);
        this.repeatInterval = repeatInterval;
        this.repeatCount = repeatCount;
    }

    /**
     * Gets the decorated alert details with repetition information.
     * @return The alert details with repetition information appended
     */
    @Override
    public String getDetails() {
        return decoratedAlert.getDetails() + " (To be repeated every " + repeatInterval + 
               " minutes, " + repeatCount + " times)";
    }

    /**
     * Gets the repeat interval in minutes.
     * @return The repeat interval
     */
    public int getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * Gets the number of times the alert should be repeated.
     * @return The repeat count
     */
    public int getRepeatCount() {
        return repeatCount;
    }
}
