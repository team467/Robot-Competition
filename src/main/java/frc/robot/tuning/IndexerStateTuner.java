/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.tuning;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.logging.RobotLogManager;
import frc.robot.gamepieces.AbstractLayers.IndexerAL.SensorTestMode;
import frc.robot.gamepieces.GamePieceController.DriverInput;

public class IndexerStateTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    IndexerAL indexer;
    GamePieceController gamePieceController;

    IndexerStateTuner() {
        indexer = IndexerAL.getInstance();
    }

    public void init() {
        SmartDashboard.putBoolean("Shoot", false);

        SmartDashboard.putNumber("Mouth Distance", 0);
        SmartDashboard.putNumber("Chamber Distance", 0);
        SmartDashboard.putBoolean("mouthSensor", false);
        SmartDashboard.putBoolean("chamberSensor", false);
        SmartDashboard.putBoolean("forwardInput", false);
        SmartDashboard.putBoolean("reverseInput", false);
        SmartDashboard.putBoolean("setAuto", false);
        SmartDashboard.putBoolean("Shooter asking for ball", false);

        gamePieceController = GamePieceController.getInstance();

        indexer.stopIndexer();
    }

    public void periodic() {
        boolean shoot = SmartDashboard.getBoolean("Shoot", false);
    
        boolean mouthSensor = SmartDashboard.getBoolean("mouthSensor", false);
        boolean chamberSensor = SmartDashboard.getBoolean("chamberSensor", false);

        boolean driverForwardInput = SmartDashboard.getBoolean("forwardInput", false);
        boolean driverReverseInput = SmartDashboard.getBoolean("reverseInput", false);

        boolean indexToAuto = SmartDashboard.getBoolean("setAuto", false);

        boolean shooterWantsBall = SmartDashboard.getBoolean("Shooter asking for ball", false);
        gamePieceController.periodic();

        if (shooterWantsBall == true) {
            LOGGER.debug("Shooter Wants ball!!!");
            gamePieceController.shooterWantsBall = true;
        } else {
            gamePieceController.shooterWantsBall = false;
        }

        if (indexToAuto == true) {
            gamePieceController.autoInput(DriverInput.FORCE_AUTO_TRUE);
            gamePieceController.IndexAuto = true;
        }  else {
            gamePieceController.autoInput(DriverInput.FORCE_AUTO_FALSE);
            gamePieceController.IndexAuto = false;
        }

        if (mouthSensor == true) {
            LOGGER.debug("mouthSensor is true");
            indexer.setForceBallInMouth(SensorTestMode.FORCE_TRUE);
        } else {
            indexer.setForceBallInMouth(SensorTestMode.FORCE_FALSE);
        }

        if (chamberSensor == true) {
            LOGGER.debug("ChamberSensor is true");
            indexer.setForceBallInChamber(SensorTestMode.FORCE_TRUE);
        } else {
            indexer.setForceBallInChamber(SensorTestMode.FORCE_FALSE);
        }
    
        if (driverForwardInput == true) {
            LOGGER.debug("mouthSensor is true");
            gamePieceController.cellsForward(DriverInput.FORCE_TRUE);
        } else {
            gamePieceController.cellsForward(DriverInput.FORCE_FALSE);
        }

        if (driverReverseInput == true) {
            LOGGER.debug("ChamberSensor is true");
            gamePieceController.cellsReverse(DriverInput.FORCE_TRUE);
        } else {
            gamePieceController.cellsReverse(DriverInput.FORCE_FALSE);
        
        }
        //SmartDashboard.putNumber("Mouth Distance", indexer.getMouthDistance());
        //SmartDashboard.putNumber("Chamber Distance", indexer.getChamberDistance());
    }

}