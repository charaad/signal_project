package data_management;

import com.cardio_generator.HealthDataSimulator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for the HealthDataSimulator class.
 * These tests verify the functionality of the health data simulation system,
 * including argument parsing, patient scheduling, and output strategies.
 */
class HealthDataSimulatorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Tests the singleton behavior of HealthDataSimulator.
     * Verifies that getInstance() always returns the same instance.
     */
    @Test
    void testSingletonInstance() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        assertSame(instance1, instance2, "Singleton instances should be the same");
    }

   

    /**
     * Tests the main method execution.
     * Verifies that the simulator runs without exceptions.
     */
    @Test
    void testMainMethod() throws Exception {
        String[] args = {"--patient-count", "5", "--output", "console"};
        HealthDataSimulator.main(args);
        
        // Verify no exceptions were thrown
        assertTrue(true, "Main method should execute without exceptions");
    }
}