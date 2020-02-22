package frc.robot.gamepieces.States;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.TriggerSettings;
import frc.robot.gamepieces.GamePieceController.ShooterMode;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.FlywheelSettings;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;


import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.RobotMap;


public enum ShooterState implements State {

    Idle {

        public  boolean autoMode;
        public boolean fireWhenReady;
        public boolean climberEnabled;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            climberEnabled = GamePieceController.getInstance().climberEnabled;
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.STOP);


            if(climberEnabled){
                LOGGER.debug("Climber enabled, stopping shooter");
                return this;
            }

            if(autoMode) {
                if(fireWhenReady) {
                    return LoadingBall;
                }
                return this;

            } else {
                return Manual;
            }
        }

        public void exit() {
        }
    },

    LoadingBall {
        public boolean autoMode;
        public boolean fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
        public GamePieceController gamePieceController = GamePieceController.getInstance();
        public void enter() {
            // float distance = Sensor.getDistance();
            // int desiredRPM = (int)(distance * 1 * 1);
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            gamePieceController.setShooterWantsBall(true);
        
            LOGGER.debug("LB " + fireWhenReady);
            if (!autoMode){
                return Manual;
            }

            if (indexerAL.isBallInChamber()) {
                return AdjustingSpeed;
            } else {
                return LoadingBall;
            }
        }

        public void exit() {
        }
    },

    AdjustingSpeed {
        public boolean autoMode;
        //Auto align robot and check if shooter is at the speed
        public void enter() {

        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            if (!autoMode) {
                return Manual;
            }

            if (shooterAL.atSpeed() && robotAligned){
                return ShootingNoDelay;
            } else {
                return AdjustingSpeed;
            }
                
        }

        public void exit() {
            // Noop
        }
    },

    ShootingNoDelay {
        public boolean autoMode;
        public boolean fireWhenReady;

        public void enter() {
            timer.start();
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            //LOGGER.error(fireWhenReady);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            shooterAL.setTrigger(TriggerSettings.SHOOTING);

            if(timer.get() < 1.0){ //!shooterAL.atSpeed() || !indexerAL.inChamber()
                return ShootingNoDelay;
            } else {
                return Idle;
            }
        }

        public void exit() {
           timer.stop();
           timer.reset();
        }
    },

    Manual {
        public boolean autoMode;
        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            //Manual mode based on controls
                if(flyWheelMan)shooterAL.setFlywheel(FlywheelSettings.MANUAL_FORWARD);
                if(triggerMan)shooterAL.setTrigger(TriggerSettings.SHOOTING);

            if (autoMode) {
                return LoadingBall;
            } else {
            return this;
            }
        }

        public void exit() {
            // Noop
        }
    };

    //controllers
    private static GamePieceController gamePieceController = GamePieceController.getInstance();
    private static ShooterAL shooterAL = ShooterAL.getInstance();
    private static IndexerAL indexerAL = IndexerAL.getInstance();
    private static boolean robotAligned = gamePieceController.RobotAligned; //TODO gpc will tell if robot is aligned




    private static final Logger LOGGER = RobotLogManager.getMainLogger(ShooterAL.class.getName());

    //Manual settings
    public static boolean triggerMan = gamePieceController.triggerManual;
    public static boolean flyWheelMan = gamePieceController.flywheelManual;

    //delay
    public static Timer timer = new Timer();
}