package frc.robot.simulator.drive;

import static org.junit.jupiter.api.Assertions.fail;

import java.text.DecimalFormat;

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
    motor.setSelectedSensorPosition(0);
  }

  @BeforeEach
  void init() {
  }

  @Test
  @DisplayName("Forward full speed")
  void testBasicForwardFullSpeed() {
    int numberOfIterations = 250;
    int robotIterationTime = 20; // TODO: Set in Robot Map
    int initialSensorPosition = motor.getSelectedSensorPosition();
    // System.err.println("Initial Sensor Position: " + initialSensorPosition);
    for (int i = 0; i < numberOfIterations; i++) {
      LOGGER.trace("SIMULATOR|DRIVE|TEST", "Testing forward full speed");
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

  @AfterEach
  void tearDown() {
  }

  @AfterAll
  static void tearDownAll() {
  }

}