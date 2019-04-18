package frc.robot.gamepieces.statemachines;

import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.states.SuperStructHQ;
import frc.robot.gamepieces.states.SuperStructStates;
import frc.robot.logging.RobotLogManager;

public class SuperStructSM {
    private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(SuperStructSM.class.getName());
    public enum NeededAction {
        STAY,
        NEED_POS,
        NEED_MANUAL

    }

    public enum SuperSystemState{
        STOPPING,
        MOVING,
        MANUAL

    }

    private SuperSystemState systemState = SuperSystemState.STOPPING;

    private SuperStructHQ SuperStructCommand = new SuperStructHQ();

    private SuperStructStates commandedState = new SuperStructStates();
    private SuperStructStates neededState = new SuperStructStates();

    //TODO: update functionality of gamepiece controller
 //   private GamePieceController gamePieceController = new GamePieceController();

    private double manualPower = 0.0;
    
    private double wristScoringHeight = RobotMap.CARGO_MECH_WRIST_BOTTOM_TICKS;
    private double turretTurnTicks = RobotMap.TURRET_HOME_TICKS;

    //in ticks
    private double turretMaxTurnL = RobotMap.TURRET_LEFT_LIMIT_TICKS;
    private double turretMaxTurnR = RobotMap.TURRET_RIGHT_LIMIT_TICKS;
    private double maxWristHeight = RobotMap.CARGO_WRIST_UP_LIMIT_TICKS;

    //sets
    public void resetManual() {
        manualPower = 0.0;
    }

    public void setMaxTurretTurn(double ticksR, double ticksL) {
        turretMaxTurnL = ticksL;
        turretMaxTurnR = ticksL;
    }

    public void setMaxWristHeight(double ticksH) {
        maxWristHeight = ticksH;
    }

    public void setManualPower(double power) {
        manualPower = power;
    }

    public void setWristHeight(double height) {
        wristScoringHeight = height;
    }

    public void setTurretTurn(double ticks) {
        turretTurnTicks = ticks;
    }

    //gets
    public double getWristHeight() {
        return wristScoringHeight;
    }

    public double getTurretTurn() {
        return turretTurnTicks;
    }

    public SuperSystemState getSystemState(){
        return systemState;
    }

    //wrist is comparing proportion to ticks TODO: need to fix
    public boolean scoringChange(){
        return (neededState.turnAngle != turretTurnTicks) || (neededState.wristProportion != wristScoringHeight);
    }

    public void updatePlanDesired(SuperStructStates currentState){
        neededState.turnAngle = turretTurnTicks;
        neededState.wristProportion = wristScoringHeight;

    }

    public SuperSystemState update(NeededAction neededAction, SuperStructStates currentState) {
        SuperSystemState newState;
        switch(systemState) {
            case STOPPING:
                newState = StopTransition(neededAction, currentState);
            case MOVING:
                newState = moveTransition(neededAction, currentState);
            case MANUAL:
                newState = manualTansition(neededAction, currentState);
            break;
            default:
                LOGGER.error("INVALID SUPER STRUCTURE STATE!" + systemState);
                newState = systemState;
        }

        if(newState != systemState) {
            LOGGER.info("Robot Super Structure Changed State from {} -> {}", systemState, newState);
            systemState = newState;
        }

        if(SuperStructCommand.turretManual ) {

        }
    }

    private SuperSystemState handleTransition(NeededAction neededAction, SuperStructStates currentState){
        if(neededAction == NeededAction.NEED_POS) {
            if(scoringChange()) {
                updatePlanDesired(currentState);
                //TODO: Add a finished condition
            } else if(true) {
                return SuperSystemState.STOPPING;
            }
            return SuperSystemState.MOVING;
        } else if(neededAction == NeededAction.NEED_MANUAL) {
                return SuperSystemState.MANUAL;
        } else {
            //&& finished condition
            if(systemState == SuperSystemState.MOVING) {
                return SuperSystemState.MOVING;
            } else {
                return SuperSystemState.STOPPING;
            }
        }

    }

    private SuperSystemState StopTransition(NeededAction neededAction, SuperStructStates currentState) {        
        return handleTransition(neededAction, currentState);
    }

    private void getStopTransitionCommandState() {
        SuperStructCommand.turretManual = false;
    }

    private SuperSystemState moveTransition(NeededAction neededAction, SuperStructStates currentState) {        
        return handleTransition(neededAction, currentState);
    }

    private void getMoveTransitionCommandState() {
        SuperStructCommand.turretManual = false;
    }

    private SuperSystemState manualTansition(NeededAction neededAction, SuperStructStates currentState) {
        if(neededAction != NeededAction.NEED_MANUAL) {
            wristScoringHeight = currentState.wristProportion;
            turretTurnTicks = currentState.turnAngle;
            return handleTransition(NeededAction.NEED_POS, currentState);
        }
        return handleTransition(neededAction, currentState);
    }

    private void getManualCommandState() {
        SuperStructCommand.turretManual = true;
        SuperStructCommand.turretPO = manualPower;
    }

}