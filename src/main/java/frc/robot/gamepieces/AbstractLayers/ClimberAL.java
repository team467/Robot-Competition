/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.AbstractLayers;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.RobotMap;
import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.drive.SparkMaxSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;
import com.revrobotics.*;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePiece;

//adds solenoid class
import edu.wpi.first.wpilibj.Solenoid;

public class ClimberAL extends GamePieceBase implements GamePiece {

    // logger
    private static final Logger LOGGER = RobotLogManager.getMainLogger(ClimberAL.class.getName());

    // inst representing climber
    private static ClimberAL instance = null;

    // motors
    private static CANSparkMax climbLeader;
    private static CANSparkMax climbFollower;
    private static SparkMaxSpeedControllerGroup climbGroup;

    // states of robot
    private climberSpeed speed;

    // solenoid on lock mechanism
    Solenoid solenoidLock = new Solenoid(1); // TODO: which port will the solenoid be connected to?

    // constructor
    private ClimberAL(SparkMaxSpeedControllerGroup climbGroup) { // constructor
        super("Telemetry", "Climber");
        this.climbGroup = climbGroup;
    }

    // method to make the climber move up or down
    public void set(climberSpeed speed) {
        this.speed = speed;
    }

    // starts the solenoid
    public void initialize() {
        solenoidLock.set(true); // sets solennoid state
        // TODO: delay function for solenoid!
    }

    public boolean isDown() {
        return false; // TODO: is climber at its lowest?
    }

    public boolean isUp() {
        return false; // TODO: is climber at its highest?
    }

    public boolean climberArmLifted() { 
        return false; // TODO: is climberArmLifted?
    }

    private float climberPosition() {
        return 0.0f; // TODO: what to do with this?
    }

    // gets the instance
    public static ClimberAL getInstance() {
        // creates new instance if none exists
        if (instance == null) {
            if (RobotMap.HAS_CLIMBER) {
                // instantiates clomber motors
                climbLeader = new CANSparkMax(RobotMap.CLIMB_MOTER_LEADER, MotorType.kBrushless);
                climbFollower = null;

                if (RobotMap.HAS_CLIMBFOLLOWER) {
                    climbFollower = new CANSparkMax(RobotMap.CLIMB_MOTER_FOLLOWER, MotorType.kBrushless);
                }
                // creates control group
                climbGroup = new SparkMaxSpeedControllerGroup("Climber", ControlType.kVelocity, RobotMap.CLIMBER_SENSOR,
                        RobotMap.motorIsInverted, climbLeader, climbFollower);

            } else {
                climbGroup = new SparkMaxSpeedControllerGroup();
            }
            instance = new ClimberAL(climbGroup); //invoking the constructor

            instance.stop();

        }
        return instance;
    }

    public enum climberSpeed {
        UP, DOWN, STOP, DOWNSTOP;

        public void actuate() {
            if (RobotMap.HAS_CLIMBER) { // TODO: whats the correct motor speed?
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
                case DOWNSTOP: // slows down the motor
                    climbGroup.set(0.5);
                    LOGGER.debug("Climber Slowed Down For Solenoid Lock");
                    break;
                }
            }
        }
    }

    public enum solenoidLock {
        LOCK, UNLOCK;
    }

    public void setLock(solenoidLock state) {
        switch (state) {
        case LOCK:
            break;
        case UNLOCK:
            break;
        }
    }

    public void stop() {
        climbGroup.set(0.0);
    }

    public void periodic() {
        if (RobotMap.HAS_CLIMBER) {
            if (enabled) {
                speed.actuate();
            } else {
                stop();
            }
        }
    }

    @Override
    public void checkSystem() {
        // TODO Auto-generated method stub

    }
}