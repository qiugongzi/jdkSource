


package javax.rmi.CORBA;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.SystemException;


public interface UtilDelegate {


    RemoteException mapSystemException(SystemException ex);


    void writeAny(OutputStream out, Object obj);


    java.lang.Object readAny(InputStream in);


    void writeRemoteObject(OutputStream out, Object obj);


    void writeAbstractObject(OutputStream out, Object obj);


    void registerTarget(Tie tie, Remote target);


    void unexportObject(Remote target) throws java.rmi.NoSuchObjectException;


    Tie getTie(Remote target);


    ValueHandler createValueHandler();


    String getCodebase(Class clz);


    Class loadClass(String className, String remoteCodebase, ClassLoader loader)
        throws ClassNotFoundException;


    boolean isLocal(Stub stub) throws RemoteException;


    RemoteException wrapException(Throwable obj);


    Object copyObject(Object obj, ORB orb) throws RemoteException;


    Object[] copyObjects(Object[] obj, ORB orb) throws RemoteException;

}
