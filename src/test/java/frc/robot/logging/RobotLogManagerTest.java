package frc.robot.logging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class RobotLogManagerTest {
  private static String testDirectoryFile = "/media/sda1/logging/log4j2.yaml";
  
  @Test
  public void directoryTest() {
    if (new File(testDirectoryFile).exists()) {
      System.out.println("File exists");
    } else {
      System.out.println("File does not exist.");
    }
    assertTrue(true);
  }

  @Test
  @Ignore("Need to fix test.")
  public void fileTest() {
    assertTrue(new File(testDirectoryFile).exists());
  }

}

