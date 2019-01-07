package frc.robot.autonomous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.gui.SimulatedData;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ActionsTest {

  private static Logger LOGGER = RobotLogManager.getTestLogger(ActionsTest.class.getName());

  private static final DecimalFormat df = new DecimalFormat("####0.00");

  private Drive drive;

  public static final int AUTONOMOUS_TIME = 15;

  /**
   * Initialize drive system and load the match configuration.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.Competition_1);
    RobotMap.useSimulator = true;
    RobotMap.USE_FAKE_GAME_DATA = true;
    LOGGER.trace("Using simulation for testing actions");
  }

  /**
   * Gets the drive instance and zeros it.
   */
  @Before
  public void init() {
    SmartDashboard.putString("DB/String 1", "0.0004"); // P Left
    SmartDashboard.putString("DB/String 6", "0.0004"); // P Right
    SmartDashboard.putString("DB/String 3", "0.012"); // P Left
    SmartDashboard.putString("DB/String 8", "0.012"); // D Right
    SmartDashboard.putString("DB/String 4", "0.0"); // F Left
    SmartDashboard.putString("DB/String 9", "0.0"); // F Right
    drive = Drive.getInstance();
    drive.readPidsFromSmartDashboard(RobotMap.PID_SLOT_DRIVE);
    drive.readPidsFromSmartDashboard(RobotMap.PID_SLOT_TURN);
    drive.zero();
  }

  /**
   * Tests a basic cross the autonomous line. This is a standard requirement most years.
   */
  @Ignore("Long running test")
  @Test
  public void crossAutoLineTest() {
    Actions.startOnLeft();
    ActionGroup autonomous = Actions.crossAutoLine();
    boolean actionCompleted = periodic(autonomous);
    // TODO: Tune simulation PIDs then reduce tolerance
    double tolerance = 1.0; // Allow high tolerance now.
    double distanceMoved = drive.absoluteDistanceMoved();
    LOGGER.info("Target 10 ft, distance moved: {} ft", df.format(distanceMoved));
    assertEquals(10.0, distanceMoved, tolerance);
    assertTrue(actionCompleted);
  }

  @Ignore("Long running test")
  @Test
  public void basicSwitchOurSideLeftTest() {
    Actions.startOnLeft();
    SimulatedData.gameSpecificMessage = "LLL";
    ActionGroup autonomous = Actions.basicSwitchOurSide();
    boolean actionCompleted = periodic(autonomous);
    // TODO: Figure out how to use field position to test move.
    double distanceMoved = drive.absoluteDistanceMoved();
    assertTrue(actionCompleted);
  }

  /**
   * Simulate autonomous periodic.
   * 
   * @param autonomous the action to test
   * @return true if the autonomous mode completed 
   */
  private boolean periodic(ActionGroup autonomous) {
    // Simulate periodic for up to 15 seconds, with a 20 ms period
    boolean isComplete = false;
    autonomous.enable();
    int iterations = AUTONOMOUS_TIME * (1000 / RobotMap.ITERATION_TIME_MS);
    for (int i = 0; i < iterations; i++) {
      autonomous.run();
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace(e);
      }
      isComplete = autonomous.isComplete();
      if (isComplete) {
        LOGGER.trace("Done at iteration {}", i);
        break; // No need to wait full 15 seconds if done.
      }
    }
    return isComplete;
  }
  
}