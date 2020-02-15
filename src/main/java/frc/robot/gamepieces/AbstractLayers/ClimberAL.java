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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
//adds solenoid class
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Relay.Value;

public class ClimberAL extends GamePieceBase implements GamePiece {

    // logger
    private static final Logger LOGGER = RobotLogManager.getMainLogger(ClimberAL.class.getName());

    // inst representing climber
    private static ClimberAL instance = null;

    // motors
    private static CANSparkMax climbLeader;
    private static CANSparkMax climbFollower;
    private static SparkMaxSpeedControllerGroup climbGroup;
    private static Relay climbLock;
    private static DigitalInput topSensor;
    private static DigitalInput bottomSensor;
    private static DigitalInput tiltLimitSwitch;

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

    public boolean isTilted() {
        return false; // TODO: is climberArmLifted?
    }

    private float climberPosition() {
        return 0.0f; // TODO: what to do with this?
    }

    // gets the instance
    public static ClimberAL getInstance() {
        // creates new instance if none exists
        LOGGER.error("Instance is {}", instance);
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
                        RobotMap.CLIMBER_MOTOR_INVERTED, climbLeader, climbFollower);

                climbGroup.pidf(RobotMap.CLIMBER_PID_SLOT, RobotMap.CLIMBER_P, RobotMap.CLIMBER_I, RobotMap.CLIMBER_D, RobotMap.CLIMBER_F,
                        RobotMap.VELOCITY_MULTIPLIER_CLIMBER);
            } else {
                climbGroup = new SparkMaxSpeedControllerGroup();
            }

            if (RobotMap.HAS_CLIMBLOCK) {
                climbLock = new Relay(RobotMap.CLIMB_LOCK_CHANNEL);
            } else {
                climbLock = null;
            }

            if (RobotMap.HAS_CLIMB_TOP_SENSOR) {
                topSensor = new DigitalInput(RobotMap.CLIMB_TOP_SENSOR_CHANNEL);
            } else {
                topSensor = null;
            }

            if (RobotMap.HAS_CLIMB_BOTTOM_SENSOR) {
                bottomSensor = new DigitalInput(RobotMap.CLIMB_BOTTOM_SENSOR_CHANNEL);
            } else {
                bottomSensor = null;
            }

            if (RobotMap.HAS_CLIMB_TILT_SWITCH) {
                tiltLimitSwitch = new DigitalInput(RobotMap.CLIMB_TILT_SWITCH_CHANNEL);
            } else {
                tiltLimitSwitch = null;
            }

            instance = new ClimberAL(climbGroup); // invoking the constructor
            LOGGER.error("Instance is {}", instance);

            instance.stopMotors();

        }
        LOGGER.error("Instance is {}", instance);
        return instance;
    }

    public void climberOff() {
        climbGroup.set(0.0);
        LOGGER.debug("Climber Has Stopped");
    }

    public void climberUp() {
        climbGroup.set(0.5);
        LOGGER.debug("CLimber Is Going Up");
    }

    public void climberDown() {
        climbGroup.set(0.5);
        LOGGER.debug("Climber Is Going Down");
    }

    public enum climberSpeed {
        OFF, UP, DOWN;
    }

    public void climberDirection(climberSpeed direction) {
        switch (direction) {
        case OFF:
        default:
            climberOff();
            break;
        case UP:
            climberUp();
            break;
        case DOWN:
            climberDown();
            break;
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

    public void stopMotors() {
        climbGroup.set(0.0);
    }

    public void climberLock() {
        if (climbLock != null && RobotMap.HAS_CLIMBLOCK) {
            climbLock.set(Value.kOn);
        }
    }

    public void climberUnlock() {
        if (climbLock != null && RobotMap.HAS_CLIMBLOCK) {
            climbLock.set(Value.kOff);
        }
    }

    // public or private
    public void setClimberPIDF(double kP, double kI, double kD, double kF, double maxVelocity) {
        if (climbGroup != null && RobotMap.HAS_CLIMBER) {
            climbGroup.pidf(RobotMap.CLIMBER_PID_SLOT, kP, kI, kD, kF, maxVelocity);
        }
    }

    public void setClimb(double speed) {
        if (climbGroup != null && RobotMap.HAS_CLIMBER) {
            speed = Math.max(-1.0, Math.min(1.0, speed));
            climbGroup.set(ControlType.kVelocity, speed);
        }
    }

    public void setSpeed(double speed) {
        if (climbGroup != null && RobotMap.HAS_CLIMBER) {
            speed = Math.max(-1.0, Math.min(1.0, speed));
            climbGroup.set(speed);
        }
    }

    public double getVelocity() {
        double speed = 0;
        if (climbGroup != null && RobotMap.HAS_CLIMBER) {
            speed = climbGroup.velocity();
        }
        return speed;
    }

    public double getPosition() {
        double position = 0;
        if (climbGroup != null && RobotMap.HAS_CLIMBER) {
            position = climbGroup.position();
        }
        return position;
    }

    public boolean getTopSensor() {
        boolean result = false;
        if (topSensor != null && RobotMap.HAS_CLIMB_TOP_SENSOR) {
            result = !topSensor.get();
        }
        
        if (RobotMap.CLIMB_TOP_SENSOR_INVERTED) {
            result = !result;
        }
        
        return result;
    }

    public boolean getBottomSensor() {
        boolean result = false;
        if (bottomSensor != null && RobotMap.HAS_CLIMB_BOTTOM_SENSOR) {
            result = !bottomSensor.get();
        }
        
        if (RobotMap.CLIMB_BOTTOM_SENSOR_INVERTED) {
            result = !result;
        }
        
        return result;
    }

    public boolean getTiltSwitch() {
        boolean result = false;
        if (bottomSensor != null && RobotMap.HAS_CLIMB_TILT_SWITCH) {
            result = !tiltLimitSwitch.get();
            if (RobotMap.CLIMB_TILT_SWITCH_INVERTED) {
                result = !result;
            }
        }
        return result;
    }

    public void periodic() {
        if (RobotMap.HAS_CLIMBER) {
            if (enabled) {
                // climberDirection(direction);
            } else {
                // ClimberAL.set(climberSpeed.OFF);
            }
        }
    }

    @Override
    public void checkSystem() {
        // TODO Auto-generated method stub

    }

    // TODO: tie climbersm to gpc, check how shooter is done
}
