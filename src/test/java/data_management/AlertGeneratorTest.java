package data_management;

import com.alerts.*;
import com.data_management.Patient;
import com.data_management.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AlertGenerator} class.
 * <p>
 * These tests verify that appropriate alerts are triggered based on the
 * patient's vital records, such as blood pressure, blood oxygen, ECG, and manual triggers.
 */
public class AlertGeneratorTest {

    private Patient patient;
    private long now;
    private TestAlertGenerator alertGenerator;

     /**
     * A test subclass of {@link AlertGenerator} that captures triggered alerts for verification.
     */
    static class TestAlertGenerator extends AlertGenerator {
        List<Alert> triggeredAlerts = new ArrayList<>();

        /**
         * Constructs the test alert generator with the given data storage.
         * @param ds the data storage object
         */
        public TestAlertGenerator(DataStorage ds) {
            super(ds);
        }

         /**
         * Overrides the method to collect triggered alerts instead of sending them.
         * @param alert the alert to capture
         */
        @Override
        protected void triggerAlert(Alert alert) {
            triggeredAlerts.add(alert);
        }

        /**
         * Returns the list of captured alerts.
         * @return list of triggered alerts
         */
        public List<Alert> getTriggeredAlerts() {
            return triggeredAlerts;
        }
    }

     /**
     * Initializes a new {@link Patient}, {@link DataStorage}, and {@link AlertGenerator}
     * before each test.
     */
    @BeforeEach
    void setUp() {
        patient = new Patient(1);
        now = System.currentTimeMillis();
        DataStorage ds = new DataStorage();
        alertGenerator = new TestAlertGenerator(ds);
    }

    /**
     * Verifies that no alerts are triggered when all vital signs are within normal ranges.
     */
    @Test
    void testNoAlertsTriggeredForNormalData() {
     patient.addRecord(120, "SystolicBloodPressure", now - 10000);
     patient.addRecord(120, "SystolicBloodPressure", now - 9000);
     patient.addRecord(120, "SystolicBloodPressure", now - 8000);
     patient.addRecord(80, "DiastolicBloodPressure", now - 7000);
     patient.addRecord(80, "DiastolicBloodPressure", now - 6000);
     patient.addRecord(80, "DiastolicBloodPressure", now - 5000);
     patient.addRecord(0.95, "BloodOxygen", now - 4000);
     for (int i = 0; i < 250; i++) {
    patient.addRecord(80, "ECG", now - 3000 - i);
     }
    alertGenerator.evaluateData(patient);
    System.out.println(alertGenerator.getTriggeredAlerts());
    assertTrue(alertGenerator.getTriggeredAlerts().isEmpty(), "No alerts should be triggered for normal data");
    }

     /**
     * Verifies that a critical systolic blood pressure alert is triggered when the value is too high.
     */
    @Test
    void testCriticalBPAlertIsTriggered() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Systolic")), "Critical BP alert should be triggered");
    }

     /**
     * Verifies that a combined alert (hypotensive + low oxygen) is triggered when both conditions occur.
     */
    @Test
    void testCombinedAlertIsTriggered() {
        patient.addRecord(85, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().toLowerCase().contains("hypotensive")), "Combined alert should be triggered");
    }

     /**
     * Verifies that a manual trigger alert is activated when a manual event record is added.
     */
    @Test
    void testTriggeredAlertsIsTriggered() {
        patient.addRecord(1, "ManualTrigger", now - 1000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().toLowerCase().contains("manual")), "Manual trigger alert should be triggered");
    }

    /**
     * Verifies that multiple alerts are triggered if multiple abnormal conditions are present.
     */
    @Test
    void testMultipleAlertsTriggered() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        patient.addRecord(1, "ManualTrigger", now - 8000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().size() >= 2, "Multiple alerts should be triggered");
    }

    /**
     * Verifies that no alerts are triggered if no records are present for a patient.
     */
    @Test
    void testNoAlertsWhenNoRecords() {
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().isEmpty(), "No alerts should be triggered when there are no records");
    }
}
