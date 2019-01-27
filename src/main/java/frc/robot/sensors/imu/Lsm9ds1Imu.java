//*----------------------------------------------------------------------------*/
// Copyright (c) FIRST 2016. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.sensors.imu;

import org.apache.commons.math3.ml.neuralnet.Network;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.Timer;

public class Lsm9ds1Imu extends ImuBase implements Imu {


  /**
   * Constructor.
   */
  NetworkTableInstance inst = NetworkTableInstance.getDefault();
  NetworkTable table = inst.getTable("imu");
  NetworkTableEntry gyro = table.getEntry("gyro");
  NetworkTableEntry accel = table.getEntry("accel");
  NetworkTableEntry mag = table.getEntry("mag");

  public Lsm9ds1Imu(Axis yawAxis, AhrsAlgorithm algorithm) {
    super(yawAxis, algorithm);
  }

  /*
   * Constructor assuming Complementary AHRS algorithm.
   */
  public Lsm9ds1Imu(Axis yawAxis) {
    this(yawAxis, AhrsAlgorithm.Complementary);
  }


  public void recalibrate() {
    table.getEntry("recal").setNumber(1);
  }

  protected void acquire() {
    synchronized (this) {
      lastSampleTime = Timer.getFPGATimestamp();
    }

    

    while (!freed.get()) {
      if (interrupt.waitForInterrupt(timeout) == InterruptableSensorBase.WaitResult.kTimeout) {
        continue;
      }

      double sampleTime = interrupt.readFallingTimestamp();
      double dt;
      synchronized (this) {
        dt = sampleTime - lastSampleTime;
        lastSampleTime = sampleTime;
      }

      double gyroX =  gyro.getDoubleArray(new double[] {0.0, 0.0, 0.0})[0];
      double gyroY = gyro.getDoubleArray(new double[] {0.0, 0.0, 0.0})[1];
      double gyroZ = gyro.getDoubleArray(new double[] {0.0, 0.0, 0.0})[2];
      double accelX = accel.getDoubleArray(new double[] {0.0, 0.0, 0.0})[0];
      double accelY = accel.getDoubleArray(new double[] {0.0, 0.0, 0.0})[1];
      double accelZ = accel.getDoubleArray(new double[] {0.0, 0.0, 0.0})[2];
      double magX = mag.getDoubleArray(new double[] {0.0, 0.0, 0.0})[0];
      double magY = mag.getDoubleArray(new double[] {0.0, 0.0, 0.0})[1];
      double magZ = mag.getDoubleArray(new double[] {0.0, 0.0, 0.0})[2];

      samplesMutex.lock();
      try {
        // If the FIFO is full, just drop it
        if (calculateStarted && samplesCount < samplesDepth) {
          Sample sample = samples[samplesPutIndex];
          sample.gyroX = gyroX;
          sample.gyroY = gyroY;
          sample.gyroZ = gyroZ;
          sample.accelX = accelX;
          sample.accelY = accelY;
          sample.accelZ = accelZ;
          sample.magX = magX;
          sample.magY = magY;
          sample.magZ = magZ;
          sample.dt = dt;
          samplesPutIndex += 1;
          if (samplesPutIndex == samples.length) {
            samplesPutIndex = 0;
          }
          samplesCount += 1;
          samplesNotEmpty.signal();
        }
      } finally {
        samplesMutex.unlock();
      }

      // Update global state
      synchronized (this) {
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.magX = magX;
        this.magY = magY;
        this.magZ = magZ;

        accumulatedCount += 1;
        accumulatedGyroX += gyroX;
        accumulatedGyroY += gyroY;
        accumulatedGyroZ += gyroZ;

        integratedGyroX += (gyroX - gyroOffsetX) * dt;
        integratedGyroY += (gyroY - gyroOffsetY) * dt;
        integratedGyroZ += (gyroZ - gyroOffsetZ) * dt;
      }

    }
  }

}
