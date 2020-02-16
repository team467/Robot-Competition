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
            isInMouth = indexerAL.isBallInMouth();
            isInChamber = indexerAL.isBallInChamber();

            if (!autoMode) {
                return Manual;
            }

            if (indexerBallsForward && isInMouth && !isInChamber) {
                return Feed;
            }

            if (GamePieceController.getInstance().getShooterState() == ShooterState.LoadingBall) {
                return Feed;
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

    Feed {

        public boolean autoMode;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().IndexAuto;

            IndexerAL.advanceBallsToShooter();

            if (!autoMode) {
                return Idle;
            }

            if (!indexerAL.isBallInMouth()) {
                return FeedBuffer;
            }

            return this;

        }

        public void exit() {
            // Noop
        }

    },

    // Timed for 50ms to let the ball Feed advance to make room for another ball to
    // be seen by the mouth TOF sensor.
    FeedBuffer {

        private Timer timer;

        public void enter() {
            timer.start();

        }

        public State action() {

            IndexerAL.advanceBallsToShooter();
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

            IndexerAL.advanceBallsToShooter();

            if (!autoMode) {
                return Manual;
            }

            if (indexerBallsReverse) {
                return Idle;
            }

            return this;
        }

        public void exit() {
            // Noop 
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

            if (autoMode) {
                return Idle;
            }

            if (indexerBallsForward) {
                IndexerAL.moveBallsTowardIntake();
            }
            if (indexerBallsReverse) {
                IndexerAL.advanceBallsToShooter();
            }
            if (!indexerBallsForward && !indexerBallsReverse) {
                IndexerAL.callStop();
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