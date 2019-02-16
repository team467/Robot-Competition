package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.usercontrol.DriverStation467;

import org.apache.logging.log4j.Logger;

public class VisionController {

  private static VisionController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(VisionController.class.getName());

  private NetworkTable vision;
  private DriverStation467 driverStation;

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */
  public static VisionController getInstance() {
    if (instance == null) {
      instance = new VisionController();
    }
    return instance;
  }

  private VisionController() {
    vision = NetworkTableInstance.getDefault().getTable("vision");  
    driverStation = DriverStation467.getInstance();
  }

  public double angle() {
    double angle = vision.getEntry("angle").getDouble(-1000);
    LOGGER.debug("Angle from network table is {}", angle);
    return angle;
  }

  /**
   * Sets the feedback for the navigator, including LEDs and rumble.
   */
  public void navigatorFeedback() {
    double angle = angle();
    boolean haveAngle = vision.getEntry("haveAngle").getBoolean(false);
    if (haveAngle) {
      if (Math.abs(angle) <= RobotMap.ON_TARGET) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        // TODO: RUMBLE
        driverStation.driverSetLeftRumble(1.0);
        driverStation.driverSetRightRumble(1.0);
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_ONE) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        // TODO: RUMBLE
        driverStation.driverSetLeftRumble(0.2);
        driverStation.driverSetRightRumble(0.2);
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_TWO) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_THREE) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_FOUR) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
      } else {
        // TODO: Debug Log
      }
    }
  }

}