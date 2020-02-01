/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.RobotMap;
import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.drive.SparkMaxSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;
import com.revrobotics.*;

public class Climber extends GamePieceBase implements GamePiece {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(Climber.class.getName());

    private static Climber instance = null;

    private static CANSparkMax climbLeader;
    private static CANSparkMax climbFollower;
    private static SparkMaxSpeedControllerGroup climbGroup;

    private Climber(SparkMaxSpeedControllerGroup climber) { // constructor
        super("Telemetry", "Climber");
    }

    public static Climber getInstance() {

        if (instance == null) {
            if (RobotMap.HAS_CLIMBER) {
                climbLeader = new CANSparkMax(RobotMap.CLIMB_MOTER_LEADER, MotorType.kBrushless);
                climbFollower = null;

                if (RobotMap.HAS_CLIMBFOLLOWER) {
                    climbFollower = new CANSparkMax(RobotMap.CLIMB_MOTER_FOLLOWER, MotorType.kBrushless);
                }

                climbGroup = new SparkMaxSpeedControllerGroup("Climber", ControlType.kVelocity, RobotMap.CLIMBER_SENSOR,
                        RobotMap.motorIsInverted, climbLeader, climbFollower);

            } else {
                climbGroup = new SparkMaxSpeedControllerGroup();
            }
            instance = new Climber(climbGroup);

            instance.stop();

        }
        return instance;
    }

    public void stop() {
        //to be filled in with anything if nessecary
        climbGroup.set(0.0);
    }

    public void periodic() {
        //figure out inputs TODO
        //switch block deciding how to power motors TODO
        if (RobotMap.HAS_CLIMBER) {
            if (enabled) {
                
            } else {
                stop();
            }
        }

    }
}
