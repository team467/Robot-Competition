package frc.robot.gamepieces.mechanisms;

import edu.wpi.first.wpilibj.Compressor;

/**
 * infrastructure has limited auxiliary control, such as the compressor
 */
public class MechInfrastruct extends GamePieceBase implements GamePiece {

    private MechInfrastruct instance = null;
    private Compressor compressor;

    private MechInfrastruct(){
        super("Telemetry", "MechInfrastruct");
        compressor = new Compressor();
    }

    public MechInfrastruct getInstance(){
        if(instance == null){
            instance = new MechInfrastruct();
        } 
        return instance;
    }

    public void stopCompressor() {
        compressor.stop();
    }

    public void startCompressor() {
        compressor.start();
    }
    
    @Override
    public boolean systemCheck() {
        return false;
    }

    @Override
    public void periodic() {

    }

    @Override
    public void stop() {

    }
}