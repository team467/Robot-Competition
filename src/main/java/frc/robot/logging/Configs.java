package frc.robot.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

public class Configs {
  private static String config = "";
  // possible locations for the config
  public static String[] configLocations = { "./csv.txt", "./csv.txt" };
  private static final Logger LOGGER = RobotLogManager.getMainLogger(Configs.class.getName());

  public static void init(){
    StringBuilder contentBuilder = new StringBuilder();
    for (String path : configLocations) {
      File file = new File(path);
      if (!file.exists()) {
        continue;
      }

      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
          contentBuilder.append(currentLine).append("\n");
        }
        break;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    config = contentBuilder.toString();
    LOGGER.info(config);
  }
}