package frc.robot.gamepieces.States;

import frc.robot.RobotMap;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.AbstractLayers.IntakeAL;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Timer;

public enum IndexerState implements State {

    Idle {

        public boolean indexAuto;
        public boolean indexerBallsReverse;
        public boolean indexerBallsForward;
        public boolean isInMouth;
        public boolean isInChamber;
        public boolean shooterWantsBall;
        public boolean climberEnabled;
        public boolean shooterAuto;

        public void enter() {
            // Noop
        }

        public State action() {
            indexAuto = GamePieceController.getInstance().IndexAuto;
            shooterAuto = GamePieceController.getInstance().ShooterAuto;
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse;
            indexerBallsForward = GamePieceController.getInstance().indexerBallsForward;
            climberEnabled = GamePieceController.getInstance().climberEnabled;

            isInMouth = indexerAL.isBallInMouth();
            isInChamber = indexerAL.isBallInChamber();
            shooterWantsBall = GamePieceController.getInstance().shooterWantsBall;
            LOGGER.debug("Shooter wants ball {}", shooterWantsBall);

            // if climber is enabled stop moving on
            if (climberEnabled) {
                LOGGER.debug("Climber enabled stopping");
                return this;
            }

            if (!indexAuto) {
                return Manual;
            }

            //Runs indexer when you have forward pressed and there isn't a ball in the chamber cause that causes death
            if (indexerBallsForward && isInMouth && !isInChamber) { //TODO: remove need for indexer forward to be held so it can be fully auto
                    LOGGER.debug("isInMouth and is not in Chamber");
                    return Feed;
            }

            if (shooterWantsBall) {
                LOGGER.debug("LoadingBalls");
                return Feed;
            }

            //Basically useless
            if ((indexerBallsForward || shooterWantsBall) && (!isInMouth || !isInChamber)) {
                IndexerAL.callIntakeBeltToIndexer();
            } else {
                IndexerAL.callIntakeBeltOff();
            }

            return this;
        }

        public void exit() {
            // Noop
        }

    },

    Feed {

        public boolean autoMode;
        public boolean indexerBallsReverse;
        public boolean isInChamber;

        public void enter() {
            // Noop
        }

        public State action() {
            LOGGER.debug("Feed is activated");
            autoMode = GamePieceController.getInstance().IndexAuto;
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse;
            isInChamber = indexerAL.isBallInChamber();

            // IndexerAL.advanceBallsToShooter();

            if (!autoMode) {
                IndexerAL.callStop();
                return Idle;
            }

            if (indexerAL.isBallInMouth()) {
                return FeedBuffer;
            }

            if (isInChamber) {
                IndexerAL.callStop();
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

        public void enter() {
            timer.start();

        }

        public State action() {

            IndexerAL.advanceBallsToShooter();
            // TODO: adjust timer based on how fast the ball is moving.
            if (timer.get() >= RobotMap.INDEXER_MOVE_TIMER) {
                IndexerAL.callStop();
                return Idle;
            } else {
                return this;
            }
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
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse;

            IndexerAL.moveBallsTowardIntake();

            if (!autoMode) {
                return Manual;
            }

            if (!indexerBallsReverse) {
                IndexerAL.callStop();
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
            indexerBallsReverse = GamePieceController.getInstance().indexerBallsReverse;
            indexerBallsForward = GamePieceController.getInstance().indexerBallsForward;

            if (autoMode) {
                return Idle;
            }

            if (indexerBallsForward) {
                IndexerAL.advanceBallsToShooter();
                IndexerAL.callIntakeBeltToIndexer();
            }

            if (indexerBallsReverse) {
                IndexerAL.moveBallsTowardIntake();
                IndexerAL.callIntakeBeltInverse();
            }

            if (!indexerBallsForward && !indexerBallsReverse) {
                IndexerAL.callStop();
                IndexerAL.callIntakeBeltOff();
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

}