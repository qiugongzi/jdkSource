

package javax.xml.stream;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;


class FactoryFinder {
    private static final String DEFAULT_PACKAGE = "com.sun.xml.internal.";


    private static boolean debug = false;


    final private static Properties cacheProps = new Properties();


    private static volatile boolean firstTime = true;


    final private static SecuritySupport ss = new SecuritySupport();

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


    static private Class getProviderClass(String className, ClassLoader cl,
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
        throws FactoryConfigurationError
    {
        return newInstance(type, className, cl, doFallback, false);
    }


    static <T> T newInstance(Class<T> type, String className, ClassLoader cl,
                              boolean doFallback, boolean useBSClsLoader)
        throws FactoryConfigurationError
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
            throw new FactoryConfigurationError(
                "Provider " + className + " not found", x);
        }
        catch (Exception x) {
            throw new FactoryConfigurationError(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }


    static <T> T find(Class<T> type, String fallbackClassName)
        throws FactoryConfigurationError
    {
        return find(type, type.getName(), null, fallbackClassName);
    }


    static <T> T find(Class<T> type, String factoryId, ClassLoader cl, String fallbackClassName)
        throws FactoryConfigurationError
    {
        dPrint("find factoryId =" + factoryId);

        try {

            final String systemProp;
            if (type.getName().equals(factoryId)) {
                systemProp = ss.getSystemProperty(factoryId);
            } else {
                systemProp = System.getProperty(factoryId);
            }
            if (systemProp != null) {
                dPrint("found system property, value=" + systemProp);
                return newInstance(type, systemProp, cl, true);
            }
        }
        catch (SecurityException se) {
            throw new FactoryConfigurationError(
                    "Failed to read factoryId '" + factoryId + "'", se);
        }

        String configFile = null;
        try {
            if (firstTime) {
                synchronized (cacheProps) {
                    if (firstTime) {
                        configFile = ss.getSystemProperty("java.home") + File.separator +
                            "lib" + File.separator + "stax.properties";
                        File f = new File(configFile);
                        firstTime = false;
                        if (ss.doesFileExist(f)) {
                            dPrint("Read properties file "+f);
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                        else {
                            configFile = ss.getSystemProperty("java.home") + File.separator +
                                "lib" + File.separator + "jaxp.properties";
                            f = new File(configFile);
                            if (ss.doesFileExist(f)) {
                                dPrint("Read properties file "+f);
                                cacheProps.load(ss.getFileInputStream(f));
                            }
                        }
                    }
                }
            }
            final String factoryClassName = cacheProps.getProperty(factoryId);

            if (factoryClassName != null) {
                dPrint("found in " + configFile + " value=" + factoryClassName);
                return newInstance(type, factoryClassName, cl, true);
            }
        }
        catch (Exception ex) {
            if (debug) ex.printStackTrace();
        }

        if (type.getName().equals(factoryId)) {
            final T provider = findServiceProvider(type, cl);
            if (provider != null) {
                return provider;
            }
        } else {
            assert fallbackClassName == null;
        }
        if (fallbackClassName == null) {
            throw new FactoryConfigurationError(
                "Provider for " + factoryId + " cannot be found", null);
        }

        dPrint("loaded from fallback value: " + fallbackClassName);
        return newInstance(type, fallbackClassName, cl, true);
    }


    private static <T> T findServiceProvider(final Class<T> type, final ClassLoader cl) {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {
                @Override
                public T run() {
                    final ServiceLoader<T> serviceLoader;
                    if (cl == null) {
                        serviceLoader = ServiceLoader.load(type);
                    } else {
                        serviceLoader = ServiceLoader.load(type, cl);
                    }
                    final Iterator<T> iterator = serviceLoader.iterator();
                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        return null;
                    }
                }
            });
        } catch(ServiceConfigurationError e) {
            final RuntimeException x = new RuntimeException(
                    "Provider for " + type + " cannot be created", e);
            final FactoryConfigurationError error =
                    new FactoryConfigurationError(x, x.getMessage());
            throw error;
          }
      }

}
