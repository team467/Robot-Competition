package frc.robot.vision;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
public class VisionController {

  private static VisionController instance = null;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(VisionController.class.getName());
  
  Gyrometer gyro;

  NetworkTableInstance inst;
  NetworkTable table;
  NetworkTableEntry netAngle;
  NetworkTableEntry netDist;
  NetworkTableEntry net_Have_Angle;
  NetworkTableEntry net_Have_Dist;

  public boolean aligned = false;


  double angle;
  double dist;
  boolean haveDistance;
  boolean haveAngle;
  boolean isTurnDone = false; //SmartDashboard.getBoolean("Turn", false);
  boolean isDriveDone = false;
  double robotTurner;
  double speed;

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
    inst = NetworkTableInstance.getDefault();
    table = inst.getTable("vision");
    gyro = Gyrometer.getInstance();
  }

  public double angle() {
    netAngle = table.getEntry("TurningAngle");
    angle = netAngle.getDouble(180);
    angle -= RobotMap.VISION_ANGLE_CENTER;
    LOGGER.debug("Angle from network table is {}", angle);
    return angle;
  }

  public boolean hasAngle(){
    net_Have_Angle = table.getEntry("haveAngle");
    haveAngle = net_Have_Angle.getBoolean(false);
    return haveAngle;
  }

  public boolean atAngle() {
    return Math.abs(angle()) < 4;
  }

  public boolean hasDistance() {
    net_Have_Dist = table.getEntry("haveDistance");
    return net_Have_Dist.getBoolean(false);
  }

  public double dist() {
    netDist = table.getEntry("DistanceFromTarget");
   return netDist.getDouble(0.0);
  }

  /**
   * tells the robot to turn
   */
  public double setTurn() {
    //will be redone, magic numbers will be removed in a future branch
    // Andrew you were wrong
    if (!hasAngle()) {
      return 0.0;
    }

    if (angle() <= RobotMap.VISION_ANGLE_BUFFER && angle() >= -RobotMap.VISION_ANGLE_BUFFER) {//Math.abs(angle() + gyro.getPitchDegrees())
        aligned = true;
        LOGGER.debug("Gyro angle {} deg, Camera angle {} deg, shooting", gyro.getPitchDegrees(), angle());
        return 0.0;
    } 
    
    // if ((gyro.getPitchDegrees()) < angle()) {
    if (angle() > 0) {
      LOGGER.debug("Gyro angle {} deg, Camera angle {} deg, turn right", gyro.getPitchDegrees(), angle());
        return RobotMap.AUTOALIGN_TURN_SPEED;
    }

    // if ((gyro.getPitchDegrees()) > angle()) {
    if (angle() < 0) {
      LOGGER.debug("Gyro angle {} deg, Camera angle {} deg, turn left", gyro.getPitchDegrees(), angle());
        return -RobotMap.AUTOALIGN_TURN_SPEED;
    }

    return 0;
  }

  public void resetGyro() {
    gyro.reset();
  }

  public double setDistDrive() {
    if(dist() > 40 && hasDistance()) {
      speed = -0.2;
    } else {
      speed = 0;
    }

    return speed;
  }

  public double determineShooterSpeed() {
    double shooterSpeed;
    double shooterPreviousSpeed = RobotMap.MANUAL_MODE_SHOOTER_SPEED;
    if (hasDistance()) {
      shooterSpeed = ((0.16120202 * dist() + 65.5092) / 100) * 0.95;
      shooterPreviousSpeed = shooterSpeed;
    } else {
      shooterSpeed = shooterPreviousSpeed;
    }

    return shooterSpeed;
  }

  /**
   * Sets the feedback for the navigator, including LEDs and rumble.
   */
  
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

}