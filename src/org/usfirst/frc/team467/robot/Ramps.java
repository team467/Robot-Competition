package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class uses a state machine with three states: START, RELEASED, and DEPLOYED.
 * Each function checks that it's in the appropriate precondition before it starts,
 * and then sets the appropriate state when it finishes.
 */
public class Ramps {
	private static final Logger LOGGER = Logger.getLogger(Ramps.class);
	private static Ramps instance;

	private Ramp left;
	private Ramp right;
	private DoubleSolenoid release;
	private State state = State.START;

	public enum State {
		START,
		RELEASED,
		DEPLOYED;
	}

	/**
	 * Time in milliseconds
	 */
	private int time = -1;

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
		if (state != State.START && DriverStation.getInstance().getMatchTime() > 30) {
			// Only deploy from start configuration in the last 30 seconds
			return;
		}

		release.set(Value.kForward);
		state = State.RELEASED;
		time = 0;
	}

	public void periodic() {
		if (state != State.RELEASED) {
			return;
		}

		time += 20; // 20 ms per iteration
		LOGGER.debug("time=" + time);

		if (time >= 200) { // This code takes priority
			left.drop();
			right.drop();
			state = State.DEPLOYED;
		} else if (time >= 100) { // This code called first
			left.lift();
			right.lift();
		}
	}

	// All functions below do not work if the ramps are not deployed
	public void leftLift() {
		if (state != State.DEPLOYED) {
			return;
		}

		left.lift();
	}

	public void leftDrop() {
		if (state != State.DEPLOYED) {
			return;
		}

		left.drop();
	}

	public void rightLift() {
		if (state != State.DEPLOYED) {
			return;
		}

		right.lift();
	}

	public void rightDrop() {
		if (state != State.DEPLOYED) {
			return;
		}

		right.drop();
	}
}
