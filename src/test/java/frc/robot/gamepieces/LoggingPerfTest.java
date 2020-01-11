package frc.robot.gamepieces;

import static org.junit.Assert.fail;

import frc.robot.Robot.RobotMode;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.utilities.PerfTimer;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoggingPerfTest {

  private static Telemetry telemetry;
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(LoggingPerfTest.class.getName());

  private PerfTimer gamepieceTimer;
 
  private static GamePieceController controller;

  // Inputs normally from driver station

  // Driver camera
  private boolean driveCameraFront; 
  private boolean driveCameraRear;

  /**
   * Sets up robot in simulation mode and gets the test object.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.ROBOT_2019);

    controller = GamePieceController.getInstance();

    telemetry = Telemetry.getInstance();
    telemetry.robotMode(RobotMode.EXTERNAL_TEST);
  }

  /**
   * Resets the game piece controller before each test.
   */
  @Before
  public void init() {
    
    gamepieceTimer = PerfTimer.timer("Gamepieces");

    // Set input defaults
    // override in individual tests
    driveCameraFront = false; 
    driveCameraRear = false;
    LOGGER.debug("Reset the game pieces to the initial game state");

    // Verify initial state    
    telemetry.start();
  }

  @AfterClass
  public static void closeAll() {
    PerfTimer.print();
  }

  @Test
  public void timeNetworkTableVersionTest() {
    int count = 100;
    for (int i = 0; i < count; i++) {
      try {
        Thread.sleep(RobotMap.ITERATION_TIME_MS);
      } catch (InterruptedException e) {
        fail(e.toString());
        LOGGER.trace(e);
      }
  
      gamepieceTimer.start();
      callProcessState();
      gamepieceTimer.end();
    }

    assert(true);
  }

  private void callProcessState() {
    controller.processGamePieceState(
        driveCameraFront, 
        driveCameraRear);
  }



}

