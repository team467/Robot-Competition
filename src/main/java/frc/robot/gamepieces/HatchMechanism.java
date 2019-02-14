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
    


    //HatchArm
    public enum HatchArm{
        OFF,
        IN,
        OUT;

    
        private static DoubleSolenoid arm;

        private static void initialize() {
            arm = new DoubleSolenoid(HATCH_ARM_FORWARD_CHANNEL, HATCH_ARM_REVERSE_CHANNEL);
        }

        /**
         * 
         * @return true if the solenoid is in the forward position, false if it is in the backwards position
         */
        private static boolean getSolenoidState(){
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
                    LOGGER.info("Hatch arm is IN.");
                    break;
                case OUT:
                    arm.set(DoubleSolenoid.Value.kForward);
                    LOGGER.info("Hatch arm is OUT.");
                    break;
                default:
                    arm.set(DoubleSolenoid.Value.kOff);
            }
        }


    }

    public enum Firer{
        FIRE,
        UNKNOWN;

        private static DoubleSolenoid firer1;
        private static DoubleSolenoid firer2;
        private static DoubleSolenoid firer3;

        private static void initialize(){
            firer1 = new DoubleSolenoid(FIRING_1_FORWARD_CHANNEL, FIRING_1_REVERSE_CHANNEL);
            firer2 = new DoubleSolenoid(FIRING_2_FORWARD_CHANNEL, FIRING_2_REVERSE_CHANNEL);
            firer3 = new DoubleSolenoid(FIRING_3_FORWARD_CHANNEL, FIRING_3_REVERSE_CHANNEL);
        }

        /**
         * Fires tthe three firing solenoids forwards and then retracts them
         */
        private void fire(){
            //Fire solenoids forward
            firer1.set(DoubleSolenoid.Value.kForward);
            firer2.set(DoubleSolenoid.Value.kForward);
            firer3.set(DoubleSolenoid.Value.kForward);

            //Retract solenoids after firing
            firer1.set(DoubleSolenoid.Value.kReverse);
            firer2.set(DoubleSolenoid.Value.kReverse);
            firer3.set(DoubleSolenoid.Value.kReverse);
        }

        private void actuate(){
            switch(this){
                case FIRE:
                    fire();
                    break;
                default:
                    LOGGER.info("No movement was done with the Hatch Mechanism");
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

      @Override
      public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("HatchArm", hatchArm::name, (command) -> hatchArm(command));
      }
}
