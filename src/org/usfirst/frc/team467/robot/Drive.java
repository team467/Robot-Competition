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
public class Drive extends DifferentialDrive {
	private ControlMode controlMode;
	// TODO: DEfine logger (Done)
	private static final Logger LOGGER = Logger.getLogger(Drive.class);
	
	private static final int TALON_TIMEOUT = 10; // 10 ms is the recommended timeout

	// Single instance of this class
	private static Drive instance = null;

	// Data storage object
	
	private WPI_TalonSRX leftLead;
	private WPI_TalonSRX leftFollower1;
	private WPI_TalonSRX leftFollower2;
	
	
	private WPI_TalonSRX rightLead;
	private WPI_TalonSRX rightFollower1;
	private WPI_TalonSRX rightFollower2;

	// Private constructor
	private Drive(WPI_TalonSRX leftLead,  WPI_TalonSRX leftFollower1,  WPI_TalonSRX leftFollower2,
		          WPI_TalonSRX rightLead, WPI_TalonSRX rightFollower1, WPI_TalonSRX rightFollower2) {
		super(leftLead, rightLead);
		
		
		// Need to specify the motor channels.
		// TODO: Define and initialize motors. (Done)
		
		this.leftLead = leftLead;
		initMotor(this.leftLead);
		leftLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TALON_TIMEOUT);
		leftLead.setSensorPhase(true);
		leftLead.config_kF(0, 0.7297, TALON_TIMEOUT); //0.7297 is 1023 (100 percent of the output you can send to the motor) divided by 1402 (max speed measured in ticks)
		
		

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
		rightLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TALON_TIMEOUT);
		rightLead.config_kF(0, 0.7297, TALON_TIMEOUT);
		
		this.rightFollower1 = rightFollower1;
		initMotor(this.rightFollower1);
		initMotorForFollowerMode(rightLead, rightFollower1);
		
		this.rightFollower2 = rightFollower2;
		initMotor(this.rightFollower2);
		initMotorForFollowerMode(rightLead, rightFollower2);
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
	public static Drive getInstance() {
		// TODO: Update if constructor changes
		if (instance == null) {
			// First usage - create Drive object
			instance = new Drive(
					new WPI_TalonSRX(1), new WPI_TalonSRX(2), new WPI_TalonSRX(3),
					new WPI_TalonSRX(4), new WPI_TalonSRX(5), new WPI_TalonSRX(6));
		}
		return instance;
	}

	public void setPIDF(double p, double i, double d, double f){
		// TODO: Set the PIDF of the talons. Assumes the same values for all motors
		
		
	}
	
	/**
	 * Initializes settings for position mode
	 *
	 * @return Successful or not
	 */
	public boolean initPositionMode() {
		// TODO: Set motors for percent voltage bus mode Check RobotMap to see if it is enabled for this robot.
		rightLead.setSelectedSensorPosition(0, 0, TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, TALON_TIMEOUT);
		
		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 7", "0"));
		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0"));
		
		double kIRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "0"));
		double kILeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0"));
		
		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "7"));
		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "7"));
				
		rightLead.config_kP(0, kPRight, TALON_TIMEOUT);
		leftLead.config_kP(0, kPLeft, TALON_TIMEOUT);
		
		rightLead.config_kI(0, kIRight, TALON_TIMEOUT);
		leftLead.config_kI(0, kILeft, TALON_TIMEOUT);
		
		rightLead.config_kD(0, kDRight, TALON_TIMEOUT);
		leftLead.config_kD(0, kDLeft, TALON_TIMEOUT);
		
		LOGGER.info("Right p value: " + kPRight + " and left p value: " + kPLeft);
		return false;
	}
	
	public void initMotionMagicMode() {
		rightLead.setSelectedSensorPosition(0, 0, TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, TALON_TIMEOUT);
		
		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 7", "0"));
		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0"));
		
		double kIRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "0"));
		double kILeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0"));
		
		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "7"));
		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "7"));
				
		rightLead.config_kP(0, kPRight, TALON_TIMEOUT);
		leftLead.config_kP(0, kPLeft, TALON_TIMEOUT);
		
		rightLead.config_kI(0, kIRight, TALON_TIMEOUT);
		leftLead.config_kI(0, kILeft, TALON_TIMEOUT);
		
		rightLead.config_kD(0, kDRight, TALON_TIMEOUT);
		leftLead.config_kD(0, kDLeft, TALON_TIMEOUT);
		
		leftLead.configMotionCruiseVelocity(1052 / 2, TALON_TIMEOUT); //1052 is 75 percent of the max speed, which is 1402	
		leftLead.configMotionAcceleration(1052 / 2, TALON_TIMEOUT);
		
		rightLead.configMotionCruiseVelocity(1052 / 2, TALON_TIMEOUT);
		rightLead.configMotionAcceleration(1052 / 2, TALON_TIMEOUT);	
	}
	public void initSpeedControl() {
		rightLead.set(ControlMode.Velocity, rightLead.getSelectedSensorVelocity(1));
		leftLead.set(ControlMode.Velocity, leftLead.getSelectedSensorVelocity(1));
		
	}
	public void initPercentOutput() {
		rightLead.set(ControlMode.PercentOutput, rightLead.getMotorOutputPercent());
		leftLead.set(ControlMode.PercentOutput, leftLead.getMotorOutputPercent());;
		
	}
	
	public void motionMagicMove(double left, double right) {
		go(left, right, ControlMode.MotionMagic);
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
	
	public void publishClosedLoopErrors() {
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

	/**
	 * Gets the error value from the motor controller.
	 *
	 * @return the current error
	 */
	public double error() {
		// TODO: Get the error from the motor sensor. If in position mode, change into a distance measurement based on the the number of codes per revolution
		
		return 0;
	}

	public boolean checkSensor() {
		// TODO: Check the sensors to make sure they are reading the values specified. For example if I set the speed at 100, the value return from get speed should be 100
		// Need separate checks for speed and position.

		// All some time for the motors to get up to speed
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Moves a distance if in position mode.
	 *
	 * @param distance
	 *            the target distance in feet.
	 * @return boolean true if move is complete
	 */

	public boolean isAtDistance() {
		// TODO: Checks to see if the robot is at the desired position, plus or minus the allowed error
		return true;
	}

	public boolean isStopped(){
		// TODO: Check to see if the robot is stopped
		return false;
	}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		// TODO: returns the amount of distance moved based on the the position of the talon sensors nad the wheel circumerence
		return 0;
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
	public double feetToTicks(double feetDist) {
		return 1024 * feetDist / (6 * Math.PI / 12);
	}
	public double degreesToTicks(double turnAmountInDegrees) {
		double diameterInInches = 22.75;
		double radius = diameterInInches / 24; //Diameter divided by (2 * 12) to translate to feet and to get radius.
		double turnAmountInRadians = Math.toRadians(turnAmountInDegrees * (367.5/360));
		return feetToTicks(turnAmountInRadians * radius);
	}
}
