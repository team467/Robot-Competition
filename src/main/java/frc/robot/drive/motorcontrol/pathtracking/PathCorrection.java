package frc.robot.drive.motorcontrol.pathtracking;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import frc.robot.RobotMap;
import frc.robot.drive.AutoDrive;
import frc.robot.drive.Drive;
import frc.robot.drive.motorcontrol.pathplanning.SplineCourseData;
import frc.robot.simulator.DriveSimulator;
import frc.robot.utilities.Utils;

/**
 */
public class PathCorrection {

    private static AutoDrive drive = (RobotMap.useSimulator) ? DriveSimulator.getInstance() : Drive.getInstance();
    
    private FieldPosition fieldState = FieldPosition.getInstance();

    private DecimalFormat df = new DecimalFormat("#0.0");

    /**
     * Maximum steering angle of the vehicle in radians
     */
    private double maxSteer = Math.toRadians(30.0);

    /**
     *
     */
    private int targetIndex = -1;
    public int targetIndex() {
        return targetIndex;
    }

    /**
     *
     */
    private double positionError = 0.0;
    public double headingError() {
        return positionError;
    }

    private double[] planX;
    private double[] planY;
    private double[] planHeading;
    public void setCoursePlan(SplineCourseData[] course ) {
        //splineFunction
        planX = SplineCourseData.vectorize(course, "x");
        planY = SplineCourseData.vectorize(course, "y");
        planHeading = SplineCourseData.vectorize(course, "heading");
        targetIndex = -1;

        System.out.print("Course: { ");
        for (int i = 1; i < planX.length; i++) {
            System.out.print("(" + df.format(planX[i]) + ", " + df.format(planY[i]) + "), ");
        }
        System.out.println(" }");
    }


    /**
     * Stanley steering control
     *
     * @param planX
     * @param planY
     * @param planHeading
     * @return
     */
    public double stanleyControl() {

    // double acceleration = state.pidControl(targetSpeed, state.velocity());
    // double delta = state.stanleyControl(courseX, courseY, courseHeading, targetIndex);
    // targetIndex = state.currentTargetIndex();
    // state.update(acceleration, delta);

         double delta = 0.0;
        if (targetIndex >= planX.length) {
            return delta;
        }

        // Search nearest point index
        double[] diffActualXtoPlanXs = new double[planX.length];
        for (int i=0; i < planX.length; i++) {
            diffActualXtoPlanXs[i] = fieldState.x() - planX[i];
//            System.out.println("X -- Current: " + fieldState.x() + " Plan: " + planX[i] + " Diff: " + diffActualXtoPlanXs[i]);
        }

        double[] diffActualYtoPlanYs = new double[planY.length];
        double tempY = fieldState.y();
        for (int i=0; i < planY.length; i++) {
            diffActualYtoPlanYs[i] = tempY - planY[i];
//            System.out.println("Y -- Current: " + fieldState.y() + " Plan: " + planY[i] + " Diff: " + diffActualYtoPlanYs[i]);
        }

        ArrayList<Double> lengthActualPositionToPlanPositions = new ArrayList<>();
        for (int i = 0; i < diffActualXtoPlanXs.length; i++) {
            double distanceToWaypoint = Math.sqrt(Math.pow(diffActualXtoPlanXs[i], 2) + Math.pow(diffActualYtoPlanYs[i], 2));
            lengthActualPositionToPlanPositions.add(distanceToWaypoint);
        }

        positionError = Collections.min(lengthActualPositionToPlanPositions);
    //    System.out.println("Position Error: " + positionError);
        int currentTargetIndex = lengthActualPositionToPlanPositions.indexOf(positionError);
        if (positionError == 0 && currentTargetIndex < planX.length) {
            currentTargetIndex++;
            positionError = lengthActualPositionToPlanPositions.get(currentTargetIndex);
        } 

        double targetHeading = Utils.normalizeAngle(Math.atan2(fieldState.y() - planY[currentTargetIndex], fieldState.x() - planX[currentTargetIndex]) - fieldState.heading());
        if (targetHeading > 0.0) {
            positionError = -positionError;
        }

    //    System.out.println("Target Index: " + targetIndex + " " + currentTargetIndex + " >> " + planX.length);
        if (targetIndex > currentTargetIndex) {
            currentTargetIndex = targetIndex;
        }
        targetIndex = currentTargetIndex;
        if (targetIndex == planX.length) {
            return delta;
        }

  //      System.out.println("Plan Heading " + df.format(Math.toDegrees(planHeading[targetIndex]))  + " current heading:  " + df.format(Math.toDegrees(fieldState.heading())));

        // thetaE corrects the heading error
        double thetaE = Utils.normalizeAngle(planHeading[targetIndex] - fieldState.heading());
        // thetaD corrects the cross track error
        double thetaD = Math.atan2(fieldState.accelleration() * positionError, fieldState.velocity());
        // Change in steering control
        delta = thetaE + thetaD;

        delta = Utils.clip(delta, -maxSteer, maxSteer);

//        System.out.println("Move " + df.format(positionError) + " feet with heading change " + df.format(Math.toDegrees(delta)) + " degrees");
        drive.moveWithTurn(positionError, delta);

        return delta;
    }

}

