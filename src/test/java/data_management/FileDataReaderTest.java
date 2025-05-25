package data_management;

import com.data_management.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FileDataReader} class.
 * <p>
 * These tests verify that data is read correctly from files,
 * malformed files are handled gracefully, and directory validation is performed.
 */
class FileDataReaderTest {
    
    /** Temporary directory created for test file I/O. Automatically cleaned up after tests. */
    @TempDir
    Path tempDir;
    
    /**
     * Tests reading multiple valid data files and verifies that the
     * correct {@link PatientRecord} entries are stored in {@link DataStorage}.
     *
     * @throws IOException if file creation fails
     */
    @Test
    void testReadDataWithValidFiles() throws IOException {
        // Create test files
        Path file1 = tempDir.resolve("data1.txt");
        Files.writeString(file1, "1,1627842123000,HeartRate,78.0,bpm\n2,1627842124000,BloodPressure,120.0,mmHg");
        
        Path file2 = tempDir.resolve("data2.txt");
        Files.writeString(file2, "1,1627842125000,HeartRate,82.0,bpm");
        
        // Test
        DataStorage storage = new DataStorage();
        DataReader reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);
        
        // Verify
        List<PatientRecord> records1 = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(2, records1.size());
        assertEquals(78.0, records1.get(0).getMeasurementValue());
        
        List<PatientRecord> records2 = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1, records2.size());
        assertEquals(120.0, records2.get(0).getMeasurementValue());
    }
    
     /**
     * Tests behavior when reading from a non-existent directory.
     * Verifies that an {@link IOException} is thrown.
     */
    @Test
    void testReadDataWithInvalidDirectory() {
        DataReader reader = new FileDataReader("/nonexistent/directory");
        assertThrows(IOException.class, () -> reader.readData(new DataStorage()));
    }
    
    /**
     * Tests reading from a malformed file (incorrect format).
     * Ensures that no exceptions are thrown and no records are stored.
     *
     * @throws IOException if the test file cannot be created
     */
    @Test
    void testReadDataWithMalformedFile() throws IOException {
        Path file = tempDir.resolve("malformed.txt");
        Files.writeString(file, "invalid,data,format");
        
        DataStorage storage = new DataStorage();
        DataReader reader = new FileDataReader(tempDir.toString());
        
        assertDoesNotThrow(() -> reader.readData(storage));
        assertTrue(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty());
    }
    
    /**
     * Tests parsing of a well-formed CSV line using {@link FileDataReader#parseLine(String)}.
     * Verifies that the resulting {@link PatientRecord} has correct field values.
     */
    @Test
    void testParseLineValid() {
        FileDataReader reader = new FileDataReader("");
        String line = "1,1627842123000,HeartRate,78.0,bpm";
        
        PatientRecord record = reader.parseLine(line);
        assertNotNull(record);
        assertEquals(1, record.getPatientId());
        assertEquals(1627842123000L, record.getTimestamp());
        assertEquals("HeartRate", record.getRecordType());
        assertEquals(78.0, record.getMeasurementValue());
    }
    
    /**
     * Tests parsing of malformed lines using {@link FileDataReader#parseLine(String)}.
     * Verifies that null is returned for invalid input formats.
     */
    @Test
    void testParseLineInvalid() {
        FileDataReader reader = new FileDataReader("");
        assertNull(reader.parseLine("invalid,data"));
        assertNull(reader.parseLine("1,notanumber,HeartRate,78.0,bpm"));
    }
}
