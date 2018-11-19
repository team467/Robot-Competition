package frc.robot.simulator;

import frc.robot.RobotMap;
import frc.robot.drive.AutoDrive;
import frc.robot.drive.motorcontrol.pathtracking.FieldPosition;
import frc.robot.simulator.communications.RobotData;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simulates the motors driving. Will be replaced by a simulated motor eventually.
 */
public class DriveSimulator implements AutoDrive {

  public static final double MAX_RPM = 821;

  // Period is 20 ms
  public static final double TIME_SLICE_IN_MS = 20.0;

  private static DriveSimulator instance = null;

  RobotData data = RobotData.getInstance();
  FieldPosition fieldState = FieldPosition.getInstance();

  private static Logger LOGGER = LogManager.getLogger(DriveSimulator.class);

  private DecimalFormat df = new DecimalFormat("####0.00");

  private double maxFeetPerPeriod; 

  private double rightPositionReading;
  private double leftPositionReading;

  private double currentVelocity;
  private double maxAccellerationPerPeriod;

  private boolean isMoving = false;

  private double controlDeadband;

  private DriveSimulator() {
    maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * MAX_RPM / 60
        * (TIME_SLICE_IN_MS / 1000);
    currentVelocity = 0;
    // Assume that it takes 2 seconds to reach max speed
    maxAccellerationPerPeriod = (TIME_SLICE_IN_MS / 1000) / 2.0;
    controlDeadband = 0.01;
    zero();
  }

  /**
   * Returns the singleton of DriveSimulator.
   * 
   * @return the drive simulator
   */
  public static DriveSimulator getInstance() {
    if (instance == null) {
      instance = new DriveSimulator();
    }
    return instance;
  }

  @Override
  public void zero() {
    rightPositionReading = 0;
    leftPositionReading = 0;
    isMoving = false;
    data.zero();
    fieldState.zeroSensors();
  }

  public double rightPosition() {
    return rightPositionReading;//absoluteRightPositionReadingOffset + rightPositionReading;
  }

  public double leftPosition() {
    return leftPositionReading;//absoluteLeftPositionReadingOffset + leftPositionReading;
  }

  /**
   * Sets the maximum speed when simulating magic motion.
   * 
   * @param percentOfMaxSpeed the percentage of the overall robot maximum speed
   */
  public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
    if (percentOfMaxSpeed < 0) {
      percentOfMaxSpeed = 0;
    } else if (percentOfMaxSpeed > 1) {
      percentOfMaxSpeed = 1;
    }
    maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
  }

  /**
   * This is used for testing the new controllers. It cannot use both the straight
   * PIDs and the turn PIDs, so the straight PIDs are used.
   * 
   * @param distanceInFeet the distance to move forward
   * @param degrees the turn distance in degrees, with counter clockwise hand turns as positive
   */
  @Override
  public void moveWithTurn(double distanceInFeet, double degrees) {

    LOGGER.trace("Simulated automated move of {} with {} degree turn.", 
        df.format(distanceInFeet), df.format(degrees));
    System.out.println("Simulated automated move of " 
        + df.format(distanceInFeet) + " with " + df.format(degrees) + " degree turn.");
    
    double turnDistanceInFeet = degreesToFeet(degrees);
    moveFeet((distanceInFeet - turnDistanceInFeet), (distanceInFeet + turnDistanceInFeet));
  }

  @Override
  public void moveLinearFeet(double distance) {
    moveFeet(distance, distance);
  }

  @Override
  public void moveFeet(double leftDistance, double rightDistance) {

    if (leftPositionReading == leftDistance && rightPositionReading == rightDistance) {
      isMoving = false;
      return; // At destination
    }

    isMoving = true;

    if (Math.abs((leftDistance - leftPositionReading)) > maxFeetPerPeriod) {
      if (leftDistance < 0) {
        leftPositionReading -= maxFeetPerPeriod;
      } else {
        leftPositionReading += maxFeetPerPeriod;
      }
    } else {
      leftPositionReading = leftDistance;
    }

    if (Math.abs((rightDistance - rightPositionReading)) > maxFeetPerPeriod) {
      if (rightDistance < 0) {
        rightPositionReading -= maxFeetPerPeriod;
      } else {
        rightPositionReading += maxFeetPerPeriod;
      }
    } else {
      rightPositionReading = rightDistance;
    }

    // Round to the Ticks per revolution
    leftPositionReading = (double) Math.round(leftPositionReading 
        * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) 
        / (double) RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
    rightPositionReading = (double) Math.round(rightPositionReading 
        * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) 
        / (double) RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;

    LOGGER.debug("Left Target: {} Right Target: {}", 
        df.format(leftDistance), df.format(rightDistance));
    LOGGER.debug("Left Move: {}", df.format(leftPositionReading) 
        + " Right Move: {}", df.format(rightPositionReading));

    System.out.println("Left Target: " + df.format(leftDistance) 
        + " Right Target: " + df.format(rightDistance));
    System.out.println("Left Move: " + df.format(leftPositionReading) 
        + " Right Move: " +  df.format(rightPositionReading));

    data.updateDrivePosition(rightPosition(), leftPosition());
    fieldState.update(leftPositionReading, rightPositionReading);

  }

  @Override
  public boolean isStopped() {
    return !isMoving;
  }

  @Override
  public double absoluteDistanceMoved() {
    double absoluteLeftDistance =  Math.abs(leftPositionReading);
    double absoluteRightDistance = Math.abs(rightPositionReading);
    if (absoluteLeftDistance < absoluteRightDistance) {
      return absoluteRightDistance;
    } else {
      return absoluteLeftDistance;
    }
  }

  @Override
  public void rotateByAngle(double rotation) {
    double turnDistanceInFeet = degreesToFeet(rotation);
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


  /**
   * Makes sure the requested speed is less than the max allowed speed.
   * 
   * @param requestedForwardSpeed the speed requested by the control input
   */
  private double limit(double requestedForwardSpeed) {
    return requestedForwardSpeed;
  }

  /**
   * Checks to see if the control input is within the deadband to all for small
   * offsets from neutral on the control system.
   * 
   * @param requestedForwardSpeed the speed requested by the control input
   * @return the modified request
   */
  private double applyDeadband(double requestedForwardSpeed, double controlDeadband) {
    if (requestedForwardSpeed < controlDeadband) {
      return 0.0;
    } else {
      return requestedForwardSpeed;
    }
  }

  /**
   * Arcade drive method for differential drive platform.
   *
   * @param speed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
   * @param rotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
   *                      positive.
   */
  public void arcadeDrive(double speed, double rotation) {
    this.arcadeDrive(speed, rotation, true);
  }

  /**
   * Arcade drive method for differential drive platform.
   *
   * @param speed         The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
   * @param rotation      The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
   *                      positive.
   * @param squareInputs  If set, decreases the input sensitivity at low speeds.
   */
  public void arcadeDrive(double speed, double rotation, boolean squaredInputs) {
  
    speed = limit(speed);
    speed = applyDeadband(speed, controlDeadband);

    double direction = Math.signum(speed);
    if (squaredInputs) {
      speed = Math.copySign(speed * speed, speed);
      rotation = Math.copySign(rotation * rotation, rotation);
    }

    double leftMotorOutput;
    double rightMotorOutput;

    double maxInput = Math.copySign(Math.max(Math.abs(speed), Math.abs(rotation)), speed);

    if (speed >= 0.0) {
      // First quadrant, else second quadrant
      if (rotation >= 0.0) {
        leftMotorOutput = maxInput;
        rightMotorOutput = speed - rotation;
      } else {
        leftMotorOutput = speed + rotation;
        rightMotorOutput = maxInput;
      }
    } else {
      // Third quadrant, else fourth quadrant
      if (rotation >= 0.0) {
        leftMotorOutput = speed + rotation;
        rightMotorOutput = maxInput;
      } else {
        leftMotorOutput = maxInput;
        rightMotorOutput = speed - rotation;
      }
    }

    // m_leftMotor.set(limit(leftMotorOutput) * m_maxOutput);
    // m_rightMotor.set(limit(rightMotorOutput) * m_maxOutput * m_rightSideInvertMultiplier);

    double requestedVelocity = speed * maxFeetPerPeriod;
    if (Math.abs(requestedVelocity - currentVelocity) > maxAccellerationPerPeriod) {
      currentVelocity += maxAccellerationPerPeriod * direction;
    } else {
      currentVelocity = requestedVelocity;
    }
    this.fieldState.update(this.leftPosition(), this.rightPosition());
  }

}
