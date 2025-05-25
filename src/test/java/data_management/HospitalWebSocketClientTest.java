package data_management;

import com.data_management.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;

/**
 * Unit tests for the HospitalWebSocketClient class.
 * Tests focus on message handling, connection events, and error scenarios.
 */
class HospitalWebSocketClientTest {

    private TestDataStorage testDataStorage;
    private HospitalWebSocketClient client;

    /**
     * Sets up a test environment before each test.
     * Initializes a test DataStorage and a WebSocket client with a dummy URI.
     */
    @BeforeEach
    void setUp() throws URISyntaxException {
        testDataStorage = new TestDataStorage();
        client = new HospitalWebSocketClient("ws://dummyuri", testDataStorage);
    }

    /**
     * Tests that valid messages are correctly parsed and stored.
     */
    @Test
    void testOnMessageWithValidData() {
        String validMessage = "123,1623456789000,HeartRate,75.0";
        client.onMessage(validMessage);

        assertEquals(1, testDataStorage.getPatientDataCount());
        assertEquals(123, testDataStorage.getLastPatientId());
        assertEquals("HeartRate", testDataStorage.getLastRecordType());
        assertEquals(75.0, testDataStorage.getLastMeasurementValue(), 0.001);
    }

    /**
     * Tests that messages with invalid format (too few parts) are handled gracefully.
     */
    @Test
    void testOnMessageWithInvalidFormatTooFewParts() {
        String invalidMessage = "123,1623456789000,HeartRate";
        client.onMessage(invalidMessage);
        
        assertEquals(0, testDataStorage.getPatientDataCount());
    }

    /**
     * Tests that messages with invalid format (too many parts) are handled gracefully.
     */
    @Test
    void testOnMessageWithInvalidFormatTooManyParts() {
        String invalidMessage = "123,1623456789000,HeartRate,75.0,extra";
        client.onMessage(invalidMessage);
        
        assertEquals(0, testDataStorage.getPatientDataCount());
    }

    /**
     * Tests that messages with non-numeric patient IDs are handled gracefully.
     */
    @Test
    void testOnMessageWithInvalidPatientId() {
        String invalidMessage = "abc,1623456789000,HeartRate,75.0";
        client.onMessage(invalidMessage);
        
        assertEquals(0, testDataStorage.getPatientDataCount());
    }

    /**
     * Tests that messages with non-numeric timestamps are handled gracefully.
     */
    @Test
    void testOnMessageWithInvalidTimestamp() {
        String invalidMessage = "123,notatimestamp,HeartRate,75.0";
        client.onMessage(invalidMessage);
        
        assertEquals(0, testDataStorage.getPatientDataCount());
    }

    /**
     * Tests that messages with non-numeric measurement values are handled gracefully.
     */
    @Test
    void testOnMessageWithInvalidMeasurementValue() {
        String invalidMessage = "123,1623456789000,HeartRate,notanumber";
        client.onMessage(invalidMessage);
        
        assertEquals(0, testDataStorage.getPatientDataCount());
    }

    /**
     * Tests that connection close events are handled properly.
     */
    @Test
    void testOnClose() {
        client.onClose(1000, "Normal closure", true);
        // Just verifying no exceptions are thrown
        assertTrue(true);
    }

    /**
     * Tests that error events are handled properly.
     */
    @Test
    void testOnError() {
        client.onError(new Exception("Test error"));
        // Just verifying no exceptions are thrown
        assertTrue(true);
    }

    /**
     * Test implementation of DataStorage for verification purposes.
     */
    private static class TestDataStorage extends DataStorage {
        private int patientDataCount = 0;
        private int lastPatientId;
        private String lastRecordType;
        private double lastMeasurementValue;

        @Override
        public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
            patientDataCount++;
            lastPatientId = patientId;
            lastRecordType = recordType;
            lastMeasurementValue = measurementValue;
        }

        public int getPatientDataCount() {
            return patientDataCount;
        }

        public int getLastPatientId() {
            return lastPatientId;
        }

        public String getLastRecordType() {
            return lastRecordType;
        }

        public double getLastMeasurementValue() {
            return lastMeasurementValue;
        }
    }
}
