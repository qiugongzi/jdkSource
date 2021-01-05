

package javax.management.loading;

import static com.sun.jmx.defaults.JmxProperties.MBEANSERVER_LOGGER;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;


@Deprecated
public class DefaultLoaderRepository {


    public static Class<?> loadClass(String className)
        throws ClassNotFoundException {
        MBEANSERVER_LOGGER.logp(Level.FINEST,
                DefaultLoaderRepository.class.getName(),
                "loadClass", className);
        return load(null, className);
    }


    public static Class<?> loadClassWithout(ClassLoader loader,
                                         String className)
        throws ClassNotFoundException {
        MBEANSERVER_LOGGER.logp(Level.FINEST,
                DefaultLoaderRepository.class.getName(),
                "loadClassWithout", className);
        return load(loader, className);
    }

    private static Class<?> load(ClassLoader without, String className)
            throws ClassNotFoundException {
        final List<MBeanServer> mbsList = MBeanServerFactory.findMBeanServer(null);

        for (MBeanServer mbs : mbsList) {
            ClassLoaderRepository clr = mbs.getClassLoaderRepository();
            try {
                return clr.loadClassWithout(without, className);
            } catch (ClassNotFoundException e) {
                }
        }
        throw new ClassNotFoundException(className);
    }

 }
