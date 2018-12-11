package frc.robot.drive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class DriveTest {

  private static final Logger LOGGER = LogManager.getLogger(TalonProxyTest.class);

  private static Drive drive;

  /**
   * Initializes the robot to the competition settings and 
   * sets the drive to use the simulator.
   */
  @BeforeClass
  public static void beforeAll() {
    RobotMap.init(RobotID.Competition_1);
    RobotMap.useSimulator = true;
    drive = Drive.getInstance();
  }

  // @DisplayName("Test driving robot forward using simulator")
  @Test
  public void testProxyToSimulatedMotor() {

    int pidSlot = 0;
    int numberOfIterations = 2000;
    int robotIterationTime = 20; // TODO: Set in Robot Map

    double initialDistance = drive.absoluteDistanceMoved();
    double targetDistance = 1.0;
    for (int i = 0; i < numberOfIterations; i++) {
      LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing moving 1 foot");
      drive.tuneForward(targetDistance, pidSlot);
      // System.err.println(drive.absoluteDistanceMoved());
      try {
        Thread.sleep(robotIterationTime);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    // Number of ticks it allows for error tolerance on either side of target
    double tolerance = 50.0; 
    // Todo: create specific PIDs for the simulator and tighten the tolerance.
    double error = drive.absoluteDistanceMoved() - initialDistance;
    // System.err.println(error);
    assertEquals(0, error, tolerance);
  }

}