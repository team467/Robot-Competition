package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.*;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;

public class Actions {
	private static final Logger LOGGER = Logger.getLogger(Actions.class);

	public static final Action nothing(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.crabDrive(0, 0));
	}

	public static Action wait(double duration) {
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				new ActionGroup.ExampleActionCondition(),
//				new ActionGroup.Duration(duration),
				() -> drive.crabDrive(0, 0));
	}

	public static final Action nothingForever(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> false,
				() -> drive.crabDrive(0, 0));
	}

	public static final Action example1 = new Action(
			"Example 1",
			new ActionGroup.ExampleActionCondition(),
			() -> { System.out.println("Running Example Autonomous Action #1: " + System.currentTimeMillis()); });

	public static final Action example2 = new Action(
			"Example 2",
			new ActionGroup.ExampleActionCondition(),
			() -> { System.out.println("Running Example Autonomous Action #2: " + System.currentTimeMillis()); });

//	public static Action print(String message) {
//		return new Action(
//				"Print custom message",
//				new ActionGroup.RunOnce(() -> LOGGER.info(message)));
//	}

	/**
	 * Autonomous requires a check to see if something is complete, in this case if the wheel pods are in position mode.
	 *
	 * @return true when the position mode is set
	 */
	public static boolean isInPositionMode() {
		Drive drive = Drive.getInstance();
		if (drive.getControlMode() == TalonControlMode.Position) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Autonomous requires a check to see if something is complete, in this case if the wheel pods are in position mode.
	 *
	 * @return true when the position mode is not set
	 */
	public static boolean isNotInPositionMode() {
		Drive drive = Drive.getInstance();
		if (drive.getControlMode() != TalonControlMode.Position) {
			return true;
		} else {
			return false;
		}
	}

	// Private method makes process, but for only one public variable
	public static ActionGroup newBasicProcess() {
		ActionGroup mode = new ActionGroup("Basic Auto");
		mode.addAction(example1);
		mode.enable();
		return mode;
	}
	public static final ActionGroup basicProcess = newBasicProcess();

	private static ActionGroup getExampleProcess() {
		ActionGroup mode = new ActionGroup("Example Auto");
		mode.addAction(example1);
		mode.addAction(example2);
		return mode;
	}
	public static final ActionGroup exampleProcess = getExampleProcess();
}
