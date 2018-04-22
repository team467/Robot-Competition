package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.MotorSafetyHelper;


public class Climber {
	private static final Logger LOGGER = LogManager.getLogger(Climber.class);
	private static Climber instance;

	private TalonSpeedControllerGroup climbController;

	public Climber() {
		if (!RobotMap.HAS_CLIMBER) {
			return;
		}
		
		if(RobotMap.HAS_CLIMBER) {
			WPI_TalonSRX climbMotorLeader = new WPI_TalonSRX(RobotMap.CLIMB_MOTOR_CONTROLLER_LEADER);
			WPI_TalonSRX climbMotorFollower1 = new WPI_TalonSRX(RobotMap.CLIMB_MOTOR_CONTROLLER_FOLLOWER1);
			climbController = new TalonSpeedControllerGroup(ControlMode.PercentOutput, false, climbMotorLeader, climbMotorFollower1);
			LOGGER.info("Created climber Motors");
		} else {
			LOGGER.info("Not enough climb motors, no climb capabilities");
		}
	}
	
	public static Climber getInstance() {
		if (instance == null) {
			if (!RobotMap.HAS_CLIMBER) {
			} else {
				instance = new Climber();
				
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

	public void neutral() {
		climbController.set(ControlMode.PercentOutput, 0);
		//LOGGER.info("Climber stopped");
	}	

	public void setOpenLoopRamp() {
		climbController.setOpenLoopRamp((RobotMap.CLIMBER_RAMP_TIME));
		
	}
}