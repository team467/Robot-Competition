package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * This class uses a state machine with two states.
 * Each function first checks that it's in the right state,
 * and then sets the right state when it finishes.
 */
public class Ramp {
	private static final Logger LOGGER = Logger.getLogger(Ramp.class);

	// Compressor automatically set to closedLoopControl when Solenoid is declared
	private DoubleSolenoid solenoid;

	private String name;
	private State state;

	/**
	 * Count-down in milliseconds
	 */
	private int time = 0;

	public enum State {
		DOWN,
		UP;
	}

	public Ramp(String name, int forwardChannel, int reverseChannel) {
		if (!RobotMap.HAS_RAMPS) {
			LOGGER.info("No ramps");
			return;
		}

		solenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
		this.name = name;
		state = State.DOWN;
	}

	public void lift() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		if (state == State.DOWN) {
			solenoid.set(Value.kForward);
			LOGGER.info(name + " Lifting ramps");
		}
	}

	public void drop() {
		if (!RobotMap.HAS_RAMPS) {
			return;
		}

		if (state == State.UP) {
			solenoid.set(Value.kReverse);
			LOGGER.info(name + " Dropping ramps");
		}
	}
}
