package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends DifferentialDrive {
	private ControlMode controlMode;
	
	private static final Logger LOGGER = Logger.getLogger(Drive.class);

	// Single instance of this class
	private static Drive instance = null;
	
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
		
		this.leftLead = leftLead;
		initMotor(this.leftLead);
		leftLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSensorPhase(true);
		leftLead.config_kF(0, 1023.0 / 1402.0, RobotMap.TALON_TIMEOUT); // (100 percent of the output you can send to the motor) divided by (max speed measured in ticks)
		
		this.leftFollower1 = leftFollower1;
		initMotor(this.leftFollower1);

		this.leftFollower2 = leftFollower2;
		initMotor(this.leftFollower2);
		
		this.rightLead = rightLead;
		initMotor(this.rightLead);
		rightLead.setSensorPhase(true);
		rightLead.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		rightLead.config_kF(0, 0.7297, RobotMap.TALON_TIMEOUT);
		
		this.rightFollower1 = rightFollower1;
		initMotor(this.rightFollower1);
		
		this.rightFollower2 = rightFollower2;
		initMotor(this.rightFollower2);
	}
	
	private void initMotor(WPI_TalonSRX talon) {
		talon.set(ControlMode.PercentOutput, 0);
		talon.selectProfileSlot(0, 0);
		talon.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);
		talon.configNominalOutputReverse(0.0, 0);
		talon.configNominalOutputForward(0.0, 0);
		talon.configPeakOutputForward(1.0, 0);
		talon.configPeakOutputReverse(-1.0, 0);
		talon.configOpenloopRamp(0.2, RobotMap.TALON_TIMEOUT);
		talon.configClosedloopRamp(0.2, RobotMap.TALON_TIMEOUT);
		//Note: This was changed from voltage to percentage used with 1 representing 100 percent or max voltage 
		//      and -1 representing 100 percent backwards.
		
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		if (instance == null) {
			// First usage - create Drive object
			instance = new Drive(
					new WPI_TalonSRX(RobotMap.LEFT_LEAD_CHANNEL),
					new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_1_CHANNEL),
					new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_2_CHANNEL),
					
					new WPI_TalonSRX(RobotMap.RIGHT_LEAD_CHANNEL),
					new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_1_CHANNEL),
					new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_2_CHANNEL));
		}
		return instance;
	}

	public void setPIDF(double p, double i, double d, double f){
	 // TODO: Set the PIDF of the talons. Assumes the same values for all motors	
	}
	
	public void logClosedLoopErrors() {
			LOGGER.debug(
					//TODO Check the arguments for the closed loop errors.
					"Vel L= " + leftLead.getSelectedSensorVelocity(0) + " R=" + rightLead.getSelectedSensorVelocity(0)
					+ "Pos L=" + leftLead.getSelectedSensorPosition(0) + " R=" + rightLead.getSelectedSensorPosition(0)+
					"Err L=" + leftLead.getClosedLoopError(0) +
					" R=" + rightLead.getClosedLoopError(0));
	}
	
	
	public void initMotionMagicMode() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No Drive System");
			return;
		}
		
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
	
	public void initSpeedControl() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		
		rightLead.set(ControlMode.Velocity, rightLead.getSelectedSensorVelocity(1));
		leftLead.set(ControlMode.Velocity, leftLead.getSelectedSensorVelocity(1));
		
	}
	public void initPercentOutput() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		
		rightLead.set(ControlMode.PercentOutput, rightLead.getMotorOutputPercent());
		leftLead.set(ControlMode.PercentOutput, leftLead.getMotorOutputPercent());;
		
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
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		
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
	
	
	
	public void zero() {
		rightLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		leftLead.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
	}
	
	public void sendData() {
		RobotData.getInstance().update(rightLead.getSelectedSensorPosition(0), leftLead.getSelectedSensorPosition(0));
	}

	/**
	 * Turns to specified angle according to gyro
	 *
	 * @param angle
	 *            in degrees
	 *
	 * @return True when pointing at the angle
	 */
	public void turn(double degrees) {
		// TODO: Turns in place to the specified angle from center using position mode
	}

	public boolean isStopped(){

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

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		//TODO: Stop all motors
		go(0,0, ControlMode.Disabled);
		
	}
	
	@Override
	public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		super.arcadeDrive(xSpeed, zRotation, squaredInputs);
		
		leftFollower1.set(ControlMode.Follower, leftLead.getDeviceID());
		leftFollower2.set(ControlMode.Follower, leftLead.getDeviceID());
		
		rightFollower1.set(ControlMode.Follower, rightLead.getDeviceID());
		rightFollower2.set(ControlMode.Follower, rightLead.getDeviceID());
	}
	public double feetToTicks (double feetDist) {
		return 1024 * feetDist / (6 * Math.PI / 12);
	}
	
	public double degreesToTicks(double turnAmountInDegrees) {
		double diameterInInches = 22.75;
		double radius = diameterInInches / 24; //Diameter divided by (2 * 12) to translate to feet and to get radius
		double turnAmountInRadians = Math.toRadians(turnAmountInDegrees * (367.5/360)); //The 367.5/360 is to fix measurement errors.
		return feetToTicks(turnAmountInRadians * radius);
	}
	public void move(double distanceInFeet) {
		moveFeet(distanceInFeet, 0);
	}
	
	/**
	 * 
	 * @param distanceInFeet
	 * @param rotationInDegrees enter positive degrees for left turn and enter negative degrees for right turn
	 */
	public void moveFeet (double distanceInFeet, double rotationInDegrees) {
		
		double turnAmtTicks, distAmtTicks, driveTicksS1, driveTicksS2;
		
		distAmtTicks = feetToTicks(distanceInFeet);
		turnAmtTicks = degreesToTicks(rotationInDegrees);
		
		driveTicksS1 = distAmtTicks - turnAmtTicks;
		driveTicksS2 = distAmtTicks + turnAmtTicks;
		go(driveTicksS1, driveTicksS2, ControlMode.MotionMagic);
		
	}
	
	public void rotateToAngle(double angleInDegrees) {
		double distForWheels;		
		if(angleInDegrees <= 180) {
			distForWheels = degreesToTicks(angleInDegrees);
			go(distForWheels, -distForWheels, ControlMode.MotionMagic);
		}
		else {
			distForWheels = degreesToTicks(360 - angleInDegrees);
			go(-distForWheels, distForWheels, ControlMode.MotionMagic);

		}
	
	

}
}