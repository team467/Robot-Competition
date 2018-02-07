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
		
	public void grab() {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}
		left.set(RobotMap.GRAB_SPEED);
		right.set(RobotMap.GRAB_SPEED);
	}
	
	public void release() {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}
		left.set(RobotMap.RELEASE_SPEED);
		right.set(RobotMap.RELEASE_SPEED);
	}
	
	public void pause() {
		if (!RobotMap.HAS_GRABBER) {
			return;
		}
		left.set(0);
		right.set(0);
	}
	
	public boolean hasCube() {
		if (!RobotMap.HAS_GRABBER) {
			return false;
		}
		return os.detectedTarget();
	}

}
