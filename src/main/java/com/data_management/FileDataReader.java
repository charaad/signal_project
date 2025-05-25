package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * {@code FileDataReader} is an implementation of the {@link DataReader} interface
 * that reads patient data records from all files in a specified directory.
 * <p>
 * Each line of the input files is expected to be a CSV string in the following format:
 * <pre>
 *     patientId, timestamp, recordType, measurementValue, <unused_field>
 * </pre>
 * The fifth field is ignored.
 */
public class FileDataReader implements DataReader {
    private String outputDirectory;

    /**
     * Constructs a {@code FileDataReader} that reads from the given output directory.
     *
     * @param outputDirectory the directory path where data files are stored
     */
    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Reads data from all regular files in the configured output directory
     * and stores it into the provided {@link DataStorage} instance.
     *
     * @param dataStorage the data storage system to store parsed patient records
     * @throws IOException if the output directory is invalid or unreadable
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Path dirPath = Paths.get(outputDirectory);
        
        // Check if directory exists
        if (!Files.isDirectory(dirPath)) {
            throw new IOException("Invalid output directory: " + outputDirectory);
        }
        
        // Process all files in the directory
        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                 .forEach(file -> processFile(file.toFile(), dataStorage));
        }
    }

    /**
     * Processes a single file, reading it line by line and adding parsed
     * {@link PatientRecord} instances to the {@link DataStorage}.
     *
     * @param file the file to be processed
     * @param dataStorage the data storage to which parsed records are added
     */
    private void processFile(File file, DataStorage dataStorage) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PatientRecord record = parseLine(line);
                if (record != null) {
                    dataStorage.addPatientData(
                        record.getPatientId(),
                        record.getMeasurementValue(),
                        record.getRecordType(),
                        record.getTimestamp()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Parses a single line of CSV-formatted patient data into a {@link PatientRecord} object.
     * Expected format:
     * <pre>
     *     patientId, timestamp, recordType, measurementValue, <unused_field>
     * </pre>
     *
     * @param line the line of text to parse
     * @return a {@code PatientRecord} if the line is valid; {@code null} otherwise
     */
    public PatientRecord parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 5) return null;
        
        try {
            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String recordType = parts[2].trim();
            double measurementValue = Double.parseDouble(parts[3].trim());
            
            return new PatientRecord(patientId, measurementValue, recordType, timestamp);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing line: " + line);
            return null;
        }
    }
}