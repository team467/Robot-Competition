package frc.robot.vision;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class VisionProcessing {

  public Mat source;
  

  public double cubeCenterPointY;
  public double cubeCenterPointX;
  public double distanceToCube;
  public double cubeWidth;

  private DetectPowerCubePipeline pipeline;

  private static final Logger LOGGER 
      = RobotLogManager.getMainLogger(VisionProcessing.class.getName());

  private static VisionProcessing instance;

  private double windowWidth;
  private double cameraAngleToCube;

  public static final double CUBE_WIDTH = 13.0;
  public static final double CUBE_HEIGHT = 11.0;
  //focal length of camera is 634

  private double average;
  private MovingAverage averageAngle;

  private VisionProcessing() {
    pipeline = new DetectPowerCubePipeline();
    averageAngle = new MovingAverage(25);

  }

  public static VisionProcessing getInstance() {
    if (instance == null) {
      instance = new VisionProcessing();
    }
    return instance;
  }
  
  public void startVision() {
    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
      camera.setResolution(160, 120);
      camera.setFPS(30);
      camera.setExposureManual(40);
      
      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo("CubeCam", 160, 120);
      
      Mat source = new Mat();
      while (!Thread.interrupted()) {
        cvSink.grabFrame(source);
        this.findCube(source);
        outputStream.putFrame(source);
      }
    }).start();
  }
  
  /**
   * process and places a bounding box around the cube to find the cube.
   * 
   * @param source Mat to process
   * @return returns an angle to the cube
   */

  public double findCube(Mat source) {
    double cameraDistanceX;

    cameraAngleToCube = Double.NaN;
    source = this.source;
    if (!source.empty()) {
      pipeline.process(source);
      windowWidth = source.size().width;
      ArrayList<Rect> boundingBoxes = new ArrayList<Rect>();
      for (MatOfPoint points : pipeline.convexHullsOutput()) {
        Rect box = Imgproc.boundingRect(points);
        boundingBoxes.add(box);
        // LOGGER.info("ADDED Bounding BOX X: {} Y: {} H: {} W: {}", box.x, box.y,
        // box.height, box.width);
        cubeCenterPointY = (box.height / 2) + box.y;
        cubeCenterPointX = (box.width / 2) + box.x;
        Imgproc.rectangle(source, new Point(box.x, box.y), 
            new Point(box.x + box.width, box.y + box.height),
            new Scalar(0, 255, 0, 0), 5);

        cameraDistanceX = cubeCenterPointX - (windowWidth / 2);
        cameraAngleToCube = 0.00294524375 * cameraDistanceX;

        if (!Double.isNaN(cameraAngleToCube)) {
          average = averageAngle.average(cameraAngleToCube);
        }

        break;
      }
    }
    return Math.toDegrees(average);
  }

  /**
   * This method is a generated getter for the output of a Find_Contours.
   */
  public void findContoursOutput() {
    LOGGER.debug("COUNT: {}", pipeline.filterContoursOutput().size());
    for (MatOfPoint points : pipeline.convexHullsOutput()) {
      LOGGER.debug("Test {}", points);
    }
  }

  public double avgAngle() {
    return Math.toDegrees(average);
  }

  public double cameraAngle() {
    return Math.toDegrees(cameraAngleToCube);
  }

  public boolean canSeeCube() {
    double angleChkrNum;
    angleChkrNum = cameraAngle();
    
    if (Double.isNaN(angleChkrNum)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean onTarget() {
    double currentDegFromCube;
  
    currentDegFromCube = avgAngle();
    if (currentDegFromCube <= RobotMap.ALLOWED_GRABBER_ERROR 
        && currentDegFromCube >= -1 * RobotMap.ALLOWED_GRABBER_ERROR) {
      return true;
    } else {
      return false;
    }
  }

}