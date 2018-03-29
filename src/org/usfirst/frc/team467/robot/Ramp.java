package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class uses a state machine with two states: UP <-> DOWN.
 * Each function checks that it's in the appropriate precondition before it starts,
 * and then sets the appropriate state when it finishes.
 */
public class Ramp {
	
    private static final Logger LOGGER = LogManager.getLogger(Drive.class);
    // Compressor automatically set to closedLoopControl when Solenoid is declared
	private DoubleSolenoid solenoid;

	private String name;
	private State state;

	public enum State {
		UP,
		DOWN,
		NOT_EXIST;
	}

	public Ramp(String name, int forwardChannel, int reverseChannel, boolean exists) {
		if (!exists) {
			LOGGER.info("No ramps");
			state = State.NOT_EXIST;
			return;
		}

		solenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
		this.name = name;
		state = State.DOWN;
		LOGGER.info("Ramp initialized: {}", name);
	}

	public State getState() {
		return state;
	}

	public void lift() {
		if (state == State.DOWN) {
			solenoid.set(DoubleSolenoid.Value.kForward);
			LOGGER.info("Lifting = {}", name);
			state = State.UP;
		}
	}

	public void drop() {
		if (state == State.UP) {
			solenoid.set(DoubleSolenoid.Value.kReverse);
			LOGGER.info("Dropping = {}", name);
			state = State.DOWN;
		}
	}
	
	public void reset() {
		solenoid.set(DoubleSolenoid.Value.kReverse);
		state = State.DOWN;
	}

	public void telemetry() {
		SmartDashboard.putString("Ramps/" + name + "/State", state.name());
	}
}
