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
        SmartDashboard.putNumber("Speed", 0);

        SmartDashboard.putBoolean("Mouth TOF", false);
        SmartDashboard.putBoolean("Chamber TOF", false);

        SmartDashboard.putNumber("Mouth Distance", 0);
        SmartDashboard.putNumber("Chamber Distance", 0);

        indexer.stopIndexer();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);

        indexer.setIndexerSpeed(speed);

        SmartDashboard.putBoolean("Mouth TOF", indexer.isBallInMouth());
        SmartDashboard.putBoolean("Chamber TOF", indexer.isBallInChamber());

        SmartDashboard.putNumber("Mouth Distance", indexer.getMouthDistance());
        SmartDashboard.putNumber("Chamber Distance", indexer.getChamberDistance());
    }

}
