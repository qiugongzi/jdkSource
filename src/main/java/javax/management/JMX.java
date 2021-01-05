

package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import sun.reflect.misc.ReflectUtil;


public class JMX {

    static final JMX proof = new JMX();

    private JMX() {}


    public static final String DEFAULT_VALUE_FIELD = "defaultValue";


    public static final String IMMUTABLE_INFO_FIELD = "immutableInfo";


    public static final String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";


    public static final String LEGAL_VALUES_FIELD = "legalValues";


    public static final String MAX_VALUE_FIELD = "maxValue";


    public static final String MIN_VALUE_FIELD = "minValue";


    public static final String MXBEAN_FIELD = "mxbean";


    public static final String OPEN_TYPE_FIELD = "openType";


    public static final String ORIGINAL_TYPE_FIELD = "originalType";


    public static <T> T newMBeanProxy(MBeanServerConnection connection,
                                      ObjectName objectName,
                                      Class<T> interfaceClass) {
        return newMBeanProxy(connection, objectName, interfaceClass, false);
    }


    public static <T> T newMBeanProxy(MBeanServerConnection connection,
                                      ObjectName objectName,
                                      Class<T> interfaceClass,
                                      boolean notificationEmitter) {
        return createProxy(connection, objectName, interfaceClass, notificationEmitter, false);
    }


    public static <T> T newMXBeanProxy(MBeanServerConnection connection,
                                       ObjectName objectName,
                                       Class<T> interfaceClass) {
        return newMXBeanProxy(connection, objectName, interfaceClass, false);
    }


    public static <T> T newMXBeanProxy(MBeanServerConnection connection,
                                       ObjectName objectName,
                                       Class<T> interfaceClass,
                                       boolean notificationEmitter) {
        return createProxy(connection, objectName, interfaceClass, notificationEmitter, true);
    }


    public static boolean isMXBeanInterface(Class<?> interfaceClass) {
        if (!interfaceClass.isInterface())
            return false;
        if (!Modifier.isPublic(interfaceClass.getModifiers()) &&
            !Introspector.ALLOW_NONPUBLIC_MBEAN) {
            return false;
        }
        MXBean a = interfaceClass.getAnnotation(MXBean.class);
        if (a != null)
            return a.value();
        return interfaceClass.getName().endsWith("MXBean");
        }


    private static <T> T createProxy(MBeanServerConnection connection,
                                     ObjectName objectName,
                                     Class<T> interfaceClass,
                                     boolean notificationEmitter,
                                     boolean isMXBean) {

        try {
            if (isMXBean) {
                Introspector.testComplianceMXBeanInterface(interfaceClass);
            } else {
                Introspector.testComplianceMBeanInterface(interfaceClass);
            }
        } catch (NotCompliantMBeanException e) {
            throw new IllegalArgumentException(e);
        }

        InvocationHandler handler = new MBeanServerInvocationHandler(
                connection, objectName, isMXBean);
        final Class<?>[] interfaces;
        if (notificationEmitter) {
            interfaces =
                new Class<?>[] {interfaceClass, NotificationEmitter.class};
        } else
            interfaces = new Class<?>[] {interfaceClass};

        Object proxy = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                interfaces,
                handler);
        return interfaceClass.cast(proxy);
    }
}
