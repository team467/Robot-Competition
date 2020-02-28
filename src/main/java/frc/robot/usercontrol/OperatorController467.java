package frc.robot.usercontrol;

import static org.apache.logging.log4j.util.Unbox.box;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.utilities.LookUpTable;
import frc.robot.utilities.MathUtils;
import java.lang.Math;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

public class OperatorController467 extends GenericHID {

    private static final Logger LOGGER = RobotLogManager.getMainLogger(OperatorController467.class.getName());
    private GenericHID controllerHID;

    public HashMap<Integer, Boolean> buttonDown = new HashMap<Integer, Boolean>();
    public HashMap<Integer, Boolean> previousButtonDown = new HashMap<Integer, Boolean>();

    public HashMap<Integer, Double> axes = new HashMap<Integer, Double>();

    private int buttonTotal = 0;
    private int axesTotal = 0;

    /**
     * Create a new controller on a given port with a given total of buttons and axes.
     *
     * @param port - Port the controller is on
     * @param buttonTotal - Total amount of buttons on the controller
     * @param axesTotal - Total amount of axes on the controller
     */
    public OperatorController467(final int port) {
        super(port);
    }

    /**
     * Returns the raw joystick object inside Joystick467.
     *
     * @return
     */
    public GenericHID getController() {
        // TODO: Get the joystick
        return controllerHID;
    }

    private void readButtons() {
        for (int b = 1; b <= buttonTotal; b++) {
            previousButtonDown.put(b, buttonDown.get(b));
            LOGGER.error("Button {}", b);
            buttonDown.put(b, controllerHID.getRawButton(b));
        }
    }

    private void readAxes() {
        for (int axis = 1; axis <= axesTotal; axis++) {
            axes.put(axis, controllerHID.getRawAxis(axis));
        }
    }

    /**
     * Read all inputs from the underlying joystick object.
     */
    public void read() {
        readButtons();
        readAxes();
    }

    public void logIdentity() {
        LOGGER.debug("Controller Port: {}", box(this.getPort()));
    }

    /**
     * Check if a specific button is being held down. Ignores first button press,
     * but the robot loops too quickly for this to matter.
     *
     * @return
     */
    public boolean down(final int b) {
        boolean result = false;
        if (b <= buttonTotal) {
            result = buttonDown.get(b);
            LOGGER.debug("Button: {} = {}", b, box(result));
        }
        return result;
    }

    /**
     * Check if a specific button has just been pressed. (Ignores holding.)
     *
     * @return
     */
    public boolean pressed(final int b) {
        final boolean result = buttonDown.get(b) && !previousButtonDown.get(b);
        LOGGER.debug("Button Pressed: {} = {} ", b, box(result));
        return result;
    }

    /**
     * Check if a specific button has just been released.
     *
     * @return
     */
    public boolean buttonReleased(final int b) {
        final boolean result = !buttonDown.get(b) && previousButtonDown.get(b);
        LOGGER.debug("Button Released: {} = {} ", b, box(result));
        return result;
    }

    @Override
    public double getX(Hand hand) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getY(Hand hand) {
        // TODO Auto-generated method stub
        return 0;
    }
}
