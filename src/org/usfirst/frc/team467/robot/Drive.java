package org.usfirst.frc.team467.robot;

import java.text.DecimalFormat;


import org.usfirst.frc.team467.robot.Elevator.Stops;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.usfirst.frc.team467.robot.Autonomous.AutoDrive;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends DifferentialDrive implements AutoDrive {
	private ControlMode controlMode;

    private static final Logger LOGGER = LogManager.getLogger(Drive.class);
    private static final Logger TELEMETRY = LogManager.getLogger("telemetry");
	private DecimalFormat df = new DecimalFormat("####0.00");

	// Single instance of this class
	private static Drive instance = null;

	private final TalonSpeedControllerGroup left;
	private final TalonSpeedControllerGroup right;
	
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
		
		carrotLength = RobotMap.MAX_CARROT_LENGTH;
		
		setPIDSFromRobotMap();
	}

	public void readPIDSFromSmartDashboard(int pidSlot) {
		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 1", "1.6")); // 1.6
		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 6", "1.4")); // 1.4

		double kIRight = 0.0;
		double kILeft = 0.0;

		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "198")); //198
		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "165")); //165

		double kFLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "1.1168")); // 0.0
		double kFRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "1.2208")); // 0.0

		left.setPIDF(pidSlot, kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(pidSlot, kPRight, kIRight, kDRight, kFRight);
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

	public void logTelemetry(double speed, double turn) {
	    // Log the speed and turn inputs, as well as the speed and position of each side.
	    // For the speed we need to convert from ticks to feet and from per 100ms to per seconds.
	    // For position we need to convert from ticks to feet.
	    TELEMETRY.info(String.format("%f,%f,%f,%f,%f,%f",
	            speed, turn,
	            ticksToFeet(10*left.getSensorVelocity()), ticksToFeet(left.getSensorPosition()),
	            ticksToFeet(10*right.getSensorVelocity()), ticksToFeet(right.getSensorPosition())));
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
	
	/**
	 * Used for tuning PIDs only, does not use carrot drive or left right balancing 
	 */
	public void tuneForward(double distanceInFeet, int pidSlot) {
		tuneMove(distanceInFeet, distanceInFeet, pidSlot);
	}
	
	/**
	 * Used for tuning PIDs only, does not use carrot drive or left right balancing 
	 */
	public void tuneTurn(double rotationInDegrees, int pidSlot) {
		double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
		tuneMove(turnDistanceInFeet, -turnDistanceInFeet, pidSlot);
	}
	
	/**
	 * Used for tuning PIDs only, does not use carrot drive or left right balancing 
	 */
	public void tuneMove(double leftDistance, double rightDistance, int pidSlot) {
		left.setPIDSlot(pidSlot);
		right.setPIDSlot(pidSlot);
		LOGGER.info("Target: L: {} R: {} Current L: {} R: {}", leftDistance, rightDistance, getLeftDistance(), getRightDistance());
		left.set(ControlMode.Position, feetToTicks(leftDistance));
		// The right motor is reversed
		right.set(ControlMode.Position, -feetToTicks(rightDistance));
	}

	@Override
	public void moveLinearFeet(double distanceInFeet) {
		left.setPIDSlot(RobotMap.PID_SLOT_DRIVE);
		right.setPIDSlot(RobotMap.PID_SLOT_DRIVE);
		moveFeet(distanceInFeet, distanceInFeet);
	}
	
	/**
	 * 
	 * @param rotationInDegrees
	 *            enter positive degrees for left turn and enter negative degrees
	 *            for right turn
	 */
	
	public void rotateByAngle(double rotationInDegrees) {
		left.setPIDSlot(RobotMap.PID_SLOT_TURN);
		right.setPIDSlot(RobotMap.PID_SLOT_TURN);

		LOGGER.trace("Automated move of {} degree turn.", rotationInDegrees);
		
		double turnDistanceInFeet = degreesToFeet(rotationInDegrees);
		moveFeet(turnDistanceInFeet, - turnDistanceInFeet);
//		tuneMove(turnDistanceInFeet, - turnDistanceInFeet, RobotMap.PID_SLOT_TURN);
	}

	/**
	 * Convert angle in degrees to wheel distance in feet (arc length).
	 */
	public static double degreesToFeet(double degrees) {
		double turnConstant = 12.0;

		// Add a constant
		if (degrees < 0) {
			degrees -= turnConstant;
		} else if (degrees > 0) {
			degrees += turnConstant;
		}

		// Convert the turn to a distance based on the circumference of the robot wheel base.
		double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
		double angleInRadians = Math.toRadians(degrees);
		double distanceInFeet = radius * angleInRadians; // This is the distance we want to turn.
		
		return distanceInFeet;
	}

	public void moveFeet(double targetLeftDistance , double targetRightDistance) {

		LOGGER.trace("Automated move of right: {} left: {} feet ", targetRightDistance, targetLeftDistance);

		// Convert the turn to a distance based on the circumference of the robot wheel base.
		// Store the sign so that all math works the same forward and backward using absolute values,
		// with direction corrected at the end.
		double leftSign = Math.signum(targetLeftDistance);
		double rightSign = Math.signum(targetRightDistance);

		// Get the current positions to determine if the request is above the max individual request
		double currentLeftPosition = getLeftDistance();
		double currentRightPosition = getRightDistance();
		LOGGER.trace("Current Position - Right: {} Left: {}", df.format(currentRightPosition), df.format(currentLeftPosition));

		// Get the average to correct for drift and move it back to straight
		// Use absolute values so that direction is ignored.
		double average = 0.5 * (Math.abs(currentRightPosition) + Math.abs(currentLeftPosition));

		// Use the minimum to go either the max allowed distance or to the target
		
		double moveLeftDistance = leftSign * Math.min(Math.abs(targetLeftDistance), (carrotLength + average));
		double moveRightDistance = rightSign * Math.min(Math.abs(targetRightDistance), (carrotLength + average));
		LOGGER.trace("Distance in Feet - Right: {} Left: {}", df.format(moveRightDistance), df.format(moveLeftDistance));


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
		LOGGER.debug("The absolute distance moved: {}", lowestAbsDist);
		return lowestAbsDist;
	}

	private double feetToTicks(double feet) {
		double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12.0)) * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
		LOGGER.trace("Feet = {} ticks = {}", feet, ticks);
		//what do i do here
		return ticks;
	}

	private double ticksToFeet(double ticks) {
		double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
		LOGGER.trace("Ticks = {} feet = {}",ticks, feet);
		return feet;
	}

	/**
	 * Sets the ramp time based on the elevator height in sensor ticks if driving straight or about to drive straight,
	 * or sets the ramp time to the minimum if turning in place or stopped.
	 * 
	 * @param elevatorHeight
	 */
	public void setRamp(int elevatorHeight) {
		double ramp;
		if (Math.abs(left.sensorSpeed() - right.sensorSpeed()) > (RobotMap.TURN_IN_PLACE_DETECT_TOLERANCE) ||
				Math.abs(DriverStation467.getInstance().getArcadeSpeed()) >= RobotMap.MIN_DRIVE_SPEED) { // If driving straight or told to drive straight
			double heightPercent = (double) (RobotMap.ELEVATOR_BOTTOM_TICKS - elevatorHeight) / (RobotMap.ELEVATOR_BOTTOM_TICKS - RobotMap.ELEVATOR_TOP_TICKS);
			ramp = MathUtils.weightedAverage(RobotMap.ELEVATOR_LOW_DRIVE_RAMP_TIME, RobotMap.ELEVATOR_HIGH_DRIVE_RAMP_TIME, heightPercent);
		} else { // Stopped or turning in place
			ramp = RobotMap.ELEVATOR_LOW_DRIVE_RAMP_TIME;
		}

		ramp = 0.0;
		left.setOpenLoopRamp(ramp);
		right.setOpenLoopRamp(ramp);
		LOGGER.trace("Ramp time: {}", ramp);
	}
	
	public void setClimberSpeed(double speed) {
		left.set(speed);
		right.set(speed);
//		if (Math.abs(DriverStation467.getInstance().getClimberSpeed()) >= RobotMap.CLIMB_MIN_DRIVE_SPEED) {
//			
//		} else {
//			return;
//		}
	}
}