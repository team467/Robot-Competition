package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.XBoxJoystick467.Button;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

public class DriverStation {
	
	XBoxJoystick467 driverJoy;
	XBoxJoystick467 navJoy;
	
	private static DriverStation station;
	
	// Mapping of functions to Controller Buttons for normal operation
	// TODO: Create enum for buttons
	/**
	 * Singleton instance of the object.
	 *
	 * @return
	 */
	public static DriverStation getInstance() {
		if (station == null) {
			station = new DriverStation();
		}
		return station;
	}

	/**
	 * Private constructor
	 */
	private DriverStation() {
		// TODO: Initialize class variables
		driverJoy = new XBoxJoystick467(0,"Drive");
		navJoy = new XBoxJoystick467 (1, "Nav");
	}

	/**
	 * Must be called prior to first button read.
	 */
	public void readInputs() {
		driverJoy.read();
		setDriverRumble(0.0); // Default unless specified otherwise
		// TODO: Read inputs from the buttons
	}

	/**
	 * Gets joystick instance used by driver.
	 *
	 * @return
	 */
	public XBoxJoystick467 getDriveJoystick() {
		return driverJoy;
	}
	
	public double getTurnSensivity() {
		return 0.0;
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
		return DriveMode.ArcadeDrive; // Update with the correct drive mode
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
	
	public double getArcadeSpeed() {
		return getDriveJoystick().turboSpeedAdjust();
	}
	
	public double getArcadeTurn() {
		return getDriveJoystick().getRightStickX();
	}
	
	public void setDriverRumble(double value) {
		getDriveJoystick().setRumble(value);
	}
}