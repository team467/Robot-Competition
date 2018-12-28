package frc.robot.sensors;

import frc.robot.drive.motorcontrol.pathplanning.Spline2D;
import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;

import java.util.ArrayList;

public class PointTracker {

  private ArrayList<Point> points = new ArrayList<Point>();


  /**
   * These are temporary tracked points for drawing.
   */
  private Point[] ephemeralPoints;
  private int ephemeralPointSize;
  private boolean enableEphemeralTracking;
  private int count;

  private class Point {
    final double time;
    final double x1;
    final double y1;

    Point(
        final double time, 
        final double x, 
        final double y) {
      this.time = time;
      this.x1 = x;
      this.y1 = y;
    }

  }

  /**
   * Creates a point tracker that separately tracks all points from the start
   * as well as a temporary collection of points.
   * 
   * @param ephemeralPointSize the number of points to track in the rolling cache
   */
  public PointTracker(int ephemeralPointSize) {
    enableEphemeralTracking = true;
    this.ephemeralPointSize = ephemeralPointSize;
    ephemeralPoints = new Point[ephemeralPointSize];
    count = 0;
  }

  /**
   * Adds a new point to the tracking list, including both the permanent list 
   * as well as the temporary list if that is enabled.
   *  
   * @param time the time of the reading
   * @param x the x coordinate, may be field offset or robot offset
   * @param y the y coordinate, may be field offset or robot offset
   */
  public void add(double time, double x, double y) {
    Point point = new Point(time, x, y);
    points.add(point);
    if (enableEphemeralTracking) {
      ephemeralPoints[count % ephemeralPointSize] = point;
      count++;
    }
  }

  /**
   * Creates a line that connects all the points, smoothing the transitions.
   * 
   * @return the data for the created spline
   */
  public AutonomousPlan spline() {
    // TODO Figure out how to just create a line and draw it
    double duration = points.get(points.size() - 1).time - points.get(0).time;
    double[][] xy = new double[points.size()][points.size()];
    for (int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      xy[0][i] = point.x1;
      xy[1][i] = point.y1;
    }
    return new AutonomousPlan(xy, 0.0, 1.0, 1.0, false);
  }

}