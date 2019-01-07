package frc.robot.autonomous;

public class Action {
  public String description;
  public Condition condition;
  public Activity activity;

  public Action(String description, Condition condition, Activity activity) {
    this.description = description;
    this.condition = condition;
    this.activity = activity;
  }
  
  public Action(String description, Combined combined) {
    this.description = description;
    this.condition = combined;
    this.activity = combined;
  }

  public void run() {
    if (!isDone()) {
      doIt();
    }
  }

  public boolean isDone() {
    return condition.isDone();
  }

  public void doIt() {
    activity.doIt();
  }

  @FunctionalInterface
  public static interface Condition {
    public boolean isDone();
  }

  @FunctionalInterface
  public static interface Activity {
    public void doIt();
  }
  
  public static interface Combined extends Condition, Activity {} // Special type that contains both

  @Override
  public String toString() {
    return "Action [description=" + description + ", isDone=" + isDone() + "]";
  }
}
