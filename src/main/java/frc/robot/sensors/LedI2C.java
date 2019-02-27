package frc.robot.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

import frc.robot.logging.RobotLogManager;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

public class LedI2C {

  private static LedI2C instance = null;

  /**
   * Returns a singleton instance of the LED communication controller.
   * 
   * @return LedI2C the instance
   */
  public static LedI2C getInstance() {
    if (instance == null) {
      instance = new LedI2C();
    }

    return instance;
  }

  private LedI2C() {

  }


  byte[] previousMessage;
  private static final Logger LOGGER = RobotLogManager.getMainLogger(LedI2C.class.getName());

  private static I2C wire = new I2C(Port.kOnboard, 8);

  public void writeBulk(byte[]message, int length) {
    wire.writeBulk(message,length);
  }

  // Color of LEDs
  public static enum LedColor {
    NONE(0), BLUE(1), GOLD(2), RED(3), ORANGE(4);

    private final int color;
    private LedColor(int color) {
      this.color = color;
    }

    public int getColor() {
      return color;
    }
  }

  // Blinking of LEDs
  public static enum LedBlink {

    // NONE(0), SHOOTING(6), ALL(255);
    NONE(0);   
    
    private final int blink;
    
    private LedBlink(int blink) {
      this.blink = blink;
    }
    
    public int getBlink() {
      return blink;
    }
  }

  // Enum for different LED modes
  public static enum LedMode {
    NONE(0), SOLID(1), SHOOTING(2), ALLBLINK(3), BLUEANDGOLD(4);
              
    private final int modes;

    private LedMode(int modes) {
      this.modes = modes;
    }

    public int getMode() {
      return modes;
    }
  }

  public void sendLedCommand(LedMode ledMode, LedColor ledColor, LedBlink ledBlink) {
    byte[] message = new byte[10];
    message[0] = (byte) 0x55;
    message[1] = (byte)0xaa;

    int ledModeValue = ledMode.getMode();
    message[2] = (byte)ledModeValue;

    int ledColorValue = ledColor.getColor();
    message[3] = (byte)ledColorValue;

    int ledBlinkValue = ledBlink.getBlink();
    message[4] = (byte)ledBlinkValue;

    if (Arrays.equals(message, previousMessage)) {
      LOGGER.debug("Message is the same");
    } else {
      wire.writeBulk(message,5);
      previousMessage = message;
    }
  }

  public void cargoMode() {
    sendLedCommand(LedMode.ALLBLINK,LedColor.BLUE,LedBlink.NONE);
  }

  public void hatchMode() {
    sendLedCommand(LedMode.ALLBLINK,LedColor.GOLD,LedBlink.NONE);
  }

  public void defensiveMode() { 
    sendLedCommand(LedMode.ALLBLINK, LedColor.RED, LedBlink.NONE);
  }

  public void shootingMode() {
    sendLedCommand(LedMode.SHOOTING, LedColor.NONE, LedBlink.NONE);
  }

  public void whenDisabled(){
    LOGGER.debug("whenDisabled Mode={} Color={} Blink={}", 
        LedMode.NONE, LedColor.ORANGE, LedBlink.NONE);
    sendLedCommand(LedMode.NONE, LedColor.ORANGE, LedBlink.NONE);
  }
  
}
