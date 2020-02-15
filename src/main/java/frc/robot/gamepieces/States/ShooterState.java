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

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.STOP);
            LOGGER.debug("ID " + fireWhenReady);



            if(autoMode) {

                if(fireWhenReady) {
                    //LOGGER.error(fireWhenReady);

                    return LoadingBall;
                }

                //LOGGER.error(fireWhenReady);
                return this;

            } else {
                return Manual;

            }
        }

        public void exit() {
           LOGGER.debug("Requested to load ball");
        }
    },

    LoadingBall {
        public boolean autoMode;
        public boolean fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
        public void enter() {
            // float distance = Sensor.getDistance();
            // int desiredRPM = (int)(distance * 1 * 1);
            LOGGER.debug("entering Loading ball");
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
        
            LOGGER.debug("LB " + fireWhenReady);
            if(autoMode){
                if(indexerAL.inChamber()) {
                    return AdjustingSpeed;
                }
            } else {
                return Idle;
            }

            return this;

        }

        public void exit() {
            // Noop
            LOGGER.debug("Requested to adjust speed");
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
            if(autoMode){
                if (shooterAL.atSpeed() && robotAligned){
                    System.out.println("shooting");
                    return ShootingNoDelay;
                }
                
            } else {
                return Manual;
            }

            return this;
        }

        public void exit() {
            // Noop
        }
    },

    ShootingNoDelay {
        public boolean autoMode;
        public boolean fireWhenReady;

        public void enter() {
            // Noop
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().getFireWhenReady();
            //LOGGER.error(fireWhenReady);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            if(autoMode){
                if(fireWhenReady){
                    shooterAL.setTrigger(TriggerSettings.SHOOTING);
                    
                    if(!shooterAL.atSpeed() || !indexerAL.inChamber() || robotAligned){
                        LOGGER.debug("Shooter is not at speed or nothing in the chamber or robot is not aligned changing to moving ball");
                        return LoadingBall;
                    }
                } else {
                    LOGGER.debug("no longer shooting");
                    return Idle;
                }
            } else {
                return Manual;
            }
            
            return this;
        }

        public void exit() {
            // Noop
        }
    },

    ShootingDelayed {
         
        //shooterdelays
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
            if(autoMode){
                if(fireWhenReady){
                    shooterAL.setTrigger(TriggerSettings.SHOOTING);
                    
                    if(!shooterAL.atSpeed() || !indexerAL.inChamber() || robotAligned){
                        LOGGER.debug("Shooter is not at speed or nothing in the chamber or robot is not aligned changing to moving ball");
                        return LoadingBall;
                    }
                } else {
                    LOGGER.debug("no longer shooting");
                    return Idle;
                }
            } else {
                return Manual;
            }
            
            return this;
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
            if(autoMode){
                LOGGER.info("Manual");
                if(flyWheelMan)shooterAL.setFlywheel(FlywheelSettings.MANUAL_FORWARD);
                if(triggerMan)shooterAL.setTrigger(TriggerSettings.SHOOTING);
            } else {
                return LoadingBall;
            }
            return this;
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

    ShooterState() {

    }


}