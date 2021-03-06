

package javax.xml.datatype;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;


class FactoryFinder {
    private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";


    private static boolean debug = false;


    private final static Properties cacheProps = new Properties();


    private static volatile boolean firstTime = true;


    private final static SecuritySupport ss = new SecuritySupport();

    static {
        try {
            String val = ss.getSystemProperty("jaxp.debug");
            debug = val != null && !"false".equals(val);
        }
        catch (SecurityException se) {
            debug = false;
        }
    }

    private static void dPrint(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }


    static private Class<?> getProviderClass(String className, ClassLoader cl,
            boolean doFallback, boolean useBSClsLoader) throws ClassNotFoundException
    {
        try {
            if (cl == null) {
                if (useBSClsLoader) {
                    return Class.forName(className, false, FactoryFinder.class.getClassLoader());
                } else {
                    cl = ss.getContextClassLoader();
                    if (cl == null) {
                        throw new ClassNotFoundException();
                    }
                    else {
                        return Class.forName(className, false, cl);
                    }
                }
            }
            else {
                return Class.forName(className, false, cl);
            }
        }
        catch (ClassNotFoundException e1) {
            if (doFallback) {
                return Class.forName(className, false, FactoryFinder.class.getClassLoader());
            }
            else {
                throw e1;
            }
        }
    }


    static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback)
        throws  DatatypeConfigurationException
    {
        return newInstance(type, className, cl, doFallback, false);
    }


    static <T> T newInstance(Class<T> type, String className, ClassLoader cl,
            boolean doFallback, boolean useBSClsLoader)
        throws DatatypeConfigurationException
    {
        assert type != null;

        if (System.getSecurityManager() != null) {
            if (className != null && className.startsWith(DEFAULT_PACKAGE)) {
                cl = null;
                useBSClsLoader = true;
            }
        }

        try {
            Class<?> providerClass = getProviderClass(className, cl, doFallback, useBSClsLoader);
            if (!type.isAssignableFrom(providerClass)) {
                throw new ClassCastException(className + " cannot be cast to " + type.getName());
            }
            Object instance = providerClass.newInstance();
            if (debug) {    dPrint("created new instance of " + providerClass +
                       " using ClassLoader: " + cl);
            }
            return type.cast(instance);
        }
        catch (ClassNotFoundException x) {
            throw new DatatypeConfigurationException(
                "Provider " + className + " not found", x);
        }
        catch (Exception x) {
            throw new DatatypeConfigurationException(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }


    static <T> T find(Class<T> type, String fallbackClassName)
        throws DatatypeConfigurationException
    {
        final String factoryId = type.getName();
        dPrint("find factoryId =" + factoryId);

        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                dPrint("found system property, value=" + systemProp);
                return newInstance(type, systemProp, null, true);
            }
        }
        catch (SecurityException se) {
            if (debug) se.printStackTrace();
        }

        try {
            if (firstTime) {
                synchronized (cacheProps) {
                    if (firstTime) {
                        String configFile = ss.getSystemProperty("java.home") + File.separator +
                            "lib" + File.separator + "jaxp.properties";
                        File f = new File(configFile);
                        firstTime = false;
                        if (ss.doesFileExist(f)) {
                            dPrint("Read properties file "+f);
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            final String factoryClassName = cacheProps.getProperty(factoryId);

            if (factoryClassName != null) {
                dPrint("found in $java.home/jaxp.properties, value=" + factoryClassName);
                return newInstance(type, factoryClassName, null, true);
            }
        }
        catch (Exception ex) {
            if (debug) ex.printStackTrace();
        }

        final T provider = findServiceProvider(type);
        if (provider != null) {
            return provider;
        }
        if (fallbackClassName == null) {
            throw new DatatypeConfigurationException(
                "Provider for " + factoryId + " cannot be found");
        }

        dPrint("loaded from fallback value: " + fallbackClassName);
        return newInstance(type, fallbackClassName, null, true);
    }


    private static <T> T findServiceProvider(final Class<T> type)
            throws DatatypeConfigurationException
    {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {
                public T run() {
                    final ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
                    final Iterator<T> iterator = serviceLoader.iterator();
                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        return null;
                    }
                }
            });
        } catch(ServiceConfigurationError e) {
            final DatatypeConfigurationException error =
                    new DatatypeConfigurationException(
                        "Provider for " + type + " cannot be found", e);
            throw error;
        }
    }

}
