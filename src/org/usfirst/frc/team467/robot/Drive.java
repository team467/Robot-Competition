package org.usfirst.frc.team467.robot;

import org.apache.log4j.Level;
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
	
	private final TalonSpeedControllerGroup left;
	private final TalonSpeedControllerGroup right;

	// Private constructor
	private Drive(TalonSpeedControllerGroup left, TalonSpeedControllerGroup right) {
		super(left, right);
		this.left = left;
		this.right = right;
		initMotionMagicMode();
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		if (instance == null) {
			// First usage - create Drive object
			WPI_TalonSRX leftLead = new WPI_TalonSRX(RobotMap.LEFT_LEAD_CHANNEL);
			WPI_TalonSRX leftFollower1 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_1_CHANNEL);
			WPI_TalonSRX leftFollower2 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_2_CHANNEL);
			
			WPI_TalonSRX rightLead = new WPI_TalonSRX(RobotMap.RIGHT_LEAD_CHANNEL);
			WPI_TalonSRX rightFollower1 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_1_CHANNEL);
			WPI_TalonSRX rightFollower2 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_2_CHANNEL);
			
			TalonSpeedControllerGroup left = new TalonSpeedControllerGroup(ControlMode.PercentOutput, leftLead, leftFollower1, leftFollower2);
			TalonSpeedControllerGroup right = new TalonSpeedControllerGroup(ControlMode.PercentOutput, rightLead, rightFollower1, rightFollower2);
			instance = new Drive(left, right);
		}
		return instance;
	}

	public void logClosedLoopErrors() {
		left.logClosedLoopErrors();
		right.logClosedLoopErrors();
	}
	
	public void initMotionMagicMode() {
		
		double kPRight = 1.4; // Double.parseDouble(SmartDashboard.getString("DB/String 7", "1.4"));
		double kPLeft = 1.6; //Double.parseDouble(SmartDashboard.getString("DB/String 2", "1.6"));
		
		double kIRight = 0.0; // Double.parseDouble(SmartDashboard.getString("DB/String 8", "0.0"));
		double kILeft = 0.0; //Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		
		double kDRight = 165; //Double.parseDouble(SmartDashboard.getString("DB/String 9", "165"));
		double kDLeft = 198; //Double.parseDouble(SmartDashboard.getString("DB/String 4", "198"));
		
		double kFall = 1023.0 / 1402.0;
		left.setPIDF(kPLeft, kILeft, kDLeft, kFall);
		right.setPIDF(kPRight, kIRight, kDRight, kFall);
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
	private void go(double leftSpeed, double rightSpeed, ControlMode mode) {
		// TODO: Check to make sure all motors exist. If not throw a null pointer exception
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		
		controlMode = mode;
		
		rightSpeed *= -1;
		
		//TODO: Set the speeds
		//TODO Check to see if we need the params.

		LOGGER.info("Drive left=" + leftSpeed + "right=" + rightSpeed + ".");
		left.set(mode, leftSpeed);
		right.set(mode, rightSpeed);		
	}
	
	public void zero() {
		right.zero();
		left.zero();
	}
	
	public void sendData() {
		RobotData.getInstance().update(right.sensorPosition(), left.sensorPosition());
	}

	public boolean isStopped(){
		return left.isStopped() && right.isStopped();
		}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double getLeftDistance() {
		return ticksToFeet(left.sensorPosition());
	}
	
	public double getRightDistance() {
		return ticksToFeet(right.sensorPosition());
	}
	
	public double absoluteDistanceMoved() {
		double lowestAbsDist;
		double leftLeadSensorPos = Math.abs(getLeftDistance());
		double rightLeadSensorPos = Math.abs(getRightDistance());
		if (leftLeadSensorPos >= rightLeadSensorPos) {
			lowestAbsDist = rightLeadSensorPos;
		} else {
			lowestAbsDist = leftLeadSensorPos;
		}	
		LOGGER.debug("The absolute distance moved: " + lowestAbsDist);
		return lowestAbsDist;
	}

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		right.stopMotor();
		left.stopMotor();	
	}
	
	public double degreesToTicks(double turnAmountInDegrees) {
		double diameterInInches = 22.75;
		double radius = diameterInInches / 24; //Diameter divided by (2 * 12) to translate to feet and to get radius
		double turnAmountInRadians = Math.toRadians(turnAmountInDegrees * (367.5/360)); //The 367.5/360 is to fix measurement errors.
		return feetToTicks(turnAmountInRadians * radius);
	}
	
	public void moveFeet(double distanceInFeet) {
		moveFeet(distanceInFeet, 0);
	}
	
	public void rotateByAngle(double angleInDegrees) {
		moveFeet(0, angleInDegrees);
	}
	
	/**
	 * 
	 * @param distanceInFeet
	 * @param rotationInDegrees enter positive degrees for left turn and enter negative degrees for right turn
	 */
	//TODO ask about putting this into the TalonSpeedControllerGroup
	public void moveFeet (double distanceInFeet, double rotationInDegrees) {
		double turnAmtTicks, distAmtTicks, leftDistTicks, rightDistTicks, radius, distTurnInFeet, angleInRadians;
		
		LOGGER.debug("Automated move of " + distanceInFeet + " feet and " + rotationInDegrees + " degree turn.");
		radius = RobotMap.WHEEL_BASE_WIDTH / 2;
		distAmtTicks = feetToTicks(distanceInFeet); //Converts distance to ticks in feet.
		angleInRadians = Math.toRadians(rotationInDegrees);
		distTurnInFeet = radius * angleInRadians; //This is the distance we want to turn.
		turnAmtTicks = (feetToTicks(distTurnInFeet)); //Converts turn angle in ticks to degrees, then to radians.
		
		rightDistTicks = distAmtTicks - turnAmtTicks;
		leftDistTicks = distAmtTicks + turnAmtTicks;
		
		LOGGER.debug("Right distance in feet: " + ticksToFeet(rightDistTicks) + " Left distance in feet: " + ticksToFeet(leftDistTicks));
		
		go(leftDistTicks, rightDistTicks, ControlMode.MotionMagic);
	}
	
	private double ticksToFeet(double ticks) {
		double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
		LOGGER.trace(ticks + " ticks = " + feet + " feet.");
		return feet; 
	}
	
	public double feetToTicks (double feet) {
		double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12)) * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
		LOGGER.trace(feet + " feet = " + ticks + " ticks.");
		return ticks;
	}
	
}