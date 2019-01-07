package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.RobotMap;
import frc.robot.logging.RobotLogManager;
import frc.robot.simulator.drive.WpiTalonSrx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

public class TalonProxy implements InvocationHandler {

  private static final Logger LOGGER = RobotLogManager.getMainLogger(TalonProxy.class.getName());

  private WPI_TalonSRX physicalMotor;
  private WpiTalonSrx simulatedMotor;

  private final Map<String, Method> physicalMotorMethods = new HashMap<>();
  private final Map<String, Method> simulatedMotorMethods = new HashMap<>();

  /**
   * Loads the talon functions into the proxy and returns either a real or simulated 
   * Talon SRX motor controller.
   */
  public static WpiTalonSrxInterface create(int deviceNumber) {
    WpiTalonSrxInterface talon = (WpiTalonSrxInterface) 
        Proxy.newProxyInstance(WpiTalonSrxInterface.class.getClassLoader(),
            new Class[] { WpiTalonSrxInterface.class },
            new TalonProxy(deviceNumber));
    return talon;
  }

  private TalonProxy(int deviceNumber) {
    //TODO: Fix native libaries with WPI Lib
    // physicalMotor = new WPI_TalonSRX(deviceNumber);
    // addMethods(physicalMotorMethods, physicalMotor.getClass());

    simulatedMotor = new WpiTalonSrx(deviceNumber);
    addMethods(simulatedMotorMethods, simulatedMotor.getClass());
  }
  
  /**
   * Cannot paramerterize class as it's a recursive method to add.
   */
  private void addMethods(Map<String, Method> methodMap, Class classToAdd) {
    if (classToAdd == null) {
      return;
    }
    for (Method method: classToAdd.getDeclaredMethods()) {
      methodMap.put(createMethodSignature(method), method);
    }
    addMethods(methodMap, classToAdd.getSuperclass());
  }

  private String createMethodSignature(Method method) {
    String methodSignature = method.getReturnType() + " " + method.getName() + "(";
    boolean firstParam = true;
    for (Type type : method.getGenericParameterTypes()) {
      if (!firstParam) {
        methodSignature += ", ";
      } else {
        firstParam = false;
      }
      methodSignature += type.getTypeName();
    }
    methodSignature += ")";
    LOGGER.debug("Adding method {} to proxy.", methodSignature);
    return methodSignature;
  } 

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) 
      throws Throwable {
    Object result = null;
    String methodSignature = createMethodSignature(method);
    if (RobotMap.useSimulator) {
      LOGGER.debug("Invoking method {} on simulator", methodSignature);
      Method implementingMethod = simulatedMotorMethods.get(methodSignature);
      result = implementingMethod.invoke(simulatedMotor, args);
    } else {
      result = physicalMotorMethods.get(method.getName()).invoke(physicalMotor, args);
    }
    return result;
  }

}

