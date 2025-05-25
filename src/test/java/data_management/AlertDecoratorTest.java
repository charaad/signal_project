package data_management;

import com.alerts.alertdecoration.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the alert decorator hierarchy.
 * Verifies the functionality of the decorator pattern implementation
 * for alert notifications including priority and repetition features.
 */
class AlertDecoratorTest {

    /**
     * Simple implementation of IAlertDeco for testing purposes.
     */
    private static class TestAlert implements IAlertDeco {
        private final int patientId;
        private final String condition;
        private final long timestamp;
        private final String details;

        public TestAlert(int patientId, String condition, long timestamp, String details) {
            this.patientId = patientId;
            this.condition = condition;
            this.timestamp = timestamp;
            this.details = details;
        }

        @Override public int getPatientId() { return patientId; }
        @Override public String getCondition() { return condition; }
        @Override public long getTimestamp() { return timestamp; }
        @Override public String getDetails() { return details; }
    }

    /**
     * Tests that the base AlertDecorator correctly delegates all calls
     * to the wrapped alert without modification.
     */
    @Test
    void testAlertDecoratorDelegation() {
        IAlertDeco baseAlert = new TestAlert(123, "High BP", 1000L, "Needs attention");
        AlertDecorator decorator = new AlertDecorator(baseAlert) {};

        assertEquals(123, decorator.getPatientId(), "Patient ID should match");
        assertEquals("High BP", decorator.getCondition(), "Condition should match");
        assertEquals(1000L, decorator.getTimestamp(), "Timestamp should match");
        assertEquals("Needs attention", decorator.getDetails(), "Details should match");
    }

    /**
     * Tests that PriorityAlertDecorator correctly adds priority information
     * to alert details while preserving other properties.
     */
    @Test
    void testPriorityAlertDecorator() {
        IAlertDeco baseAlert = new TestAlert(456, "Low HR", 2000L, "Monitor closely");
        PriorityAlertDecorator priorityAlert = new PriorityAlertDecorator(baseAlert, "Critical");

        assertEquals(456, priorityAlert.getPatientId(), "Patient ID should be preserved");
        assertEquals("Low HR", priorityAlert.getCondition(), "Condition should be preserved");
        assertEquals(2000L, priorityAlert.getTimestamp(), "Timestamp should be preserved");
        assertEquals("[PRIORITY: Critical] Monitor closely", priorityAlert.getDetails(), 
            "Details should include priority");
        assertEquals("Critical", priorityAlert.getPriorityLevel(), "Priority level should match");
    }

    /**
     * Tests that RepeatedAlertDeco correctly adds repetition information
     * to alert details while preserving other properties.
     */
    @Test
    void testRepeatedAlertDeco() {
        IAlertDeco baseAlert = new TestAlert(789, "Critical", 3000L, "Immediate action needed");
        RepeatedAlertDeco repeatedAlert = new RepeatedAlertDeco(baseAlert, 5, 3);

        assertEquals(789, repeatedAlert.getPatientId(), "Patient ID should be preserved");
        assertEquals("Critical", repeatedAlert.getCondition(), "Condition should be preserved");
        assertEquals(3000L, repeatedAlert.getTimestamp(), "Timestamp should be preserved");
        assertEquals("Immediate action needed (To be repeated every 5 minutes, 3 times)", 
            repeatedAlert.getDetails(), "Details should include repetition info");
        assertEquals(5, repeatedAlert.getRepeatInterval(), "Repeat interval should match");
        assertEquals(3, repeatedAlert.getRepeatCount(), "Repeat count should match");
    }

    /**
     * Tests that decorators can be chained together with each decorator
     * adding its specific modification to the alert details.
     */
    @Test
    void testDecoratorChaining() {
        IAlertDeco baseAlert = new TestAlert(111, "Unstable", 4000L, "Check vitals");
        IAlertDeco priorityThenRepeat = new RepeatedAlertDeco(
            new PriorityAlertDecorator(baseAlert, "High"), 10, 2);

        assertEquals(111, priorityThenRepeat.getPatientId(), "Patient ID should be preserved");
        assertEquals("Unstable", priorityThenRepeat.getCondition(), "Condition should be preserved");
        assertEquals(4000L, priorityThenRepeat.getTimestamp(), "Timestamp should be preserved");
        assertEquals("[PRIORITY: High] Check vitals (To be repeated every 10 minutes, 2 times)", 
            priorityThenRepeat.getDetails(), "Details should include both decorations");
    }

    /**
     * Tests edge case where an alert is decorated multiple times
     * with the same decorator type.
     */
    @Test
    void testMultipleSameDecorators() {
        IAlertDeco baseAlert = new TestAlert(333, "Alert", 6000L, "Base message");
        IAlertDeco multiPriority = new PriorityAlertDecorator(
            new PriorityAlertDecorator(baseAlert, "High"), "Urgent");

        assertEquals("[PRIORITY: Urgent] [PRIORITY: High] Base message", 
            multiPriority.getDetails(), "Should show all priority decorations");
    }
}