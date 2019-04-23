package frc.robot.gamepieces.mechanisms;

import frc.robot.gamepieces.statemachines.SuperStructSM;
import frc.robot.gamepieces.states.SuperStructHQ;
import frc.robot.gamepieces.states.SuperStructStates;

//Controls turret and wrist and if climber is implemented it would go here too
public class MechanismSuperStruct extends GamePieceBase{

    private static MechanismSuperStruct instance = null;

  
    private CargoWrist cargoWrist = CargoWrist.getInstance();
    private Turret turret = Turret.getInstance();
    private IntakeStructure intake = IntakeStructure.getInstance();

    private SuperStructSM stateMachine = new SuperStructSM();
    private SuperStructSM.NeededAction neededAction = SuperStructSM.NeededAction.STAY;

    private SuperStructStates state = new SuperStructStates();

    private boolean isTurretMoving = false;
    private boolean isWristMoving = false;

    private MechanismSuperStruct(){
        super("Telemetry","SuperStructure");
    }

    //Singleton
    public static MechanismSuperStruct getInstance() {
        if(instance == null) {
            instance = new MechanismSuperStruct();
        }
        return instance;
    }

    public SuperStructSM.SuperSystemState getSystemState() {
        return stateMachine.getSystemState();
    }

    public void updateState(SuperStructStates state) {
        state.turnTicks = turret.getTickPosition();
        state.wristTicks = cargoWrist.cargoMechWristTickValueIn();
        
    }

    void setFromHQ(SuperStructHQ command) {
        if(command.turretManual) {
            turret.manual(command.turretPO);
        } else {
            turret.setPosition(command.turnAngle);
        }
        if(command.wristManual) {
            cargoWrist.manualWristMove(command.wristPO);
        } else {
            cargoWrist.setPosition(command.wristHeight);
        }
    }

    public SuperStructStates getState() {
        return state;
    }

    @Override
    public void periodic() {
        SuperStructHQ command;
        updateState(state);

        command = stateMachine.update(neededAction, state);
        setFromHQ(command);
    }

    @Override
    public boolean systemCheck() {
        return true;
    }

}