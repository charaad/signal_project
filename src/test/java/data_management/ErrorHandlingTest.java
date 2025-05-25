package data_management;

import com.data_management.*;
import com.alerts.AlertGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.java_websocket.handshake.ServerHandshake;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests for verifying the system's error handling capabilities,
 * particularly around network failures and data transmission issues.
 */
class ErrorHandlingTest {

    private DataStorage dataStorage;
    private TestWebSocketClient webSocketClient;
    private AlertGenerator alertGenerator;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() throws URISyntaxException {
        DataStorage.resetInstance();
        dataStorage = DataStorage.getInstance();
        alertGenerator = new AlertGenerator(dataStorage);
        webSocketClient = new TestWebSocketClient("ws://dummyuri", dataStorage);
    }

    /**
     * Tests that connection errors are properly handled and don't crash the system.
     */
    @Test
    void testConnectionErrorHandling() {
        // Simulate connection error
        webSocketClient.onError(new Exception("Simulated connection error"));
        
        // Verify system remains in a stable state
        assertFalse(webSocketClient.isOpen(), "Connection should be closed after error");
        assertTrue(dataStorage.getAllPatients().isEmpty(), "No data should be stored from failed connection");
    }

    /**
     * Tests that message processing continues after recovering from an error.
     */
    @Test
    void testErrorRecovery() {
        // First simulate an error
        webSocketClient.onError(new Exception("Simulated error"));
        
        // Then send valid data
        String validMessage = "1,1623456789000,HeartRate,75.0";
        webSocketClient.onMessage(validMessage);
        
        // Verify data was processed despite previous error
        assertEquals(1, dataStorage.getAllPatients().size(), "System should recover and process valid messages");
    }

    /**
     * Tests that the system handles sudden connection closures gracefully.
     */
    @Test
    void testUnexpectedConnectionClosure() {
        // Simulate unexpected connection close
        webSocketClient.onClose(1006, "Abnormal closure", true);
        
        // Verify closure was handled
        assertFalse(webSocketClient.isOpen(), "Connection should be closed");
        
        // Verify system can receive new connections
        assertDoesNotThrow(() -> webSocketClient.reconnect(), "Should be able to reconnect after closure");
    }

    /**
     * Tests that partial/incomplete messages are handled without crashing.
     */
    @Test
    void testPartialMessageHandling() {
        String partialMessage1 = "1,1623456789000"; // Missing record type and value
        String partialMessage2 = "2"; // Only patient ID
        
        webSocketClient.onMessage(partialMessage1);
        webSocketClient.onMessage(partialMessage2);
        
        // Verify no data was stored from partial messages
        assertTrue(dataStorage.getAllPatients().isEmpty(), "No data should be stored from partial messages");
    }

    /**
     * Tests that the system maintains data integrity during high error rates.
     */
    @Test
    void testHighErrorRateHandling() {
        // Send mix of valid and invalid messages
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                // Valid message every 3rd iteration
                String validMessage = i + ",162345678900" + i + ",HeartRate," + (70 + i);
                webSocketClient.onMessage(validMessage);
            } else {
                // Invalid message
                webSocketClient.onError(new Exception("Error #" + i));
            }
        }
        
        // Verify only valid messages were processed
        assertEquals(4, dataStorage.getAllPatients().size(), "Only valid messages should be stored");
    }

    /**
     * Test implementation of WebSocketClient that allows for error simulation.
     */
    private static class TestWebSocketClient extends HospitalWebSocketClient {
        private AtomicBoolean isOpen = new AtomicBoolean(false);

        public TestWebSocketClient(String serverUri, DataStorage dataStorage) throws URISyntaxException {
            super(serverUri, dataStorage);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            super.onOpen(handshakedata);
            isOpen.set(true);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            super.onClose(code, reason, remote);
            isOpen.set(false);
        }

        @Override
        public boolean isOpen() {
            return isOpen.get();
        }

        public void reconnect() {
            isOpen.set(true);
        }
    }
}
