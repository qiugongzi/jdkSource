


package javax.rmi;

import java.lang.reflect.Method ;

import org.omg.CORBA.INITIALIZE;
import javax.rmi.CORBA.Util;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.net.MalformedURLException ;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.rmi.server.RMIClassLoader;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;


public class PortableRemoteObject {

    private static final javax.rmi.CORBA.PortableRemoteObjectDelegate proDelegate;

    private static final String PortableRemoteObjectClassKey =
            "javax.rmi.CORBA.PortableRemoteObjectClass";

    static {
        proDelegate = (javax.rmi.CORBA.PortableRemoteObjectDelegate)
            createDelegate(PortableRemoteObjectClassKey);
    }


    protected PortableRemoteObject() throws RemoteException {
        if (proDelegate != null) {
            PortableRemoteObject.exportObject((Remote)this);
        }
    }


    public static void exportObject(Remote obj)
        throws RemoteException {

        if (proDelegate != null) {
            proDelegate.exportObject(obj);
        }
    }


    public static Remote toStub (Remote obj)
        throws NoSuchObjectException {

        if (proDelegate != null) {
            return proDelegate.toStub(obj);
        }
        return null;
    }


    public static void unexportObject(Remote obj)
        throws NoSuchObjectException {

        if (proDelegate != null) {
            proDelegate.unexportObject(obj);
        }

    }


    public static java.lang.Object narrow ( java.lang.Object narrowFrom,
                                            java.lang.Class narrowTo)
        throws ClassCastException {

        if (proDelegate != null) {
            return proDelegate.narrow(narrowFrom, narrowTo);
        }
        return null;

    }


    public static void connect (Remote target, Remote source)
        throws RemoteException {

        if (proDelegate != null) {
            proDelegate.connect(target, source);
        }

    }

    private static Object createDelegate(String classKey) {
        String className = (String)
            AccessController.doPrivileged(new GetPropertyAction(classKey));
        if (className == null) {
            Properties props = getORBPropertiesFile();
            if (props != null) {
                className = props.getProperty(classKey);
            }
        }
        if (className == null) {
            return new com.sun.corba.se.impl.javax.rmi.PortableRemoteObject();
        }

        try {
            return (Object) loadDelegateClass(className).newInstance();
        } catch (ClassNotFoundException ex) {
            INITIALIZE exc = new INITIALIZE( "Cannot instantiate " + className);
            exc.initCause( ex ) ;
            throw exc ;
        } catch (Exception ex) {
            INITIALIZE exc = new INITIALIZE( "Error while instantiating" + className);
            exc.initCause( ex ) ;
            throw exc ;
        }

    }

    private static Class loadDelegateClass( String className )  throws ClassNotFoundException
    {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
            }

        try {
            return RMIClassLoader.loadClass(className);
        } catch (MalformedURLException e) {
            String msg = "Could not load " + className + ": " + e.toString();
            ClassNotFoundException exc = new ClassNotFoundException( msg ) ;
            throw exc ;
        }
    }


    private static Properties getORBPropertiesFile () {
        return (Properties) AccessController.doPrivileged(new GetORBPropertiesFileAction());
    }
}

class GetORBPropertiesFileAction implements PrivilegedAction {
    private boolean debug = false ;

    public GetORBPropertiesFileAction () {
    }

    private String getSystemProperty(final String name) {
        String propValue = (String) AccessController.doPrivileged(
            new PrivilegedAction() {
                public java.lang.Object run() {
                    return System.getProperty(name);
                }
            }
        );

        return propValue;
    }

    private void getPropertiesFromFile( Properties props, String fileName )
    {
        try {
            File file = new File( fileName ) ;
            if (!file.exists())
                return ;

            FileInputStream in = new FileInputStream( file ) ;

            try {
                props.load( in ) ;
            } finally {
                in.close() ;
            }
        } catch (Exception exc) {
            if (debug)
                System.out.println( "ORB properties file " + fileName +
                    " not found: " + exc) ;
        }
    }

    public Object run()
    {
        Properties defaults = new Properties() ;

        String javaHome = getSystemProperty( "java.home" ) ;
        String fileName = javaHome + File.separator + "lib" + File.separator +
            "orb.properties" ;

        getPropertiesFromFile( defaults, fileName ) ;

        Properties results = new Properties( defaults ) ;

        String userHome = getSystemProperty( "user.home" ) ;
        fileName = userHome + File.separator + "orb.properties" ;

        getPropertiesFromFile( results, fileName ) ;
        return results ;
    }
}
