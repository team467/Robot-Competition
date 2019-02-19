package frc.robot.sensors;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class LedI2C {
  private static I2C wire = new I2C(Port.kOnboard, 8);
  private static final int maxSize = 32;

  public void writeBulk(byte[] message, int length) {
    wire.writeBulk(message,length);
  }

    // Color of LEDs
  public static enum LEDColor {
    NONE(0), BLUE(1), GOLD(2), RED(3);

    private final int color;
    private LEDColor(int color) {
      this.color = color;
    }
    public int getColor() {
      return color;
    }
  }

  // Blinking of LEDs
  public static enum LEDBlink {
    NONE(0), SHOOTING(6), ALL(255);
        
    private final int blink;
    private LEDBlink(int blink) {
      this.blink = blink;
    }
    public int getBlink() {
      return blink;
    }
}

  // Enum for different LED modes
  public static enum LEDMode {
  NONE(0);
            
  private final int modes;
  private LEDMode(int modes) {
    this.modes = modes;
  }
  public int getMode() {
    return modes;
  }
}

public void sendLEDCmd(LEDMode ledMode, LEDColor ledColor, LEDBlink ledBlink) {
    int ledModeValue = ledMode.getMode();
    int ledColorValue = ledColor.getColor();
    int ledBlinkValue = ledBlink.getBlink();
    byte[] message = new byte[10];
    message[0] = (byte)ledModeValue;
    message[1] = (byte)ledColorValue;
    message[2] = (byte)ledBlinkValue;
    wire.writeBulk(message,3);
}

public void cargoInLine() {
  LEDColor ledColor;
  LEDBlink ledBlink;
  LEDMode ledMode;
  ledColor = LEDColor.BLUE;
  ledBlink = LEDBlink.ALL;
  ledMode = LEDMode.NONE;
  sendLEDCmd(ledMode,ledColor,ledBlink);
}

public void defensiveMode() { 
  LEDColor ledColor;
  LEDBlink ledBlink;
  LEDMode ledMode;
  ledColor = LEDColor.RED;
  ledBlink = LEDBlink.ALL;
  ledMode = LEDMode.NONE;
}

public void shootingMode() {
  LEDColor ledColor;
  LEDBlink ledBlink;
  LEDMode ledMode;
  ledColor = LEDColor.BLUE;
  ledBlink = LEDBlink.SHOOTING;
  ledMode = LEDMode.NONE;
}}
