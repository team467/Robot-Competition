package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;

public class Ramps {
	private static final Logger LOGGER = Logger.getLogger(Ramps.class);
	private static Ramps instance;

	public Ramp left;
	public Ramp right;
	private DoubleSolenoid release;

	private boolean isDeployed = false;

	/**
	 * Count-down in milliseconds
	 */
	private int waitTime = 999999;

	private Ramps() {
		left = new Ramp("Left Ramp", RobotMap.RAMP_LEFT_FORWARD_CHANNEL, RobotMap.RAMP_LEFT_REVERSE_CHANNEL);
		right = new Ramp("Right Ramp", RobotMap.RAMP_RIGHT_FORWARD_CHANNEL, RobotMap.RAMP_RIGHT_REVERSE_CHANNEL);
		release = new DoubleSolenoid(RobotMap.RAMP_RELEASE_FORWARD_CHANNEL, RobotMap.RAMP_RELEASE_REVERSE_CHANNEL);
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

		if (DriverStation.getInstance().getMatchTime() > 30) {
			// Only allow deployment in last 30 seconds.
			return;
		}

		LOGGER.info("Deploying");
		waitTime = 1000;
	}

	public void periodic() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		if (DriverStation.getInstance().getMatchTime() > 30) {
			// Only allow deployment in last 30 seconds.
			return;
		}

		if (waitTime > 0) {
			// It's not time to deploy yet.
			release.set(Value.kForward);
			left.lift();
			right.lift();
			waitTime -= 20; // 20 ms per iteration
			return;
		}

		if (!isDeployed) {
			left.drop();
			right.drop();
			waitTime = 0;
			isDeployed = true;
		} else if (isDeployed) {
			if (DriverStation467.getInstance().getLeftRampButton()) {
				left.lift();
				left.drop();
			}

			if (DriverStation467.getInstance().getRightRampButton()) {
				right.lift();
				right.drop();
			}
		}
	}
}
