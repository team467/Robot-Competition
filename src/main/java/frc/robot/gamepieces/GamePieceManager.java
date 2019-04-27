package frc.robot.gamepieces;


import frc.robot.gamepieces.mechanisms.GamePieceBase;

import java.util.ArrayList;
import java.util.List;


public class GamePieceManager {

    private final List<GamePieceBase> RobotMechs;

    public GamePieceManager(List<GamePieceBase> mechanisms) {
        RobotMechs = mechanisms;
    }

    public void stop() {
        RobotMechs.forEach((m) -> m.stop());
    }

    public class PeriodicLooper {

        public void startPeriodics() {
            for(GamePieceBase m : RobotMechs) {
                m.periodic();
            }
        }
    }
}