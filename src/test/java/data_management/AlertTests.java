package data_management;

import com.alerts.*;
import com.alerts.alertStrategies.*;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for individual alert strategy classes under {@code com.alerts.alertStrategies}.
 * <p>
 * Each test case ensures that alerts are correctly triggered or suppressed based on
 * the patient's recorded vital signs and the logic implemented in each strategy.
 */
public class AlertTests {

    private Patient patient;
    private long now;

    /**
     * Set up a fresh {@link Patient} instance and timestamp before each test.
     */
    @BeforeEach
    void setUp() {
        patient = new Patient(1);
        now = System.currentTimeMillis();
    }

    // BPAlert tests

    /**
     * Tests that {@link BPAlert} triggers when there is a consistent upward trend in systolic BP.
     */
    @Test
    void testBPAlertsTriggered() {
        patient.addRecord(100, "SystolicBloodPressure", now - 30000);
        patient.addRecord(115, "SystolicBloodPressure", now - 20000);
        patient.addRecord(130, "SystolicBloodPressure", now - 10000);
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNotNull(alert, "BPAlerts should trigger on consecutive increases");
    }

     /**
     * Tests that {@link BPAlert} does not trigger on minor variations in systolic BP.
     */
    @Test
    void testBPAlertsNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 30000);
        patient.addRecord(122, "SystolicBloodPressure", now - 20000);
        patient.addRecord(123, "SystolicBloodPressure", now - 10000);
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNull(alert, "BPAlerts should not trigger on small changes");
    }

    /**
     * Tests that {@link BPAlert} does not trigger when there are no relevant records.
     */
    @Test
    void testBPAlertsEmptyRecords() {
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNull(alert, "BPAlerts should not trigger if there are no records");
    }

    // CriticalBPAlert tests

    /**
     * Tests that {@link CriticalBPAlert} triggers on dangerously high systolic BP.
     */
    @Test
    void testCriticalBPAlertTriggeredSystolic() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CriticalBPAlert should trigger on high systolic");
        assertTrue(alert.getCondition().contains("Systolic"));
    }

    /**
     * Tests that {@link CriticalBPAlert} triggers on dangerously high diastolic BP.
     */
    @Test
    void testCriticalBPAlertTriggeredDiastolic() {
        patient.addRecord(125, "DiastolicBloodPressure", now - 10000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CriticalBPAlert should trigger on high diastolic");
        assertTrue(alert.getCondition().contains("Diastolic"));
    }

     /**
     * Tests that {@link CriticalBPAlert} does not trigger on normal blood pressure.
     */
    @Test
    void testCriticalBPAlertNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 10000);
        patient.addRecord(80, "DiastolicBloodPressure", now - 9000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CriticalBPAlert should not trigger on normal values");
    }

     /**
     * Tests that {@link CriticalBPAlert} does not trigger at threshold boundary values.
     */
    @Test
    void testCriticalBPAlertBoundary() {
        patient.addRecord(180, "SystolicBloodPressure", now - 10000);
        patient.addRecord(120, "DiastolicBloodPressure", now - 9000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CriticalBPAlert should not trigger at threshold values");
    }

    // BloodSaturationAlert tests

     /**
     * Tests that {@link BloodSaturationAlert} triggers when blood oxygen falls below threshold.
     */
    @Test
    void testBloodSaturationAlertsTriggered() {
        patient.addRecord(0.91, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNotNull(alert, "BloodSaturationAlerts should trigger on low saturation");
    }

     /**
     * Tests that {@link BloodSaturationAlert} does not trigger on normal saturation.
     */
    @Test
    void testBloodSaturationAlertsNotTriggered() {
        patient.addRecord(0.95, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "BloodSaturationAlerts should not trigger on normal saturation");
    }

     /**
     * Tests that {@link BloodSaturationAlert} does not trigger exactly at the threshold.
     */
    @Test
    void testBloodSaturationAlertsBoundary() {
        patient.addRecord(0.92, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "BloodSaturationAlerts should not trigger at threshold");
    }

    // CombinedAlert tests

     /**
     * Tests that {@link CombinedAlert} triggers when both low BP and low oxygen are present.
     */
    @Test
    void testCombinedAlertTriggered() {
        patient.addRecord(85, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CombinedAlert should trigger when both conditions are met");
    }

     /**
     * Tests that {@link CombinedAlert} does not trigger when only one condition is met.
     */
    @Test
    void testCombinedAlertNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CombinedAlert should not trigger if only one condition is met");
    }

     /**
     * Tests that {@link CombinedAlert} does not trigger with no records.
     */
    @Test
    void testCombinedAlertEmptyRecords() {
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CombinedAlert should not trigger with no records");
    }

    // TriggeredAlerts tests

     /**
     * Tests that {@link TriggeredAlerts} triggers when a manual trigger record is added.
     */
    @Test
    void testTriggeredAlertsTriggered() {
        patient.addRecord(1, "ManualTrigger", now - 1000);
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNotNull(alert, "TriggeredAlerts should trigger on manual trigger record");
    }

     /**
     * Tests that {@link TriggeredAlerts} does not trigger on unrelated records.
     */
    @Test
    void testTriggeredAlertsNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 1000);
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNull(alert, "TriggeredAlerts should not trigger if no manual trigger record");
    }

     /**
     * Tests that {@link TriggeredAlerts} does not trigger with no records.
     */
    @Test
    void testTriggeredAlertsEmptyRecords() {
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNull(alert, "TriggeredAlerts should not trigger with no records");
    }

    // RapidBloodSatAlert tests

     /**
     * Tests that {@link RapidBloodSatAlert} triggers on a rapid drop in blood oxygen.
     */
    @Test
    void testRapidBloodSatAlertTriggered() {
        patient.addRecord(0.97, "BloodOxygen", now - 20000);
        patient.addRecord(0.93, "BloodOxygen", now - 10000);
        patient.addRecord(0.89, "BloodOxygen", now);
        RapidBloodSatAlert alertStrategy = new RapidBloodSatAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 30000, now);
        assertNotNull(alert, "RapidBloodSatAlert should trigger on rapid drop");
    }

     /**
     * Tests that {@link RapidBloodSatAlert} does not trigger if the drop is too slow.
     */
    @Test
    void testRapidBloodSatAlertNotTriggered() {
        patient.addRecord(0.97, "BloodOxygen", now - 20000);
        patient.addRecord(0.96, "BloodOxygen", now - 10000);
        patient.addRecord(0.95, "BloodOxygen", now);
        RapidBloodSatAlert alertStrategy = new RapidBloodSatAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 30000, now);
        assertNull(alert, "RapidBloodSatAlert should not trigger if drop is not rapid enough");
    }

    // ECGDataAlert tests

     /**
     * Tests that {@link ECGDataAlert} triggers when abnormal ECG values are recorded.
     */
    @Test
    void testECGDataAlertTriggered() {
        // Add enough records to fill the window if needed by your implementation
        for (int i = 0; i < 249; i++) {
            patient.addRecord(80, "ECG", now - 10000 + i);
        }
        patient.addRecord(250, "ECG", now);
        ECGDataAlert alertStrategy = new ECGDataAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNotNull(alert, "ECGDataAlert should trigger on abnormal ECG value");
    }

     /**
     * Tests that {@link ECGDataAlert} does not trigger when all ECG values are normal.
     */
    @Test
    void testECGDataAlertNotTriggered() {
        for (int i = 0; i < 250; i++) {
            patient.addRecord(80, "ECG", now - 10000 + i);
        }
        ECGDataAlert alertStrategy = new ECGDataAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "ECGDataAlert should not trigger on normal ECG values");
    }

    // General edge case

     /**
     * Tests that old data outside the specified evaluation window does not trigger an alert.
     */
    @Test
    void testRecordsOutsideWindow() {
        patient.addRecord(185, "SystolicBloodPressure", now - 1000000); // way outside window
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "No alert should trigger for records outside the time window");
    }
}
