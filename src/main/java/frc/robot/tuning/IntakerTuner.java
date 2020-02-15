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

public class IntakerTuner implements Tuner {

    IntakeAL intakeAL;

    IntakerTuner() {
        intakeAL = IntakeAL.getInstance();

    }

    public void init() {
        

        SmartDashboard.putNumber("InntakeAL arm", 0);
        SmartDashboard.putNumber("IntakeAL Roller", 0);
        
        SmartDashboard.putNumber("IntakeAL top limit Switch", 0);
        SmartDashboard.putNumber("IntakeAL bottom limit switch", 0);
        
        SmartDashboard.putBoolean("Sucking", false);
        SmartDashboard.putBoolean("Arm Raise", false);

        IntakeAL.callArmStop();
        IntakeAL.callRollerStop();
        
    }

    public void periodic() {
        //inputs
        boolean sucking = SmartDashboard.getBoolean("Sucking", false);
        boolean armActivated = SmartDashboard.getBoolean("Arm Raise", false);
        if(sucking){
            IntakeAL.callBackward();
        }else{
            IntakeAL.callRollerStop();
        }
        if(armActivated){
            IntakeAL.callUp();
        }else{
            IntakeAL.callArmStop();
        }
        //display values

        SmartDashboard.putNumber("InntakeAL arm", 0);
        SmartDashboard.putNumber("IntakeAL Roller", 0);
    }
    
}