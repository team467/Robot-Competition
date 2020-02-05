/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces;

import org.apache.logging.log4j.Logger;

import frc.robot.gamepieces.IntakeController;
import frc.robot.logging.RobotLogManager;

public class Intaker {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(Intaker.class.getName());

    public enum IntakerArm {
        ARM_UP, ARM_DOWN

    }

    public enum IntakerRollers {
        ROLLERS_IN, ROLLERS_OUT, ROLLERS_OFF

    }

    public void periodic(IntakerArm arm, IntakerRollers rollers) {
        switch (arm) {

        default:
            LOGGER.error("message");
        case ARM_UP:
            IntakeController.callUp();
            break;

        case ARM_DOWN:
            IntakeController.callDown();
            break;
        }
        switch (rollers) {

        case ROLLERS_IN:
            IntakeController.callBackward();
            break;
        case ROLLERS_OUT:
            IntakeController.callFoward();
            break;
        default:
        case ROLLERS_OFF:
            IntakeController.callRollerStop();
            break;
        }
    }
}
