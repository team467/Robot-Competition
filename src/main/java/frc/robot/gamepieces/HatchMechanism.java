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
    private Latch latch;
    private Firing firing;

    //TODO: Place these values in RobotMap
    public static int HATCH_ARM_FORWARD_CHANNEL;
    public static int HATCH_ARM_REVERSE_CHANNEL;
    public static int HATCH_ARM_IN_SENSOR_CHANNEL;
    public static int HATCH_ARM_OUT_SENSOR_CHANNEL;

    public static int LATCH_DEVICE_CHANNEL;
    public static int LATCH_ENGAGE_SENSOR_CHANNEL;
    public static int LATCH_DISENGAGE_SENSOR_CHANNEL;

    public static int FIRING_1_FORWARD_CHANNEL;
    public static int FIRING_1_REVERSE_CHANNEL;
    public static int FIRING_2_FORWARD_CHANNEL;
    public static int FIRING_2_REVERSE_CHANNEL;
    public static int FIRING_3_FORWARD_CHANNEL;
    public static int FIRING_3_REVERSE_CHANNEL;
    

    //States
    private HatchArmState hatchArmState;
    private LatchState latchState;

    //HatchArm
    public enum HatchArm{
        OFF,
        IN,
        OUT;

    
        private static DoubleSolenoid arm;

        private static void initialize() {
            arm = new DoubleSolenoid(HATCH_ARM_FORWARD_CHANNEL, HATCH_ARM_REVERSE_CHANNEL);
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

    //HatchArmState
    public enum HatchArmState {
        IN, 
        MOVING_IN,
        OUT,
        MOVING_OUT,
        UNKNOWN;

        //TODO: Find out what sensors if any the arm uses

        private static DigitalInput armIn;
        private static DigitalInput armOut;
        private static HatchArmState previousState;

        private static void initialize(){
            armIn = new DigitalInput(LATCH_ENGAGE_SENSOR_CHANNEL);
            armIn.setName("Telemetry", "armIn");
            armOut = new DigitalInput(LATCH_DISENGAGE_SENSOR_CHANNEL);
            armOut.setName("Telemetry", "armOut");
        }

        private static HatchArmState read(){
            HatchArmState state;
            if (armOut.get()) {
                state = OUT;
            } else if (armIn.get()) {
                state = IN;
            } else if (previousState == OUT || previousState == MOVING_IN) {
                state = MOVING_IN;
            } else if (previousState == IN || previousState == MOVING_IN) {
                state = MOVING_OUT;
            } else {
                state = UNKNOWN;
            }
            previousState = state;
            return state;
        }

    }

    //Latch
    public enum Latch{
        OFF,
        ENGAGE,
        DISENGAGE;

        //TODO: Find device that is used to move latch and declare here

        private static void initialize(){
            //Initialize latch movement device
        }

        private void actuate(){
            switch(this){
                case ENGAGE:
                    //engage latch
                    break;
                case DISENGAGE:
                    //disengage latch
                    break;
                default:
                    //turn device off
            }
        }
    }

    public enum LatchState{
        ENGAGE,
        ENGAGING,
        DISENGAGE,
        DISENGAGING,
        UNKNOWN;

        private static DigitalInput latchEngaged;
        private static DigitalInput latchDisengaged;
        private static LatchState previousState;

        private static void initialize(){
            latchEngaged = new DigitalInput(LATCH_ENGAGE_SENSOR_CHANNEL);
            latchEngaged.setName("Telemetry", "latchEngaged");
            latchDisengaged = new DigitalInput(LATCH_DISENGAGE_SENSOR_CHANNEL);
            latchDisengaged.setName("Telemetry", "latchDisengaged");
        }

        private static LatchState read(){
            LatchState state;
            if (latchEngaged.get()) {
              state = ENGAGE;
            } else if (latchDisengaged.get()) {
              state = DISENGAGE;
            } else if (previousState == ENGAGE || previousState == DISENGAGING) {
              state = DISENGAGING;
            } else if (previousState == DISENGAGE || previousState == ENGAGING) {
              state = ENGAGING;
            } else {
              state = UNKNOWN;
            }
            previousState = state;
            return state;
        }
    }

    public enum Firing{
        FIRING,
        UNKNOWN;

        private static DoubleSolenoid fire1;
        private static DoubleSolenoid fire2;
        private static DoubleSolenoid fire3;

        private static void initialize(){
            fire1 = new DoubleSolenoid(FIRING_1_FORWARD_CHANNEL, FIRING_1_REVERSE_CHANNEL);
            fire2 = new DoubleSolenoid(FIRING_2_FORWARD_CHANNEL, FIRING_2_REVERSE_CHANNEL);
            fire3 = new DoubleSolenoid(FIRING_3_FORWARD_CHANNEL, FIRING_3_REVERSE_CHANNEL);
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
        Latch.initialize();
        Firing.initialize();

        hatchArm = HatchArm.IN;
        latch = Latch.DISENGAGE;
        hatchArmState = HatchArmState.read();
        latchState = LatchState.read();
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

    public void latch(Latch command){
        latch = command;
    }

    public void latch(String command){
        latch = Latch.valueOf(command);
    }

    public LatchState latch(){
        return latchState;
    }

    public void periodic() {
        // Take Actions
        if (enabled) {
          hatchArm.actuate();
          latch.actuate();
        }
        // Update state
        hatchArmState = HatchArmState.read();
        latchState = LatchState.read();
      }

      @Override
      public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("HatchArm", hatchArm::name, (command) -> hatchArm(command));
        builder.addStringProperty("Latch", latch::name, (command) -> latch(command));
        builder.addStringProperty("HatchArmState", hatchArmState::name, null);
        builder.addStringProperty("LatchState", latchState::name, null);
      }
}
