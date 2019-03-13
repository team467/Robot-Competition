package frc.robot.vision;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.sensors.LedI2C;
import frc.robot.sensors.LedI2C.LedBlink;
import frc.robot.sensors.LedI2C.LedColor;
import frc.robot.sensors.LedI2C.LedMode;
import org.apache.logging.log4j.Logger;

public class VisionController {

  private static VisionController instance = null;

  private static final Logger LOGGER = RobotLogManager.getLogger(VisionController.class.getName());

  private NetworkTable vision;
  private DriverStation467 driverStation;

  // Led Variables
  private LedI2C lights;
  private LedMode ledMode;
  private LedColor ledColor;
  private LedBlink ledBlink;

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

  // TODO log these
  private VisionController() {
    vision = NetworkTableInstance.getDefault().getTable("vision");
    driverStation = DriverStation467.getInstance();
    registerMetrics();
  }

  public double angle() {
    double angle = vision.getEntry("angle").getDouble(-1000);
    LOGGER.debug("Angle from network table is {}", box(angle));
    return angle;
  }

public boolean hasAngle(){
  return vision.getEntry("angle").exists();
}
  public double getAngle(){
    return vision.getEntry("angle").getDouble(-1000);
  }
  
  public void setAngle(double angle){
    vision.getEntry("angle").setDouble(angle);
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
        // mode, color, blink
        // all none for now because it needs to be implemented later
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);

      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_ONE) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        // TODO: RUMBLE
        driverStation.driverSetLeftRumble(0.2);
        driverStation.driverSetRightRumble(0.2);
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);

      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_TWO) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_THREE) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);
      } else if (Math.abs(angle) <= RobotMap.ANGLE_OFFSET_LEVEL_FOUR) {
        // TODO: Debug Log
        // TODO: LED FUNCTION
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);
      } else {
        // TODO: Debug Log
        lights.sendLedCommand(ledMode.NONE, ledColor.NONE, ledBlink.NONE);
      }
    }
  }

  private String name;
  private String subsystem;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setName(String subsystem, String name) {
    setSubsystem(subsystem);
    setName(name);
  }

  public String getSubsystem() {
    return subsystem;
  }

  public void setSubsystem(String subsystem) {
    this.subsystem = subsystem;
  }

  public void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addDoubleMetric("Vision Angle", this::getAngle);
    telemetry.addBooleanMetric("Vision Has Angle", this::hasAngle);
  }

}