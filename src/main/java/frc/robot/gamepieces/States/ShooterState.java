package frc.robot.gamepieces.States;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.TriggerSettings;
import frc.robot.gamepieces.GamePieceController.ShooterMode;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.FlywheelSettings;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.RobotMap;


public enum ShooterState implements State {

    Idle {

        public void enter() {
            // Noop
        }

        public State action() {
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.STOP);

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
            // Noop
        }
    },

    LoadingBall {
        public void enter() {
            // float distance = Sensor.getDistance();
            // int desiredRPM = (int)(distance * 1 * 1);
        }

        public State action() {
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
        
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
        }
    },

    AdjustingSpeed {
        public void enter() {

        }

        public State action() {
            shooterAL.setTrigger(TriggerSettings.STOP);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            if(autoMode){
                if (shooterAL.atSpeed() && robotAligned){
                    return Shooting;
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

    Shooting {
         
        public void enter() {
            // Noop
        }

        public State action() {
            shooterAL.setTrigger(TriggerSettings.SHOOTING);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            if(autoMode){
                if(!fireWhenReady) {
                    return Idle;
                }

                if(!shooterAL.atSpeed() || !indexerAL.inChamber() || robotAligned){
                    return LoadingBall;
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

    Manual {
        public void enter() {
            // Noop
        }

        public State action() {
            //Manual mode does not care about speeds, just shoots and constantly feeds
            shooterAL.setFlywheel(FlywheelSettings.MANUAL_FORWARD);
            shooterAL.setTrigger(TriggerSettings.SHOOTING);
            return this;
        }

        public void exit() {
            // Noop
        }
    };

    private static GamePieceController gamePieceController = GamePieceController.getInstance();
    private static ShooterAL shooterAL = ShooterAL.getInstance();
    private static IndexerAL indexerAL = IndexerAL.getInstance();
    private static boolean robotAligned = gamePieceController.RobotAligned; //TODO gpc will tell if robot is aligned
    private static boolean fireWhenReady = gamePieceController.fireWhenReady;
    public static boolean autoMode = (gamePieceController.shooterMode == ShooterMode.AUTO) ? true:false;

    ShooterState() {

    }


}