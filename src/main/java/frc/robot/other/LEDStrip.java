package frc.robot.other;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
public class LEDStrip{
    private static LEDStrip instance;
    private AddressableLED LEDStrip;
    private AddressableLEDBuffer LEDBuffer;
    int r = 128;
    int g = 128;
    int b = 128;
    public static LEDStrip getInstance(){
        if(instance == null){
            instance = new LEDStrip();
        }
        return instance;
    }
    private LEDStrip(){
        //creating LED Strip
        //leds on PWM 9
        LEDStrip = new AddressableLED(9);
        //led buffer with 12 leds: the length of the neopixel ring
        LEDBuffer = new AddressableLEDBuffer(12);
        LEDStrip.setLength(LEDBuffer.getLength());
        //sets data
        LEDStrip.setData(LEDBuffer);
        LEDStrip.start();
    }
    public void update(){
    //--LED stuff--
    
    //writes to LEDs
    for(int i = 0; i < LEDBuffer.getLength(); i++) {
        LEDBuffer.setRGB(i, r, g, b);
      }
      //copies stuff to LEDS
      LEDStrip.setData(LEDBuffer);
    }
    public void setColor(int red, int green, int blue){
        r = red;
        g = green;
        b = blue;
    }
}