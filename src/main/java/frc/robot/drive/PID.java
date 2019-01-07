package frc.robot.drive;

public class PID {
  /**
   * Proportional control term. It is the base term for getting to the target point.
   */
  public final double p;

  /**
   * Integral control term, it gets to the target point faster.
   */
  public final double i;

  /**
   * Derivative control term. It reduces thrash.
   */
  public final double d;

  /**
   * The feed forward term controls for static resistance such as friction.
   */
  public final double f;

  /*
   * The values may only be set in the constructor as they are final. 
   * Default feed forward (f) value is 0.0.
   */
  public PID(double p, double i, double d) {
    this.p = p;
    this.i = i;
    this.d = d;
    this.f = 0.0;
  }

  /*
   * The values may only be set in the constructor as they are final.
   */
  public PID(double p, double i, double d, double f) {
    this.p = p;
    this.i = i;
    this.d = d;
    this.f = f;
  }

}