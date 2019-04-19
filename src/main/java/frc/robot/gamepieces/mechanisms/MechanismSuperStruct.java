package frc.robot.gamepieces.mechanisms;

import frc.robot.gamepieces.statemachines.SuperStructSM;
import frc.robot.gamepieces.states.SuperStructStates;

//Controls turret and wrist
public class MechanismSuperStruct extends GamePieceBase{

    private static MechanismSuperStruct instance = null;

  
    private CargoWrist cargoWrist = CargoWrist.getInstance();
    private Turret turret = Turret.getInstance();

    private SuperStructSM stateMachine = new SuperStructSM();
    private SuperStructSM.NeededAction neededAction = SuperStructSM.NeededAction.STAY;

    private SuperStructStates state = new SuperStructStates();

    private MechanismSuperStruct(){
        super("Telemetry","SuperStructure");
    }

    //Singleton
    public MechanismSuperStruct getInstance() {
        if(instance == null) {
            instance = new MechanismSuperStruct();
        }
        return instance;
    }

    @Override
    public void periodic() {

    }

    @Override
    public boolean systemCheck() {
        return false;
    }

}