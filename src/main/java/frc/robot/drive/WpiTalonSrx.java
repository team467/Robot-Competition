package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class WpiTalonSrx extends WPI_TalonSRX implements WpiTalonSrxInterface {

  public WpiTalonSrx(int deviceId) {
    super(deviceId);
  }

}