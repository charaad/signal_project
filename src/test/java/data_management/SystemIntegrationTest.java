package data_management;

import com.data_management.*;
import com.alerts.Alert;
import com.alerts.AlertGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;

/**
 * Integration tests for the complete system including WebSocketClient, DataStorage,
 * and AlertGenerator components.
 */
class SystemIntegrationTest {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
    private HospitalWebSocketClient webSocketClient;

    /**
     * Sets up the test environment before each test.
     * Initializes all system components with fresh instances.
     */
    @BeforeEach
    void setUp() throws URISyntaxException {
        // Reset the singleton instance for clean testing
        DataStorage.resetInstance();
        dataStorage = DataStorage.getInstance();
        alertGenerator = new AlertGenerator(dataStorage);
        webSocketClient = new HospitalWebSocketClient("ws://dummyuri", dataStorage);
    }

    /**
     * Tests the system's handling of malformed WebSocket messages.
     */
    @Test
    void testMalformedMessageHandling() {
        String validMessage = "3,1623456789000,HeartRate,72.0";
        String invalidFormat = "3,1623456789000,HeartRate"; // Missing value
        String invalidNumber = "3,notatimestamp,HeartRate,72.0"; // Bad timestamp

        // Process messages
        webSocketClient.onMessage(validMessage);
        webSocketClient.onMessage(invalidFormat);
        webSocketClient.onMessage(invalidNumber);

        // Verify only the valid message was processed
        Patient patient = dataStorage.getAllPatients().get(0);
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        assertEquals(1, records.size(), "Only valid message should be processed");
    }

    /**
     * Tests the system's behavior when processing data for multiple patients.
     */
    @Test
    void testMultiplePatientsHandling() {
        String patient1Data = "10,1623456789000,HeartRate,75.0";
        String patient2Data = "20,1623456789001,BloodPressure,130.0";
        String patient3Data = "30,1623456789002,BloodSaturation,95.0";

        // Process messages
        webSocketClient.onMessage(patient1Data);
        webSocketClient.onMessage(patient2Data);
        webSocketClient.onMessage(patient3Data);

        // Verify all patients were processed
        List<Patient> patients = dataStorage.getAllPatients();
        assertEquals(3, patients.size(), "All patients should be stored");
    }

    /**
     * Test implementation of AlertGenerator that captures generated alerts
     * for verification purposes.
     */
    private static class TestAlertGenerator extends AlertGenerator {
        private List<Alert> generatedAlerts = new ArrayList<>();

        public TestAlertGenerator(DataStorage dataStorage) {
            super(dataStorage);
        }

        @Override
        protected void triggerAlert(Alert alert) {
            generatedAlerts.add(alert);
        }

        public List<Alert> getGeneratedAlerts() {
            return generatedAlerts;
        }
    }
}
