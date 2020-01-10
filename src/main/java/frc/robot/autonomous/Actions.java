package frc.robot.autonomous;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

public class Actions {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Actions.class.getName());

  private static final DecimalFormat df = new DecimalFormat("####0.00");

  private static Drive drive = Drive.getInstance();

  private static double mirrorTurns = 1.0;
  
  public static void startOnLeft() {
    mirrorTurns = -1.0;
  }
  
  public static void startOnRight() {
    mirrorTurns = 1.0;
  }
  
  public static void startInCenter() {
    mirrorTurns = 1.0;
  }
  
  public static final Action nothing() {
    String actionText = "Do Nothing";
    return new Action(actionText,
        () -> drive.isStopped(),
        () -> drive.moveLinearFeet(0));
  }

  public static Action wait(double duration) {
    String actionText = "Do Nothing";
    return new Action(actionText,
        new ActionGroup.Duration(duration),
        () -> drive.moveLinearFeet(0));
  }

  public static final Action nothingForever() {
    String actionText = "Do Nothing";
    return new Action(actionText,
        () -> false,
        () -> drive.moveLinearFeet(0));
  }

  public static ActionGroup doNothing() {
    ActionGroup mode = new ActionGroup("none");
    mode.addAction(nothing());
    return mode;
  }

  public static Action print(String message) {
    return new Action(
        "Print custom message",
        new ActionGroup.RunOnce(() -> LOGGER.info(message)));
  }


  public static Action zeroDistance() {
    return new Action(
        "Zeroing the distance",
        new ActionGroup.RunOnce(() -> drive.zero()));
  }

  /**
   * 
   * @param distance moves robot in feet.
   * @return
   */
  public static Action moveDistanceForward(double distance) {
    String actionText = "Move forward " + distance + " feet";
    return new Action(actionText,
        new ActionGroup.ReachDistance(distance),
        () -> drive.moveLinearFeet(distance));
  }

  /**
   * 
   * @param rotationInDegrees Rotates robot in radians. Enter rotation amount in Degrees.
   * 
   */
  public static Action moveturn(double rotationInDegrees) {
    String actionText = "Rotate " + rotationInDegrees + " degrees.";
    return new Action(actionText,
        new ActionGroup.ReachAngle(rotationInDegrees), 
        // reach distance was here instead of reachAngle
        () -> drive.rotateByAngle(rotationInDegrees));
  }

  public static boolean moveDistanceComplete(double distance) {
    double distanceMoved = drive.absoluteDistanceMoved();

    LOGGER.debug("Distances - Target: {} Moved: {}", 
        df.format(Math.abs(distance)), df.format(distanceMoved));
    if (distanceMoved >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
      LOGGER.info("Finished moving {} feet", df.format(distanceMoved));
      return true;
    } else {
      LOGGER.info("Still moving {} feet", df.format(distanceMoved));
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
    String actionGroupText = "Turn " + degrees + " degrees";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addAction(zeroDistance());
    mode.addAction(moveturn(mirrorTurns * degrees));
    return mode;
  }

  public static ActionGroup start() {
    String actionGroupText = "Lower grabber down and move elevator to safe height";
    ActionGroup mode = new ActionGroup(actionGroupText);
    return mode;
  }

  public static ActionGroup crossAutoLine() {
    String actionGroupText = "Go straight to cross the auto line.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(10.0));
    return mode;
  }
  
  // TEST ACTIONS

  public static ActionGroup fourFootSquare() {
    String actionGroupText = "Move in 4 foot square.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(move(4.0));
    mode.addActions(turn(90));
    mode.addActions(move(4.0));
    mode.addActions(turn(90));
    mode.addActions(move(4.0));
    mode.addActions(turn(90));
    mode.addActions(move(4.0));
    mode.addActions(turn(90));
    return mode;
  }

}
