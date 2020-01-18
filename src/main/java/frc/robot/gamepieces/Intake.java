// /*----------------------------------------------------------------------------*/
// /* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

package frc.robot.gamepieces;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.logging.Telemetry;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.apache.logging.log4j.Logger;

/**
 * Add your docs here.
 */
public class Intake extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(Intake.class.getName());

    private static Intake instance = null;

    private IntakeRoller roller;
    private IntakeArm arm;

    public enum IntakeArm {
        STOP, UP, DOWN;

        private static WPI_TalonSRX armMotor;

        private static void initialize() {
            armMotor = new WPI_TalonSRX(RobotMap.ARM_MOTOR);
            armMotor.setInverted(false);
        }

        private void actuate() {
            if (RobotMap.HAS_INTAKE) {
                switch (this) {
                case STOP:
                default:
                    armMotor.set(0.0);
                    break;
                case UP:
                    armMotor.set(1.0);
                    break;

                case DOWN:
                    armMotor.set(-1.0);
                    break;
                }
            }
        }

    }

    public enum IntakeRoller {
        OFF, REJECT, INTAKE;

        private static WPI_TalonSRX rollerMotor;

        private static void initialize() {
            rollerMotor = new WPI_TalonSRX(RobotMap.ROLLER_MOTOR_CHANNEL);
            rollerMotor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
        }

        private void actuate() {
                if (RobotMap.HAS_INTAKE) {
                    switch(this) {
                        case OFF:
                        default: 
                            rollerMotor.set(0.0);
                            break;
                        case REJECT:
                            rollerMotor.set(1.0);
                            break;

                        case INTAKE:
                            rollerMotor.set(-1.0);
                            break;
                    }
                }
            }

    }

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        return instance;
    }

    private Intake() {
        super("Telemetry", "Intake");
        // Initialize

        IntakeArm.initialize();
        IntakeRoller.initialize();

    }

    public IntakeArm arm() {
        return arm;
    }

    public IntakeRoller roller() {
        return roller;
    }

    public void periodic() {

        if (enabled) {
            roller.actuate();
            arm.actuate();
        }
    }

}
