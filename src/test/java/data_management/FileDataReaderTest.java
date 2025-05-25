package data_management;

import com.data_management.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileDataReaderTest {
    
    @TempDir
    Path tempDir;
    
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
    
    @Test
    void testReadDataWithInvalidDirectory() {
        DataReader reader = new FileDataReader("/nonexistent/directory");
        assertThrows(IOException.class, () -> reader.readData(new DataStorage()));
    }
    
    @Test
    void testReadDataWithMalformedFile() throws IOException {
        Path file = tempDir.resolve("malformed.txt");
        Files.writeString(file, "invalid,data,format");
        
        DataStorage storage = new DataStorage();
        DataReader reader = new FileDataReader(tempDir.toString());
        
        assertDoesNotThrow(() -> reader.readData(storage));
        assertTrue(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty());
    }
    
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
    
    @Test
    void testParseLineInvalid() {
        FileDataReader reader = new FileDataReader("");
        assertNull(reader.parseLine("invalid,data"));
        assertNull(reader.parseLine("1,notanumber,HeartRate,78.0,bpm"));
    }
}
