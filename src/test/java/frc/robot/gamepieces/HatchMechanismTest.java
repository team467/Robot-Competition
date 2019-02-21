package frc.robot.gamepieces;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.gamepieces.HatchMechanism.HatchLauncher;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HatchMechanismTest {

  private static int TEST_PERIODIC_ITERATIONS = 1;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(HatchMechanismTest.class.getName());

  private static Robot robot;
  private static HatchMechanism hatch;

  /**
   * Sets up robot in simulation mode and gets the test object.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.ROBOT_2019);
    Robot.enableSimulator();
    robot = new Robot();
    robot.robotInit();
    hatch = HatchMechanism.getInstance();
    hatch.enabled(true);
  }

  @Test
  public void testHatchArmIn() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing hatch arn in");
    hatch.arm(HatchArm.IN);
    boolean armState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      hatch.periodic();
      armState = (hatch.arm() == HatchArm.IN) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(armState);
  }

  @Test
  public void testHatchArmOut() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing hatch arm out");
    hatch.arm(HatchArm.OUT);
    boolean armState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      hatch.periodic();
      armState = (hatch.arm() == HatchArm.OUT) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(armState);
  }

  @Test
  public void testHatchLauncherFire() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing firing hatch launcher");
    hatch.launcher(HatchLauncher.FIRE);
    boolean launcherState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      hatch.periodic();
      launcherState = (hatch.launcher() == HatchLauncher.FIRE) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(launcherState);
  }

  @Test
  public void testHatchLauncherReset() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing resetting hatch launcher");
    hatch.launcher(HatchLauncher.RESET);
    boolean launcherState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      hatch.periodic();
      launcherState = (hatch.launcher() == HatchLauncher.RESET) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(launcherState);
  }

  @AfterClass
  public static void closeAll() {
    robot.close();
  }

}