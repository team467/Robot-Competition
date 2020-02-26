/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import static frc.robot.gamepieces.AbstractLayers.ClimberAL.SolenoidLock.*;
import static frc.robot.gamepieces.AbstractLayers.ClimberAL.climberSpeed.*;

/**
 * Add your docs here.
 */
public enum ClimberState implements State {
    InitialLocked {
        public boolean upButtonPressed;
        public boolean climberEnabled;

        public void enter() {
            // noop
        }

        public State action() {
            climberEnabled = GamePieceController.getInstance().climberIsEnabled();
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();

            climber.setSpeed(OFF);
            climber.setLock(LOCK);
            if (climberEnabled && upButtonPressed) {
                return UnlockingUp;

            }
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    UnlockingUp {
        public boolean upButtonPressed;
        public double entryPosition;
        public double currentPosition;
        public boolean isHighest;

        public void enter() {
            entryPosition = ClimberAL.getInstance().potentiometerPosition();
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            currentPosition = ClimberAL.getInstance().potentiometerPosition();
            isHighest = ClimberAL.getInstance().isUp();

            climber.setSpeed(UPSLOW);
            climber.setLock(UNLOCK);

            // if potentiometer returns true for highest, it stops the motor and goes to
            // gamelocked
            if (isHighest) {
                return GameLocked;
            }

            // if the operator no longer wishes to go up, it stops
            if (!upButtonPressed) {
                return GameLocked;
            }

            // compares the potentiometer position to see if enough has been travelled for
            // it to go up at normal speed
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                return Extending;
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    UnlockingDown {
        public boolean downButtonPressed;
        public double entryPosition;
        public double currentPosition;
        public boolean isLowest;

        public void enter() {
            entryPosition = ClimberAL.getInstance().potentiometerPosition();
            ClimberAL.getInstance().getPosition();
        }

        public State action() {
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();
            currentPosition = ClimberAL.getInstance().potentiometerPosition();
            isLowest = ClimberAL.getInstance().isDown();

            climber.setSpeed(DOWNSLOW);
            climber.setLock(UNLOCK);

            // if potentiometer returns true for lowest, it stops
            if (isLowest) {
                return GameLocked;
            }

            // if the operator no longer wants to go down, it stops
            if (!downButtonPressed) {
                return GameLocked;
            }

            // compares the potentiometer position to see if enough has been travelled for
            // it to go down at normal speed
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                return Retracting;
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Extending {
        public boolean upButtonPressed;
        public boolean downButtonPressed;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();

            climber.setSpeed(UP);
            climber.setLock(UNLOCK);
            if (isHighest) {
                return GameLocked;
            }
            if (!upButtonPressed || downButtonPressed) {
                return GameLocked;
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    Retracting {
        public boolean downButtonPressed;

        public void enter() {
            // Noop
        }

        public State action() {
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();

            climber.setSpeed(DOWN);
            climber.setLock(UNLOCK);
            if (!downButtonPressed || isLowest) {
                return GameLocked;
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    GameLocked {
        public boolean upButtonPressed;
        public boolean downButtonPressed;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();

            climber.setSpeed(OFF);
            climber.setLock(LOCK);

            if (upButtonPressed && !downButtonPressed && !isHighest) {
                return UnlockingUp;
            }
            if (downButtonPressed && !upButtonPressed && !isLowest) {
                return UnlockingDown;
            }
            return this;
        }

        public void exit() {
            // Noop
        }
    };

    private static ClimberAL climber = ClimberAL.getInstance();
    private static boolean isLowest = climber.isDown();
    private static boolean isHighest = climber.isUp();
    private final static double climbThreshold = 2.0; // TODO: determine threshold for climber, should this be static or
                                                      // non static
}
