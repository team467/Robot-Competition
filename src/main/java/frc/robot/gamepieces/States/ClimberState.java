/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public enum ClimberState implements State{
    ClimberDown {
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

            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(LOCK);
            if(climberEnabled && upWanted) {

                return Extending;

            } 
            return this;
        }

        public void exit() {
            // Noop
        }
    }

    Unlocking {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            timer.start();
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(UNLOCK);
            if(timer.get() > 1) { //TODO: figure out timing

                return Extending;

            } 
            if (!upWanted) {
                return Locking;
            }

            return this;
        }
        
        public void exit() {
            timer.stop();
            timer.reset();
        }
    }

    Extending {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(UP);
            CLimberAL.setLock(UNLOCK);
            if(!upWanted & downWanted) {
                return RetractingSlow;
            } 
            if (!upWanted) {
                return ClimberUp;
            }
            if (climberIsHighest) {
                return ClimberUp; 
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    ClimberUp { //TODO: pickup from here
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(LOCK);
            if(upWanted & !climberIsHighest) {
                return Extending;
            } 
            if(downWanted & !climberIsLowest){
                return RetractingFast;
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    RetractingFast {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(DOWN);
            CLimberAL.setLock(UNLOCK);
            if(upWanted) {
                return Extending;
            } 
            if(climberIsLowest || !downWanted) {
                return ClimberUp;
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    RetractingSlow {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(DOWNSTOP);
            CLimberAL.setLock(UNLOCK);
            if(!upWanted & !downWanted) {
                return Locking;
            } 
            if(upWanted) {
                return Extending; //extending slow cuz changing direction
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    Locking {
        public boolean upWanted;
        public boolean downWanted;

        public void enter() {
            // Noop
        }

        public State action() {
            upWanted = GamePieceController.getInstance().upWanted;
            downWanted = GamePieceController.getInstance().downWanted;

            ClimberAL.climberSpeed(DOWNSTOP);
            CLimberAL.setLock(LOCK);
            if(downWanted & climberIsLowest) {
                return RetractingFast;
            }
            if(upWanted) {
                return Extending; //extending slow
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    private static boolean climberIsLowest = ClimberAL.getInstance().isUp();
    private static boolean climberIsHighest = ClimberAL.getInstance().isDown();
   
    ClimberState() {

    }
}
