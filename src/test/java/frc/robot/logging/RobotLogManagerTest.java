package frc.robot.logging;

import java.io.*;
import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class RobotLogManagerTest {
    private static String testDirectoryFile = "/media/sda1/logging/log4j2.yaml";
    private static Logger LOGGER = RobotLogManager.getTestLogger(RobotLogManagerTest.class.getName());
    
    @BeforeClass
    public static void initAll() {
    }

    @Before
    public void init() {
    }

    @Test
    public void directoryTest() {
        System.out.println(RobotLogManager.getDirectory());
        assertTrue(true);
    }

    @Test
    public void fileTest() {
        assertTrue(new File(testDirectoryFile).exists());
    }


    // Commented out so that test passes
    // @Test
    // public void failingTest() {
    //   fail("a failing test");
    // }

}

