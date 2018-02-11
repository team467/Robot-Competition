package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;

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
		
		//only have sensor on leader
		leader.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		leader.setSensorPhase(true);
		leader.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
		
		leader.set(ControlMode.PercentOutput, 0);
		leader.selectProfileSlot(0, 0);
		leader.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);
		leader.configNominalOutputReverse(0.0, 0);
		leader.configNominalOutputForward(0.0, 0);
		leader.configPeakOutputForward(1.0, 0);
		leader.configPeakOutputReverse(-1.0, 0);

		//Note: This was changed from voltage to percentage used with 1 representing 100 percent or max voltage 
		//      and -1 representing 100 percent backwards.
		leader.configOpenloopRamp(0.2, RobotMap.TALON_TIMEOUT);
		leader.configClosedloopRamp(0.2, RobotMap.TALON_TIMEOUT);		
		
		leader.configMotionCruiseVelocity(1052 / 2, RobotMap.TALON_TIMEOUT); //1052 is 75 percent of the max speed, which is 1402	
		leader.configMotionAcceleration(1052 / 2, RobotMap.TALON_TIMEOUT);
		
		leader.configMotionCruiseVelocity(1052 / 2, RobotMap.TALON_TIMEOUT);
		leader.configMotionAcceleration(1052 / 2, RobotMap.TALON_TIMEOUT);	
	}
	
	public void logClosedLoopErrors() {
		LOGGER.debug(
				//TODO Check the arguments for the closed loop errors.
				"Vel = " + leader.getSelectedSensorVelocity(0) +
				"Pos = " + leader.getSelectedSensorPosition(0) +
				"Err = " + leader.getClosedLoopError(0));
	}
	
	public void setPIDF(double p, double i, double d, double f){
		leader.config_kP(0, p, RobotMap.TALON_TIMEOUT);
		leader.config_kI(0, i, RobotMap.TALON_TIMEOUT);
		leader.config_kD(0, d, RobotMap.TALON_TIMEOUT);
		leader.config_kF(0, f, RobotMap.TALON_TIMEOUT);
	}
		
	
	public void zero() {
		leader.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
	}
	
	@Override
	public void disable() {
		leader.disable();
		follower1.disable();
		follower2.disable();
	}
	
	@Override
	public double get() {
		return leader.get();
	}
	
	@Override
	public void pidWrite(double output) {
		leader.pidWrite(output);
		follower1.follow(leader);
		follower2.follow(leader);
	}
	
	@Override
	public void set(double speed) {
		set(controlMode, speed);
	}
	
	public void set(ControlMode controlMode, double speed) {
		leader.set(controlMode, speed);
		follower1.follow(leader);
		follower2.follow(leader);
	}
	
	@Override
	public void setInverted(boolean isInverted) {
		leader.setInverted(isInverted);
		follower1.setInverted(isInverted);
		follower2.setInverted(isInverted);
	}
	
	@Override
	public boolean getInverted() {
		return leader.getInverted();
	}
	
	@Override
	public void stopMotor() {
		leader.stopMotor();
		follower1.stopMotor();
		follower2.stopMotor();		
	}
	
}
