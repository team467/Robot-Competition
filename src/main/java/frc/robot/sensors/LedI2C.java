package frc.robot.sensors;

import java.util.Arrays;


import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import frc.robot.logging.RobotLogManager;
public class LedI2C {
  byte[] previousMessage;
  private static final Logger LOGGER = RobotLogManager.getMainLogger(LedI2C.class.getName());

  private static I2C wire = new I2C(Port.kOnboard, 8);

  public void writeBulk(byte[]message, int length) {
    wire.writeBulk(message,length);
  }

    // Color of LEDs
  public static enum LEDColor {
    NONE(0), BLUE(1), GOLD(2), RED(3), ORANGE(4);

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
   // NONE(0), SHOOTING(6), ALL(255);
     NONE(0);   
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
  NONE(0), SOLID(1), SHOOTING(2), ALLBLINK(3), BLUEANDGOLD(4);
            
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
    message[0] = (byte) 0x55;
    message[1] = (byte)0xaa;
    message[2] = (byte)ledModeValue;
    message[3] = (byte)ledColorValue;
    message[4] = (byte)ledBlinkValue;
    if(Arrays.equals(message, previousMessage)) {
      LOGGER.error("I am the same");
    }else{
    wire.writeBulk(message,5);
    previousMessage = message;
    }
}

public void cargoInLine() {
  sendLEDCmd(LEDMode.ALLBLINK,LEDColor.BLUE,LEDBlink.NONE);
}

public void defensiveMode() { 
  sendLEDCmd(LEDMode.ALLBLINK, LEDColor.RED, LEDBlink.NONE);
}

public void shootingMode() {
  sendLEDCmd(LEDMode.SHOOTING, LEDColor.NONE, LEDBlink.NONE);
}
public void whenDisabled(){
  LOGGER.error("whenDisabled Mode={} Color={} Blink={}", 
  LEDMode.NONE, LEDColor.ORANGE, LEDBlink.NONE);
  sendLEDCmd(LEDMode.NONE, LEDColor.ORANGE, LEDBlink.NONE);
}
}
