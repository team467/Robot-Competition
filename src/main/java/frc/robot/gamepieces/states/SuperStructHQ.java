package frc.robot.gamepieces.states;

import frc.robot.RobotMap;

public class SuperStructHQ {
    public double turnAngle = RobotMap.TURRET_HOME_TICKS;
    public double wristProportion = RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS;
    public boolean intakeArmDown = true;
    
    public boolean turretManual = false;
    public double turretPO = 0.0;


}