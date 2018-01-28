package vision;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


public class VisionProcessing {
	private DetectPowerCubePipeline pipeline;
	private static final Logger LOGGER = Logger.getLogger(VisionProcessing.class);
	private static VisionProcessing instance;
	private VideoCapture camera;
	
	public static void main (String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VisionProcessing vision = VisionProcessing.getInstance();
		vision.findCube();
	}
	
	private VisionProcessing() {
		 pipeline = new DetectPowerCubePipeline();
		 camera = new VideoCapture();
		 camera.open(1);
		 
	}
	
	public Mat grabFrame() {
		Mat frame = new Mat();
		camera.read(frame);
		pipeline.process(frame);
		return frame;
	}
	
	public static VisionProcessing getInstance() {
		if (instance == null) {
			instance = new VisionProcessing();
		}
		return instance;
	}
	
	public void findCube() {
		grabFrame();
		ArrayList<Rect> boundingBoxes = new ArrayList<Rect>();
		for (MatOfPoint point : pipeline.convexHullsOutput()) {
			Rect box = Imgproc.boundingRect(point);
			boundingBoxes.add(box);
			LOGGER.info(box.x + box.y + box.width + box.height);
		}
	}

	@Override
	public String toString() {
		return "";
	}
}
