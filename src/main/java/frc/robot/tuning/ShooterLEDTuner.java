package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.TalonSpeedControllerGroup;
import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.logging.RobotLogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

public class ShooterLEDTuner implements Tuner {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(TuneController.class.getName());

    // ShooterAL shooter;
    // TalonSpeedControllerGroup shooterMotor;
    AddressableLED leds;
    AddressableLEDBuffer ledBuffer;
    boolean useVelocity;

    ShooterLEDTuner() {
        // shooter = ShooterAL.getInstance();
        leds = new AddressableLED(0);
        ledBuffer = new AddressableLEDBuffer(5);
        leds.setLength(ledBuffer.getLength());

        for (var i = 0; i < ledBuffer.getLength(); i++) {
          ledBuffer.setRGB(i, 255, 0, 0);
        }

        leds.setData(ledBuffer);
        leds.start();
    }

    public void init() {
        SmartDashboard.putNumber("R", 0);
        SmartDashboard.putNumber("G", 0);
        SmartDashboard.putNumber("B", 0);
        SmartDashboard.putBoolean("ON", false);
    }

    public void periodic() {
        int r = (int) SmartDashboard.getNumber("R", 0);
        int g = (int) SmartDashboard.getNumber("G", 0);
        int b = (int) SmartDashboard.getNumber("B", 0);
        boolean on = SmartDashboard.getBoolean("ON", false);

        // if (on) {
        //     shooter.setLedStrip(r, g, b, 0, 5);
        // } else {
        //     shooter.setLedStrip(0, 0, 0, 0, 5);
        // }
    }
}