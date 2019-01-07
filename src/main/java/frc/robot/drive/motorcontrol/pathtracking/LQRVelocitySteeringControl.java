package frc.robot.drive.motorcontrol.pathtracking;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import frc.robot.utilities.RobotUtilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Path tracking simulation with LQR speed and steering control.
 */
public class LqrVelocitySteeringControl {

  private static boolean showAnimation = true;

  private RealMatrix matrixQ = MatrixUtils.createRealIdentityMatrix(5);
  
  private RealMatrix matrixR = MatrixUtils.createRealIdentityMatrix(2);

  private RealMatrix matrixX;
  
  public RealMatrix matrixX() {
    return matrixX;
  }

  private EigenDecomposition eigenDecomposition;
  
  public EigenDecomposition eigenDecomposition() {
    return eigenDecomposition;
  }


  private double timeIncrement = 0.1; // Time ticks
  private  double wheelBase = 0.5; // Wheel base of the vehicle [m]
  private double maxSteer = Math.toRadians(45.0);

  // State variables
  private double currentX = 0.0;
  private double currentY = 0.0;
  private double currentYaw = 0.0;
  private double currentVelocity = 0.0;

  public LqrVelocitySteeringControl(
      double x,
      double y,
      double yaw,
      double velocity) {
    this.currentX = x;
    this.currentY = y;
    this.currentYaw = yaw;
    this.currentVelocity = velocity;
  }

  public void update(double accelleration, double delta) {
    RobotUtilities.clip(delta, -maxSteer, maxSteer);
    currentX += currentVelocity * Math.cos(currentYaw) * timeIncrement;
    currentY += currentVelocity * Math.sin(currentYaw) * timeIncrement;
    currentYaw += currentVelocity / wheelBase * Math.tan(delta) * timeIncrement;
    currentVelocity += accelleration * timeIncrement;
  }

  public double pi2pi(double angle) {
    return (double) ((int) (angle + Math.PI) % (int) ((2 * Math.PI) - Math.PI));
  }


  public RealMatrix solveDare(
      RealMatrix matrixA,
      RealMatrix matrixB,
      RealMatrix matrixQ,
      RealMatrix matrixR) {

    RealMatrix matrixX = matrixQ;
    RealMatrix matrixXn 
        = MatrixUtils.createRealMatrix(matrixX.getRowDimension(), matrixX.getColumnDimension());
    double eps = 0.1;

    for (int i = 0; i < 150; i++) {
      // A.T * X * A - A.T * X * B * INV(R + B.T * X * B) * B.T * X * A + Q
      RealMatrix matrixAtX = matrixA.transpose().multiply(matrixX);
      RealMatrix matrixBtX = matrixB.transpose().multiply(matrixX);
      RealMatrix firstTerm = matrixA.multiply(matrixAtX);
      RealMatrix inverse = MatrixUtils.inverse(matrixR.add(matrixB.multiply(matrixBtX)));
      RealMatrix secondTerm 
            = matrixB.multiply(matrixAtX).multiply(inverse).multiply(matrixA.multiply(matrixBtX));
      matrixXn = firstTerm.subtract(secondTerm.add(matrixQ));

      double[][] matrixXData = matrixX.getData();
      double[][] matrixXnData = matrixXn.getData();
      double max = 0.0;
      for (int j = 0; j < matrixXData.length; j++) {
        for (int k = 0; k < matrixXData[j].length; k++) {
          double value = Math.abs(matrixXnData[j][k] - matrixXData[j][k]);
          if (value > max) {
            max = value;
          }
        }
      }
      if (max < eps) {
        matrixX = matrixXn;
        break;
      }
    }
    return matrixXn;
  }

  // Returns K, X, and eigVals in python version
  /**
   * Solve the discrete time lqr controller.
   *      x[k+1] = A x[k] + B u[k]
   *      cost = sum x[k].T*Q*x[k] + u[k].T*R*u[k]
   * ref Bertsekas, p.151
   *
   * @param matrixA
   * @param matrixB
   * @param matrixQ
   * @param matrixR
   */
  public RealMatrix solveDiscreteTimeLqr(
      RealMatrix matrixA,
      RealMatrix matrixB,
      RealMatrix matrixQ,
      RealMatrix matrixR) {

    // First try to solve the ricatti equation
    matrixX = solveDare(matrixA, matrixB, matrixQ, matrixR);

    // Compute the LQR gain
    // (B.T * X * B + R) * (B.T * X * A)
    RealMatrix matrixBtX = matrixB.transpose().multiply(matrixX);
    RealMatrix firstTerm = matrixBtX.multiply(matrixB).add(matrixR);
    RealMatrix secondTerm = matrixBtX.multiply(matrixA);
    RealMatrix matrixK = MatrixUtils.inverse(firstTerm).multiply(secondTerm);

    RealMatrix eigenMatrix = matrixB.multiply(matrixK);
    eigenMatrix = matrixA.subtract(eigenMatrix);
    eigenDecomposition = new EigenDecomposition(eigenMatrix);

    return matrixK;
  }

  private double delta = 0.0;
  private double acceleration = 0.0;

  public int lqrSteeringControl(
      double[] planX,
      double[] planY,
      double[] planYaw,
      double[] planK,
      double pe,
      double pthE,
      double[] speedProfile) {

    double velocity = this.currentVelocity;

    RealMatrix matrixA = MatrixUtils.createRealMatrix(5, 5);
    matrixA.setEntry(0, 0, 1.0);
    matrixA.setEntry(0, 1, timeIncrement);
    matrixA.setEntry(1, 2, velocity);
    matrixA.setEntry(2, 2, 1.0);
    matrixA.setEntry(2, 3, timeIncrement);
    matrixA.setEntry(4, 4, 1.0);

    RealMatrix matrixB = MatrixUtils.createRealMatrix(5, 2);
    matrixB.setEntry(3, 0, velocity / wheelBase);
    matrixB.setEntry(4, 1, timeIncrement);

    RealMatrix matrixK = solveDiscreteTimeLqr(matrixA, matrixB, matrixQ, matrixR);

    int index = calculateNearestIndex(planX, planY, planYaw);
    double tv = speedProfile[index];
    double k = planK[index];
    double thE = pi2pi(currentYaw - planYaw[index]);
    RealMatrix x = MatrixUtils.createRealMatrix(5, 1);
    x.setEntry(0, 0, distanceToNearestPlanPoint);
    x.setEntry(1, 0, (distanceToNearestPlanPoint - pe) / timeIncrement);
    x.setEntry(2, 0, thE);
    x.setEntry(3, 0, (thE - pthE) / timeIncrement);
    x.setEntry(4, 0, velocity - tv);

    RealMatrix ustar = matrixK.scalarMultiply(-1.0).multiply(x);

    // Calculate steering input
    double ff = Math.atan2(wheelBase * k, 1);
    double fb = pi2pi(ustar.getEntry(0, 0));
    delta = ff + fb;

    // Calculate acceleration input
    acceleration = ustar.getEntry(1, 0);

    return index;
  }

  private double distanceToNearestPlanPoint = 0.0;

  public double distanceToNearestPlanPoint() {
    return distanceToNearestPlanPoint;
  }

  /**
   *
   * @param planX
   * @param planY
   * @param planYaw
   * @return
   */
  public int calculateNearestIndex(double[] planX, double[] planY, double[] planYaw) {

    ArrayList<Double> distancesToPlanPoints = new ArrayList<Double>(planY.length);
    for (int i = 0; i < planX.length; i++) {
      distancesToPlanPoints.add(
          Math.pow((currentX - planX[i]), 2) + Math.pow((currentY - planY[i]), 2));
    }
    distanceToNearestPlanPoint = Collections.min(distancesToPlanPoints);
    int index = distancesToPlanPoints.indexOf(distanceToNearestPlanPoint);

    double angle 
        = pi2pi(planYaw[index] - Math.atan2((planY[index] - currentY), (planX[index] - currentX)));
    if (angle < 0) {
      distanceToNearestPlanPoint *= -1.0;
    }

    return index;
  }

  public void closedLoopPrediction(
      double[] planX,
      double[] planY,
      double[] planYaw,
      double[] planK,
      double[] speedProfile,
      double[] goal) {

    double goalDistance = 0.3;
    double stopSpeed = 0.05;

    ArrayList<Double> x = new ArrayList<Double>();
    ArrayList<Double> y = new ArrayList<Double>();
    ArrayList<Double> yaw = new ArrayList<Double>();

    x.add(currentX);
    y.add(currentY);
    yaw.add(currentYaw);

    ArrayList<Double> velocity = new ArrayList<Double>();
    ArrayList<Double> t = new ArrayList<Double>();
    velocity.add(currentVelocity);
    t.add(0.0);
    int targetIndex = calculateNearestIndex(planX, planY, planYaw);

    double e = 0.0;
    double eth = 0.0;

    double maxSimulationTime = 500.0;
    double time = 0.0;
    while (maxSimulationTime >= time) {
      targetIndex = lqrSteeringControl(planX, planY, planYaw, planK, e, eth, speedProfile);

      update(acceleration, delta);

      if (Math.abs(currentVelocity) <= stopSpeed) {
        targetIndex++;
      }

      time += timeIncrement;

      // Check goal
      double dx = currentX - goal[0];
      double dy = currentY - goal[0];
      if (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) <= goalDistance) {
        System.out.println("Goal");
        break;
      }

      x.add(currentX);
      y.add(currentY);
      yaw.add(currentYaw);
      velocity.add(currentVelocity);
      t.add(time);

      if (targetIndex % 1 == 0 && showAnimation) {
        Plot plt = Plot.create();
        plt.plot()
            .add(RobotUtilities.arrayToList(planX), RobotUtilities.arrayToList(planY))
            .label("course")
            .linestyle("-.")
        ;
        plt.plot()
            .add(x, y)
            .label("trajectory")
            .linestyle("-")
        ;
        plt.legend();

        try {
          plt.show();
        } catch (IOException ex) {
          System.err.println(ex);
        } catch (PythonExecutionException ex) {
          System.err.println(ex);
        }
      }

    }
    // return t, x, y, yaw, v
  }

  public double[] calculateSpeedProfile(
      double[] planX,
      double[] planY,
      double[] planYaw,
      double targetSpeed
  ) {
    double[] speedProfile = new double[planX.length];
    for (int i = 0; i < speedProfile.length; i++) {
      speedProfile[i] = targetSpeed;
    }

    double direction = 1.0;

    for (int i = 0; i < (planX.length - 1); i++) {
      double dyaw = Math.abs(planYaw[i + 1] - planYaw[i]);
      boolean switchDirection = (((Math.PI / 4.0) <= dyaw) && (dyaw < (Math.PI / 2.0)));

      if (switchDirection) {
        direction *= -1.0;
      }

      if (direction != 1.0) {
        speedProfile[i] = -targetSpeed;
      } else {
        speedProfile[i] = targetSpeed;
      }

      if (switchDirection) {
        speedProfile[i] = 0.0;
      }
    }

    // Speed down
    for (int i = 0; i < 40; i++) {
      int index = speedProfile.length - i;
      speedProfile[index] = targetSpeed / (50 - i);
      if (speedProfile[index] <= (1.0 / 3.6)) {
        speedProfile[index] = 1.0 / 3.6;
      }
    }

    return speedProfile;
  }

}
