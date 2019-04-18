package frc.robot.gamepieces.states;

import frc.robot.RobotMap;

public class SuperStructStates {
    public double turnAngle = RobotMap.TURRET_HOME_TICKS;
    public double wristProportion = RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION;

    public SuperStructStates(double angle, double height){
        this.turnAngle = angle;
        this.wristProportion = height;
    }

    public SuperStructStates(){
        this(RobotMap.TURRET_HOME_TICKS, RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION);
    }

}