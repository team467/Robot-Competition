package frc.robot.drive.motorcontrol.pathplanning;

import frc.robot.utilities.RobotUtilities;

import java.util.Arrays;

import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * Cubic spline planner class.
 */
public class Spline {

  private double[] coordinateX;

  //private double[]  y;

  private int nx;

  private Coefficients[] coefficients;

  //private double[] w;


  /**
   * Creates a spline given a set of x and y coordinates. A spline is a 
   * continuous function that interpolates the data to minimize the 
   * roughness of lines connecting the points.
   * 
   * @param coordinatesX the x values of the points to connect
   * @param coordinatesY the y values of the points to connect
   */
  public Spline(double[] coordinatesX, double[] coordinatesY) {
    this.coordinateX = coordinatesX;
    //this.y = y;
    nx = coordinatesX.length; // Dimension of x
    coefficients = new Coefficients[nx];
    for (int i = 0; i < nx; i++) {
      coefficients[i] = new Coefficients();
    }

    double[] h = RobotUtilities.differenceBetweenArrayValues(coordinatesX);

    // Calculate coefficient a
    for (int i = 0; i < coordinatesY.length; i++) {
      coefficients[i].coefficientA = coordinatesY[i];
    }

    double[][] matrixA = calculateMatrixA(h);
    double[] matrixB = calculateB(h);

    double[] tempC = RobotUtilities.solveLinearEquation(matrixA, matrixB);
    for (int i = 0; i < tempC.length; i++) {
      coefficients[i].coefficientC = tempC[i];
    }

    // calc spline coefficient b and d
    for (int i = 0; i < (nx - 1); i++) {
      coefficients[i].coefficientD 
          = (coefficients[i + 1].coefficientC - coefficients[i].coefficientC) / (3.0 * h[i]);
      coefficients[i].coefficientB 
          = (coefficients[i + 1].coefficientA - coefficients[i].coefficientA) / h[i]
          - h[i] * (coefficients[i + 1].coefficientC + 2.0 * coefficients[i].coefficientC) / 3.0;
    }

  }

  /**
   * Calculate the position given a point.
   *
   * @param point the point for which to find the postition
   * @return the position of the robot
   * @throws OutOfRangeException if point is outside the planned x range
   */
  public double calculatePosition(double point) throws OutOfRangeException {
    if (point < coordinateX[0] || point > coordinateX[coordinateX.length - 1]) {
      throw new OutOfRangeException(point, coordinateX[0], coordinateX[coordinateX.length - 1]);
    }

    int index = this.searchIndex(point);
    double dx = point - coordinateX[index];
    Coefficients coef = coefficients[index];
    double result =   coef.coefficientA 
        + coef.coefficientB * dx + coef.coefficientC * Math.pow(dx, 2.0) 
        + coef.coefficientD * Math.pow(dx, 3.0);

    return result;
  }

  /**
   * Calculate the first derivative of the spline at a point in time.
   * 
   * @param pointInTime the point at which to find the derivate.
   * @return the first derivative of the spline
   */
  public double calculateFirstDerivative(double pointInTime) throws OutOfRangeException {
    if (pointInTime < coordinateX[0] || pointInTime > coordinateX[coordinateX.length - 1]) {
      throw new OutOfRangeException(pointInTime, 
          coordinateX[0], coordinateX[coordinateX.length - 1]);
    }

    int index = searchIndex(pointInTime);
    double dx = pointInTime - coordinateX[index];
    Coefficients coef = coefficients[index];

    double result = coef.coefficientB 
        + 2.0 * coef.coefficientC * dx 
        + 3.0 * coef.coefficientD * Math.pow(dx, 2.0);
    return result;
  }

  /** 
   * Calculates the second derivative of the spline at a given point in time.
   * 
   * @param pointInTime the point to find the derivative for
   * @return the second derivative
   */
  public Double calculateSecondDerivative(double pointInTime) throws OutOfRangeException {
    if (pointInTime < coordinateX[0] || pointInTime > coordinateX[coordinateX.length - 1]) {
      throw new OutOfRangeException(pointInTime, 
          coordinateX[0], coordinateX[coordinateX.length - 1]);
    }

    int index = searchIndex(pointInTime);
    double dx = pointInTime - coordinateX[index];
    Coefficients coef = coefficients[index];
    double result = 2.0 * coef.coefficientC + 6.0 * coef.coefficientD * dx;

    return result;
  }

  /**
   * Search for the data segment index.
   * 
   * @param point the point to find in the array
   * @return the index of the point
   */
  public int searchIndex(double point) {
    int index = Arrays.binarySearch(coordinateX, point);
    if (index < 0) {
      index *= -1;
      index -= 2;
    }
    return index;
  }

  /**
   * calc matrix A for spline coefficient c.
   *
   * @param inputVector the target points
   * @return the matrix
   */
  public double[][] calculateMatrixA(double[] inputVector) {
    double[][] matrixA = new double[nx][nx];
    // Zero the 2D matrix
    for (double[] row : matrixA) {
      Arrays.fill(row, 0.0);
    }
    matrixA[0][0] = 1.0;

    for (int i = 0; i < (nx - 1); i++) {
      if (i != (nx - 2)) {
        matrixA[i + 1][i + 1] = 2.0 * (inputVector[i] + inputVector[i + 1]);
      }
      matrixA[i + 1][i] = inputVector[i];
      matrixA[i][i + 1] = inputVector[i];
    }
    matrixA[0][1] = 0.0;
    matrixA[nx - 1][nx - 2] = 0.0;
    matrixA[nx - 1][nx - 1] = 1.0;

    return matrixA;
  }

  /**
   * Calculate matrix B for spline coefficient c.
   *
   * @param inputMatrix the input values
   * @return the spline coefficients
   */
  private double[] calculateB(double[] inputVector) {
    double[] matrixB = new double[nx];
    Arrays.fill(matrixB, 0.0); // Zero Array
    for (int i = 0; i < (nx - 2); i++) {
      matrixB[i + 1] = 3.0 * (coefficients[i + 2].coefficientA - coefficients[i + 1].coefficientA) 
          / inputVector[i + 1] 
          - 3.0 * (coefficients[i + 1].coefficientA - coefficients[i].coefficientA) 
          / inputVector[i];
    }

    return matrixB;
  }

  private class Coefficients {
    double coefficientA = 0;
    double coefficientB = 0;
    double coefficientC = 0;
    double coefficientD = 0;
  }

}
