package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileDataReader implements DataReader {
    private String outputDirectory;

    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

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

    private PatientRecord parseLine(String line) {
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