package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

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

	private WPI_TalonSRX heightController;
	private MotorSafetyHelper m_safetyHelper;

	private Stops targetHeight;

	public enum Stops {
		// null if no stop is desired
		basement(761),
		floor(747),
		fieldSwitch(636),
		lowScale(468),
		highScale(358);
		/**
		 * Height in sensor units
		 */
		public final int height;

		Stops(int height) {
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
		configMotorParameters();

		targetHeight = null;
		m_safetyHelper = new MotorSafetyHelper(this.heightController);
	}

	public void configMotorParameters() {
		// Configure talon to be able to use the analog sensor.
		this.heightController.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, RobotMap.TALON_TIMEOUT);
		this.heightController.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);
		this.heightController.setInverted(false);
		this.heightController.setSensorPhase(false);

		heightController.config_kP(0, 40.0, RobotMap.TALON_TIMEOUT);
		heightController.config_kI(0, 0.0, RobotMap.TALON_TIMEOUT);
		heightController.config_kD(0, 5.0, RobotMap.TALON_TIMEOUT);
		heightController.config_kF(0, 0.0, RobotMap.TALON_TIMEOUT);

		heightController.configAllowableClosedloopError(0, 3, RobotMap.TALON_TIMEOUT);
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

	private void automaticMove() {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}

		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}

		configMotorParameters();
		LOGGER.info("Moving to height=" + targetHeight.height);

		heightController.set(ControlMode.Position, targetHeight.height);
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

		// TODO Re-enable and test to see if it works
		// rumbleOnPresetHeights();

		if (Math.abs(speed) >= RobotMap.MIN_LIFT_SPEED) {
			// The controller is asking for elevator movement, cancel preset target and move.
			targetHeight = null;
			heightController.set(ControlMode.PercentOutput, speed);
		} else if (targetHeight != null) {
			// There is a target preset position, move there.
			automaticMove();
		} else {
			// Nothing to do, make sure we're not moving.
			heightController.set(ControlMode.MotionMagic, getRawHeight());
		}

		telemetry();
	}

	public void rumbleOnPresetHeights() {
		double currentHeight = getRawHeight();
		for(Stops stop: Stops.values()) {
			if(stop.height == currentHeight) {
				DriverStation.getInstance().getNavRumbler().rumble(150, 0.3);
			}
		}
	}

	public void telemetry() {
		DriverStation.getInstance().set(0,"target ticks");
		DriverStation.getInstance().set(5, heightController.getActiveTrajectoryPosition());
		DriverStation.getInstance().set(1, "position");
		DriverStation.getInstance().set(6, heightController.getSelectedSensorPosition(0));

		SmartDashboard.putNumber("Elevator Closed Loop Error", heightController.getClosedLoopError(0));
		SmartDashboard.putNumber("Elevator Current Position", heightController.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Elevator Target Height (in)", targetHeight != null ? targetHeight.height : -1);
	}
}