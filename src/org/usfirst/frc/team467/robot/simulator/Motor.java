/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IFollower;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.signals.IInvertable;
import com.ctre.phoenix.signals.IOutputSignal;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Class for simulated or actual motor. This will define a pass-through motor that will check
 * for the presence of motors and call if they are available. It will also enable the use of the
 * simulator.
 *
 */
public class Motor extends WPI_TalonSRX implements SpeedController, Sendable, MotorSafety, 
IFollower, IMotorController, IMotorControllerEnhanced, IInvertable, IOutputSignal {

	public static final double MAX_RPM = 821;
	
	private double maxFeetPerPeriod; // Period is 20 ms
	
	RobotData data = RobotData.getInstance();
	
	private double positionReading;
	
	private double absolutePositionReadingOffset;
	
	public Motor(int deviceNumber) {
		super(deviceNumber);
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * MAX_RPM / (20 * 60 / 1000); // One move every 20 ms
		absolutePositionReadingOffset = 0.0;
	}
	
	// Start overridden methods
	public ErrorCode setSelectedSensorPosition(int sensorPosition, int closedLoopSelect, int timeoutMs) {
		if (RobotMap.useSimulator) {
			absolutePositionReadingOffset = positionReading;
			positionReading = sensorPosition;
		}
		if (RobotMap.HAS_WHEELS) {
			return super.setSelectedSensorPosition(sensorPosition, closedLoopSelect, timeoutMs);
		} else {
			return ErrorCode.OK;
		}
	}
	
	public double absolutePosition() {
		return absolutePositionReadingOffset + positionReading;
	}
	
	public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
		if (percentOfMaxSpeed < 0) {
			percentOfMaxSpeed = 0;
		} else if (percentOfMaxSpeed > 1) {
			percentOfMaxSpeed = 1;
		}
		maxFeetPerPeriod = RobotMap.WHEEL_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
	}
	
	public void set(ControlMode mode, double outputValue) {
		if (RobotMap.useSimulator) {
			if (mode == ControlMode.MotionMagic || mode == ControlMode.Position) {
				if (Math.abs((outputValue - positionReading)) > maxFeetPerPeriod) {
					if (outputValue < 0) {
						positionReading -= maxFeetPerPeriod;
					} else {
						positionReading += maxFeetPerPeriod;
					}
				} else {
					positionReading = outputValue;
				}				
			} else if (mode == ControlMode.PercentOutput || mode == ControlMode.Velocity) {
				positionReading += outputValue * maxFeetPerPeriod;				
			}
		} else if (RobotMap.HAS_WHEELS) {
			super.set(mode, outputValue);
			positionReading = super.getSelectedSensorPosition(0);
		}
	}
	
	public int getSelectedSensorPosition(int pidIdx) {
		if (RobotMap.useSimulator) {
			return (int) (positionReading * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION / RobotMap.WHEEL_CIRCUMFERENCE);
		} else if (RobotMap.HAS_WHEELS) {
			return super.getSelectedSensorPosition(pidIdx);
		} else {
			return 0;
		}
	}

	
}
