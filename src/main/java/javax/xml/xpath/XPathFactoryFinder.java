

package javax.xml.xpath;

import com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl;
import java.io.File;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;


class XPathFactoryFinder  {
    private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xpath.internal";

    private static final SecuritySupport ss = new SecuritySupport() ;

    private static boolean debug = false;
    static {
        try {
            debug = ss.getSystemProperty("jaxp.debug") != null;
        } catch (Exception unused) {
            debug = false;
        }
    }


    private static final Properties cacheProps = new Properties();


    private volatile static boolean firstTime = true;


    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }


    private final ClassLoader classLoader;


    public XPathFactoryFinder(ClassLoader loader) {
        this.classLoader = loader;
        if( debug ) {
            debugDisplayClassLoader();
        }
    }

    private void debugDisplayClassLoader() {
        try {
            if( classLoader == ss.getContextClassLoader() ) {
                debugPrintln("using thread context class loader ("+classLoader+") for search");
                return;
            }
        } catch( Throwable unused ) {
             }

        if( classLoader==ClassLoader.getSystemClassLoader() ) {
            debugPrintln("using system class loader ("+classLoader+") for search");
            return;
        }

        debugPrintln("using class loader ("+classLoader+") for search");
    }


    public XPathFactory newFactory(String uri) throws XPathFactoryConfigurationException {
        if (uri == null) {
            throw new NullPointerException();
        }
        XPathFactory f = _newFactory(uri);
        if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + uri);
        } else {
            debugPrintln("unable to find a factory for " + uri);
        }
        return f;
    }


    private XPathFactory _newFactory(String uri) throws XPathFactoryConfigurationException {
        XPathFactory xpathFactory = null;

        String propertyName = SERVICE_CLASS.getName() + ":" + uri;

        try {
            debugPrintln("Looking up system property '"+propertyName+"'" );
            String r = ss.getSystemProperty(propertyName);
            if(r!=null) {
                debugPrintln("The value is '"+r+"'");
                xpathFactory = createInstance(r);
                if (xpathFactory != null) {
                    return xpathFactory;
                }
            } else
                debugPrintln("The property is undefined.");
        } catch( Throwable t ) {
            if( debug ) {
                debugPrintln("failed to look up system property '"+propertyName+"'" );
                t.printStackTrace();
            }
        }

        String javah = ss.getSystemProperty( "java.home" );
        String configFile = javah + File.separator +
        "lib" + File.separator + "jaxp.properties";

        try {
            if(firstTime){
                synchronized(cacheProps){
                    if(firstTime){
                        File f=new File( configFile );
                        firstTime = false;
                        if(ss.doesFileExist(f)){
                            debugPrintln("Read properties file " + f);
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            final String factoryClassName = cacheProps.getProperty(propertyName);
            debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties");

            if (factoryClassName != null) {
                xpathFactory = createInstance(factoryClassName);
                if(xpathFactory != null){
                    return xpathFactory;
                }
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
        }

        assert xpathFactory == null;
        xpathFactory = findServiceProvider(uri);

        if (xpathFactory != null) {
            return xpathFactory;
        }

        if(uri.equals(XPathFactory.DEFAULT_OBJECT_MODEL_URI)) {
            debugPrintln("attempting to use the platform default W3C DOM XPath lib");
            return new XPathFactoryImpl();
        }

        debugPrintln("all things were tried, but none was found. bailing out.");
        return null;
    }


    private Class<?> createClass(String className) {
        Class clazz;
        boolean internal = false;
        if (System.getSecurityManager() != null) {
            if (className != null && className.startsWith(DEFAULT_PACKAGE)) {
                internal = true;
            }
        }

        try {
            if (classLoader != null && !internal) {
                    clazz = Class.forName(className, false, classLoader);
            } else {
                    clazz = Class.forName(className);
            }
        } catch (Throwable t) {
            if(debug) {
                t.printStackTrace();
            }
            return null;
        }

        return clazz;
    }


    XPathFactory createInstance(String className)
            throws XPathFactoryConfigurationException
    {
        XPathFactory xPathFactory = null;

        debugPrintln("createInstance(" + className + ")");

        Class<?> clazz = createClass(className);
        if (clazz == null) {
            debugPrintln("failed to getClass(" + className + ")");
            return null;
        }
        debugPrintln("loaded " + className + " from " + which(clazz));

        try {
                xPathFactory = (XPathFactory) clazz.newInstance();
        } catch (ClassCastException classCastException) {
                debugPrintln("could not instantiate " + clazz.getName());
                if (debug) {
                        classCastException.printStackTrace();
                }
                return null;
        } catch (IllegalAccessException illegalAccessException) {
                debugPrintln("could not instantiate " + clazz.getName());
                if (debug) {
                        illegalAccessException.printStackTrace();
                }
                return null;
        } catch (InstantiationException instantiationException) {
                debugPrintln("could not instantiate " + clazz.getName());
                if (debug) {
                        instantiationException.printStackTrace();
                }
                return null;
        }

        return xPathFactory;
    }

    private boolean isObjectModelSupportedBy(final XPathFactory factory,
            final String objectModel,
            AccessControlContext acc) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                    public Boolean run() {
                        return factory.isObjectModelSupported(objectModel);
                    }
                }, acc);
    }


    private XPathFactory findServiceProvider(final String objectModel)
            throws XPathFactoryConfigurationException {

        assert objectModel != null;
        final AccessControlContext acc = AccessController.getContext();
        try {
            return AccessController.doPrivileged(new PrivilegedAction<XPathFactory>() {
                public XPathFactory run() {
                    final ServiceLoader<XPathFactory> loader =
                            ServiceLoader.load(SERVICE_CLASS);
                    for (XPathFactory factory : loader) {
                        if (isObjectModelSupportedBy(factory, objectModel, acc)) {
                            return factory;
                        }
                    }
                    return null; }
            });
        } catch (ServiceConfigurationError error) {
            throw new XPathFactoryConfigurationException(error);
        }
    }

    private static final Class<XPathFactory> SERVICE_CLASS = XPathFactory.class;

    private static String which( Class clazz ) {
        return which( clazz.getName(), clazz.getClassLoader() );
    }


    private static String which(String classname, ClassLoader loader) {

        String classnameAsResource = classname.replace('.', '/') + ".class";

        if( loader==null )  loader = ClassLoader.getSystemClassLoader();

        URL it = ss.getResourceAsURL(loader, classnameAsResource);
        if (it != null) {
            return it.toString();
        } else {
            return null;
        }
    }
}
