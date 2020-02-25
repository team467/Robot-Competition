/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.tuning;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.IntakeAL;

public class IntakeTuner implements Tuner {

    IntakeAL intake;

    enum ArmState {
        UP, DOWN, STOP;
    }

    ArmState currentState = ArmState.STOP; 

    IntakeTuner() {
        intake = IntakeAL.getInstance();

    }

    public void init() {
        SmartDashboard.putBoolean("Top Limit Switch", false);
        SmartDashboard.putBoolean("Bottom Limit Switch", false);

        SmartDashboard.putBoolean("Arm Up", false);
        SmartDashboard.putBoolean("Arm Down", false);

        SmartDashboard.putNumber("Speed", 0);

        IntakeAL.callArmStop();
        IntakeAL.callRollerStop();
    }

    public void periodic() {
        boolean armUp = SmartDashboard.getBoolean("Arm Up", false);
        boolean armDown = SmartDashboard.getBoolean("Arm Down", false);
        double speed = SmartDashboard.getNumber("Speed", 0);

        intake.setArmSpeed(speed);

        if (armUp) {
            if (currentState != ArmState.UP) {
                SmartDashboard.putBoolean("Arm Down", false);
                currentState = ArmState.UP;
            }
            
            if (intake.getTopLimit()) {
                SmartDashboard.putBoolean("Arm Up", false);
                intake.stopArm();
            } else {
                intake.armUp();
            }
        } else if (armDown) {
            if (currentState != ArmState.DOWN) {
                SmartDashboard.putBoolean("Arm Up", false);
                currentState = ArmState.DOWN;
            }            
            
            if (intake.getBottomLimit()) {
                SmartDashboard.putBoolean("Arm Down", false);
                intake.stopArm();
            } else {
                intake.armDown();
            }
        } else {
            intake.stopArm();
            currentState = ArmState.STOP;
        }

        SmartDashboard.putBoolean("Top Limit Switch", intake.getTopLimit());
        SmartDashboard.putBoolean("Bottom Limit Switch", intake.getBottomLimit());
    }
}