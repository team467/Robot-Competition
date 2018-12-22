package frc.robot.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;

public class RobotLogManager {

  private static boolean initialized = false;

  private static void init(String pathToConfig) {
    try {
      File configSourceFile = new File(pathToConfig);
      ConfigurationSource source = new ConfigurationSource(
            new FileInputStream(configSourceFile), configSourceFile);
      ConfigurationFactory configurationFactory = YamlConfigurationFactory.getInstance();
      ConfigurationFactory.setConfigurationFactory(configurationFactory);
      Configuration configuration = configurationFactory.getConfiguration(null, source);
      Configurator.initialize(configuration);
    } catch (IOException e) {
      e.printStackTrace();
    }
    initialized = true;
  }

  private static void init() {
    String path = "./src/main/deploy/log4j2-test.yaml";
    init(path);
  }

  /**
   * Initializes the log system if required, then returns the appropriate class logger.
   * 
   * @param className the class for subsetting the logger
   * @return the logger
   */
  public static Logger getMainLogger(String className) {
    if (!initialized) {
      init();
    }
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
    if (!initialized) {
      init(customLogConfig);
    }
    return LogManager.getLogger(className);
  }

  /**
   * Initializes the log system if required, then returns the appropriate class logger.
   * 
   * @param className the class for subsetting the logger
   * @return the test logger
   */
  public static Logger getTestLogger(String className) {
    if (!initialized) {
      init("./src/main/deploy/log4j2-test.yaml");
    }
    return LogManager.getLogger(className);
  }

  /**
   * Initializes the log system if required, then returns the telemetry.
   * 
   * @return the telemetry logger
   */
  public static Logger telemetryLogger() {
    if (!initialized) {
      init();
    }
    return LogManager.getLogger("telemetry");
  }

}