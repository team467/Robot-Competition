package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Spark;

public class Grabber {
    private static Grabber instance;
    private Spark left;
	private Spark right;
	OpticalSensor os;
	
	public final static double GRAB_SPEED = 1.0;
	public final static double RELEASE_SPEED = -1.0;
	public final static int GRABBER_L_CHANNEL = 1; 
    public final static int GRABBER_R_CHANNEL = 2;
	
	private Grabber() {
		left = new Spark(GRABBER_L_CHANNEL);
		right = new Spark(GRABBER_R_CHANNEL);
		os = OpticalSensor.getInstance();
	}
	
	public static Grabber getInstance() {
		if(instance == null) {
			instance = new Grabber(); 
		}
		
		return instance;
	}
	
	//speeds will be determined by map 
	
	public void grab() {
		left.set(GRAB_SPEED);
		right.set(GRAB_SPEED);
	}
	
	public void release() {
		left.set(RELEASE_SPEED);
		right.set(RELEASE_SPEED);
	}
	
	public void pause() {
		left.set(0);
		right.set(0);
	}
	
	public boolean hasCube() {
		return os.detectedTarget();
	}

}
