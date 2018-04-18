package org.usfirst.frc.team467.robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.wpilibj.Solenoid;

public class GrabberSolenoid{
	private static final Logger LOGGER = LogManager.getLogger(GrabberSolenoid.class);
	private boolean toggle = false;
	
    Solenoid solenoid;
    State state;
   
	private static GrabberSolenoid instance;
	
    public enum State {
        OPEN, 
        CLOSE, 
        NONEXISTENT;
    }
    
    public static GrabberSolenoid getInstance() {
		if (instance == null) {
			instance = new GrabberSolenoid();
		}
		return instance;
    }
    public GrabberSolenoid() {
        if(!RobotMap.GRABBER_SOLENOID_EXISTS) {
        	LOGGER.info("Could not detect grabber solenoids");
            state = State.NONEXISTENT;
            return;
        }
        solenoid = new Solenoid(RobotMap.GRABBER_OPEN_CHANNEL);
        state = State.CLOSE;
        
    }
    
    public State getGrabberState() {
        return state;
    }
    
    public void open() {
        if(state == State.CLOSE) {
        	LOGGER.info("Grabber Opening");
            solenoid.set(true);
            state = State.OPEN;
        }
    }
    
    public void close() {
        if(state == State.OPEN) {
            solenoid.set(false);
            LOGGER.info("Grabber Closing");
            state = State.CLOSE;
        }
    }
    
    public void reset() {
        close();
        state = State.CLOSE;
    }
    	
}
