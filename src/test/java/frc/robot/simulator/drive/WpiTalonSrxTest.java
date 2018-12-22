package frc.robot.simulator.drive;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class WpiTalonSrxTest {

  private static int TEST_DEVICE_NUMBER = 1;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(WpiTalonSrxTest.class.getName());
  
  private static WpiTalonSrx motor;

  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.Competition_1);
    motor = new WpiTalonSrx(TEST_DEVICE_NUMBER);
  }

  @Before
  public void init() {
    motor.setSelectedSensorPosition(0, RobotMap.PID_SLOT_DRIVE, RobotMap.TALON_TIMEOUT);
  } 

  @Ignore("Long running")
  @Test
  public void testBasicForwardFullSpeed() {
    LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing forward full speed");
    int numberOfIterations = 20;
    int robotIterationTime = RobotMap.ITERATION_TIME_MS;
    int initialSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Initial Sensor Position: " + initialSensorPosition);
    for (int i = 0; i < numberOfIterations; i++) {
      double forwardSpeed = 1.0;
      motor.set(forwardSpeed);
      try {
        Thread.sleep(robotIterationTime);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    int currentSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Sensor Positions: " 
    //     + currentSensorPosition + " > " + initialSensorPosition);
    boolean moved = (currentSensorPosition > initialSensorPosition) ? true : false;
    assertTrue(moved);
    // TODO: Determine the exact amount it should have moved and check to see if move is correct
  }

  @Ignore("Long running")
  @Test
  public void testBasicForwardPosition() {
    int numberOfIterations = 2000;
    int robotIterationTime = RobotMap.ITERATION_TIME_MS;
    double target = 1.0 * 12.0
        / RobotMap.WHEEL_CIRCUMFERENCE * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    // int initialSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Initial Sensor Position: " 
    //     + initialSensorPosition + " Target: " + target);
    motor.config_kP(0, 0.001, RobotMap.TALON_TIMEOUT);
    motor.config_kD(0, 0.0, RobotMap.TALON_TIMEOUT);
    for (int i = 0; i < numberOfIterations; i++) {
      LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing forward full speed");
      motor.set(ControlMode.Position, target);
      // System.err.println(
      //       "Target: " + target  
      //     + " Pos: " + motor.getSelectedSensorPosition() 
      //     + " Error: " + motor.getClosedLoopError()
      //     + " Percent Output: " + df.format(motor.getMotorOutputPercent())
      //     + " Bus Voltage: " + df.format(motor.getBusVoltage())
      //     + " RPM: " + (motor.getSelectedSensorVelocity() 
      //       * 10 * 60 / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION)
      // );
      try {
        Thread.sleep(robotIterationTime);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    // Number of ticks it allows for error tollerance on either side of target
    int tolerance = 50; 
    LOGGER.debug("TEST|DRIVE", "Final position error: {}", motor.getClosedLoopError());
    boolean inTargetRange = (Math.abs(motor.getClosedLoopError()) < tolerance) ? true : false;
    assertTrue(inTargetRange);
  }

  /**
   * Sleep for 2 seconds so that the motor stops between tests. 
   */
  @After
  public void tearDown() {
    motor.set(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      fail(e.toString());
      LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
    }
  }

}