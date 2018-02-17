package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class NullSolenoid extends Solenoid {
	public NullSolenoid() {
		super(0);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// Nothing
	}

	@Override
	public void set(boolean on) {
		// Nothing
	}

	@Override
	public boolean get() {
		return false;
	}
}
