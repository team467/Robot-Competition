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
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Timer;

public enum IndexerState implements State {

    Idle {

        public boolean autoMode;
        public boolean indexerBallsReverse;
        public boolean indexerBallsForward;
        public boolean isInMouth;
        public boolean isInChamber;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().IndexAuto;
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse();
            indexerBallsForward = GamePieceController.getInstance().indexerBallsForward();
            isInMouth = indexerAL.inMouth();
            isInChamber = indexerAL.inChamber();

            if (!autoMode) {
                return Manual;
            }

            if (indexerBallsForward && isInMouth && !isInChamber) {
                return Feed1;
            }

            if (GamePieceController.getInstance().getShooterState() == ShooterState.LoadingBall) {
                return Feed1;
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

        public boolean autoMode;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().IndexAuto;

            IndexerAL.callForward();
            if (autoMode) {
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
            if (timer.get() == RobotMap.INDEXER_MOVE_TIMER) {
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

        public boolean autoMode;
        public boolean indexerBallsReverse;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().IndexAuto;
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse();

            if (autoMode) {
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

        public boolean autoMode;
        public boolean indexerBallsForward;
        public boolean indexerBallsReverse;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().IndexAuto;
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse();
            indexerBallsForward = GamePieceController.getInstance().indexerBallsReverse();

            if (!autoMode) {
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

    private static IndexerAL indexerAL = IndexerAL.getInstance();

    // LOGGER
    private static final Logger LOGGER = RobotLogManager.getMainLogger(IndexerAL.class.getName());

    // delay
    public static Timer timer = new Timer();

    IndexerState() {

    }

}