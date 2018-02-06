/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

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
 *
 */
public class Motor extends WPI_TalonSRX implements SpeedController, Sendable, MotorSafety, 
IFollower, IMotorController, IMotorControllerEnhanced, IInvertable, IOutputSignal {

	public Motor(int deviceNumber) {
		super(deviceNumber);
		// TODO Auto-generated constructor stub
	}

}
