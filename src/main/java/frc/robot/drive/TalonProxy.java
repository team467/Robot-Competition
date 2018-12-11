package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;
import frc.robot.simulator.drive.WpiTalonSrx;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TalonProxy implements InvocationHandler {

  private WPI_TalonSRX physicalMotor;
  private WpiTalonSrx simulatedMotor;

  private final Map<String, Method> physicalMotorMethods = new HashMap<>();
  private final Map<String, Method> simulatedMotorMethods = new HashMap<>();

  public static WpiTalonSrxInterface create(int deviceNumber) {
    WpiTalonSrxInterface talon = (WpiTalonSrxInterface) 
        Proxy.newProxyInstance(WpiTalonSrxInterface.class.getClassLoader(),
            new Class[] { WpiTalonSrxInterface.class },
            new TalonProxy(deviceNumber));
    return talon;
  }

  public TalonProxy(int deviceNumber) {
    //TODO: Fix native libaries with WPI Lib
    // physicalMotor = new WPI_TalonSRX(deviceNumber);
    // addMethods(physicalMotorMethods, physicalMotor.getClass());

    simulatedMotor = new WpiTalonSrx(deviceNumber);
    addMethods(simulatedMotorMethods, simulatedMotor.getClass());
  }

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
    // System.err.println(methodSignature);
    return methodSignature;
  } 

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) 
      throws Throwable {
    Object result = null;
    String methodSignature = createMethodSignature(method);
    if (RobotMap.useSimulator) {
      // System.err.println("Invoking method " + methodSignature + " on simulator");
      Method implementingMethod = simulatedMotorMethods.get(methodSignature);
      result = implementingMethod.invoke(simulatedMotor, args);
    } else {
      result = physicalMotorMethods.get(method.getName()).invoke(physicalMotor, args);
    }

    // LOGGER.info("Executing {} finished in {} ns", method.getName(), 
    //   elapsed);

    return result;
  }

}

