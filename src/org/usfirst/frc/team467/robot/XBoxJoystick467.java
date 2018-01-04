/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import java.lang.Math;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 */
public class XBoxJoystick467 {
	private Joystick joystick;
	private int pov = 0;

	private static final double DEADZONE = 0.1;

	public enum Button {
		a(1),
		b(2),
		x(3),
		y(4),
		bumperLeft(5),
		BumperRight(6),
		back(7),
		start(8),
		left(9),
		right(10);
		
		public boolean isPressed;		
		public boolean wasPressed;
		
		public final int channel;
		
		Button(int channel) {
			this.channel = channel;
			isPressed = false;
			wasPressed = false;
		}
		
		public static void read() {
			// TODO Iterate over the enum, updating all the values. 
			// Store the current value into the previous state, then read the raw button value
		}
		
		/**
		 * Check if a specific button is being held down. Ignores first button press, but the robot loops too quickly for this to
		 * matter.
		 *
		 * @return
		 */
		public boolean down() {
			// TODO: Return if the button is currently down
			return false;
		}

		/**
		 * Check if a specific button has just been pressed. (Ignores holding.)
		 *
		 * @return
		 */
		public boolean pressed() {
			// TODO: return true if the button is pressed, but wasn't before
			return false;
		}

		/**
		 * Check if a specific button has just been released.
		 *
		 * @return
		 */
		public boolean buttonReleased() {
			// TODO: Reverse of above
			return false;
		}


		
	}
	
	private enum Axis {
		leftX(0),
		leftY(1),
		leftTrigger(2),
		rightTrigger(3),
		rightX(4),
		rightY(5);
		
		public final int channel;
		
		private double value;
		
		Axis(int channel) {
			this.channel = channel;
			value = 0.0;
		}
		
		public double value() {
			return value;
		}
		
		public static void read(Joystick joystick) {
			// TODO Traverse the enum and read all the values
			// Example --> value = accelerateJoystickInput(joystick.getRawAxis(channel));
		}
		
		/**
		 * Implement a dead zone for Joystick centering - and a non-linear acceleration as the user moves away from the zero position.
		 *
		 * @param input
		 * @return processed input
		 */
		private double accelerateJoystickInput(double input) {
			// Ensure that there is a dead zone around zero
			if (Math.abs(input) < DEADZONE) {
				return 0.0;
			}
			// Simply square the input to provide acceleration
			// ensuring that the sign of the input is preserved
			return (input * Math.abs(input));
		}


	}

	/**
	 * Create a new joystick on a given channel
	 *
	 * @param stick
	 */
	public XBoxJoystick467(int stick) {
		// TODO: Set a new joystick on the given channel
	}

	/**
	 * Returns the raw joystick object inside Joystick467
	 *
	 * @return
	 */
	public Joystick getJoystick() {
		// TODO: Get the joystick
		return joystick;
	}

	/**
	 * Read all inputs from the underlying joystick object.
	 */
	public void read() {
		// TODO: Store the current button state into the previous state, then read the raw button
		// TODO Read all the joystick axis into the values
		pov = joystick.getPOV(0);
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
		return 0;
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
		return 0;
	}

}