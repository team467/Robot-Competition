package frc.robot.gamepieces;

import frc.robot.gamepieces.Turret;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class VisionTurretTurn {

    Turret turret;
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    boolean hasAngle = false;
    NetworkTable table = inst.getTable("vision");
    NetworkTableEntry angle = table.getEntry("Angle");

    private double visionAngle;

    public void haveTurretAngle() {
        hasAngle = angle.getBoolean(false);
        if (hasAngle = false)
            ;

        if (hasAngle = true)
            ;

    }

    private VisionTurretTurn() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("vision");
        visionAngle = table.getEntry("angle").getDouble(-1000);
    }

    public void moveTurret() {

        if (visionAngle >= -137 && visionAngle <= 137)
            ;
        turret.targetPositon = visionAngle;

    }
}