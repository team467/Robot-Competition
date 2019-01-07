package frc.robot.drive;

/**
 * A collection of methods that a drive class must implement to run our autonomous modes.
 */
public interface AutoDrive {

  void moveLinearFeet(double distance);

  void rotateByAngle(double rotationInDegrees);

  /**
   * This is used for testing the new controllers. It cannot use both the straight
   * PIDs and the turn PIDs, so the straight PIDs are used.
   * 
   * @param distanceInFeet the distance to move forward
   * @param degrees the turn distance in degrees, with counter clockwise hand turns as positive
   */
  public void moveWithTurn(double distanceInFeet, double degrees);

  /**
   * Move each side independently. Distances must be equal or opposite.
   */
  public void moveFeet(double leftDistance, double rightDistance);

  boolean isStopped();

  /**
   * Gets the distance moved for checking drive modes.
   *
   * @return the absolute distance moved in feet
   */
  public double absoluteDistanceMoved();

  /**
   * Resets the current sensor position to zero.
   */
  public void zero();

}