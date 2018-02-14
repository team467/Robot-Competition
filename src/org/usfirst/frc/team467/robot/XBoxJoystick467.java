/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import java.lang.Math;
import java.util.EnumMap;

import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 *
 */
public class XBoxJoystick467 {

	private static final Logger LOGGER = Logger.getLogger(XBoxJoystick467.class);
	private XboxController xbox;
	private String name;
	private int pov = 0;

	private static final double DEADZONE = 0.1;
	
    private static final double SENSITIVITY_MODIFIER = 0.6;
    
    public EnumMap<Button, Boolean> buttonDown = new EnumMap<>(Button.class);
    public EnumMap<Button, Boolean> prev_buttonDown = new EnumMap<>(Button.class);
    
    public EnumMap<Axis, Double> axes = new EnumMap<>(Axis.class);
    
    public enum Button {
        a(0),
        b(1),
        x(2),
        y(3),
        BumperLeft(4),
        BumperRight(5),
        back(6),
        start(7),
        left(8),
        right(9);
        
        public final int channel;
        
        Button(int channel) {
            this.channel = channel;
        }
    }
    
    /**
     * Check if a specific button is being held down. Ignores first button press, but the robot loops too quickly for this to
     * matter.
     *
     * @return
     */
    public boolean down(Button b) {
        // TODO: Return if the button is currently down
        return buttonDown.get(b);
    }

    /**
     * Check if a specific button has just been pressed. (Ignores holding.)
     *
     * @return
     */
    public boolean pressed(Button b) {
        // TODO: return true if the button is pressed, but wasn't before
        return buttonDown.get(b) && !prev_buttonDown.get(b);
    }

    /**
     * Check if a specific button has just been released.
     *
     * @return
     */
    public boolean buttonReleased(Button b) {
        // TODO: Reverse of above
        return !buttonDown.get(b) && !prev_buttonDown.get(b);
    }

    private enum Axis {
        leftX(0),
        leftY(1),
        leftTrigger(2),
        rightTrigger(3),
        rightX(4),
        rightY(5);
        
        public final int channel;
                
        Axis(int channel) {
            this.channel = channel;
        }
        
    }
        
    /**
     * Implement a dead zone for Joystick centering - and a non-linear acceleration as the user moves away from the zero position.
     *
     * @param input
     * @return processed input
     */
    private static double accelerateJoystickInput(double input) {
        // Ensure that there is a dead zone around zero
        if (Math.abs(input) < DEADZONE) {
            return 0.0;
        }
        // Simply square the input to provide acceleration
        // ensuring that the sign of the input is preserved
        return (input * Math.abs(input));
    }

    private static double limitSensitivity(double input) {
        return input * SENSITIVITY_MODIFIER;
    }

    /**
     * Create a new joystick on a given channel
     *
     * @param stick
     */
    public XBoxJoystick467(int stick, String name) {
        // TODO: Set a new joystick on the given channel
        xbox = new XboxController(stick);
        this.name = name;
    }

    /**
     * Returns the raw joystick object inside Joystick467
     *
     * @return
     */
    public XboxController getJoystick() {
        // TODO: Get the joystick
        return xbox;
    }
    
    private void readButtons() {
    	for (Button b : Button.values()) {
    		prev_buttonDown.put(b, buttonDown.get(b));
    		buttonDown.put(b, xbox.getRawButton(b.channel));
    	}
    }
    
    private void readAxes() {
        for (Axis axis : Axis.values()) {
            axes.put(axis, xbox.getRawAxis(axis.channel));
        }
    }
    
    /**
     * Read all inputs from the underlying joystick object.
     */
    public void read() {
        readButtons();
        readAxes();
        pov = xbox.getPOV(0);
    }
    
    public void logIdentity() {
    	LOGGER.debug(name + " Port: " + xbox.getPort());
    }
    
    public double turboSpeedAdjust() {
        if (getLeftTrigger() > 0.0) {
            return turboFastSpeed(); 
        } else {
            return turboSlowSpeed(); 
        }   
    }
    
    public double turboFastSpeed() {
        return (getLeftStickY()*(RobotMap.NORMAL_MAX_SPEED 
                + (RobotMap.FAST_MAX_SPEED-RobotMap.NORMAL_MAX_SPEED)
                * getLeftTrigger()))
                * -1; // For some reason, up stick is negative, so we flip it;
    }
    
    public double turboSlowSpeed() {
        return (getLeftStickY()*(RobotMap.NORMAL_MAX_SPEED 
                + (RobotMap.SLOW_MAX_SPEED-RobotMap.NORMAL_MAX_SPEED)
                * getRightTrigger()))
                * -1; // For some reason, up stick is negative, so we flip it;
    }

    public double getPOV() {
        return pov;
    }

    /**
     * Calculate the distance of this stick from the center position.
     *
     * @return
     */
    public double getLeftStickDistance() {
        return Math.sqrt((getLeftStickX() * getLeftStickX()) + (getLeftStickY() * getLeftStickY()));
    }

    public double getRightStickDistance() {
    	return Math.sqrt((getRightStickX() * getRightStickX()) + (getRightStickY() * getRightStickY()));
    }

    private double calculateStickAngle(double stickX, double stickY) {
        if (stickY == 0.0) {
            // In Y deadzone avoid divide by zero error
            return (stickX > 0.0) ? Math.PI / 2 : -Math.PI / 2;
        }

        // Return value in range -PI to PI
        double stickAngle = LookUpTable.getArcTan(stickX / -stickY);

        if (stickY > 0) {
            stickAngle += (stickX > 0) ? Math.PI : -Math.PI;
        }

        return (stickAngle);
    }
    
    public double getLeftStickY() {
    	return axes.get(Axis.leftY);
    }
    
    public double getLeftStickX() {
        return axes.get(Axis.leftX);
    }
    
    public double getRightStickY() {
        return axes.get(Axis.rightY);
    }
    
    public double getRightStickX() {
        return axes.get(Axis.rightX);
    }
    
    public double getLeftTrigger() {
    	return axes.get(Axis.leftTrigger);
    }
    
    public double getRightTrigger() {
    	return axes.get(Axis.rightTrigger);
    }

    /**
     * Calculate the angle of this joystick.
     *
     * @return Joystick Angle in range -PI to PI
     */
    public double getLeftStickAngle() {
        return calculateStickAngle(getLeftStickX(), getLeftStickY());
    }

    public double getRightStickAngle() {
        // TODO Repeat for right stick
        return calculateStickAngle(getRightStickX(), getRightStickY());
    }
    
    public void leftRumble(double value) {
        xbox.setRumble(RumbleType.kLeftRumble, value);
    }
    
    public void rightRumble(double value) {
        xbox.setRumble(RumbleType.kRightRumble, value);
    }
    
    public void setRumble(RumbleType type, double value) {
        xbox.setRumble(type, value);
    }
    
    public void setRumble(double value) {
        xbox.setRumble(RumbleType.kLeftRumble, value);
        xbox.setRumble(RumbleType.kRightRumble, value);
    }
}
