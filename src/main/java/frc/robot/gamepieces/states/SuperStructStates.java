package frc.robot.gamepieces.states;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

public class SuperStructStates {
    private static final Logger LOGGER = RobotLogManager.getMainLogger(SuperStructStates.class.getName());
    public double turnTicks = RobotMap.TURRET_HOME_TICKS;
    public double wristProportion = RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION;

    public SuperStructStates(double turretAngle, double wristHeight){
        this.turnTicks = turretAngle;
        this.wristProportion = wristHeight;
    }

    public SuperStructStates(){
        this(RobotMap.TURRET_HOME_TICKS, RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION);
    }

    public SuperStructStates(SuperStructStates alternate){
        this.turnTicks = alternate.turnTicks;
    }

    public boolean isTurretHome() {
        double distanceToHome = turnTicks - RobotMap.TURRET_HOME_TICKS;
        if (Math.abs(distanceToHome) <= RobotMap.TURRET_ALLOWABLE_ERROR_TICKS) {
          LOGGER.debug("Turret is home at distance: {}, ticks {}, angle: {}", 
              turnTicks, distanceToHome);
          return true;
        }
        LOGGER.debug("Turret is NOT home at distance: {}, ticks {}, angle: {}", 
            turnTicks);
        return false;
      }

    public boolean fubarTurretState() {
        //Figure out which way the logic goes
        return (turnTicks - RobotMap.TURRET_ALLOWABLE_ERROR_TICKS) > RobotMap.TURRET_LEFT_LIMIT_TICKS || (turnTicks + RobotMap.TURRET_ALLOWABLE_ERROR_TICKS) < RobotMap.TURRET_RIGHT_LIMIT_TICKS;
    }

    public String stateSitRep() {
        return "turnticks: " + turnTicks;
    }

}