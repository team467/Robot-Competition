package frc.robot.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;

public class RobotLogManager {

  private static boolean initialized = false;
  private static String directory = "";

  private static String[] filepaths = { //Filepaths go in this array
    "C:\\Users\\Team467\\Documents\\GitHub\\Robot2019-Competition\\src\\main\\deploy\\log4j2.yaml",
    "C:\\Users\\Team467\\Documents\\GitHub\\Robot2019-Competition\\src\\main\\deploy\\log4j2-test.yaml",
    "./src/main/deploy/log4j2-test.yaml"
  };
  
  private static ArrayList<Integer> getOccurenceIndicies(String s, char c) {
    ArrayList<Integer> inidicies = new ArrayList<Integer>();
    for(int i=0; i<s.length(); i++) {
      if(s.charAt(i) == c) {
        inidicies.add(i);
      }
    }
    return inidicies;
  }

  public static String getDirectory(String s) {
    ArrayList<Integer> indicies = getOccurenceIndicies(s, '\\');
    String directory = s.substring(0,indicies.get(indicies.size()-1));
    return directory;
  }

  private static boolean doesFileExist(String filepath) {
    if(new File(filepath).exists()) {
      directory = getDirectory(filepath);
      return true;
    } else {
      return false;
    }
  }

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
<<<<<<< HEAD
    // String path = "./src/main/deploy/log4j2-test.yaml"; default path (already in array) in case the other filepaths dont load
    for(String path : filepaths) {
      if(doesFileExist(path)) {
        init(path);
        break;
      }
    }
=======
    // String path = "./src/main/deploy/log4j2-test.yaml";
    String path = "/home/lvuser/deploy/log4j2-test.yaml";
    init(path);
>>>>>>> master
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