package frc.robot.gamepieces.States;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.TriggerSettings;
import frc.robot.gamepieces.GamePieceController.ShooterMode;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.FlywheelSettings;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.logging.RobotLogManager;
import frc.robot.vision.VisionController;

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


            //if climber is enabled stop moving on
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
        public boolean autoMode, indexAuto, robotAligned;
        public boolean fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
        public GamePieceController gamePieceController = GamePieceController.getInstance();

        public void enter() {
            GamePieceController.getInstance().determineShooterSpeed();
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            indexAuto = GamePieceController.getInstance().IndexAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            // gamePieceController.setShooterWantsBall(true);
        
            LOGGER.debug("LB " + fireWhenReady);
            if (!autoMode){
                gamePieceController.setShooterWantsBall(false);
                shooterAL.setShooterWantsBall(false);
                return Manual;
            }

            if (!fireWhenReady) {
                gamePieceController.setShooterWantsBall(false);
                shooterAL.setShooterWantsBall(false);
                return Idle;
            }
            
            if (indexerAL.ballLoaded() || !indexAuto) {
                LOGGER.debug("ball loaded");
                gamePieceController.setShooterWantsBall(false);
                shooterAL.setShooterWantsBall(false);
                return AdjustingSpeed;
            }
            
            gamePieceController.setShooterWantsBall(true);
            shooterAL.setShooterWantsBall(true);
            return this;
        }

        public void exit() {
        }
    },

    AdjustingSpeed {
        public boolean autoMode, robotAligned;
        //Auto align robot and check if shooter is at the speed
        public void enter() {
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            robotAligned = GamePieceController.getInstance().RobotAligned;
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);

            LOGGER.debug("Adjusting speed");

            if (!autoMode) {
                return Manual;
            }

            if (shooterAL.atSpeed() && robotAligned) {
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
        public boolean hasAngle;
        public boolean hasDistance;

        public void enter() {
            timer.start();
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            hasAngle = VisionController.getInstance().hasAngle();
            hasDistance = VisionController.getInstance().hasDistance();

            LOGGER.debug("Shooting");
            
            if (hasAngle && hasDistance) {
                shooterAL.setFlywheel(FlywheelSettings.FORWARD);
                shooterAL.setTrigger(TriggerSettings.SHOOTING);
            } else {
                return Manual;
            }

            if(timer.get() < RobotMap.SHOOTER_AUTO_TIMER){ //!shooterAL.atSpeed() || !indexerAL.inChamber()
                return this;
            } else {
                indexerAL.shootBall();
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
        public boolean triggerMan, flyWheelMan;
        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            flyWheelMan = GamePieceController.getInstance().flywheelManual;
            triggerMan = GamePieceController.getInstance().triggerManual;

            //Manual mode based on controls
            if (flyWheelMan) {
                shooterAL.setFlywheel(FlywheelSettings.MANUAL_FORWARD);
            } else {
                shooterAL.setFlywheel(FlywheelSettings.STOP);
            }

            if (triggerMan) {
                shooterAL.setTrigger(TriggerSettings.SHOOTING);
            } else {
                shooterAL.setTrigger(TriggerSettings.STOP);
            }

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
    private static boolean robotAligned = gamePieceController.RobotAligned;

    private static final Logger LOGGER = RobotLogManager.getMainLogger(ShooterAL.class.getName());
    
    //delay
    public static Timer timer = new Timer();
}