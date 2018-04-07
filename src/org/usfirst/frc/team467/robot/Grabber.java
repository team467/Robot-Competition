package org.usfirst.frc.team467.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;

public class Grabber {

	public enum GrabberState {
		START_GRAB,
		GRAB,
		NEUTRAL,
		RELEASE
	}

	public static final int GRAB_TIME_MS = 1000;
	public static final int RELEASE_TIME_MS = 1000;
	private GrabberState state = GrabberState.NEUTRAL;

	private static final Logger LOGGER = LogManager.getLogger(Grabber.class);

	private static Grabber instance;
	private SpeedController left;
	private SpeedController right;
	private boolean hadCube = false;
	private boolean hasCube = false;
	private OpticalSensor os;

	private Grabber() {
		if (RobotMap.HAS_GRABBER && !RobotMap.useSimulator) {
			left = new Spark(RobotMap.GRABBER_L_CHANNEL);
			left.setInverted(RobotMap.GRABBER_INVERT);
			right = new Spark(RobotMap.GRABBER_R_CHANNEL);
			right.setInverted(RobotMap.GRABBER_INVERT);
			os = OpticalSensor.getInstance();
		} else {
			left = new NullSpeedController();
			right = new NullSpeedController();
			os = OpticalSensor.getInstance();
		}

	}

	public static Grabber getInstance() {
		if (instance == null) {
			instance = new Grabber();
		}

		return instance;
	}

	public void periodic() {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}

		double speed = 0.0;
		switch (state) {
		
		case START_GRAB:
			if(hasCube()) {
				state = GrabberState.NEUTRAL;
			}
			else {
				speed = RobotMap.MAX_GRAB_SPEED;
			}
			break;

		case GRAB:
			if(hasCube()) { 
				state = GrabberState.NEUTRAL;
			} else {
				speed = RobotMap.MAX_GRAB_SPEED;
			}
			break;

		case NEUTRAL:
			speed = 0.0;
			break; 

		case RELEASE:
			speed = -RobotMap.MAX_GRAB_SPEED;
			break;

		default:

		}

		if (!RobotMap.useSimulator) {
			left.set(speed);
			right.set(-speed);
		}
		
		// Save the previous state and check for current state.
		hadCube = hasCube;
		hasCube = hasCube();
	}
	
	public void startGrab() {
		state = GrabberState.START_GRAB;
	}

	public void grab() {
		state = GrabberState.GRAB;
	}

	public void release() {
		state = GrabberState.RELEASE;
	}

	public void pause() {
		state = GrabberState.NEUTRAL;
	}

	public void grab(double throttle) {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}

		if (Math.abs(throttle) < RobotMap.MIN_GRAB_SPEED) {
			throttle = 0.0;
		}

		if (!RobotMap.useSimulator) {
			if (DriverStation467.getInstance().getNavJoystick().getLeftStickY() > 0.5 
					|| DriverStation467.getInstance().getNavJoystick().getLeftStickY() < -0.5) {
				DriverStation467.getInstance().getNavRumbler().rumble(25, 0.1);
				DriverStation467.getInstance().getDriverRumbler().rumble(25, 0.1);
				if (hasCube()) {
					DriverStation467.getInstance().getNavRumbler().rumble(150, 1.0);
					DriverStation467.getInstance().getDriverRumbler().rumble(50, 1.0);
				}
			}
		}

		LOGGER.debug("Grabber Throttle= {}", throttle);
		left.set(throttle * RobotMap.MAX_GRAB_SPEED);
		right.set(-throttle * RobotMap.MAX_GRAB_SPEED);

		// Save the previous state and check for current state.
		hadCube = hasCube;
		hasCube = hasCube();
	}

	public boolean justGotCube() {
		if(!RobotMap.useSimulator) {
		return (!hadCube && hasCube());
		}
		
		else return false;
	}

	public boolean hasCube() {
		return (!RobotMap.useSimulator && RobotMap.HAS_GRABBER && os.detectedTarget());
	}
}
