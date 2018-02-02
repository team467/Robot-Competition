/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import java.lang.Math;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 *
 */
public class XBoxJoystick467 {
	private XboxController xbox;
	private int pov = 0;

	private static final double DEADZONE = 0.1;

	private static final double SENSITIVITY_MODIFIER = 0.6;
	
	public static boolean[] buttonDown = new boolean[10]; 
	public static boolean[] prev_buttonDown = new boolean[10];
	
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
		
		public boolean isPressed;		
		public boolean wasPressed;
		
		public final int channel;
		
		Button(int channel) {
			this.channel = channel;
			isPressed = false;
			wasPressed = false;
		}
	}
		
//			// Button Enum read
//			// TODO Iterate over the enum, updating all the values. 
//			// Store the current value into the previous state, then read the raw button value
		
		/**
		 * Check if a specific button is being held down. Ignores first button press, but the robot loops too quickly for this to
		 * matter.
		 *
		 * @return
		 */
		public boolean down(Button b) {
			// TODO: Return if the button is currently down
			return buttonDown[b.ordinal()];
		}

		/**
		 * Check if a specific button has just been pressed. (Ignores holding.)
		 *
		 * @return
		 */
		public boolean pressed(Button b) {
			// TODO: return true if the button is pressed, but wasn't before
			return buttonDown[b.ordinal()] && !prev_buttonDown[b.ordinal()];
		}

		/**
		 * Check if a specific button has just been released.
		 *
		 * @return
		 */
		public boolean buttonReleased(Button b) {
			// TODO: Reverse of above
			return !buttonDown[b.ordinal()] && prev_buttonDown[b.ordinal()];
		}
	
	private enum Axis {
		leftX(0),
		leftY(1),
		leftTrigger(2),
		rightTrigger(3),
		RightX(4),
		RightY(5);
		
		public final int channel;
		
		private double value;
		
		Axis(int channel) {
			this.channel = channel;
			value = 0.0;
		}
		
		public double value() {
			return value;
		}
		
		public static void read(XboxController xbox) {
			// TODO Traverse the enum and read all the values
			// Example --> value = accelerateJoystickInput(joystick.getRawAxis(channel));
			
			for(int i = 0; i < buttonDown.length; i++) {
				prev_buttonDown[i] = buttonDown[i];
				buttonDown[i] = xbox.getRawButton(i);
				
			}
			
			
			// Read Joystick Axes
			leftX.value = accelerateJoystickInput(xbox.getX(GenericHID.Hand.kLeft));
			leftY.value = accelerateJoystickInput(xbox.getY(GenericHID.Hand.kLeft));

			RightX.value = accelerateJoystickInput(xbox.getX(GenericHID.Hand.kRight));
			RightY.value = accelerateJoystickInput(xbox.getY(GenericHID.Hand.kRight));

			leftTrigger.value = accelerateJoystickInput(xbox.getTriggerAxis(GenericHID.Hand.kLeft));
			rightTrigger.value = accelerateJoystickInput(xbox.getTriggerAxis(GenericHID.Hand.kRight));
		
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

	}

	/**
	 * Create a new joystick on a given channel
	 *
	 * @param stick
	 */
	public XBoxJoystick467(int stick) {
		// TODO: Set a new joystick on the given channel
		xbox = new XboxController(stick);
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

	/**
	 * Read all inputs from the underlying joystick object.
	 */
	public void read() {
		// TODO: Store the current button state into the previous state, then read the raw button
		// TODO Read all the joystick axis into the values
		Axis.read(xbox);
		pov = xbox.getPOV(0);
	}
	
	public double turboSpeedAdjust() {
		if (Axis.leftTrigger.value() > 0.0) {
			return turboFastSpeed(); 
		} else {
			return turboSlowSpeed(); 
		}	
	}
	
	public double turboFastSpeed() {

		return (getLeftStickY()*(RobotMap.NORMAL_MAX_SPEED 
				+ (RobotMap.FAST_MAX_SPEED-RobotMap.NORMAL_MAX_SPEED)
				*Axis.leftTrigger.value()))
				*-1; // For some reason, up stick is negative, so we flip it;
	}
	
	public double turboSlowSpeed() {

		return (getLeftStickY()*(RobotMap.NORMAL_MAX_SPEED 
				+ (RobotMap.SLOW_MAX_SPEED-RobotMap.NORMAL_MAX_SPEED)
				*Axis.rightTrigger.value()))
				*-1; // For some reason, up stick is negative, so we flip it;
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
		return Math.sqrt((Axis.leftX.value * Axis.leftX.value) + (Axis.leftY.value * Axis.leftY.value));
	}

	public double getRightStickDistance() {
		// TODO Repeat for right
		return Math.sqrt((Axis.RightX.value * Axis.RightX.value) + (Axis.RightY.value * Axis.RightY.value));
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
		return Axis.leftY.value();
	}
	
	public double getLeftStickX() {
		return Axis.leftX.value();
	}
	
	public double getRightStickY() {
		return Axis.RightY.value();
	}
	
	public double getRightStickX() {
		return Axis.RightX.value();
	}

	/**
	 * Calculate the angle of this joystick.
	 *
	 * @return Joystick Angle in range -PI to PI
	 */
	public double getLeftStickAngle() {
		return (calculateStickAngle(Axis.leftX.value, Axis.leftY.value));
	}

	public double getRightStickAngle() {
		// TODO Repeat for right stick
		return (calculateStickAngle(Axis.RightX.value, Axis.RightY.value));
	}
	
	public void leftRumble(double value) {
		xbox.setRumble(RumbleType.kLeftRumble, value);
	}
	
	public void rightRumble(double value) {
		xbox.setRumble(RumbleType.kRightRumble, value);
	}


}