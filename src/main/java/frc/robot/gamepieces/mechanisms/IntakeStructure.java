package frc.robot.gamepieces.mechanisms;

import frc.robot.gamepieces.statemachines.IntakeSM;
import frc.robot.gamepieces.statemachines.IntakeSM.IntakeSystemState;
import frc.robot.gamepieces.statemachines.IntakeSM.NeededAction;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.IntakeStates.CargoIntakeArmStates;
import frc.robot.gamepieces.states.IntakeStates.HatchArmStates;
import frc.robot.gamepieces.states.IntakeStates.HatchLauncherStates;

public class IntakeStructure extends GamePieceBase {

    private IntakeStructure instance = null;

    private CargoClaw cargoClaw = CargoClaw.getInstance();
    private CargoIntakeArm cargoIntakeArm = CargoIntakeArm.getInstance();
    private CargoIntakeRoller cargoIntakeRoller = CargoIntakeRoller.getInstance();
    private HatchLauncher hatchLauncher = HatchLauncher.getInstance();
    private HatchArm hatchArm = HatchArm.getInstance();

    private IntakeSM.NeededAction neededAction = NeededAction.NEED_MANUAL;

    private IntakeStates.CargoIntakeArmStates cargoIntakeArmState = CargoIntakeArmStates.UP;
    private IntakeStates.HatchArmStates hatchArmState = HatchArmStates.IN;
    private IntakeStates.HatchLauncherStates hatchLauncherState = HatchLauncherStates.RESET;
    
    private IntakeStates currentState = new IntakeStates();
    private IntakeSM stateMachine = new IntakeSM();

    private IntakeStructure() {
        super("Telemtry", "IntakeStructure");
    }

    //singleton
    public IntakeStructure getInstance(){
        if(instance == null){
            instance = new IntakeStructure();
        }
        return instance;
    }
    
    public IntakeStates getCurrentState() {
        return currentState;
    }

    public IntakeStates.CargoIntakeArmStates getCargoIntakeArmStates() {
        return cargoIntakeArmState;
    }

    public IntakeStates.HatchArmStates getHatchArmStates() {
        return hatchArmState;
    }

    public IntakeStates.HatchLauncherStates getHatchlauncherState() {
        return hatchLauncherState;
    }

    public void setState(IntakeSM.NeededAction action) {
        neededAction = action;
    }

    //intake roller lumped in with grabber because both can be running
    public void setPower(double power) {
        currentState.setCargoPower(power);
        currentState.setRollerPower(power);
    }

    public void shootCargo(double power) {
        setState(IntakeSM.NeededAction.NEED_MANUAL);
        setPower(power);
    }

    public void extendHatchArm() {
       stateMachine.setHatchArm(HatchArmStates.OUT);
    }

    public void extendHatchLauncher() {
        stateMachine.setHatchLauncher(HatchLauncherStates.FIRE);
    }

    public void retractHatchArm() {
        stateMachine.setHatchArm(HatchArmStates.IN);
    }

    public void retractHatchLauncher() {
        stateMachine.setHatchLauncher(HatchLauncherStates.RESET);
    }

    public void intakeArmUp() {
        stateMachine.setCargoArm(CargoIntakeArmStates.UP);
    }

    public void intakeArmDown() {
        stateMachine.setCargoArm(CargoIntakeArmStates.DOWN);
    }

    public void shootHatch(){
        setState(IntakeSM.NeededAction.NEED_MANUAL);
        extendHatchArm();
        extendHatchLauncher();

    }

    public void updatefromState(IntakeStates currentState){
        cargoClaw.set(currentState.CargoMotorP);
        cargoIntakeRoller.set(currentState.RollerMotorP);
        hatchArm.actuate();
        hatchLauncher.actuate();
    }
 
    @Override
    public void periodic() {

    }

    @Override
    public boolean systemCheck() {
        return false;
    }

}