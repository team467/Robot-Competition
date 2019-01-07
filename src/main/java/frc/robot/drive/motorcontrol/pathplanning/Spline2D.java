package frc.robot.drive.motorcontrol.pathplanning;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import frc.robot.utilities.RobotUtilities;

import java.io.IOException;

import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * 2D Cubic Spline Builder.
 */
public class Spline2D {

  private double[] distancesToSplineEnd;

  private Spline splineX;

  private Spline splineY;

  /**
   * Creates a two dimensional spline from x and y coordinates.
   * 
   * @param xvalues the x values to the points to connect
   * @param yvalues the y values of the points to connect
   */
  public Spline2D(double[] xvalues, double[] yvalues) {
    distancesToSplineEnd = calculateSumsOfDistanceToPoints(xvalues, yvalues);
    splineX = new Spline(distancesToSplineEnd, xvalues);
    splineY = new Spline(distancesToSplineEnd, yvalues);
  }

  /**
   * Calculates the sums from the first point to all the following points to determine
   * the length from a intermediate point to the end of the spline. 
   * 
   * @param xvalues the x values to the points to connect
   * @param yvalues the y values of the points to connect
   * @return a vector of distances to the spline end
   */
  public static double[] calculateSumsOfDistanceToPoints(double [] xvalues, double[] yvalues) {
    double[] diffX = RobotUtilities.differenceBetweenArrayValues(xvalues);
    double[] diffY = RobotUtilities.differenceBetweenArrayValues(yvalues);
    double[] distanceBetweenPoints = new double[xvalues.length - 1];

    for (int i = 0; i < xvalues.length - 1; i++) {
      distanceBetweenPoints[i] = Math.sqrt(Math.pow(diffX[i], 2) + Math.pow(diffY[i], 2));
    }

    double[] sums = new double[xvalues.length];
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

  static void test2dSpline() {

    System.out.println("Spline 2D Test");
    double[][] xy = {
        {-2.5,  0.0, 2.5, 5.0, 7.5, 3.0, -1.0},
        { 0.7, -6.0, 5.0, 6.5, 0.0, 5.0, -2.0}
    };
    double step = 0.1; // [m] distance of each intepolated points
    AutonomousPlan course = new AutonomousPlan(xy, 0.0, 1.0, 1.0, false);

    double[] yaw = new double[course.size];
    for (int i = 0; i < course.size; i++) {
      yaw[i] = Math.toDegrees(course.heading[i]);
    }

    Plot plt1 = Plot.create();
    plt1.plot()
        .add(RobotUtilities.arrayToList(xy[0]), RobotUtilities.arrayToList(xy[1]))
        .label("input")
        .linestyle("-.")
    ;
    plt1.plot()
          .add(RobotUtilities.arrayToList(course.x1), RobotUtilities.arrayToList(course.y1))
          .label("spline")
          .linestyle("-")
      ;
    plt1.xlabel("x[m]");
    plt1.ylabel("y[m]");
    plt1.legend();

    Plot plt2 = Plot.create();
    plt2.plot()
        .add(RobotUtilities.arrayToList(course.step), RobotUtilities.arrayToList(yaw))
        .label("yaw")
        .linestyle("-")
    ;
    plt2.legend();
    plt2.xlabel("line length[m]");
    plt2.ylabel("yaw angle[deg]");

    Plot plt3 = Plot.create();
    plt3.plot()
        .add(RobotUtilities.arrayToList(course.step), RobotUtilities.arrayToList(course.curvature))
        .label("curvature")
        .linestyle("-")
    ;
    plt3.legend();
    plt3.xlabel("line length[m]");
    plt3.ylabel("curvature [1/m]");

    try {
      // plt1.show();
      plt2.show();
      // plt3.show();
    } catch (IOException e) {
      System.err.println(e);
    } catch (PythonExecutionException e) {
      System.err.println(e);
    }
  }

}

