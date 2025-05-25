package com.alerts.alertdecoration;

/**
 * Decorator that adds priority information to an alert.
 * This decorator enhances the alert details with priority tagging
 * for alerts that require urgent attention.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    /**
     * Creates a new PriorityAlertDecorator.
     * @param alert The alert to be decorated
     * @param priorityLevel The priority level to assign (e.g., "High", "Critical")
     */
    public PriorityAlertDecorator(IAlertDeco alert, String priorityLevel) {
        super(alert);
        this.priorityLevel = priorityLevel;
    }

    /**
     * Gets the decorated alert details with priority information.
     * @return The alert details prefixed with priority information
     */
    @Override
    public String getDetails() {
        return "[PRIORITY: " + priorityLevel + "] " + decoratedAlert.getDetails();
    }

    /**
     * Gets the priority level of this alert.
     * @return The priority level
     */
    public String getPriorityLevel() {
        return priorityLevel;
    }
}
