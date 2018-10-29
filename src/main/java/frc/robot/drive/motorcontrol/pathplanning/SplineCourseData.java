package frc.robot.drive.motorcontrol.pathplanning;

public class SplineCourseData {
    public double x;
    public double y;
    public double heading;
    public double k;
    public double step;

    public static double[] vectorize(SplineCourseData[] course, String variable) {
        double[] vector = new double[course.length];
        variable = variable.toLowerCase();
        for (int i = 0; i < course.length; i++) {
            switch (variable) {
            case "x":
                vector[i] = course[i].x;
                break;
            case "y":
                vector[i] = course[i].y;
                break;
            case "heading":
                vector[i] = course[i].heading;
                break;
            case "k":
                vector[i] = course[i].k;
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
