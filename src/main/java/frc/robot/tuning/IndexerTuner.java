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

        SmartDashboard.putBoolean("Mouth Sensor", indexer.isBallInMouth());
        SmartDashboard.putBoolean("Chamber Sensor", indexer.isBallInChamber());
        
        indexer.stopIndexer();
    }

    public void periodic() {
        double speed = SmartDashboard.getNumber("Speed", 0);
    
        SmartDashboard.getBoolean("Mouth Sensor", indexer.isBallInMouth());
        SmartDashboard.getBoolean("Chamber Sensor", indexer.isBallInChamber());
        
        indexer.setIndexerSpeed(speed);

    }

}