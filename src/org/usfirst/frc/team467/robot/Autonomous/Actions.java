package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Elevator;
import org.usfirst.frc.team467.robot.Elevator.Stops;
import org.usfirst.frc.team467.robot.Grabber;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;

import edu.wpi.first.wpilibj.Timer;

public class Actions {

	private static final Logger LOGGER = Logger.getLogger(Actions.class);

	public static final Action nothing(){
		String actionText = "Do Nothing";
		if (RobotMap.useSimulator) {
			return new Action(actionText,
					() -> DriveSimulator.getInstance().isStopped(),
					() -> DriveSimulator.getInstance().moveFeet(0));
		} else {
			return new Action(actionText,
					() -> Drive.getInstance().isStopped(),
					() -> Drive.getInstance().moveFeet(0));
		}
	}

	public static Action wait(double duration) {
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				new ActionGroup.Duration(duration),
				() -> drive.tankDrive(0, 0, false));
	}

	public static final Action nothingForever(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> false,
				() -> drive.tankDrive(0, 0, false));
	}

	public static Action print(String message) {
		return new Action(
				"Print custom message",
				new ActionGroup.RunOnce(() -> LOGGER.info(message)));
	}

	public static Action grabCube() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Grabbing cube",
				new ActionGroup.RunOnce(() -> grabber.grab()));
	}
	
	public static Action releaseCube() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Releasing cube",
				new ActionGroup.RunOnce(() -> grabber.release()));
	}
	
	public static Action pauseGrabber() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Pausing grabber",
				new ActionGroup.RunOnce(() -> grabber.pause()));
	}
	
	public static Action elevatorToFloor() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to lowest level", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.basement)));
	}
	
	public static Action elevatorToSwitch() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going up to switch height", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.fieldSwitch)));
	}
	
	public static Action elevatorToLowScale() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to lower level on scale",
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.lowScale)));
	}
	
	public static Action elevatorToHighScale() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to higher level on scale", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.highScale)));		
	}
		
	public static Action zeroDistance() {
		if (RobotMap.useSimulator) {
			return new Action(
					"Zeroing the distance",
					new ActionGroup.RunOnce(() -> DriveSimulator.getInstance().zero()));
		} else {
			return new Action(
					"Zeroing the distance",
					new ActionGroup.RunOnce(() -> Drive.getInstance().zero()));
		}
	}
	
	/**
	 * 
	 * @param Distance moves robot in feet.
	 * @return
	 */

	public static Action moveDistanceForward(double distance) {
		String actionText = "Move forward " + distance + " feet";
		if (RobotMap.useSimulator) {
			return new Action(actionText,
					new ActionGroup.ReachDistance(distance),
					() -> DriveSimulator.getInstance().moveFeet(distance));
		} else {
			return new Action(actionText,
					new ActionGroup.ReachDistance(distance),
					() -> Drive.getInstance().moveFeet(distance));
		}
	}

	/**
	 * 
	 * @param rotationInDegrees Rotates robot in radians. Enter rotation amount in Degrees.
	 * @return
	 */
	public static Action moveturn(double rotationInDegrees) {
		String actionText = "Rotate " + rotationInDegrees + " degrees.";
	    double rotation = rotationInDegrees;
	    if (RobotMap.useSimulator) {
			return new Action(actionText,
					new ActionGroup.ReachDistance(rotation),
					() -> DriveSimulator.getInstance().rotateByAngle(rotation));
	    } else {
			return new Action(actionText,
					new ActionGroup.ReachDistance(rotation),
					() -> Drive.getInstance().rotateByAngle(rotation));
	    }
	}

	public static boolean moveDistanceComplete(double distance) {
		double distanceMoved;
		if (RobotMap.useSimulator) {
			distanceMoved = DriveSimulator.getInstance().absoluteDistanceMoved();
		} else {
			distanceMoved = Drive.getInstance().absoluteDistanceMoved();
		}

		LOGGER.debug("Distances - Target: " + Math.abs(distance) + " Moved: " + distanceMoved);
		if (distanceMoved >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
			LOGGER.info("Finished moving " + distanceMoved + " feet");
			return true;
		} else {
			LOGGER.info("Still moving " + distanceMoved + " feet");
			return false;
		}
	}

	public static ActionGroup moveDistanceForwardProcess3X(double distance) {
		String actionGroupText = "Move forward 3X " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		mode.enable();
		return mode;
	}

	public static ActionGroup moveDistance(double distance) {
		String actionGroupText = "Move forward " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		return mode;
	}
	
	public static ActionGroup turn(double degrees) {
		String actionGroupText = "Turn " + degrees + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(degrees));
		return mode;
	}
	
	//SWITCH
	
	
	//SWITCH - BASIC
	/*
	 * 
	 * 
	 */

	public static ActionGroup leftBasicSwitchLeft() {
		String actionGroupText = "Start on left side, put cube on switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(12.33)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.479));
		mode.addAction(zeroDistance());
		return mode;
	} 

	public static ActionGroup centerBasicSwitchLeft() {
		String actionGroupText = "Start in center, put cube on left switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.27)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.34)); 
		return mode;
	}

	public static ActionGroup centerBasicSwitchRight() {
		String actionGroupText = "Start in center, put cube on right switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.27)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.34));
		return mode;
	}

	public static ActionGroup rightBasicSwitchRight() {
		String actionGroupText = "Start on right, put cube on switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(grabCube());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(12.33)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(elevatorToSwitch());
		mode.addAction(moveDistanceForward(1.479)); // 1' 5.75"
		mode.addAction(releaseCube());
		return mode; //works
	}

	//SCALE - BASIC
	/*
	 * 
	 * 
	 */

	public static ActionGroup leftBasicScaleLeft() {
		String actionGroupText = "Start on left, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(25.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	public static ActionGroup leftBasicScaleRight(){
		String actionGroupText = "Start on left, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	public static ActionGroup centerBasicScaleLeft(){
		String actionGroupText = "Start in center, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(10.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(21.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	public static ActionGroup centerBasicScaleRight(){
		String actionGroupText = "Start in center, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(9.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(21.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	public static ActionGroup rightBasicScaleLeft(){
		String actionGroupText = "Start on right, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	public static ActionGroup rightBasicScaleRight(){
		String actionGroupText = "Start on right, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(25.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		return mode;
	}

	//ADVANCED
	//left
	/*
	 * 
	 * 
	 */
	public static ActionGroup leftAdvancedSwitch() {
		String actionGroupText = "Start on left side, put cube on switch and second on left side of scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicSwitchLeft());

		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3)); 
		// pick up cube
		mode.addAction(grabCube());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-4.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}


	public static ActionGroup leftAdvancedScaleLeftSwitch() {
		String actionGroupText = "Start on left side, put cube on left scale and second on left switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicScaleLeft());
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.08 + 1.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.4));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.4));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-5.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(3.1));
		mode.addAction(zeroDistance());
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScale() {
		String actionGroupText = "Start on left side, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicSwitchLeft());
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.2057));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 1.0 + 0.45));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		
		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScaleRight() {
		String actionGroupText = "Start on left side, put cube on right switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.475));
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.79));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.2));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(moveDistanceForward(-6.5));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScaleLeft() {
		String actionGroupText = "Start on left side, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.475));
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.475));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08- 3.75));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(3.75));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		
		return mode;
	}
	
	public static ActionGroup testAction() {
		String actionGroupText = "Testing grab with a 2 foot move.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		//mode.addAction(grabCube());
		//mode.addAction(moveDistanceForward(2.0));
		mode.addAction(elevatorToSwitch());
		return mode;
	}
	
	//right
	/*
	 * 
	 * 
	 */
	public static ActionGroup rightAdvancedSwitch() {
		String actionGroupText = "Start on Right side, put cube on switch and second on Right side of scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(rightBasicSwitchRight());
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-4.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521)); 
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}
	public static ActionGroup rightAdvancedSwitchLeftScale() {
		String actionGroupText = "Start on right side, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(rightBasicSwitchRight());
		//lift elevator to place cube into right switch. 
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.2057));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 1.0 + 0.45));
		return mode;
	}

	public static ActionGroup rightAdvancedSwitchLeftScaleRight() {
		String actionGroupText = "Start on Right side, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.475));
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.475));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08- 3.75));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(3.75));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}
	
	public static ActionGroup rightAdvancedSwitchLeftScaleLeft() {
		String actionGroupText = "Start on Right side, put cube on left switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(18.14));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(19.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.19 + 0.6));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.475));
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.79));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.2));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(moveDistanceForward(-6.5));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}
	
	//center
	/*
	 * 
	 * 
	 */
	public static ActionGroup centerAdvancedSwitchLeftScaleLeft() {
		String actionGroupText = "Start in center, put cube on left switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(10.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(8.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.479));
		mode.addAction(zeroDistance());
		//Drop cube, then find new cube
		mode.addAction(releaseCube());
		mode.addAction(grabCube());
		
		mode.addAction(moveDistanceForward(-2.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3)); 
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521)); 
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}	
	
	public static ActionGroup centerAdvancedSwitchLeftScaleRight() {
		String actionGroupText = "Start in center, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(10.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(8.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.479));
		mode.addAction(zeroDistance());
		// lift elevator to place cube into switch, then find new cube
		
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.2057));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 1.0 + 0.45));
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}	
	
	public static ActionGroup centerAdvancedSwitchRightScaleLeft() {
		String actionGroupText = "Start in center, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(9.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(8.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.479));
		mode.addAction(zeroDistance());
		//lift elevator to place cube into right switch, then find new cube
		
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3));
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.2057));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 1.0 + 0.45));
		return mode;
	}	
	
	public static ActionGroup centerAdvancedSwitchRightScaleRight() {
		String actionGroupText = "Start in center, put cube on right switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.0));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(9.08));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(8.33));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.479));
		mode.addAction(zeroDistance());
		// lift elevator to place cube into switch, then find new cube
		
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-2.0)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(5.81)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90));
		mode.addAction(zeroDistance());
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(1.3)); 
		// pick up cube
		mode.addAction(grabCube());
		
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-1.3));
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(-4.08)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(7.19)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(-90)); 
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(0.375 + 0.521)); 
		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		return mode;
	}	
	
	//WOW
	/*
	 * 
	 * 
	 */

	public static ActionGroup doNothing(){
		ActionGroup mode = new ActionGroup("none");
		mode.addAction(nothing());
		return mode;
	}


	public static ActionGroup newDriveSquareProcess() {
		ActionGroup mode = new ActionGroup("Drive in a 2x2 square clockwise from bottom left");
		//		mode.addAction(turnAndMoveDistance(0, 2));
		//		mode.addAction(turnAndMoveDistance(90, 2));
		//		mode.addAction(turnAndMoveDistance(180, 2));
		//		mode.addAction(turnAndMoveDistance(270, 2));
		return mode;
	}

}
