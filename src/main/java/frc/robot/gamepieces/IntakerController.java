// /*----------------------------------------------------------------------------*/                     
// /* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
// import frc.robot.logging.Telemetry;

import org.apache.logging.log4j.Logger;

public class IntakerController extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(IntakerController.class.getName());

    private static IntakerController instance = null;

    private static WPI_TalonSRX arm;
    private static WPI_TalonSRX roller;

    public static IntakerController getInstance() {
        if (instance == null) {

            if (RobotMap.HAS_INTAKE) {
                arm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_CHANNEL);
                roller = new WPI_TalonSRX(RobotMap.ROLLER_MOTOR_CHANNEL);
            }
            instance = new IntakerController();
        }
        return instance;
    }

    private void setUp() {
        arm.set(1.0);
    }

    private void setDown() {
        arm.set(-1.0);
    }

    private void setForward() {
        roller.set(1.0);
    }

    private void setBackward() {
        roller.set(-1.0);
    }

    private void setRollerStop() {
        roller.set(0.0);
    }

    private void setArmStop() {
        arm.set(0.0);

    }

    public static void callUp() {
        IntakerController.getInstance().setUp();
    }

    public static void callDown() {
        IntakerController.getInstance().setDown();
    }

    public static void callFoward() {
        IntakerController.getInstance().setForward();
    }

    public static void callBackward() {
        IntakerController.getInstance().setBackward();
    }

    public static void callArmStop() {
        IntakerController.getInstance().setArmStop();
    }

    public static void callRollerStop() {
        IntakerController.getInstance().setRollerStop();
    }

    private IntakerController() {
        super("Telemetry", "Intake");
    }

    public void periodic() {

        if (enabled) {
        }

    }

}