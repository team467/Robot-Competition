package org.usfirst.frc.team467.robot;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Autonomous.AutoDrive;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends DifferentialDrive implements AutoDrive {
	private ControlMode controlMode;

	private static final Logger LOGGER = Logger.getLogger(Drive.class);
	private DecimalFormat df = new DecimalFormat("####0.00");

	// Single instance of this class
	private static Drive instance = null;

	private final TalonSpeedControllerGroup left;
	private final TalonSpeedControllerGroup right;

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

			LOGGER.info("Number of Motors:" + RobotMap.DRIVEMOTOR_NUM);
			if (RobotMap.HAS_WHEELS && RobotMap.DRIVEMOTOR_NUM > 0) {
				LOGGER.info("Creating  Lead Motors");
				WPI_TalonSRX leftLead = new WPI_TalonSRX(RobotMap.LEFT_LEAD_CHANNEL);
				WPI_TalonSRX rightLead = new WPI_TalonSRX(RobotMap.RIGHT_LEAD_CHANNEL);
				WPI_TalonSRX leftFollower1 = null;
				WPI_TalonSRX rightFollower1 = null;
				WPI_TalonSRX leftFollower2 = null;
				WPI_TalonSRX rightFollower2= null;

				if (RobotMap.DRIVEMOTOR_NUM > 2) {
					LOGGER.info("Creating  first set of follower motors");
					leftFollower1 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_1_CHANNEL);
					rightFollower1 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_1_CHANNEL);
				}

				if (RobotMap.DRIVEMOTOR_NUM > 4) {
					LOGGER.info("Creating second set of follower motors");
					leftFollower2 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_2_CHANNEL);
					rightFollower2 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_2_CHANNEL);
				}

				left = new TalonSpeedControllerGroup(ControlMode.PercentOutput,
						RobotMap.LEFT_DRIVE_SENSOR_IS_INVERTED, leftLead, leftFollower1, leftFollower2);
				right = new TalonSpeedControllerGroup(ControlMode.PercentOutput,
						RobotMap.RIGHT_DRIVE_SENSOR_IS_INVERTED, rightLead, rightFollower1, rightFollower2);
			} else {
				left = new TalonSpeedControllerGroup();
				right = new TalonSpeedControllerGroup();
			}
			instance = new Drive(left, right);

		}
		return instance;
	}

	private Drive(TalonSpeedControllerGroup left, TalonSpeedControllerGroup right) {
		super(left, right);
		this.left = left;
		this.right = right;

		setPIDSFromRobotMap();
	}

	public void readPIDSFromSmartDashboard() {
		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 1", "1.6")); // 1.6
		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 6", "1.4")); // 1.4

		double kIRight = 0.0;
		double kILeft = 0.0;

		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "198")); //198
		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "165")); //165

		double kFLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "1.1168")); // 0.0
		double kFRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "1.2208")); // 0.0

		left.setPIDF(RobotMap.PID_SLOT_DRIVE, kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(RobotMap.PID_SLOT_DRIVE, kPRight, kIRight, kDRight, kFRight);

		left.setPIDF(RobotMap.PID_SLOT_TURN, kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(RobotMap.PID_SLOT_TURN, kPRight, kIRight, kDRight, kFRight);
	}

	public void setPIDSFromRobotMap() {
		// Set drive PIDs
		double kFRight = RobotMap.RIGHT_DRIVE_PID_F;
		double kFLeft = RobotMap.LEFT_DRIVE_PID_F;

		double kPRight = RobotMap.RIGHT_DRIVE_PID_P;
		double kPLeft = RobotMap.LEFT_DRIVE_PID_P;

		double kIRight = RobotMap.RIGHT_DRIVE_PID_I;
		double kILeft = RobotMap.LEFT_DRIVE_PID_I;

		double kDRight = RobotMap.RIGHT_DRIVE_PID_D;
		double kDLeft = RobotMap.LEFT_DRIVE_PID_D;

		left.setPIDF(RobotMap.PID_SLOT_DRIVE, kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(RobotMap.PID_SLOT_DRIVE, kPRight, kIRight, kDRight, kFRight);

		// Set turn PIDs
		kFRight = RobotMap.RIGHT_TURN_PID_F;
		kFLeft = RobotMap.LEFT_TURN_PID_F;

		kPRight = RobotMap.RIGHT_TURN_PID_P;
		kPLeft = RobotMap.LEFT_TURN_PID_P;

		kIRight = RobotMap.RIGHT_TURN_PID_I;
		kILeft = RobotMap.LEFT_TURN_PID_I;

		kDRight = RobotMap.RIGHT_TURN_PID_D;
		kDLeft = RobotMap.LEFT_TURN_PID_D;

		left.setPIDF(RobotMap.PID_SLOT_TURN, kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(RobotMap.PID_SLOT_TURN, kPRight, kIRight, kDRight, kFRight);
	}

	public void configPeakOutput(double percentOut) {
		left.configPeakOutput(percentOut);
		right.configPeakOutput(percentOut);
	}

	public void logClosedLoopErrors() {
		left.logClosedLoopErrors("Left");
		right.logClosedLoopErrors("Right");
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

	@Override
	public void zero() {
		LOGGER.trace("Zeroed the motor sensors.");
		left.zero();
		right.zero();
	}

	public void sendData() {
		RobotData.getInstance().updateDrivePosition(getRightDistance(), getLeftDistance());
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

	@Override
	public void moveFeet(double distanceInFeet) {
		left.setPIDSlot(RobotMap.PID_SLOT_DRIVE);
		right.setPIDSlot(RobotMap.PID_SLOT_DRIVE);
		moveFeet(distanceInFeet, distanceInFeet);
	}
	
	public static final double POSITION_GAIN_FEET = 2.5;

	/**
	 * 
	 * @param rotationInDegrees
	 *            enter positive degrees for left turn and enter negative degrees
	 *            for right turn
	 */
	public void rotateByAngle(double rotationInDegrees) {
		left.setPIDSlot(RobotMap.PID_SLOT_TURN);
		right.setPIDSlot(RobotMap.PID_SLOT_TURN);

		LOGGER.trace("Automated move of " + rotationInDegrees + " degree turn.");
		
		double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
		moveFeet(turnDistanceInFeet, - turnDistanceInFeet);
	}

	/**
	 * Convert angle in degrees to wheel distance in feet (arc length).
	 */
	public static double degreesToFeet(double degrees) {
		// Adjust requested degrees because the robot predictably undershoots. Value was found empirically.
		degrees *= 1.06;

		// Convert the turn to a distance based on the circumference of the robot wheel base.
		double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
		double angleInRadians = Math.toRadians(degrees);
		double distanceInFeet = radius * angleInRadians; // This is the distance we want to turn.
		
		return distanceInFeet;
	}

	public void moveFeet(double targetLeftDistance , double targetRightDistance) {

		LOGGER.trace("Automated move of right: "+ targetRightDistance +" left: "+ targetLeftDistance + " feet ");

		// Convert the turn to a distance based on the circumference of the robot wheel base.
		// Store the sign so that all math works the same forward and backward using absolute values,
		// with direction corrected at the end.
		double leftSign = Math.signum(targetLeftDistance);
		double rightSign = Math.signum(targetRightDistance);

		// Get the current positions to determine if the request is above the max individual request
		double currentLeftPosition = getLeftDistance();
		double currentRightPosition = getRightDistance();
		LOGGER.trace("Current Position - Right: " + df.format(currentRightPosition) + " Left: "
				+ df.format(currentLeftPosition));

		// Get the average to correct for drift and move it back to straight
		// Use absolute values so that direction is ignored.
		double average = 0.5 * (Math.abs(currentRightPosition) + Math.abs(currentLeftPosition));

		// Use the minimum to go either the max allowed distance or to the target
		double moveLeftDistance = leftSign * Math.min(Math.abs(targetLeftDistance), (POSITION_GAIN_FEET + average));
		double moveRightDistance = rightSign * Math.min(Math.abs(targetRightDistance), (POSITION_GAIN_FEET + average));
		LOGGER.trace("Distance in Feet - Right: " + df.format(moveRightDistance) + " Left: "
				+ df.format(moveLeftDistance));

		// Converts turn angle in ticks to degrees, then to radians.
		double leftDistTicks = feetToTicks(moveLeftDistance);
		double rightDistTicks = feetToTicks(moveRightDistance);

		// The right motor is reversed
		left.set(ControlMode.Position, leftDistTicks);
		
		right.set(ControlMode.Position, -rightDistTicks);
	}
	
	public double getLeftDistance() {
		double leftLeadSensorPos = ticksToFeet(left.sensorPosition());
		return leftLeadSensorPos;
	}

	public double getRightDistance() {
		double rightLeadSensorPos = -ticksToFeet(right.sensorPosition());
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
		LOGGER.debug("The absolute distance moved: " + lowestAbsDist);
		return lowestAbsDist;
	}

	private double feetToTicks(double feet) {
		double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12.0)) * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
		LOGGER.trace(feet + " feet = " + ticks + " ticks.");
		return ticks;
	}

	private double ticksToFeet(double ticks) {
		double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
		LOGGER.trace(ticks + " ticks = " + feet + " feet.");
		return feet;
	}

	public void setRamp(int elevatorHeight) {
		double heightPercent = (double) (RobotMap.ELEVATOR_BOTTOM_TICKS - elevatorHeight) / (RobotMap.ELEVATOR_BOTTOM_TICKS - RobotMap.ELEVATOR_TOP_TICKS);
		double ramp = MathUtils.weightedAverage(RobotMap.ELEVATOR_LOW_DRIVE_RAMP_TIME, RobotMap.ELEVATOR_HIGH_DRIVE_RAMP_TIME, heightPercent);

		left.setOpenLoopRamp(ramp);
		right.setOpenLoopRamp(ramp);
		LOGGER.trace("Ramp time: "+ ramp);
	}
}