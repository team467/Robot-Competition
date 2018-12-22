package frc.robot.drive.motorcontrol.pathtracking;

import frc.robot.drive.AutoDrive;
import frc.robot.drive.Drive;
import frc.robot.drive.motorcontrol.pathplanning.SplineCourseData;
import frc.robot.utilities.Utils;

import java.lang.Double;
import java.text.DecimalFormat;

/**
 * TODO.
 */
public class PathCorrection {

  private static AutoDrive drive = Drive.getInstance();

  private FieldPosition fieldState = FieldPosition.getInstance();

  private DecimalFormat df = new DecimalFormat("#0.0");

  /**
   * Maximum steering angle of the vehicle in radians.
   */
  private double maxSteer = Math.toRadians(30.0);

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

  private double[] planX;
  private double[] planY;
  private double[] planHeading;

  /**
   * TODO.
   * @param course TODO
   */
  public void setCoursePlan(SplineCourseData[] course) {
    // splineFunction
    planX = SplineCourseData.vectorize(course, "x");
    planY = SplineCourseData.vectorize(course, "y");
    planHeading = SplineCourseData.vectorize(course, "heading");
    targetIndex = -1;
    lastAchievedIndex = 0;
    done = false;

    // System.out.print("Course: { ");
    // for (int i = 1; i < planX.length; i++) {
    // System.out.print("(" + df.format(planX[i]) + ", " + df.format(planY[i]) + "),
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
    double[] diffActualXtoPlanXs = new double[planX.length - lastAchievedIndex];
    for (int i = 0; i < (planX.length - lastAchievedIndex); i++) {
      diffActualXtoPlanXs[i] = fieldState.fieldX() - planX[i + lastAchievedIndex];
      System.out.println("X -- Current: " + fieldState.fieldX() + " Plan: " + planX[i]
            + " Diff: " + diffActualXtoPlanXs[i]);
    }

    double[] diffActualYtoPlanYs = new double[planY.length - lastAchievedIndex];
    double tempY = fieldState.fieldY();
    for (int i = 0; i < (planX.length - lastAchievedIndex); i++) {
      diffActualYtoPlanYs[i] = tempY - planY[i + lastAchievedIndex];
      System.out.println("Y -- Current: " + fieldState.fieldY() + " Plan: " + planY[i]
            + " Diff: " + diffActualYtoPlanYs[i]);
    }

    positionError = Double.MAX_VALUE;
    // ArrayList<Double> lengthActualPositionToPlanPositions = new ArrayList<Double>();
    for (int i = 0; i < diffActualXtoPlanXs.length; i++) {
      double distanceToWaypoint = Math
          .sqrt(Math.pow(diffActualXtoPlanXs[i], 2) + Math.pow(diffActualYtoPlanYs[i], 2));
      // lengthActualPositionToPlanPositions.add(distanceToWaypoint);
      if (positionError > distanceToWaypoint) {
        if (distanceToWaypoint != 0.0) {
          positionError = distanceToWaypoint;
          targetIndex = i + lastAchievedIndex;
        } else {
          if ((i + 1) == diffActualXtoPlanXs.length) {
            // At end
            positionError = 0.0;
            targetIndex = planX.length - 1;
            done = true;
            return delta;
          }
        }
      }
    }

    // positionError = Collections.min(lengthActualPositionToPlanPositions);
    System.out.println("Position Error: " + positionError);
    // int currentTargetIndex = lengthActualPositionToPlanPositions.indexOf(positionError) 
    //     + lastAchievedIndex;
    // if (positionError == 0 && currentTargetIndex < planX.length) {
    //   currentTargetIndex++;
    //   positionError = lengthActualPositionToPlanPositions.get(currentTargetIndex);
    // }

    // Correct for the front axel position -- ignore for now
    /**
    double targetHeading = Utils.normalizeAngle(
        Math.atan2(fieldState.y() - planY[targetIndex], 
              fieldState.x() - planX[targetIndex])
            - fieldState.heading());
    if (targetHeading > 0.0) {
      positionError = -positionError;
    }
     */

    System.out.println("Target Index: " + targetIndex + " " 
        + " " + lastAchievedIndex + " >> " + planX.length);
    // if (targetIndex > currentTargetIndex) {
    //   currentTargetIndex = targetIndex;
    // }
    // targetIndex = currentTargetIndex;
    if (targetIndex > lastAchievedIndex) {
      lastAchievedIndex = targetIndex;
    }

    System.out.println("Plan Heading "
          + df.format(Math.toDegrees(planHeading[targetIndex])) + " current heading: "
          + df.format(Math.toDegrees(fieldState.heading())));

    // thetaE corrects the heading error
    double thetaE = Utils.normalizeAngle(planHeading[targetIndex] - fieldState.heading());
    // thetaD corrects the cross track error
    double thetaD = 0.0;
    //double thetaD = Math.atan2(fieldState.accelleration() * positionError, fieldState.velocity());
    
    // Change in steering control
    delta = thetaE + thetaD;

    delta = Utils.clip(delta, -maxSteer, maxSteer);

    System.out.println("Move " + df.format(positionError) 
          + " feet with heading change " + df.format(Math.toDegrees(delta)) + " degrees");
    drive.moveWithTurn(positionError, delta);

    return delta;
  }

}
