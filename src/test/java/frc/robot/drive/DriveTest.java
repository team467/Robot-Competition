package frc.robot.drive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Drive tests are long.")
public class DriveTest {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(DriveTest.class.getName());

  private static Drive drive;

  /**
   * Initializes the robot to the competition settings and 
   * sets the drive to use the simulator.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.Competition_1);
    RobotMap.useSimulator = true;
    drive = Drive.getInstance();
    SmartDashboard.putString("DB/String 1", "0.001"); // P Left
    SmartDashboard.putString("DB/String 6", "0.001"); // P Right
    SmartDashboard.putString("DB/String 3", "0.0"); // P Left
    SmartDashboard.putString("DB/String 8", "0.0"); // D Right
    SmartDashboard.putString("DB/String 4", "0.0"); // F Left
    SmartDashboard.putString("DB/String 9", "0.0"); // F Right
    drive.readPidsFromSmartDashboard(RobotMap.PID_SLOT_DRIVE);
  }

  @Before
  public void init() {
    drive.zero();
  }

  @Ignore("Long running test.")
  @Test
  public void testDriveForwardOneFoot() {

    int pidSlot = 0;
    int numberOfIterations = 300;

    double initialDistance = drive.absoluteDistanceMoved();
    double targetDistance = 1.0;
    for (int i = 0; i < numberOfIterations; i++) {
      LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing moving 1 foot");
      drive.tuneForward(targetDistance, pidSlot);
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    // Number of ticks it allows for error tolerance on either side of target
    double tolerance = 0.1; 
    // Todo: create specific PIDs for the simulator and tighten the tolerance.
    double error = drive.absoluteDistanceMoved() - initialDistance;
    assertEquals(targetDistance, error, tolerance);
  }

}