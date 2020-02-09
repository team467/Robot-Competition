/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public enum ClimberState implements State{
    ClimberDown {

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(LOCK);
            if(climberEnabled && ClimberUp) {

                return Extending;

            } else {
                return this;
            }
        }

        public void exit() {
            // Noop
        }
    }

    Unlocking {

        public void enter() {
            timer.start();
        }

        public State action() {
            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(UNLOCK);
            if(timer.get() > 1) { //TODO: figure out timing

                return Extending;

            } 
            if (!ClimberUp) {
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

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(UP);
            CLimberAL.setLock(UNLOCK);
            if(!ClimberUp & ClimberDown) {

                return RetractingSlow;

            } 
            if ((!isUp && !isDown)||(ClimberAL.isUp()||(highPosition >= ClimberAL.climberPosition() ) ) ) {
                return ClimberUp; 
            }
            return this;
        }
        
        public void exit() {
            // Noop
        }
    }

    ClimberUp {

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(STOP);
            CLimberAL.setLock(UNLOCK);
            if() {


            } else {
                return this;
            }
        }
        
        public void exit() {
            // Noop
        }
    }

    RetractingFast {

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(DOWN);
            CLimberAL.setLock(UNLOCK);
            if() {

                
                return this;

            } else {
                return this;
            }
        }
        
        public void exit() {
            // Noop
        }
    }

    RetractingSlow {

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(DOWNSTOP);
            CLimberAL.setLock(UNLOCK);
            if() {

            
                return this;

            } else {
                return this;
            }
        }
        
        public void exit() {
            // Noop
        }
    }

    Locking {

        public void enter() {
            // Noop
        }

        public State action() {
            ClimberAL.climberSpeed(DOWNSTOP);
            CLimberAL.setLock(LOCK);
            if() {

        
                return this;

            } else {
                return this;
            }
        }
        
        public void exit() {
            // Noop
        }
    }

    private final float highPosition = 5.0f; //TODO: determine highest position 
    private final float lowPosition = 0.0f; //TODO: determine lowest position

    ClimberState() {

    }



}
