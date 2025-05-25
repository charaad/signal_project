package data_management;

import com.data_management.WebSocketDataReader;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;

/**
 * Unit tests for the WebSocketDataReader class.
 * Tests focus on connection management, data reading, and error handling.
 */
class WebSocketDataReaderTest {

    private DataStorage testDataStorage;
    private WebSocketDataReader dataReader;

    /**
     * Sets up a test environment before each test.
     * Initializes a test DataStorage and a WebSocketDataReader.
     */
    @BeforeEach
    void setUp() {
        testDataStorage = new DataStorage();
        dataReader = new WebSocketDataReader();
    }

    /**
     * Tests that connecting to an invalid URI throws an IOException.
     */
    @Test
    void testConnectWithInvalidUri() {
        assertThrows(IOException.class, () -> {
            dataReader.connectAndReadData(testDataStorage, "invalid uri");
        });
    }

    /**
     * Tests that reading data without first connecting throws an IOException.
     */
    @Test
    void testReadDataWithoutConnection() {
        assertThrows(IOException.class, () -> {
            dataReader.readData(testDataStorage);
        });
    }

    /**
     * Tests that isConnected returns false when not connected.
     */
    @Test
    void testIsConnectedWhenNotConnected() {
        assertFalse(dataReader.isConnected());
    }

    /**
     * Tests that disconnect works when not connected (should not throw exceptions).
     */
    @Test
    void testDisconnectWhenNotConnected() throws IOException {
        dataReader.disconnect();
        assertFalse(dataReader.isConnected());
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