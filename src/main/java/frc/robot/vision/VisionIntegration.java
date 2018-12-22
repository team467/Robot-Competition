package frc.robot.vision;

import frc.robot.drive.Drive;
import frc.robot.gamepieces.Grabber;

public class VisionIntegration {
  private static VisionIntegration instance;
  private Drive drive = Drive.getInstance();
  private VisionProcessing vision = VisionProcessing.getInstance();
  private Grabber grabber = Grabber.getInstance();
  //private static final Logger LOGGER = RobotLogManager.getMainLogger(VisionIntegration.class);

  public static VisionIntegration getInstance() {
    if (instance == null) {
      instance = new VisionIntegration();
    }
    return instance;
  }
  
  public void periodic() {
  // driverstation.
  }

  public void target() {
    if (!grabber.hasCube()) {
      if (vision.canSeeCube()) {
        if (vision.onTarget()) {
          // Start Grabber & drive forward
          return;
        }
      }
    } else {
      drive.stop();
      grabber.grab(0);
    }
  }
}

/*
 * TODO 
 * 1. Vision class gets the cube angle 
 * 2. Integration says whether or not
 * the robot can get the cube 
 * 3. Autonomous action of driving to the correct
 * angle of the cube so that the camera angle returns zero or the cube is within
 * suck angle 
 * 4. then drive forward and grab the cube. 
 * 5. Optical sensor checks
 * to see if cube is in the grabber the whole time
 * 
 * 1. can vision see cube? done 
 * 2. if yes, what is the angle to the cube? done
 * 3. turn to the angle? done 
 * 4. on target?done
 * 5a. activate grabber 
 * 5b. drive into cube 
 * 6. have cube 
 * 7. stop driving 
 * 8. thats it?
 * 
 * 
 *
 * exception cases
 * 
 * 1. lost cube? - stop, VisionIntegration retarget
 * 
 */
