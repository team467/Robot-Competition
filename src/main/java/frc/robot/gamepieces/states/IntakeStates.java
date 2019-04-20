package frc.robot.gamepieces.states;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;
import org.apache.logging.log4j.Logger;

public class IntakeStates {
    
    private static final Logger LOGGER 
    = RobotLogManager.getMainLogger(IntakeStates.class.getName());

    public enum CargoIntakeArmStates  {
        UP,
        DOWN,
        OFF
    }

    public enum HatchArmStates {
        OUT,
        IN,
        OFF
    }

    public enum HatchLauncherStates {
        FIRE,
        RESET
    }

    public enum LEDstates {
        //not used until LEDs implemented
    }

    public CargoIntakeArmStates cargoArmIntakeArmState = CargoIntakeArmStates.DOWN;
    public HatchArmStates hatchArmState = HatchArmStates.IN;
    public HatchLauncherStates hatchLauncherState = HatchLauncherStates.RESET;

    public double RollerMotorP = 0;
    public double CargoMotorP = 0;

    public void setCargoPower(double power) {
        CargoMotorP = power;
    }

    public void setRollerPower(double power) {
        RollerMotorP = power;
    }

    public void fatalReset() {
        cargoArmIntakeArmState = CargoIntakeArmStates.DOWN;
        hatchArmState = HatchArmStates.IN;
        hatchLauncherState = HatchLauncherStates.RESET;
        CargoMotorP = 0.0;
        RollerMotorP = 0.0;
        LOGGER.error("Robot has encountered fatal state going to neutral state");
    }
}