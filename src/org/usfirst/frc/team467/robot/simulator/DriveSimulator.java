/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator;

import java.text.DecimalFormat;

import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

/**
 *
 */
public class DriveSimulator implements Drive {
	
	public static final double MAX_RPM = 821;
		
	private static Drive instance = null;
	
	private double maxFeetPerPeriod; // Period is 20 ms
	
	RobotData data = RobotData.getInstance();
	
	private DecimalFormat df = new DecimalFormat("####0.00");
	
	private double rightPositionReading;
	private double leftPositionReading;
	
	private double absoluteRightPositionReadingOffset;
	private double absoluteLeftPositionReadingOffset;
	
	private boolean isMoving = false;
	
	private DriveSimulator() {
		maxFeetPerPeriod = RobotMap.WHEELPOD_CIRCUMFERENCE / 12 * MAX_RPM / 60 / 500;
		zeroPosition();
		absoluteRightPositionReadingOffset = 0.0;
		absoluteLeftPositionReadingOffset = 0.0;
	}
	
	public static Drive getInstance() {
		if (instance == null) {
			instance = new DriveSimulator();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.simulator.Drive#zeroPosition()
	 */
	@Override
	public void zeroPosition() {
		absoluteRightPositionReadingOffset = rightPositionReading;
		absoluteLeftPositionReadingOffset = leftPositionReading;
		rightPositionReading = 0;
		leftPositionReading = 0;
		isMoving = false;
		data.zeroPosition();
	}
	
	public double rightPosition() {
		return absoluteRightPositionReadingOffset + rightPositionReading;
	}
	
	public double leftPosition() {
		return absoluteLeftPositionReadingOffset + leftPositionReading;
	}
	
	public void setMaxMotionMagicSpeed(double percentOfMaxSpeed) {
		if (percentOfMaxSpeed < 0) {
			percentOfMaxSpeed = 0;
		} else if (percentOfMaxSpeed > 1) {
			percentOfMaxSpeed = 1;
		}
		maxFeetPerPeriod = RobotMap.WHEELPOD_CIRCUMFERENCE / 12 * percentOfMaxSpeed * MAX_RPM / 60 / 1000;
	}
	
	
	@Override
	public void moveDistance(double distance) {
		moveDistance(distance, 0);
	}
		
	@Override
	public void moveDistance(double distance, double rotation) {

	    double leftDistance;
	    double rightDistance;
	    
	    double maxDistance = Math.copySign(Math.max(Math.abs(distance), Math.abs(rotation)), distance);;

	    if (distance >= 0.0) {
	      // First quadrant, else second quadrant
	      if (rotation >= 0.0) {
	    	  leftDistance = maxDistance;
	    	  rightDistance = distance - rotation;
	      } else {
	    	  leftDistance = distance + rotation;
	    	  rightDistance = maxDistance;
	      }
	    } else {
	      // Third quadrant, else fourth quadrant
	      if (rotation >= 0.0) {
	    	  leftDistance = distance + rotation;
	    	  rightDistance = maxDistance;
	      } else {
	    	  leftDistance = maxDistance;
	    	  rightDistance = distance - rotation;
	      }
	    }

		if (leftPositionReading == leftDistance && rightPositionReading == rightDistance) {
			isMoving = false;
			return; // At destination
		}
		
		isMoving = true;

		if (Math.abs((leftDistance - leftPositionReading)) > maxFeetPerPeriod) {
			if (leftDistance < 0) {
				leftPositionReading -= maxFeetPerPeriod;
			} else {
				leftPositionReading += maxFeetPerPeriod;
			}
		} else {
			leftPositionReading = leftDistance;
		}
		
		if (Math.abs((rightDistance - rightPositionReading)) > maxFeetPerPeriod) {
			if (rightDistance < 0) {
				rightPositionReading -= maxFeetPerPeriod;
			} else {
				rightPositionReading += maxFeetPerPeriod;
			}
		} else {
			rightPositionReading = rightDistance;
		}
		
		System.out.println("Left Target: " + df.format(leftDistance) + " Right Target: " + df.format(rightDistance));
		System.out.println("Left Move: " + df.format(leftPositionReading) 
			+ " Right Move: " + df.format(rightPositionReading));
		
		data.update(rightPosition(), leftPosition());
		
	}
	
	@Override
	public void arcadeDrive(double speed, double rotation, boolean squaredInputs) {
		// TODO Auto-generated method stub
		
		speed = limit(speed);
		rotation = limit(rotation);
		
		if (squaredInputs) {
			Math.copySign(speed * speed, speed);
			Math.copySign(rotation * rotation, rotation);
		}
	    double leftMotorOutput;
	    double rightMotorOutput;
	    
	    double maxInput = Math.copySign(Math.max(Math.abs(speed), Math.abs(rotation)), speed);

	    if (speed >= 0.0) {
	      // First quadrant, else second quadrant
	      if (rotation >= 0.0) {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = speed - rotation;
	      } else {
	        leftMotorOutput = speed + rotation;
	        rightMotorOutput = maxInput;
	      }
	    } else {
	      // Third quadrant, else fourth quadrant
	      if (rotation >= 0.0) {
	        leftMotorOutput = speed + rotation;
	        rightMotorOutput = maxInput;
	      } else {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = speed - rotation;
	      }
	    }
	    
	    simulateMove(leftMotorOutput, rightMotorOutput);
	 }
	
	@Override
	public void tankDrive(double leftMotorOutput, double rightMotorOutput) {
		tankDrive(leftMotorOutput, rightMotorOutput, true);		
	}

	@Override
	public void tankDrive(double leftMotorOutput, double rightMotorOutput, boolean squaredInputs) {
		if (squaredInputs) {
			Math.copySign(leftMotorOutput * leftMotorOutput, leftMotorOutput);
			Math.copySign(rightMotorOutput * rightMotorOutput, rightMotorOutput);
		}
		simulateMove(leftMotorOutput, rightMotorOutput);
	}

	private void simulateMove(double leftMotorOutput, double rightMotorOutput) {
		if (leftMotorOutput == 0 && rightMotorOutput == 0) {
			isMoving = false;
		}
		isMoving = true;
		leftPositionReading += leftMotorOutput * maxFeetPerPeriod;
		rightPositionReading += rightMotorOutput * maxFeetPerPeriod;
		data.update(rightPosition(), leftPosition());		
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return !isMoving;
	}

	@Override
	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		double absoluteLeftDistance =  Math.abs(leftPositionReading);
		double absoluteRightDistance = Math.abs(rightPositionReading);
		if (absoluteLeftDistance < absoluteRightDistance) {
			return absoluteRightDistance;
		} else {
			return absoluteLeftDistance;
		}
	}
	
	  /**
	   * Limit motor values to the -1.0 to +1.0 range.
	   */
	  private double limit(double value) {
	    if (value > 1.0) {
	      return 1.0;
	    }
	    if (value < -1.0) {
	      return -1.0;
	    }
	    return value;
	  }



	public static void main(String[] args) {		
		Drive drive = DriveSimulator.getInstance();
		drive.zeroPosition();
		RobotData.getInstance().startPosition(20, 1.5);
		
		double left = -1 * Math.toRadians(100);
		double right =     Math.toRadians(100);
		
		do {
			drive.moveDistance(0,90);
		} while (!drive.isStopped());	
		
//		while (drive.moveDistance(left, right) != true);
		
//		drive.moveDistance(0, 0); // Stationary
//		drive.moveDistance(10, 10); // Straight forward
//		drive.moveDistance(-3.14, 3.14); // 180 degrees
//		drive.moveDistance(5.7, 35.7);
//		drive.moveDistance(15.7, -15.7);
//		drive.moveDistance(31.4, -31.4);
//		drive.moveDistance(131.4, 68.6);
	}

}
