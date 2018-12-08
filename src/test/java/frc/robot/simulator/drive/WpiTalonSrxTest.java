package frc.robot.simulator.drive;

import static org.junit.jupiter.api.Assertions.fail;

import java.text.DecimalFormat;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotID;

public class WpiTalonSrxTest {

  private static int TEST_DEVICE_NUMBER = 1;

  private static final Logger LOGGER = LogManager.getLogger(WpiTalonSrxTest.class);
  
  private static final DecimalFormat df = new DecimalFormat("#0.0");

  private static WpiTalonSrx motor;

  @BeforeAll
  static void initAll() {
    RobotMap.init(RobotID.Competition_1);
    motor = new WpiTalonSrx(TEST_DEVICE_NUMBER);
  }

  @BeforeEach
  void init() {
    motor.setSelectedSensorPosition(0);
  } 

  @Test
  @DisplayName("Forward full speed for 5 seconds")
  void testBasicForwardFullSpeed() {
    LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing forward full speed");
    int numberOfIterations = 250;
    int robotIterationTime = 20; // TODO: Set in Robot Map
    int initialSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Initial Sensor Position: " + initialSensorPosition);
    for (int i = 0; i < numberOfIterations; i++) {
      double forwardSpeed = 1.0;
      motor.set(forwardSpeed);
      try {
        Thread.sleep(robotIterationTime);
      } catch (InterruptedException e) {
        fail("Test thread interupted", e);
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    int currentSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Sensor Positions: " + currentSensorPosition + " > " + initialSensorPosition);
    boolean moved = (currentSensorPosition > initialSensorPosition) ? true : false;
    assert(moved);
    // TODO: Determine the exact amount it should have moved and check to see if move is correct
  }

  @Test
  @DisplayName("Go forward 1 foot")
  void testBasicForwardPosition() {
    int numberOfIterations = 2000;
    int robotIterationTime = 20; // TODO: Set in Robot Map
    double target = 1.0 * 12.0 / RobotMap.WHEEL_CIRCUMFERENCE * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    int initialSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Initial Sensor Position: " + initialSensorPosition + " Target: " + target);
    motor.config_kP(0, 0.001);
    motor.config_kD(0, 0.0);
    for (int i = 0; i < numberOfIterations; i++) {
      LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing forward full speed");
      motor.set(ControlMode.Position, target);
      // System.err.println(
      //       "Target: " + target  
      //     + " Pos: " + motor.getSelectedSensorPosition() 
      //     + " Error: " + motor.getClosedLoopError()
      //     + " Percent Output: " + df.format(motor.getMotorOutputPercent())
      //     + " Bus Voltage: " + df.format(motor.getBusVoltage())
      //     + " RPM: " + (motor.getSelectedSensorVelocity() * 10 * 60 / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION)
      //     );
      try {
        Thread.sleep(robotIterationTime);
      } catch (InterruptedException e) {
        fail("Test thread interupted", e);
        LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
      }
    }
    // Number of ticks it allows for error tollerance on either side of target
    int tolerance = 50; 
    System.err.println(motor.getClosedLoopError());
    boolean inTargetRange = (Math.abs(motor.getClosedLoopError()) < tolerance ) ? true : false;
    assert(inTargetRange);
  }

  @AfterEach
  void tearDown() {
    // Sleep for 5 seconds so that the motor stops between tests.
    motor.set(0);
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      fail("Test thread interupted", e);
      LOGGER.trace("SIMULATOR|DRIVE|TEST", e);
    }
  }

  @AfterAll
  static void tearDownAll() {
  }

}