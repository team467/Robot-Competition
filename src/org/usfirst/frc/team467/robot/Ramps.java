package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Solenoid;

public class Ramps {
	private static final Logger LOGGER = Logger.getLogger(Ramps.class);

	private static Ramps instance;

	// Compressor automatically set to closedLoopControl when Solenoid is declared
	private Solenoid leftSolenoid;
	private Solenoid rightSolenoid;

	private boolean isDeployed = false;

	private Ramps() {
		if (!RobotMap.HAS_RAMPS) {
			LOGGER.debug("No ramps");
			return;
		}

		leftSolenoid = new Solenoid(RobotMap.RAMP_LEFT_SOLENOID_CHANNEL);
		rightSolenoid = new Solenoid(RobotMap.RAMP_RIGHT_SOLENOID_CHANNEL);
	}

	public static Ramps getInstance() {
		if (instance == null) {
			instance = new Ramps();
		}
		return instance;
	}

	public void deploy() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		// TODO Ramp deploy code here, not sure if it's motors or pneumatics yet
		LOGGER.info("Deploying ramps");
		isDeployed = true;
	}

	public void lift() {
		if (RobotMap.HAS_RAMPS && isDeployed) { // Only lift if deployed
			leftSolenoid.set(true);
			rightSolenoid.set(true);
			LOGGER.info("Lifting ramps");
		}
	}

	public void drop() {
		if (RobotMap.HAS_RAMPS) {
			leftSolenoid.set(false);
			rightSolenoid.set(false);
			LOGGER.info("Dropping ramps");
		}
	}
}
