

package javax.xml.soap;

import java.io.*;
import java.util.Properties;


class FactoryFinder {


    private static Object newInstance(String className,
                                      ClassLoader classLoader)
            throws SOAPException
    {
        try {
            Class spiClass = safeLoadClass(className, classLoader);
            return spiClass.newInstance();

        } catch (ClassNotFoundException x) {
            throw new SOAPException("Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new SOAPException("Provider " + className + " could not be instantiated: " + x, x);
        }
    }


    static Object find(String factoryId)
            throws SOAPException
    {
        return find(factoryId, null, false);
    }


    static Object find(String factoryId, String fallbackClassName)
            throws SOAPException
    {
        return find(factoryId, fallbackClassName, true);
    }


    static Object find(String factoryId, String defaultClassName,
                       boolean tryFallback) throws SOAPException {
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Exception x) {
            throw new SOAPException(x.toString(), x);
        }

        try {
            String systemProp =
                    System.getProperty( factoryId );
            if( systemProp!=null) {
                return newInstance(systemProp, classLoader);
            }
        } catch (SecurityException se) {
        }

        try {
            String javah=System.getProperty( "java.home" );
            String configFile = javah + File.separator +
                    "lib" + File.separator + "jaxm.properties";
            File f=new File( configFile );
            if( f.exists()) {
                Properties props=new Properties();
                props.load( new FileInputStream(f));
                String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryClassName, classLoader);
            }
        } catch(Exception ex ) {
        }

        String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is=null;
            if (classLoader == null) {
                is=ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is=classLoader.getResourceAsStream(serviceId);
            }

            if( is!=null ) {
                BufferedReader rd =
                        new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String factoryClassName = rd.readLine();
                rd.close();

                if (factoryClassName != null &&
                        ! "".equals(factoryClassName)) {
                    return newInstance(factoryClassName, classLoader);
                }
            }
        } catch( Exception ex ) {
        }

        if (!tryFallback)
            return null;

        if (defaultClassName == null) {
            throw new SOAPException(
                    "Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(defaultClassName, classLoader);
    }


    private static Class safeLoadClass(String className,
                                       ClassLoader classLoader)
            throws ClassNotFoundException {
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
            if (isDefaultImplementation(className))
                return Class.forName(className);

            throw se;
        }
    }

    private static boolean isDefaultImplementation(String className) {
        return MessageFactory.DEFAULT_MESSAGE_FACTORY.equals(className) ||
                SOAPFactory.DEFAULT_SOAP_FACTORY.equals(className) ||
                SOAPConnectionFactory.DEFAULT_SOAP_CONNECTION_FACTORY.equals(className) ||
                SAAJMetaFactory.DEFAULT_META_FACTORY_CLASS.equals(className);
    }
}
