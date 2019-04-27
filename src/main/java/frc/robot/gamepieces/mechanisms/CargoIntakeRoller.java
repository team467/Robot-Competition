package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoIntakeRoller extends GamePieceBase implements GamePiece{
    

    private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoIntakeRoller.class.getName());

    private static Spark motor;

    private static CargoIntakeRoller instance = null;
    public RollerController rollerController; 


    private CargoIntakeRoller() {
        super("Telemetry", "CargoIntakeRoller");
        motor = new Spark(RobotMap.ROLLER_MOTOR_CHANNEL);
        motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
        motor.setName("Telemetry", "CargoIntakeRollerMotor");
    }
    
    public static CargoIntakeRoller getInstance() {
        if (instance == null) {
            instance = new CargoIntakeRoller();
          }
          return instance;
    }

    @Override
    public void read() {
        //n/a
        return;
    }

    @Override
    public void actuate() {
        LOGGER.debug("Actuate cargo intake roller: {}", this);
    }

  

    public void set(double power){
        rollerController.motorDemand = power; 
    }

    @Override
    public boolean systemCheck() {
        return true;
    }

    @Override
    public void periodic() {
        read();
        actuate();

    }

    public class RollerController{
        public double motorDemand;

    }

    @Override
    public void stop() {
        set(0.0);
    }
}