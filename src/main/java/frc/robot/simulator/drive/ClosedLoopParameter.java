package frc.robot.simulator.drive;

import frc.robot.utilities.PreferencesStorage;

class ClosedLoopParameter {

  private String prefix;

  // private PreferencesStorage store = PreferencesStorage.getInstance();

  ClosedLoopParameter(String prefix) {
    this.prefix = prefix;
    // TODO: Figure out storage once WPILib native libraries are working in build.
    // proportionalGain = store.getDouble(prefix + "proportionalGain", proportionalGain);
    // integral = store.getDouble(prefix + "integral", integral);
    // derivative = store.getDouble(prefix + "derivative", derivative);
    // feedForward = store.getDouble(prefix + "feedForward", feedForward);
    // allowableClosedLoopError 
    //     = store.getInt(prefix + "allowableClosedLoopError", allowableClosedLoopError);
    // loopTimeMs = store.getInt(prefix + "loopTimeMs", loopTimeMs);
    // peakPercentOut = store.getDouble(prefix + "-peakPercentOut", peakPercentOut);
    // integralAccumulator = store.getDouble(prefix + "-integralAccumulator", integralAccumulator);
    // integralZone = store.getInt(prefix + "-integralZone", integralZone);
  }
 
  private double proportionalGain = 1.0;

  void proportionalGain(double proportionalGain) {
    this.proportionalGain = proportionalGain;
    // store.putDouble(prefix + proportionalGain, proportionalGain);
  }

  double proportionalGain() {
    return proportionalGain;
  }

  private double integral = 0.0;

  void integral(double integral) {
    this.integral = integral;
    // store.putDouble(prefix + integral, integral);
  }

  double integral() {
    return integral;
  }

  private double derivative = 0.0;

  void derivative(double derivative) {
    this.derivative = derivative;
    // store.putDouble(prefix + derivative, derivative);
  }

  double derivative() {
    return derivative;
  }

  private double feedForward = 0.0;

  void feedForward(double feedForward) {
    this.feedForward = feedForward;
    // store.putDouble(prefix + feedForward, feedForward);
  }

  double feedForward() {
    return feedForward;
  }

  private int allowableClosedLoopError = 0;

  void allowableClosedLoopError(int allowableClosedLoopError) {
    this.allowableClosedLoopError = allowableClosedLoopError;
    // store.putDouble(prefix + allowableClosedLoopError, allowableClosedLoopError);
  }

  int allowableClosedLoopError() {
    return allowableClosedLoopError;
  }

  private int loopTimeMs = 5;

  void loopTimeMs(int loopTimeMs) {
    this.loopTimeMs = loopTimeMs;
    // store.putDouble(prefix + loopTimeMs, loopTimeMs);
  }

  int loopTimeMs() {
    return loopTimeMs;
  }

  private double peakPercentOut = 1.0;

  void peakPercentOut(double peakPercentOut) {
    this.peakPercentOut = peakPercentOut;
    // store.putDouble(prefix + peakPercentOut, peakPercentOut);
  }

  double peakPercentOut() {
    return peakPercentOut;
  }

  private double integralAccumulator = 0.0;

  void integralAccumulator(double integralAccumulator) {
    this.integralAccumulator = integralAccumulator;
    // store.putDouble(prefix + integralAccumulator, integralAccumulator);
  }

  double integralAccumulator() {
    return integralAccumulator;
  }

  private int integralZone = 0;

  void integralZone(int integralZone) {
    this.integralZone = integralZone;
    // store.putDouble(prefix + integralZone, integralZone);
  }

  int integralZone() {
    return integralZone;
  }

}