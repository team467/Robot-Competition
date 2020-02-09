package frc.robot.gamepieces.States;

import org.apache.logging.log4j.Logger;

import frc.robot.logging.RobotLogManager;

public class StateMachine {
    
  private static final Logger LOGGER = RobotLogManager.getMainLogger(StateMachine.class.getName());

    private State currentState;

    public StateMachine(State initialState) {
        currentState = initialState;
        currentState.enter();
    }

    public void step() {
        State nextState = currentState.action();

        if (nextState != currentState) {
            LOGGER.error("Leaving state " + currentState.toString());
            currentState.exit();

            currentState = nextState;

            LOGGER.error("Entering state " + currentState.toString());
            currentState.enter();
        }
    }

    public State getCurrentState() {
        return currentState;
    }
}
