/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Drive extends DifferentialDrive {
	private ControlMode controlMode;
	// TODO: DEfine logger (Done)
	private static final Logger LOGGER = Logger.getLogger(Drive.class);

	// Single instance of this class
	private static Drive instance = null;

	// Data storage object
	private DataStorage data;
	
	private WPI_TalonSRX leftFollower1;
	private WPI_TalonSRX leftFollower2;
	private WPI_TalonSRX rightFollower1;
	private WPI_TalonSRX rightFollower2;
	
	private WPI_TalonSRX leftLead;
	private WPI_TalonSRX rightLead;

	// Private constructor
	private Drive(WPI_TalonSRX left, WPI_TalonSRX leftFollower1, WPI_TalonSRX leftFollower2,
			WPI_TalonSRX right, WPI_TalonSRX rightFollower1, WPI_TalonSRX rightFollower2) {
		super(left, right);
		
		
		// Need to specify the motor channels.
		// TODO: Define and initialize motors. (Done)
		
		this.leftLead = left;
		initMotor(this.leftLead);

		this.leftFollower1 = leftFollower1;
		initMotor(this.leftFollower1);
		initMotorForFollowerMode(left, leftFollower1);

		this.leftFollower2 = leftFollower2;
		initMotor(this.leftFollower2);
		initMotorForFollowerMode(left, leftFollower2);
		
		this.rightLead = right;
		initMotor(this.rightLead);

		this.rightFollower1 = rightFollower1;
		initMotor(this.rightFollower1);
		initMotorForFollowerMode(right, rightFollower1);
		
		this.rightFollower2 = rightFollower2;
		initMotor(this.rightFollower2);
		initMotorForFollowerMode(right, rightFollower2);
		
	// Make objects
	data = DataStorage.getInstance();
	

}
	
	
	

		

	
	private void initMotor(WPI_TalonSRX talon) {
		talon.set(ControlMode.PercentOutput, 0);
		talon.selectProfileSlot(RobotMap.VELOCITY_PID_PROFILE, 0);
		talon.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);
		talon.configNominalOutputReverse(-1.0, 1);
		// TODO: Set the default Talon parameters (done- check over again)
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		// TODO: Update if constructure changes
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
	 * Sets the motors to drive in speed mode.
	 *
	 * @return Successful or not
	 */
	public boolean setSpeedMode() {
		// TODO: Set the motors in speed control mode. Check RobotMap to see if it is enabled for this robot.
		return false; // Update
	}

	/**
	 * Sets the motors to drive in percent of voltage mode. Default for when the speed sensors are not working.
	 */
	public boolean setPercentOutputMode() {
		// TODO: Set motors for percent voltage bus mode
		//TODO: When will this be used?
		leftLead.set(ControlMode.PercentOutput, 0);
		rightLead.set(ControlMode.PercentOutput, 0);
		//TODO Set the followers.
		return true;
	}

	/**
	 * Sets the motors to drive in position mode.
	 *
	 * @return Successful or not
	 */
	public boolean setPositionMode() {
		// TODO: Set motors for percent voltage bus mode Check RobotMap to see if it is enabled for this robot.
		return false;
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
				"closedLoopErr FL=" + leftLead.getClosedLoopError(0) +
				" FR=" + rightLead.getClosedLoopError(0));
	}

	public ControlMode getControlMode() {
		// TODO: Update to return the currently used control mode
		return ControlMode.PercentOutput;
	}

	/**
	 * Takes the drive out of position mode back into its default drive mode.
	 */
	public void setDefaultDriveMode() {
		// TODO: Check the Robot Map and set the system to use the speed controllers if enabled there
		if (RobotMap.useSpeedControllers) {
			setSpeedMode();
		} else {
			setPercentOutputMode();
		}

		stop();
	}

	/**
	 * Drives each of the four wheels at different speeds using invert constants to account for wiring.
	 *
	 * @param left
	 * 			Speed or Distance value for left wheels
	 * @param right
	 * 			Speed or Distance value for right wheels
	 */
	private void go(double left, double right, ControlMode mode) {
		// TODO: Check to make sure all motors exist. If not throw a null pointer exception
		if (leftLead == null || rightLead == null || this.leftFollower1 == null || this.leftFollower2 == null || this.rightFollower1 == null || this.rightFollower2 == null) {
			throw new NullPointerException("Null motor provided");
		}
		
		//TODO: Set the speeds
		//TODO Check to see if we need the params.
		leftLead.set(mode, left);
		rightLead.set(mode, right);
		leftFollower1.set(ControlMode.Follower, leftLead.getDeviceID());
		leftFollower2.set(ControlMode.Follower, leftLead.getDeviceID());
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
	 * Single point of entry for any speed adjustments made to the robot. This can be used for:
	 * - limiting rate of acceleration or deceleration
	 * - adjusting speed parameter based on Talon control mode (Speed, Position etc.)
	 *
	 * @param speedOrDistance
	 *            input speed or distance for robot
	 *            	speed will be in range -1.0 to 1.0
	 *              distance is measured in feet
	 * @return returns adjusted speed
	 */
	private double adjustSpeedOrDistance(double speedOrDistance) {
		// TODO: Check the control mode to do the right adjustment
		// TODO: Adjust speed value based on the robot's max speed setting
		// TODO: Adjust position value based on the circumference of the wheel
		return 0;
	}

	public void zeroPosition() {
		// TODO: Zero the encoder and current position so that next position move is relative to current position
		
	}

	/**
	 * Gets the error value from the motor controller.
	 *
	 * @return the current error
	 */
	public double error() {
		// TODO: Get the error from the motor sensor. If in position mode, change into a distance measurement based onte the number of codes per revolution
		
		return 0;
	}

	public boolean checkSensor() {
		// TODO: Check the sensors to make sure they are reading the values specified. For example if I set the speed at 100, the value return from get speed should be 100
		// TODO: Check with bryan about what this is for
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
	public void moveDistance(double distance) {
		// TODO: CHecks to see if the robot is in postion mode, and if so, converts the distance to revolutions and moves
	}

	public boolean isAtDistance() {
		// TODO: Checks to see if the robot is at the desired postion, plus or minus the allowed error
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

}
