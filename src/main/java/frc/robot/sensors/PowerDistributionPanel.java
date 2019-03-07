package frc.robot.sensors;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.logging.Telemetry;
import frc.robot.logging.TelemetryBuilder;

public class PowerDistributionPanel extends edu.wpi.first.wpilibj.PowerDistributionPanel {

  private static PowerDistributionPanel instance = null;

  /**
   * Returns a singleton instance of the power distribution panel.
   * 
   * @return PowerDistributionPanel467 the PDP instance
   */
  public static PowerDistributionPanel getInstance() {
    if (instance == null) {
      instance = new PowerDistributionPanel();
    }
    // instance.initSendable(TelemetryBuilder.getInstance());
    instance.registerMetrics();

    return instance;
  }

  /**
   * This registers the power distribution with telemetry;
   */
  public static void registerPowerDistributionWithTelemetry() {
    // Register PDP with Telemetry
    PowerDistributionPanel.getInstance(); // need to create the panel before registry.
    NetworkTable table = NetworkTableInstance.getDefault().getTable("Telemetry");
    NetworkTable pdpTable = table.getSubTable("Power Distribution Panel");
    pdpTable.getKeys(); // Removes warning, need to get the table for creation.
  }

  private PowerDistributionPanel() {
    setName("Telemetry", "Power Distribution Panel");
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("PDP Input Voltage", this::getVoltage, null);
    builder.addDoubleProperty("PDP Temperature", this::getTemperature, null);
    builder.addDoubleProperty("PDP Total Current", this::getTotalCurrent, null);
    builder.addDoubleProperty("PDP Total Energy", this::getTotalEnergy, null);
    builder.addDoubleProperty("PDP Total Power", this::getVoltage, null);
    builder.addDoubleProperty("PDP Current (0)", this::current0, null);
    builder.addDoubleProperty("PDP Current (1)", this::current1, null);
    builder.addDoubleProperty("PDP Current (2)", this::current2, null);
    builder.addDoubleProperty("PDP Current (3)", this::current3, null);
    builder.addDoubleProperty("PDP Current (4)", this::current4, null);
    builder.addDoubleProperty("PDP Current (5)", this::current5, null);
    builder.addDoubleProperty("PDP Current (6)", this::current6, null);
    builder.addDoubleProperty("PDP Current (7)", this::current7, null);
    builder.addDoubleProperty("PDP Current (8)", this::current8, null);
    builder.addDoubleProperty("PDP Current (9)", this::current9, null);
    builder.addDoubleProperty("PDP Current (10)", this::current10, null);
    builder.addDoubleProperty("PDP Current (11)", this::current11, null);
    builder.addDoubleProperty("PDP Current (12)", this::current12, null);
    builder.addDoubleProperty("PDP Current (13)", this::current13, null);
    builder.addDoubleProperty("PDP Current (14)", this::current14, null);
    builder.addDoubleProperty("PDP Current (15)", this::current15, null);
  }

  public void registerMetrics() {
    Telemetry telemetry = Telemetry.getInstance();
    telemetry.addDoubleMetric("PDP Input Voltage", this::getVoltage);
    telemetry.addDoubleMetric("PDP Temperature", this::getTemperature);
    telemetry.addDoubleMetric("PDP Total Current", this::getTotalCurrent);
    telemetry.addDoubleMetric("PDP Total Energy", this::getTotalEnergy);
    telemetry.addDoubleMetric("PDP Total Power", this::getVoltage);
    telemetry.addDoubleMetric("PDP Current (0)", this::current0);
    telemetry.addDoubleMetric("PDP Current (1)", this::current1);
    telemetry.addDoubleMetric("PDP Current (2)", this::current2);
    telemetry.addDoubleMetric("PDP Current (3)", this::current3);
    telemetry.addDoubleMetric("PDP Current (4)", this::current4);
    telemetry.addDoubleMetric("PDP Current (5)", this::current5);
    telemetry.addDoubleMetric("PDP Current (6)", this::current6);
    telemetry.addDoubleMetric("PDP Current (7)", this::current7);
    telemetry.addDoubleMetric("PDP Current (8)", this::current8);
    telemetry.addDoubleMetric("PDP Current (9)", this::current9);
    telemetry.addDoubleMetric("PDP Current (10)", this::current10);
    telemetry.addDoubleMetric("PDP Current (11)", this::current11);
    telemetry.addDoubleMetric("PDP Current (12)", this::current12);
    telemetry.addDoubleMetric("PDP Current (13)", this::current13);
    telemetry.addDoubleMetric("PDP Current (14)", this::current14);
    telemetry.addDoubleMetric("PDP Current (15)", this::current15);
  }

  private double current0() {
    return getCurrent(0);
  }

  private double current1() {
    return getCurrent(1);
  }

  private double current2() {
    return getCurrent(2);
  }

  private double current3() {
    return getCurrent(3);
  }

  private double current4() {
    return getCurrent(4);
  }

  private double current5() {
    return getCurrent(5);
  }

  private double current6() {
    return getCurrent(6);
  }

  private double current7() {
    return getCurrent(7);
  }

  private double current8() {
    return getCurrent(8);
  }

  private double current9() {
    return getCurrent(9);
  }

  private double current10() {
    return getCurrent(10);
  }

  private double current11() {
    return getCurrent(11);
  }

  private double current12() {
    return getCurrent(12);
  }

  private double current13() {
    return getCurrent(13);
  }

  private double current14() {
    return getCurrent(14);
  }

  private double current15() {
    return getCurrent(15);
  }




}