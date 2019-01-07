package frc.robot.drive;

import static org.junit.Assert.assertTrue;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class TalonProxyTest {

  private static int TEST_DEVICE_NUMBER = 1;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(TalonProxyTest.class.getName());
  private static WpiTalonSrxInterface motor;

  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.Competition_1);
    motor = TalonProxy.create(TEST_DEVICE_NUMBER);
  }

  @Test
  public void testProxyToSimulatedMotor() {
    RobotMap.useSimulator = true;
    LOGGER.trace("TEST|SIMULATOR", "Testing simulator");
    boolean isSimulated = (motor.getName().contains("Simulated")) ? true : false;
    assertTrue(isSimulated);
  }

}

