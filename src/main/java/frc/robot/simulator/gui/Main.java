package frc.robot.simulator.gui;

import org.opencv.core.Core;

class Main {

  static {
//   System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
  }

  public static void main(String[] args) {
    // System.out.println(PlatformDetector.getPlatform().name());
    // System.out.println(PlatformDetector.getPlatform().defaultJavaLocation());
    // System.out.println(PlatformDetector.getPlatform().defaultJniLocation());
    System.out.println(Core.VERSION);
    System.out.println(Core.NATIVE_LIBRARY_NAME);
    
    Map.main(args);

  }

}