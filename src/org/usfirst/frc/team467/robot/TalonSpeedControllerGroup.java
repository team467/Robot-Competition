package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Autonomous.Actions;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class TalonSpeedControllerGroup implements SpeedController {
	private static final Logger LOGGER = Logger.getLogger(TalonSpeedControllerGroup.class);
	WPI_TalonSRX leader;
	WPI_TalonSRX follower1;
	WPI_TalonSRX follower2;
	
	ControlMode controlMode = ControlMode.PercentOutput;
	
	public TalonSpeedControllerGroup(ControlMode controlMode, WPI_TalonSRX leader, WPI_TalonSRX follower1, WPI_TalonSRX follower2) {
		this.leader = leader;
		this.follower1 = follower1;
		this.follower2 = follower2;	
		this.controlMode = controlMode;
	}
	
	@Override
	public void disable() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.disable();
		follower1.disable();
		follower2.disable();
	}
	
	@Override
	public double get() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return 0.0;
		}
		return leader.get();
	}
	
	@Override
	public void pidWrite(double output) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.pidWrite(output);
		follower1.set(ControlMode.Follower, leader.getDeviceID());
		follower2.set(ControlMode.Follower, leader.getDeviceID());	
	}
	
	@Override
	public void set(double speed) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		set(controlMode, speed);
	}
	
	public void set(ControlMode controlMode, double speed) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.set(controlMode, speed);
		follower1.set(ControlMode.Follower, leader.getDeviceID());
		follower2.set(ControlMode.Follower, leader.getDeviceID());
	}
	
	@Override
	public void setInverted(boolean isInverted) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.setInverted(isInverted);
		follower1.setInverted(isInverted);
		follower2.setInverted(isInverted);
	}
	
	@Override
	public boolean getInverted() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return false;
		}
		return leader.getInverted();
	}
	
	@Override
	public void stopMotor() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.stopMotor();
		follower1.stopMotor();
		follower2.stopMotor();		
	}
	

}
