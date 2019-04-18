package frc.robot.gamepieces.statemachines;

public class SuperStructSM {
    public enum NeededAction {
        STAY,
        NEED_GOAL,
        NEED_MANUAL

    }

    public enum SuperSystemState{
        STOPPING,
        MOVING,
        MANUAL

    }

    private SuperSystemState systemState = SuperSystemState.STOPPING;

    
}