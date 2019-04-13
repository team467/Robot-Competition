package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.RobotMap;
import frc.robot.drive.TalonProxy;
import frc.robot.drive.WpiTalonSrxInterface;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoClaw extends GamePieceBase implements GamePiece {
    
    
    private static final Logger LOGGER = RobotLogManager.getMainLogger(CargoClaw.class.getName());

    private static CargoClaw instance = null;

    private static Spark motorLeader;
    private static Spark motorFollower;

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
    public enum clawCurrentStates{
        FIRE, 
        INTAKE, 
        STOP;
    }

    /**
     * Moves the belts of the claw forward or backward based on the requested
     * command.
     */
    private void actuate(clawCurrentStates state) {
      LOGGER.debug("Actuating cargo mech claw");
      
      LOGGER.debug("Calling Claw Actuate state: {}", state);
      if (RobotMap.HAS_CARGO_MECHANISM) {
        switch (state) {
          case FIRE:
              motorLeader.set(1.0);
              motorFollower.set(1.0);
            LOGGER.debug("Claw going forward");
            break;

          case INTAKE:
              motorLeader.set(-1.0);
              motorFollower.set(-1.0);
            LOGGER.debug("Claw going backward");
            break;

          case STOP:
          default:
              motorLeader.set(0.0);
              motorFollower.set(0.0);
            LOGGER.debug("Claw is stopping");
        }
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
    public boolean checksystem() {
        return false;
    }

    @Override
    public void periodic() {

    }
}