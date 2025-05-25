package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of DataReader that reads patient data from a WebSocket server
 * in real-time and stores it in the provided DataStorage.
 */
public class WebSocketDataReader implements DataReader {
    private DataStorage dataStorage;
    private PatientDataWebSocketClient client;
    private String currentUri;

    /**
     * Reads data from a WebSocket server and stores it in the data storage.
     * This implementation initiates a continuous WebSocket connection.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (currentUri == null) {
            throw new IOException("No WebSocket URI specified. Use connectAndReadData() instead.");
        }
        connectAndReadData(dataStorage, currentUri);
    }

    /**
     * Connects to a WebSocket server and begins continuous data reading.
     *
     * @param dataStorage the storage where data will be stored
     * @param uri the WebSocket server URI to connect to
     * @throws IOException if there is an error connecting to the data source
     */
    @Override
    public void connectAndReadData(DataStorage dataStorage, String uri) throws IOException {
        this.dataStorage = dataStorage;
        this.currentUri = uri;

        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }

            client = new PatientDataWebSocketClient(new URI(uri), dataStorage);
            client.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI: " + uri, e);
        } catch (Exception e) {
            throw new IOException("Failed to connect to WebSocket server", e);
        }
    }

    /**
     * Disconnects from the WebSocket server and stops data reading.
     *
     * @throws IOException if there is an error during disconnection
     */
    @Override
    public void disconnect() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Checks if the reader is currently connected to the WebSocket server.
     *
     * @return true if connected, false otherwise
     */
    @Override
    public boolean isConnected() {
        return client != null && client.isOpen();
    }

    /**
     * Inner WebSocket client class that handles the actual WebSocket communication
     * and data processing.
     */
    private static class PatientDataWebSocketClient extends WebSocketClient {
        private final DataStorage dataStorage;

        public PatientDataWebSocketClient(URI serverUri, DataStorage dataStorage) {
            super(serverUri);
            this.dataStorage = dataStorage;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connected to WebSocket server: " + getURI());
        }

        @Override
        public void onMessage(String message) {
            try {
                // Expected format: patientId,timestamp,recordType,measurementValue
                String[] parts = message.split(",");
                if (parts.length != 4) {
                    System.err.println("Invalid message format: " + message);
                    return;
                }

                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String recordType = parts[2];
                double measurementValue = Double.parseDouble(parts[3]);

                // Store the data
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                System.out.println("Stored data for patient " + patientId + ": " + recordType + " = " + measurementValue);

            } catch (NumberFormatException e) {
                System.err.println("Error parsing numeric values in message: " + message);
            } catch (Exception e) {
                System.err.println("Error processing message: " + message);
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Connection closed: " + reason + " (code: " + code + ")");
        }

        @Override
        public void onError(Exception ex) {
            System.err.println("WebSocket error:");
            ex.printStackTrace();
        }
    }
}