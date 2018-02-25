package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//TalonSpeedControllerGroup
public class TalonSpeedControllerGroup implements SpeedController {
	private static final Logger LOGGER = Logger.getLogger(TalonSpeedControllerGroup.class);
	private WPI_TalonSRX leader;
	private WPI_TalonSRX follower1;
	private WPI_TalonSRX follower2;
	
	private int previousSensorPosition;

	private ControlMode controlMode = ControlMode.PercentOutput;

	public TalonSpeedControllerGroup() {
		leader = null;
		follower1 = null;
		follower2 = null;
	}

	public TalonSpeedControllerGroup(ControlMode controlMode, boolean sensorIsInverted,
			WPI_TalonSRX leader, WPI_TalonSRX follower1, WPI_TalonSRX follower2) {
		if (!RobotMap.HAS_WHEELS) {
			leader = null;
			follower1 = null;
			follower2 = null;
			LOGGER.trace("No drive system");
			return;
		}
		this.leader = leader;
		this.follower1 = follower1;
		this.follower2 = follower2;	
		this.controlMode = controlMode;

		initMotor(this.leader);
		if(follower1 != null) initMotor(this.follower1);
		if(follower2 != null) initMotor(this.follower2);

		//only have sensor on leader
		leader.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, RobotMap.TALON_TIMEOUT);
		leader.setSensorPhase(sensorIsInverted);
		
		zero();		
	}

	public TalonSpeedControllerGroup(ControlMode controlMode, boolean sensorIsInverted,
			WPI_TalonSRX leader, WPI_TalonSRX follower1) {
		this(controlMode, sensorIsInverted, leader, follower1, null);
	}

	public TalonSpeedControllerGroup(ControlMode controlMode, boolean sensorIsInverted,
			WPI_TalonSRX leader) {
		this(controlMode, sensorIsInverted, leader, null, null);
	}

	private void initMotor(WPI_TalonSRX talon) {
		talon.set(ControlMode.PercentOutput, 0);
		talon.selectProfileSlot(0, 0);
		talon.configAllowableClosedloopError(0, RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR, 0);

		//Note: -1 and 1 are the max outputs
		talon.configNominalOutputReverse(0.0, 0);		
		talon.configNominalOutputForward(0.0, 0);
		talon.configPeakOutputForward(1.0, 0);
		talon.configPeakOutputReverse(-1.0, 0);

		talon.configOpenloopRamp(0.2, RobotMap.TALON_TIMEOUT);
		talon.configClosedloopRamp(0.2, RobotMap.TALON_TIMEOUT);		
	}

	public void logClosedLoopErrors(String side) {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No CLosed Loop errors");
			return;
		}
		LOGGER.debug(
				side + ": Vel = " + leader.getSelectedSensorVelocity(0) +
				" Pos = " + leader.getSelectedSensorPosition(0) +
				" Err = " + leader.getClosedLoopError(0));
	}

	public void setPIDF(double p, double i, double d, double f){
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.debug("No PIDF");
			return;
		}
		leader.config_kP(0, p, RobotMap.TALON_TIMEOUT);
		leader.config_kI(0, i, RobotMap.TALON_TIMEOUT);
		leader.config_kD(0, d, RobotMap.TALON_TIMEOUT);
		leader.config_kF(0, f, RobotMap.TALON_TIMEOUT);
//		int motionAcceleration = Integer.parseInt(SmartDashboard.getString("DB/String 3", "20000")); 
//		int motionCruiseVelocity = Integer.parseInt(SmartDashboard.getString("DB/String 8", "15000")); 
		int motionAcceleration = 600;
		int motionCruiseVelocity = 675;

		leader.configMotionCruiseVelocity(motionCruiseVelocity, RobotMap.TALON_TIMEOUT); 
		leader.configMotionAcceleration(motionAcceleration, RobotMap.TALON_TIMEOUT);
		leader.configNeutralDeadband(0.04, RobotMap.TALON_TIMEOUT);

	}

	public void zero() {
		if (!RobotMap.HAS_WHEELS) {
			LOGGER.trace("No drive system");
			return;
		}
		previousSensorPosition = 0;
		leader.setSelectedSensorPosition(0, 0, RobotMap.TALON_TIMEOUT);
	}

	@Override
	public void disable() {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.disable();
		if (follower1 != null) {
			follower1.disable();
		}
		if (follower2 != null) {
			follower2.disable();
		}
	}

	@Override
	public double get() {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return 0.0;
		}
		return leader.get();
	}

	@Override
	public void pidWrite(double output) {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.pidWrite(output);
		if (follower1 != null) {
			follower1.follow(leader);
		}
		if (follower2 != null) {
			follower2.follow(leader);
		}
	}

	@Override
	public void set(double speed) {
		set(controlMode, speed);
	}

	public void set(ControlMode controlMode, double outputValue) {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.set(controlMode, outputValue);
		if (follower1 != null) {
			follower1.follow(leader);
		}
		if (follower2 != null) {
			follower2.follow(leader);
		}
	}

	@Override
	public void setInverted(boolean isInverted) {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}		leader.setInverted(isInverted);
		if (follower1 != null) {
			follower1.setInverted(isInverted);
		}
		if (follower2 != null) {
			follower2.setInverted(isInverted);
		}
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
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.stopMotor();
		if (follower1 != null) {
			follower1.stopMotor();
		}
		if (follower2 != null) {
			follower2.stopMotor();		
		}
	}

	public boolean isStopped(){
		if (leader == null) {
			LOGGER.trace("No drive system");
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
		if (leader == null) {
			LOGGER.trace("No drive system");
			return 0;
		}
		return leader.getSelectedSensorPosition(0);
	}
	
	public void setOpenLoopRamp(double ramp) {
		if (leader == null) {
			LOGGER.trace("No drive system");
			return;
		}
		leader.configOpenloopRamp(ramp, RobotMap.TALON_TIMEOUT);
	}
}
