package frc.robot.simulator.communications;

import java.io.File;

public class CSVReplayer {
  public CSVFile csvFile = new CSVFile();
  public double leftDistance;
  public double rightDistance;
  public double startX;
  public double startY;
  public boolean isZeroed;
  public int steps = 0;
  public String lastpath = "";

  public CSVReplayer(String source) {
    csvFile.loadFromFile(source);
    leftDistance = Double.parseDouble((String) csvFile.get(0));
    rightDistance = Double.parseDouble((String) csvFile.get(1));
    startX = Double.parseDouble((String) csvFile.get(2));
    startY = Double.parseDouble((String) csvFile.get(3));
    isZeroed = Boolean.parseBoolean((String) csvFile.get(5));
  }

  public CSVReplayer(File source) {

    csvFile.loadFromFile(source);
    leftDistance = Double.parseDouble((String) csvFile.get(0));
    rightDistance = Double.parseDouble((String) csvFile.get(1));
    startX = Double.parseDouble((String) csvFile.get(2));
    startY = Double.parseDouble((String) csvFile.get(3));
    isZeroed = Boolean.parseBoolean((String) csvFile.get(5));
  }

  public void next() {
    csvFile.currentRow++;
    leftDistance = Double.parseDouble((String) csvFile.get(0));
    rightDistance = Double.parseDouble((String) csvFile.get(1));
    startX = Double.parseDouble((String) csvFile.get(2));
    startY = Double.parseDouble((String) csvFile.get(3));
    isZeroed = Boolean.parseBoolean((String) csvFile.get(5));
    steps++;
  }
}