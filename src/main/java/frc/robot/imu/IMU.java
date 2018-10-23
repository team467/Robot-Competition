package frc.robot.imu;

public interface IMU
{

    /**
       * {@inheritDoc}
       */
    void calibrate();

    /**
       * {@inheritDoc}
       */
    void reset();

    /**
       * Delete (free) the spi port used for the IMU.
       */
    void free();

    /**
       * {@inheritDoc}
       */
    double getAngle();

    /**
       * {@inheritDoc}
       */
    double getRate();

    double getAngleX();

    double getAngleY();

    double getAngleZ();

    double getRateX();

    double getRateY();

    double getRateZ();

    double getAccelX();

    double getAccelY();

    double getAccelZ();

    double getMagX();

    double getMagY();

    double getMagZ();

    double getPitch();

    double getRoll();

    double getYaw();

    double getLastSampleTime();

    double getBarometricPressure();

    double getTemperature();

    double getMeasuresPerDegree();

    // Get quaternion W for the Kalman AHRS.
    // Always returns 0 for the Complementary AHRS.
    double getQuaternionW();

    // Get quaternion X for the Kalman AHRS.
    // Always returns 0 for the Complementary AHRS.
    double getQuaternionX();

    // Get quaternion Y for the Kalman AHRS.
    // Always returns 0 for the Complementary AHRS.
    double getQuaternionY();

    // Get quaternion Z for the Kalman AHRS.
    // Always returns 0 for the Complementary AHRS.
    double getQuaternionZ();

    // Enable or disable yaw tilt-compensation for the Complementary AHRS.
    // Has no effect on the Kalman AHRS.
    //
    // It is likely best to set Yaw tilt-compensation to off (False) if the Yaw
    // value is to be used as feedback in a closed loop control application.
    // The tradeoff is that Yaw will only be accurate while the robot is level.
    void setTiltCompYaw(boolean enabled);

    /**
       * {@inheritDoc}
       */
    void updateTable();

}