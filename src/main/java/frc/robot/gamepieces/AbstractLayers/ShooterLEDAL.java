package frc.robot.gamepieces.AbstractLayers;

import frc.robot.gamepieces.AbstractLayers.ShooterAL;
import frc.robot.logging.RobotLogManager;
import frc.robot.RobotMap;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class ShooterLEDAL {
    private static ShooterLEDAL instance = null;

    private static final Logger LOGGER = RobotLogManager.getMainLogger(ShooterAL.class.getName());

    private static AddressableLED leds;
    private static AddressableLEDBuffer ledBuffer;

    private static HashMap<String, Integer> ledRange = new HashMap<>();
    private static int totalLeds = 0;

    public static ShooterLEDAL getInstance() {
        if (instance == null) {
          if (RobotMap.HAS_SHOOTER_LEDS) {
            leds = new AddressableLED(RobotMap.SHOOTER_LED_CHANNEL);

            ledRange.put("Shooter LEDs", RobotMap.SHOOTER_LED_AMOUNT);
            
            if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
                ledRange.put("Shooter Double LEDs", RobotMap.SHOOTER_LED_AMOUNT);
            }

            ledRange.forEach((key, num) -> {
                totalLeds = totalLeds + num;
            });

            ledBuffer = new AddressableLEDBuffer(totalLeds);
            leds.setLength(ledBuffer.getLength());
    
            for (var i = 0; i < ledBuffer.getLength(); i++) {
              ledBuffer.setRGB(i, 0, 0, 0);
            }
    
            leds.setData(ledBuffer);
            leds.start();
          } else {
            leds = null;
            ledBuffer = null;
          }
    
          instance = new ShooterLEDAL();
        }
        return instance;
      }

    
    public ShooterLEDAL() {
        
    }

    public void setLedStrip(int r, int g, int b, int startingLed, int endingLed) {
        if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTER_LEDS) {
          for (var i = Math.max(0, startingLed); i <= Math.min(ledBuffer.getLength()-1, endingLed); i++) {
            LOGGER.warn("Setting led {} to color R{} G{} B{}", i, r, g, b);
            ledBuffer.setRGB(i, r, g, b);
         }
         leds.setData(ledBuffer);
        }
      }
    
      public void fillStrip(int r, int g, int b, int led) {
        if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTER_LEDS) {
          int setLed = Math.min(RobotMap.SHOOTER_LED_AMOUNT-1, led);
          setLedStrip(r, g, b, 0, setLed);
          if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
            
            doubleStrip(r, g, b, 0, setLed);
          }
          if (setLed < RobotMap.SHOOTER_LED_AMOUNT-1) {
            setLedStrip(0, 0, 0, setLed + 1, RobotMap.SHOOTER_LED_AMOUNT-1);
            if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
              doubleStrip(0, 0, 0, setLed + 1, RobotMap.SHOOTER_LED_AMOUNT-1);
            }
          }
        }
      }

      private void doubleStrip(int r, int g, int b, int startingLed, int endingLed) {
        if (ledBuffer != null && leds != null && RobotMap.HAS_SHOOTER_LEDS) {
            if (RobotMap.SHOOTER_DOUBLESIDE_LED) {
                int maxLed = RobotMap.SHOOTER_LED_AMOUNT*2 - 1;
                int sl = maxLed - endingLed;
                int el = maxLed - startingLed;

                setLedStrip(r, g, b, sl, el);
            }
        }
      }
    
      public void fillStrip(Color color, int led) {
        int r = (int) color.red * 255; 
        int g = (int) color.green * 255; 
        int b = (int) color.blue * 255; 
    
        fillStrip(r, g, b, led);
      }
    
      public void clearStrip() {
        setLedStrip(0, 0, 0, 0, ledBuffer.getLength()-1);
      }
}