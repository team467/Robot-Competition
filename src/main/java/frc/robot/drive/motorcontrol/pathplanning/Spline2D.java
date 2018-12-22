package frc.robot.drive.motorcontrol.pathplanning;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import frc.robot.utilities.Utils;

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
  public double[] calculateSumsOfDistanceToPoints(double [] xvalues, double[] yvalues) {
    double[] diffX = Utils.differenceBetweenArrayValues(xvalues);
    double[] diffY = Utils.differenceBetweenArrayValues(yvalues);
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
  private double[] calculatePosition(double point) throws OutOfRangeException {
    double[] position = new double[2];
    double x = splineX.calculatePosition(point);
    double y = splineY.calculatePosition(point);
    position[0] = x;
    position[1] = y;
    return position;
  }

  private double calculateCurvature(double point) throws OutOfRangeException {
    double firstDerivativeX = splineX.calculateFirstDerivative(point);
    double secondDerivativeX = splineX.calculateSecondDerivative(point);
    double firstDerivativeY = splineY.calculateFirstDerivative(point);
    double secondDerivativeY = splineY.calculateSecondDerivative(point);
    double curvature = (firstDerivativeX * secondDerivativeY - secondDerivativeX * firstDerivativeY)
        / Math.pow(Math.pow(firstDerivativeX, 2.0) + Math.pow(firstDerivativeY, 2.0), (3.0 / 2.0));
    return curvature;
  }

  private double calculateHeading(double point) throws OutOfRangeException {
    double firstDerivativeX = splineX.calculateFirstDerivative(point);
    double firstDerivativeY = splineY.calculateFirstDerivative(point);
    double yaw = Math.atan2(firstDerivativeY, firstDerivativeX);
    return yaw;
  }

  public SplineCourseData[] calculateSplineCourse(double[] x, double[] y) {
    return calculateSplineCourse(x, y, 0.1);
  }

  /**
   * Creates a spline course plan given a set of x and y coordinates and the desired step size 
   * between the given points.
   * 
   * @param x the x coordinates to interpolate
   * @param y the y coordinates to interpolate
   * @param step the distance between given points for adding interpolated points
   */
  public static SplineCourseData[] calculateSplineCourse(double[] x, double[] y, double step) {
    Spline2D spline = new Spline2D(x, y);
    double[] s = Utils.arrangeInterval(0, 
        spline.distancesToSplineEnd[spline.distancesToSplineEnd.length - 1], step);
    SplineCourseData[] courseData = new SplineCourseData[s.length];
    for (int i = 0; i < s.length; i++) {
      double[] xy = spline.calculatePosition(s[i]);
      courseData[i] = new SplineCourseData();
      courseData[i].coordinateX = xy[0];
      courseData[i].coordinateY = xy[1];
      courseData[i].heading = spline.calculateHeading(s[i]);
      courseData[i].curvature = spline.calculateCurvature(s[i]);
      courseData[i].step = s[i];
    }
    return courseData;
  }

  static void test2dSpline() {

    System.out.println("Spline 2D Test");
    double[] x = {-2.5,  0.0, 2.5, 5.0, 7.5, 3.0, -1.0};
    double[] y = { 0.7, -6.0, 5.0, 6.5, 0.0, 5.0, -2.0};
    double step = 0.1; // [m] distance of each intepolated points

    SplineCourseData[] course = calculateSplineCourse(x, y, step);

    double[] rx = new double[course.length];
    double[] ry = new double[course.length];
    double[] rk = new double[course.length];
    double[] ryaw = new double[course.length];
    double[] s = new double[course.length];

    for (int i = 0; i < course.length; i++) {
      rx[i] = course[i].coordinateX;
      ry[i] = course[i].coordinateY;
      ryaw[i] = Math.toDegrees(course[i].heading);
      rk[i] = course[i].curvature;
      s[i] = course[i].step;
    }

    Plot plt1 = Plot.create();
    plt1.plot()
        .add(Utils.arrayToList(x), Utils.arrayToList(y))
        .label("input")
        .linestyle("-.")
    ;
    plt1.plot()
          .add(Utils.arrayToList(rx), Utils.arrayToList(ry))
          .label("spline")
          .linestyle("-")
      ;
    plt1.xlabel("x[m]");
    plt1.ylabel("y[m]");
    plt1.legend();

    Plot plt2 = Plot.create();
    plt2.plot()
        .add(Utils.arrayToList(s), Utils.arrayToList(ryaw))
        .label("yaw")
        .linestyle("-")
    ;
    plt2.legend();
    plt2.xlabel("line length[m]");
    plt2.ylabel("yaw angle[deg]");

    Plot plt3 = Plot.create();
    plt3.plot()
        .add(Utils.arrayToList(s), Utils.arrayToList(rk))
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

