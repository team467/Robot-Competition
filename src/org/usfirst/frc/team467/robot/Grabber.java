package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Spark;

public class Grabber {
	//private cube sensor
	//8 pounds 
    private static Grabber instance;
    private Spark left;
	private Spark right;
	OpticalSensor os;
	
	
	public final static double GRAB_SPEED = 1.0;
	public final static double RELEASE_SPEED = -1.0;
	public final static int GRABBER_L_PORT = 1; 
    public final static int GRABBER_R_PORT = 2;
    
    public boolean gotCube = false;
	// 1 and 2 are place-holders
	
	
	private Grabber() {
		left = new Spark(GRABBER_L_PORT);
		right = new Spark(GRABBER_R_PORT);
		os = new OpticalSensor();
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
	
	public boolean hasCube() {
		if(os.hasCube()) {
			gotCube = true;
		}
		else {
			gotCube = false;
		}
		
		return gotCube;
	}

}
