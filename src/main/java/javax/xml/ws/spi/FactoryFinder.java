

package javax.xml.ws.spi;

import java.io.*;

import java.util.Properties;
import javax.xml.ws.WebServiceException;

class FactoryFinder {


    private static Object newInstance(String className,
                                      ClassLoader classLoader)
    {
        try {
            Class spiClass = safeLoadClass(className, classLoader);
            return spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new WebServiceException(
                "Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new WebServiceException(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }


    static Object find(String factoryId, String fallbackClassName)
    {
        if (isOsgi()) {
            return lookupUsingOSGiServiceLoader(factoryId);
        }
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Exception x) {
            throw new WebServiceException(x.toString(), x);
        }

        String serviceId = "META-INF/services/" + factoryId;
        BufferedReader rd = null;
        try {
            InputStream is;
            if (classLoader == null) {
                is=ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is=classLoader.getResourceAsStream(serviceId);
            }

            if( is!=null ) {
                rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String factoryClassName = rd.readLine();

                if (factoryClassName != null &&
                    ! "".equals(factoryClassName)) {
                    return newInstance(factoryClassName, classLoader);
                }
            }
        } catch( Exception ignored) {
        } finally {
            close(rd);
        }


        FileInputStream inStream = null;
        try {
            String javah=System.getProperty( "java.home" );
            String configFile = javah + File.separator +
                "lib" + File.separator + "jaxws.properties";
            File f=new File( configFile );
            if( f.exists()) {
                Properties props=new Properties();
                inStream = new FileInputStream(f);
                props.load(inStream);
                String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryClassName, classLoader);
            }
        } catch(Exception ignored) {
        } finally {
            close(inStream);
        }

        try {
            String systemProp =
                System.getProperty( factoryId );
            if( systemProp!=null) {
                return newInstance(systemProp, classLoader);
            }
        } catch (SecurityException ignored) {
        }

        if (fallbackClassName == null) {
            throw new WebServiceException(
                "Provider for " + factoryId + " cannot be found", null);
        }

        return newInstance(fallbackClassName, classLoader);
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }



    private static Class safeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            SecurityManager s = System.getSecurityManager();
            if (s != null) {
                int i = className.lastIndexOf('.');
                if (i != -1) {
                    s.checkPackageAccess(className.substring(0, i));
                }
            }

            if (classLoader == null)
                return Class.forName(className);
            else
                return classLoader.loadClass(className);
        } catch (SecurityException se) {
            if (Provider.DEFAULT_JAXWSPROVIDER.equals(className))
                return Class.forName(className);
            throw se;
        }
    }

    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

    private static boolean isOsgi() {
        try {
            Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    private static Object lookupUsingOSGiServiceLoader(String factoryId) {
        try {
            Class serviceClass = Class.forName(factoryId);
            Class[] args = new Class[]{serviceClass};
            Class target = Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            java.lang.reflect.Method m = target.getMethod("lookupProviderInstances", Class.class);
            java.util.Iterator iter = ((Iterable) m.invoke(null, (Object[]) args)).iterator();
            return iter.hasNext() ? iter.next() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

}
