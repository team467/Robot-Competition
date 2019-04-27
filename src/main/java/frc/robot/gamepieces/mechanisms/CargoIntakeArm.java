package frc.robot.gamepieces.mechanisms;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.RobotMap;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.IntakeStates.CargoIntakeArmStates;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class CargoIntakeArm extends GamePieceBase implements GamePiece {
  
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(CargoIntakeArm.class.getName());

  private static CargoIntakeArm instance = null;
  
  private static DoubleSolenoid leftSolenoid;
  private static DoubleSolenoid rightSolenoid;
  private static IntakeStates.CargoIntakeArmStates cargoIntakeArmStates = CargoIntakeArmStates.UP;

  // Actuators
  private CargoIntakeArm arm;
  
  public static CargoIntakeArm getInstance() {
    if(instance == null){
      instance = new CargoIntakeArm();
    } 
    return instance;
  }

  private CargoIntakeArm() {
    super("Telemetry", "CargoIntake");
    if (RobotMap.HAS_ROLLER_INTAKE) {
        leftSolenoid = new DoubleSolenoid(RobotMap.ROLLER_PCM_CHANNEL,
        RobotMap.ROLLER_LEFT_ARM_UP_SOLINOID_CHANNEL, 
        RobotMap.ROLLER_LEFT_ARM_DOWN_SOLINOID_CHANNEL);
        leftSolenoid.setName("Telemetry", "RollerArmLeftSolenoid");
        rightSolenoid = new DoubleSolenoid(RobotMap.ROLLER_PCM_CHANNEL,
        RobotMap.ROLLER_RIGHT_ARM_UP_SOLINOID_CHANNEL, 
        RobotMap.ROLLER_RIGHT_ARM_DOWN_SOLINOID_CHANNEL);
        rightSolenoid.setName("Telemetry", "RollerArmRightSolenoid");
    }
    registerMetrics();
    LOGGER.trace("Created roller arm game piece.");
  }


 //Soon to be Ploop
  public void periodic() {
    // Take Actions
    if (enabled) {
      actuate();
    } else {
      LOGGER.debug("Cargo intake mechanism is disabled.");
    }
  }

  //Telem TODO: figure out how this works
  private void registerMetrics() {
     Telemetry telemetry = Telemetry.getInstance();
    // telemetry.addStringMetric("Intake Roller Command", this::rollerCommandString);
    //     telemetry.addStringMetric("Intake Arm Command", this::armCommandString);
    // if (RobotMap.HAS_ROLLER_INTAKE) {
    //   telemetry.addStringMetric("Intake Left Arm Solenoid", this::armLeftSolinoidString);
    //   telemetry.addStringMetric("Intake Right Arm Solenoid", this::armRightSolinoidString);
    //   telemetry.addDoubleMetric("Intake Roller Motor Output", this::rollerMotorOutput);
    //}
  }

  @Override
  public boolean systemCheck() {
    return false;
  }

  @Override
  public void read() {
    //nothing to read
  }

  //TODO: make states for this
  @Override
  public void actuate() {
    switch(cargoIntakeArmStates){
      case UP:
        leftSolenoid.set(DoubleSolenoid.Value.kForward);
        rightSolenoid.set(DoubleSolenoid.Value.kForward);
      case DOWN:
      leftSolenoid.set(DoubleSolenoid.Value.kReverse);
      rightSolenoid.set(DoubleSolenoid.Value.kReverse);
      break;
      default:
      leftSolenoid.set(DoubleSolenoid.Value.kOff);
      rightSolenoid.set(DoubleSolenoid.Value.kOff);
    }
    
  }

  @Override
  public void stop() {
    leftSolenoid.set(DoubleSolenoid.Value.kOff);
    rightSolenoid.set(DoubleSolenoid.Value.kOff);
  }

}