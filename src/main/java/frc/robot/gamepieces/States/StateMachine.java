package frc.robot.gamepieces.States;

class StateMachine {
    private State currentState;

    public StateMachine(State initialState) {
        currentState = initialState;
        currentState.enter();
    }

    void step() {
        State nextState = currentState.action();

        if (nextState != currentState) {
            System.out.println("Leaving state " + currentState.toString());
            currentState.exit();

            currentState = nextState;

            System.out.println("Entering state " + currentState.toString());
            currentState.enter();
        }
    }

    State getCurrentState() {
        return currentState;
    }
}
