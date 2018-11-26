package frc.robot;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
    "frc.robot.drive.motorcontrol.pathtracking",
    "frc.robot.simulator.drive"
})
public class RobotTests {}