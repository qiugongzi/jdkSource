
package java.beans;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.ConstructorFinder;
import com.sun.beans.finder.MethodFinder;
import sun.reflect.misc.MethodUtil;


public class Statement {

    private static Object[] emptyArray = new Object[]{};

    static ExceptionListener defaultExceptionListener = new ExceptionListener() {
        public void exceptionThrown(Exception e) {
            System.err.println(e);
            System.err.println("Continuing ...");
        }
    };

    private final AccessControlContext acc = AccessController.getContext();
    private final Object target;
    private final String methodName;
    private final Object[] arguments;
    ClassLoader loader;


    @ConstructorProperties({"target", "methodName", "arguments"})
    public Statement(Object target, String methodName, Object[] arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = (arguments == null) ? emptyArray : arguments.clone();
    }


    public Object getTarget() {
        return target;
    }


    public String getMethodName() {
        return methodName;
    }


    public Object[] getArguments() {
        return this.arguments.clone();
    }


    public void execute() throws Exception {
        invoke();
    }

    Object invoke() throws Exception {
        AccessControlContext acc = this.acc;
        if ((acc == null) && (System.getSecurityManager() != null)) {
            throw new SecurityException("AccessControlContext is not set");
        }
        try {
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            return invokeInternal();
                        }
                    },
                    acc
            );
        }
        catch (PrivilegedActionException exception) {
            throw exception.getException();
        }
    }

    private Object invokeInternal() throws Exception {
        Object target = getTarget();
        String methodName = getMethodName();

        if (target == null || methodName == null) {
            throw new NullPointerException((target == null ? "target" :
                                            "methodName") + " should not be null");
        }

        Object[] arguments = getArguments();
        if (arguments == null) {
            arguments = emptyArray;
        }
        if (target == Class.class && methodName.equals("forName")) {
            return ClassFinder.resolveClass((String)arguments[0], this.loader);
        }
        Class<?>[] argClasses = new Class<?>[arguments.length];
        for(int i = 0; i < arguments.length; i++) {
            argClasses[i] = (arguments[i] == null) ? null : arguments[i].getClass();
        }

        AccessibleObject m = null;
        if (target instanceof Class) {

            if (methodName.equals("new")) {
                methodName = "newInstance";
            }
            if (methodName.equals("newInstance") && ((Class)target).isArray()) {
                Object result = Array.newInstance(((Class)target).getComponentType(), arguments.length);
                for(int i = 0; i < arguments.length; i++) {
                    Array.set(result, i, arguments[i]);
                }
                return result;
            }
            if (methodName.equals("newInstance") && arguments.length != 0) {
                if (target == Character.class && arguments.length == 1 &&
                    argClasses[0] == String.class) {
                    return new Character(((String)arguments[0]).charAt(0));
                }
                try {
                    m = ConstructorFinder.findConstructor((Class)target, argClasses);
                }
                catch (NoSuchMethodException exception) {
                    m = null;
                }
            }
            if (m == null && target != Class.class) {
                m = getMethod((Class)target, methodName, argClasses);
            }
            if (m == null) {
                m = getMethod(Class.class, methodName, argClasses);
            }
        }
        else {

            if (target.getClass().isArray() &&
                (methodName.equals("set") || methodName.equals("get"))) {
                int index = ((Integer)arguments[0]).intValue();
                if (methodName.equals("get")) {
                    return Array.get(target, index);
                }
                else {
                    Array.set(target, index, arguments[1]);
                    return null;
                }
            }
            m = getMethod(target.getClass(), methodName, argClasses);
        }
        if (m != null) {
            try {
                if (m instanceof Method) {
                    return MethodUtil.invoke((Method)m, target, arguments);
                }
                else {
                    return ((Constructor)m).newInstance(arguments);
                }
            }
            catch (IllegalAccessException iae) {
                throw new Exception("Statement cannot invoke: " +
                                    methodName + " on " + target.getClass(),
                                    iae);
            }
            catch (InvocationTargetException ite) {
                Throwable te = ite.getTargetException();
                if (te instanceof Exception) {
                    throw (Exception)te;
                }
                else {
                    throw ite;
                }
            }
        }
        throw new NoSuchMethodException(toString());
    }

    String instanceName(Object instance) {
        if (instance == null) {
            return "null";
        } else if (instance.getClass() == String.class) {
            return "\""+(String)instance + "\"";
        } else {
            return NameGenerator.unqualifiedClassName(instance.getClass());
        }
    }


    public String toString() {
        Object target = getTarget();
        String methodName = getMethodName();
        Object[] arguments = getArguments();
        if (arguments == null) {
            arguments = emptyArray;
        }
        StringBuffer result = new StringBuffer(instanceName(target) + "." + methodName + "(");
        int n = arguments.length;
        for(int i = 0; i < n; i++) {
            result.append(instanceName(arguments[i]));
            if (i != n -1) {
                result.append(", ");
            }
        }
        result.append(");");
        return result.toString();
    }

    static Method getMethod(Class<?> type, String name, Class<?>... args) {
        try {
            return MethodFinder.findMethod(type, name, args);
        }
        catch (NoSuchMethodException exception) {
            return null;
        }
    }
}
