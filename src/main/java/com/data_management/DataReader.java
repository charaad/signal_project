package com.data_management;

import java.io.IOException;

/**
 * Interface for reading patient data either from static sources or real-time streams.
 * Implementations should handle both batch and continuous data reading scenarios.
 */
public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * For file-based implementations, this would read all available data.
     * For WebSocket implementations, this would initiate a continuous connection.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a real-time data source and begins continuous data reading.
     * 
     * @param dataStorage the storage where data will be stored
     * @param uri the WebSocket server URI to connect to (e.g., "ws://localhost:8080")
     * @throws IOException if there is an error connecting to the data source
     */
    void connectAndReadData(DataStorage dataStorage, String uri) throws IOException;

    /**
     * Disconnects from the real-time data source and stops data reading.
     * 
     * @throws IOException if there is an error during disconnection
     */
    void disconnect() throws IOException;

    /**
     * Checks if the reader is currently connected to a real-time data source.
     * 
     * @return true if connected, false otherwise
     */
    boolean isConnected();
}