package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class GrabberSolenoid{
    DoubleSolenoid solenoid;
    State state;
    
    public enum State {
        OPEN, 
        CLOSE, 
        NONEXISTENT;
    }
    
    public GrabberSolenoid() {
        if(!RobotMap.GRABBER_SOLENOID_EXISTS) {
            state = State.NONEXISTENT;
            return;
        }
        solenoid = new DoubleSolenoid(RobotMap.GRABBER_OPEN_FORWARD_CHANNEL, RobotMap.GRABBER_CLOSE_REVERSE_CHANNEL);
        state = State.CLOSE;
        
    }
    
    public State getGrabberState() {
        return state;
    }
    
    public void open() {
        if(state == State.CLOSE) {
            solenoid.set(DoubleSolenoid.Value.kForward);
            state = State.OPEN;
        }
    }
    
    public void close() {
        if(state == State.OPEN) {
            solenoid.set(DoubleSolenoid.Value.kReverse);
            state = State.CLOSE;
        }
    }
    
    public void reset() {
        close();
        state = State.CLOSE;
    }

}
