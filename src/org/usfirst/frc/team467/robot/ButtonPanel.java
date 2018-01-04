package org.usfirst.frc.team467.robot;

import java.util.HashMap;

public class ButtonPanel {

	public enum Buttons {
		exampleButtonName(1); // Need to add all buttons to enum with their respective hardware id

		public final int channel;

		Buttons(int channel) {
			this.channel = channel;
		}
	}

	// Set of button states
	private HashMap<Buttons, Boolean> buttonStates = new HashMap<Buttons, Boolean>();

	public ButtonPanel(int port) {
		// TODO initialize button states
	}

	public void readInputs() {
		// TODO Get button states and place into hash map
	}

	public boolean buttonDown(Buttons button) {
		// TODO Check to see if a specific button is pushed.
		return false; // Replace with check
	}

}
