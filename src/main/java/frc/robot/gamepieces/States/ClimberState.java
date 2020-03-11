package frc.robot.gamepieces.States;

import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import edu.wpi.first.wpilibj.Timer;

public enum ClimberState implements State {
    InitialLocked {
        public boolean upButtonPressed;
        public boolean climberEnabled;

        public void enter() {
            // noop
        }

        public State action() {
            climberEnabled = GamePieceController.getInstance().climberEnabled;
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;

            climber.climberOff();
            climber.climberLock();
            if (climberEnabled && upButtonPressed) {
                return Extending;

            }
            return this;
        }

        public void exit() {
            // Noop
        }
    }, 

    //removed from logic because physically cannot be done
    UnlockingUp {
        public boolean upButtonPressed;
        public double entryPosition;
        public double currentPosition;
        public double distanceTravelled;
        public double distanceNeeded;

        public void enter() {
            entryPosition = ClimberAL.getInstance().climberPosition();
            //encoder.setDistancePerPulse(3./256.); //TODO determine value
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            currentPosition = ClimberAL.getInstance().climberPosition();
           // distanceTravelled = encoder.getDistance();

           climber.climberUpSlow();
           climber.climberUnlock();
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                if (upButtonPressed && distanceNeeded > distanceTravelled) {
                    return this;
                }
                if (distanceNeeded < distanceTravelled ) {
                    return Extending;
                } else {
                    return GameLocked;
                }
            }
            return this;
        }

        public void exit() {
            //encoder.reset();
        }
    },

    //removed from logic because physically cannot be done
    UnlockingDown {
        public boolean downButtonPressed;
        public double entryPosition;
        public double currentPosition;
        public double distanceTravelled;
        public double distanceNeeded;

        public void enter() {
            entryPosition = ClimberAL.getInstance().climberPosition();
            //encoder.setDistancePerPulse(3./256.); //TODO determine value
        }

        public State action() {
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;
            currentPosition = ClimberAL.getInstance().climberPosition();
            //distanceTravelled = encoder.getDistance();

            climber.climberDownSlow();
            climber.climberUnlock();
            if (Math.abs(currentPosition - entryPosition) > climbThreshold) {
                if (downButtonPressed && distanceNeeded > distanceTravelled) {
                    return this;
                }
                if (distanceNeeded < distanceTravelled) {
                    return Retracting;
                }
                if (!downButtonPressed) {
                    return GameLocked;
                }
            }
            return this;
        }

        public void exit() {
            //encoder.reset();
        }
    },

    Extending {
        public boolean upButtonPressed;
        public boolean downButtonPressed;

        public void enter() {
            // Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climber.climberUp();
            climber.climberUnlock();
            // if (isHighest) {
            //     return GameLocked;
            // }
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
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climber.climberDown();
            climber.climberUnlock();
            if (!downButtonPressed) {
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
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climber.climberOff();
            climber.climberLock();
            if (upButtonPressed && !downButtonPressed) { //&&!isHighest
                return Extending;
            }
            if (downButtonPressed && !upButtonPressed) { //&&!isLowest
                return Retracting;
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

    public final double distanceNeeded = 3.0; 
    
    private final static double climbThreshold = 2.0;

    public static Timer timer = new Timer();
}

