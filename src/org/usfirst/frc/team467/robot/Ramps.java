package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class uses a state machine with three states: START -> RELEASED -> DEPLOYED.
 * Each function checks that it's in the appropriate precondition before it starts,
 * and then sets the appropriate state when it finishes.
 */
public class Ramps {
	private static final Logger LOGGER = Logger.getLogger(Ramps.class);
	private static Ramps instance;

	private Ramp left;
	private Ramp right;
	private DoubleSolenoid releaseSolenoid;
	private State state = State.START;

	public enum State {
		START,
		RELEASED,
		DEPLOYED;
	}

	/**
	 * Time in milliseconds
	 */
	private int timeSinceRelease = -1;

	private Ramps() {
		left = new Ramp("Left Ramp", RobotMap.RAMP_LEFT_FORWARD_CHANNEL, RobotMap.RAMP_LEFT_REVERSE_CHANNEL);
		right = new Ramp("Right Ramp", RobotMap.RAMP_RIGHT_FORWARD_CHANNEL, RobotMap.RAMP_RIGHT_REVERSE_CHANNEL);
		releaseSolenoid = new DoubleSolenoid(RobotMap.RAMP_RELEASE_FORWARD_CHANNEL, RobotMap.RAMP_RELEASE_REVERSE_CHANNEL);
	}

	public static Ramps getInstance() {
		if (instance == null) {
			instance = new Ramps();
		}
		return instance;
	}

	public void deploy() {
		if (state != State.START && DriverStation.getInstance().getMatchTime() > 30) {
			// Only deploy from start configuration in the last 30 seconds
			return;
		}

		releaseSolenoid.set(DoubleSolenoid.Value.kForward);
		state = State.RELEASED;
		timeSinceRelease = 0;
		LOGGER.info("Deploying");
	}

	public void periodic() {
		if (state != State.RELEASED) {
			return;
		}

		timeSinceRelease += 20; // 20 ms per iteration
		LOGGER.trace("time=" + timeSinceRelease);

		if (timeSinceRelease >= 200) { // This code takes priority
			left.toggle();
			right.toggle();
			LOGGER.info("Deployed");
			state = State.DEPLOYED;
		} else if (timeSinceRelease >= 100) { // This code called first
			left.lift();
			right.lift();
		}
	}

	// All functions below do not work if the ramps are not deployed
	public void toggleLeftState() {
		if (state != State.DEPLOYED) {
			return;
		}

		left.toggle();
	}

	public void toggleRightState() {
		if (state != State.DEPLOYED) {
			return;
		}

		right.toggle();
	}
}
