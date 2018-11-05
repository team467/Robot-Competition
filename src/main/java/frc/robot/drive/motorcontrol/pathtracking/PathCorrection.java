package frc.robot.drive.motorcontrol.pathtracking;

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

        // Search nearest point index
        double[] diffActualXtoPlanXs = new double[planX.length];
        for (int i=0; i < planX.length; i++) {
            diffActualXtoPlanXs[i] = fieldState.x() - planX[i];
        }

        double[] diffActualYtoPlanYs = new double[planY.length];
        for (int i=0; i < planY.length; i++) {
            diffActualYtoPlanYs[i] = fieldState.y() - planY[i];
        }

        ArrayList<Double> lengthActualPositionToPlanPositions = new ArrayList<>();
        for (int i = 0; i < diffActualXtoPlanXs.length; i++) {
            lengthActualPositionToPlanPositions.add(Math.sqrt(Math.pow(diffActualXtoPlanXs[i], 2) + Math.pow(diffActualYtoPlanYs[i], 2)));
        }

        positionError = Collections.min(lengthActualPositionToPlanPositions);

        int currentTargetIndex = lengthActualPositionToPlanPositions.indexOf(positionError);

        double targetHeading = Utils.normalizeAngle(Math.atan2(fieldState.y() - planY[currentTargetIndex], fieldState.x() - planX[currentTargetIndex]) - fieldState.heading());
        if (targetHeading > 0.0) {
            positionError = -positionError;
        }

        if (targetIndex >= currentTargetIndex) {
            currentTargetIndex = targetIndex;
        }
        targetIndex = currentTargetIndex;

        System.out.println("Plan Heading " + planHeading[currentTargetIndex]  + " current heading:  " + fieldState.heading());

        // thetaE corrects the heading error
        double thetaE = Utils.normalizeAngle(planHeading[currentTargetIndex] - fieldState.heading());
        // thetaD corrects the cross track error
        double thetaD = Math.atan2(fieldState.accelleration() * positionError, fieldState.velocity());
        // Change in steering control
        double delta = thetaE + thetaD;

        delta = Utils.clip(delta, -maxSteer, maxSteer);

        System.out.println("Move " + positionError + " feet with heading change " + Math.toDegrees(delta) + " degrees");
        drive.moveWithTurn(positionError, delta);

        return delta;
    }

}

