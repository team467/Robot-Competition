package frc.robot.gamepieces;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import frc.robot.logging.RobotLogManager;

import org.apache.logging.log4j.Logger;

public class HatchMechanism extends GamePieceBase implements GamePieceInterface{

    private static final Logger LOGGER = RobotLogManager.getMainLogger(HatchMechanism.class.getName());

    private static HatchMechanism instance = null;

    //Actuators
    private HatchArm hatchArm;
    private Firer firer;

    //TODO: Place these values in RobotMap
    public static int HATCH_ARM_FORWARD_CHANNEL;
    public static int HATCH_ARM_REVERSE_CHANNEL;
    public static int HATCH_ARM_IN_SENSOR_CHANNEL;
    public static int HATCH_ARM_OUT_SENSOR_CHANNEL;

    public static int FIRING_1_FORWARD_CHANNEL;
    public static int FIRING_1_REVERSE_CHANNEL;
    public static int FIRING_2_FORWARD_CHANNEL;
    public static int FIRING_2_REVERSE_CHANNEL;
    public static int FIRING_3_FORWARD_CHANNEL;
    public static int FIRING_3_REVERSE_CHANNEL;
    

    //States
    private HatchArmState hatchArmState;

    //HatchArm
    public enum HatchArm{
        OFF,
        IN,
        OUT;

    
        private static DoubleSolenoid arm;

        private static void initialize() {
            arm = new DoubleSolenoid(HATCH_ARM_FORWARD_CHANNEL, HATCH_ARM_REVERSE_CHANNEL);
        }

        private static boolean getSolenoid(){
            boolean solenoidForward;
            switch(arm.get()){
                case kForward:
                    solenoidForward = true;
                    break;
                case kReverse:
                    solenoidForward = false;
                    break;
                default:
                    solenoidForward = false;
                    LOGGER.debug("Solenoid state undetermined");
            }
            return solenoidForward;
        }

        private void actuate(){
            switch(this){
                case IN:
                    arm.set(DoubleSolenoid.Value.kReverse);
                    break;
                case OUT:
                    arm.set(DoubleSolenoid.Value.kForward);
                    break;
                default:
                    arm.set(DoubleSolenoid.Value.kOff);
            }
        }


    }

    public enum Firer{
        READY,
        FIRING,
        UNKNOWN;

        private static DoubleSolenoid firer1;
        private static DoubleSolenoid firer2;
        private static DoubleSolenoid firer3;

        private static void initialize(){
            firer1 = new DoubleSolenoid(FIRING_1_FORWARD_CHANNEL, FIRING_1_REVERSE_CHANNEL);
            firer2 = new DoubleSolenoid(FIRING_2_FORWARD_CHANNEL, FIRING_2_REVERSE_CHANNEL);
            firer3 = new DoubleSolenoid(FIRING_3_FORWARD_CHANNEL, FIRING_3_REVERSE_CHANNEL);
        }

        private void fire(){
            //Use multithreading for firing all 3 solenoids at once?
        }

        private void actuate(){
            switch(this){
                case FIRING:
                    fire();
                    break;
                default:
                    break;
            }
        }
    }

    public static HatchMechanism getInstance(){
        if(instance == null){
            instance = new HatchMechanism();
        }
        return instance;
    }

    //Constructor
    private HatchMechanism(){
        super("Telemetry", "HatchMechanism");

        //Initialize sensors and actuators
        HatchArm.initialize();
        Firer.initialize();

        hatchArm = HatchArm.IN;
        hatchArmState = HatchArmState.read();
    }
    
  /**
   * Moves the roller arm up or down.
   * 
   * @param command which way to move the arm.
   */
    public void hatchArm(HatchArm command){
        hatchArm = command;
    }

    /**
   * Moves the arm in or out. The String version sets the 
   * command from the Smart Dashboard.
   * 
   * @param command which way to move the arm.
   */
    public void hatchArm(String command){
        hatchArm = HatchArm.valueOf(command);
    }

    public HatchArmState hatchArm(){
        return hatchArmState;
    }

    public void periodic() {
        // Take Actions
        if (enabled) {
          hatchArm.actuate();
        //   latch.actuate();
        }
        // Update state
        hatchArmState = HatchArmState.read();
        // latchState = LatchState.read();
      }

      @Override
      public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("HatchArm", hatchArm::name, (command) -> hatchArm(command));
        builder.addStringProperty("HatchArmState", hatchArmState::name, null);
      }
}
