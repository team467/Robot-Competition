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
	private double previousHeight;
	private MotorSafetyHelper m_safetyHelper;

	private Stops targetHeight;

	public enum Stops {
		// null if no stop is desired
//		floor(10.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
//		fieldSwitch(24.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
//		lowScale(72.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES),
//		highScale(96.0 + RobotMap.ELEVATOR_ERROR_TOLERANCE_INCHES);
//		Basement - 761
//		Floor - 747
//		Switch - 636
//		Scale Low - 468
//		Scale High - 358
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

		// Configure talon to be able to use the analog sensor. 
		this.heightController.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, RobotMap.TALON_TIMEOUT);
		this.heightController.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);
		this.heightController.configAllowableClosedloopError(0, 3, RobotMap.TALON_TIMEOUT);
		this.heightController.setInverted(false);
		this.heightController.setSensorPhase(false);
		configMotionMagicParameters();

		targetHeight = null;
//		previousHeight = getHeightInches();
		previousHeight = getRawHeight();
		m_safetyHelper = new MotorSafetyHelper(this.heightController);
	}

	public void configMotionMagicParameters() {
		SmartDashboard.putString("DB/String 3", "P");
		SmartDashboard.putString("DB/String 4", "D");
		double kPElevator = Double.parseDouble(SmartDashboard.getString("DB/String 8", "5.27"));
		double kIElevator = 0.0;
		double kDElevator = Double.parseDouble(SmartDashboard.getString("DB/String 9", "5.27"));
		double kFElevator = 51.15;

		heightController.config_kP(0, kPElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kI(0, kIElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kD(0, kDElevator, RobotMap.TALON_TIMEOUT);
		heightController.config_kF(0, kFElevator, RobotMap.TALON_TIMEOUT);

		heightController.configMotionCruiseVelocity(15, RobotMap.TALON_TIMEOUT);
		heightController.configMotionAcceleration(15, RobotMap.TALON_TIMEOUT);
		heightController.configAllowableClosedloopError(0, 0, RobotMap.TALON_TIMEOUT);
	}

//	public double getHeightInches() {
//		double height = (getRawHeight() - RobotMap.ELEVATOR_BOTTOM_TICKS) / RobotMap.ELEVATOR_TICKS_PER_INCH;
//		LOGGER.trace("Height in inches: " + height);
//		return height;
//	}

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

		configMotionMagicParameters();
		LOGGER.info("Moving to heightInInches=" + targetHeight.height);

		heightController.set(ControlMode.MotionMagic, targetHeight.height);
	}
	
//	private int getTargetTicks() {
//	    if (targetHeight == null) {
//	        return -1;
//	    }
//
//	    return (int) (RobotMap.ELEVATOR_BOTTOM_TICKS - targetHeight.height * RobotMap.ELEVATOR_TICKS_PER_INCH);
//	}

	/**
	 * Moves based on the Xbox controller analog input
	 * 
	 * @param speed The velocity. Shall be a value between -1 and 1.
	 */
	public void move(double speed) {
		if (!RobotMap.HAS_ELEVATOR) {
			return;
		}

//		double currentHeight = getHeightInches();
		double currentHeight = getRawHeight();
		// TODO Re-enable and test to see if it works
		//		for (Stops stop : Stops.values()) {
		//			if ((previousHeight < stop.height && currentHeight >= stop.height)
		//					|| (previousHeight > stop.height && currentHeight <= stop.height)) {
		//				DriverStation.getInstance().getNavRumbler().rumble(200, 0.8);
		//			}
		//		}

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

		previousHeight = currentHeight;

		telemetry();
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