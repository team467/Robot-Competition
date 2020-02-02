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
    //logger
    private static final Logger LOGGER = RobotLogManager.getMainLogger(Climber.class.getName());
    //inst reresenting climber
    private static Climber instance = null;
    //motors
    private static CANSparkMax climbLeader;
    private static CANSparkMax climbFollower;
    private static SparkMaxSpeedControllerGroup climbGroup;
    //status of robot
    private ClimberStatus status;
//constructor
    private Climber(SparkMaxSpeedControllerGroup climber) { // constructor
        super("Telemetry", "Climber");
    }
    
    //gets the instance
    public static Climber getInstance() {
        //creates new instance if none exists
        if (instance == null) {
            if (RobotMap.HAS_CLIMBER) {
                //instantiates clomber motors
                climbLeader = new CANSparkMax(RobotMap.CLIMB_MOTER_LEADER, MotorType.kBrushless);
                climbFollower = null;

                if (RobotMap.HAS_CLIMBFOLLOWER) {
                    climbFollower = new CANSparkMax(RobotMap.CLIMB_MOTER_FOLLOWER, MotorType.kBrushless);
                }
                //creates control group
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
        // stops the climber so that the robot doesnt move
        climbGroup.set(0.0);
    }

    //method to make the climber move up or down
    public void set(ClimberStatus status) {
        this.status = status;
    }

    public enum ClimberStatus {
        UP, DOWN, STOP;

        public void actuate() {
            if (RobotMap.HAS_CLIMBER) {
                switch (this) {
                case STOP:
                default:
                    climbGroup.set(0.0);
                    LOGGER.debug("Climber Stopped");
                    break;
                case UP:
                    climbGroup.set(1.0);
                    LOGGER.debug("Climber Going Up");
                    break;
                case DOWN:
                    climbGroup.set(-1.0);
                    LOGGER.debug("Climber Going Down");
                    break;
                }
            }
        }
    }

    public void periodic() {
        // figure out inputs TODO
        // switch block deciding how to power motors TODO
        if (RobotMap.HAS_CLIMBER) {
            if (enabled) {
                status.actuate();
            } else {
                stop();
            }
        }

    }
}
