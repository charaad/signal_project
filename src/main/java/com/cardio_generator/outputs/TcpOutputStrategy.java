package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/** 
 * Initialises server socket.
 * Once client connection is estabelished, sends patient data as CSV through tCP connection.
 * Implements OutputStrategy interface.
 * Strategy stores patient data in a TCP socket.
 * The data format is: <code>patientId,timestamp,label,data</code>.
*/
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /** 
     * Constructor for TcpOutputStrategy initializes the server socket and waits for client connection.
     * @param port the port number on which the server will listen for incoming connections.
    */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs and stores patient data in a TCP socket.
     * @param patientId as unique identifier for patients.
     * @param timestamp as time of data generation.
     * @param label as type of data being generated.
     * @param data as actual data value being generated.
     * Does nothing if no connection.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
