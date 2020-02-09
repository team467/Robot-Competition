package frc.robot.gamepieces.States;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.TriggerSettings;
import frc.robot.gamepieces.GamePieceController.ShooterMode;
import frc.robot.gamepieces.AbstractLayers.ShooterAL.FlywheelSettings;
import frc.robot.gamepieces.AbstractLayers.IndexerAL;
import frc.robot.gamepieces.GamePieceController;
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
            fireWhenReady = GamePieceController.getInstance().fireWhenReady;
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
        public boolean autoMode;
        public void enter() {
            // float distance = Sensor.getDistance();
            // int desiredRPM = (int)(distance * 1 * 1);
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
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
                return Idle;
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
            fireWhenReady = GamePieceController.getInstance().fireWhenReady;
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

    ShootingDelayed {
         
        //shooterdelays
        public boolean autoMode;
        public boolean fireWhenReady;
        public void enter() {
            timer.start();
        }

        public State action() {
            autoMode = GamePieceController.getInstance().ShooterAuto;
            fireWhenReady = GamePieceController.getInstance().fireWhenReady;
            shooterAL.setTrigger(TriggerSettings.SHOOTING);
            shooterAL.setFlywheel(FlywheelSettings.FORWARD);
            if(autoMode){
                if(!fireWhenReady) {
                    if(timer.get() > 0.20) return Idle;
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
            timer.stop();
            timer.reset();
        }
    },

    Manual {
        public void enter() {
            // Noop
        }

        public State action() {
            //Manual mode based on controls
            if(flyWheelMan)shooterAL.setFlywheel(FlywheelSettings.MANUAL_FORWARD);
            if(triggerMan)shooterAL.setTrigger(TriggerSettings.SHOOTING);
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



    //Manual settings
    public static boolean triggerMan = gamePieceController.triggerManual;
    public static boolean flyWheelMan = gamePieceController.flywheelManual;

    //delay
    public static Timer timer = new Timer();

    ShooterState() {

    }


}