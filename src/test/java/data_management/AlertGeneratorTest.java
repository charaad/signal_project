package data_management;

import com.alerts.*;
import com.data_management.Patient;
import com.data_management.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertGeneratorTest {

    private Patient patient;
    private long now;
    private TestAlertGenerator alertGenerator;

    // Helper subclass to capture triggered alerts
    static class TestAlertGenerator extends AlertGenerator {
        List<Alert> triggeredAlerts = new ArrayList<>();
        public TestAlertGenerator(DataStorage ds) {
            super(ds);
        }
        @Override
        protected void triggerAlert(Alert alert) {
            triggeredAlerts.add(alert);
        }
        public List<Alert> getTriggeredAlerts() {
            return triggeredAlerts;
        }
    }

    @BeforeEach
    void setUp() {
        patient = new Patient(1);
        now = System.currentTimeMillis();
        DataStorage ds = new DataStorage();
        alertGenerator = new TestAlertGenerator(ds);
    }

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

    @Test
    void testCriticalBPAlertIsTriggered() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Systolic")), "Critical BP alert should be triggered");
    }

    @Test
    void testCombinedAlertIsTriggered() {
        patient.addRecord(85, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().toLowerCase().contains("hypotensive")), "Combined alert should be triggered");
    }

    @Test
    void testTriggeredAlertsIsTriggered() {
        patient.addRecord(1, "ManualTrigger", now - 1000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().toLowerCase().contains("manual")), "Manual trigger alert should be triggered");
    }

    @Test
    void testMultipleAlertsTriggered() {
        patient.addRecord(185, "SystolicBloodPressure", now - 10000);
        patient.addRecord(0.91, "BloodOxygen", now - 9000);
        patient.addRecord(1, "ManualTrigger", now - 8000);
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().size() >= 2, "Multiple alerts should be triggered");
    }

    @Test
    void testNoAlertsWhenNoRecords() {
        alertGenerator.evaluateData(patient);
        assertTrue(alertGenerator.getTriggeredAlerts().isEmpty(), "No alerts should be triggered when there are no records");
    }
}
