package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.SpeedController;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	private static Elevator instance;
	private static final Logger LOGGER = Logger.getLogger(Elevator.class);

	private WPI_TalonSRX heightController;
	private int maxTicksPerIteration = 100;
	private double previousHeight;
	private MotorSafetyHelper m_safetyHelper;

	private Stops targetHeight;

	public enum Stops {
		// null if no stop is desired
		floor(10.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
		fieldSwitch(24.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
		lowScale(72.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
		highScale(96.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES);

		/**
		 * Height in inches
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

		this.heightController = (WPI_TalonSRX) heightController;

		// Configure talon to be able to use the analog sensor. 
		this.heightController.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, RobotMap.TALON_TIMEOUT);
		this.heightController.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);
		this.heightController.configAllowableClosedloopError(0, 3, RobotMap.TALON_TIMEOUT);
		this.heightController.setInverted(false);
		this.heightController.setSensorPhase(false);
		configMotionMagicParameters();

		targetHeight = null;
		previousHeight = getHeightInches();
		m_safetyHelper = new MotorSafetyHelper(this.heightController);
	}

	public void configMotionMagicParameters() {
		double kPElevator = 12.0; // Double.parseDouble(SmartDashboard.getString("DB/String 7", "1.4"));
		double kIElevator = 0.0; // Double.parseDouble(SmartDashboard.getString("DB/String 8", "0.0"));
		double kDElevator = 198; // Double.parseDouble(SmartDashboard.getString("DB/String 9", "165"));
		double kFElevator = 0.729672; //  Double.parseDouble(SmartDashboard.getString("DB/String 6", "0.5"));

		heightController.config_kP(0, kPElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kI(0, kIElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kD(0, kDElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kF(0, kFElevator, RobotMap.TALON_TIMEOUT);

		heightController.configMotionCruiseVelocity(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);
		heightController.configMotionAcceleration(maxTicksPerIteration / 2, RobotMap.TALON_TIMEOUT);
	}

	public double getHeightInches() {
		double height = (getRawHeight() - RobotMap.ELEVATOR_BOTTOM_TICKS) / RobotMap.ELEVATOR_TICKS_PER_INCH;
		LOGGER.trace("Height in inches: " + height);
		return height;
	}

	private double getRawHeight() {
		if (!RobotMap.HAS_ELEVATOR) {
			return 0.0;
		}
		return heightController.getSelectedSensorPosition(0);
	}

	public void moveToHeight(Stops target) {
		// targetHeight member variable used in periodic function to reach the height
		targetHeight = target;
	}

	private void automaticMove(double heightInInches) {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}

		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}

		LOGGER.info("Moving to heightInInches=" + heightInInches);

		double ticks = RobotMap.ELEVATOR_BOTTOM_TICKS - heightInInches * RobotMap.ELEVATOR_TICKS_PER_INCH;
		heightController.set(ControlMode.MotionMagic, ticks);
		logSensorAndTargetPosition();
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

		double currentHeight = getHeightInches();
		// TODO Re-enable and test to see if it works
		//		for (Stops stop : Stops.values()) {
		//			if ((previousHeight < stop.height && currentHeight >= stop.height)
		//					|| (previousHeight > stop.height && currentHeight <= stop.height)) {
		//				DriverStation.getInstance().getNavRumbler().rumble(200, 0.8);
		//			}
		//		}

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

	public void logSensorAndTargetPosition() {
		LOGGER.debug(
				//TODO Check the arguments for the closed loop errors.
				"Target= " + heightController.getActiveTrajectoryPosition()
				+ "Pos=" + heightController.getSelectedSensorPosition(0));

		DriverStation.getInstance().set(0,"target ticks");
		DriverStation.getInstance().set(5, heightController.getActiveTrajectoryPosition());

		DriverStation.getInstance().set(1, "position");
		DriverStation.getInstance().set(6, heightController.getSelectedSensorPosition(0));

		DriverStation.getInstance().set(2, "get");
		DriverStation.getInstance().set(7, String.valueOf(heightController.get()));
	}
}