package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.communications.RobotData;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.utilities.MathUtils;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;


public class Drive extends DifferentialDrive implements AutoDrive {

  private ControlMode controlMode;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Drive.class.getName());

  private static final Logger TELEMETRY = RobotLogManager.telemetryLogger();

  private static final DecimalFormat df = new DecimalFormat("####0.00");

  // Single instance of this class
  private static Drive instance = null;

  private final TalonSpeedControllerGroup left;
  private final TalonSpeedControllerGroup right;

  private RobotData data = RobotData.getInstance();
  
  double carrotLength;

  // Private constructor

  /**
   * Gets the single instance of this class.
   *
   * @return The single instance.
   */
  public static Drive getInstance() {
    if (instance == null) {
      TalonSpeedControllerGroup left;
      TalonSpeedControllerGroup right;

      LOGGER.info("Number of Motors: {}", RobotMap.DRIVEMOTOR_NUM);
      if (RobotMap.HAS_WHEELS && RobotMap.DRIVEMOTOR_NUM > 0) {
        LOGGER.info("Creating  Lead Motors");

        final WpiTalonSrxInterface leftLead = TalonProxy.create(RobotMap.LEFT_LEAD_CHANNEL);
        final WpiTalonSrxInterface rightLead = TalonProxy.create(RobotMap.RIGHT_LEAD_CHANNEL);
        WpiTalonSrxInterface leftFollower1 = null;
        WpiTalonSrxInterface rightFollower1 = null;
        WpiTalonSrxInterface leftFollower2 = null;
        WpiTalonSrxInterface rightFollower2 = null;

        if (RobotMap.DRIVEMOTOR_NUM > 2) {
          LOGGER.info("Creating first set of follower motors");
          leftFollower1 = TalonProxy.create(RobotMap.LEFT_FOLLOWER_1_CHANNEL);
          rightFollower1 = TalonProxy.create(RobotMap.RIGHT_FOLLOWER_1_CHANNEL);
        }

        if (RobotMap.DRIVEMOTOR_NUM > 4) {
          LOGGER.info("Creating second set of follower motors");
          leftFollower2 = TalonProxy.create(RobotMap.LEFT_FOLLOWER_2_CHANNEL);
          rightFollower2 = TalonProxy.create(RobotMap.RIGHT_FOLLOWER_2_CHANNEL);
        }

        left = new TalonSpeedControllerGroup("Left Drive", ControlMode.PercentOutput,
            RobotMap.LEFT_DRIVE_SENSOR_IS_INVERTED, RobotMap.LEFT_DRIVE_MOTOR_IS_INVERTED, 
            leftLead, leftFollower1, leftFollower2);
        right = new TalonSpeedControllerGroup("Right Drive", ControlMode.PercentOutput,
            RobotMap.RIGHT_DRIVE_SENSOR_IS_INVERTED, RobotMap.RIGHT_DRIVE_MOTOR_IS_INVERTED, 
            rightLead, rightFollower1, rightFollower2);
      } else {
        left = new TalonSpeedControllerGroup();
        right = new TalonSpeedControllerGroup();
      }
      instance = new Drive(left, right);
      instance.zero();

    }
    return instance;
  }

 
  private Drive(TalonSpeedControllerGroup left, TalonSpeedControllerGroup right) {
    super(left, right);
    this.left = left;
    this.right = right;
    
    carrotLength = RobotMap.MAX_CARROT_LENGTH;
    
    setPidsFromRobotMap();
  }

  public void readPidsFromSmartDashboard(int pidSlot) {

    double coefficientPLeft 
        = Double.parseDouble(SmartDashboard.getString("DB/String 1", "1.6")); // 1.6
    double coefficientPRight 
        = Double.parseDouble(SmartDashboard.getString("DB/String 6", "1.4")); // 1.4

    double coefficientIRight
        = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0")); // 0.0
    double coefficientILeft
        = Double.parseDouble(SmartDashboard.getString("DB/String 7", "0.0")); // 0.0

    double coefficientDLeft 
        = Double.parseDouble(SmartDashboard.getString("DB/String 3", "198")); //198
    double coefficientDRight 
        = Double.parseDouble(SmartDashboard.getString("DB/String 8", "165")); //165

    double coefficientFLeft 
        = Double.parseDouble(SmartDashboard.getString("DB/String 4", "1.1168")); // 0.0
    double coefficientFRight 
        = Double.parseDouble(SmartDashboard.getString("DB/String 9", "1.2208")); // 0.0

    left.pidf(pidSlot, 
        coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft);
    right.pidf(pidSlot, 
        coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight);
  }

  public void setPidsFromRobotMap() {
    // Set drive PIDs
    double coefficientFRight = RobotMap.RIGHT_DRIVE_PID_F;
    double coefficientFLeft = RobotMap.LEFT_DRIVE_PID_F;

    double coefficientPRight = RobotMap.RIGHT_DRIVE_PID_P;
    double coefficientPLeft = RobotMap.LEFT_DRIVE_PID_P;

    double coefficientIRight = RobotMap.RIGHT_DRIVE_PID_I;
    double coefficientILeft = RobotMap.LEFT_DRIVE_PID_I;

    double coefficientDRight = RobotMap.RIGHT_DRIVE_PID_D;
    double coefficientDLeft = RobotMap.LEFT_DRIVE_PID_D;

    left.pidf(RobotMap.PID_SLOT_DRIVE, 
        coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft);
    right.pidf(RobotMap.PID_SLOT_DRIVE, 
        coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight);

    // Set turn PIDs
    coefficientFRight = RobotMap.RIGHT_TURN_PID_F;
    coefficientFLeft = RobotMap.LEFT_TURN_PID_F;

    coefficientPRight = RobotMap.RIGHT_TURN_PID_P;
    coefficientPLeft = RobotMap.LEFT_TURN_PID_P;

    coefficientIRight = RobotMap.RIGHT_TURN_PID_I;
    coefficientILeft = RobotMap.LEFT_TURN_PID_I;

    coefficientDRight = RobotMap.RIGHT_TURN_PID_D;
    coefficientDLeft = RobotMap.LEFT_TURN_PID_D;

    left.pidf(RobotMap.PID_SLOT_TURN, 
        coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft);
    right.pidf(RobotMap.PID_SLOT_TURN, 
        coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight);
  }

  public void configPeakOutput(double percentOut) {
    left.configPeakOutput(percentOut);
    right.configPeakOutput(percentOut);
  }

  public void logClosedLoopErrors() {
    left.logClosedLoopErrors();
    right.logClosedLoopErrors();
  }

  public ControlMode getControlMode() {
    return controlMode;
  }

  @Override
  public void zero() {
    LOGGER.debug("Zeroed the motor sensors.");
    left.zero();
    right.zero();
    data.zero();
  }

  public void sendData() {
    RobotData.getInstance().updateDrivePosition(getLeftDistance(), getRightDistance());
  }

  /**
   * Does not drive drive motors and keeps steering angle at previous position.
   */
  public void stop() {
    right.stopMotor();
    left.stopMotor();
  }

  @Override
  public boolean isStopped() {
    return left.isStopped() && right.isStopped();
  }
  
  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneForward(double distanceInFeet, int pidSlot) {
    tuneMove(distanceInFeet, distanceInFeet, pidSlot);
  }
  
  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneTurn(double rotationInDegrees, int pidSlot) {
    double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
    tuneMove(turnDistanceInFeet, -turnDistanceInFeet, pidSlot);
  }
  
  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneMove(double leftDistance, double rightDistance, int pidSlot) {
    left.selectPidSlot(pidSlot);
    right.selectPidSlot(pidSlot);
    LOGGER.debug("Target: L: {} R: {} Current L: {} R: {}", 
        df.format(leftDistance), df.format(rightDistance), 
        df.format(getLeftDistance()), df.format(getRightDistance()));
    left.set(ControlMode.Position, leftDistance);
    // The right motor is reversed
    right.set(ControlMode.Position, rightDistance);
    data.updateDrivePosition(getLeftDistance(), getRightDistance());
  }

  @Override
  public void moveLinearFeet(double distanceInFeet) {
    left.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    right.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    moveFeet(distanceInFeet, distanceInFeet);
  }

  /**
   * This is used for testing the new controllers. It cannot use both the straight
   * PIDs and the turn PIDs, so the straight PIDs are used.
   * 
   * @param distanceInFeet the distance to move forward
   * @param degrees the turn distance in degrees, with counter clockwise hand turns as positive
   */
  public void moveWithTurn(double distanceInFeet, double degrees) {
    left.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    right.selectPidSlot(RobotMap.PID_SLOT_DRIVE);

    LOGGER.trace("Automated move of {} with {} degree turn.", 
        df.format(distanceInFeet), df.format(degrees));
    
    double turnDistanceInFeet = degreesToFeet(degrees);
    // Temp change to tune move to test motor control.
    // moveFeet((distanceInFeet - turnDistanceInFeet), (distanceInFeet + turnDistanceInFeet));
    tuneMove((distanceInFeet - turnDistanceInFeet), (distanceInFeet + turnDistanceInFeet), 
        RobotMap.PID_SLOT_DRIVE);
  }
  
  /**
   * 
   * @param rotationInDegrees
   *            enter positive degrees for left turn and enter negative degrees
   *            for right turn.
   */
  
  public void rotateByAngle(double rotationInDegrees) {
    left.selectPidSlot(RobotMap.PID_SLOT_TURN);
    right.selectPidSlot(RobotMap.PID_SLOT_TURN);

    LOGGER.debug("Automated move of {} degree turn.", df.format(rotationInDegrees));
    
    double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
    moveFeet(turnDistanceInFeet, -turnDistanceInFeet);
  }

  /**
   * Convert angle in degrees to wheel distance in feet (arc length).
   */
  public static double degreesToFeet(double degrees) {

    // Convert the turn to a distance based on the circumference of the robot wheel base.
    double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
    double angleInRadians = Math.toRadians(degrees);
    double distanceInFeet = radius * angleInRadians; // This is the distance we want to turn.
    
    return distanceInFeet;
  }

  public void moveFeet(double targetLeftDistance , double targetRightDistance) {

    LOGGER.debug("Automated move of right: {} left: {} feet ", 
        df.format(targetRightDistance), df.format(targetLeftDistance));

    // Convert the turn to a distance based on the circumference of the robot wheel base.
    // Store the sign so that all math works the same forward and backward using absolute values,
    // with direction corrected at the end.
    double leftSign = Math.signum(targetLeftDistance);
    double rightSign = Math.signum(targetRightDistance);

    // Get the current positions to determine if the request is above the max individual request
    double currentLeftPosition = getLeftDistance();
    double currentRightPosition = getRightDistance();
    LOGGER.debug("Current Position - Right: {} feet, Left: {} feet", 
        df.format(currentRightPosition), df.format(currentLeftPosition));

    // Get the average to correct for drift and move it back to straight
    // Use absolute values so that direction is ignored.
    double average = 0.5 * (Math.abs(currentRightPosition) + Math.abs(currentLeftPosition));

    // Use the minimum to go either the max allowed distance or to the target
    
    double moveLeftDistance
        = leftSign * Math.min(Math.abs(targetLeftDistance), (carrotLength + average));
    double moveRightDistance
        = rightSign * Math.min(Math.abs(targetRightDistance), (carrotLength + average));
    LOGGER.debug("Target distance in Feet - Right: {} feet, Left: {} feet", 
        df.format(moveRightDistance), df.format(moveLeftDistance));

    left.set(ControlMode.Position, moveLeftDistance);
    // The right motor is reversed
    right.set(ControlMode.Position, moveRightDistance);

    data.updateDrivePosition(getLeftDistance(), getRightDistance());
  }
  
  public double getLeftDistance() {
    double leftLeadSensorPos = left.position();
    return leftLeadSensorPos;
  }

  public double getRightDistance() {
    double rightLeadSensorPos = right.position();
    return rightLeadSensorPos;
  }

  /**
   * Gets the distance moved for checking drive modes.
   *
   * @return the absolute distance moved in feet
   */
  public double absoluteDistanceMoved() {
    double leftLeadSensorPos = Math.abs(getLeftDistance());
    double rightLeadSensorPos = Math.abs(getRightDistance());
    double lowestAbsDist = Math.min(leftLeadSensorPos, rightLeadSensorPos);
    LOGGER.debug("The absolute distance moved: {}", df.format(lowestAbsDist));
    return lowestAbsDist;
  }

  // This section overrides the standard Differential Drive 
  // class functions to capture the move state

  public void arcadeDrive(double speed, double rotation) {
    this.arcadeDrive(speed, rotation, true);
  }

  public void arcadeDrive(double speed, double rotation, boolean squaredInputs) {
    super.arcadeDrive(speed, rotation, squaredInputs);
    data.updateDrivePosition(getLeftDistance(), getRightDistance());
  }

  public void tankDrive(double leftSpeed, double rightSpeed) {
    this.tankDrive(leftSpeed, rightSpeed, true);
  }

  public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squaredInputs);
    data.updateDrivePosition(getLeftDistance(), getRightDistance());
  }

  public void curvatureDrive(double speed, double rotation, boolean isQuickTurn) {
    super.curvatureDrive(speed, rotation, isQuickTurn);
    data.updateDrivePosition(getLeftDistance(), getRightDistance());
  }

}