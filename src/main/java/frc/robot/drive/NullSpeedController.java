package frc.robot.drive;

import edu.wpi.first.wpilibj.SpeedController;

public class NullSpeedController implements SpeedController {

  @Override
  public void pidWrite(double output) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void set(double speed) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public double get() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setInverted(boolean isInverted) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean getInverted() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void disable() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stopMotor() {
    // TODO Auto-generated method stub
    
  }

}
