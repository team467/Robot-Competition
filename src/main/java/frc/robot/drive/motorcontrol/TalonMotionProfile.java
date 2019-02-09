package frc.robot.drive.motorcontrol;

import com.ctre.phoenix.motion.TrajectoryPoint;

import frc.robot.drive.WpiTalonSrxInterface;

public class TalonMotionProfile {
    public static void startFilling(double[][] profileL, double[][] profileR, int totalCnt, WpiTalonSrxInterface talonL , WpiTalonSrxInterface talonR) {
        TrajectoryPoint pointL = new TrajectoryPoint();
        TrajectoryPoint pointR = new TrajectoryPoint();

        talonL.clearMotionProfileTrajectories();
        talonR.clearMotionProfileTrajectories();

        for (int i = 0; i < totalCnt; ++i) {
            pointL.position = profileL[i][0];
            pointL.velocity = profileL[i][1];
            pointL.timeDur = (int) profileL[i][2];
        
            pointR.position = profileR[i][0];
            pointR.velocity = profileR[i][1];
            pointR.timeDur = (int) profileR[i][2];

            pointL.zeroPos = false;
            pointR.zeroPos = false;

            if (i == 0) {
                pointL.zeroPos = true;
                pointR.zeroPos = true;
            }

            pointL.isLastPoint = false;
            pointR.isLastPoint = false;

            if ((i + 1) == totalCnt) {
                pointL.isLastPoint = true;
                pointR.isLastPoint = true;
            }

            talonL.pushMotionProfileTrajectory(pointL);
            talonR.pushMotionProfileTrajectory(pointR);
        }
    }
} 