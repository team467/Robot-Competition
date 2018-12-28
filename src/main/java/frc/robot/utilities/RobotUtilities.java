package frc.robot.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class RobotUtilities {

  public static double[] differenceBetweenArrayValues(double[] base) {
    double[] diffOfBaseValues = new double[base.length - 1];
    for (int i = 0; i < (base.length - 1); i++) {
      diffOfBaseValues[i] = base[i + 1] - base[i];
    }
    return diffOfBaseValues;
  }

  public static double[] cumulativeSumOfArrayValues(double[] base) {
    double[] cumulativeSums = new double[base.length];
    double sum = 0;
    for (int i = 0; i < base.length; i++) {
      sum += base[i];
      cumulativeSums[i] = sum;
    }
    return cumulativeSums;
  }

  public static void printVector(String name, double[] vector) {
    int count = 0;
    System.out.print(name + ": [");
    for (double value : vector) {
      System.out.print(value);
      count++;
      if (count < vector.length) {
        System.out.print(", ");
      }
    }
    System.out.print("]");
  }

  public static void printMatrix(String name, double[][] matrix) {
    System.out.println("Matrix: " + name);
    int rowCount = 0;
    for (double[] row : matrix) {
      printVector("" + rowCount, row);
      rowCount++;
      if (rowCount < matrix.length) {
        System.out.println(", ");
      }
    }
  }

  public static double[] solveLinearEquation(double[][] matrixA, double[] vectorB) {

    // printMatrix("A", matrixA);
    // System.out.println();
    // System.out.println();

    // printVector("B", vectorB);
    // System.out.println();

    RealMatrix coefficients = new Array2DRowRealMatrix(matrixA);
    RealVector constants = new ArrayRealVector(vectorB);
    DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
    RealVector solution = solver.solve(constants);
    return solution.toArray();
  }

  public static double[] listToArray(ArrayList<Double> list) {
    // Frikin complicated way to convert from a array list that knows it has doubles 
    //to an array of doubles due to null handling issues.
    return Arrays.stream(list.toArray(new Double[0])).mapToDouble(Double::doubleValue).toArray();
  }

  public static ArrayList<Double> arrayToList(double[] array) {
    Double[] boxedArray = Arrays.stream(array).boxed().toArray(Double[]::new);
    ArrayList<Double> list = new ArrayList<Double>();
    Collections.addAll(list, boxedArray);
    return list;
  }

  public static double[] arrangeInterval(double start, double stop) {
    return RobotUtilities.arrangeInterval(start, stop, 0.1);
  }

  public static double[] arrangeInterval(double start, double stop, double step) {
    int stepCount = (int) Math.ceil((stop - start) / step);
    double[] interval = new double[stepCount];
    for (int i = 0; i < stepCount; i++) {
      interval[i] = start + step * i;
    }
    return interval;
  }

  /**
   * Ensures value is between the specified interval bounds.
   *
   * @param value the value to check
   * @param min the interval lower limit
   * @param max the interval upper limit
   * @return the value if in bounds, else min or max as appropriate
   */
  public static double clip(double value, double min, double max) {
    if (value < min) {
      value = min;
    } else if (value > max) {
      value = max;
    }
    return value;
  }

  /**
   * Normalize an angle to [-pi, pi].
   *
   * @param angle the angle to normalize
   * @return double Angle in radian in [-pi, pi]
   */
  public static double normalizeAngle(double angle) {
    while (angle > Math.PI) {
      angle -= 2.0 * Math.PI;
    }

    while (angle < -Math.PI) {
      angle += 2.0 * Math.PI;
    }

    return angle;
  }

}
