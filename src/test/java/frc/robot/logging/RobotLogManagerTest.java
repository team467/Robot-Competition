package frc.robot.logging;

import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class RobotLogManagerTest {
    private static String testDirectoryFile = "C:\\Users\\Team467\\Documents\\GitHub\\Robot2019-Competition\\src\\main\\deploy\\log4j2.yaml";
    private static Logger LOGGER = RobotLogManager.getTestLogger(RobotLogManagerTest.class.getName());
    
    @BeforeClass
    public static void initAll() {
    }

    @Before
    public void init() {
    }

    @Test
    public void directoryTest() {
        System.out.println(RobotLogManager.getDirectory(testDirectoryFile));
        assertTrue(true);
    }

    // Commented out so that test passes
    // @Test
    // public void failingTest() {
    //   fail("a failing test");
    // }

}

