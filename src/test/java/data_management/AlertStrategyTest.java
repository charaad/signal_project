package data_management;

import com.alerts.alertStrategies.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for AlertStrategy and its implementations
 */
class AlertStrategyTest {
    private CombinedAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new CombinedAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that no alert is generated when no conditions are met
     */
    @Test
    void testNoAlertWhenConditionsNotMet() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 95, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 0.93, "BloodOxygen", currentTime - 500));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 2000, currentTime);
        assertNull(result);
    }

    /**
     * Tests that an alert is generated when both systolic BP is low and blood oxygen is low
     */
    @Test
    void testAlertWhenBothConditionsMet() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 0.91, "BloodOxygen", currentTime - 500));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 2000, currentTime);
        assertNotNull(result);
        assertEquals("Hypotensive Hypoxemia alert - IMMEDIATE ATTENTION", result.getCondition());
    }

    /**
     * Tests that no alert is generated when only systolic BP is low
     */
    @Test
    void testNoAlertWhenOnlySystolicLow() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 0.93, "BloodOxygen", currentTime - 500));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 2000, currentTime);
        assertNull(result);
    }

    /**
     * Tests that no alert is generated when only blood oxygen is low
     */
    @Test
    void testNoAlertWhenOnlyBloodOxygenLow() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 95, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 0.91, "BloodOxygen", currentTime - 500));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 2000, currentTime);
        assertNull(result);
    }
}

/**
 * Test class for CriticalBPAlert strategy
 */
class CriticalBPAlertTest {
    private CriticalBPAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new CriticalBPAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that a critical systolic alert is generated for high systolic BP
     */
    @Test
    void testCriticalSystolicHighAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 185, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Critical Systolic Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that a critical systolic alert is generated for low systolic BP
     */
    @Test
    void testCriticalSystolicLowAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Critical Systolic Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that a critical diastolic alert is generated for high diastolic BP
     */
    @Test
    void testCriticalDiastolicHighAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 125, "DiastolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Critical Diastolic Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that a critical diastolic alert is generated for low diastolic BP
     */
    @Test
    void testCriticalDiastolicLowAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 55, "DiastolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Critical Diastolic Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that no alert is generated when BP values are normal
     */
    @Test
    void testNoAlertForNormalBP() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 120, "SystolicBloodPressure", currentTime));
        records.add(new PatientRecord(1, 80, "DiastolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNull(result);
    }
}

/**
 * Test class for ECGDataAlert strategy
 */
class ECGDataAlertTest {
    private ECGDataAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new ECGDataAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that no alert is generated when there's insufficient data
     */
    @Test
    void testNoAlertForInsufficientData() {
        List<PatientRecord> records = new ArrayList<>();
        // Add fewer records than the window size
        for (int i = 0; i < 100; i++) {
            records.add(new PatientRecord(1, 1.0, "ECG", currentTime + i));
        }
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + 1000);
        assertNull(result);
    }

    /**
     * Tests that an alert is generated when an abnormal peak is detected
     */
    @Test
    void testAlertForAbnormalPeak() {
        List<PatientRecord> records = new ArrayList<>();
        // Create normal baseline
        for (int i = 0; i < 300; i++) {
            records.add(new PatientRecord(1, 1.0, "ECG", currentTime + i));
        }
        // Add an abnormal peak
        records.add(new PatientRecord(1, 3.5, "ECG", currentTime + 301));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + 302);
        assertNotNull(result);
        assertEquals("Abnormal ECG Peak Detected", result.getCondition());
    }

    /**
     * Tests that no alert is generated for normal ECG data
     */
    @Test
    void testNoAlertForNormalECG() {
        List<PatientRecord> records = new ArrayList<>();
        // Create normal data
        for (int i = 0; i < 500; i++) {
            records.add(new PatientRecord(1, 1.0 + (i % 10) * 0.1, "ECG", currentTime + i));
        }
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + 500);
        assertNull(result);
    }
}

/**
 * Test class for RapidBloodSatAlert strategy
 */
class RapidBloodSatAlertTest {
    private RapidBloodSatAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new RapidBloodSatAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that an alert is generated for a rapid drop in blood saturation
     */
    @Test
    void testAlertForRapidDrop() {
        List<PatientRecord> records = new ArrayList<>();
        long tenMinutes = 10 * 60 * 1000;
        records.add(new PatientRecord(1, 0.97, "BloodOxygen", currentTime));
        records.add(new PatientRecord(1, 0.91, "BloodOxygen", currentTime + tenMinutes - 1));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + tenMinutes);
        assertNotNull(result);
        assertEquals("Rapid Drop in Blood Saturation Alert", result.getCondition());
    }

    /**
     * Tests that no alert is generated for a slow drop in blood saturation
     */
    @Test
    void testNoAlertForSlowDrop() {
        List<PatientRecord> records = new ArrayList<>();
        long elevenMinutes = 11 * 60 * 1000;
        records.add(new PatientRecord(1, 0.97, "BloodOxygen", currentTime));
        records.add(new PatientRecord(1, 0.91, "BloodOxygen", currentTime + elevenMinutes));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + elevenMinutes);
        assertNull(result);
    }

    /**
     * Tests that no alert is generated for a small drop in blood saturation
     */
    @Test
    void testNoAlertForSmallDrop() {
        List<PatientRecord> records = new ArrayList<>();
        long fiveMinutes = 5 * 60 * 1000;
        records.add(new PatientRecord(1, 0.97, "BloodOxygen", currentTime));
        records.add(new PatientRecord(1, 0.94, "BloodOxygen", currentTime + fiveMinutes));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + fiveMinutes);
        assertNull(result);
    }

    /**
     * Tests that no alert is generated when there's insufficient data
     */
    @Test
    void testNoAlertForInsufficientData() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 0.97, "BloodOxygen", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime, currentTime + 1000);
        assertNull(result);
    }
}

/**
 * Test class for TriggeredAlerts strategy
 */
class TriggeredAlertsTest {
    private TriggeredAlerts strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new TriggeredAlerts();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that an alert is generated when a manual trigger is present
     */
    @Test
    void testAlertForManualTrigger() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 1.0, "ManualTrigger", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Manual Alert Triggered", result.getCondition());
    }

    /**
     * Tests that no alert is generated when no manual trigger is present
     */
    @Test
    void testNoAlertWithoutManualTrigger() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 120, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNull(result);
    }
}

/**
 * Test class for BloodSaturationAlert strategy
 */
class BloodSaturationAlertTest {
    private BloodSaturationAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new BloodSaturationAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that an alert is generated for low blood saturation
     */
    @Test
    void testAlertForLowSaturation() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 0.91, "BloodOxygen", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNotNull(result);
        assertEquals("Low Blood Saturation Alert", result.getCondition());
    }

    /**
     * Tests that no alert is generated for normal blood saturation
     */
    @Test
    void testNoAlertForNormalSaturation() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 0.95, "BloodOxygen", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 1000, currentTime + 1000);
        assertNull(result);
    }
}

/**
 * Test class for BPAlert strategy
 */
class BPAlertTest {
    private BPAlert strategy;
    private Patient patient;
    private long currentTime;

    @BeforeEach
    void setUp() {
        strategy = new BPAlert();
        patient = new Patient(1);
        currentTime = System.currentTimeMillis();
    }

    /**
     * Tests that an alert is generated for increasing trend in blood pressure
     */
    @Test
    void testAlertForIncreasingTrend() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 100, "SystolicBloodPressure", currentTime - 2000));
        records.add(new PatientRecord(1, 115, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 130, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 3000, currentTime);
        assertNotNull(result);
        assertEquals("Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that an alert is generated for decreasing trend in blood pressure
     */
    @Test
    void testAlertForDecreasingTrend() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 130, "SystolicBloodPressure", currentTime - 2000));
        records.add(new PatientRecord(1, 115, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 100, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 3000, currentTime);
        assertNotNull(result);
        assertEquals("Blood Pressure Alert", result.getCondition());
    }

    /**
     * Tests that no alert is generated for small changes in blood pressure
     */
    @Test
    void testNoAlertForSmallChanges() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 100, "SystolicBloodPressure", currentTime - 2000));
        records.add(new PatientRecord(1, 105, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 110, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 3000, currentTime);
        assertNull(result);
    }

    /**
     * Tests that no alert is generated when there's insufficient data
     */
    @Test
    void testNoAlertForInsufficientData() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 100, "SystolicBloodPressure", currentTime - 1000));
        records.add(new PatientRecord(1, 115, "SystolicBloodPressure", currentTime));
        for (PatientRecord record : records) {
            patient.addRecord(record.getMeasurementValue(), record.getRecordType(), record.getTimestamp());
        }
        
        Alert result = strategy.evaluate(patient, currentTime - 2000, currentTime);
        assertNull(result);
    }
}