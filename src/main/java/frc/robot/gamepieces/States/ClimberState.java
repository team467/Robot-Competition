/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.AbstractLayers.ClimberAL;
import frc.robot.gamepieces.AbstractLayers.ClimberAL.solenoidLock;
import frc.robot.gamepieces.AbstractLayers.ClimberAL.climberSpeed;
import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public enum ClimberState implements State{
    InitialLocked {
        //inlcude these
        public boolean upButtonPressed;
        public boolean downButtonPressed; 
        public boolean climberEnabled;

        public void enter() {
            //noop
        }
        
        public State action() {
            //first hand updates on the status of these stuff
            climberEnabled = GamePieceController.getInstance().climberEnabled;
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climberAL.climberDirection(climberSpeed.OFF);
            climberAL.setLock(solenoidLock.LOCK);
            if(climberEnabled && upButtonPressed) {

                return Extending;

            } 
            return this;
        }

        public void exit() {
            // Noop
        }
    }, 

    Extending{
        public boolean upButtonPressed;
        public boolean downButtonPressed;

        public void enter() {
           //Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climberAL.climberDirection(climberSpeed.UP);
            climberAL.setLock(solenoidLock.UNLOCK);
            if (isHighest) { 
                return GameLocked;
            }

            if (!upButtonPressed || downButtonPressed) {
                return GameLocked;
            }
            return this;
        }
        
        
        public void exit() {
            //Noop
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

            climberAL.set(climberSpeed.OFF);
            climberAL.setLock(solenoidLock.LOCK);
            if (upButtonPressed && !downButtonPressed && !isHighest) {
                return Extending;
            }

            if (downButtonPressed && !upButtonPressed && !isLowest) {
                return Retracting;
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
            //Noop
        }

        public State action() {
            upButtonPressed = GamePieceController.getInstance().climberUpButtonPressed;
            downButtonPressed = GamePieceController.getInstance().climberDownButtonPressed;

            climberAL.set(climberSpeed.DOWN);
            climberAL.setLock(solenoidLock.UNLOCK);
            if(!downButtonPressed) {
                return GameLocked;
            }
            if(isLowest) {
                return GameLocked;
            }
            return this;
        }
        
        public void exit() {
            //Noop
        }
    };

    private static ClimberAL climberAL = ClimberAL.getInstance();
    private static boolean isLowest = climberAL.isDown();
    private static boolean isHighest = climberAL.isUp();
    public static Timer timer = new Timer();
    ClimberState() {

    }
}
