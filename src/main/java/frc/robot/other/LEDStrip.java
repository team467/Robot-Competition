package frc.robot.other;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.RobotMap;

public class LEDStrip {
    private static LEDStrip instance;
    private AddressableLED LEDStrip;
    private AddressableLEDBuffer LEDBuffer;
    private int red = 128;
    private int green = 128;
    private int blue = 128;
    // the time so that the led can update and the lights can blink or move or stuff
    private int time = 0;
    private Color mode = Color.WHITE;

    public static enum Color {
        CUSTOM, RED, GREEN, BLUE, RAINBOW, BLINKING, WHITE
    }

    public static LEDStrip getInstance() {
        if (instance == null) {
            instance = new LEDStrip();
        }
        return instance;
    }

    private LEDStrip() {
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
        // writes to LEDs
        for (int i = 0; i < LEDBuffer.getLength(); i++) {
            LEDBuffer.setRGB(i, red, green, blue);
        }
        // copies stuff to LEDS
        LEDStrip.setData(LEDBuffer);
        time++;
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}