/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import org.apache.logging.log4j.Logger;

import frc.robot.gamepieces.AbstractLayers.IntakeAL;
import frc.robot.logging.RobotLogManager;

public class IntakeState {

    private static IntakeState instance = null;
    private static final Logger LOGGER = RobotLogManager.getMainLogger(IntakeState.class.getName());

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
            IntakeAL.callFoward();
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
            IntakeAL.callFoward();
            break;
        default:
        case ROLLERS_OFF:
            IntakeAL.callRollerStop();
            break;
        }
    }
}

