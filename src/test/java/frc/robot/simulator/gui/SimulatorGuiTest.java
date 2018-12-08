package frc.robot.simulator.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SimulatorGuiTest {

  @BeforeAll
  static void initAll() {
  }

  @BeforeEach
  void init() {
  }

  @AfterEach
  void tearDown() {
  }

  @AfterAll
  static void tearDownAll() {
  }

  @Test
  @DisplayName("Current just for running the GUI.")
  void testSimulatorGui() {
    Map.main(null);
    assert(true);
  }
  
}