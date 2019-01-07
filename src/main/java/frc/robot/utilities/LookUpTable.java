package frc.robot.utilities;

/*
 * @author Shashvat
 * LookUp table for 
 * sine, cosine, arctangent
 *
 *internally works in degrees
 *input/output is in radians
 */
public class LookUpTable {

  private static double[] sineValues;

  private static double[] tangentValues;

  public static double radiansToDegrees(double radians) {
    return 180 * radians / Math.PI;
  }

  public static double degreesToRadians(double degrees) {
    return degrees * Math.PI / 180;
  }

  public static void init() {
    sineValues = new double[360];
    tangentValues = new double[181];
    for (int i = 0; i < 360; ++i) {
      double angle = degreesToRadians(i);
      sineValues[i] = Math.sin(angle);
    }
    tangentValues[0] = Double.NEGATIVE_INFINITY;
    for (int i = 1; i < 180; ++i) {
      double angle = degreesToRadians(i - 90);
      tangentValues[i] = Math.tan(angle);
    }
    tangentValues[180] = Double.POSITIVE_INFINITY;
  }

  /* input in radians */
  public static double getSin(double a) {
    int angle = (int) Math.round(radiansToDegrees(a));
    angle %= 360;
    if (angle < 0) {
      angle += 360;
    }
    return sineValues[angle];
  }

  public static double getCos(double a) {
    return getSin(Math.PI / 2 - a);
  }

  /* uses binary search plus look up table to find arcTan */
  public static double getArcTan(double a) {
    int low = 0;
    int high = 180;
    while (low < high) {
      int mid = (low + high) / 2;
      if (tangentValues[mid] == a) {
        // actually found value
        return degreesToRadians(mid - 90);
      }
      if (tangentValues[mid] < a) {
        low = mid + 1;
      } else if (tangentValues[mid] > a) {
        high = mid - 1;
      }
    }
    double degreesangle = (low + high) / 2 - 90;
    return degreesToRadians(degreesangle);
  }
}
