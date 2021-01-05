


package javax.rmi.CORBA;

import java.rmi.RemoteException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ObjectImpl;

import javax.rmi.CORBA.Tie;
import java.rmi.Remote;
import java.io.File;
import java.io.FileInputStream;
import java.io.SerializablePermission;
import java.net.MalformedURLException ;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.rmi.server.RMIClassLoader;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;


public class Util {

    private static final javax.rmi.CORBA.UtilDelegate utilDelegate;
    private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";

    private static final String ALLOW_CREATEVALUEHANDLER_PROP = "jdk.rmi.CORBA.allowCustomValueHandler";
    private static boolean allowCustomValueHandler;

    static {
        utilDelegate = (javax.rmi.CORBA.UtilDelegate)createDelegate(UtilClassKey);
        allowCustomValueHandler = readAllowCustomValueHandlerProperty();
    }

    private static boolean readAllowCustomValueHandlerProperty () {
       return AccessController
        .doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.getBoolean(ALLOW_CREATEVALUEHANDLER_PROP);
            }
        });
    }

    private Util(){}


    public static RemoteException mapSystemException(SystemException ex) {

        if (utilDelegate != null) {
            return utilDelegate.mapSystemException(ex);
        }
        return null;
    }


    public static void writeAny(OutputStream out, Object obj) {

        if (utilDelegate != null) {
            utilDelegate.writeAny(out, obj);
        }
    }


    public static Object readAny(InputStream in) {

        if (utilDelegate != null) {
            return utilDelegate.readAny(in);
        }
        return null;
    }


    public static void writeRemoteObject(OutputStream out,
                                         java.lang.Object obj) {

        if (utilDelegate != null) {
            utilDelegate.writeRemoteObject(out, obj);
        }

    }


    public static void writeAbstractObject(OutputStream out,
                                           java.lang.Object obj) {

        if (utilDelegate != null) {
            utilDelegate.writeAbstractObject(out, obj);
        }
    }


    public static void registerTarget(javax.rmi.CORBA.Tie tie,
                                      java.rmi.Remote target) {

        if (utilDelegate != null) {
            utilDelegate.registerTarget(tie, target);
        }

    }


    public static void unexportObject(java.rmi.Remote target)
        throws java.rmi.NoSuchObjectException
    {

        if (utilDelegate != null) {
            utilDelegate.unexportObject(target);
        }

    }


    public static Tie getTie (Remote target) {

        if (utilDelegate != null) {
            return utilDelegate.getTie(target);
        }
        return null;
    }



    public static ValueHandler createValueHandler() {

        isCustomSerializationPermitted();

        if (utilDelegate != null) {
            return utilDelegate.createValueHandler();
        }
        return null;
    }


    public static String getCodebase(java.lang.Class clz) {
        if (utilDelegate != null) {
            return utilDelegate.getCodebase(clz);
        }
        return null;
    }


    public static Class loadClass(String className,
                                  String remoteCodebase,
                                  ClassLoader loader)
        throws ClassNotFoundException {
        if (utilDelegate != null) {
            return utilDelegate.loadClass(className,remoteCodebase,loader);
        }
        return null ;
    }



    public static boolean isLocal(Stub stub) throws RemoteException {

        if (utilDelegate != null) {
            return utilDelegate.isLocal(stub);
        }

        return false;
    }


    public static RemoteException wrapException(Throwable orig) {

        if (utilDelegate != null) {
            return utilDelegate.wrapException(orig);
        }

        return null;
    }


    public static Object[] copyObjects (Object[] obj, ORB orb)
        throws RemoteException {

        if (utilDelegate != null) {
            return utilDelegate.copyObjects(obj, orb);
        }

        return null;
    }


    public static Object copyObject (Object obj, ORB orb)
        throws RemoteException {

        if (utilDelegate != null) {
            return utilDelegate.copyObject(obj, orb);
        }
        return null;
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
            return new com.sun.corba.se.impl.javax.rmi.CORBA.Util();
        }

        try {
            return loadDelegateClass(className).newInstance();
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

    private static Properties getORBPropertiesFile ()
    {
        return (Properties) AccessController.doPrivileged(
            new GetORBPropertiesFileAction());
    }

    private static void isCustomSerializationPermitted() {
        SecurityManager sm = System.getSecurityManager();
        if (!allowCustomValueHandler) {
            if ( sm != null) {
                sm.checkPermission(new SerializablePermission(
                        "enableCustomValueHandler"));
            }
        }
    }
}
