package frc.robot.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;

public class RobotLogManager {

  private static boolean initialized = false;

  private static File directory;

  private static String[] filepaths = { //File paths go in this array
    "/media/sda1/logging/log4j2.yaml",
    "/media/sda2/logging/log4j2.yaml",
    "/media/sdb1/logging/log4j2.yaml",
    "/media/sda1/logging/log4j2-test.yaml",
    "/media/sda2/logging/log4j2-test.yaml",
    "./home/lvuser/deploy/log4j2.yaml",
    "./home/lvuser/deploy/log4j2-test.yaml",
    "./src/main/deploy/log4j2.yaml",
    "./src/main/deploy/log4j2-test.yaml"
  };
  
  public static File getDirectory() {
    return directory;
  }

  private static boolean doesFileExist(String filepath) {
    File file = new File(filepath);
    if (file.exists()) {
      directory = new File(file.getParent(), "logs");
      if (!directory.exists()) {
        directory.mkdirs();
      }
      return true;
    } else {
      return false;
    }
  }

  private static void init(String pathToConfig) {
    if (!initialized) {
      try {
        File configSourceFile = new File(pathToConfig);
        ConfigurationSource source = new ConfigurationSource(
              new FileInputStream(configSourceFile), configSourceFile);
        ConfigurationFactory configurationFactory = YamlConfigurationFactory.getInstance();
        ConfigurationFactory.setConfigurationFactory(configurationFactory);
        Configuration configuration = configurationFactory.getConfiguration(null, source);
        //TODO make it dynamically change output
        
      } catch (IOException e) {
        e.printStackTrace();
      }
      initialized = true;
    }
  }

  private static boolean init() {
    if (!initialized) {
      for (String path : filepaths) {
        if (doesFileExist(path)) {
          init(path);
          System.out.println(path);
          break;
        }
      }
    }
    return initialized;
  }

  /**
   * Initializes the log system if required, then returns the telemetry logger.
   * 
   * @param className the class for subsetting the logger
   * @return the logger
   */
  public static Logger getTelemetryLogger() {
    init();
    return LogManager.getLogger("TELEMETRY");
  }

  /**
   * Initializes the log system if required, then returns the appropriate class logger.
   * 
   * @param className the class for subsetting the logger
   * @return the logger
   */
  public static Logger getPerfLogger() {
    init();
    return LogManager.getLogger("PERF_TIMERS");
  }

  /**
   * Initializes the log system if required, then returns the appropriate class logger.
   * 
   * @param className the class for subsetting the logger
   * @return the logger
   */
  public static Logger getMainLogger(String className) {
    init();
    return LogManager.getLogger(className);
  }

  /**
   * Initializes the log system if required, then returns the appropriate class logger.
   * The custom config file would normally be used for tests.
   * 
   * @param customLogConfig the full path to a customized log configuration file.
   * @param className the class for subsetting the logger
   * @return the logger
   */
  public static Logger getMainLogger(String customLogConfig, String className) {
    init(customLogConfig);
    return LogManager.getLogger(className);
  }

}