package frc.robot.drive.motorcontrol.pathplanning;

import java.io.IOException;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import org.apache.commons.math3.exception.OutOfRangeException;

import frc.robot.utilities.Utils;

/**
 * 2D Cubic Spline Builder
 */
public class Spline2D {

    private double[] s;

    private Spline splineX;

    private Spline splineY;

    public Spline2D(double[] x, double[] y) {
        s = calculateS(x, y);
        splineX = new Spline(s, x);
        splineY = new Spline(s, y);
    }

    public double[] calculateS(double [] x, double[] y) {
        double[] diffX = Utils.differenceBetweenArrayValues(x);
        double[] diffY = Utils.differenceBetweenArrayValues(y);
        double[] dS = new double[x.length-1];

        for (int i=0; i < x.length-1; i++) {
            dS[i] = Math.sqrt(Math.pow(diffX[i], 2) + Math.pow(diffY[i], 2));
        }

        double[] spline = new double[x.length];
        double cumulativeSum = 0.0;
        spline[0] = 0;
        for (int i=0; i < dS.length; i++) {
            cumulativeSum += dS[i];
            spline[i+1] = cumulativeSum;
        }

        return spline;
}

    /**
     * Calculates the position on the spline.
     *
     * @param splineToCalculate
     */
    private double[] calculatePosition(double point) throws OutOfRangeException {
        double[] position = new double[2];
        double x = splineX.calculatePosition(point);
        double y = splineY.calculatePosition(point);
        position[0] = x;
        position[1] = y;
        return position;

    }

    private double calculateCurvature(double point) throws OutOfRangeException {
        double firstDerivativeX = splineX.calculateFirstDerivative(point);
        double secondDerivativeX = splineX.calculateSecondDerivative(point);
        double firstDerivativeY = splineY.calculateFirstDerivative(point);
        double secondDerivativeY = splineY.calculateSecondDerivative(point);
        double curvature = (firstDerivativeX * secondDerivativeY - secondDerivativeX * firstDerivativeY)
            / Math.pow(Math.pow(firstDerivativeX, 2.0) + Math.pow(firstDerivativeY, 2.0), (3.0 / 2.0));
        return curvature;
    }

    private double calculateHeading(double point) throws OutOfRangeException {
        double firstDerivativeX = splineX.calculateFirstDerivative(point);
        double firstDerivativeY = splineY.calculateFirstDerivative(point);
        double yaw = Math.atan2(firstDerivativeY, firstDerivativeX);
        return yaw;
    }

    public SplineCourseData[] calculateSplineCourse(double[] x, double[] y) {
        return calculateSplineCourse (x, y, 0.1);
    }

    public static SplineCourseData[] calculateSplineCourse(double[] x, double[] y, double step) {
        Spline2D spline = new Spline2D(x, y);
        double[] s = Utils.arrangeInterval(0, spline.s[spline.s.length-1], step);
        SplineCourseData[] courseData = new SplineCourseData[s.length];
        for (int i = 0; i < s.length; i++) {
            double[] xy = spline.calculatePosition(s[i]);
            courseData[i] = new SplineCourseData();
            courseData[i].x = xy[0];
            courseData[i].y = xy[1];
            courseData[i].heading = spline.calculateHeading(s[i]);
            courseData[i].k = spline.calculateCurvature(s[i]);
            courseData[i].step = s[i];
        }


        return courseData;
    }

    public static void test2dSpline() {

        System.out.println("Spline 2D Test");
        double[] x = {-2.5,  0.0, 2.5, 5.0, 7.5, 3.0, -1.0};
        double[] y = { 0.7, -6.0, 5.0, 6.5, 0.0, 5.0, -2.0};
        double step = 0.1; // [m] distance of each intepolated points

        SplineCourseData[] course = calculateSplineCourse(x, y, step);

        double[] rx = new double[course.length];
        double[] ry = new double[course.length];
        double[] rk = new double[course.length];
        double[] ryaw = new double[course.length];
        double[] s = new double[course.length];

        for (int i = 0; i < course.length; i++) {
            rx[i] = course[i].x;
            ry[i] = course[i].y;
            ryaw[i] = Math.toDegrees(course[i].heading);
            rk[i] = course[i].k;
            s[i] = course[i].step;
        }

        Plot plt1 = Plot.create();
        Plot plt2 = Plot.create();
        Plot plt3 = Plot.create();

        plt1.plot()
            .add(Utils.arrayToList(x), Utils.arrayToList(y))
            .label("input")
            .linestyle("-.")
        ;

        plt1.plot()
             .add(Utils.arrayToList(rx), Utils.arrayToList(ry))
             .label("spline")
             .linestyle("-")
         ;
        plt1.xlabel("x[m]");
        plt1.ylabel("y[m]");
        plt1.legend();

        plt2.plot()
            .add(Utils.arrayToList(s), Utils.arrayToList(ryaw))
            .label("yaw")
           .linestyle("-")
        ;
        plt2.legend();
        plt2.xlabel("line length[m]");
        plt2.ylabel("yaw angle[deg]");

        plt3.plot()
            .add(Utils.arrayToList(s), Utils.arrayToList(rk))
            .label("curvature")
            .linestyle("-")
        ;
        plt3.legend();
        plt3.xlabel("line length[m]");
        plt3.ylabel("curvature [1/m]");

    try{
        // plt1.show();
        plt2.show();
        // plt3.show();
    } catch (IOException ex) {
        System.err.println(ex);
    } catch (PythonExecutionException ex) {
        System.err.println(ex);
    }

}

    public static void main(String[] args) {
        Spline2D.test2dSpline();
    }



}

