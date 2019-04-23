package frc.robot.gamepieces.statemachines;

import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;
import frc.robot.gamepieces.GamePieceController;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.SuperStructHQ;
import frc.robot.gamepieces.states.SuperStructStates;
import frc.robot.gamepieces.states.IntakeStates.HatchArmStates;
import frc.robot.gamepieces.states.IntakeStates.HatchLauncherStates;
import frc.robot.logging.RobotLogManager;

public class IntakeSM {
    private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(IntakeSM.class.getName());

    public enum NeededAction {
        NEED_OBJ,
        NEED_MANUAL,

    }

    public enum IntakeSystemState {
        KEEPING_OBJ,
        MOVING,
        MANUAL

    }

    private IntakeSystemState systemState = IntakeSystemState.MANUAL;

    private IntakeStates commandedState = new IntakeStates();
    private IntakeStates.CargoIntakeArmStates neededCargoIntakeArmState = IntakeStates.CargoIntakeArmStates.UP;
    private IntakeStates.HatchArmStates neededHatchArmState = IntakeStates.HatchArmStates.IN;
    private IntakeStates.HatchLauncherStates neededHatchLauncherState = IntakeStates.HatchLauncherStates.RESET;

    private IntakeStates intakeStates;

    //TODO: update functionality of gamepiece controller
 //   private GamePieceController gamePieceController = new GamePieceController();

    private double manualPower = 0.0;
    private double intakeCargoPower = -1.0;
    
    //in ticks

    //sets
    public void resetManual() {
        manualPower = 0.0;
    }

    public void setManualPower(double power) {
        manualPower = power;
    }

    public void setHatchArm(final IntakeStates.HatchArmStates hatchArmState) {
        neededHatchArmState = hatchArmState;
    }

    public void setCargoArm(final IntakeStates.CargoIntakeArmStates cargoIntakeArmState){
        neededCargoIntakeArmState = cargoIntakeArmState;
    }

    public void setHatchLauncher(final IntakeStates.HatchLauncherStates hatchLauncherState) {
        neededHatchLauncherState = hatchLauncherState;
    }

    //gets
    public IntakeStates.CargoIntakeArmStates getCargoIntakeState() {
        return neededCargoIntakeArmState;
    }

    public IntakeStates.HatchArmStates getHatcharmState() {
        return neededHatchArmState;
    }

    public IntakeStates.HatchLauncherStates getHatchLauncherState() {
        return neededHatchLauncherState;
    }



    public IntakeSystemState getSystemState(){
        return systemState;
    }

    public IntakeStates update(NeededAction neededAction, IntakeStates currentState) {
       
        IntakeSystemState newState;
        switch(systemState) {
            case KEEPING_OBJ:
                newState = handleManualTransition(neededAction, currentState);
            case MANUAL:
                newState = KeepOBJTransition(neededAction, currentState);
            break;
            default:
                LOGGER.error("INVALID INTAKE STATE!" + systemState);
                newState = systemState;
        }

        if(newState != systemState) {
            LOGGER.info("Robot Super Structure transitioned from {} -> {}", systemState, newState);
            systemState = newState;
        }

        switch(systemState){
            case KEEPING_OBJ:
            getKeepOBJTransitionCommandState(currentState, commandedState);
        case MANUAL:
            getManualCommandState(currentState, commandedState);
        break;
        default:
            getManualCommandState(currentState, commandedState);          
        break;
        }
       
        return commandedState;
    }

    private IntakeSystemState handleManualTransition(NeededAction neededAction, IntakeStates currentState){
        switch(neededAction){
            case NEED_OBJ:
                return IntakeSystemState.KEEPING_OBJ;
            case NEED_MANUAL:
            default:
                return IntakeSystemState.MANUAL;
        }
    }

    private void getManualCommandState(IntakeStates currentState, IntakeStates commandedState) {
        commandedState.setCargoPower(manualPower);
        commandedState.setRollerPower(manualPower);
        commandedState.hatchArmState = neededHatchArmState;
        commandedState.hatchLauncherState = neededHatchLauncherState;


        //more condition checking
    }

    private IntakeSystemState KeepOBJTransition(NeededAction neededAction, IntakeStates currentState) {        
        switch(neededAction) {
            case NEED_MANUAL:
                return IntakeSystemState.MANUAL;
            default:
                return IntakeSystemState.KEEPING_OBJ;
        }
    }

    private void getKeepOBJTransitionCommandState(IntakeStates currentState, IntakeStates commandedState) {
        commandedState.setCargoPower(intakeCargoPower);
        commandedState.setRollerPower(intakeCargoPower);
        commandedState.hatchArmState = HatchArmStates.IN;
        commandedState.hatchLauncherState = HatchLauncherStates.RESET;

    }
}