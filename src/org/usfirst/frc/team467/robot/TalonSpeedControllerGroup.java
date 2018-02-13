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
	
	public TalonSpeedControllerGroup() {
		RobotMap.HAS_WHEELS = false;
		leader = null;
		follower1 = null;
		follower2 = null;
	}
	
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
	
	public void setPIDF(double p, double i, double d, double f){
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.config_kP(0, p, RobotMap.TALON_TIMEOUT);
		leader.config_kI(0, i, RobotMap.TALON_TIMEOUT);
		leader.config_kD(0, d, RobotMap.TALON_TIMEOUT);
		leader.config_kF(0, f, RobotMap.TALON_TIMEOUT);
	}
		
	public void zero() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		previousSensorPosition = 0;
		leader.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
	}
	
	@Override
	public void disable() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.disable();
		follower1.disable();
		follower2.disable();
	}
	
	@Override
	public double get() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return 0.0;
		}
		return leader.get();
	}
	
	@Override
	public void pidWrite(double output) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.pidWrite(output);
		follower1.follow(leader);
		follower2.follow(leader);
	}
	
	@Override
	public void set(double speed) {
		set(controlMode, speed);
	}
	
	public void set(ControlMode controlMode, double outputValue) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.set(controlMode, outputValue);
		follower1.follow(leader);
		follower2.follow(leader);
	}
	
	@Override
	public void setInverted(boolean isInverted) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.setInverted(isInverted);
		follower1.setInverted(isInverted);
		follower2.setInverted(isInverted);
	}
	
	@Override
	public boolean getInverted() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return false;
		}
		return leader.getInverted();
	}
	
	@Override
	public void stopMotor() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return;
		}
		leader.stopMotor();
		follower1.stopMotor();
		follower2.stopMotor();		
	}
	
	public boolean isStopped(){
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return true;
		}
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
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No drive system");
			return 0;
		}
		return leader.getSelectedSensorPosition(0);
	}
}
