package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.XBoxJoystick467.Button;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverStation {

	private XBoxJoystick467 driverJoy;
	private XBoxJoystick467 navJoy;

	private Rumbler driverRumbler;
	private Rumbler navRumbler;

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
		driverJoy = new XBoxJoystick467(0, "driver");
		navJoy = new XBoxJoystick467(1, "nav");

		driverRumbler = new Rumbler(driverJoy);
		navRumbler = new Rumbler(navJoy);
	}

	/**
	 * Must be called prior to first button read.
	 */
	public void readInputs() {
		if (driverJoy != null) {
			driverJoy.read();
		}
		if (navJoy != null) {
			navJoy.read();
		}
	}

	public void logJoystickIDs() {
		if (driverJoy != null) {
			driverJoy.logIdentity();
		}
		if (navJoy != null) {
			navJoy.logIdentity();
		}
	}

	/**
	 * Gets joystick instance used by driver.
	 *
	 * @return
	 */
	public XBoxJoystick467 getDriveJoystick() {
		return driverJoy;
	}

	public XBoxJoystick467 getNavJoystick() {
		return navJoy;
	}

	public Rumbler getDriverRumbler() {
		return driverRumbler;
	}

	public Rumbler getNavRumbler() {
		return navRumbler;
	}

	public double getTurnSensivity() {
		return 0.0;
	}

	public void periodic() {
		driverRumbler.periodic();
		navRumbler.periodic();
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
		return DriveMode.CurvatureDrive; // Update with the correct drive mode
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

	public double getElevatorSpeed() {
		return getNavJoystick().getRightStickY();
	}

	public boolean getFloorHeightButtonPressed() {
		return getNavJoystick().pressed(Button.a);
	}

	public boolean getSwitchHeightButtonPressed() {
		return getNavJoystick().pressed(Button.b);
	}

	public boolean getLowScaleHeightButtonPressed() {
		return getNavJoystick().pressed(Button.y);
	}

	public boolean getHighScaleHeightButtonPressed() {
		return getNavJoystick().pressed(Button.x);
	}

	public double getGrabThrottle() {
		return getNavJoystick().getLeftStickY();
	}

	public void navSetLeftRumble(double value) {
		navJoy.leftRumble(value);
	}

	public void navSetRightRumble(double value) {
		navJoy.rightRumble(value);
	}

	public void driverSetLeftRumble(double value) {
		driverJoy.leftRumble(value);
	}

	public void driverSetRightRumble(double value) {
		driverJoy.rightRumble(value);
	}

	public void setDriverRumble(double value) {
		getDriveJoystick().setRumble(value);
	}

	/**
	 * Set a value in 'Basic' tab of the driver station
	 * 
	 * @param slot position of the value to set (0-9)
	 * @param value any string value
	 */
	public void set(int slot, String value) {
		if (slot < 0 || slot > 9) {
			return;
		}
		SmartDashboard.putString("DB/String " + slot, value);
	}

	/**
	 * Set a value in 'Basic' tab of the driver station
	 * 
	 * @param slot position of the value to set (0-9)
	 * @param value any integer value
	 */
	public void set(int slot, int value) {
		set(slot, String.valueOf(value));
	}

}