package org.usfirst.frc.team467.robot.Autonomous;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Runs through a set of actions. <br>
 * Can be used in Autonomous and also Teleop routines.
 */
public class ActionGroup {
//	private static final Logger LOGGER = Logger.getLogger(ActionGroup.class);
//	private String name;
//	private LinkedList<Action> agenda;
//	private final LinkedList<Action> master;
//	private Action action = null;

	public ActionGroup(String name) {
		// TODO
	}

	/**
	 * Run periodically to perform the Actions
	 */
	public void run() {
		// TODO
	}

	public boolean isComplete() {
		// TODO
		return true;
	}

	public void terminate() {
		// TODO
	}

	public void addAction(Action action) {
		// TODO
	}

	public void addActions(List<Action> actions) {
		// TODO
	}

	public void addActions(ActionGroup actions) {
		// TODO
	}

	public void enable() {
		// TODO
	}

	static class ExampleActionCondition implements Action.Condition {

		public ExampleActionCondition() {
			// TODO
		}

		@Override
		public boolean isDone() {
			// TODO
			return true;
		}

	}

	static class ExampleActionActivity implements Action.Activity {
		@Override
		public void doIt() {
			// TODO
		}
	}

	public String getName() {
		// TODO
		return "";
	}
}
