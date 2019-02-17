package frc.robot.simulator.communications;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class CSVFileTest {

    @Test
    @Ignore("Need to fix test.")
    public void testcorrectReading() {
      CSVFile file = new CSVFile();
      String out = file.loadFromFile("test.txt");
      boolean success = file.toString().equals("abcd, !@#$%^&*()_+-=\n?:\";\', 1234567890");
      assertTrue("|" + file.toString() + "|", success);
    }
}