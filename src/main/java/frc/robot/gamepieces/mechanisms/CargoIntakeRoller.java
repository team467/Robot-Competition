package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoIntakeRoller extends GamePieceBase implements GamePiece {
    
    private static Spark motor;

    private CargoIntakeRoller(){
        super("Telemetry", "CargoIntakeRoller");
        motor = new Spark(RobotMap.ROLLER_MOTOR_CHANNEL);
      motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
      motor.setName("Telemetry", "CargoIntakeRollerMotor");
    }

    @Override
    public void read() {
        //n/a
    }

    @Override
    public void actuate() {

    }

    @Override
    public boolean systemCheck() {
        return true;
    }

    @Override
    public void periodic() {

    }

    public class RollerController{
        public double motorDemand;

    }
}