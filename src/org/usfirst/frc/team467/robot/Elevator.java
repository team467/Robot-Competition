package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	private static Elevator instance;
	private static final Logger LOGGER = Logger.getLogger(Elevator.class);

	private AnalogInput heightSensor;
	private WPI_TalonSRX heightController;
	private double feetPerTick;
	private int maxTicksPerIteration;
	private double previousHeight;
	private MotorSafetyHelper m_safetyHelper;

	private Stops targetHeight;

	public enum Stops {
		// null if no stop is desired
		floor(RobotMap.ELEVATOR_MIN_HEIGHT_IN_FEET),
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

	/**
	 * 
	 * @return a single instance of the Elevator object.
	 */
	public static Elevator getInstance() {
		if (instance == null) {
			if (!RobotMap.HAS_ELEVATOR) {
				instance = new Elevator(new NullSpeedController());
			} else {
				instance = new Elevator(new WPI_TalonSRX(RobotMap.ELEVATOR_MOTOR_CHANNEL));
			}
		}
		return instance;
	}

	private Elevator(SpeedController heightController) {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}
		heightSensor = new AnalogInput(RobotMap.ELEVATOR_HEIGHT_SENSOR_ID);
		this.heightController = (WPI_TalonSRX) heightController;

		// Configure talon to be able to use the analog sensor. 
		this.heightController.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, RobotMap.TALON_TIMEOUT);
		this.heightController.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);

		targetHeight = null;
		feetPerTick = (RobotMap.ELEVATOR_GEAR_CIRCUMFERENCE_IN_INCHES / 12) / RobotMap.ELEVATOR_TICKS_PER_TURN;
		maxTicksPerIteration = RobotMap.ELEVATOR_TICKS_PER_TURN * RobotMap.MAX_ELEVATOR_RPM / 60 / 100; // 10 ms per iteration
		previousHeight = getHeightFeet();
		this.heightController.setInverted(true);

		m_safetyHelper = new MotorSafetyHelper(this.heightController);
	}

	public void initMotionMagicMode() {
		double kPElevator = 1.4; // Double.parseDouble(SmartDashboard.getString("DB/String 7", "1.4"));
		double kIElevator = 0.0; // Double.parseDouble(SmartDashboard.getString("DB/String 8", "0.0"));
		double kDElevator = 165; // Double.parseDouble(SmartDashboard.getString("DB/String 9", "165"));
		double kFElevator = 0.5; //  Double.parseDouble(SmartDashboard.getString("DB/String 6", "0.5"));

		heightController.config_kP(0, kPElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kI(0, kIElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kD(0, kDElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kF(0, kFElevator, RobotMap.TALON_TIMEOUT);


		heightController.configMotionCruiseVelocity(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);
		heightController.configMotionAcceleration(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);	
	}

	public double getHeightFeet() {
		double height = (getRawHeight() - RobotMap.ELEVATOR_INITIAL_TICKS) * feetPerTick;
		LOGGER.debug("Height in feet: " + height);
		return height;
	}

	private double getRawHeight() {
		if (!RobotMap.HAS_ELEVATOR) {
			return 0.0;
		}
		return heightSensor.getValue();
	}

	public void moveToHeight(Stops target) {
		// targetHeight member variable used in periodic function to reach the height
		targetHeight = target;
	}

	private void automaticMove(double height) {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}
		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
		double ticks =  height / feetPerTick + RobotMap.ELEVATOR_INITIAL_TICKS;
		heightController.set(ControlMode.MotionMagic, ticks);
		logSensorVelocityAndPosition();
	}

	/**
	 * Moves based on the Xbox controller analog input
	 * 
	 * @param speed The velocity. Shall be a value between -1 and 1.
	 */
	public void move(double speed) {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}

		double currentHeight = getHeightFeet();
		for (Stops stop : Stops.values()) {
			if ((previousHeight < stop.height && currentHeight >= stop.height)
					|| (previousHeight > stop.height && currentHeight <= stop.height)) {
				DriverStation.getInstance().getNavRumbler().rumble(200, 0.8);
			}
		}

		LOGGER.debug("Height prev=" + previousHeight + " current=" + currentHeight);
		previousHeight = currentHeight;

		if (Math.abs(speed) >= RobotMap.MIN_LIFT_SPEED) {
			// The controller is asking for elevator movement, cancel preset target and move.
			targetHeight = null;
			heightController.set(ControlMode.PercentOutput, speed);
		} else if (targetHeight != null) {
			// There is a target preset position, move there.
			automaticMove(targetHeight.height);
		} else {
			// Nothing to do, make sure we're not moving.
			heightController.stopMotor();
		}
	}

	public void logSensorVelocityAndPosition() {
		LOGGER.info(
				//TODO Check the arguments for the closed loop errors.
				"Vel= " + heightController.getSelectedSensorVelocity(0)
				+ "Pos=" + heightController.getSelectedSensorPosition(0));
		DriverStation.getInstance().set(0,"velocity");
		DriverStation.getInstance().set(1, "position");
		DriverStation.getInstance().set(5, heightController.getSelectedSensorVelocity(0));
		DriverStation.getInstance().set(6, heightController.getSelectedSensorPosition(0));
	}
}