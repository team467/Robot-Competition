package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	private static Elevator instance;
	private static final Logger LOGGER = Logger.getLogger(Elevator.class);

	private AnalogInput heightSensor;
	private WPI_TalonSRX heightController;
	private double feetPerTick;
	private int maxTicksPerIteration;
	private double previousHeight;
	private MotorSafetyHelper m_safetyHelper = new MotorSafetyHelper(heightController);
	
	private Stops targetHeight;
	
	public enum Stops {
		noStop(RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET),
		min(RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET),
		fieldSwitch(2),
		lowScale(6),
		highScale(8);
		
		/**
		 * Height in feet
		 */
		public final double height;
		
		Stops(double height) {
			this.height = height;
		}
	}

	private Elevator() {
		heightSensor = new AnalogInput(RobotMap.ELEVATOR_HEIGHT_SENSOR_ID);
		heightController = new WPI_TalonSRX(RobotMap.ELEVATOR_MOTOR_CHANNEL);
		targetHeight = Stops.noStop;
		feetPerTick = (RobotMap.GEAR_CIRCUMFERENCE_IN_INCHES / 12) / RobotMap.ELEVATOR_TICKS_PER_TURN;
		maxTicksPerIteration = RobotMap.ELEVATOR_TICKS_PER_TURN * RobotMap.MAX_ELEVATOR_RPM / 60 / 100; // 10 ms per iteration
		previousHeight = getHeight();
	}
	
	/**
	 * The lowest value is 196.0, the maximum value is 3741.0. The middle is 1968.5
	 * New max: 2980, new min:956.5
	 * 16.9 ticks = 1 inch
	 * 1 rotation=253 ticks
	 * 
	 * @return a single instance of the Elevator object.
	 */
	public static Elevator getInstance() {
		if (instance == null) {
			instance = new Elevator();
		}
		return instance;
	}

	/**
	 * Moves based on the Xbox controller analog input
	 * 
	 * @param speed The velocity. Shall be a value between -1 and 1.
	 */
	public void manualMove(double speed) {

		targetHeight = Stops.noStop;
		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
		if (isOutOfRange()) {
			heightController.set(0);
			//DriverStation.getInstance().setDriverRumble(0.5);
			return; // Don't bother with any more logic here.
		}
		
		double currentHeight = getHeight();
		for (Stops stop : Stops.values()) {
			if ((previousHeight < stop.height && currentHeight >= stop.height)
				|| (previousHeight > stop.height && currentHeight <= stop.height)) {
				
				//DriverStation.getInstance().setDriverRumble(0.5);
			} else {
				//DriverStation.getInstance().setDriverRumble(0.0);
			}
		}
		if(currentHeight >= 2 && currentHeight <= 2.1 || currentHeight >= 4 && currentHeight <=4.1 || currentHeight >= 6 && currentHeight <= 6.1)
			LOGGER.debug("Rumbling right now");
			else {		
			}
		LOGGER.debug("Current Height: " + currentHeight);
		heightController.set(speed);
		previousHeight = currentHeight;
		LOGGER.debug("Previous Height: " + previousHeight);
	}
	
	public void initMotionMagicMode() {
//		heightController.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);

		
		double kPElevator = 1.4; // Double.parseDouble(SmartDashboard.getString("DB/String 7", "1.4"));
		double kIElevator = 0.0; // Double.parseDouble(SmartDashboard.getString("DB/String 8", "0.0"));
		double kDElevator = 165; // Double.parseDouble(SmartDashboard.getString("DB/String 9", "165"));
		double kFElevator = 0.5; //  Double.parseDouble(SmartDashboard.getString("DB/String 6", "0.5"));
		
		heightController.config_kP(0, kPElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kI(0, kIElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kD(0, kDElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kF(0, kFElevator, RobotMap.TALON_TIMEOUT);

		//		This is commented out because we will need the SmartDashboard to tune other things later.
		
		heightController.configMotionCruiseVelocity(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);
		heightController.configMotionAcceleration(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);	
	}

	public boolean isOutOfRange() {
		return (getHeight() > RobotMap.ELEVATOR_MAX_HEIGHT_IN_FEET || getHeight() < RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET);
	}

	public double getHeight() {
		double height = (heightSensor.getValue() - RobotMap.ELEVATOR_INITIAL_TICKS) * feetPerTick;
		LOGGER.debug("Height in feet: " + height);
		return height;
	}
	
	public void targetHeight(Stops target) {
		targetHeight = target;
	}
	
	public void periodic() {
		if (targetHeight != Stops.noStop) {
			automaticMove(targetHeight.height);
		}
	}
	public void cancelAutomaticMove() {
		targetHeight = Stops.noStop;
		heightController.stopMotor(); 
	}
	
	private void automaticMove(double height) {
		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
		double ticks =  height / feetPerTick + RobotMap.ELEVATOR_INITIAL_TICKS;
		heightController.set(ControlMode.MotionMagic, ticks);
		logSensorVelocityAndPosition();
	}
	
	public void logSensorVelocityAndPosition() {
		LOGGER.debug(
				//TODO Check the arguments for the closed loop errors.
				"Vel= " + heightController.getSelectedSensorVelocity(0)
				+ "Pos=" + heightController.getSelectedSensorPosition(0));
 	}
	
}