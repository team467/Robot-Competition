package frc.robot.autonomous;

import frc.robot.RobotMap;
import frc.robot.autonomous.Action.Activity;
import frc.robot.drive.AutoDrive;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

/**
 * Runs through a set of actions. <br>
 * Can be used in Autonomous and also Teleop routines.
 */
public class ActionGroup {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(ActionGroup.class.getName());
  private static final DecimalFormat df = new DecimalFormat("####0.00");

  private static AutoDrive drive = Drive.getInstance();
  private String name;
  private LinkedList<Action> agenda;
  private final LinkedList<Action> master;
  private Action action = null;
  private boolean cancel = true;

  public ActionGroup(String name) {
    this.name = name;
    master = new LinkedList<>();
    agenda = new LinkedList<>();
  }

  /**
   * Run periodically to perform the Actions.
   */
  public void run() {
    if (cancel) {
      action = null;
      while (!agenda.isEmpty()) {
        Action act = agenda.pop();
        act.isDone();
      }
      /* cancels run function */
      return;
    }
    if (action == null || action.isDone()) {
      try {
        if (!agenda.isEmpty()) {
          action = agenda.pop();
          LOGGER.info("----- Starting action: {} -----", action.description);
        } else {
          // Stop everything forever
          if (action != null) {
            LOGGER.info("----- Final action completed -----");
          }
          action = null;
          return;
        }
      } catch (NoSuchElementException e) {
        LOGGER.error("Ran out of actions!", e);
      }
    }

    LOGGER.trace("run {}", action);
    action.doIt();
  }

  public boolean isComplete() {
    return action == null && agenda.isEmpty();
  }

  public void terminate() {
    LOGGER.debug("Terminating Process");
    agenda.clear();
    action = null;
  }

  public void addAction(Action action) {
    master.add(action);
  }

  public void addActions(List<Action> actions) {
    master.addAll(actions);
  }

  public void addActions(ActionGroup actions) {
    master.addAll(actions.master);
  }

  public void enable() {
    LOGGER.debug("Enabling Process");
    for (Action act : master) {
      if (act.condition instanceof Duration) {
        LOGGER.debug("Resetting Duration");
        ((Duration) act.condition).reset();
      }
    }
    // Copy master (not reference)
    agenda = new LinkedList<>(master);
    action = null;
  }

  static class RunOnce implements Action.Combined {
    boolean isDone = false;
    final Action.Activity activity;

    public RunOnce(Action.Activity activity) {
      this.activity = activity;
    }

    @Override
    public boolean isDone() {
      return isDone;
    }

    @Override
    public void doIt() {
      activity.doIt();
      isDone = true;
    }
  }

  static class Duration implements Action.Condition {
    private double durationMs;
    private double actionStartTimeMs = -1;

    public Duration(double duration) {
      durationMs = duration * 1000;
      LOGGER.debug("durationMS= {}", durationMs);
    }

    @Override
    public boolean isDone() {
      if (actionStartTimeMs < 0) {
        actionStartTimeMs = System.currentTimeMillis();
      }

      return System.currentTimeMillis() > durationMs + actionStartTimeMs;
    }

    public void reset() {
      actionStartTimeMs = -1;
    }
  }

  static class ReachDistance implements Action.Condition {
    private double distance = 0.0;
    private double currentPosition = 0.0;
    private double lastPosition = 0.0;
    private int increment = 0;
    protected double timeout = RobotMap.AUTONOMOUS_DRIVE_TIMEOUT_MS;

    public ReachDistance(double distance) {
      this.distance = distance;
    }

    @Override
    public boolean isDone() {
      lastPosition = currentPosition;
      currentPosition = drive.absoluteDistanceMoved();

      LOGGER.debug("Distances - Target: {} Moved: {}", df.format(Math.abs(distance)), df.format(currentPosition));
      if (currentPosition > 0.3 && Math.abs(lastPosition - currentPosition) < 0.01) {
        increment++;
      } else {
        increment = 0;
      }

      // Each iteration is 20 ms.
      // the increment check checks to see how long the robot is stopping for,
      // if it is stopped for longer than (RobotMap.AUTONOMOUS_DRIVE_TIMEOUT_MS / 20)
      // then the robot is done.
      if (increment >= (timeout / 20)) {
        return true;
      } else if (currentPosition >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
        LOGGER.debug("Finished moving");
        return true;
      } else {
        LOGGER.debug("Still moving");
        return false;

      }
    }
  }

  static class ReachAngle extends ReachDistance {
    public ReachAngle(double rotationInDegrees) {
      super(Drive.degreesToFeet(rotationInDegrees));
      timeout = RobotMap.AUTONOMOUS_TURN_TIMEOUT_MS;
    }
  }

  public String getName() {
    return name;
  }

  static class ConcurrentActions implements Action.Activity {

    private List<Activity> activities;

    public ConcurrentActions(Action.Activity... activities) {
      this.activities = Arrays.asList(activities);
    }

    @Override
    public void doIt() {
      for (Action.Activity activity : activities) {
        activity.doIt();
      }

    }
  }

  static class MultiCondition implements Action.Condition {

    private List<Action.Condition> conditions;

    public MultiCondition(Action.Condition... conditions) {
      this.conditions = Arrays.asList(conditions);
    }

    @Override
    public boolean isDone() {
      for (Action.Condition condition : conditions) {
        if (condition.isDone()) {
          return true;
        }
      }
      return false;
    }
  }
}
