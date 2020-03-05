// /*----------------------------------------------------------------------------*/                     
// /* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.GamePiece;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
// import frc.robot.logging.Telemetry;

import org.apache.logging.log4j.Logger;

public class IntakeAL extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(IntakeAL.class.getName());

    private static IntakeAL instance = null;

    private static WPI_TalonSRX armMotor;
    private static WPI_TalonSRX rollerMotor;

    private static TalonSpeedControllerGroup arm;
    private static TalonSpeedControllerGroup roller;

    private static SensorCollection armSensors;

    public static IntakeAL getInstance() {
        if (instance == null) {
            if (RobotMap.HAS_INTAKE) {
                armMotor = new WPI_TalonSRX(RobotMap.ARM_MOTOR_CHANNEL);
                rollerMotor = new WPI_TalonSRX(RobotMap.ROLLER_MOTOR_CHANNEL);

                arm = new TalonSpeedControllerGroup("Arm", ControlMode.PercentOutput, false, RobotMap.ARM_MOTOR_INVERTED, armMotor);
                roller = new TalonSpeedControllerGroup("Roller", ControlMode.PercentOutput, false, RobotMap.ROLLER_MOTOR_INVERTED, rollerMotor);

                armSensors = armMotor.getSensorCollection();
            } else {
                arm = new TalonSpeedControllerGroup();
                roller = new TalonSpeedControllerGroup();
            }

            instance = new IntakeAL();

            instance.stopArm();
            instance.stopRoller();
        }
        return instance;
    }

    public void stopArm() {
        if (arm != null && RobotMap.HAS_INTAKE) {
            arm.set(0.0);
        }
    }

    public void setArmSpeed(double speed) {
        if (arm != null && RobotMap.HAS_INTAKE) {
            double output = Math.max(-1.0, Math.min(1.0, speed));
            arm.set(output);
        }
    }

    public void armUp() {
        if (arm != null && RobotMap.HAS_INTAKE) {
            setArmSpeed(0.5);
        }
    }

    public void armDown() {
        if (arm != null && RobotMap.HAS_INTAKE) {
            setArmSpeed(-0.5);
        }
    }

    public void stopRoller() {
        if (roller != null && RobotMap.HAS_INTAKE) {
            roller.set(0.0);
        }
    }

    public void setRollerSpeed(double speed) {
        if (roller != null && RobotMap.HAS_INTAKE) {
            double output = Math.max(-1.0, Math.min(1.0, speed));
            roller.set(output);
        }
    }

    public boolean getTopLimit() {
        boolean result = false;
        if (roller != null && RobotMap.HAS_INTAKE) {
            result = armSensors.isFwdLimitSwitchClosed();
        }

        if (RobotMap.ARM_TOP_LIMIT_INVERTED) {
            result = !result;
        }

        return result;
    }

    public boolean getBottomLimit() {
        boolean result = false;
        if (roller != null && RobotMap.HAS_INTAKE) {
            result = armSensors.isRevLimitSwitchClosed();
        }

        if (RobotMap.ARM_BOTTOM_LIMIT_INVERTED) {
            result = !result;
        }

        return result;
    }

    private void setUp() {
        LOGGER.debug("setUp called");
        arm.set(0.5);
    }

    private void setDown() {
        LOGGER.debug("setUp called");
        arm.set(-0.5);
    }

    private void setForward() {
        LOGGER.debug("setForward called");
        roller.set(-1.0);
    }

    private void setBackward() {
        LOGGER.debug("setBackward called");
        roller.set(1.0);
    }

    private void setRollerStop() {
        LOGGER.debug("setRollerStop called");
        roller.set(0.0);
    }

    private void setArmStop() {
        LOGGER.debug("setArmStop called");
        arm.set(0.0);

    }

    public static void callUp() {
        IntakeAL.getInstance().setUp();
    }

    public static void callDown() {
        IntakeAL.getInstance().setDown();
    }

    public static void callForward() {
        IntakeAL.getInstance().setForward();
    }

    public static void callBackward() {
        IntakeAL.getInstance().setBackward();
    }

    public static void callArmStop() {
        IntakeAL.getInstance().setArmStop();
    }

    public static void callRollerStop() {
        IntakeAL.getInstance().setRollerStop();
    }

    private IntakeAL() {
        super("Telemetry", "Intake");
    }

    public void periodic() {

        if (enabled) {
        }

    }

    @Override
    public void checkSystem() {
        // TODO Auto-generated method stub

    }

}