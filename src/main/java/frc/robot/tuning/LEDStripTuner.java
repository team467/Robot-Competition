package frc.robot.tuning;

import java.text.ParseException;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.other.LEDStrip;

public class LEDStripTuner implements Tuner{
    //the LEDStrip wrapper instance. This deals with interacting on a hardware level
    LEDStrip strip;
    //initalizes stuff
    LEDStripTuner(){
        strip = LEDStrip.getInstance();
    }
    //nothing to do in init since thats handeled in robotinit and the constructor
    public void init(){
    }
    //this only runs in test
    public void periodic(){
        //retrieves a string fron slot 0 on the DB. This is supposed to be i the form R G B
        //, with spaces inbetween
        String tuningValue = SmartDashboard.getString("DB/String 5", "0.0");
        //splits up string into tokens and cleans the array of blanks
        String[] tokens = tuningValue.split(" ");

        try{
            int red = Integer.parseInt(tokens[0]);
            int green = Integer.parseInt(tokens[1]);
            int blue = Integer.parseInt(tokens[2]);
            strip.setColor(red, green, blue);
        }catch(ArrayIndexOutOfBoundsException e){

        }catch(NumberFormatException e){

        }
    }
}