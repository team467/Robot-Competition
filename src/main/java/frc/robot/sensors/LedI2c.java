// I2C connection between roborio and arduino
package frc.robot.sensors;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
public class LedI2c {
    private static I2C wire = new I2C(Port.kOnboard, 8);
    // private static final int Max_size = 32;
    public void writeBulk(byte[]message, int length){
        //byte[] writeData = new byte[hello];
        wire.writeBulk(message,length);
    }
    // color of LEDS
    public static enum LEDColor{
        NONE(0), BLUE(1), GOLD(2), RED(3);

        private final int color;
        private LEDColor(int color){
            this.color = color;
        }
        public int getColor(){
            return color;
        }
    }
        //blinkiness of LEDS
        public static enum LEDBlink{
            //NONE(0),ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5),SHOOTING(6), ALL(255);
            NONE(0), SHOOTING(6), ALL(255);
        
            private final int blinky;
            private LEDBlink(int blinky){
                this.blinky = blinky;
            }
            public int getBlink(){
                return blinky;
            }
        }
        // enum for different LED modes
        public static enum LEDMode{
            NONE(0);
            
            private final int modes;
            private LEDMode( int modes){
                this.modes = modes;
            }
            public int getMode(){
                return modes;
            }
        }
    public void LEDSendCmd(LEDMode led_mode, LEDColor led_color, LEDBlink led_blink){
        int i_led_mode = led_mode.getMode();
        int i_led_color = led_color.getColor();
        int i_led_blink = led_blink.getBlink();
         byte[] message = new byte[10];
        message[0] = (byte)i_led_mode;
        message[1] = (byte)i_led_color;
        message[2] = (byte)i_led_blink;
        wire.writeBulk(message,3);
    }
      
    
    public void CargoInLine(Boolean placeholder){//boolean is a placeholder, no actual data as of yet
        LEDColor led_color;
        LEDBlink led_blink;
        LEDMode led_mode;
        led_color = LEDColor.BLUE;
        led_blink = LEDBlink.ALL;
        led_mode = LEDMode.NONE;
        LEDSendCmd(led_mode,led_color,led_blink);
    }
    public void DefensiveMode(Boolean placeholder){
        LEDColor led_color;
        LEDBlink led_blink;
        LEDMode led_mode;
        led_color = LEDColor.RED;
        led_blink = LEDBlink.ALL;
        led_mode = LEDMode.NONE;
    }
    public void ShootingMode(Boolean placeholder){
        LEDColor led_color;
        LEDBlink led_blink;
        LEDMode led_mode;
        led_color = LEDColor.BLUE;
        led_blink = LEDBlink.SHOOTING;
        led_mode = LEDMode.NONE;
    }
}