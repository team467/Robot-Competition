package frc.robot.tuning;

import frc.robot.RobotMap;
import frc.robot.drive.Drive;
import frc.robot.logging.RobotLogManager;
import frc.robot.sensors.Gyrometer;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SplineAlgorithem {
    public SplineAlgorithem() {


    }

    public double calculateAngleRate(double time, boolean radians) {
        //Temp
        double Numerator, Denominator; 

        Numerator = 0.012 * (Math.pow(time,2)) - 0.21 * time + 0.6;
        Denominator = 1 + Math.pow((0.004 * (Math.pow(time, 3)) - 0.105 *(Math.pow(time, 2)) + 0.6 * time),2);

        if(radians){
            return Numerator / Denominator;
        } else {
            return (180 / Math.PI) * (Numerator / Denominator);
        }

    }

    public double calculateSpeed(double time) {

        return 0.004 * (Math.pow(time, 3)) - 0.105 * (Math.pow(time, 2)) + 0.6 * time;
    }





}