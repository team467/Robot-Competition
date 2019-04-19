package frc.robot.gamepieces.mechanisms;

import frc.robot.gamepieces.statemachines.IntakeSM;
import frc.robot.gamepieces.statemachines.IntakeSM.NeededAction;
import frc.robot.gamepieces.states.IntakeStates;
import frc.robot.gamepieces.states.IntakeStates.CargoIntakeArmStates;
import frc.robot.gamepieces.states.IntakeStates.HatchArmStates;
import frc.robot.gamepieces.states.IntakeStates.HatchLauncherStates;

public class IntakeStructure extends GamePieceBase {


    private CargoClaw cargoClaw = CargoClaw.getInstance();
    private CargoIntakeArm cargoIntakeArm = CargoIntakeArm.getInstance();
    private HatchLauncher hatchLauncher = HatchLauncher.getInstance();
    private HatchArm hatchArm = HatchArm.getInstance();

    private IntakeSM.NeededAction neededAction = NeededAction.NEED_MANUAL;

    private IntakeStates.CargoIntakeArmStates cargoIntakeArmState = CargoIntakeArmStates.UP;
    private IntakeStates.HatchArmStates hatchArmStates = HatchArmStates.IN;
    private IntakeStates.HatchLauncherStates hatchLauncherStates = HatchLauncherStates.RESET;


    private IntakeStructure() {
        super("Telemtry", "IntakeStructure");
    }

    @Override
    public void periodic() {

    }

    @Override
    public boolean systemCheck() {
        return false;
    }

}