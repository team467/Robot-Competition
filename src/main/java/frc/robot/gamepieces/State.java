package frc.robot.gamepieces;

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
