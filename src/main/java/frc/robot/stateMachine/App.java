package frc.robot.stateMachine;

public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        StateMachine shooterStateMachine = new StateMachine(ShooterState.Idle);
        // Add new state machine here

        long startTimeMS = System.currentTimeMillis();

        System.out.println("Entering main loop");
        while (System.currentTimeMillis() < startTimeMS + 10*1000) {
            shooterStateMachine.step();
            // Invoke new state machine here
            
            Thread.sleep(100 /*ms*/);
        }
        System.out.println("Leaving main loop");
    }
}
