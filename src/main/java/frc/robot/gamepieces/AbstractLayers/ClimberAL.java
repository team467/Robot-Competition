/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.gamepieces.AbstractLayers;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.RobotMap;

import frc.robot.drive.SparkMaxSpeedControllerGroup;
import frc.robot.logging.RobotLogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import com.revrobotics.*;
import frc.robot.gamepieces.GamePieceBase;
import frc.robot.gamepieces.GamePiece;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
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
    private static CANSparkMax climberMotor;
    // private static CANSparkMax climbFollower;
    private static SparkMaxSpeedControllerGroup climberGroup;
    private static Relay climbLock;
    // private static DigitalInput topSensor;
    private static DigitalInput bottomSensor;
    // private static DigitalInput tiltLimitSwitch;
    // private static AnalogPotentiometer potentiometer;

    // states of robot
    private climberSpeed speed;
    // threshold
    private double lowestPoint = 1.0; // TODO: determine threshold value
    private double highestPoint = 5.0; // TODO: determine threshold value

    // climber tuner
    public boolean hasHighestPoint = false;
    public boolean hasLowestPoint = false;

    // constructor
    private ClimberAL(SparkMaxSpeedControllerGroup climberGroup) { // constructor
        super("Telemetry", "Climber");
        this.climberGroup = climberGroup;
    }
    
    public boolean isDown() { // TODO: equal to or not
        if (!getBottomSensor() || getPosition() <= lowestPoint) {
            return true;
        }
        return false;
    }

    public boolean isUp() { // TODO: equal or not
        if (getPosition() >= highestPoint) {
            return true;
        }
        return false;
    }

    // gets the instance
    public static ClimberAL getInstance() {
        // creates new instance if none exists
        LOGGER.debug("Instance is {}", instance);
        if (instance == null) {
            if (RobotMap.HAS_CLIMBER) {
                // instantiates clomber motors
                climberMotor = new CANSparkMax(RobotMap.CLIMB_MOTOR, MotorType.kBrushless);
                climberMotor.setIdleMode(IdleMode.kBrake);

                // creates control group
                climberGroup = new SparkMaxSpeedControllerGroup("Climber", ControlType.kVelocity, RobotMap.CLIMBER_SENSOR,
                        RobotMap.CLIMBER_MOTOR_INVERTED, climberMotor);

                climberGroup.pidf(RobotMap.CLIMBER_PID_SLOT, RobotMap.CLIMBER_P, RobotMap.CLIMBER_I, RobotMap.CLIMBER_D,
                        RobotMap.CLIMBER_F, RobotMap.VELOCITY_MULTIPLIER_CLIMBER);
            } else {
                climberGroup = new SparkMaxSpeedControllerGroup();
            }

            if (RobotMap.HAS_CLIMB_BOTTOM_SENSOR) {
                bottomSensor = new DigitalInput(RobotMap.CLIMB_BOTTOM_SENSOR_CHANNEL);
            } else {
                bottomSensor = null;
            }

            instance = new ClimberAL(climberGroup); // invoking the constructor
            LOGGER.debug("Instance is {}", instance);

            instance.stopMotors();
        } 
        LOGGER.debug("Instance is {}", instance);
        return instance;
    }

    public void climberOff() {
        climberGroup.set(0.0);
        LOGGER.debug("Climber Has Stopped");
    }

    public void climberUp() {
        climberGroup.set(1.0);
        LOGGER.debug("CLimber Is Going Up");
    }

    public void climberDown() {
        climberGroup.set(-1.0);
        LOGGER.debug("Climber Is Going Down");
    }

    public void climberUpSlow() {
        climberGroup.set(0.1); // TODO: how slow? 5%?
        LOGGER.debug("Climber Is Going Up Slowly");
    }

    public void climberDownSlow() {
        climberGroup.set(-0.1);
        LOGGER.debug("Climber Is Going Down Slowly");
    }

    public enum climberSpeed {
        OFF, UP, UPSLOW, DOWN, DOWNSLOW;
    }

    public void climberMotion(climberSpeed motion) {
        switch (motion) {
        case OFF:
        default:
            climberOff();
            break;
        case UP:
            climberUp();
            break;
        case UPSLOW:
            climberUpSlow();
            break;
        case DOWN:
            climberDown();
            break;
        case DOWNSLOW:
            climberDownSlow();
            break;
        }
    }
//will the motor stop when turning on the climber?

    public void stopMotors() {
        LOGGER.debug("motors has stopped");
        climberGroup.set(0.0);
    }

    // public or private
    public void setClimberPIDF(double kP, double kI, double kD, double kF, double maxVelocity) {
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            climberGroup.pidf(RobotMap.CLIMBER_PID_SLOT, kP, kI, kD, kF, maxVelocity);
        }
    }

    public void setClimb(double speed) {
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            speed = Math.max(-1.0, Math.min(1.0, speed));
            climberGroup.set(ControlType.kVelocity, speed);
        }
    }

    public void setSpeed(double speed) {
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            speed = Math.max(-1.0, Math.min(1.0, speed));
            climberGroup.set(speed);
        }
    }

    public double getSpeed() {
        double speed = 0;
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            speed = climberGroup.velocity();
        }
        return speed;
    }

    public double getPosition() {
        double position = 0;
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            position = climberGroup.position();
        }
        return position;
    }

    public void resetPosition() {
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            climberGroup.zero();
        }
    }
    public double getCurrent() {
        double current = 0;
        if (climberGroup != null && RobotMap.HAS_CLIMBER) {
            current = climberGroup.current();
        }
        return current;
    }

    /**
     * @return what the topsensor sees, true if something is detected false if
     *         nothing is detected
     */
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

    @Override
    public void checkSystem() {
    }

    // TODO: tie climbersm to gpc, check how shooter is done
}
