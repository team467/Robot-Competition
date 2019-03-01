package frc.robot.gamepieces;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CargoIntakeTest {

  private static int TEST_PERIODIC_ITERATIONS = 1;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CargoIntakeTest.class.getName());

  private static Robot robot;
  private static CargoIntake cargoIntake;

  /**
   * Sets up robot in simulation mode and gets the test object.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.ROBOT_2019);
    Robot.enableSimulator();
    robot = new Robot();
    robot.robotInit();
    cargoIntake = CargoIntake.getInstance();
    cargoIntake.enabled(true);
  }

  @Test
  public void testCargoIntakeArmUp() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing cargo intake arm up");
    //cargoIntake.arm(CargoIntakeArm.UP);
    boolean rollerState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      cargoIntake.periodic();
      rollerState = (cargoIntake.arm() == CargoIntakeArm.UP) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(rollerState);
  }

  @Test
  public void testCargoIntakeArmDown() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing cargo arm down");
    cargoIntake.arm(CargoIntakeArm.DOWN);
    boolean armState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      cargoIntake.periodic();
      armState = (cargoIntake.arm() == CargoIntakeArm.DOWN) ? true : false;
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
  public void testRollerIntake() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing cargo intake mechanism");
    cargoIntake.roller(CargoIntakeRoller.INTAKE);
    boolean rollerState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      cargoIntake.periodic();
      rollerState = (cargoIntake.roller() == CargoIntakeRoller.INTAKE) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(rollerState);
  }

  @Test
  public void testRollerReject() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing cargo reject mechanism");
    cargoIntake.roller(CargoIntakeRoller.REJECT);
    boolean rollerState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      cargoIntake.periodic();
      rollerState = (cargoIntake.roller() == CargoIntakeRoller.REJECT) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(rollerState);
  }

  @Test
  public void testRollerOff() {
    LOGGER.trace("TEST|GAMEPIECES", "Testing turning off cargo intake");
    cargoIntake.roller(CargoIntakeRoller.STOP);
    boolean rollerState = false;
    for (int i = 0; i < TEST_PERIODIC_ITERATIONS; i++) {
      cargoIntake.periodic();
      rollerState = (cargoIntake.roller() == CargoIntakeRoller.STOP) ? true : false;
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    assertTrue(rollerState);
  }

  @AfterClass
  public static void closeAll() {
    robot.close();
  }

}