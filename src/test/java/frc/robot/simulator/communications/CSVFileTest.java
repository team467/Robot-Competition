package frc.robot.simulator.communications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class CSVFileTest {

    @Test
    public void testcorrectReading() {
        CSVFile file = new CSVFile();
        file.loadFromFile("src/test/resources/example.txt");
        boolean success = file.toString().equals("abcd, !@#$%^&*()_+-=\n?:\";\', 1234567890");
        assertTrue("|" + file.toString() + "|", success);
    }
}