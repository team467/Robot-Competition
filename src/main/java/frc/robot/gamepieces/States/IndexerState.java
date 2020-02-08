/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.States;

import frc.robot.RobotMap;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;
import edu.wpi.first.wpilibj.Timer;

enum IndexerState implements State {

    Idle {
        private boolean isInMouth = false;
        private boolean AutoMode = false;
        private boolean isInChamber = false;
        public GamePieceController gamePiece;

        public void enter() {
            // Noop
        }

        public State action() {

            if (AutoMode) {
                if (gamePiece.indexerBallsForward() && isInMouth && !isInChamber) {
                    return Feed1;
                }

                // TODO: if shooterSM asking for a ball return feed.
                // if() {
                // return Feed1;
                // }
            } else {
                return Manual;
            }

            if (gamePiece.indexerBallsReverse()) {
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
            if (autoMode) {   
                if (!indexer.inMouth()) {
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
            IndexerAL.callBackwards();
            if (gamePiece.indexerBallsReverse()) {
                return Idle;
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

            if (gamePiece.indexerBallsForward()) {
                IndexerAL.callForward();
            }
            if (gamePiece.indexerBallsReverse()) {
                IndexerAL.callBackwards();
            }
            if (!gamePiece.indexerBallsForward() && !gamePiece.indexerBallsReverse()) {
                IndexerAL.callStop();
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

}