package frc.robot.drive.motorcontrol.pathtracking;

import frc.robot.drive.Drive;
import frc.robot.drive.motorcontrol.pathplanning.AutonomousPlan;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.communications.RobotData;
import frc.robot.utilities.RobotUtilities;

import java.lang.Double;
import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

/**
 * TODO.
 */
public class PathCorrection {

  private static Drive drive = Drive.getInstance();

  private static RobotData data = RobotData.getInstance();

  private FieldPosition fieldState = FieldPosition.getInstance();

  private AutonomousPlan course;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(PathCorrection.class.getName());
  private DecimalFormat df = new DecimalFormat("#0.0");

  /**
   * Maximum steering angle of the vehicle in radians.
   */
  private double maxSteer = Math.toRadians(360.0);

  /**
   * TODO.
   */
  private int targetIndex = -1;

  public int targetIndex() {
    return targetIndex;
  }

  /**
   * TODO.
   */
  private int lastAchievedIndex = 0;

  public int lastAchievedIndex() {
    return lastAchievedIndex;
  }

  /**
   * TODO.
   */
  private double positionError = 0.0;

  private boolean done = false;

  public double headingError() {
    return positionError;
  }

  /**
   * TODO.
   * @param course TODO
   */
  public void setCoursePlan(AutonomousPlan course) {
    // splineFunction
    this.course = course;
    data.course(course);
    targetIndex = -1;
    lastAchievedIndex = 0;
    done = false;

    // System.out.print("Course: { ");
    // for (int i = 1; i < course.x1.length; i++) {
    // System.out.print("(" + df.format(course.x1[i]) + ", " + df.format(course.y1[i]) + "),
    // ");
    // }
    // System.out.println(" }");
  }

  /**
   * Stanley steering control.
   *
   * @return the difference in heading between plan and actual
   */
  public double stanleyControl() {

    // double acceleration = state.pidControl(targetSpeed, state.velocity());
    // double delta = state.stanleyControl(courseX, courseY, courseHeading,
    // targetIndex);
    // targetIndex = state.currentTargetIndex();
    // state.update(acceleration, delta)

    double delta = 0.0;
    if (done) {
      return delta;
    }

    // Search nearest point index
    double[] diffActualXtoPlanXs = new double[course.x1.length - lastAchievedIndex];
    for (int i = 0; i < (course.x1.length - lastAchievedIndex); i++) {
      diffActualXtoPlanXs[i] = fieldState.x1() - course.x1[i + lastAchievedIndex];
      LOGGER.debug("X -- Current: {} Plan: {} Diff: {}",
          fieldState.x1(), course.x1[i], diffActualXtoPlanXs[i]);
    }

    double[] diffActualYtoPlanYs = new double[course.y1.length - lastAchievedIndex];
    double tempY = fieldState.y1();
    for (int i = 0; i < (course.x1.length - lastAchievedIndex); i++) {
      diffActualYtoPlanYs[i] = tempY - course.y1[i + lastAchievedIndex];
      LOGGER.debug("Y -- Current: {} Plan: {} Diff: {}",
          fieldState.y1(), course.y1[i], diffActualYtoPlanYs[i]);
    }

    positionError = Double.MAX_VALUE;
    // ArrayList<Double> lengthActualPositionToPlanPositions = new ArrayList<Double>();
    for (int i = 0; i < diffActualXtoPlanXs.length; i++) {
      double distanceToWaypoint = Math
          .sqrt(Math.pow(diffActualXtoPlanXs[i], 2) + Math.pow(diffActualYtoPlanYs[i], 2));
      LOGGER.debug("Position Error {} Distance to Waypoint: {}", positionError, distanceToWaypoint);
      // lengthActualPositionToPlanPositions.add(distanceToWaypoint);
      if (positionError > distanceToWaypoint) {
        if (distanceToWaypoint != 0.0) {
          positionError = distanceToWaypoint;
          targetIndex = i + lastAchievedIndex;
        } else {
          if ((i + 1) == diffActualXtoPlanXs.length) {
            // At end
            positionError = 0.0;
            targetIndex = course.x1.length - 1;
            done = true;
            return delta;
          }
        }
      }
    }

    // positionError = Collections.min(lengthActualPositionToPlanPositions);
    LOGGER.debug("Position Error: {}", positionError);
    // int currentTargetIndex = lengthActualPositionToPlanPositions.indexOf(positionError) 
    //     + lastAchievedIndex;
    // if (positionError == 0 && currentTargetIndex < course.x1.length) {
    //   currentTargetIndex++;
    //   positionError = lengthActualPositionToPlanPositions.get(currentTargetIndex);
    // }

    // Correct for the front axel position -- ignore for now
    /**
    double targetHeading = Utils.normalizeAngle(
        Math.atan2(fieldState.y() - course.y1[targetIndex], 
              fieldState.x() - course.x1[targetIndex])
            - fieldState.heading());
    if (targetHeading > 0.0) {
      positionError = -positionError;
    }
     */

    LOGGER.debug("Target Index: {} last: {} total: {}",
        targetIndex, lastAchievedIndex, course.x1.length);
    // if (targetIndex > currentTargetIndex) {
    //   currentTargetIndex = targetIndex;
    // }
    // targetIndex = currentTargetIndex;
    if (targetIndex > lastAchievedIndex) {
      lastAchievedIndex = targetIndex;
    }

    LOGGER.debug("Heading - Plan: {}, Current: {}", 
        df.format(Math.toDegrees(course.heading[targetIndex])),
        df.format(Math.toDegrees(fieldState.heading())));

    // thetaE corrects the heading error
    double thetaE = RobotUtilities.normalizeAngle(course.heading[targetIndex] - fieldState.heading());
    // thetaD corrects the cross track error
    double thetaD = 0.0;
    //double thetaD = Math.atan2(fieldState.accelleration() * positionError, fieldState.velocity());
    
    // Change in steering control
    delta = thetaE + thetaD;

    delta = RobotUtilities.clip(delta, -maxSteer, maxSteer);

    LOGGER.debug("Move {} feet with Heading change {} degrees",
        df.format(positionError), df.format(Math.toDegrees(delta)));

    // Normalize for arcade drive
    double rotationSpeed = delta / maxSteer;
    double forwardSpeed = 1.0 - rotationSpeed;
    rotationSpeed *= course.speed[targetIndex];
    forwardSpeed *= course.speed[targetIndex];
    // double normalizer = Math.abs(forwardSpeed) + Math.abs(rotationSpeed);
    // if (normalizer != 0.0) {
    //   rotationSpeed /= normalizer;
    //   forwardSpeed /= normalizer;
    // }

    LOGGER.debug("Speed - Forward = {},  Rotation = {}",
        df.format(forwardSpeed), df.format(rotationSpeed));

    // Arcade Drive is positive counterclockwise, but field coordinates
    // are positive to the right, so flip roation speed
    drive.arcadeDrive(forwardSpeed, -rotationSpeed, false); 

    return delta;
  }

}
