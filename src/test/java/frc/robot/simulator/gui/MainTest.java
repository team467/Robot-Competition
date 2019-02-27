package frc.robot.simulator.gui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Don't run GUI as standard part of test.")
public class MainTest {

  @BeforeClass
  public static void initAll() {
  }

  @Before
  public void init() {
  }

  @After
  public void tearDown() {
  }

  @AfterClass
  public static void tearDownAll() {
  }

  @Test
  public void runGui() {
    Map.main(null);
  }

}

