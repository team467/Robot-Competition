package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.SpeedController;

public class Climber {
	private static final Logger LOGGER = LogManager.getLogger(Climber.class);
	private static Climber instance;

	private WPI_TalonSRX climbController;

	public Climber(SpeedController heightController) {
		if (!RobotMap.HAS_CLIMBER) {
			return;
		}
		
		climbController = (WPI_TalonSRX) heightController;
	}
	
	public static Climber getInstance() {
		if (instance == null) {
			if (!RobotMap.HAS_CLIMBER) {
			} else {
				instance = new Climber(new WPI_TalonSRX(RobotMap.CLIMBER_MOTOR_CHANNEL));
				
			}
		}
		return instance;
	}

	public void climbUp() {
		if (DriverStation.getInstance().getMatchTime() <= 30.0) {
			climbController.set(ControlMode.PercentOutput, RobotMap.CLIMBER_SPEED);
			LOGGER.info("Climbing up.");
		} else {
			LOGGER.info("Too early to climb up.");
		}
	}

	public void climbDown() {
		if (DriverStation.getInstance().getMatchTime() <= 30.0) {
			climbController.set(ControlMode.PercentOutput, -1 * RobotMap.CLIMBER_SPEED);
			LOGGER.info("Climbing down.");
		} else {
			LOGGER.info("Too early to climb down.");
		}
	}

	public void neutral() {
		climbController.set(ControlMode.PercentOutput, 0);
		//LOGGER.info("Climber stopped");
	}	

	public void setOpenLoopRamp() {
		climbController.configOpenloopRamp(RobotMap.CLIMBER_RAMP_TIME, RobotMap.TALON_TIMEOUT);
	}
}