package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A WebSocket client that connects to a WebSocket server, receives patient data,
 * and stores it in the DataStorage system.
 */
public class HospitalWebSocketClient extends WebSocketClient {

    private DataStorage dataStorage;

    /**
     * Constructs a WebSocketClient and connects to the specified server URI.
     *
     * @param serverUri the URI of the WebSocket server to connect to
     * @param dataStorage the DataStorage instance to store received data
     * @throws URISyntaxException if the server URI is invalid
     */
    public HospitalWebSocketClient(String serverUri, DataStorage dataStorage) throws URISyntaxException {
        super(new URI(serverUri));
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server");
    }

    @Override
    public void onMessage(String message) {
        try {
            // Parse the message and store the data
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
            System.out.println("Data stored for patient " + patientId + ": " + recordType + " = " + measurementValue);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing message: " + message);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error:");
        ex.printStackTrace();
    }

    /**
     * Starts the WebSocket client and connects to the server.
     */
    public void start() {
        this.connect();
    }
}
