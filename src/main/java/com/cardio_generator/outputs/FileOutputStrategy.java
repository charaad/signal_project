package com.cardio_generator.outputs;

//imports added for error handling
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

//Class comments added
/*
 * This class sets up a base directory.
 * Organises and creates files based on data labels.
 * Formats data by including patient ID, timestamp, label and data value in each line.
 * Handles errors by logging them.
 * Uses a ConcurrentHashMap to store file paths for each label.
 */
public class FileOutputStrategy implements OutputStrategy {

    private static final Logger logger = Logger.getLogger(FileOutputStrategy.class.getName());

    //base directory for file output
    //naming starts in lowercase
    private String baseDirectory; 

    //Map for storage of file paths for each label
    //remove final keyword
    private ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    
    /**
     * Constructor for FileOutputStrategy
     * @param baseDirectory as a baseDirectory the base directory where files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    //method comments added
    /**
     * Outputs and stores patient data in a file based on label.
     * @param patientId as unique identifier for patients.
     * @param timestamp as time of data generation.
     * @param label as type of data being generated.
     * @param data as actual data value being generated.
     * @throws IOException for file handling and explains error.
     * Sets up the base directory for file output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory if non-existent
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            // Log the error
            logger.log(Level.SEVERE, "Error creating base directory: " + baseDirectory, e);
            return;
        }

        // Set the FilePath variable for label
        //change name to camelCase
        String filePath = fileMap.computeIfAbsent(
            label, k -> Paths.get(baseDirectory, label + ".txt").toString()
            ); 

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
            //log exception if any and handle using IOException
        } catch (IOException  e) {
            logger.log(Level.SEVERE,"Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}