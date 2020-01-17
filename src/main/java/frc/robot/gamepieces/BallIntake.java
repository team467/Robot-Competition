package frc.robot.gamepieces;

import frc.robot.RobotMap;




public class BallIntake extends GamePieceBase implements GamePiece
{
    private static BallIntake instance;
    //motor
    private BallIntake(){
        super("subsystem","name");
    }
    public enum MotorStatus {
        OFF,
        IN,
        OUT,
    }
    public static void initialize() {

    }
    private static void actuate() { 

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