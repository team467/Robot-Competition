package frc.robot.gamepieces.states;

import frc.robot.RobotMap;

public class IntakeHQ {
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

    public double wristHeight = 0;

    public CargoIntakeArmStates cargoArmIntakeArmState = CargoIntakeArmStates.DOWN;
    public HatchArmStates hatchArmState = HatchArmStates.IN;

    public double RollerMotorP = 0;
    public double CargoMotorP = 0;

    public void setCargoPower(double power) {
        CargoMotorP = power;
    }

    public void setRollerPower(double power) {
        RollerMotorP = power;
    }
}