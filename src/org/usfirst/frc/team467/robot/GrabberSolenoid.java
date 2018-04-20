package org.usfirst.frc.team467.robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class GrabberSolenoid{
	private static final Logger LOGGER = LogManager.getLogger(GrabberSolenoid.class);
	//private boolean toggle = false;
	
    DoubleSolenoid solenoid;
    DoubleSolenoid solenoidLeft;
    State state;
   
	private static GrabberSolenoid instance;
	
    public enum State {
        OPEN, 
        CLOSE, 
        NONEXISTENT;
    }
    
//    public static GrabberSolenoid getInstance() {
//		if (instance == null) {
//			instance = new GrabberSolenoid();
//		}
//		return instance;
//    }
    public GrabberSolenoid(String name, int forwardChannel, int reverseChannel, boolean exists) {
        if(!exists) {
        	LOGGER.info("Could not detect grabber solenoids");
            state = State.NONEXISTENT;
            return;
        }
        
        solenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
        state = State.CLOSE;
    	LOGGER.info("Grabber solenoid initialized: {}", name); 
    }
    
    public State getGrabberState() {
        return state;
    }
    public boolean exists() {
    	return (state != State.NONEXISTENT);
    }
    public void open() {
        if(state == State.CLOSE) {
        	LOGGER.info("Grabber Opening");
            solenoid.set(DoubleSolenoid.Value.kForward);
            state = State.OPEN;
        }
    }
    
    public void close() {
        if(state == State.OPEN) {
            solenoid.set(DoubleSolenoid.Value.kReverse);
            LOGGER.info("Grabber Closing");
            state = State.CLOSE;
        }
    }
    
    public void reset() {
        close();
        state = State.CLOSE;
    }
    	
}
