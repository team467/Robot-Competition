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
	
	private int previousSensorPosition;
	
	ControlMode controlMode = ControlMode.PercentOutput;
	
	public TalonSpeedControllerGroup(ControlMode _controlMode, boolean sensorIsInverted,
			WPI_TalonSRX _leader, WPI_TalonSRX _follower1, WPI_TalonSRX _follower2) {
		this.leader = _leader;
		this.follower1 = _follower1;
		this.follower2 = _follower2;	
		this.controlMode = _controlMode;
		
		initMotor(leader);
		initMotor(follower1);
		initMotor(follower2);
		
		//only have sensor on leader
		leader.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		leader.setSensorPhase(sensorIsInverted);
		leader.configMotionCruiseVelocity(1052 / 2, RobotMap.TALON_TIMEOUT); //1052 is 75 percent of the max speed, which is 1402	
		leader.configMotionAcceleration(1052 / 2, RobotMap.TALON_TIMEOUT);		
		zero();
		
	}
	
	private void initMotor(WPI_TalonSRX talon) {
		talon.set(ControlMode.PercentOutput, 0);
		talon.selectProfileSlot(0, 0);
		talon.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);

		//Note: This was changed from voltage to percentage used with 1 representing 100 percent or max voltage 
		//      and -1 representing 100 percent backwards.
		talon.configNominalOutputReverse(0.0, 0);		
		talon.configNominalOutputForward(0.0, 0);
		talon.configPeakOutputForward(1.0, 0);
		talon.configPeakOutputReverse(-1.0, 0);
		
		talon.configOpenloopRamp(0.2, RobotMap.TALON_TIMEOUT);
		talon.configClosedloopRamp(0.2, RobotMap.TALON_TIMEOUT);		
	}
	
	public void logClosedLoopErrors(String side) {
		LOGGER.debug(
				//TODO Check the arguments for the closed loop errors.
				side + ": Vel = " + leader.getSelectedSensorVelocity(0) +
				" Pos = " + leader.getSelectedSensorPosition(0) +
				" Err = " + leader.getClosedLoopError(0));
	}
	
	private void logTooMuch() {
		LOGGER.debug("GET: " + leader.get());
		LOGGER.debug("Closed Loop error: " + leader.getClosedLoopError(0));
//		LOGGER.debug("Closed loop Target: " + leader.getClosedLoopTarget(0));
		LOGGER.debug("Name: " + leader.getName());
		LOGGER.debug("Motor output percent: " + leader.getMotorOutputPercent());
		LOGGER.debug("Sensor Position: " + leader.getSelectedSensorPosition(0));
		LOGGER.debug("Sensor Velocity: " + leader.getSelectedSensorVelocity(0));
		
	}
	
	public void setPIDF(double p, double i, double d, double f){
		LOGGER.debug("Set PIDF to: " + p + " " + i + " " + d +" " + f);
		leader.config_kP(0, p, RobotMap.TALON_TIMEOUT);
		leader.config_kI(0, i, RobotMap.TALON_TIMEOUT);
		leader.config_kD(0, d, RobotMap.TALON_TIMEOUT);
		leader.config_kF(0, f, RobotMap.TALON_TIMEOUT);
	}
		
	public void zero() {
		previousSensorPosition = 0;
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
	
	public void set(ControlMode controlMode, double outputValue) {
//		LOGGER.debug("Devices: " + leader.getDeviceID() + ", " + follower1.getDeviceID() + ", " + follower2.getDeviceID());
//		LOGGER.debug("Control Mode: " + controlMode + " Output Value: " + outputValue);
		leader.set(controlMode, outputValue);
		follower1.follow(leader);
		follower2.follow(leader);
//		logTooMuch();
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
	
	public boolean isStopped(){
		int leaderSensorPosition = leader.getSelectedSensorPosition(0);
		boolean isStopped = false;

		if (leader.getControlMode() == ControlMode.Disabled) {
			isStopped =  true;
		} else if (leaderSensorPosition == previousSensorPosition) {
			isStopped = true;
		}
		previousSensorPosition = leaderSensorPosition;
		return isStopped;
		}

	public int sensorPosition() {
		return leader.getSelectedSensorPosition(0);
	}
}
