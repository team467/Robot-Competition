package frc.robot.drive;

import static org.junit.Assert.assertTrue;

import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class TalonProxyTest {

  private static int TEST_DEVICE_NUMBER = 1;

  private static final Logger LOGGER = LogManager.getLogger(TalonProxyTest.class);
  
  private static WpiTalonSrxInterface motor;

  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotID.Competition_1);
    motor = TalonProxy.create(TEST_DEVICE_NUMBER);
  }

  @Test
  public void testProxyToSimulatedMotor() {
    RobotMap.useSimulator = true;
    LOGGER.log(Level.TRACE, "TEST|SIMULATOR", "Testing simulator");
    boolean isSimulated = (motor.getName().contains("Simulated")) ? true : false;
    assertTrue(isSimulated);
  }

}

