/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.tuning;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.logging.RobotLogManager;

public class IndexerTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    IndexerAL indexer;

    IndexerTuner() {
        indexer = IndexerAL.getInstance();
    }

    public void init() {
        SmartDashboard.putNumber("Stage 1 Speed", 0);
        SmartDashboard.putNumber("Stage 2 Speed", 0);

        SmartDashboard.putBoolean("Mouth TOF", false);
        SmartDashboard.putBoolean("Chamber TOF", false);
        SmartDashboard.putNumber("Mouth Dist", 0);
        SmartDashboard.putNumber("Chamber Dist", 0);
        
        indexer.stopIndexer();
    }

    public void periodic() {
        double s1Speed = SmartDashboard.getNumber("Stage 1 Speed", 0);
        double s2Speed = SmartDashboard.getNumber("Stage 2 Speed", 0);
    
        SmartDashboard.putBoolean("Mouth TOF", indexer.isBallInMouth());
        SmartDashboard.putBoolean("Chamber TOF", indexer.isBallInChamber());
        SmartDashboard.putNumber("Mouth Dist", indexer.getMouthDistance());
        SmartDashboard.putNumber("Chamber Dist", indexer.getChamberDistance());
        
        indexer.setIndexerFirstStageSpeed(s1Speed);
        indexer.setIndexerSecondStageSpeed(s2Speed);
    }
}