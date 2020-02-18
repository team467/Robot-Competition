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
import edu.wpi.first.wpilibj.Timer;

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

            climberAL.setSpeed(OFF);
            climberAL.setLock(LOCK);
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

        public void enter() {
            timer.start();
            entryPosition = climberAL.climberPosition();
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed();
            currentPosition = climberAL.climberPosition();

            climberAL.setSpeed(UPSLOW);
            climberAL.setLock(LOCK);
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                if (upButtonPressed) {
                    return Extending;
                }
                if (!upButtonPressed) {
                    return GameLocked;
                }
            }
            if (timer.get() > unlockingDelay) {
                return Disabled;
            }
            return this;
        }

        public void exit() {
            timer.stop();
            timer.reset();
        }
    },

    UnlockingDown {
        public boolean downButtonPressed;
        public double entryPosition;
        public double currentPosition;

        public void enter() {
            timer.start();
            entryPosition = climberAL.climberPosition();
        }

        public State action() {
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed();
            currentPosition = climberAL.climberPosition();

            climberAL.setSpeed(DOWNSLOW);
            climberAL.setLock(LOCK);
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                if (downButtonPressed) {
                    return Retracting;
                }
                if (!downButtonPressed) {
                    return GameLocked;
                }
            }
            if (timer.get() > unlockingDelay && Math.abs(currentPosition - entryPosition) <= climbThreshold) { // TODO: entry position - current position
                return Disabled;
            }
            return this;
        }

        public void exit() {
            timer.stop();
            timer.reset();
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

            climberAL.setSpeed(UP);
            climberAL.setLock(UNLOCK);
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

            climberAL.setSpeed(DOWN);
            climberAL.setLock(UNLOCK);
            if (!downButtonPressed) {
                return GameLocked;
            }
            if (isLowest) {
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

            climberAL.setSpeed(OFF);
            climberAL.setLock(LOCK);
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
    },

    Disabled {
        public void enter() {
            timer.start();
        }

        public State action() {
            climberAL.setSpeed(OFF);
            climberAL.setLock(LOCK);
            if (timer.get() > disableDelay) { // TODO: determine timer value
                return GameLocked;
            }
            return this;
        }

        public void exit() {
            timer.stop();
            timer.reset();
        }
    };

    private static ClimberAL climberAL = ClimberAL.getInstance();
    private static boolean isLowest = climberAL.isDown();
    private static boolean isHighest = climberAL.isUp();

    private final static double unlockingDelay = 2.0; // TODO: determine delay in seconds
    private final static double disableDelay = 3.0; // TODO: determine delay in seconds
    private final static double climbThreshold = 2.0; // TODO: determine threshold for climber, should this be static or non static

    public static Timer timer = new Timer();
}

