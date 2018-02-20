package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Ramp {
	private static final Logger LOGGER = Logger.getLogger(Ramp.class);

	// Compressor automatically set to closedLoopControl when Solenoid is declared
	private DoubleSolenoid solenoid;

	private String name;

	private boolean isDeployed = false;

	/**
	 * Count-down in milliseconds
	 */
	private int waitTime = 0;

	public Ramp(String name, int forwardChannel, int reverseChannel) {
		if (!RobotMap.HAS_RAMPS) {
			LOGGER.info("No ramps");
			return;
		}

		this.name = name;
		solenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
	}

	public void lift() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		solenoid.set(Value.kForward);
		LOGGER.info(name + " Lifting ramps");
	}

	public void drop() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		solenoid.set(Value.kReverse);
		LOGGER.info(name + " Dropping ramps");
	}
}
