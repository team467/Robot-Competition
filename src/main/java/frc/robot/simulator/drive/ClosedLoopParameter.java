package frc.robot.simulator.drive;

class ClosedLoopParameter {

  double propGain = 1.0;

  double integral = 0.0;

  double derivative = 0.0;

  double feedForward = 0.0;

  int allowableClosedLoopError = 0;

  int loopTimeMs = 5;

  double peakPercentOut = 1.0;

  double integralAccumulator = 0.0;

  int integralZone = 0;
 
}