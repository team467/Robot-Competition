package frc.robot.drive;

import static org.apache.logging.log4j.util.Unbox.box;
//import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

public class Drive extends DifferentialDrive implements AutoDrive {

  private ControlType controlType;

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Drive.class.getName());

  // Single instance of this class
  private static Drive instance = null;

  //private final TalonSpeedControllerGroup leftTalons;
  //private final TalonSpeedControllerGroup rightTalons;
  
  //sparkMax
  private final SparkMaxSpeedControllerGroup leftSM;
  private final SparkMaxSpeedControllerGroup rightSM;


  // Private constructor

  /**
   * Gets the single instance of this class.
   *
   * @return The single instance.
   */
  public static Drive getInstance() {
    if (instance == null) {
      // TalonSpeedControllerGroup leftTalons;
      // TalonSpeedControllerGroup rightTalons;

      SparkMaxSpeedControllerGroup leftSM;
      SparkMaxSpeedControllerGroup rightSM;

      LOGGER.info("Number of Motors: {}", box(RobotMap.DRIVEMOTOR_NUM));
      if (RobotMap.HAS_WHEELS && RobotMap.DRIVEMOTOR_NUM > 0) {
        LOGGER.info("Creating  Lead Motors");

        CANSparkMax leftLead = new CANSparkMax(RobotMap.LEFT_LEAD_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax rightLead = new CANSparkMax(RobotMap.RIGHT_LEAD_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax leftFollower1 = null;
        CANSparkMax rightFollower1 = null;
        CANSparkMax leftFollower2 = null;
        CANSparkMax rightFollower2 = null;

        if (RobotMap.DRIVEMOTOR_NUM > 2) {
          LOGGER.info("Creating first set of follower motors");
          leftFollower1 = new CANSparkMax(RobotMap.LEFT_FOLLOWER_1_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
          rightFollower1 = new CANSparkMax(RobotMap.RIGHT_FOLLOWER_1_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
        }

        if (RobotMap.DRIVEMOTOR_NUM > 4) {
          LOGGER.info("Creating second set of follower motors");
          leftFollower2 = new CANSparkMax(RobotMap.LEFT_FOLLOWER_2_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
          rightFollower2 = new CANSparkMax(RobotMap.RIGHT_FOLLOWER_2_CHANNEL, CANSparkMaxLowLevel.MotorType.kBrushless);
        }

        ControlType teleopControlMode = ControlType.kVoltage;
        if (RobotMap.USE_VELOCITY_SPEED_CONTROL_FOR_TELOP) {
          teleopControlMode = ControlType.kVelocity;
        }

        leftSM = new SparkMaxSpeedControllerGroup("Left_Drive", teleopControlMode, RobotMap.LEFT_DRIVE_SENSOR_IS_INVERTED,
            RobotMap.LEFT_DRIVE_MOTOR_IS_INVERTED, leftLead, leftFollower1, leftFollower2);
        rightSM = new SparkMaxSpeedControllerGroup("Right_Drive", teleopControlMode,
            RobotMap.RIGHT_DRIVE_SENSOR_IS_INVERTED, RobotMap.RIGHT_DRIVE_MOTOR_IS_INVERTED, rightLead, rightFollower1,
            rightFollower2);
      } else {
        leftSM = new SparkMaxSpeedControllerGroup();
        rightSM = new SparkMaxSpeedControllerGroup();
      }
      instance = new Drive(leftSM, rightSM);
      instance.zero();

    }
    return instance;
  }

  private Drive(final SparkMaxSpeedControllerGroup left, final SparkMaxSpeedControllerGroup right) {
    super(left, right);
    this.leftSM = left;
    this.rightSM = right;

    setPidsFromRobotMap();
  }

  public void readPidsFromSmartDashboard(final int pidSlot) {

    final double coefficientPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 1", "1.6")); // 1.6
    final double coefficientPRight = Double.parseDouble(SmartDashboard.getString("DB/String 6", "1.4")); // 1.4

    final double coefficientIRight = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0")); // 0.0
    final double coefficientILeft = Double.parseDouble(SmartDashboard.getString("DB/String 7", "0.0")); // 0.0

    final double coefficientDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "198")); // 198
    final double coefficientDRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "165")); // 165

    final double coefficientFLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "1.1168")); // 0.0
    final double coefficientFRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "1.2208")); // 0.0

    leftSM.pidf(pidSlot, coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft,
        RobotMap.VELOCITY_MULTIPLIER_LEFT);
    rightSM.pidf(pidSlot, coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight,
        RobotMap.VELOCITY_MULTIPLIER_RIGHT);
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

    leftSM.pidf(RobotMap.PID_SLOT_DRIVE, coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft,
        RobotMap.VELOCITY_MULTIPLIER_LEFT);
    rightSM.pidf(RobotMap.PID_SLOT_DRIVE, coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight,
        RobotMap.VELOCITY_MULTIPLIER_RIGHT);

    // Set turn PIDs
    coefficientFRight = RobotMap.RIGHT_TURN_PID_F;
    coefficientFLeft = RobotMap.LEFT_TURN_PID_F;

    coefficientPRight = RobotMap.RIGHT_TURN_PID_P;
    coefficientPLeft = RobotMap.LEFT_TURN_PID_P;

    coefficientIRight = RobotMap.RIGHT_TURN_PID_I;
    coefficientILeft = RobotMap.LEFT_TURN_PID_I;

    coefficientDRight = RobotMap.RIGHT_TURN_PID_D;
    coefficientDLeft = RobotMap.LEFT_TURN_PID_D;

    leftSM.pidf(RobotMap.PID_SLOT_TURN, coefficientPLeft, coefficientILeft, coefficientDLeft, coefficientFLeft,
        RobotMap.VELOCITY_MULTIPLIER_LEFT);
    rightSM.pidf(RobotMap.PID_SLOT_TURN, coefficientPRight, coefficientIRight, coefficientDRight, coefficientFRight,
        RobotMap.VELOCITY_MULTIPLIER_RIGHT);
  }

  public void configPeakOutput(final double percentOut) {
    leftSM.configPeakOutput(percentOut);
    rightSM.configPeakOutput(percentOut);
  }

  public void logClosedLoopErrors() {
    leftSM.logClosedLoopErrors();
    rightSM.logClosedLoopErrors();
  }

  public ControlType getControlMode() {
    return controlType;
  }

  @Override
  public void zero() {
    LOGGER.debug("Zeroed the motor sensors.");
    leftSM.zero();
    rightSM.zero();
  }

  /**
   * Does not drive drive motors and keeps steering angle at previous position.
   */
  public void stop() {
    rightSM.stopMotor();
    leftSM.stopMotor();
  }

  @Override
  public boolean isStopped() {
    return leftSM.isStopped() && rightSM.isStopped();
  }

  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneForward(final double distanceInFeet, final int pidSlot) {
    tuneMove(distanceInFeet, distanceInFeet, pidSlot);
  }

  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneTurn(final double rotationInDegrees, final int pidSlot) {
    final double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
    tuneMove(turnDistanceInFeet, -turnDistanceInFeet, pidSlot);
  }

  /**
   * Used for tuning PIDs only, does not use carrot drive or left right balancing.
   */
  public void tuneMove(final double leftDistance, final double rightDistance, final int pidSlot) {
    leftSM.selectPidSlot(pidSlot); //currently pidSlot does nothing
    rightSM.selectPidSlot(pidSlot);
    LOGGER.debug("Target: L: {} R: {} Current L: {} R: {}", 
        box(leftDistance), box(rightDistance),
        box(getLeftDistance()), box(getRightDistance()));
    leftSM.set(ControlType.kPosition, feetToTicks(leftDistance));
    // The right motor is reversed
    rightSM.set(ControlType.kPosition, feetToTicks(rightDistance));
  }

  @Override
  public void moveLinearFeet(final double distanceInFeet) {
    leftSM.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    rightSM.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    moveFeet(distanceInFeet, distanceInFeet);
  }

  /**
   * This is used for testing the new controllers. It cannot use both the straight
   * PIDs and the turn PIDs, so the straight PIDs are used.
   * 
   * @param distanceInFeet the distance to move forward
   * @param degrees        the turn distance in degrees, with counter clockwise
   *                       hand turns as positive
   */
  public void moveWithTurn(final double distanceInFeet, final double degrees) {
    leftSM.selectPidSlot(RobotMap.PID_SLOT_DRIVE);
    rightSM.selectPidSlot(RobotMap.PID_SLOT_DRIVE);

    LOGGER.trace("Automated move of {} with {} degree turn.", box(distanceInFeet), box(degrees));

    final double turnDistanceInFeet = degreesToFeet(degrees);
    // Temp change to tune move to test motor control.
    // moveFeet((distanceInFeet - turnDistanceInFeet), (distanceInFeet +
    // turnDistanceInFeet));
    tuneMove((distanceInFeet - turnDistanceInFeet), (distanceInFeet + turnDistanceInFeet), RobotMap.PID_SLOT_DRIVE);
  }

  /**
   * 
   * @param rotationInDegrees enter positive degrees for left turn and enter
   *                          negative degrees for right turn.
   */

  public void rotateByAngle(final double rotationInDegrees) {
    leftSM.selectPidSlot(RobotMap.PID_SLOT_TURN);
    rightSM.selectPidSlot(RobotMap.PID_SLOT_TURN);

    LOGGER.debug("Automated move of {} degree turn.", box(rotationInDegrees));

    final double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
    moveFeet(turnDistanceInFeet, -turnDistanceInFeet);
  }

  /**
   * Convert angle in degrees to wheel distance in feet (arc length).
   */
  public static double degreesToFeet(final double degrees) {

    // Convert the turn to a distance based on the circumference of the robot wheel
    // base.
    final double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
    final double angleInRadians = Math.toRadians(degrees);
    final double distanceInFeet = radius * angleInRadians; // This is the distance we want to turn.

    return distanceInFeet;
  }

  public void moveFeet(final double targetLeftDistance, final double targetRightDistance) {

    LOGGER.debug("Automated move of right: {} left: {} feet ", box(targetRightDistance), box(targetLeftDistance));

    leftSM.set(ControlType.kPosition, targetLeftDistance);
    // The right motor is reversed
    rightSM.set(ControlType.kPosition, targetRightDistance);
  }

  public void moveVelMode(final double leftOut, final double rightOut) {

    // LOGGER.debug("Automated move of right: {} left: {} feet ",
    // df.format(targetRightDistance), df.format(targetLeftDistance));

    leftSM.set(ControlType.kVoltage, leftOut);
    // The right motor is reversed
    rightSM.set(ControlType.kVoltage, rightOut);
  }

  public double getLeftDistance() {
    final double leftLeadSensorPos = leftSM.position();
    return leftLeadSensorPos;
  }

  public double getRightDistance() {
    final double rightLeadSensorPos = rightSM.position();
    return rightLeadSensorPos;
  }

  /**
   * Gets the distance moved for checking drive modes.
   *
   * @return the absolute distance moved in feet
   */
  public double absoluteDistanceMoved() {
    final double leftLeadSensorPos = Math.abs(getLeftDistance());
    final double rightLeadSensorPos = Math.abs(getRightDistance());
    final double lowestAbsDist = Math.min(leftLeadSensorPos, rightLeadSensorPos);
    LOGGER.debug("The absolute distance moved: {}", box(lowestAbsDist));
    return lowestAbsDist;
  }

  private double feetToTicks(final double feet) {
    final double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12.0)) * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    LOGGER.trace("Feet = {} ticks = {}", box(feet), box(ticks));
    // what do i do here
    return ticks;
  }

  private double TicksToFeet(final double ticks){
    final double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
    LOGGER.trace("Ticks = {} feet = {}", box(ticks), box(feet));
    return feet;
  }

  // This section overrides the standard Differential Drive
  public void arcadeDrive(final double speed, final double rotation) {
    this.arcadeDrive(speed, rotation, true);
  }

  public void arcadeDrive(final double speed, final double rotation, final boolean squaredInputs) {
    super.arcadeDrive(speed, rotation, squaredInputs);
    LOGGER.debug("Expected Output: {}", box(speed));
  }

  public void tankDrive(final double leftSpeed, final double rightSpeed) {
    this.tankDrive(leftSpeed, rightSpeed, true);
  }

  public void tankDrive(final double leftSpeed, final double rightSpeed, final boolean squaredInputs) {
    LOGGER.debug("expected left: {}, expected right: {} ", box(leftSpeed), box(rightSpeed));
    super.tankDrive(leftSpeed, rightSpeed, squaredInputs);
  }

  public void curvatureDrive(final double speed, final double rotation, final boolean isQuickTurn) {
    super.curvatureDrive(speed, rotation, isQuickTurn);
  }

}