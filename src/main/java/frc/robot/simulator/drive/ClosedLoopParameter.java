package frc.robot.simulator.drive;

class ClosedLoopParameter {

  double p = 1.0;

  double i = 0.0;

  double d = 0.0;

  double f = 0.0;

  int allowableClosedLoopError = 0;

  int loopTimeMs = 5;

  double peakPercentOut = 1.0;

  double integralAccumulator = 0.0;

  int iZone = 0;

}