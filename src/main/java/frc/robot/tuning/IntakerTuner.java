/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.tuning;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamepieces.AbstractLayers.IntakeAL;

public class IntakerTuner implements Tuner {

    IntakeAL IntakeAL;

    IntakerTuner() {
        IntakeAL = IntakeAL.getInstance();

    }

    public void init() {
        

        SmartDashboard.putNumber("InntakeAL arm", 0);
        SmartDashboard.putNumber("IntakeAL Roller", 0);
        
        SmartDashboard.putNumber("IntakeAL top limitSwitch", 0);
        SmartDashboard.putNumber("IntakeAL bottom limit switch", 0);
        
        SmartDashboard.putBoolean("Sucking", false);

        IntakeAL.callArmStop();
        IntakeAL.callRollerStop();
        
    }

    public void periodic() {

    }
    
}
