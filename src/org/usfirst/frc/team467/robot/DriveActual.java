/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveActual extends DifferentialDrive implements Drive {
	private ControlMode controlMode;
	// TODO: DEfine logger (Done)
	private static final Logger LOGGER = Logger.getLogger(DriveActual.class);

	// Single instance of this class
	private static DriveActual instance = null;

	// Data storage object
	
	private WPI_TalonSRX leftLead;
	private WPI_TalonSRX leftFollower1;
	private WPI_TalonSRX leftFollower2;
	
	
	private WPI_TalonSRX rightLead;
	private WPI_TalonSRX rightFollower1;
	private WPI_TalonSRX rightFollower2;

	// Private constructor
	private DriveActual(WPI_TalonSRX leftLead,  WPI_TalonSRX leftFollower1,  WPI_TalonSRX leftFollower2,
		          WPI_TalonSRX rightLead, WPI_TalonSRX rightFollower1, WPI_TalonSRX rightFollower2) {
		super(leftLead, rightLead);
		
		// Need to specify the motor channels.
		// TODO: Define and initialize motors. (Done)
		
		this.leftLead = leftLead;
		initMotor(this.leftLead);
		leftLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSensorPhase(true);
		leftLead.config_kF(0, 1023.0 / 1402.0, RobotMap.TALON_TIMEOUT); // (100 percent of the output you can send to the motor) divided by (max speed measured in ticks)
		
		

		this.leftFollower1 = leftFollower1;
		initMotor(this.leftFollower1);
		initMotorForFollowerMode(leftLead, leftFollower1);

		this.leftFollower2 = leftFollower2;
		initMotor(this.leftFollower2);
		initMotorForFollowerMode(leftLead, leftFollower2);
		
		this.rightLead = rightLead;
		initMotor(this.rightLead);
		//rightLead.setInverted(false);
		rightLead.setSensorPhase(true);
	//	rightLead.setInverted(false);
		rightLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		rightLead.config_kF(0, 0.7297, RobotMap.TALON_TIMEOUT);
		
		this.rightFollower1 = rightFollower1;
		initMotor(this.rightFollower1);
		initMotorForFollowerMode(rightLead, rightFollower1);
		
		this.rightFollower2 = rightFollower2;
		initMotor(this.rightFollower2);
		initMotorForFollowerMode(rightLead, rightFollower2);
		
		initMotionMagicMode();
	}
		
		

	
	private void initMotor(WPI_TalonSRX talon) {
		talon.set(ControlMode.PercentOutput, 0);
		talon.selectProfileSlot(RobotMap.VELOCITY_PID_PROFILE, 0);
		talon.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);
		talon.configNominalOutputReverse(0.0, 0);
		talon.configNominalOutputForward(0.0, 0);
		talon.configPeakOutputForward(1.0, 0);
		talon.configPeakOutputReverse(-1.0, 0);
		//Note: This was changed from voltage to percentage used with 1 representing 100 percent or max voltage and -1 representing 100 percent backwards.
		
		// TODO: Set the default Talon parameters (done- check over again)
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static DriveActual getInstance() {
		// TODO: Update if constructor changes
		if (instance == null) {
			// First usage - create Drive object
			instance = new DriveActual(
					new WPI_TalonSRX(1), new WPI_TalonSRX(2), new WPI_TalonSRX(3),
					new WPI_TalonSRX(4), new WPI_TalonSRX(5), new WPI_TalonSRX(6));
		}
		return instance;
	}

	/**
	 * Initializes settings for position mode
	 *
	 * @return Successful or not
	 */
	public boolean initPositionMode() {
		// TODO: Set motors for percent voltage bus mode Check RobotMap to see if it is enabled for this robot.
		rightLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		
//		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 7", "0"));
//		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0"));
//		
//		double kIRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "0"));
//		double kILeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0"));
//		
//		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "7"));
//		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "7"));
//				
//		rightLead.config_kP(0, kPRight, RobotMap.TALON_TIMEOUT);
//		leftLead.config_kP(0, kPLeft, RobotMap.TALON_TIMEOUT);
//		
//		rightLead.config_kI(0, kIRight, RobotMap.TALON_TIMEOUT);
//		leftLead.config_kI(0, kILeft, RobotMap.TALON_TIMEOUT);
//		
//		rightLead.config_kD(0, kDRight, RobotMap.TALON_TIMEOUT);
//		leftLead.config_kD(0, kDLeft, RobotMap.TALON_TIMEOUT);
		
//			LOGGER.info("Right p value: " + kPRight + " and left p value: " + kPLeft);
		return false;
	}
	
	public void initMotionMagicMode() {
		rightLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		
		double kPRight = 1.4; // Double.parseDouble(SmartDashboard.getString("DB/String 7", "1.4"));
		double kPLeft = 1.6; //Double.parseDouble(SmartDashboard.getString("DB/String 2", "1.6"));
		
		double kIRight = 0.0; // Double.parseDouble(SmartDashboard.getString("DB/String 8", "0.0"));
		double kILeft = 0.0; //Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		
		double kDRight = 165; //Double.parseDouble(SmartDashboard.getString("DB/String 9", "165"));
		double kDLeft = 198; //Double.parseDouble(SmartDashboard.getString("DB/String 4", "198"));
				
		rightLead.config_kP(0, kPRight, RobotMap.TALON_TIMEOUT);
		leftLead.config_kP(0, kPLeft, RobotMap.TALON_TIMEOUT);
		
		rightLead.config_kI(0, kIRight, RobotMap.TALON_TIMEOUT);
		leftLead.config_kI(0, kILeft, RobotMap.TALON_TIMEOUT);
		
		rightLead.config_kD(0, kDRight, RobotMap.TALON_TIMEOUT);
		leftLead.config_kD(0, kDLeft, RobotMap.TALON_TIMEOUT);
//		This is commented out because we will need the SmartDashboard to tune other things later.
		
		leftLead.configMotionCruiseVelocity(1052 / 2, RobotMap.TALON_TIMEOUT); //1052 is 75 percent of the max speed, which is 1402	
		leftLead.configMotionAcceleration(1052 / 2, RobotMap.TALON_TIMEOUT);
		
		rightLead.configMotionCruiseVelocity(1052 / 2, RobotMap.TALON_TIMEOUT);
		rightLead.configMotionAcceleration(1052 / 2, RobotMap.TALON_TIMEOUT);	
	}
	@Override
	public void zeroPosition() {
		rightLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
	}
	/**
	 * @param distanceInFeet
	 * 		Distance robot needs to go in feet
	 */
	@Override
	public void moveFeet(double distanceInFeet) {
		go(distanceInFeet, distanceInFeet, ControlMode.MotionMagic);
	}
	/**
	 * @param rotation 
	 * 			Turn amount in degrees. Currently turns first then moves.
	 * 
	 * @param distance 
	 * 		 Amount of distance robot needs to go. Currently turns first then moves.
	 * 
	 * 
	 */
	@Override
	public void rotateDegrees(double rotation) {
		double rotationTicks = degreesToTicks(rotation);
		
		go(rotationTicks, -rotationTicks, ControlMode.MotionMagic );
		
	}
	
	public void PositionModeMove(double left, double right) {
		go(left, right, ControlMode.Position);
	}
	
	private void initMotorForFollowerMode(WPI_TalonSRX master, WPI_TalonSRX slave) {
		// TODO: Slave motors to a single master
		//TODO: Check the value on the follower set.
		slave.set(ControlMode.Follower, master.getDeviceID());
		LOGGER.debug("Set " + slave.getDeviceID() + " Following " + master.getDeviceID());
	}

	public void logClosedLoopErrors() {
		LOGGER.debug(
				//TODO Check the arguments for the closed loop errors.
				"Vel L= " + leftLead.getSelectedSensorVelocity(0) + " R=" + rightLead.getSelectedSensorVelocity(0)
				+ "Pos L=" + leftLead.getSelectedSensorPosition(0) + " R=" + rightLead.getSelectedSensorPosition(0)+
				"Err L=" + leftLead.getClosedLoopError(0) +
				" R=" + rightLead.getClosedLoopError(0));
	}
	
	public void publishRawSensorValues() {
		SmartDashboard.putNumber("leftRawSensorPosition", leftLead.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("rightRawSensorPosition", rightLead.getSelectedSensorPosition(0));
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

	/**
	 * Drives each of the six wheels at different speeds using invert constants to account for wiring.
	 *
	 * @param left
	 * 			Speed or Distance value for left wheels
	 * @param right
	 * 			Speed or Distance value for right wheels
	 */
	//TODO: Check to see if we still need this function.
	private void go(double left, double right, ControlMode mode) {
		// TODO: Check to make sure all motors exist. If not throw a null pointer exception
		controlMode = mode;
		if (leftLead == null || rightLead == null || this.leftFollower1 == null || this.leftFollower2 == null || this.rightFollower1 == null || this.rightFollower2 == null) {
			throw new NullPointerException("Null motor provided");
		}
		
		right *= -1;
		
		//TODO: Set the speeds
		//TODO Check to see if we need the params.
		LOGGER.info("Drive left=" + left + "right=" + right + ".");
		leftLead.set(mode, left);
		leftFollower1.set(ControlMode.Follower, leftLead.getDeviceID());
		leftFollower2.set(ControlMode.Follower, leftLead.getDeviceID());
		
		rightLead.set(mode, right);
		rightFollower1.set(ControlMode.Follower, rightLead.getDeviceID());
		rightFollower2.set(ControlMode.Follower, rightLead.getDeviceID());
		
		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
	}

	public void turnByPosition(double degrees) {
		// TODO: Turns in place to the specified angle from center using position mode
	}

	/**
	 * Turns to specified angle according to gyro
	 *
	 * @param angle
	 *            in degrees
	 *
	 * @return True when pointing at the angle
	 */
	public boolean turnToAngle(double angle) {
		//TODO: Uses the gyro to determine the angle, correcting until it points the correct direction
		return false; // Put in test to determine if on target
	}

	public boolean isStopped(){
		// TODO: Check to see if the robot is stopped
		return false;
	}

	public double getTurnError() {
		// TODO Get the absolute error when turning
		return 0;
	}

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		//TODO: Stop all motors
		go(0,0, ControlMode.Disabled);
		
	}
	
	@Override
	public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
		super.arcadeDrive(xSpeed, zRotation, squaredInputs);
		
		leftFollower1.set(ControlMode.Follower, leftLead.getDeviceID());
		leftFollower2.set(ControlMode.Follower, leftLead.getDeviceID());
		
		rightFollower1.set(ControlMode.Follower, rightLead.getDeviceID());
		rightFollower2.set(ControlMode.Follower, rightLead.getDeviceID());
	}
	
	/**
	 * Ticks are set to the distance in feet divided by the circumference of the wheel in feet
	 * and then multiplied by 1024 which is the number of ticks in one revolution of the wheel.
	 * @param feetDist Distance in feet
	 * @return Distance in sensor units
	 */
	@Override
	public double feetToTicks(double feetDist) {
		return 1024 * feetDist / (6 * Math.PI / 12);
	}
	@Override
	public double degreesToTicks(double turnAmountInDegrees) {
		double diameterInInches = 22.75;
		double radius = diameterInInches / 24.0; //Diameter divided by (2 * 12) to translate to feet and to get radius.
		double turnAmountInRadians = Math.toRadians(turnAmountInDegrees * (367.5/360)); //The 367.5/360 is to fix measurement errors.
		return feetToTicks(turnAmountInRadians * radius);
	}
	
	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	@Override
	public double absoluteDistanceMoved() {
		double leftRotations =  Math.abs(leftLead.getSelectedSensorPosition(0));
		double rightRotations = Math.abs(this.rightLead.getSelectedSensorPosition(0));
		double distance =  RobotMap.WHEELPOD_CIRCUMFERENCE / 12;
		if (leftRotations < rightRotations) {
			distance *= leftRotations;
		} else {
			distance *= rightRotations;
		}
		return distance;
	}

}
