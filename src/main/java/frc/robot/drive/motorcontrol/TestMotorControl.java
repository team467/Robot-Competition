package frc.robot.drive.motorcontrol;

import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;
import frc.robot.drive.motorcontrol.pathplanning.Spline2D;
import frc.robot.drive.motorcontrol.pathtracking.FieldPosition;
import frc.robot.drive.motorcontrol.pathtracking.PathCorrection;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

public class TestMotorControl {

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(TestMotorControl.class.getName());

  private static final double INITIAL_X_POSITION = 0.0;
  private static final double INITIAL_Y_POSITION = 0.0;
  private static final double INITIAL_HEADING = 90.0;
  private static final double INITIAL_VELOCITY = 0.0;

  private PathCorrection controller;
  private FieldPosition state = FieldPosition.getInstance();

  // Course Data
  private AutonomousPlan course;

  /**
   * Should be converted to JUnit Test.
   */
  public TestMotorControl() {
    state.init(INITIAL_X_POSITION, INITIAL_Y_POSITION, INITIAL_HEADING, INITIAL_VELOCITY, 0.0, 0.0);
    controller = new PathCorrection();

    // double[][] xy = {
    //   {0.0,  0.0,  2.0,  4.0,  6.0}, 
    //   {0.0, 10.0, 14.0, 18.0, 22.0}
    // };
    double[][] xy = {
      {0.0,  0.0,  2.0,  4.0,   6.0, 20.0}, 
      {0.0,  2.0,  7.0,  8.0,   4.0,   2.0}
    };


    //splineFunction
    course = new AutonomousPlan(xy, INITIAL_VELOCITY, 0.2, 0.95, false);
    controller.setCoursePlan(course);
    LOGGER.debug(course);
  }

  public void periodic() {
    controller.stanleyControl();
    LOGGER.debug(state);
  }

}