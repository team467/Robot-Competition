package frc.robot.LED;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.gamepieces.AbstractLayers.*;
import frc.robot.LED.LEDStrip.Pattern;

import java.awt.Color;

import org.apache.logging.log4j.Logger;

public class LEDController {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(LEDController.class.getName());

    private static LEDController instance = null;
    private static LEDStrip strip = null;
    private boolean hasBall = false;
    private boolean hasTarget = false;

    // gets the instance
    public static LEDController getInstance() {
        if (instance == null) {
            instance = new LEDController();
        }
        return instance;
    }

    // constructor
    private LEDController() {
        // creates the strip
        strip = LEDStrip.getInstance();
    }

    // updates and prints stuff
    public void update() {
        setBall(IndexerAL.getInstance().isBallInMouth() && IndexerAL.getInstance().isBallInChamber());
        setTarget(false);// get value 4 this TODO
        if (hasBall & hasTarget) {
            strip.setColorEnum(Pattern.GREEN);
        }
        if (!hasBall & hasTarget) {
            strip.setColorEnum(Pattern.BLUE);
        }
        if (hasBall & !hasTarget) {
            strip.setColorEnum(Pattern.YELLOW);
        }
        if (!hasBall & !hasTarget) {
            strip.setColorEnum(Pattern.BLANK);
        }
    }

    private void setBall(boolean hasBall) {
        this.hasBall = hasBall;
    }

    private void setTarget(boolean hasTarget) {
        this.hasTarget = hasTarget;
    }

}