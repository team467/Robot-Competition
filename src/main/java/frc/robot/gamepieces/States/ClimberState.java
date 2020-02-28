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

import frc.robot.gamepieces.GamePiece;

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

            // if switch that activates the climber is activated and the operator wishes to
            // go up, it goes up at a slowed speed
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
        public boolean isHighest;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();
            isHighest = ClimberAL.getInstance().isUp();

            climber.setSpeed(UP);
            climber.setLock(UNLOCK);

            // if potentiometer returns true for highest, it stops
            if (isHighest) {
                return GameLocked;
            }

            // if the operator no longer wishes to up, or the operator wishes to go down, it
            // stops and goes to GameLocked for a transition
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
        public boolean upButtonPressed;
        public boolean isLowest;

        public void enter() {
            // Noop
        }

        public State action() {
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            isLowest = ClimberAL.getInstance().isDown();

            climber.setSpeed(DOWN);
            climber.setLock(UNLOCK);

            // if potentiometer returns true for lowest, it stops
            if (isLowest) {
                return GameLocked;
            }

            // if the operator no longer wishes to go down, or the operator wishes to go up,
            // it stops and goes to GameLcoked for a transition
            if (!downButtonPressed || upButtonPressed) {
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
        public boolean isHighest;
        public boolean isLowest;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();
            isHighest = ClimberAL.getInstance().isUp();
            isLowest = ClimberAL.getInstance().isDown();

            climber.setSpeed(OFF);
            climber.setLock(LOCK);

            // if the operator is pressing up, not down and it is not already at the
            // highest, it goes up at a slowed speed
            if (upButtonPressed && !downButtonPressed && !isHighest) {
                return UnlockingUp;
            }

            // if the operator is pressing down, not up, and it is not already at the
            // lowest, it goes down at a slowed speed
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
    private final static double climbThreshold = 2.0; // TODO: determine threshold for climber, should this be static or
                                                      // non static
}
