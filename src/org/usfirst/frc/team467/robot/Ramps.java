package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
		DEPLOYED,
		NOT_EXIST;
	}

	/**
	 * Time in milliseconds
	 */
	private int timeSinceRelease;

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

		releaseSolenoid.set(DoubleSolenoid.Value.kForward);
		state = State.RELEASED;
		timeSinceRelease = 0;
		LOGGER.info("Deploying");
	}

	public void periodic() {
		telemetry();

		if (state != State.RELEASED) {
			return;
		}

		if (timeSinceRelease >= 200) { // This code takes priority
			left.drop();
			right.drop();
			LOGGER.info("Deployed");
			state = State.DEPLOYED;
			return;
		} else if (timeSinceRelease >= 100) { // This code called first
			left.lift();
			right.lift();
			LOGGER.info("Kicked");
		}

		timeSinceRelease += 20; // 20 ms per iteration
		LOGGER.trace("time=" + timeSinceRelease);
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

	public void reset() {
		state = State.START;
		timeSinceRelease = 0;
		left.drop();
		right.drop();
	}

	public void telemetry() {
		SmartDashboard.putString("Ramps/State", state.name());
		SmartDashboard.putNumber("Ramps/Time Since Release", timeSinceRelease);
		left.telemetry();
		right.telemetry();
	}
}
