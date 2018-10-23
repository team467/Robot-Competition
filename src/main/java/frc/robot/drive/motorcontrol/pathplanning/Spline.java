package frc.robot.drive.motorcontrol.pathplanning;

import java.util.Arrays;

import org.apache.commons.math3.exception.OutOfRangeException;

import frc.robot.utilities.Utils;

/**
 * Cubic spline planner class
 */
public class Spline {

    private double[] x;

    //private double[]  y;

    private int nx;

    private Coefficients[] coefficients;

    //private double[] w;

    public Spline(double[] x, double[] y) {
        this.x = x;
//        this.y = y;
        nx = x.length; // Dimension of x
        coefficients = new Coefficients[nx];
        for (int i=0; i<nx; i++)
            coefficients[i] = new Coefficients();

        double[] h = Utils.differenceBetweenArrayValues(x);

        // Calculate coefficient a
        for (int i = 0; i < y.length; i++) {
            coefficients[i].a = y[i];
        }

        double[][] A = calculateMatrixA(h);
        double[] B = calculateB(h);

        double[] tempC = Utils.solveLinearEquation(A, B);
        for (int i=0; i < tempC.length; i++) {
            coefficients[i].c = tempC[i];
        }

        // calc spline coefficient b and d
        for (int i=0; i < (nx-1); i++) {
            coefficients[i].d = (coefficients[i+1].c - coefficients[i].c) / (3.0 * h[i]);
            coefficients[i].b = (coefficients[i+1].a - coefficients[i].a) / h[i]
                - h[i] * (coefficients[i+1].c + 2.0 * coefficients[i].c) / 3.0;
        }

    }

    /**
     * Calculate the position
     *
     * @param point
     * @return the position
     * @throws OutOfRangeException if point is outside the planned x range
     */
    public double calculatePosition(double point) throws OutOfRangeException {
        if (point < x[0] || point > x[x.length-1]) {
            throw new OutOfRangeException(point, x[0], x[x.length-1]);
        }

        int index = this.searchIndex(point);
        double dx = point - x[index];
        Coefficients coef = coefficients[index];
        double result =   coef.a + coef.b * dx + coef.c * Math.pow(dx, 2.0) + coef.d * Math.pow(dx, 3.0);

        return result;
}

    /**
     * Calculate the first derivative
     * @param point
     * @return
     */
    public double calculateFirstDerivative(double pointInTime) throws OutOfRangeException {
        if (pointInTime < x[0] || pointInTime > x[x.length-1]) {
            throw new OutOfRangeException(pointInTime, x[0], x[x.length-1]);
        }

        int index = searchIndex(pointInTime);
        double dx = pointInTime - x[index];
        Coefficients coef = coefficients[index];

        double result = coef.b + 2.0 * coef.c * dx + 3.0 * coef.d * Math.pow(dx, 2.0);
        return result;

    }

    /** */
    public Double calculateSecondDerivative(double pointInTime) throws OutOfRangeException {
        if (pointInTime < x[0] || pointInTime > x[x.length-1]) {
            throw new OutOfRangeException(pointInTime, x[0], x[x.length-1]);
        }

        int index = searchIndex(pointInTime);
        double dx = pointInTime - x[index];
        Coefficients coef = coefficients[index];
        double result = 2.0 * coef.c + 6.0 * coef.d * dx;

        return result;
    }

    /**
     * Search for the data segment index
     * @param point
     * @return
     */
    public int searchIndex(double point) {

        int index = Arrays.binarySearch(x, point);
        if (index < 0) {
            index *= -1;
            index -= 2;
        }
        return index;
    }

    /**
     * calc matrix A for spline coefficient c
     *
     * @param inputMatrix
     * @return
     */
    public double[][] calculateMatrixA(double[] inputVector) {
        double[][] A = new double[nx][nx];
        // Zero the 2D matrix
        for (double[] row : A) {
            Arrays.fill(row, 0.0);
        }
        A[0][0] = 1.0;

        for (int i = 0; i < (nx-1); i++) {
            if (i != (nx-2)) {
                A[i+1][i+1] = 2.0 * (inputVector[i] + inputVector[i+1]);
            }
            A[i+1][i] = inputVector[i];
            A[i][i+1] = inputVector[i];
        }
        A[0][1] = 0.0;
        A[nx-1][nx-2] = 0.0;
        A[nx-1][nx-1] = 1.0;

        return A;
    }

    /**
     * Calculate matrix B for spline coefficient c
     *
     * @param inputMatrix
     * @return
     */
    private double[] calculateB(double[] inputVector) {
        double[] B = new double[nx];
        Arrays.fill(B, 0.0); // Zero Array
        for (int i = 0; i < (nx - 2); i++) {
            B[i+1] = 3.0 * (coefficients[i+2].a - coefficients[i+1].a) / inputVector[i+1] - 3.0 * (coefficients[i+1].a - coefficients[i].a) / inputVector[i];
        }

        return B;
    }

    private class Coefficients {
        double a=0, b=0, c=0, d=0;
    }



}
