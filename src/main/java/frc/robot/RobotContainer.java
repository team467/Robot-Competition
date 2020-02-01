package frc.robot;

import java.util.List;


import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;

import frc.robot.drive.DriveSubsystem;



/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem robotDrive = new DriveSubsystem();

  // The driver's controller
 

  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
  }



  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {

    // Create a voltage constraint to ensure we don't accelerate too fast
    var autoVoltageConstraint =
        new DifferentialDriveVoltageConstraint(
            new SimpleMotorFeedforward(RobotMap.AUTO_STATIC_VOLTAGE_GAIN,
                                        RobotMap.AUTO_VELOCITY_VOLTAGE_GAIN,
                                        RobotMap.AUTO_ACCELERATION_VOLTAGE_GAIN),
            RobotMap.kDriveKinematics,
            10);

    // Create config for trajectory
    TrajectoryConfig config =
        new TrajectoryConfig(RobotMap.AUTO_MAX_VELOCITY_METERS,
                             RobotMap.AUTO_MAX_ACCELERATION_METERS)
            // Add kinematics to ensure max speed is actually obeyed
            .setKinematics(RobotMap.kDriveKinematics)
            // Apply the voltage constraint
            .addConstraint(autoVoltageConstraint);

    // An example trajectory to follow.  All units in meters.
    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
        // Start at the origin facing the +X direction
        new Pose2d(0, 0, new Rotation2d(0)),
        // Pass through these two interior waypoints, making an 's' curve path
        List.of(
            new Translation2d(1, 1),
            new Translation2d(2, -1)
        ),
        // End 3 meters straight ahead of where we started, facing forward
        new Pose2d(3, 0, new Rotation2d(0)),
        // Pass config
        config
    );

    RamseteCommand ramseteCommand = new RamseteCommand(
        exampleTrajectory,
        robotDrive::getPose,
        new RamseteController(RobotMap.RAMSETE_B, RobotMap.RAMSETE_ZETA),
        new SimpleMotorFeedforward(RobotMap.AUTO_STATIC_VOLTAGE_GAIN,
                                   RobotMap.AUTO_VELOCITY_VOLTAGE_GAIN,
                                   RobotMap.AUTO_ACCELERATION_VOLTAGE_GAIN),
        RobotMap.kDriveKinematics,
        robotDrive::getWheelSpeeds,
        new PIDController(RobotMap.LEFT_TURN_PID_P, 0, 0),
        new PIDController(RobotMap.RIGHT_DRIVE_PID_P, 0, 0),
        // RamseteCommand passes volts to the callback
        robotDrive::tankDriveVolts,
        robotDrive
    );

    // Run path following command, then stop at the end.
    return ramseteCommand.andThen(() -> robotDrive.tankDriveVolts(0, 0));
  }
}