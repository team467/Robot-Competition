package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;

public class DriverStation {
	
	
	// Create class variable for Singleton instance
	// TODO: implement Singleton
	
	// Mapping of functions to Joystick Buttons for normal operation
	// TODO: Create enum for buttons
	
	private static DriverStation instance;
	private XBoxJoystick467 joystick;
	
	/**
	 * Singleton instance of the object.
	 *
	 * @return
	 */
	public static DriverStation getInstance() {
		if (instance == null) {
			instance = new DriverStation();
		}
		return instance;
	}

	/**
	 * Private constructor
	 */
	private DriverStation() {
		joystick = new XBoxJoystick467(0);
	}

	/**
	 * Must be called prior to first button read.
	 */
	public void readInputs() {
		// TODO: Read inputs from the buttons
	}

	/**
	 * Gets joystick instance used by driver.
	 *
	 * @return
	 */
	public XBoxJoystick467 getDriveJoystick() {
		// TODO Return the joystick
		return null;
	}

	public ButtonPanel getButtonPanel() {
		// TODO: Return the button panel
		return null;
	}

	// All button mappings are accessed through the functions below

	/**
	 * returns the current drive mode. Modes lower in the function will override
	 * those higher up. only 1 mode can be active at any time
	 *
	 * @return currently active drive mode.
	 */
	public DriveMode getDriveMode() {
		// TODO: Set the drive mode based on the buttons pushed
		return DriveMode.CRAB; // Update with the correct drive mode
	}

	public boolean getTerminateAuto() {
		// TODO Manually break out of autonoumous mode
		return true;
	}

	public ActionGroup getActionGroup() {
		// TODO Get an action group if required
		return null; 
	}

	/**
	 * @return true if button to reset the gyroscope selection is pressed
	 */
	public boolean getGyroReset() {
		// TODO Check the gyro reset button
		return false;
	}

}