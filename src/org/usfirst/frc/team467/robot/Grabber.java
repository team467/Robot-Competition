package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Spark;

public class Grabber {
    private static Grabber instance;
    private Spark left;
	private Spark right;
	OpticalSensor os;
	
	private Grabber() {
		left = new Spark(RobotMap.GRABBER_L_CHANNEL);
		right = new Spark(RobotMap.GRABBER_R_CHANNEL);
		os = OpticalSensor.getInstance();
	}
	
	public static Grabber getInstance() {
		if(instance == null) {
			instance = new Grabber(); 
		}
		
		return instance;
	}
		
	public void grab(double throttle) {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}
		
		if (Math.abs(throttle) < RobotMap.MIN_GRAB_SPEED) {
			throttle = 0.0;
		}
		
		left.set(throttle * RobotMap.MAX_GRAB_SPEED);
		right.set(throttle * RobotMap.MAX_GRAB_SPEED);
	}
	
	public boolean hasCube() {
		if (!RobotMap.HAS_GRABBER) {
			return false;
		}
		return os.detectedTarget();
	}

}
