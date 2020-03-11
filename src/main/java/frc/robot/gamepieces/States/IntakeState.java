package frc.robot.gamepieces.States;

import org.apache.logging.log4j.Logger;

import frc.robot.gamepieces.AbstractLayers.IntakeAL;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;

import frc.robot.logging.RobotLogManager;

public class IntakeState {

    private static IntakeState instance = null;
    private static final Logger LOGGER = RobotLogManager.getMainLogger(IntakeState.class.getName());
    private static IndexerAL indexer = IndexerAL.getInstance();
    private static GamePieceController gamePieceController = GamePieceController.getInstance();
    private static IntakeAL intake = IntakeAL.getInstance();

    public enum IntakerArm {
        ARM_UP, ARM_DOWN

    }

    public enum IntakerRollers {
        ROLLERS_IN, ROLLERS_OUT, ROLLERS_OFF

    }

    public static IntakeState getInstance() {
        if (instance == null) {
          instance = new IntakeState();
        }
        return instance;
      }
    

    public void setIntakeArm(IntakerArm arm, IntakerRollers rollers) {
        switch (arm) {

        default:
            LOGGER.error("message");
        case ARM_UP:
            IntakeAL.callUp();
            break;
        case ARM_DOWN:
            IntakeAL.callDown();
            break;
        }
        switch (rollers) {

        case ROLLERS_IN:
            IntakeAL.callBackward();
            break;
        case ROLLERS_OUT:
            IntakeAL.callForward();
            break;
        default:
        case ROLLERS_OFF:
            IntakeAL.callRollerStop();
            break;
        }
    }
    public void setIntakeArm(IntakerArm arm) {
        switch (arm) {

        default:
            LOGGER.error("message");
        case ARM_UP:
            IntakeAL.callUp();
            break;

        case ARM_DOWN:
            IntakeAL.callDown();
            break;
        }
    }

    public void setIntakeRoller(IntakerRollers rollers) {
        switch (rollers) {
        case ROLLERS_IN:
            IntakeAL.callBackward();
            break;
        case ROLLERS_OUT:
            IntakeAL.callForward();
            break;
        default:
        case ROLLERS_OFF:
            IntakeAL.callRollerStop();
            break;
        }
    }
}

