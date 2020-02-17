/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import static frc.robot.gamepieces.AbstractLayers.ClimberAL.solenoidLock.*;
import static frc.robot.gamepieces.AbstractLayers.ClimberAL.climberSpeed.*;
import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public enum ClimberState implements State {
    InitialLocked {
        // inlcude these
        public boolean upButtonPressed;
        public boolean downButtonPressed;
        public boolean climberEnabled;

        public void enter() {
            // noop
        }

        public State action() {
            // first hand updates on the status of these stuff
            climberEnabled = GamePieceController.getInstance().climberEnabled;
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;

            climberAL.set(OFF);
            climberAL.setLock(LOCK);
            if (climberEnabled && upButtonPressed) {

                return Extending;

            }
            return this;
        }

        public void exit() {
            // Noop
        }
    }, // it's an enum so a period is neccessary

    UnlockingUp {
        public boolean upButtonPressed;
        public boolean downButtonPressed;
        public double Position;
        public double unlockingDelay;

        public void enter() {
            timer.start();
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;
            Position = ClimberAL.getInstance().getPosition();

            climberAL.set(UPSLOW);
            climberAL.setLock(LOCK);
            if (Position >= unlockingDelay) {
                if (upButtonPressed) {
                    return Extending;
                }
                if (!upButtonPressed) {
                    return GameLocked;
                }
            }
            return this;
        }

        public void exit() {
            timer.stop();
            timer.reset();
        }
    },

    UnlockingDown {
        public boolean upButtonPressed;
        public boolean downButtonPressed;
        public double Position;
        public double unlockingDelay;

        public void enter() {
            timer.start();
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;
            Position = ClimberAL.getInstance().getPosition();

            climberAL.set(DOWNSLOW);
            climberAL.setLock(LOCK);
            if (Position >= unlockingDelay) {
                if (downButtonPressed) {
                    return Extending;
                }
                if (!downButtonPressed) {
                    return GameLocked;
                }
            }
            if (timer.get() > unlockingDelay && Position <= 2) { // TODO: determine position value
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
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;

            climberAL.set(UP);
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
        public boolean upButtonPressed;
        public boolean downButtonPressed;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;

            climberAL.set(DOWN);
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
            upButtonPressed = GamePieceController.getInstance().upButtonPressed;
            downButtonPressed = GamePieceController.getInstance().downButtonPressed;

            climberAL.set(OFF);
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
        public double disableDelay;

        public void enter() {
            timer.start();
        }

        public State action() {
            climberAL.set(OFF);
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
    private final double unlockingDelay = 2.0; // TODO: determine delay in seconds
    private final double disableDelay = 3.0; // TODO: determine delay in seconds
    public static Timer timer = new Timer();

    ClimberState() {

    }
}
