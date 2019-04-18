package frc.robot.gamepieces.mechanisms;

//Controls all the mechanisms
public class MechanismSuperStruct extends GamePieceBase{

    private CargoClaw cargoClaw = CargoClaw.getInstance();
    private CargoIntakeArm cargoIntakeArm = CargoIntakeArm.getInstance();
    private CargoIntakeRoller cargoIntakeRoller = CargoIntakeRoller.getInstance();
    private CargoWrist cargoWrist = CargoWrist.getInstance();
    private HatchArm hatchArm = HatchArm.getInstance();
    private HatchLauncher hatchLauncher = HatchLauncher.getInstance();
    private Turret turret = Turret.getInstance();

    private MechanismSuperStruct(){
        super("Telemetry","SuperStructure");
    }
    @Override
    public void periodic() {

    }

    @Override
    public boolean systemCheck() {
        return false;
    }

}