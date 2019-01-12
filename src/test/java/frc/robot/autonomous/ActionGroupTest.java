package frc.robot.autonomous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.logging.RobotLogManager;
import frc.robot.drive.Drive;

public class ActionGroupTest{
    private static Logger LOGGER = RobotLogManager.getTestLogger(ActionGroupTest.class.getName());

    private static final DecimalFormat df = new DecimalFormat("####0.00");

    private Drive drive;

    public static final int AUTONOMOUS_TIME = 15;

     /**
   * Initialize drive system and load the match configuration.
   */

   @BeforeClass
    public static void initAll(){
        RobotMap.init(RobotId.Competition_1);
        RobotMap.useSimulator = true;
        RobotMap.USE_FAKE_GAME_DATA = true;
        LOGGER.trace("Using simulation for testing actions");
    }

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

    //@Test
    public void run(){
        Actions.startOnLeft();
        ActionGroup autonomous = Actions.crossAutoLine();
        boolean actionCompleted = periodic(autonomous);
        double tolerance = 1.0;
        double distanceMoved = drive.absoluteDistanceMoved();
        LOGGER.info("Target 10 ft, distance moved: {} ft", df.format(distanceMoved));
        assertEquals(10.0, distanceMoved, tolerance);
        assertTrue(actionCompleted);
    }

    @Test
    public void cancelRun(){
        Actions.startOnLeft();
        ActionGroup autonomous = Actions.crossAutoLine();
        boolean actionCompleted = periodic(autonomous);
        LOGGER.info("Test for cancel completed");
        assertEquals(true, actionCompleted);
    }
}