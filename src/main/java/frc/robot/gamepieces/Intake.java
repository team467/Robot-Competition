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

    private static final Logger LOGGER 
    = RobotLogManager.getMainLogger(Intake.class.getName());

    private static Intake instance = null;

    private IntakeRoller roller; 
    private IntakeArm arm;

    public enum IntakeArm {
        STOP,
        UP,
        DOWN;

        private static WPI_TalonSRX armMotorLeader;
        private static WPI_TalonSRX armMotorFollwoer;

        private static void initialize() {
            armMotorLeader = new WPI_TalonSRX(1);
            armMotorLeader.setInverted(false);

            armMotorFollwoer = new WPI_TalonSRX(2);
            armMotorFollwoer.setInverted(false);
        }

    }

    // private void actuate() {
    //     if (RobotMap.HAS_INTAKE) {
    //         switch(arm) {
    //             case STOP:
    //             default: 
    //                 armMotorLeader.set(0.0);
    //                 armMotorFollower.set(0.0);
    //                 break;
    //             case UP:
    //                 armMotorLeader.set(1.0);
    //                 armMotorFollower.set(1.0);
    //             break;

    //             case DOWN;
    //                 armMotorLeader.set(-1.0);
    //                 armMotorFollower.set(-1.0);
    //             break;
    //         }
    //     }
    // }

    public enum IntakeRoller {
        OFF,
        REJECT,
        INTAKE;

        private static WPI_TalonSRX motor;

        private static void initialize() {
            motor = new WPI_TalonSRX(RobotMap.ROLLER_MOTOR_CHANNEL);
            motor.setInverted(RobotMap.ROLLER_MOTOR_INVERTED);
        }

    }

    // private void actuate() {
    //     if (RobotMap.HAS_INTAKE) {
    //         switch(roller) {
    //             case OFF:
    //             default: 
    //                 motor.set(0.0);
    //                 break;
    //             case REJECT:
    //                 motor.set(1.0);
    //                 break;

    //             case INTAKE;
    //                 motor.set(-1.0);
    //                 break;
    //         }
    //     }
    // }

  
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

 

    public void periodic() {

        if (enabled) {
            // roller.actuate();
            // arm.actuate();
        }
    }

}
