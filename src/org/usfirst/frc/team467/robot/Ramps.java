package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class uses a state machine with three states: START -> RELEASED -> DEPLOYED.
 * Each function checks that it's in the appropriate precondition before it starts,
 * and then sets the appropriate state when it finishes.
 */
public class Ramps {
	private static final Logger LOGGER = LogManager.getLogger(Ramps.class);
	private static Ramps instance;

	private Ramp left;
	private Ramp right;
	private DoubleSolenoid releaseSolenoid;
	private State state = State.START;

	public enum State {
		START,
		DEPLOYED,
		NOT_EXIST;
	}

	private Ramps() {
		if (!RobotMap.HAS_LEFT_RAMP && !RobotMap.HAS_RIGHT_RAMP) {
			state = State.NOT_EXIST;
			return;
		}
		releaseSolenoid = new DoubleSolenoid(RobotMap.RAMP_RELEASE_FORWARD_CHANNEL, RobotMap.RAMP_RELEASE_REVERSE_CHANNEL);
		left = new Ramp("Left", RobotMap.RAMP_LEFT_FORWARD_CHANNEL, RobotMap.RAMP_LEFT_REVERSE_CHANNEL, RobotMap.HAS_LEFT_RAMP);
		right = new Ramp("Right", RobotMap.RAMP_RIGHT_FORWARD_CHANNEL, RobotMap.RAMP_RIGHT_REVERSE_CHANNEL, RobotMap.HAS_RIGHT_RAMP);
	}

	public static Ramps getInstance() {
		if (instance == null) {
			instance = new Ramps();
		}
		return instance;
	}

	public void deploy() {
		if (DriverStation.getInstance().getMatchTime() > 30.0) {
			// Nothing gets past here unless you are in the last 30 seconds.
			return;
		}

		if (state != State.START) {
			// Only deploy from start configuration.
			return;
		}

		releaseSolenoid.set(DoubleSolenoid.Value.kReverse);
		state = State.DEPLOYED;
		LOGGER.info("Deploying");
	}

	// All functions below do not work if the ramps are not deployed
	public void liftLeft() {
		if (state != State.DEPLOYED) {
			return;
		}

		left.lift();
	}

	public void dropLeft() {
		if (state != State.DEPLOYED) {
			return;
		}

		left.drop();
	}

	public void liftRight() {
		if (state != State.DEPLOYED) {
			return;
		}

		right.lift();
	}

	public void dropRight() {
		if (state != State.DEPLOYED) {
			return;
		}

		right.drop();
	}

	public void reset() {
		if (!RobotMap.HAS_LEFT_RAMP || !RobotMap.HAS_RIGHT_RAMP) {
			return;
		}

		state = State.START;
		releaseSolenoid.set(DoubleSolenoid.Value.kForward);
		left.reset();
		right.reset();
	}

	public boolean isDeployed() {
		return state == State.DEPLOYED;
	}

	public void telemetry() {
		SmartDashboard.putString("Ramps/State", state.name());
		left.telemetry();
		right.telemetry();
	}
}
