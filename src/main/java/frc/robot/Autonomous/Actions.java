package frc.robot.autonomous;

import frc.robot.RobotMap;
import frc.robot.autonomous.ActionGroup.ConcurrentActions;
import frc.robot.autonomous.ActionGroup.MultiCondition;
import frc.robot.drive.Drive;
import frc.robot.gamepieces.Elevator;
import frc.robot.gamepieces.Elevator.Stops;
import frc.robot.gamepieces.Grabber;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.communications.RobotData;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

public class Actions {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(Actions.class.getName());

  private static final DecimalFormat df = new DecimalFormat("####0.00");

  private static Drive drive = Drive.getInstance();

  private static RobotData data = RobotData.getInstance();

  private static double mirrorTurns = 1.0;
  
  public static void startOnLeft() {
    mirrorTurns = -1.0;
    data.startingLocation(2.5, 0);
  }
  
  public static void startOnRight() {
    mirrorTurns = 1.0;
    data.startingLocation(21.58, 0);
  }
  
  public static void startInCenter() {
    mirrorTurns = 1.0;
    data.startingLocation(12.5, 0);
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

  public static ActionGroup grabCube() {
    Grabber grabber = Grabber.getInstance();
    
    ConcurrentActions closeGrabberAndGrab = new ConcurrentActions(
        () -> grabber.close(),
        () -> grabber.grab()
        );
    
    MultiCondition grabbing1secOrStopWhenHaveCube = new MultiCondition(
        new ActionGroup.Duration(1.0)
        );
    
    //group.addAction(openGrabber());
    ActionGroup group = new ActionGroup("grab cube with solenoid");
    
    group.addAction(new Action(
        "Grabbing",
        new ActionGroup.Duration(2.0),
        () -> grabber.grabAndOpen()));
    
    group.addAction(new Action(
        "Closing grabber on cube",
        grabbing1secOrStopWhenHaveCube,
        closeGrabberAndGrab
        ));
    
    group.addAction(new Action(
        "Stoping Wheels",
        new ActionGroup.Duration(1.0),
        () -> grabber.pause()
      ));
    return group;
  }
  
  public static Action lockCube() {
    Grabber grabber = Grabber.getInstance();
    return new Action(
        "Starting: Locking cube",
        new ActionGroup.Duration(0.5),
        () -> grabber.startGrab());
  }

  public static ActionGroup grabAndMoveLinear(double distance) {
    ActionGroup group = new ActionGroup("grab and move Linear");
    group.addAction(zeroDistance());
    MultiCondition multicondition = new MultiCondition(
        new ActionGroup.ReachDistance(distance)//, 
        // () -> grabber.hasCube()
        );
    
    ConcurrentActions concurrentaction = new ConcurrentActions(
        () -> grabCube(),
        () -> drive.moveLinearFeet(distance));
    
    group.addAction(new Action(
        "Grabbing cube and driving forward",
        multicondition,
        concurrentaction));
    return group;
  }

  public static Action releaseCube() {
    Grabber grabber = Grabber.getInstance();
    return new Action(
        "Releasing cube",
        //new ActionGroup.RunOnce(
        new ActionGroup.Duration(1.0),
        () -> grabber.release());
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
    mode.addAction(elevatorToSwitch());
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

  public static ActionGroup testGrab() {
    String actionGroupText = "Testing grab with a 2 foot move.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    //mode.addAction(elevatorToFloor());
    mode.addActions(grabCube()); 
    return mode;
  }

  // BASIC ACTIONS

  public static ActionGroup centerBasicSwitchLeft() {
    String actionGroupText = "Start in center, put cube on left switch.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(3.5)); 
    mode.addActions(turn(-90));
    mode.addActions(move(5.27));//5.77 
    mode.addActions(turn(90));
    mode.addActions(move(4.84));//4.34
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    return mode;
  }

  public static ActionGroup centerBasicSwitchRight() {
    String actionGroupText = "Start in center, put cube on right switch.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(3.5));
    mode.addActions(turn(90));
    mode.addActions(move(4.27));
    mode.addActions(turn(-90));
    mode.addActions(move(4.84));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    return mode;
  }

  public static ActionGroup basicSwitchOurSide() {
    String actionGroupText = "Put cube on our side switch.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(11.83));
    mode.addActions(turn(-90));
    mode.addActions(move(2.0)); 
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    return mode;
  }

  public static ActionGroup basicScaleOurSide() {
    String actionGroupText = "Put cube on our side scale";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(22.83));
    mode.addAction(elevatorToHighScale());
    mode.addActions(move(2.0));
    mode.addActions(turn(-95));
    mode.addActions(move(0.375));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    return mode;
  }

  public static ActionGroup basicScaleOppositeSide() {
    String actionGroupText = "Put cube on opposite side scale";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(start());
    mode.addActions(move(17.5));//17.64
    mode.addActions(turn(-90));
    mode.addActions(move(17.4));//17.4
    mode.addActions(turn(100));
    mode.addAction(elevatorToHighScale());
    mode.addActions(move(3.0));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    return mode;
  }

  // Advanced
  
  public static ActionGroup advancedSwitchOurSideScaleOurSide() {
    String actionGroupText = "Put cube on our side scale and second on our side switch.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(basicScaleOurSide());

    // pick up cube
    mode.addActions(move(-1.0)); 
    mode.addAction(elevatorToFloor());
    mode.addAction(zeroDistance()); //Wait doesn't zero
    mode.addAction(wait(0.5));
    mode.addActions(turn(-68)); 
    mode.addActions(grabAndMoveLinear(9.3)); 
    
    // Move To Switch 
    mode.addActions(grabAndMoveLinear(-1.0));
    mode.addAction(elevatorToSwitch());
    mode.addActions(move(1.0));
    
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    
    //Get read for teleop 
    mode.addActions(move(-1.0));
    mode.addAction(elevatorToFloor());

    return mode;
  }

  public static ActionGroup advancedSwitchOurSideScaleOpposite() {
    String actionGroupText = "Put cube on our side switch then opposite side scale.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(basicSwitchOurSide());
    
    //pick up cube 
    mode.addActions(move(-2.0));
    mode.addAction(elevatorToFloor());
    mode.addActions(turn(90));
    mode.addActions(move(6.31));
    mode.addActions(turn(-125));
    mode.addActions(grabAndMoveLinear(3.2));
    mode.addActions(grabAndMoveLinear(-3.2));
    
    //Go to far scale
    mode.addActions(turn(35));
    mode.addActions(move(18.0));
    
    //place cube in scale 
    mode.addActions(turn(100));
    mode.addAction(elevatorToHighScale());
    mode.addActions(move(3.4));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    
    //Get ready for teleop
    mode.addActions(move(-1.25));
    mode.addAction(elevatorToFloor());
    return mode;
  }

  public static ActionGroup advancedSwitchOppositeScaleOurSide() {
    String actionGroupText = "Start on our side scale then get another.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(basicScaleOurSide());

    //Go to pick up cube
    mode.addActions(move(-1.0)); 
    mode.addAction(elevatorToFloor());
    mode.addAction(zeroDistance()); //Wait doesn't zero
    mode.addAction(wait(0.5));
    mode.addActions(turn(-60)); 
    mode.addActions(grabAndMoveLinear(9.3));
    
    //Return to Scale
    mode.addActions(grabAndMoveLinear(-9.3));
    mode.addAction(elevatorToSwitch());
    mode.addActions(turn(60));
    mode.addActions(move(1.0));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    
    //Get ready for teleop 
    mode.addActions(move(-1.25));
    mode.addAction(elevatorToFloor());
    return mode;
  }

  public static ActionGroup advancedSwitchOppositeScaleOpposite() {
    String actionGroupText 
        = "Start on Right side, put cube on left switch and second on left scale.";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(basicScaleOppositeSide());
    
    return mode;
  }
  
  public static ActionGroup advancedCenterRightExchange() {
    String actionGroupText = "Start in center, put cube on right switch and second in exchange";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(centerBasicSwitchRight());
    
    mode.addActions(move(-5.7));
    mode.addAction(elevatorToFloor());
    mode.addActions(turn(-45));
    mode.addActions(grabAndMoveLinear(4.6));
    mode.addActions(grabAndMoveLinear(-1.5));
    mode.addActions(turn(-90));
    mode.addActions(move(6.1));
    mode.addActions(turn(-45));
    mode.addActions(move(0.7));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    
    return mode;
  }

  public static ActionGroup advancedCenterLeftExchange() {
    String actionGroupText = "Start in center, put cube on left switch and second in exchange";
    ActionGroup mode = new ActionGroup(actionGroupText);
    mode.addActions(centerBasicSwitchLeft());
    
    mode.addActions(move(-5.9));
    mode.addAction(elevatorToFloor());
    mode.addActions(turn(45));
    mode.addActions(grabAndMoveLinear(4.8));
    mode.addActions(grabAndMoveLinear(-1.6));
    mode.addActions(turn(135));
    mode.addActions(move(4.5));
    mode.addAction(releaseCube());
    mode.addAction(pauseGrabber());
    
    return mode;
  }
}
