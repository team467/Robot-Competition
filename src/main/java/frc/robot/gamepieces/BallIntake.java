package frc.robot.gamepieces;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;

public class BallIntake extends GamePieceBase implements GamePiece {
    
    private static BallIntake instance = null;
    

    
    //motor
    private BallIntake(){
        super("subsystem","name");
    }

    private static WPI_TalonSRX motor;
        
    public enum MotorStatus { //nothing else inside enum
        OFF,
        IN,
        OUT
    }

    private static void initalize() {
        motor = new WPI_TalonSRX(1); //number for motor, talk to electrical, its 1 for now
        motor.setInverted(RobotMap.INTAKE_MOTOR_INVERTED);
    }

    private static void actuate() { 
        LOGGER.debug(""); //set motor status here, on, off, etc...
    }

    public static BallIntake getInstance() {
        if (instance == null) {
            instance = new BallIntake();
        }
        return instance;
    }

    public void periodic(){

    }





    
}