package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DoubleSolenoid;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class GrabberSolenoid {
  
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GrabberSolenoid.class.getName());
  //private boolean toggle = false;
  
  DoubleSolenoid solenoid;
  State state;
   
  private static GrabberSolenoid rightInstance;
  private static GrabberSolenoid leftInstance;
  
  public enum State {
      OPEN, 
      CLOSE, 
      NONEXISTENT;
  }
    
  public GrabberSolenoid(String name, int forwardChannel, int reverseChannel, boolean exists) {
    if (!exists) {
      LOGGER.info("Could not create grabber solenoid");
      state = State.NONEXISTENT;
      return;
    }
      
    solenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
    state = State.CLOSE;
    LOGGER.info("Grabber solenoid initialized: {}", name); 
  }
  
  public static GrabberSolenoid getRightInstance() {
    if (rightInstance == null) {
      rightInstance = new GrabberSolenoid("right Grabber", 
          RobotMap.RIGHT_GRABBER_FORWARD_CHANNEL, 
          RobotMap.RIGHT_GRABBER_REVERSE_CHANNEL, 
          RobotMap.RIGHT_GRABBER_SOLENOID_EXISTS);
    }
    return rightInstance;
  }

  public static GrabberSolenoid getLeftInstance() {
    if (leftInstance == null) {
      leftInstance =  new GrabberSolenoid("left Grabber", 
          RobotMap.LEFT_GRABBER_FORWARD_CHANNEL, 
          RobotMap.LEFT_GRABBER_REVERSE_CHANNEL, 
          RobotMap.LEFT_GRABBER_SOLENOID_EXISTS);
    }
    return leftInstance;
  }
  
  public State getGrabberState() {
    return state;
  }

  public boolean exists() {
    return (state != State.NONEXISTENT);
  }

  public void open() {
    if (state == State.CLOSE) {
      LOGGER.info("Grabber Opening");
      solenoid.set(DoubleSolenoid.Value.kReverse);
      state = State.OPEN;
    }
  }
    
  public void close() {
    if (state == State.OPEN) {
      solenoid.set(DoubleSolenoid.Value.kForward);
      LOGGER.info("Grabber Closing");
      state = State.CLOSE;
    }
  }
    
  public void reset() {
    close();
    state = State.CLOSE;
  }
      
}
