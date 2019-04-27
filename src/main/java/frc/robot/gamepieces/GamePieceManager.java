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
        RobotMechs.forEach((s) -> s.stop());
    }
}