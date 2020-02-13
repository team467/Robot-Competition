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
public enum ClimberState implements State{
    InitialLocked {
        //inlcude these
        public boolean upWanted;
        public boolean downWanted;
        public boolean climberEnabled;

        public void enter() {
            //noop
        }
        
        public State action() {
            //first hand updates on the status of these stuff
            climberEnabled = GamePieceController.getInstance().climberEnabled;
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(OFF);
            climberAL.setLock(LOCK);
            if(climberEnabled && upWanted) {

                return Unlocking;

            } 
            return this;
        }

        public void exit() {
            // Noop
        }
    }, //it's an enum so a period is neccessary

    Unlocking {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            timer.start();
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(OFF);
            climberAL.setLock(UNLOCK);
            if(timer.get() > 1) { //TODO: timer pause second 
                if(upWanted && !isHighest) {
                    return ExtendingFast;
                }
                if(downWanted && !upWanted && !isLowest) {
                    return RetractingFast;
                }
            } 
            if (!upWanted && !downWanted) {
                return GameLocked;
            }

            return this;
        }
        
        public void exit() {
            timer.stop();
            timer.reset();
        }
    },

    ExtendingSlow{
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            timer.start();
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(UPSLOW);
            climberAL.setLock(UNLOCK);
            if(timer.get() > 1) { //TODO: timer pause second
                return LockingUp;
            }
            if(upWanted && !isHighest) {
                return ExtendingFast;
            }
            return this;
        }
        
        
        public void exit() {
            timer.stop();
            timer.reset();
        }
    },

    ExtendingFast{
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(UPFAST);
            climberAL.setLock(UNLOCK);
            if(isHighest) {
                return ExtendingSlow;
            }
            if(!upWanted || downWanted) {
                return ExtendingSlow;
            }
            return this;
        }
        
        
        public void exit() {
            // Noop
        }
    },

    LockingUp{ //TODO: pickup from here
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(UPSLOW);
            climberAL.setLock(LOCK);
            if(timer.get() > 1) { //TODO: timer pause second
                return GameLocked;
            }
            if(upWanted && !isHighest) {
                return Unlocking;
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    },

    GameLocked { //TODO: pickup from here
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(OFF);
            climberAL.setLock(LOCK);
            if(upWanted & downWanted) {
                return Unlocking;
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    },

    RetractingFast {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            //Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(DOWNFAST);
            climberAL.setLock(UNLOCK);
            if(!downWanted) {
                return RetractingSlow;
            }
            if(isLowest) {
                return RetractingSlow;
            }
            return this;
        }
        
        public void exit() {
            //Noop
        }
    },

    RetractingSlow {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            timer.start();
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(DOWNSLOW);
            climberAL.setLock(UNLOCK);
            if(timer.get() > 1) { //TODO: timer pause second
                return LockingDown;
            }
            if(downWanted && !isLowest) {
                return RetractingFast;
            }
            return this;
        }
        
        public void exit() {
            timer.stop();
            timer.reset();
        }
    },
 
    LockingDown { //locking while going down
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            timer.start();
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            climberAL.set(DOWNSLOW);
            climberAL.setLock(LOCK);
            if(timer.get() > 1) { //TODO: timer pause second
                return GameLocked;
            }
            if(downWanted && !isLowest) {
                return Unlocking;
            }
            return this;
        }
        
        public void exit() {
            timer.stop();
            timer.reset();
        }
    };

    private static ClimberAL climberAL = ClimberAL.getInstance();
    private static boolean isLowest = ClimberAL.getInstance().isDown();
    private static boolean isHighest = ClimberAL.getInstance().isUp();
    public static Timer timer = new Timer();
    ClimberState() {

    }
}
