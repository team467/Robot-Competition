package frc.robot.drive.motorcontrol.pathplanning;

public class SplineCourseData {
  public double coordinateX;
  public double coordinateY;
  public double heading;
  public double curvature;
  public double step;

  /**
   * Converts the data a an array of individual point data into vectors for the given point types.
   * 
   * @param course the array of course data
   * @param variable the variable to convert to a vector
   * @return the vector for the desired variable
   */
  public static double[] vectorize(SplineCourseData[] course, String variable) {
    double[] vector = new double[course.length];
    variable = variable.toLowerCase();
    for (int i = 0; i < course.length; i++) {
      switch (variable) {

        case "x":
          vector[i] = course[i].coordinateX;
          break;
          
        case "y":
          vector[i] = course[i].coordinateY;
          break;

        case "heading":
          vector[i] = course[i].heading;
          break;

        case "curvature":
          vector[i] = course[i].curvature;
          break;

        case "step":
        default:
          vector[i] = course[i].step;
          break;
      }
    }
    return vector;
  }

}
