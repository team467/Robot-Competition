package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Elevator;
import org.usfirst.frc.team467.robot.Elevator.Stops;
import org.usfirst.frc.team467.robot.Grabber;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;

public class Actions {

	private static final Logger LOGGER = Logger.getLogger(Actions.class);

	private static AutoDrive drive = (RobotMap.useSimulator) ? DriveSimulator.getInstance() : Drive.getInstance();

	public static final Action nothing(){
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.moveFeet(0));
	}

	public static Action wait(double duration) {
		String actionText = "Do Nothing";
		return new Action(actionText,
				new ActionGroup.Duration(duration),
				() -> drive.moveFeet(0));
	}

	public static final Action nothingForever(){
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> false,
				() -> drive.moveFeet(0));
	}

	public static ActionGroup doNothing(){
		ActionGroup mode = new ActionGroup("none");
		mode.addAction(nothing());
		return mode;
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
				new ActionGroup.Duration(1.0),
				() -> grabber.grab(RobotMap.MAX_GRAB_SPEED));
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
		return new Action(
				"Zeroing the distance",
				new ActionGroup.RunOnce(() -> drive.zero()));
	}

	/**
	 * 
	 * @param Distance moves robot in feet.
	 * @return
	 */

	public static Action moveDistanceForward(double distance) {
		String actionText = "Move forward " + distance + " feet";
		return new Action(actionText,
				new ActionGroup.ReachDistance(distance),
				() -> drive.moveFeet(distance));
	}

	/**
	 * 
	 * @param rotationInDegrees Rotates robot in radians. Enter rotation amount in Degrees.
	 * 
	 */
	public static Action moveturn(double rotationInDegrees) {
		String actionText = "Rotate " + rotationInDegrees + " degrees.";
		return new Action(actionText,
				new ActionGroup.ReachDistance(rotationInDegrees),
				() -> drive.rotateByAngle(rotationInDegrees));
	}

	public static boolean moveDistanceComplete(double distance) {
		double distanceMoved = drive.absoluteDistanceMoved();

		LOGGER.debug("Distances - Target: " + Math.abs(distance) + " Moved: " + distanceMoved);
		if (distanceMoved >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
			LOGGER.info("Finished moving " + distanceMoved + " feet");
			return true;
		} else {
			LOGGER.info("Still moving " + distanceMoved + " feet");
			return false;
		}
	}

	public static ActionGroup move(double distance) {
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
	
	public static ActionGroup start() {
		String actionGroupText = "Lower grabber down and move elevator to safe height";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(grabCube());
		mode.addAction(elevatorToSwitch());
		return mode;
	}
	
    public static ActionGroup simpleTest() {
        String actionGroupText = "Simplified version of leftbasicswitchleft.";
        ActionGroup mode = new ActionGroup(actionGroupText);
//		mode.addActions(start());
        mode.addActions(move(4.0));
        mode.addActions(turn(90));
        mode.addActions(move(4.0));
        mode.addActions(turn(90));
        mode.addActions(move(4.0));
        mode.addActions(turn(90));
        mode.addActions(move(4.0));
        mode.addActions(turn(90));
//        mode.addActions(moveDistance(2.0));
//        mode.addAction(releaseCube());
        return mode;
    }

	public static ActionGroup testGrab() {
		String actionGroupText = "Testing grab with a 2 foot move.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(grabCube());
		mode.addActions(move(2.0));
		mode.addAction(elevatorToSwitch());
		return mode;
	}

	//SWITCH - BASIC

	public static ActionGroup leftBasicSwitchLeft() {
		String actionGroupText = "Start on left side, put cube on switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(12.33));
		mode.addActions(turn(90));
		mode.addActions(move(1.479));
		mode.addAction(releaseCube());
		return mode;
	} 

	public static ActionGroup centerBasicSwitchLeft() {
		String actionGroupText = "Start in center, put cube on left switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0)); 
		mode.addActions(turn(-90));
		mode.addActions(move(5.27)); 
		mode.addActions(turn(90));
		mode.addActions(move(4.34)); 
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup centerBasicSwitchRight() {
		String actionGroupText = "Start in center, put cube on right switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(4.27)); 
		mode.addActions(turn(-90));
		mode.addActions(move(4.34));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup rightBasicSwitchRight() {
		String actionGroupText = "Start on right, put cube on switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(12.33)); 
		mode.addActions(turn(-90));
		mode.addActions(move(1.479)); // 1' 5.75"
		mode.addAction(releaseCube());
		return mode;
	}

	//SCALE - BASIC

	public static ActionGroup leftBasicScaleLeft() {
		String actionGroupText = "Start on left, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(25.33));
		mode.addActions(turn(90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup leftBasicScaleRight(){
		String actionGroupText = "Start on left, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(18.14));
		mode.addActions(turn(90));
		mode.addActions(move(19.08));
		mode.addActions(turn(-90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup centerBasicScaleLeft(){
		String actionGroupText = "Start in center, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0));
		mode.addActions(turn(-90));
		mode.addActions(move(10.0));
		mode.addActions(turn(90));
		mode.addActions(move(21.33));
		mode.addActions(turn(90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup centerBasicScaleRight(){
		String actionGroupText = "Start in center, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(9.08));
		mode.addActions(turn(-90));
		mode.addActions(move(21.33));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup rightBasicScaleLeft(){
		String actionGroupText = "Start on right, put cube on left scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(18.14));
		mode.addActions(turn(-90));
		mode.addActions(move(19.08));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup rightBasicScaleRight(){
		String actionGroupText = "Start on right, put cube on right scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(25.33));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		return mode;
	}

	//ADVANCED - LEFT

	public static ActionGroup leftAdvancedSwitch() {
		String actionGroupText = "Start on left side, put cube on switch and second on left side of scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicSwitchLeft());

		// pick up cube
		mode.addActions(move(-2.0)); 
		mode.addActions(turn(-90)); 
		mode.addActions(move(5.81)); 
		mode.addActions(turn(90)); 
		mode.addActions(move(4.08)); 
		mode.addActions(turn(90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3)); 
		mode.addActions(move(-1.3)); 
		mode.addActions(turn(-90));
		mode.addActions(move(-4.08));
		mode.addActions(turn(-90)); 
		mode.addActions(move(7.19)); 
		mode.addActions(turn(90));
		mode.addActions(move(0.375 + 0.521));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());

		return mode;
	}


	public static ActionGroup leftAdvancedScaleLeftSwitch() {
		String actionGroupText = "Start on left side, put cube on left scale and second on left switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicScaleLeft());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(4.08 + 1.0));
		mode.addActions(turn(90));
		mode.addActions(move(1.4));
		mode.addAction(grabCube());
		
		mode.addActions(move(-1.4));
		mode.addActions(turn(-90));
		mode.addActions(move(-5.08));
		mode.addActions(turn(90));
		mode.addActions(move(5.81));
		mode.addActions(turn(-90));
		mode.addActions(move(3.1));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScale() {
		String actionGroupText = "Start on left side, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(leftBasicSwitchLeft());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(-90));
		mode.addActions(move(5.81));
		mode.addActions(turn(90));
		mode.addActions(move(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addActions(turn(90));
		mode.addActions(move(1.3));
		mode.addAction(grabCube());
		
		mode.addActions(move(-1.3));
		mode.addActions(turn(-90));
		mode.addActions(move(5.2057));
		mode.addActions(turn(-90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375 + 1.0 + 0.45));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());

		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScaleRight() {
		String actionGroupText = "Start on left side, put cube on right switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(18.14));
		mode.addActions(turn(90));
		mode.addActions(move(19.08));
		mode.addActions(turn(90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(90));
		mode.addActions(move(1.475));

		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(90));
		mode.addActions(move(5.79));
		mode.addActions(turn(-90));
		mode.addActions(move(4.2));
		mode.addActions(turn(-90));
		mode.addActions(move(1.3));
		mode.addAction(grabCube());

		mode.addActions(move(-1.3));
		mode.addActions(turn(90));
		mode.addActions(move(-6.5));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375 + 0.521));
		mode.addActions(move(-2.0));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		return mode;
	}

	public static ActionGroup leftAdvancedSwitchRightScaleLeft() {
		String actionGroupText = "Start on left side, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(18.14));
		mode.addActions(turn(90));
		mode.addActions(move(19.08));
		mode.addActions(turn(90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(90));
		mode.addActions(move(1.475));
		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-1.475));
		mode.addActions(turn(90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(-90));
		mode.addActions(move(19.08- 3.75));
		mode.addActions(turn(-90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));

		mode.addActions(move(-1.3));
		mode.addActions(turn(90));
		mode.addActions(move(3.75));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(90));
		mode.addActions(move(0.375));
		mode.addActions(move(-2.0));

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

	public static ActionGroup rightAdvancedSwitch() {
		String actionGroupText = "Start on Right side, put cube on switch and second on Right side of scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(rightBasicSwitchRight());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(90)); 
		mode.addActions(move(5.81));
		mode.addActions(turn(-90));
		mode.addActions(move(4.08)); 
		mode.addActions(turn(-90)); 
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		
		mode.addActions(move(-1.3));
		mode.addActions(turn(90));
		mode.addActions(move(-4.08));
		mode.addActions(turn(90));
		mode.addActions(move(7.19)); 
		mode.addActions(turn(-90));
		mode.addActions(move(0.375 + 0.521)); 

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		
		return mode;
	}
	public static ActionGroup rightAdvancedSwitchLeftScale() {
		String actionGroupText = "Start on right side, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(rightBasicSwitchRight());

		// pick up cube
		mode.addActions(move(-2.0)); 
		mode.addActions(turn(90));
		mode.addActions(move(5.81));
		mode.addActions(turn(-90));
		mode.addActions(move(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addActions(turn(-90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		mode.addActions(move(-1.3));
		mode.addActions(turn(90));
		mode.addActions(move(5.2057));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(90));
		mode.addActions(move(0.375 + 1.0 + 0.45));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());

		return mode;
	}

	public static ActionGroup rightAdvancedSwitchLeftScaleRight() {
		String actionGroupText = "Start on Right side, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(18.14));
		mode.addActions(turn(-90));
		mode.addActions(move(19.08));
		mode.addActions(turn(-90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(-90));
		mode.addActions(move(1.475));

		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-1.475));
		mode.addActions(turn(-90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(90));
		mode.addActions(move(19.08- 3.75));
		mode.addActions(turn(90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		mode.addActions(move(-1.3));
		mode.addActions(turn(-90));
		mode.addActions(move(3.75));
		mode.addActions(turn(-90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375));
		mode.addActions(move(-2.0));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		
		return mode;
	}

	public static ActionGroup rightAdvancedSwitchLeftScaleLeft() {
		String actionGroupText = "Start on Right side, put cube on left switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(18.14));
		mode.addActions(turn(-90));
		mode.addActions(move(19.08));
		mode.addActions(turn(-90));
		mode.addActions(move(5.19 + 0.6));
		mode.addActions(turn(-90));
		mode.addActions(move(1.475));

		// lift elevator to place cube into switch
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(-90));
		mode.addActions(move(5.79));
		mode.addActions(turn(90));
		mode.addActions(move(4.2));
		mode.addActions(turn(90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		mode.addActions(move(-1.3));
		mode.addActions(turn(-90));
		mode.addActions(move(-6.5));
		mode.addActions(turn(-90));
		mode.addActions(move(7.19));
		mode.addActions(turn(90));
		mode.addActions(move(0.375 + 0.521));
		mode.addActions(move(-2.0));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		
		return mode;
	}

	//center

	public static ActionGroup centerAdvancedSwitchLeftScaleLeft() {
		String actionGroupText = "Start in center, put cube on left switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		
		mode.addActions(move(4.0));
		mode.addActions(turn(-90));
		mode.addActions(move(10.0));
		mode.addActions(turn(90));
		mode.addActions(move(8.33));
		mode.addActions(turn(90));
		mode.addActions(move(1.479));

		//Drop cube, then find new cube
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(-90));
		mode.addActions(move(5.81)); 
		mode.addActions(turn(90));
		mode.addActions(move(4.08)); 
		mode.addActions(turn(90)); 
		mode.addAction(grabCube());
		mode.addActions(move(1.3)); 

		mode.addActions(move(-1.3)); 
		mode.addActions(turn(-90)); 
		mode.addActions(move(-4.08)); 
		mode.addActions(turn(-90)); 
		mode.addActions(move(7.19)); 
		mode.addActions(turn(90));
		mode.addActions(move(0.375 + 0.521)); 

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());
		
		return mode;
	}	

	public static ActionGroup centerAdvancedSwitchLeftScaleRight() {
		String actionGroupText = "Start in center, put cube on left switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(4.0));
		mode.addActions(turn(-90));
		mode.addActions(move(10.0));
		mode.addActions(turn(90));
		mode.addActions(move(8.33));
		mode.addActions(turn(90));
		mode.addActions(move(1.479));

		// lift elevator to place cube into switch, then find new cube
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0)); 
		mode.addActions(turn(-90));
		mode.addActions(move(5.81));
		mode.addActions(turn(90));
		mode.addActions(move(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addActions(turn(90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		mode.addActions(move(-1.3));
		mode.addActions(turn(-90));
		mode.addActions(move(5.2057));
		mode.addActions(turn(-90));
		mode.addActions(move(7.19));
		mode.addActions(turn(-90));
		mode.addActions(move(0.375 + 1.0 + 0.45));

		// lift elevator to place cube into scale
		mode.addAction(elevatorToLowScale());
		mode.addAction(releaseCube());
		
		return mode;
	}	

	public static ActionGroup centerAdvancedSwitchRightScaleLeft() {
		String actionGroupText = "Start in center, put cube on right switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(9.08));
		mode.addActions(turn(-90));
		mode.addActions(move(8.33));
		mode.addActions(turn(-90));
		mode.addActions(move(1.479));
		//lift elevator to place cube into right switch, then find new cube	
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0)); 
		mode.addActions(turn(90));
		mode.addActions(move(5.81));
		mode.addActions(turn(-90));
		mode.addActions(move(2.0 + 11.7083 + 1.083 + 1.083));
		mode.addActions(turn(-90));
		mode.addActions(move(1.3));
		mode.addAction(grabCube());
		mode.addActions(move(-1.3));
		mode.addActions(turn(90));
		mode.addActions(move(5.2057));
		mode.addActions(turn(90));
		mode.addActions(move(7.19));
		mode.addActions(turn(90));
		mode.addActions(move(0.375 + 1.0 + 0.45));
		return mode;
	}	

	public static ActionGroup centerAdvancedSwitchRightScaleRight() {
		String actionGroupText = "Start in center, put cube on right switch and second on right scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);

		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(9.08));
		mode.addActions(turn(-90));
		mode.addActions(move(8.33));
		mode.addActions(turn(-90));
		mode.addActions(move(1.479));

		// lift elevator to place cube into switch, then find new cube
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());

		// pick up cube
		mode.addActions(move(-2.0)); 
		mode.addActions(turn(90)); 
		mode.addActions(move(5.81)); 
		mode.addActions(turn(-90));
		mode.addActions(move(4.08)); 
		mode.addActions(turn(-90));
		mode.addAction(grabCube());
		mode.addActions(move(1.3));
		mode.addActions(move(-1.3));
		mode.addActions(turn(90)); 
		mode.addActions(move(-4.08)); 
		mode.addActions(turn(90)); 
		mode.addActions(move(7.19)); 
		mode.addActions(turn(-90)); 
		mode.addActions(move(0.375 + 0.521)); 

		// lift elevator to place cube into scale
		mode.addAction(elevatorToHighScale());
		mode.addAction(releaseCube());

		return mode;
	}
}
