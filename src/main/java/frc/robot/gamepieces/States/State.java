package frc.robot.gamepieces.States;

public interface State {
    /**
     * 
     */
    void enter();

    /**
     * 
     */
    State action();

    /**
     * 
     */
    void exit();
}
