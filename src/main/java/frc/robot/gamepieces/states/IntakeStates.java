package frc.robot.gamepieces.states;

import frc.robot.RobotMap;

public class IntakeStates {
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
        //deprecated until LEDs implemented
    }

    public double wristHeight = 0;

    public CargoIntakeArmStates cargoArmIntakeArmState = CargoIntakeArmStates.DOWN;
    public HatchArmStates hatchArmState = HatchArmStates.IN;
    public HatchLauncherStates hatchLauncherState = HatchLauncherStates.RESET;

    public boolean wristManual = false;

    public double RollerMotorP = 0;
    public double CargoMotorP = 0;
    public double wristP = 0;

    public void setCargoPower(double power) {
        CargoMotorP = power;
    }

    public void setRollerPower(double power) {
        RollerMotorP = power;
    }

    public void setWristPower(double power) {
        wristP = 0;
    }
}