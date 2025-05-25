package data_management;

import com.alerts.Alert;
import com.alerts.alertfactories.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the AlertFactory interface and its implementations.
 * Verifies that each factory creates alerts with the correct properties and formatting.
 */
class AlertFactoryTest {

    /**
     * Tests the base AlertFactory interface contract.
     * Verifies that the factory method signature works as expected.
     */
    @Test
    void testAlertFactoryInterface() {
        AlertFactory factory = new AlertFactory() {
            @Override
            public Alert createAlert(int patientId, String condition, long timestamp) {
                return new Alert(patientId, condition, timestamp);
            }
        };
        
        Alert alert = factory.createAlert(1, "Test", 123456789L);
        assertNotNull(alert, "Factory should create non-null alerts");
    }

    /**
     * Tests the BloodOAlertFactory implementation.
     * Verifies that blood oxygen alerts are properly formatted.
     */
    @Test
    void testBloodOAlertFactory() {
        AlertFactory factory = new BloodOAlertFactory();
        Alert alert = factory.createAlert(1, "Low Oxygen", 123456789L);
        
        assertEquals(1, alert.getPatientId());
        assertEquals("Blood Oxygen: Low Oxygen", alert.getCondition());
        assertEquals(123456789L, alert.getTimestamp());
    }

    /**
     * Tests the BPAlertFactory implementation.
     * Verifies that blood pressure alerts are properly formatted.
     */
    @Test
    void testBPAlertFactory() {
        AlertFactory factory = new BPAlertFactory();
        Alert alert = factory.createAlert(2, "High Pressure", 123456790L);
        
        assertEquals(2, alert.getPatientId());
        assertEquals("Blood Pressure: High Pressure", alert.getCondition());
        assertEquals(123456790L, alert.getTimestamp());
    }

    /**
     * Tests the CombinedAlertFactory implementation.
     * Verifies that combined alerts are properly formatted.
     */
    @Test
    void testCombinedAlertFactory() {
        AlertFactory factory = new CombinedAlertFactory();
        Alert alert = factory.createAlert(3, "Multiple Issues", 123456791L);
        
        assertEquals(3, alert.getPatientId());
        assertEquals("Combined: Multiple Issues", alert.getCondition());
        assertEquals(123456791L, alert.getTimestamp());
    }

    /**
     * Tests the CriticalBPAlertFactory implementation.
     * Verifies that critical blood pressure alerts are properly formatted.
     */
    @Test
    void testCriticalBPAlertFactory() {
        AlertFactory factory = new CriticalBPAlertFactory();
        Alert alert = factory.createAlert(4, "Critical High", 123456792L);
        
        assertEquals(4, alert.getPatientId());
        assertEquals("Critical BP: Critical High", alert.getCondition());
        assertEquals(123456792L, alert.getTimestamp());
    }

    /**
     * Tests the ECGAlertFactory implementation.
     * Verifies that ECG alerts are properly formatted.
     */
    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert(5, "Irregular Rhythm", 123456793L);
        
        assertEquals(5, alert.getPatientId());
        assertEquals("ECG: Irregular Rhythm", alert.getCondition());
        assertEquals(123456793L, alert.getTimestamp());
    }

    /**
     * Tests the RapidBloodSatAlertFactory implementation.
     * Verifies that rapid blood saturation alerts are properly formatted.
     */
    @Test
    void testRapidBloodSatAlertFactory() {
        AlertFactory factory = new RapidBloodSatAlertFactory();
        Alert alert = factory.createAlert(6, "Rapid Drop", 123456794L);
        
        assertEquals(6, alert.getPatientId());
        assertEquals("Rapid Blood Sat: Rapid Drop", alert.getCondition());
        assertEquals(123456794L, alert.getTimestamp());
    }

    /**
     * Tests the TriggeredAlertsFactory implementation.
     * Verifies that manually triggered alerts are properly formatted.
     */
    @Test
    void testTriggeredAlertsFactory() {
        AlertFactory factory = new TriggeredAlertsFactory();
        Alert alert = factory.createAlert(7, "Manual Alert", 123456795L);
        
        assertEquals(7, alert.getPatientId());
        assertEquals("Manual Trigger: Manual Alert", alert.getCondition());
        assertEquals(123456795L, alert.getTimestamp());
    }

    /**
     * Tests that all factory implementations create distinct alert types.
     * Verifies the uniqueness of alert condition prefixes.
     */
    @Test
    void testAlertTypeDistinctness() {
        String condition = "Test Condition";
        
        Alert bloodOAlert = new BloodOAlertFactory().createAlert(1, condition, 1L);
        Alert bpAlert = new BPAlertFactory().createAlert(1, condition, 1L);
        Alert criticalBpAlert = new CriticalBPAlertFactory().createAlert(1, condition, 1L);
        
        assertNotEquals(bloodOAlert.getCondition(), bpAlert.getCondition(), 
                      "Blood oxygen and blood pressure alerts should be distinct");
        assertNotEquals(bpAlert.getCondition(), criticalBpAlert.getCondition(), 
                      "Regular and critical blood pressure alerts should be distinct");
    }

    /**
     * Tests alert creation with edge case values.
     * Verifies handling of minimum/maximum patient IDs and timestamps.
     */
    @Test
    void testAlertEdgeCases() {
        AlertFactory factory = new ECGAlertFactory();
        
        // Minimum values
        Alert minAlert = factory.createAlert(Integer.MIN_VALUE, "", Long.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, minAlert.getPatientId());
        assertEquals("ECG: ", minAlert.getCondition());
        assertEquals(Long.MIN_VALUE, minAlert.getTimestamp());
        
        // Maximum values
        Alert maxAlert = factory.createAlert(Integer.MAX_VALUE, "Max", Long.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, maxAlert.getPatientId());
        assertEquals("ECG: Max", maxAlert.getCondition());
        assertEquals(Long.MAX_VALUE, maxAlert.getTimestamp());
    }
}
