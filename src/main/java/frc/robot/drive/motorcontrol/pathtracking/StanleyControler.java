package frc.robot.drive.motorcontrol.pathtracking;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import frc.robot.RobotMap;
import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;
import frc.robot.drive.motorcontrol.pathplanning.Spline2D;
import frc.robot.utilities.RobotUtilities;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class StanleyControler {

  /**
   * field X-Coordinate.
   */
  private double fieldX = 0.0;

  public double fieldX() {
    return fieldX;
  }

  /**
   * Y-Coordinate.
   */
  private double fieldY = 0.0;

  public double fieldY() {
    return fieldY;
  }

  /**
   * Heading angle.
   */
  private double heading = 0.0;

  public double heading() {
    return heading;
  }

  /**
   * Velocity.
   */
  private double velocity = 0.0;

  public double velocity() {
    return velocity;
  }

  /**
   * Control gain.
   */
  private double controlGain = 0.5;

  /**
   * Speed proportional gain.
   */
  private double speedProportionalGain = 1.0;

  /**
   * Time difference in seconds.
   */
  private double timeIncrement = 0.02;

  public double timeIncrement() {
    return timeIncrement;
  }

  /**
   * Maximum steering angle of the vehicle in radians.
   */
  private double maxSteer = Math.toRadians(30.0);

  private int currentTargetIndex = 0;

  public int currentTargetIndex() {
    return currentTargetIndex;
  }

  private double frontAxleError = 0.0;

  public double frontAxleError() {
    return frontAxleError;
  }

  /**
   * Creates a new instance of the stanley controller, including the robot starting coordinates.
   *
   * @param initialFieldPositionX
   * @param initialFieldPostitionY
   * @param initialFacing
   * @param initialVelocity
   */
  public StanleyControler(
      double initialFieldPositionX,
      double initialFieldPostitionY,
      double initialFacing,
      double initialVelocity) {
    this.fieldX = initialFieldPositionX;
    this.fieldY = initialFieldPostitionY;
    this.heading = initialFacing;
    this.velocity = initialVelocity;
  }

  /**
   * Update the state of the vehicle. The Stanley Controler uses a bicycle model
   * with acceleration and change in steering angle.
   *
   * @param acceleration  change in velocity
   * @param delta the change in the steering angle
   */
  public void update(double acceleration, double delta) {
    delta = RobotUtilities.clip(delta, -maxSteer, maxSteer);
    fieldX += velocity * Math.cos(heading) * timeIncrement;
    fieldY += velocity * Math.sin(heading)  * timeIncrement;
    velocity += acceleration * timeIncrement;
    heading += velocity / RobotMap.WHEEL_BASE_WIDTH * Math.tan(delta) * timeIncrement;
    heading = RobotUtilities.normalizeAngle(heading);
  }

  /**
   * Proportional control for the speed.
   *
   * @param targetVelocity the target speed
   * @param currentVelocity the current speed
   */
  public double pidControl(double targetVelocity, double currentVelocity) {
    return speedProportionalGain * (targetVelocity - currentVelocity);
  }

  /**
   * Stanley steering control.
   *
   * @param planX
   * @param planY
   * @param planHeading
   * @param indexOfLastTarget
   * @return
   */
  public double stanleyControl(double[] planX, double[] planY, 
      double[] planHeading, int indexOfLastTarget) {

    double frontAxleError = calculateTargetIndexAndFrontAxleError(planX, planY);

    if (indexOfLastTarget >= currentTargetIndex) {
      currentTargetIndex = indexOfLastTarget;
    }

    // thetaE corrects the heading error
    double thetaE = RobotUtilities.normalizeAngle(planHeading[currentTargetIndex] - heading);
    // thetaD corrects the cross track error
    double thetaD = Math.atan2(controlGain * frontAxleError, velocity);
    // Change in steering control
    double delta = thetaE + thetaD;

    return delta;
  }

  /**
   * Compute index in the trajectory list of the target.
   *
   * @param planX
   * @param planY
   * @return float the front axle error
   */
  public double calculateTargetIndexAndFrontAxleError(double[] planX, double[] planY) {
    // Calculate the position of the  front axle
    double actualXPos = fieldX + RobotMap.WHEEL_BASE_WIDTH * Math.cos(heading);
    double actualYPos = fieldY + RobotMap.WHEEL_BASE_WIDTH * Math.sin(heading);

    // Search nearest point index
    double[] diffActualXtoPlanXs = new double[planX.length];
    for (int i = 0; i < planX.length; i++) {
      diffActualXtoPlanXs[i] = actualXPos - planX[i];
    }

    double[] diffActualYtoPlanYs = new double[planY.length];
    for (int i = 0; i < planY.length; i++) {
      diffActualYtoPlanYs[i] = actualYPos - planY[i];
    }

    ArrayList<Double> lengthActualPositionToPlanPositions = new ArrayList<>();
    for (int i = 0; i < diffActualXtoPlanXs.length; i++) {
      lengthActualPositionToPlanPositions.add(
          Math.sqrt(Math.pow(diffActualXtoPlanXs[i], 2) + Math.pow(diffActualYtoPlanYs[i], 2)));
    }

    frontAxleError = Collections.min(lengthActualPositionToPlanPositions);

    currentTargetIndex = lengthActualPositionToPlanPositions.indexOf(frontAxleError);

    double targetHeading = RobotUtilities.normalizeAngle(
        Math.atan2(actualYPos - planY[currentTargetIndex], 
        actualXPos - planX[currentTargetIndex]) - heading);
    if (targetHeading > 0.0) {
      frontAxleError = -frontAxleError;
    }

    return frontAxleError;
  }

  /**
   * Plot an example of Stanley steering control on a cubic spline. Used for testing.
   * @param args Input args, none used
   */
  public static void main(String[] args) {

    // Target course
    double[][] xy = {
      {0.0, 100.0, 100.0, 50.0, 60.0},
      {0.0, 0.0, -30.0, -20.0, 0.0}
    };

    //splineFunction
    AutonomousPlan course = new AutonomousPlan(xy, 0.0, 1.0, 1.0, false);
    DecimalFormat df = new DecimalFormat("#0.0");
    System.out.println("x\ty\theading\tk\tstep");
    for (int i = 0; i < course.size; i++) {
      System.out.println(df.format(course.x1[i]) + "\t" + df.format(course.y1[i]) 
          + "\t" + df.format(Math.toDegrees(course.heading[i])) + "\t" 
          + df.format(course.curvature[i]) + "\t" + df.format(course.step[i]));
    }
    // cx, cy, cyaw, ck, s = cubic_spline_planner.calc_spline_course(ax, ay, ds=0.1)

    double targetSpeed = 30.0 / 3.6;  // [m/s]

    // Initial state (x & y position, heading, velocity)
    StanleyControler state = new StanleyControler(-0.0, 5.0, Math.toRadians(20.0), 0.0);

    ArrayList<Double> x = new ArrayList<Double>();
    x.add(state.fieldX);
    ArrayList<Double> y = new ArrayList<Double>();
    y.add(state.fieldY);
    ArrayList<Double> heading = new ArrayList<Double>();
    heading.add(state.heading);
    ArrayList<Double> velocity = new ArrayList<Double>();
    velocity.add(state.velocity);
    ArrayList<Double> t = new ArrayList<Double>();
    t.add(0.0);
    state.calculateTargetIndexAndFrontAxleError(xy[0], xy[1]);
    int targetIndex = state.currentTargetIndex();

    double maxSimulationTime = 100.0;
    int lastIndex = course.size - 1;
    double time = 0.0;
    while (maxSimulationTime >= time && lastIndex > targetIndex) {
      double acceleration = state.pidControl(targetSpeed, state.velocity());
      double delta = state.stanleyControl(course.x1, course.y1, course.heading, targetIndex);
      targetIndex = state.currentTargetIndex();
      state.update(acceleration, delta);
      time += state.timeIncrement();
      x.add(state.fieldX());
      y.add(state.fieldY());
      heading.add(state.heading());
      velocity.add(state.velocity());
      t.add(time);
    }

    Plot plt = Plot.create();
    plt.plot()
        .add(RobotUtilities.arrayToList(course.x1), RobotUtilities.arrayToList(course.y1))
        .label("course")
        .linestyle("-.")
    ;
    plt.plot()
        .add(x, y)
        .label("trajectory")
        .linestyle("-")
    ;
    plt.legend();

    try {
      // plt1.show();
      plt.show();
      // plt3.show();
    } catch (IOException ex) {
      System.err.println(ex);
    } catch (PythonExecutionException ex) {
      System.err.println(ex);
    }
  }
}

