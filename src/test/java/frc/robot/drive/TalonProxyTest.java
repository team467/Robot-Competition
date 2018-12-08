package frc.robot.drive;

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

public class TalonProxyTest {

  private static int TEST_DEVICE_NUMBER = 1;

  private static final Logger LOGGER = LogManager.getLogger(TalonProxyTest.class);
  
  private static final DecimalFormat df = new DecimalFormat("#0.0");

  private static WpiTalonSrxInterface motor;

  @BeforeAll
    static void initAll() {
      RobotMap.init(RobotID.Competition_1);
      motor = TalonProxy.create(TEST_DEVICE_NUMBER);
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

    @Test
    @DisplayName("Use a simulated motor through the proxy")
    void testProxyToSimulatedMotor() {
      RobotMap.useSimulator = true;
      boolean isSimulated = (motor.getName().contains("Simulated")) ? true : false;
      assert(isSimulated);
    }

}

