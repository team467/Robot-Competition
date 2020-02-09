/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.RobotMap;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.GamePieceController.IndexerMode;
import frc.robot.gamepieces.GamePiece;
import frc.robot.gamepieces.GamePieceController;
import edu.wpi.first.wpilibj.Timer;

public enum IndexerState implements State {

    Idle {

        public void enter() {
            // Noop
        }

        public State action() {
            if (AutoMode) {
                if (indexerBallsForward && indexerAL.inMouth() && indexerAL.inChamber()) {
                    return Feed1;
                }

                // TODO: if shooterSM asking for a ball return feed.
                // if() {
                // return Feed1;
                // }
            } else {
                return Manual;
            }

            if (indexerBallsReverse) {
                return Reverse;
            }
            return this;
        }

        public void exit() {
            // Noop
        }

    },

    Feed1 {
        private GamePieceController gamePiece;
        private IndexerAL indexer;
        private boolean autoMode = false;

        public void enter() {
            // Noop
        }

        public State action() {
            IndexerAL.callForward();
            if (AutoMode) {   
                if (!indexerAL.inMouth()) {
                   return Feed2;
                }
            } else {
                return Idle;
            }
            
            return this;

        }

        public void exit() {
            // Noop
        }

    },

    Feed2 {
        private Timer timer;
        public void enter() {
            timer.start();
            
        }

        public State action() {
            IndexerAL.callForward();
            // TODO: adjust timer based on how fast the ball is moving. 
            if (timer.get() == 0.20) {
                return Idle;
            }
            
            return this;
        }

        public void exit() {
            timer.stop();
            timer.reset();
        }

    },

    Reverse {

        private GamePieceController gamePiece;
        public void enter() {
            // Noop
        }

        public State action() {
            if(AutoMode){
            IndexerAL.callBackwards();
            if (indexerBallsReverse) {
                return Idle;
            }     
        } else {
            return Manual;
        }
            return this;
        }

        public void exit() {
            // Noop tst
        }

    },

    Manual {

        private GamePieceController gamePiece;

        public void enter() {
            // Noop
        }

        public State action() {

        if(!AutoMode){
            if (indexerBallsForward) {
                IndexerAL.callForward();
            }
            if (indexerBallsReverse) {
                IndexerAL.callBackwards();
            }
            if (!indexerBallsForward && !indexerBallsReverse) {
                IndexerAL.callStop();
            }

        } else {
            return Idle;
        }

        return this;
        }

        public void exit() {
            // Noop
        }
    };

    private static IndexerAL indexerAL= IndexerAL.getInstance();
    private static GamePieceController gamePieceController = GamePieceController.getInstance();
    private static boolean indexerBallsReverse = gamePieceController.indexerBallsReverse();
    private static boolean indexerBallsForward = gamePieceController.indexerBallsForward();
    private static boolean isInMouth = false;
    private static boolean AutoMode = (gamePieceController.ShooterAuto)? false : true;
    private static boolean isInChamber = false;
    IndexerState() {

    }

}