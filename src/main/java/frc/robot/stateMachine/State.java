package frc.robot.stateMachine;;

interface State {
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
