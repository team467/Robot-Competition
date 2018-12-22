package frc.robot.drive.motorcontrol;

import frc.robot.drive.motorcontrol.pathplanning.Spline2D;
import frc.robot.drive.motorcontrol.pathplanning.SplineCourseData;
import frc.robot.drive.motorcontrol.pathtracking.FieldPosition;
import frc.robot.drive.motorcontrol.pathtracking.PathCorrection;

public class TestMotorControl {

  private static final double INITIAL_X_POSITION = 0.0;
  private static final double INITIAL_Y_POSITION = 0.0;
  private static final double INITIAL_HEADING = 90.0;
  private static final double INITIAL_VELOCITY = 0.0;

  private PathCorrection controller;
  private FieldPosition state = FieldPosition.getInstance();

  // Course Data
  private SplineCourseData[] course;

  /**
   * Should be converted to JUnit Test.
   */
  public TestMotorControl() {
    state.init(INITIAL_X_POSITION, INITIAL_Y_POSITION, INITIAL_HEADING, INITIAL_VELOCITY, 0.0, 0.0);
    controller = new PathCorrection();

    // Target course waypoints
    double[] ax = {0.0, 0.0};
    double[] ay = {0.0, 3.0};
    double stepSize = 1.0;

    //splineFunction
    course = Spline2D.calculateSplineCourse(ax, ay, stepSize);
    controller.setCoursePlan(course);
  }

  public void periodic() {
    controller.stanleyControl();
    //System.out.println(state);
  }

}