package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.RobotMap;

import edu.wpi.first.wpilibj.Timer;

public class Actions {
	private static final Logger LOGGER = Logger.getLogger(Actions.class);

	public static final Action nothing(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.tankDrive(0, 0));
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

	public static final Action moveForward = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().tankDrive(0.5, 0.5));

	public static Action print(String message) {
		return new Action(
				"Print custom message",
				new ActionGroup.RunOnce(() -> LOGGER.info(message)));
	}

	public static final Action goForward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move Forward " + seconds + "seconds";
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.tankDrive(0.7, 0.7));
	}

	public static Action zeroDistance() {
		Drive drive = Drive.getInstance();
		return new Action(
				"Zeroing the distance",
				new ActionGroup.RunOnce(() -> drive.zeroPosition()));
	}

	public static Action moveDistanceForward(double distance) {
		Drive drive = Drive.getInstance();
		String actionText = "Move forward " + distance + " feet";
		return new Action(actionText,
				new ActionGroup.ReachDistance(distance),
				() -> drive.moveDistance(distance));
	}
	
	public static Action moveDistanceWithTurn(double distance, double rotationInDegrees) {
	    double rotation = Math.toRadians(rotationInDegrees) * (RobotMap.WHEEL_BASE_WIDTH / 2);
		double maxDistance = Math.copySign(Math.max(Math.abs(distance), Math.abs(rotation)), distance);
	    
		Drive drive = Drive.getInstance();
		String actionText = "Move forward " + distance + " feet with a " + rotationInDegrees + " degree rotation.";
		return new Action(actionText,
				new ActionGroup.ReachDistance(maxDistance),
				() -> drive.moveDistance(distance, rotation));
	}

	public static Action turn(double rotationInDegrees) {
	    double rotation = Math.toRadians(rotationInDegrees) * (RobotMap.WHEEL_BASE_WIDTH / 2);
	    
		Drive drive = Drive.getInstance();
		String actionText = "Rotate " + rotationInDegrees + " degrees.";
		return new Action(actionText,
				new ActionGroup.ReachDistance(rotation),
				() -> drive.moveDistance(0, rotation));
	}

	public static boolean moveDistanceComplete(double distance) {
		Drive drive = Drive.getInstance();
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

//	public static boolean turnDistanceComplete() {
//		Drive drive = Drive.getInstance();
//		double turnError = drive.getTurnError();
//		LOGGER.debug("Error: " + turnError);
//		if (turnError <= RobotMap.POSITION_ALLOWED_ERROR) {
//			LOGGER.info("Error: " + turnError);
//			return true;
//		} else {
//			LOGGER.debug("Error" + turnError);
//			return false;
//		}
//	}

	public static ActionGroup moveDistanceForwardProcess3X(double distance) {
		String actionGroupText = "Move forward 3X " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
//		mode.addAction(zeroDistance());
		//mode.addAction(moveDistanceForward(distance));
//		mode.addAction(zeroDistance());
//		mode.addAction(moveDistanceForward(distance));
//		mode.addAction(zeroDistance());
//		mode.addAction(moveDistanceForward(distance));
//		mode.addAction(zeroDistance());
//		mode.addAction(turn(90));
		mode.addAction(moveDistanceWithTurn(5,45));
		return mode;
	}

	public static ActionGroup moveDistance(double distance) {
		String actionGroupText = "Move forward 3X " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		return mode;
	}

//	public static Action aim(double angle) {
//		Drive drive = Drive.getInstance();
//		Action aim =  new Action(
//				"Aim",
//				new ActionGroup.OnTarget(5),
//				() -> drive.turnToAngle(angle));
//		return aim;
//	}

//	public static Action disableAiming() {
//		Drive drive = Drive.getInstance();
//		Action disableAimingAction = new Action(
//				"Disable",
//				() -> true,
//				() -> drive.aiming.disable());
//		return disableAimingAction;
//	}


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
