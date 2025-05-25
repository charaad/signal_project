package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {
  
    private DataReader reader;

    @Test
    void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        
        assertSame(instance1, instance2, "Both instances should be the same");
        assertNotNull(instance1, "Instance should not be null");
    }


    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertNotNull(records, "Records should not be null");
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
        assertEquals(200.0, records.get(1).getMeasurementValue());

    }

     @Test
    void testGetRecordsForNonExistentPatient() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(999, 0L, System.currentTimeMillis());
        
        assertNotNull(records, "Should return empty list rather than null");
        assertTrue(records.isEmpty(), "Should return empty list for non-existent patient");
    }

    @Test
    void testAddDataToMultiplePatients() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 120.0, "BloodPressure", 1714376789050L);
        storage.addPatientData(2, 98.6, "Temperature", 1714376789051L);
        
        List<PatientRecord> patient1Records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        List<PatientRecord> patient2Records = storage.getRecords(2, 0L, Long.MAX_VALUE);
        
        assertEquals(1, patient1Records.size());
        assertEquals(1, patient2Records.size());
        assertEquals("BloodPressure", patient1Records.get(0).getRecordType());
        assertEquals("Temperature", patient2Records.get(0).getRecordType());
    }

    @Test
    void testGetRecordsWithTimeRange() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(1, 110.0, "HeartRate", 2000L);
        storage.addPatientData(1, 120.0, "HeartRate", 3000L);
        
        List<PatientRecord> records = storage.getRecords(1, 1500L, 2500L);
        assertEquals(1, records.size());
        assertEquals(110.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetAllPatients() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 120.0, "BloodPressure", 1714376789050L);
        storage.addPatientData(2, 98.6, "Temperature", 1714376789051L);
        storage.addPatientData(3, 72.0, "HeartRate", 1714376789052L);
        
        List<Patient> patients = storage.getAllPatients();
        assertEquals(3, patients.size());
        assertTrue(patients.stream().anyMatch(p -> p.getPatientId() == 1));
        assertTrue(patients.stream().anyMatch(p -> p.getPatientId() == 2));
        assertTrue(patients.stream().anyMatch(p -> p.getPatientId() == 3));
    }

    @Test
    void testAddMultipleRecordsSameTimestamp() {
        DataStorage storage = new DataStorage();
        long timestamp = 1714376789050L;
        storage.addPatientData(1, 100.0, "HeartRate", timestamp);
        storage.addPatientData(1, 120.0, "BloodPressure", timestamp);
        
        List<PatientRecord> records = storage.getRecords(1, timestamp, timestamp);
        assertEquals(2, records.size());
    }
}
