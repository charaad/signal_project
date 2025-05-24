package data_management;

import com.alerts.*;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertTests {

    private Patient patient;
    private long now;

    @BeforeEach
    void setUp() {
        patient = new Patient(1);
        now = System.currentTimeMillis();
    }

    // BPAlert tests
    @Test
    void testBPAlertsTriggered() {
        patient.addRecord(100, "SystolicBloodPressure", now - 30000);
        patient.addRecord(115, "SystolicBloodPressure", now - 20000);
        patient.addRecord(130, "SystolicBloodPressure", now - 10000);
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNotNull(alert, "BPAlerts should trigger on consecutive increases");
    }

    @Test
    void testBPAlertsNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 30000);
        patient.addRecord(122, "SystolicBloodPressure", now - 20000);
        patient.addRecord(123, "SystolicBloodPressure", now - 10000);
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNull(alert, "BPAlerts should not trigger on small changes");
    }

    @Test
    void testBPAlertsEmptyRecords() {
        BPAlert bpAlerts = new BPAlert();
        Alert alert = bpAlerts.evaluate(patient, now - 40000, now);
        assertNull(alert, "BPAlerts should not trigger if there are no records");
    }

    // CriticalBPAlert tests
    @Test
    void testCriticalBPAlertTriggeredSystolic() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CriticalBPAlert should trigger on high systolic");
        assertTrue(alert.getCondition().contains("Systolic"));
    }

    @Test
    void testCriticalBPAlertTriggeredDiastolic() {
        patient.addRecord(125, "DiastolicBloodPressure", now - 10000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CriticalBPAlert should trigger on high diastolic");
        assertTrue(alert.getCondition().contains("Diastolic"));
    }

    @Test
    void testCriticalBPAlertNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 10000);
        patient.addRecord(80, "DiastolicBloodPressure", now - 9000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CriticalBPAlert should not trigger on normal values");
    }

    @Test
    void testCriticalBPAlertBoundary() {
        patient.addRecord(180, "SystolicBloodPressure", now - 10000);
        patient.addRecord(120, "DiastolicBloodPressure", now - 9000);
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CriticalBPAlert should not trigger at threshold values");
    }

    // BloodSaturationAlert tests
    @Test
    void testBloodSaturationAlertsTriggered() {
        patient.addRecord(0.91, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNotNull(alert, "BloodSaturationAlerts should trigger on low saturation");
    }

    @Test
    void testBloodSaturationAlertsNotTriggered() {
        patient.addRecord(0.95, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "BloodSaturationAlerts should not trigger on normal saturation");
    }

    @Test
    void testBloodSaturationAlertsBoundary() {
        patient.addRecord(0.92, "BloodOxygen", now - 5000);
        BloodSaturationAlert alertStrategy = new BloodSaturationAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "BloodSaturationAlerts should not trigger at threshold");
    }

    // CombinedAlert tests
    @Test
    void testCombinedAlertTriggered() {
        patient.addRecord(85, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNotNull(alert, "CombinedAlert should trigger when both conditions are met");
    }

    @Test
    void testCombinedAlertNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CombinedAlert should not trigger if only one condition is met");
    }

    @Test
    void testCombinedAlertEmptyRecords() {
        CombinedAlert alertStrategy = new CombinedAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "CombinedAlert should not trigger with no records");
    }

    // TriggeredAlerts tests
    @Test
    void testTriggeredAlertsTriggered() {
        patient.addRecord(1, "ManualTrigger", now - 1000);
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNotNull(alert, "TriggeredAlerts should trigger on manual trigger record");
    }

    @Test
    void testTriggeredAlertsNotTriggered() {
        patient.addRecord(120, "SystolicBloodPressure", now - 1000);
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNull(alert, "TriggeredAlerts should not trigger if no manual trigger record");
    }

    @Test
    void testTriggeredAlertsEmptyRecords() {
        TriggeredAlerts alertStrategy = new TriggeredAlerts();
        Alert alert = alertStrategy.evaluate(patient, now - 5000, now);
        assertNull(alert, "TriggeredAlerts should not trigger with no records");
    }

    // RapidBloodSatAlert tests
    @Test
    void testRapidBloodSatAlertTriggered() {
        patient.addRecord(0.97, "BloodOxygen", now - 20000);
        patient.addRecord(0.93, "BloodOxygen", now - 10000);
        patient.addRecord(0.89, "BloodOxygen", now);
        RapidBloodSatAlert alertStrategy = new RapidBloodSatAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 30000, now);
        assertNotNull(alert, "RapidBloodSatAlert should trigger on rapid drop");
    }

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

    @Test
    void testECGDataAlertNotTriggered() {
        for (int i = 0; i < 250; i++) {
            patient.addRecord(80, "ECG", now - 10000 + i);
        }
        ECGDataAlert alertStrategy = new ECGDataAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 10000, now);
        assertNull(alert, "ECGDataAlert should not trigger on normal ECG values");
    }

    // General edge case: records outside time window
    @Test
    void testRecordsOutsideWindow() {
        patient.addRecord(185, "SystolicBloodPressure", now - 1000000); // way outside window
        CriticalBPAlert alertStrategy = new CriticalBPAlert();
        Alert alert = alertStrategy.evaluate(patient, now - 20000, now);
        assertNull(alert, "No alert should trigger for records outside the time window");
    }
}
