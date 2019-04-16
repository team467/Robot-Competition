package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoClaw extends GamePieceBase implements GamePiece {
    
    
    private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoClaw.class.getName());

    private static CargoClaw instance = null;

    private static Spark motorLeader;
    private static Spark motorFollower;

    public clawController clawcontroller = new clawController();

    private CargoClaw() {
        super("Telemetry", "CargoClaw");
      // Create the roller object. No sensors
      LOGGER.trace("Initializing Claw");
      if (RobotMap.HAS_CARGO_MECHANISM) {
        motorLeader = new Spark(RobotMap.CARGO_MECH_CLAW_LEFT_MOTOR_CHANNEL);
        motorLeader.setInverted(RobotMap.CARGO_MECH_CLAW_LEFT_MOTOR_INVERTED);
        
        motorFollower = new Spark(RobotMap.CARGO_MECH_CLAW_RIGHT_MOTOR_CHANNEL);
        motorFollower.setInverted(RobotMap.CARGO_MECH_CLAW_RIGHT_MOTOR_INVERTED);
        LOGGER.debug("Spark channels: {}, {}", 
            box(motorLeader.getChannel()), box(motorFollower.getChannel()));
      }
    }

    public void setClawActuation(clawCurrentStates state){
        switch (state) {
            case FIRE:
               clawcontroller.demand = 1.0;
              LOGGER.debug("Claw going forward");
              break;
  
            case INTAKE:
                clawcontroller.demand = -1.0;
              LOGGER.debug("Claw going backward");
              break;
  
            case STOP:
            default:
            clawcontroller.demand = 0.0;
              LOGGER.debug("Claw is stopping");

        }
    }

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */
  public static CargoClaw getInstance() {
    if (instance == null) {
      instance = new CargoClaw();
    }
    return instance;
  }

    @Override
    public boolean systemCheck() {
        motorLeader.checkMotors();
        return true;
    }

    @Override
    public void periodic() {
        actuate();

    }

    @Override
    public void read() {
        //no sensors nothing to read
    }

    public enum clawCurrentStates {
        FIRE, 
        INTAKE, 
        STOP;
    }

    public enum controlStates{
        //uneeded only 1 control state
    }

    public class clawController{
        public double demand;
    }

    @Override
    public void actuate() {
        motorLeader.set(clawcontroller.demand);
        motorFollower.set(clawcontroller.demand);

    }
}