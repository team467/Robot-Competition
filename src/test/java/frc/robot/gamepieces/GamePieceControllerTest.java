package frc.robot.gamepieces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import frc.robot.Robot;
import frc.robot.Robot.RobotMode;
import frc.robot.RobotMap;
import frc.robot.RobotMap.RobotId;
import frc.robot.gamepieces.CargoIntake.CargoIntakeArm;
import frc.robot.gamepieces.CargoIntake.CargoIntakeRoller;
import frc.robot.gamepieces.CargoMech.CargoMechClaw;
import frc.robot.gamepieces.CargoMech.CargoMechWrist;
import frc.robot.gamepieces.CargoMech.CargoMechWristState;
import frc.robot.gamepieces.GamePieceController.GamePieceMode;
import frc.robot.gamepieces.HatchMechanism.HatchArm;
import frc.robot.gamepieces.HatchMechanism.HatchLauncher;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.TelemetryBuilder;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GamePieceControllerTest {

  private static TelemetryBuilder telemetry;
  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(GamePieceControllerTest.class.getName());

  private static Robot robot;
  private static GamePieceController controller;
  private static HatchMechanism hatch;
  private static CargoIntake intake;
  private static CargoMech cargo;
  private static Turret turret;

  // Inputs normally from driver station

  // Driver camera
  private boolean driveCameraFront; 
  private boolean driveCameraRear;

  // Disable Safety
  private boolean disableSafety;

  // Mode switches
  private boolean defenseMode;
  private boolean cargoMode;
  private boolean hatchMode;
  
  // Hatch mode inputs
  private boolean acquireHatch;
  private boolean fireHatch;
  
  // Cargo mode inputs
  private boolean acquireCargo;
  private boolean fireCargo;
  private boolean moveCargoWristToCargoShipPosition;
  private boolean moveCargoWristToLowRocketPosition;
  private double  manualWristMove;

  // Cargo intake inputs (either hatch or cargo mode)
  private boolean rejectCargo;
  private boolean intakeCargo;

  // Turret inputs (either hatch or cargo mode)
  private boolean moveTurretRight;
  private boolean moveTurretLeft;
  private boolean moveTurretToHome;
  private boolean enableTargetLock;
  private double  manualTurretMove;

  /**
   * Sets up robot in simulation mode and gets the test object.
   */
  @BeforeClass
  public static void initAll() {
    RobotMap.init(RobotId.ROBOT_2019);
    Robot.enableSimulator();
    robot = new Robot();
    robot.robotInit();

    controller = GamePieceController.getInstance();
    hatch = HatchMechanism.getInstance();
    intake = CargoIntake.getInstance();
    cargo = CargoMech.getInstance();
    turret = Turret.getInstance();

    hatch.enabled(true);
    intake.enabled(true);
    cargo.enabled(true);
    turret.enabled(true);

    telemetry = TelemetryBuilder.getInstance();
    telemetry.robotMode(RobotMode.EXTERNAL_TEST);
  }

  /**
   * Resets the game piece controller before each test.
   */
  @Before
  public void init() {

    // Set input defaults
    // override in individual tests
    driveCameraFront = false; 
    driveCameraRear = false;
    disableSafety = false;
    defenseMode = false;
    cargoMode = false;
    hatchMode = false;
    acquireCargo = false;
    fireCargo = false;
    moveCargoWristToCargoShipPosition = false;
    moveCargoWristToLowRocketPosition = false;
    acquireHatch = false;
    fireHatch = false;
    rejectCargo = false;
    intakeCargo = false;
    moveTurretRight = false; 
    moveTurretLeft = false;
    moveTurretToHome = false;
    enableTargetLock = false;
    manualWristMove = 0.0;
    manualTurretMove = 0.0;

    // reset the robot
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION));
    turret.simulatedSensorData(RobotMap.TURRET_HOME);
    intake.arm(CargoIntakeArm.UP); 
    intake.roller(CargoIntakeRoller.STOP);
    cargo.wrist(CargoMechWrist.CARGO_BIN);
    cargo.claw(CargoMechClaw.STOP);
    hatch.arm(HatchArm.IN);
    hatch.launcher(HatchLauncher.RESET);
    defenseMode = true;
    controller.switchMode(defenseMode, hatchMode, cargoMode);
    controller.updateGamePieces(); // Need the game pieces to update their interal state.
    defenseMode = false;
    LOGGER.debug("Reset the game pieces to the initial game state");

    // Verify initial state
    assertTrue(intake.arm() == CargoIntakeArm.UP); 
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);
    
  }

  @Test
  public void testSwitchMode() {


    cargoMode = true;
    GamePieceMode mode = controller.switchMode(defenseMode, hatchMode, cargoMode);
    assertTrue(mode == GamePieceMode.CARGO);
  }

  @Test
  public void testCargoModeAcquireBall() {

    // Set initial state
    cargoMode = true;
    LOGGER.debug("Press cargo mode button");

    LOGGER.debug("Verify initial CARGO mode state.");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    // Set pilot/specialist input
    cargoMode = false; // Don't need to press the mode switch button anymore.
    LOGGER.debug("Release cargo mode button.");
    acquireCargo = true;
    LOGGER.debug("Press acquire cargo button.");


    // Iteration 1, Arm started down and turret started in position, 
    // so verify rollers turned on
    LOGGER.debug("Iteration 1: Arm is down, turn rollers on.");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.INTAKE);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.INTAKE);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    // Release pilot/specialist input
    acquireCargo = false;
    LOGGER.debug("Release acquire cargo button.");

    // Iteration 2, Verify rollers stopped
    LOGGER.debug("Final: verify rollers stopped.");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);
  }

  @Test
  public void testMoveTurretRightAndCargoWristToRocket() {

    // Set initial state
    cargoMode = true;
    LOGGER.debug("Press cargo mode button.");

    LOGGER.debug("Verify initial CARGO mode state.");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    // Set pilot/specialist input
    cargoMode = false;
    LOGGER.debug("Release cargo mode button.");
    moveTurretRight = true;
    moveCargoWristToLowRocketPosition = true;
    LOGGER.debug("Press both turret right and cargo wrist low rocket buttons.");

    // Iteration 1, Arm started down and turret started at home. 
    // Assumes we have a cargo ball.
    // Verify cargo wrist moves to the turret safe position
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_SAFE_TURRET_PROPORTION));
    // Sensor simulation should come before process state
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.isSafeToMoveTurret());
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    // Iteration 2, move the turret to the right
    turret.simulatedSensorData(-90.0); 
    // Sensor simulation should come before process state
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.isSafeToMoveTurret());
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(-90.0, turret.position(), 1.0);

    // Iteration 3, move the wrist to the right height.
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_LOW_ROCKET_PROPORTION));
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.LOW_ROCKET);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(-90.0, turret.position(), 1.0);

    // Release pilot/specialist input
    moveTurretRight = false;
    moveCargoWristToLowRocketPosition = false;
    LOGGER.debug("Relase both turret right and cargo wrist to low rocket buttons.");


    // Iteration 4, Verify in position to fire.
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.LOW_ROCKET);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(-90.0, turret.position(), 1.0);

    // Release pilot/specialist input
    fireCargo = true;
    LOGGER.debug("Press fire cargo button.");

    // Iteration 5, Verify fire cargo
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.LOW_ROCKET);
    assertTrue(cargo.claw() == CargoMechClaw.FIRE);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(-90.0, turret.position(), 1.0);

    // Release pilot/specialist input
    fireCargo = false;
    LOGGER.debug("Release button.");

    // Iteration 5, Verify reset
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.LOW_ROCKET);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(-90.0, turret.position(), 1.0);
  }

  @Test
  public void enterDefenseMode() {

    // Set initial state
    LOGGER.debug("Setup initial conditions with wrist up and turret away.");
    cargoMode = true;
    callProcessState();
    cargoMode = false;
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_SAFE_TURRET_PROPORTION));
    // Sensor simulation should come before process state
    callProcessState();
    moveTurretLeft = true;
    moveCargoWristToCargoShipPosition = true;
    turret.simulatedSensorData(90.0); 
    callProcessState();
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_CARGO_SHIP_PROPORTION));
    callProcessState();
    moveTurretLeft = false;
    moveCargoWristToCargoShipPosition = false;
    callProcessState();

    // Verify initial state
    LOGGER.debug("Verify initial state with turret turned 90 and wrist up at cargo ship.");
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_SHIP);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(90.0, turret.position(), 1.0);

    // Set pilot/specialist input
    LOGGER.debug("Press the activate DEFENSE mode button");
    defenseMode = true;

    LOGGER.debug("Iteration 1: Wrist is safe, move the turret home.");
    turret.simulatedSensorData(0.0); 
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.isSafeToMoveTurret());
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    // Release pilot/specialist input
    LOGGER.debug("Release the DEFENSE mode button.");
    defenseMode = false; // Conversion should continue   
    
    LOGGER.debug("Iteration 2: Moving wrist down into to bin.");
    CargoMech.simulatedSensorData(
        CargoMechWrist.heightTicksFromProportion(RobotMap.CARGO_MECH_CARGO_BIN_PROPORTION));
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.DOWN);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    LOGGER.debug("Iteration 3: Moving intake arm up.");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.UP);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);

    LOGGER.debug("Final verify in DEFENSE state");
    callProcessState();
    assertTrue(intake.arm() == CargoIntakeArm.UP);
    assertTrue(intake.roller() == CargoIntakeRoller.STOP);
    assertTrue(cargo.wrist() == CargoMechWristState.CARGO_BIN);
    assertTrue(cargo.claw() == CargoMechClaw.STOP);
    assertTrue(hatch.arm() == HatchArm.IN);
    assertTrue(hatch.launcher() == HatchLauncher.RESET);
    assertEquals(0.0, turret.position(), 1.0);
  }

  private void callProcessState() {
    controller.processGamePieceState(
        driveCameraFront, 
        driveCameraRear,
        disableSafety,
        defenseMode, 
        hatchMode, 
        cargoMode,
        acquireCargo, 
        fireCargo, 
        moveCargoWristToCargoShipPosition, 
        moveCargoWristToLowRocketPosition,
        acquireHatch, 
        fireHatch, 
        rejectCargo, 
        intakeCargo, 
        moveTurretRight, 
        moveTurretLeft, 
        moveTurretToHome, 
        enableTargetLock, 
        manualWristMove, 
        manualTurretMove);
    telemetry.updateTable();
  }

  @AfterClass
  public static void closeAll() {
    robot.close();
  }

}