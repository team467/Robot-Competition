package frc.robot.other;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import java.awt.Color;

import org.apache.logging.log4j.Logger;

public class LEDStrip {
    private static final Logger LOGGER = RobotLogManager.getMainLogger(LEDStrip.class.getName());
    private static LEDStrip instance;
    private AddressableLED LEDStrip;
    private AddressableLEDBuffer LEDBuffer;
    private int red = 128;
    private int green = 128;
    private int blue = 128;
    // the time so that the led can update and the lights can blink or move or stuff
    private int time = 0;
    private Pattern mode = Pattern.WHITE;

    public static enum Pattern {
        RGB, RED, GREEN, BLUE, RAINBOW, BLINKING, WHITE, BOOMER
    }

    public static LEDStrip getInstance() {
        if (instance == null) {
            instance = new LEDStrip();
        }
        return instance;
    }

    private LEDStrip() {
        LOGGER.debug("LED Strip Initialized");
        // creating LED Strip
        // leds on PWM 9
        LEDStrip = new AddressableLED(RobotMap.LEDPort);
        // led buffer with 12 leds: the length of the neopixel ring
        LEDBuffer = new AddressableLEDBuffer(RobotMap.LEDCount);
        LEDStrip.setLength(LEDBuffer.getLength());
        // sets data
        LEDStrip.setData(LEDBuffer);
        LEDStrip.start();
    }

    public void periodic() {
        LOGGER.debug("LED Strip Periodic");
        // writes to LEDs
        switch (mode) {
        case RGB:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                LEDBuffer.setRGB(i, red, green, blue);
            }
            break;
        case RED:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                LEDBuffer.setRGB(i, 255, 0, 0);
            }
            break;
        case GREEN:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                LEDBuffer.setRGB(i, 0, 255, 0);
            }
            break;
        case BLUE:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                LEDBuffer.setRGB(i, 0, 0, 255);
            }
            break;
        case RAINBOW:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                Color rainbow = Color.getHSBColor((float) i / RobotMap.LEDCount + (float) time / 50, 1, 1);
                LEDBuffer.setRGB(i, rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue());
            }
            break;
        case BLINKING:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                if (time % 100 < 50) {
                    LEDBuffer.setRGB(i, 0, 0, 0);
                } else {
                    LEDBuffer.setRGB(i, red, green, blue);
                }
            }
            break;
        case WHITE:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                LEDBuffer.setRGB(i, 128, 128, 128);
            }
            break;
        case BOOMER:
            for (int i = 0; i < LEDBuffer.getLength(); i++) {
                int position = i+time;
                if (position % 4 < 2) {
                    LEDBuffer.setRGB(i, 16, 16, 82);
                } else {
                    LEDBuffer.setRGB(i, 203, 224, 43);
                }
            }
            break;
        }
        // copies stuff to LEDS
        LEDStrip.setData(LEDBuffer);
        time++;
    }

    // sets the strip color in an rgb format
    public void setColorRGB(int red, int green, int blue) {
        mode = Pattern.RGB;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public boolean setColorEnum(String patternName) {
        switch (patternName) {
        case "RED":
            mode = Pattern.RED;
            return true;
        case "GREEN":
            mode = Pattern.GREEN;
            return true;
        case "BLUE":
            mode = Pattern.BLUE;
            return true;
        case "RAINBOW":
            mode = Pattern.RAINBOW;
            return true;
        case "BLINKING":
            mode = Pattern.BLINKING;
            return true;
        case "WHITE":
            mode = Pattern.WHITE;
            return true;
        case "BOOMER":
            mode = Pattern.BOOMER;
            return true;
        }
        return false;
    }
}