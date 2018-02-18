package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Solenoid;

public class Ramp {
	private static final Logger LOGGER = Logger.getLogger(Ramp.class);

	// Compressor automatically set to closedLoopControl when Solenoid is declared
	private Solenoid solenoid;

	private String name;

	private boolean isDeployed = false;
	private boolean isLifted = false;

	public Ramp(String name, int solenoidChannel) {
		if (!RobotMap.HAS_RAMPS) {
			LOGGER.debug("No ramps");
			return;
		}

		this.name = name;
		solenoid = new Solenoid(solenoidChannel);
	}

	public void deploy() {
		if (RobotMap.HAS_RAMPS) {
			// TODO Ramp deploy code here, not sure if it's motors or pneumatics yet
			LOGGER.info(name + " Deploying ramps");
			isDeployed = true;
		}
	}

	public void lift() {
		if (RobotMap.HAS_RAMPS && isDeployed  && !isLifted) { // Only lift if deployed
			solenoid.set(true);
			LOGGER.info(name + " Lifting ramps");
			isLifted = true;
		}
	}

	public void drop() {
		if (RobotMap.HAS_RAMPS && isDeployed && isLifted) { // Only drop if deployed
			solenoid.set(false);
			LOGGER.info(name + " Dropping ramps");
			isLifted = false;
		}
	}
}
