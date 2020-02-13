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

    IndexerAL indexerAL;

    IndexerTuner() {
        indexerAL = IndexerAL.getInstance();
        // IndexerALSensors = IndexerAL.getSensors();
    }

    public void init() {
        SmartDashboard.putNumber("key", 0);

        // display values
        SmartDashboard.putNumber("IndexerAL Chamber Sensor Value", 0);
        SmartDashboard.putNumber("IndexerAL Mouth Sensor Value", 0);
        
        IndexerAL.callStop();
    }

    public void periodic() {
        boolean feeding = SmartDashboard.getBoolean("Feed", false);
        boolean override = SmartDashboard.getBoolean("Override TOF", false);
        boolean overrideChamber = SmartDashboard.getBoolean("Override Chamber", false);
        boolean overrideMouth = SmartDashboard.getBoolean("Override Mouth", false);
        if (feeding) {
            indexerAL.indexerBeltDirection(IndexerAL.setBelts.FORWARD);
        }
        indexerAL.override=override;
        indexerAL.mouthOverride=overrideMouth;
        indexerAL.chamberOverride=overrideChamber;
    }

}
