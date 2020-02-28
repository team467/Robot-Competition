// /*----------------------------------------------------------------------------*/                     
// /* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.AbstractLayers;

import frc.robot.RobotMap;
import frc.robot.gamepieces.GamePiece;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.logging.RobotLogManager;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
// import frc.robot.logging.Telemetry;

import org.apache.logging.log4j.Logger;

public class IntakeAL extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(IntakeAL.class.getName());

    private static IntakeAL instance = null;
   
    private static WPI_TalonSRX arm;
    private static WPI_TalonSRX roller;

    public static IntakeAL getInstance() {
        if (instance == null) {
            if(RobotMap.HAS_INTAKE){
            arm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_CHANNEL);
            roller = new WPI_TalonSRX(RobotMap.ROLLER_MOTOR_CHANNEL);
            }

            instance = new IntakeAL();
        }
        return instance;
    }
     
    private void setUp() {
        LOGGER.debug("setUp called");
        arm.set(1.0);
    }

    private void setDown() {
        LOGGER.debug("setUp called");
        arm.set(-1.0);
    }

    private void setForward() {
        LOGGER.debug("setForward called");
        roller.set(1.0);
    }

    private void setBackward() {
        LOGGER.debug("setBackward called");
        roller.set(-1.0);
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

    @Override
    public void checkSystem() {
        // TODO Auto-generated method stub

    }

}