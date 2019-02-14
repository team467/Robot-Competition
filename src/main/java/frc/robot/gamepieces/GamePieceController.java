package frc.robot.gamepieces;

import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.HatchMech.HatchMechArm;
import frc.robot.logging.RobotLogManager;
import frc.robot.usercontrol.DriverStation467;
import frc.robot.vision.CameraSwitcher;

import org.apache.logging.log4j.Logger;

public class GamePieceController {

  private static GamePieceController instance = null;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceController.class.getName());

  // Game Pieces
  private CargoIntake cargoIntake;
  private CargoMech cargoMech;
  private HatchMech hatchMech;
  private Turret turret;
  private CameraSwitcher camera;

  private DriverStation467 driverStation;

  private GamePieceMode gamePieceMode;

  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetryBuilder the telemetry builder instance
   */  /**
   * Returns a singleton instance of the telemery builder.
   * 
   * @return TelemetrGamePieceControllerthe telemetry builder instance
   */
  public static GamePieceController getInstance()  {
    if (instance == null) {
      instance = new GamePieceController();
    }
    return instance;
  }

  public enum GamePieceMode {
    DEFENSE,
    CARGO,
    HATCH;
  }

  private GamePieceController() {
    
    cargoIntake = CargoIntake.getInstance();
    cargoMech = CargoMech.getInstance();
    hatchMech = HatchMech.getInstance();
    turret = Turret.getInstance();
    driverStation = DriverStation467.getInstance();
    gamePieceMode = GamePieceMode.DEFENSE;

  }

  /**
   * checks for states.
   */
  public void periodic() {

    if (true) { // TODO: driverStation.acquireCargo()
      gamePieceMode = GamePieceMode.CARGO;
    }
    if (gamePieceMode == GamePieceMode.CARGO) {
      if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
        cargoIntake.roller(CargoIntakeRoller.REVERSE);
      }
    }

    if (true) { // TODO: driverStation.defenseMode()
      gamePieceMode = GamePieceMode.DEFENSE;
    }
    if (gamePieceMode == GamePieceMode.DEFENSE) {
      turret.moveTurretToHome();
      if (turret.isHome()) {
        if (cargoIntake.arm() == CargoIntakeArm.DOWN) {
          cargoIntake.arm(CargoIntakeArm.UP);
        }
        if (hatchMech.arm() == HatchMechArm.EXTEND) {
          hatchMech.arm(HatchMechArm.RETRACT);
        }
      }
    }

    if (true) { // TODO: driverStation.acquireHatch()
      gamePieceMode = GamePieceMode.HATCH;
    }
    if (gamePieceMode == GamePieceMode.HATCH) {
      if (hatchMech.arm() == HatchMechArm.RETRACT) {
        hatchMech.launcher(HatchLauncher.EXTEND);
      }
    }

    cargoIntake.periodic();
    cargoMech.periodic();
    hatchMech.periodic();
    turret.periodic();
    
    /*
      Action	System	Controller	Button/Stick	Behavior Notes	LED
      Camera back	Camera	Driver Xbox	D-Pad Back		
      Camera front	Camera	Driver Xbox	D-Pad Up		
      Acquire ball	Cargo Intake	Driver Xbox	Right trigger	Roller must be down	
      Leave defense mode	Cargo Intake	Driver Xbox	Left & Right Bumper	Cargo/Hatch last mode, default hatch	
      Reject ball	Cargo Intake	Driver Xbox	Left trigger	Roller must be down	
      Set defense mode	Cargo Intake / CaM	Driver Xbox	Left & Right Bumper	Roller must be up, CaM must be secured	Red blinking when securing, solid red when secure
      Drive forward/back	Drive	Driver Xbox	Left Stick	Only up/down	
      Turn right/left	Drive	Driver Xbox	Right Stick	Only left/right	
      Fire cargo	CaM	Nav Xbox	Left Trigger	Needs to be in hatch mode and pressing camera bumper. Rumbles when on target. Must pull more than 50%	
      Get Cargo into CaM	CaM	Nav Xbox	Y	Move Cargo Arm to Bottom and activate gripper reverse, Roller arm must be down, must be in cargo mode	
      Set cargo mode	CaM	Nav Xbox	Right Bumper		Blue
      Set cargo ship	CaM	Nav Xbox	D-Pad Up		
      Set low rocket	CaM	Nav Xbox	D-Pad Down	Must be in cargo mode, adds or subtracts position	
      Set mid rocket	CaM	Nav Xbox	Not implemented		
      Cargo camera	Camera	Nav Xbox	Right Bumper	Must be in cargo mode	
      Hatch camera	Camera	Nav Xbox	Left Bumper	Must be in hatch mode	
      Acquire hatch	HaM	Nav Xbox	Right Trigger	Must be in hatch mode, Must pull more than 50%, reverses when not pressed	
      Fire hatch	HaM	Nav Xbox	Left Trigger	Needs to be in hatch mode and pressing camera bumper. Rumbles when on target. Must pull more than 50%	Sequence blink
      Set hatch mode	HaM	Nav Xbox	Left Bumper		Gold
      Fine adjust turret	Turret	Nav Xbox	Right stick	Manual	
      Target lock	Turret	Nav Xbox	A	If implemented, must be on target	
      Turret front	Turret	Nav Xbox	Y	In cargo mode, active acquire sequence	
      Turret left	Turret	Nav Xbox	X	Relative to mode, in cargo mode, if arm is low, first needs to go to low rocket	
      Turret right	Turret	Nav Xbox	B	Relative to mode, in cargo mode, if arm is low, first needs to go to low rocket	
    */

  }


}