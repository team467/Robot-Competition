//*----------------------------------------------------------------------------*/
// Copyright (c) FIRST 2016. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.sensors.imu;

import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.Timer;

public class Lsm9ds1Imu extends ImuBase implements Imu {


  /**
   * Constructor.
   */
  public Lsm9ds1Imu(Axis yawAxis, AhrsAlgorithm algorithm) {
    super(yawAxis, algorithm);
  }

  /*
   * Constructor assuming Complementary AHRS algorithm.
   */
  public Lsm9ds1Imu(Axis yawAxis) {
    this(yawAxis, AhrsAlgorithm.Complementary);
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

      double gyroX = angleXTableEntry.getValue().getDouble();
      double gyroY = angleYTableEntry.getValue().getDouble();
      double gyroZ = angleZTableEntry.getValue().getDouble();
      double accelX = accelXTableEntry.getValue().getDouble();
      double accelY = accelYTableEntry.getValue().getDouble();
      double accelZ = accelZTableEntry.getValue().getDouble();
      double magX = magXTableEntry.getValue().getDouble();
      double magY = magYTableEntry.getValue().getDouble();
      double magZ = magZTableEntry.getValue().getDouble();
      double temp = tempTableEntry.getValue().getDouble();

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
        this.temp = temp;

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
