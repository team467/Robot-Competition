package frc.robot.drive.motorcontrol.pathplanning;

import frc.robot.RobotMap;
import frc.robot.utilities.RobotUtilities;
import java.text.DecimalFormat;
import org.apache.commons.math3.exception.OutOfRangeException;

public class AutonomousPlan {

  public static final double STEP_SIZE = 0.436; // Roughly one 20 ms iteration at 800 RPM


  public int size = 0;
  public double[] x1;
  public double[] y1;
  public double[] heading;
  public double[] curvature;
  public double[] speed;
  public double[] step;

  private double[] distancesToSplineEnd;

  private Spline splineX;
  private Spline splineY;


  /**
   * Constructure takes in existing spline data for storage and passing as a unit.
   * 
   * @param x1 the x coordinates
   * @param y1 the y coordinates
   * @param heading the current direction of movement
   * @param curvature the current curve of the spline
   * @param step the distance between waypoints in time to point
   */
  public AutonomousPlan(
      double[] x1,
      double[] y1,
      double[] heading,
      double[] curvature,
      double[] step) {
    size = x1.length;
    this.x1 = x1;
    this.y1 = y1;
    this.heading = heading;
    this.curvature = curvature;
    speed = new double[size];
    this.step = step;
  }

  /**
   * Creates a spline based on the given parameters.
   * 
   * @param xy array of (x,y) coordinates
   * @param startingSpeedPercentage used if not starting from a stop, always positive
   * @param rampSpeedTimeToFull the max change between steps in percent
   * @param maxSpeedPercentage the percent of max speed to go in the plan
   * @param reverse drive in reverse
   */
  public AutonomousPlan(
      double[][] xy, 
      double startingSpeedPercentage,
      double rampSpeedTimeToFull, 
      double maxSpeedPercentage, 
      boolean reverse) {
        
    distancesToSplineEnd = calculateSumsOfDistanceToPoints(xy);
    splineX = new Spline(distancesToSplineEnd, xy[0]);
    splineY = new Spline(distancesToSplineEnd, xy[1]);

    double[] s = RobotUtilities.arrangeInterval(0.0, 
        distancesToSplineEnd[distancesToSplineEnd.length - 1], STEP_SIZE);
    size = s.length;
    x1 = new double[size];
    y1 = new double[size];
    heading = new double[size];
    curvature = new double[size];
    step = new double[size];
    for (int i = 0; i < s.length; i++) {
      double[] position = calculatePosition(s[i]);
      x1[i] = position[0];
      y1[i] = position[1];
      heading[i] = calculateHeading(s[i]);
      curvature[i] = calculateCurvature(s[i]);
      step[i] = s[i];
    }
    speed = addSpeeds(startingSpeedPercentage, rampSpeedTimeToFull, maxSpeedPercentage, reverse);
  }

  /**
   * Add speed component to spline. Easier after spline is created.
   * 
   * @param startingSpeedPercentage used if not starting from a stop, always positive
   * @param rampSpeedSecondsToFull the max change between steps in percent
   * @param maxSpeedPercentage the percent of max speed to go in the plan
   * @param reverse drive in reverse
   * @return the speeds
   */
  public double[] addSpeeds(
        double startingSpeedPercentage,
        double rampSpeedSecondsToFull, 
        double maxSpeedPercentage, 
        boolean reverse) {
    
    double[] speeds = new double[size];
    double currentSpeed = startingSpeedPercentage;
    double rampSpeed = rampSpeedSecondsToFull;
    for (int i = 0; i < size; i++) {

      // Accelleration Limiting
      currentSpeed += rampSpeed;

      // Cruise Velocity Limiting
      if (currentSpeed > maxSpeedPercentage) {
        currentSpeed = maxSpeedPercentage;
      }

      // Deccelleration Limiting
      if (currentSpeed > (size - (i + 1)) * rampSpeed) {
        currentSpeed = (size - (i + 1)) * rampSpeed;
      }

      // Reverse direction last
      speeds[i] = currentSpeed * (reverse ? -1.0 : 1.0);
    }

    return speeds; // For call chaining
  }

  /**
   * Calculates the sums from the first point to all the following points to determine
   * the length from a intermediate point to the end of the spline. 
   * 
   * @param xy the (x,y) coordinates to connect
   * @return a vector of distances to the spline end
   */
  public static double[] calculateSumsOfDistanceToPoints(double[][] xy) {
    double[] diffX = RobotUtilities.differenceBetweenArrayValues(xy[0]);
    double[] diffY = RobotUtilities.differenceBetweenArrayValues(xy[1]);
    double[] distanceBetweenPoints = new double[xy[0].length - 1];

    for (int i = 0; i < xy[0].length - 1; i++) {
      distanceBetweenPoints[i] = Math.sqrt(Math.pow(diffX[i], 2) + Math.pow(diffY[i], 2));
    }

    double[] sums = new double[xy[0].length];
    double cumulativeSum = 0.0;
    sums[0] = 0;
    for (int i = 0; i < distanceBetweenPoints.length; i++) {
      cumulativeSum += distanceBetweenPoints[i];
      sums[i + 1] = cumulativeSum;
    }

    return sums;
  }

  /**
   * Calculates the position on the spline give a current point.
   *
   * @param point the current point
   */
  double[] calculatePosition(double point) throws OutOfRangeException {
    double[] position = new double[2];
    double x = splineX.calculatePosition(point);
    double y = splineY.calculatePosition(point);
    position[0] = x;
    position[1] = y;
    return position;
  }

  double calculateCurvature(double point) throws OutOfRangeException {
    double firstDerivativeX = splineX.calculateFirstDerivative(point);
    double secondDerivativeX = splineX.calculateSecondDerivative(point);
    double firstDerivativeY = splineY.calculateFirstDerivative(point);
    double secondDerivativeY = splineY.calculateSecondDerivative(point);
    double curvature = (firstDerivativeX * secondDerivativeY - secondDerivativeX * firstDerivativeY)
        / Math.pow(Math.pow(firstDerivativeX, 2.0) + Math.pow(firstDerivativeY, 2.0), (3.0 / 2.0));
    return curvature;
  }

  double calculateHeading(double point) throws OutOfRangeException {
    double firstDerivativeX = splineX.calculateFirstDerivative(point);
    double firstDerivativeY = splineY.calculateFirstDerivative(point);
    double yaw = Math.atan2(firstDerivativeY, firstDerivativeX);
    return yaw;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    DecimalFormat df = new DecimalFormat("#0.00");
    builder.append("Step,\tx,\ty,\theading,\tcurvature,\tspeed\n");
    for (int i = 0; i < size; i++) {
      builder.append(df.format(step[i]));
      builder.append(",\t");
      builder.append(df.format(x1[i]));
      builder.append(",\t");
      builder.append(df.format(y1[i]));
      builder.append(",\t");
      builder.append(df.format(Math.toDegrees(heading[i])));
      builder.append(",\t\t");
      builder.append(df.format(curvature[i]));
      builder.append(",\t\t");
      builder.append(df.format(speed[i]));
      builder.append("\n");
    }
    return builder.toString();
  }

  public static void main(String[] args) {

    double[][] xy = {
      {0.0,   0.0,  -2.0,  -4.0,  -6.0}, 
      {0.0,  10.0,  14.0,  18.0,  22.0}
    };

    //splineFunction
    AutonomousPlan course = new AutonomousPlan(xy, 0.0, 0.2, 0.95, false);
    System.err.println(course);
  }

}
