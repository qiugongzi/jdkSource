


package com.sun.corba.se.impl.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputValidation;
import java.io.NotActiveException;
import java.io.InvalidObjectException;
import java.io.InvalidClassException;
import java.io.DataInputStream;
import java.io.OptionalDataException;
import java.io.WriteAbortedException;
import java.io.Externalizable;
import java.io.EOFException;
import java.lang.reflect.*;
import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;

import sun.corba.Bridge ;

import java.security.AccessController ;
import java.security.PrivilegedAction ;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.util.Utility;

import org.omg.CORBA.portable.ValueInputStream;

import org.omg.CORBA.ValueMember;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;

import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;

import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import java.security.*;
import java.util.*;

import com.sun.corba.se.impl.orbutil.ObjectUtility ;
import com.sun.corba.se.impl.logging.OMGSystemException ;
import com.sun.corba.se.impl.logging.UtilSystemException ;

import com.sun.corba.se.spi.logging.CORBALogDomains ;



public class IIOPInputStream
    extends com.sun.corba.se.impl.io.InputStreamHook
{
    private static Bridge bridge =
        (Bridge)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return Bridge.get() ;
                }
            }
        ) ;

    private static OMGSystemException omgWrapper = OMGSystemException.get(
        CORBALogDomains.RPC_ENCODING ) ;
    private static UtilSystemException utilWrapper = UtilSystemException.get(
        CORBALogDomains.RPC_ENCODING ) ;

    private ValueMember defaultReadObjectFVDMembers[] = null;

    private org.omg.CORBA_2_3.portable.InputStream orbStream;

    private CodeBase cbSender;

    private ValueHandlerImpl vhandler;  private Object currentObject = null;

    private ObjectStreamClass currentClassDesc = null;

    private Class currentClass = null;

    private int recursionDepth = 0;

    private int simpleReadDepth = 0;

    ActiveRecursionManager activeRecursionMgr = new ActiveRecursionManager();

    private IOException abortIOException = null;


    private ClassNotFoundException abortClassNotFoundException = null;


    private Vector callbacks;

    ObjectStreamClass[] classdesc;
    Class[] classes;
    int spClass;

    private static final String kEmptyStr = "";

    public static final TypeCode kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
    public static final TypeCode kValueTypeCode =  ORB.init().get_primitive_tc(TCKind.tk_value);

    private static final boolean useFVDOnly = false;

    private byte streamFormatVersion;

    private static final Constructor OPT_DATA_EXCEPTION_CTOR;

    private Object[] readObjectArgList = { this } ;

    static {
        OPT_DATA_EXCEPTION_CTOR = getOptDataExceptionCtor();
    }

    private static Constructor getOptDataExceptionCtor() {

        try {

            Constructor result =

                (Constructor) AccessController.doPrivileged(
                                    new PrivilegedExceptionAction() {
                    public java.lang.Object run()
                        throws NoSuchMethodException,
                        SecurityException {

                        Constructor boolCtor
                            = OptionalDataException.class.getDeclaredConstructor(
                                                               new Class[] {
                                Boolean.TYPE });

                        boolCtor.setAccessible(true);

                        return boolCtor;
                    }});

            if (result == null)
                throw new Error("Unable to find OptionalDataException constructor");

            return result;

        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private OptionalDataException createOptionalDataException() {
        try {
            OptionalDataException result
                = (OptionalDataException)
                   OPT_DATA_EXCEPTION_CTOR.newInstance(new Object[] {
                       Boolean.TRUE });

            if (result == null)
                throw new Error("Created null OptionalDataException");

            return result;

        } catch (Exception ex) {
            throw new Error("Couldn't create OptionalDataException", ex);
        }
    }

    protected byte getStreamFormatVersion() {
        return streamFormatVersion;
    }

    private void readFormatVersion() throws IOException {

        streamFormatVersion = orbStream.read_octet();

        if (streamFormatVersion < 1 ||
            streamFormatVersion > vhandler.getMaximumStreamFormatVersion()) {
            SystemException sysex = omgWrapper.unsupportedFormatVersion(
                    CompletionStatus.COMPLETED_MAYBE);
            IOException result = new IOException("Unsupported format version: "
                                                 + streamFormatVersion);
            result.initCause( sysex ) ;
            throw result ;
        }

        if (streamFormatVersion == 2) {
            if (!(orbStream instanceof ValueInputStream)) {
                SystemException sysex = omgWrapper.notAValueinputstream(
                    CompletionStatus.COMPLETED_MAYBE);
                IOException result = new IOException("Not a ValueInputStream");
                result.initCause( sysex ) ;
                throw result;
            }
        }
    }

    public static void setTestFVDFlag(boolean val){
        }


    public IIOPInputStream()
        throws java.io.IOException {
        super();
        resetStream();
    }

    final void setOrbStream(org.omg.CORBA_2_3.portable.InputStream os) {
        orbStream = os;
    }

    final org.omg.CORBA_2_3.portable.InputStream getOrbStream() {
        return orbStream;
    }

    public final void setSender(CodeBase cb) {
        cbSender = cb;
    }

    public final CodeBase getSender() {
        return cbSender;
    }

    public final void setValueHandler(ValueHandler vh) {
        vhandler = (com.sun.corba.se.impl.io.ValueHandlerImpl) vh;
    }

    public final ValueHandler getValueHandler() {
        return (javax.rmi.CORBA.ValueHandler) vhandler;
    }

    final void increaseRecursionDepth(){
        recursionDepth++;
    }

    final int decreaseRecursionDepth(){
        return --recursionDepth;
    }


    public final synchronized Object readObjectDelegate() throws IOException
    {
        try {

            readObjectState.readData(this);

            return orbStream.read_abstract_interface();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, true);
            throw marshalException;
        } catch(IndirectionException cdrie)
            {
                return activeRecursionMgr.getObject(cdrie.offset);
            }
    }

    final synchronized Object simpleReadObject(Class clz,
                                  String repositoryID,
                                  com.sun.org.omg.SendingContext.CodeBase sender,
                                  int offset)

    {


        Object prevObject = currentObject;
        ObjectStreamClass prevClassDesc = currentClassDesc;
        Class prevClass = currentClass;
        byte oldStreamFormatVersion = streamFormatVersion;

        simpleReadDepth++;      Object obj = null;


        try {
            if (vhandler.useFullValueDescription(clz, repositoryID)) {
                obj = inputObjectUsingFVD(clz, repositoryID, sender, offset);
            } else {
                obj = inputObject(clz, repositoryID, sender, offset);
            }

            obj = currentClassDesc.readResolve(obj);
        }
        catch(ClassNotFoundException cnfe)
            {
                bridge.throwException( cnfe ) ;
                return null;
            }
        catch(IOException ioe)
            {
                bridge.throwException(ioe) ;
                return null;
            }
        finally {
            simpleReadDepth --;
            currentObject = prevObject;
            currentClassDesc = prevClassDesc;
            currentClass = prevClass;
            streamFormatVersion = oldStreamFormatVersion;
        }



        IOException exIOE = abortIOException;
        if (simpleReadDepth == 0)
            abortIOException = null;
        if (exIOE != null){
            bridge.throwException( exIOE ) ;
            return null;
        }


        ClassNotFoundException exCNF = abortClassNotFoundException;
        if (simpleReadDepth == 0)
            abortClassNotFoundException = null;
        if (exCNF != null) {
            bridge.throwException( exCNF ) ;
            return null;
        }

        return obj;
    }

    public final synchronized  void simpleSkipObject(String repositoryID,
                                       com.sun.org.omg.SendingContext.CodeBase sender)

    {


        Object prevObject = currentObject;
        ObjectStreamClass prevClassDesc = currentClassDesc;
        Class prevClass = currentClass;
        byte oldStreamFormatVersion = streamFormatVersion;

        simpleReadDepth++;      Object obj = null;


        try {
            skipObjectUsingFVD(repositoryID, sender);
        }
        catch(ClassNotFoundException cnfe)
            {
                bridge.throwException( cnfe ) ;
                return;
            }
        catch(IOException ioe)
            {
                bridge.throwException( ioe ) ;
                return;
            }
        finally {
            simpleReadDepth --;
            streamFormatVersion = oldStreamFormatVersion;
            currentObject = prevObject;
            currentClassDesc = prevClassDesc;
            currentClass = prevClass;
        }



        IOException exIOE = abortIOException;
        if (simpleReadDepth == 0)
            abortIOException = null;
        if (exIOE != null){
            bridge.throwException( exIOE ) ;
            return;
        }


        ClassNotFoundException exCNF = abortClassNotFoundException;
        if (simpleReadDepth == 0)
            abortClassNotFoundException = null;
        if (exCNF != null) {
            bridge.throwException( exCNF ) ;
            return;
        }

        return;
    }
    protected final Object readObjectOverride()
        throws OptionalDataException, ClassNotFoundException, IOException
    {
        return readObjectDelegate();
    }


    final synchronized void defaultReadObjectDelegate()

    {
        try {
            if (currentObject == null || currentClassDesc == null)
                throw new NotActiveException("defaultReadObjectDelegate");

            if (!currentClassDesc.forClass().isAssignableFrom(
                    currentObject.getClass())) {
                throw new IOException("Object Type mismatch");
            }

            if (defaultReadObjectFVDMembers != null &&
                defaultReadObjectFVDMembers.length > 0) {

                inputClassFields(currentObject,
                                 currentClass,
                                 currentClassDesc,
                                 defaultReadObjectFVDMembers,
                                 cbSender);

            } else {

                ObjectStreamField[] fields =
                    currentClassDesc.getFieldsNoCopy();
                if (fields.length > 0) {
                    inputClassFields(currentObject, currentClass, fields, cbSender);
                }
            }
        }
        catch(NotActiveException nae)
            {
                bridge.throwException( nae ) ;
            }
        catch(IOException ioe)
            {
                bridge.throwException( ioe ) ;
            }
        catch(ClassNotFoundException cnfe)
            {
                bridge.throwException( cnfe ) ;
            }

    }


    public final boolean enableResolveObjectDelegate(boolean enable)

    {
        return false;
    }

    public final void mark(int readAheadLimit) {
        orbStream.mark(readAheadLimit);
    }

    public final boolean markSupported() {
        return orbStream.markSupported();
    }

    public final void reset() throws IOException {
        try {
            orbStream.reset();
        } catch (Error e) {
            IOException err = new IOException(e.getMessage());
            err.initCause(e) ;
            throw err ;
        }
    }

    public final int available() throws IOException{
        return 0; }

    public final void close() throws IOException{
        }

    public final int read() throws IOException{
        try{
            readObjectState.readData(this);

            return (orbStream.read_octet() << 0) & 0x000000FF;
        } catch (MARSHAL marshalException) {
            if (marshalException.minor
                == OMGSystemException.RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1) {
                setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
                return -1;
            }

            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e) ;
            throw exc ;
        }
    }

    public final int read(byte data[], int offset, int length) throws IOException{
        try{
            readObjectState.readData(this);

            orbStream.read_octet_array(data, offset, length);
            return length;
        } catch (MARSHAL marshalException) {
            if (marshalException.minor
                == OMGSystemException.RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1) {
                setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
                return -1;
            }

            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e) ;
            throw exc ;
        }

    }

    public final boolean readBoolean() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_boolean();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;

        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final byte readByte() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_octet();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;

        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final char readChar() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_wchar();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;

        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final double readDouble() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_double();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final float readFloat() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_float();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final void readFully(byte data[]) throws IOException{
readFully(data, 0, data.length);
    }

    public final void readFully(byte data[],  int offset,  int size) throws IOException{
try{
            readObjectState.readData(this);

            orbStream.read_octet_array(data, offset, size);
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);

            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final int readInt() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_long();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final String readLine() throws IOException{
        throw new IOException("Method readLine not supported");
    }

    public final long readLong() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_longlong();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final short readShort() throws IOException{
        try{
            readObjectState.readData(this);

            return orbStream.read_short();
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    protected final void readStreamHeader() throws IOException, StreamCorruptedException{
        }

    public final int readUnsignedByte() throws IOException{
        try{
            readObjectState.readData(this);

            return (orbStream.read_octet() << 0) & 0x000000FF;
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    public final int readUnsignedShort() throws IOException{
        try{
            readObjectState.readData(this);

            return (orbStream.read_ushort() << 0) & 0x0000FFFF;
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }


    protected String internalReadUTF(org.omg.CORBA.portable.InputStream stream)
    {
        return stream.read_wstring();
    }

    public final String readUTF() throws IOException{
        try{
            readObjectState.readData(this);

            return internalReadUTF(orbStream);
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);
            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e);
            throw exc ;
        }
    }

    private void handleOptionalDataMarshalException(MARSHAL marshalException,
                                                    boolean objectRead)
        throws IOException {

        if (marshalException.minor
            == OMGSystemException.RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1) {

            IOException result;

            if (!objectRead)
                result = new EOFException("No more optional data");
            else
                result = createOptionalDataException();

            result.initCause(marshalException);

            setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);

            throw result;
        }
    }

    public final synchronized void registerValidation(ObjectInputValidation obj,
                                                      int prio)
        throws NotActiveException, InvalidObjectException{
        throw new Error("Method registerValidation not supported");
    }

    protected final Class resolveClass(ObjectStreamClass v)
        throws IOException, ClassNotFoundException{
        throw new IOException("Method resolveClass not supported");
    }

    protected final Object resolveObject(Object obj) throws IOException{
        throw new IOException("Method resolveObject not supported");
    }

    public final int skipBytes(int len) throws IOException{
        try{
            readObjectState.readData(this);

            byte buf[] = new byte[len];
            orbStream.read_octet_array(buf, 0, len);
            return len;
        } catch (MARSHAL marshalException) {
            handleOptionalDataMarshalException(marshalException, false);

            throw marshalException;
        } catch(Error e) {
            IOException exc = new IOException(e.getMessage());
            exc.initCause(e) ;
            throw exc ;
        }
    }

    private synchronized Object inputObject(Class clz,
                               String repositoryID,
                               com.sun.org.omg.SendingContext.CodeBase sender,
                               int offset)
        throws IOException, ClassNotFoundException
    {



        currentClassDesc = ObjectStreamClass.lookup(clz);
        currentClass = currentClassDesc.forClass();
        if (currentClass == null)
            throw new ClassNotFoundException(currentClassDesc.getName());

        try {

            if (Enum.class.isAssignableFrom( clz )) {
                int ordinal = orbStream.read_long() ;
                String value = (String)orbStream.read_value( String.class ) ;
                return Enum.valueOf( clz, value ) ;
            } else if (currentClassDesc.isExternalizable()) {
                try {
                    currentObject = (currentClass == null) ?
                        null : currentClassDesc.newInstance();
                    if (currentObject != null) {

                        activeRecursionMgr.addObject(offset, currentObject);

                        readFormatVersion();

                        Externalizable ext = (Externalizable)currentObject;
                        ext.readExternal(this);
                }
            } catch (InvocationTargetException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "InvocationTargetException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            } catch (UnsupportedOperationException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "UnsupportedOperationException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            } catch (InstantiationException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "InstantiationException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            }
        } else {


            ObjectStreamClass currdesc = currentClassDesc;
            Class currclass = currentClass;

            int spBase = spClass;       if (currentClass.getName().equals("java.lang.String")) {
                return this.readUTF();
            }



            for (currdesc = currentClassDesc, currclass = currentClass;
                 currdesc != null && currdesc.isSerializable();
                 currdesc = currdesc.getSuperclass()) {


                Class cc = currdesc.forClass();
                Class cl;
                for (cl = currclass; cl != null; cl = cl.getSuperclass()) {
                    if (cc == cl) {
                        break;
                    } else {

                    }
                } spClass++;
                if (spClass >= classes.length) {
                    int newlen = classes.length * 2;
                    Class[] newclasses = new Class[newlen];
                    ObjectStreamClass[] newclassdesc = new ObjectStreamClass[newlen];

                    System.arraycopy(classes, 0,
                                     newclasses, 0,
                                     classes.length);
                    System.arraycopy(classdesc, 0,
                                     newclassdesc, 0,
                                     classes.length);

                    classes = newclasses;
                    classdesc = newclassdesc;
                }

                if (cl == null) {

                    classdesc[spClass] = currdesc;
                    classes[spClass] = null;
                } else {

                    classdesc[spClass] = currdesc;
                    classes[spClass] = cl;
                    currclass = cl.getSuperclass();
                }
            } try {
                currentObject = (currentClass == null) ?
                    null : currentClassDesc.newInstance() ;

                activeRecursionMgr.addObject(offset, currentObject);
            } catch (InvocationTargetException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "InvocationTargetException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            } catch (UnsupportedOperationException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "UnsupportedOperationException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            } catch (InstantiationException e) {
                InvalidClassException exc = new InvalidClassException(
                    currentClass.getName(),
                    "InstantiationException accessing no-arg constructor");
                exc.initCause( e ) ;
                throw exc ;
            }


            try {
                for (spClass = spClass; spClass > spBase; spClass--) {

                    currentClassDesc = classdesc[spClass];
                    currentClass = classes[spClass];
                    if (classes[spClass] != null) {


                        ReadObjectState oldState = readObjectState;
                        setState(DEFAULT_STATE);

                        try {

                            if (currentClassDesc.hasWriteObject()) {

                                readFormatVersion();

                                boolean calledDefaultWriteObject = readBoolean();

                                readObjectState.beginUnmarshalCustomValue(this,
                                                                          calledDefaultWriteObject,
                                                                          (currentClassDesc.readObjectMethod
                                                                           != null));
                            } else {
                                if (currentClassDesc.hasReadObject())
                                    setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                            }

                            if (!invokeObjectReader(currentClassDesc, currentObject, currentClass) ||
                                readObjectState == IN_READ_OBJECT_DEFAULTS_SENT) {

                                ObjectStreamField[] fields =
                                    currentClassDesc.getFieldsNoCopy();
                                if (fields.length > 0) {
                                    inputClassFields(currentObject, currentClass, fields, sender);
                                }
                            }

                            if (currentClassDesc.hasWriteObject())
                                readObjectState.endUnmarshalCustomValue(this);

                        } finally {
                            setState(oldState);
                        }

                    } else {

                        ObjectStreamField[] fields =
                                currentClassDesc.getFieldsNoCopy();
                            if (fields.length > 0) {
                                inputClassFields(null, currentClass, fields, sender);
                            }

                        }

                }
            } finally {
                                spClass = spBase;
            }
        }
        } finally {
            activeRecursionMgr.removeObject(offset);
        }

        return currentObject;
    }

    private Vector getOrderedDescriptions(String repositoryID,
                                          com.sun.org.omg.SendingContext.CodeBase sender) {
        Vector descs = new Vector();

        if (sender == null) {
            return descs;
        }

        FullValueDescription aFVD = sender.meta(repositoryID);
        while (aFVD != null) {
            descs.insertElementAt(aFVD, 0);
            if ((aFVD.base_value != null) && !kEmptyStr.equals(aFVD.base_value)) {
                aFVD = sender.meta(aFVD.base_value);
            }
            else return descs;
        }

        return descs;
    }


    private synchronized Object inputObjectUsingFVD(Class clz,
                                       String repositoryID,
                                       com.sun.org.omg.SendingContext.CodeBase sender,
                                       int offset)
        throws IOException, ClassNotFoundException
    {
        int spBase = spClass;   try{



            ObjectStreamClass currdesc = currentClassDesc = ObjectStreamClass.lookup(clz);
            Class currclass = currentClass = clz;


            if (currentClassDesc.isExternalizable()) {
                try {
                    currentObject = (currentClass == null) ?
                        null : currentClassDesc.newInstance();
                    if (currentObject != null) {
                        activeRecursionMgr.addObject(offset, currentObject);

                        readFormatVersion();

                        Externalizable ext = (Externalizable)currentObject;
                        ext.readExternal(this);
                    }
                } catch (InvocationTargetException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "InvocationTargetException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                } catch (UnsupportedOperationException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "UnsupportedOperationException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                } catch (InstantiationException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "InstantiationException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                }
            } else {

                for (currdesc = currentClassDesc, currclass = currentClass;
                     currdesc != null && currdesc.isSerializable();

                     currdesc = currdesc.getSuperclass()) {


                    Class cc = currdesc.forClass();
                    Class cl;
                    for (cl = currclass; cl != null; cl = cl.getSuperclass()) {
                        if (cc == cl) {
                            break;
                        } else {

                        }
                    } spClass++;
                    if (spClass >= classes.length) {
                        int newlen = classes.length * 2;
                        Class[] newclasses = new Class[newlen];
                        ObjectStreamClass[] newclassdesc = new ObjectStreamClass[newlen];

                        System.arraycopy(classes, 0,
                                         newclasses, 0,
                                         classes.length);
                        System.arraycopy(classdesc, 0,
                                         newclassdesc, 0,
                                         classes.length);

                        classes = newclasses;
                        classdesc = newclassdesc;
                    }

                    if (cl == null) {

                        classdesc[spClass] = currdesc;
                        classes[spClass] = null;
                    } else {

                        classdesc[spClass] = currdesc;
                        classes[spClass] = cl;
                        currclass = cl.getSuperclass();
                    }
                } try {
                    currentObject = (currentClass == null) ?
                        null : currentClassDesc.newInstance();

                    activeRecursionMgr.addObject(offset, currentObject);
                } catch (InvocationTargetException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "InvocationTargetException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                } catch (UnsupportedOperationException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "UnsupportedOperationException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                } catch (InstantiationException e) {
                    InvalidClassException exc = new InvalidClassException(
                        currentClass.getName(),
                        "InstantiationException accessing no-arg constructor");
                    exc.initCause( e ) ;
                    throw exc ;
                }

                Enumeration fvdsList = getOrderedDescriptions(repositoryID, sender).elements();

                while((fvdsList.hasMoreElements()) && (spClass > spBase)) {
                    FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
                    String repIDForFVD = vhandler.getClassName(fvd.id);
                    String repIDForClass = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));

                    while ((spClass > spBase) &&
                           (!repIDForFVD.equals(repIDForClass))) {
                        int pos = findNextClass(repIDForFVD, classes, spClass, spBase);
                        if (pos != -1) {
                            spClass = pos;
                            currclass = currentClass = classes[spClass];
                            repIDForClass = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));
                        }
                        else { if (fvd.is_custom) {

                                readFormatVersion();
                                boolean calledDefaultWriteObject = readBoolean();

                                if (calledDefaultWriteObject)
                                    inputClassFields(null, null, null, fvd.members, sender);

                                if (getStreamFormatVersion() == 2) {

                                    ((ValueInputStream)getOrbStream()).start_value();
                                    ((ValueInputStream)getOrbStream()).end_value();
                                }

                                } else {

                                inputClassFields(null, currentClass, null, fvd.members, sender);
                            }

                            if (fvdsList.hasMoreElements()){
                                fvd = (FullValueDescription)fvdsList.nextElement();
                                repIDForFVD = vhandler.getClassName(fvd.id);
                            }
                            else return currentObject;
                        }
                    }

                    currdesc = currentClassDesc = ObjectStreamClass.lookup(currentClass);

                    if (!repIDForClass.equals("java.lang.Object")) {

                        ReadObjectState oldState = readObjectState;
                        setState(DEFAULT_STATE);

                        try {

                            if (fvd.is_custom) {

                                readFormatVersion();

                                boolean calledDefaultWriteObject = readBoolean();

                                readObjectState.beginUnmarshalCustomValue(this,
                                                                          calledDefaultWriteObject,
                                                                          (currentClassDesc.readObjectMethod
                                                                           != null));
                            }

                            boolean usedReadObject = false;

                            try {

                                if (!fvd.is_custom && currentClassDesc.hasReadObject())
                                    setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);

                                defaultReadObjectFVDMembers = fvd.members;
                                usedReadObject = invokeObjectReader(currentClassDesc,
                                                                    currentObject,
                                                                    currentClass);

                            } finally {
                                defaultReadObjectFVDMembers = null;
                            }

                            if (!usedReadObject || readObjectState == IN_READ_OBJECT_DEFAULTS_SENT)
                                inputClassFields(currentObject, currentClass, currdesc, fvd.members, sender);

                            if (fvd.is_custom)
                                readObjectState.endUnmarshalCustomValue(this);

                        } finally {
                            setState(oldState);
                        }

                        currclass = currentClass = classes[--spClass];

                    } else {

                        inputClassFields(null, currentClass, null, fvd.members, sender);

                        while (fvdsList.hasMoreElements()){
                            fvd = (FullValueDescription)fvdsList.nextElement();

                            if (fvd.is_custom)
                                skipCustomUsingFVD(fvd.members, sender);
                            else
                                inputClassFields(null, currentClass, null, fvd.members, sender);
                        }

                    }

                } while (fvdsList.hasMoreElements()){

                    FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
                    if (fvd.is_custom)
                        skipCustomUsingFVD(fvd.members, sender);
                    else
                        throwAwayData(fvd.members, sender);
                }
            }

            return currentObject;
        }
        finally {
                spClass = spBase;

                activeRecursionMgr.removeObject(offset);
            }

        }


    private Object skipObjectUsingFVD(String repositoryID,
                                      com.sun.org.omg.SendingContext.CodeBase sender)
        throws IOException, ClassNotFoundException
    {

        Enumeration fvdsList = getOrderedDescriptions(repositoryID, sender).elements();

        while(fvdsList.hasMoreElements()) {
            FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
            String repIDForFVD = vhandler.getClassName(fvd.id);

            if (!repIDForFVD.equals("java.lang.Object")) {
                if (fvd.is_custom) {

                    readFormatVersion();

                    boolean calledDefaultWriteObject = readBoolean();

                    if (calledDefaultWriteObject)
                        inputClassFields(null, null, null, fvd.members, sender);

                    if (getStreamFormatVersion() == 2) {

                        ((ValueInputStream)getOrbStream()).start_value();
                        ((ValueInputStream)getOrbStream()).end_value();
                    }

                    } else {
                    inputClassFields(null, null, null, fvd.members, sender);
                }
            }

        } return null;

    }

    private int findNextClass(String classname, Class classes[], int _spClass, int _spBase){

        for (int i = _spClass; i > _spBase; i--){
            if (classname.equals(classes[i].getName())) {
                return i;
            }
        }

        return -1;
    }


    private boolean invokeObjectReader(ObjectStreamClass osc, Object obj, Class aclass)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException
    {
        if (osc.readObjectMethod == null) {
            return false;
        }

        try {
            osc.readObjectMethod.invoke( obj, readObjectArgList ) ;
            return true;
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof ClassNotFoundException)
                throw (ClassNotFoundException)t;
            else if (t instanceof IOException)
                throw (IOException)t;
            else if (t instanceof RuntimeException)
                throw (RuntimeException) t;
            else if (t instanceof Error)
                throw (Error) t;
            else
                throw new Error("internal error");
        } catch (IllegalAccessException e) {
            return false;
        }
    }


    private void resetStream() throws IOException {

        if (classes == null)
            classes = new Class[20];
        else {
            for (int i = 0; i < classes.length; i++)
                classes[i] = null;
        }
        if (classdesc == null)
            classdesc = new ObjectStreamClass[20];
        else {
            for (int i = 0; i < classdesc.length; i++)
                classdesc[i] = null;
        }
        spClass = 0;

        if (callbacks != null)
            callbacks.setSize(0);       }


    private void inputPrimitiveField(Object o, Class cl, ObjectStreamField field)
        throws InvalidClassException, IOException {

        try {
            switch (field.getTypeCode()) {
                case 'B':
                    byte byteValue = orbStream.read_octet();
                    if (field.getField() != null) {
                        bridge.putByte( o, field.getFieldID(), byteValue ) ;
                        }
                    break;
                case 'Z':
                    boolean booleanValue = orbStream.read_boolean();
                    if (field.getField() != null) {
                        bridge.putBoolean( o, field.getFieldID(), booleanValue ) ;
                        }
                    break;
                case 'C':
                    char charValue = orbStream.read_wchar();
                    if (field.getField() != null) {
                        bridge.putChar( o, field.getFieldID(), charValue ) ;
                        }
                    break;
                case 'S':
                    short shortValue = orbStream.read_short();
                    if (field.getField() != null) {
                        bridge.putShort( o, field.getFieldID(), shortValue ) ;
                        }
                    break;
                case 'I':
                    int intValue = orbStream.read_long();
                    if (field.getField() != null) {
                        bridge.putInt( o, field.getFieldID(), intValue ) ;
                        }
                    break;
                case 'J':
                    long longValue = orbStream.read_longlong();
                    if (field.getField() != null) {
                        bridge.putLong( o, field.getFieldID(), longValue ) ;
                        }
                    break;
                case 'F' :
                    float floatValue = orbStream.read_float();
                    if (field.getField() != null) {
                        bridge.putFloat( o, field.getFieldID(), floatValue ) ;
                        }
                    break;
                case 'D' :
                    double doubleValue = orbStream.read_double();
                    if (field.getField() != null) {
                        bridge.putDouble( o, field.getFieldID(), doubleValue ) ;
                        }
                    break;
                default:
                    throw new InvalidClassException(cl.getName());
            }
        } catch (IllegalArgumentException e) {

            ClassCastException cce = new ClassCastException("Assigning instance of class " +
                                         field.getType().getName() +
                                         " to field " +
                                         currentClassDesc.getName() + '#' +
                                         field.getField().getName());
            cce.initCause( e ) ;
            throw cce ;
        }
     }

    private Object inputObjectField(org.omg.CORBA.ValueMember field,
                                    com.sun.org.omg.SendingContext.CodeBase sender)
        throws IndirectionException, ClassNotFoundException, IOException,
               StreamCorruptedException {

        Object objectValue = null;
        Class type = null;
        String id = field.id;

        try {
            type = vhandler.getClassFromType(id);
        } catch(ClassNotFoundException cnfe) {
            type = null;
        }

        String signature = null;
        if (type != null)
            signature = ValueUtility.getSignature(field);

        if (signature != null && (signature.equals("Ljava/lang/Object;") ||
                                  signature.equals("Ljava/io/Serializable;") ||
                                  signature.equals("Ljava/io/Externalizable;"))) {
            objectValue = javax.rmi.CORBA.Util.readAny(orbStream);
        } else {
            int callType = ValueHandlerImpl.kValueType;

            if (!vhandler.isSequence(id)) {

                if (field.type.kind().value() == kRemoteTypeCode.kind().value()) {

                    callType = ValueHandlerImpl.kRemoteType;

                } else {

                    if (type != null && type.isInterface() &&
                        (vhandler.isAbstractBase(type) ||
                         ObjectStreamClassCorbaExt.isAbstractInterface(type))) {

                        callType = ValueHandlerImpl.kAbstractType;
                    }
                }
            }

            switch (callType) {
                case ValueHandlerImpl.kRemoteType:
                    if (type != null)
                        objectValue = Utility.readObjectAndNarrow(orbStream, type);
                    else
                        objectValue = orbStream.read_Object();
                    break;
                case ValueHandlerImpl.kAbstractType:
                    if (type != null)
                        objectValue = Utility.readAbstractAndNarrow(orbStream, type);
                    else
                        objectValue = orbStream.read_abstract_interface();
                    break;
                case ValueHandlerImpl.kValueType:
                    if (type != null)
                        objectValue = orbStream.read_value(type);
                    else
                                            objectValue = orbStream.read_value();
                    break;
                default:
                    throw new StreamCorruptedException("Unknown callType: " + callType);
            }
        }

        return objectValue;
    }


    private Object inputObjectField(ObjectStreamField field)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IndirectionException, IOException {

        if (ObjectStreamClassCorbaExt.isAny(field.getTypeString())) {
            return javax.rmi.CORBA.Util.readAny(orbStream);
        }

        Object objectValue = null;

        Class fieldType = field.getType();
        Class actualType = fieldType; int callType = ValueHandlerImpl.kValueType;
        boolean narrow = false;

        if (fieldType.isInterface()) {
            boolean loadStubClass = false;

            if (java.rmi.Remote.class.isAssignableFrom(fieldType)) {

                callType = ValueHandlerImpl.kRemoteType;

            } else if (org.omg.CORBA.Object.class.isAssignableFrom(fieldType)){

                callType = ValueHandlerImpl.kRemoteType;
                loadStubClass = true;

            } else if (vhandler.isAbstractBase(fieldType)) {
                callType = ValueHandlerImpl.kAbstractType;
                loadStubClass = true;
            } else if (ObjectStreamClassCorbaExt.isAbstractInterface(fieldType)) {
                callType = ValueHandlerImpl.kAbstractType;
            }

            if (loadStubClass) {
                try {
                    String codebase = Util.getCodebase(fieldType);
                    String repID = vhandler.createForAnyType(fieldType);
                    Class stubType =
                        Utility.loadStubClass(repID, codebase, fieldType);
                    actualType = stubType;
                } catch (ClassNotFoundException e) {
                    narrow = true;
                }
            } else {
                narrow = true;
            }
        }

        switch (callType) {
            case ValueHandlerImpl.kRemoteType:
                if (!narrow)
                    objectValue = (Object)orbStream.read_Object(actualType);
                else
                    objectValue = Utility.readObjectAndNarrow(orbStream, actualType);
                break;
            case ValueHandlerImpl.kAbstractType:
                if (!narrow)
                    objectValue = (Object)orbStream.read_abstract_interface(actualType);
                else
                    objectValue = Utility.readAbstractAndNarrow(orbStream, actualType);
                break;
            case ValueHandlerImpl.kValueType:
                objectValue = (Object)orbStream.read_value(actualType);
                break;
            default:
                throw new StreamCorruptedException("Unknown callType: " + callType);
        }

        return objectValue;
    }

    private final boolean mustUseRemoteValueMembers() {
        return defaultReadObjectFVDMembers != null;
    }

    void readFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        if (mustUseRemoteValueMembers()) {
            inputRemoteMembersForReadFields(fieldToValueMap);
        } else
            inputCurrentClassFieldsForReadFields(fieldToValueMap);
    }

    private final void inputRemoteMembersForReadFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        ValueMember fields[] = defaultReadObjectFVDMembers;

        try {

            for (int i = 0; i < fields.length; i++) {

                switch (fields[i].type.kind().value()) {

                case TCKind._tk_octet:
                    byte byteValue = orbStream.read_octet();
                    fieldToValueMap.put(fields[i].name, new Byte(byteValue));
                    break;
                case TCKind._tk_boolean:
                    boolean booleanValue = orbStream.read_boolean();
                    fieldToValueMap.put(fields[i].name, new Boolean(booleanValue));
                    break;
                case TCKind._tk_char:
                    case TCKind._tk_wchar:
                    char charValue = orbStream.read_wchar();
                    fieldToValueMap.put(fields[i].name, new Character(charValue));
                    break;
                case TCKind._tk_short:
                    short shortValue = orbStream.read_short();
                    fieldToValueMap.put(fields[i].name, new Short(shortValue));
                    break;
                case TCKind._tk_long:
                    int intValue = orbStream.read_long();
                    fieldToValueMap.put(fields[i].name, new Integer(intValue));
                    break;
                case TCKind._tk_longlong:
                    long longValue = orbStream.read_longlong();
                    fieldToValueMap.put(fields[i].name, new Long(longValue));
                    break;
                case TCKind._tk_float:
                    float floatValue = orbStream.read_float();
                    fieldToValueMap.put(fields[i].name, new Float(floatValue));
                    break;
                case TCKind._tk_double:
                    double doubleValue = orbStream.read_double();
                    fieldToValueMap.put(fields[i].name, new Double(doubleValue));
                    break;
                case TCKind._tk_value:
                case TCKind._tk_objref:
                case TCKind._tk_value_box:
                    Object objectValue = null;
                    try {
                        objectValue = inputObjectField(fields[i],
                                                       cbSender);

                    } catch (IndirectionException cdrie) {
                        objectValue = activeRecursionMgr.getObject(cdrie.offset);
                    }

                    fieldToValueMap.put(fields[i].name, objectValue);
                    break;
                default:
                    throw new StreamCorruptedException("Unknown kind: "
                                                       + fields[i].type.kind().value());
                }
            }
        } catch (Throwable t) {
            StreamCorruptedException result = new StreamCorruptedException(t.getMessage());
            result.initCause(t);
            throw result;
        }
    }


    private final void inputCurrentClassFieldsForReadFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        ObjectStreamField[] fields = currentClassDesc.getFieldsNoCopy();

        int primFields = fields.length - currentClassDesc.objFields;

        for (int i = 0; i < primFields; ++i) {

            switch (fields[i].getTypeCode()) {
                case 'B':
                    byte byteValue = orbStream.read_octet();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Byte(byteValue));
                    break;
                case 'Z':
                   boolean booleanValue = orbStream.read_boolean();
                   fieldToValueMap.put(fields[i].getName(),
                                       new Boolean(booleanValue));
                   break;
                case 'C':
                    char charValue = orbStream.read_wchar();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Character(charValue));
                    break;
                case 'S':
                    short shortValue = orbStream.read_short();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Short(shortValue));
                    break;
                case 'I':
                    int intValue = orbStream.read_long();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Integer(intValue));
                    break;
                case 'J':
                    long longValue = orbStream.read_longlong();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Long(longValue));
                    break;
                case 'F' :
                    float floatValue = orbStream.read_float();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Float(floatValue));
                    break;
                case 'D' :
                    double doubleValue = orbStream.read_double();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Double(doubleValue));
                    break;
                default:
                    throw new InvalidClassException(currentClassDesc.getName());
            }
        }


        if (currentClassDesc.objFields > 0) {
            for (int i = primFields; i < fields.length; i++) {
                Object objectValue = null;
                try {
                    objectValue = inputObjectField(fields[i]);
                } catch(IndirectionException cdrie) {
                    objectValue = activeRecursionMgr.getObject(cdrie.offset);
                }

                fieldToValueMap.put(fields[i].getName(), objectValue);
            }
        }
    }


    private void inputClassFields(Object o, final Class<?> cl,
                                  ObjectStreamField[] fields,
                                  com.sun.org.omg.SendingContext.CodeBase sender)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException
    {

        int primFields = fields.length - currentClassDesc.objFields;

        if (o != null) {
            for (int i = 0; i < primFields; ++i) {
                inputPrimitiveField(o, cl, fields[i]);
            }
        }


        if (currentClassDesc.objFields > 0) {
            for (int i = primFields; i < fields.length; i++) {
                Object objectValue = null;

                try {
                    objectValue = inputObjectField(fields[i]);
                } catch(IndirectionException cdrie) {
                    objectValue = activeRecursionMgr.getObject(cdrie.offset);
                }

                if ((o == null) || (fields[i].getField() == null)) {
                    continue;
                }

                try {
                    Class<?> fieldCl = fields[i].getClazz();
                    if ((objectValue != null)
                            && (!fieldCl.isAssignableFrom(
                                    objectValue.getClass()))) {
                        throw new IllegalArgumentException("Field mismatch");
                    }
                    Field declaredClassField = null;
                    final String inputStreamFieldName = fields[i].getName();
                    try {
                        declaredClassField = getDeclaredField( cl, inputStreamFieldName);
                    } catch (PrivilegedActionException paEx) {
                        throw new IllegalArgumentException(
                            (NoSuchFieldException) paEx.getException());
                    } catch (SecurityException secEx) {
                        throw new IllegalArgumentException(secEx);
                    }  catch (NullPointerException npEx) {
                        continue;
                    } catch (NoSuchFieldException e) {
                        continue;
                    }

                    if (declaredClassField == null) {
                        continue;
                    }
                    Class<?> declaredFieldClass = declaredClassField.getType();

                    if (!declaredFieldClass.isAssignableFrom(fieldCl)) {
                        throw new IllegalArgumentException(
                                "Field Type mismatch");
                    }
                    if (objectValue != null && !fieldCl.isInstance(objectValue)) {
                        throw new IllegalArgumentException();
                    }
                    bridge.putObject( o, fields[i].getFieldID(), objectValue ) ;
                    } catch (IllegalArgumentException iaEx) {
                    String objectValueClassName = "null";
                    String currentClassDescClassName = "null";
                    String fieldName = "null";
                    if (objectValue != null) {
                        objectValueClassName = objectValue.getClass().getName();
                    }
                    if (currentClassDesc != null) {
                        currentClassDescClassName = currentClassDesc.getName();
                    }
                    if (fields[i] != null && fields[i].getField() != null) {
                        fieldName = fields[i].getField().getName();
                    }
                    ClassCastException ccEx = new ClassCastException(
                            "Assigning instance of class " + objectValueClassName
                                    + " to field " + currentClassDescClassName + '#' + fieldName);
                    ccEx.initCause( iaEx ) ;
                    throw ccEx ;
                }
            } }
        }


    private void inputClassFields(Object o, Class cl,
                                  ObjectStreamClass osc,
                                  ValueMember[] fields,
                                  com.sun.org.omg.SendingContext.CodeBase sender)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException
    {
        try{
            for (int i = 0; i < fields.length; ++i) {
                try {
                    switch (fields[i].type.kind().value()) {
                    case TCKind._tk_octet:
                        byte byteValue = orbStream.read_octet();
                        if ((o != null) && osc.hasField(fields[i]))
                        setByteField(o, cl, fields[i].name, byteValue);
                        break;
                    case TCKind._tk_boolean:
                        boolean booleanValue = orbStream.read_boolean();
                        if ((o != null) && osc.hasField(fields[i]))
                        setBooleanField(o, cl, fields[i].name, booleanValue);
                        break;
                    case TCKind._tk_char:
                        case TCKind._tk_wchar:
                        char charValue = orbStream.read_wchar();
                        if ((o != null) && osc.hasField(fields[i]))
                        setCharField(o, cl, fields[i].name, charValue);
                        break;
                    case TCKind._tk_short:
                        short shortValue = orbStream.read_short();
                        if ((o != null) && osc.hasField(fields[i]))
                        setShortField(o, cl, fields[i].name, shortValue);
                        break;
                    case TCKind._tk_long:
                        int intValue = orbStream.read_long();
                        if ((o != null) && osc.hasField(fields[i]))
                        setIntField(o, cl, fields[i].name, intValue);
                        break;
                    case TCKind._tk_longlong:
                        long longValue = orbStream.read_longlong();
                        if ((o != null) && osc.hasField(fields[i]))
                        setLongField(o, cl, fields[i].name, longValue);
                        break;
                    case TCKind._tk_float:
                        float floatValue = orbStream.read_float();
                        if ((o != null) && osc.hasField(fields[i]))
                        setFloatField(o, cl, fields[i].name, floatValue);
                        break;
                    case TCKind._tk_double:
                        double doubleValue = orbStream.read_double();
                        if ((o != null) && osc.hasField(fields[i]))
                        setDoubleField(o, cl, fields[i].name, doubleValue);
                        break;
                    case TCKind._tk_value:
                    case TCKind._tk_objref:
                    case TCKind._tk_value_box:
                        Object objectValue = null;
                        try {
                            objectValue = inputObjectField(fields[i], sender);
                        } catch (IndirectionException cdrie) {
                            objectValue = activeRecursionMgr.getObject(cdrie.offset);
                        }

                        if (o == null)
                            continue;
                        try {
                            if (osc.hasField(fields[i])){
                                setObjectField(o,
                                               cl,
                                               fields[i].name,
                                               objectValue);
                            } else {
                                }
                        } catch (IllegalArgumentException e) {
                            ClassCastException cce = new ClassCastException("Assigning instance of class " +
                                objectValue.getClass().getName() + " to field " + fields[i].name);
                            cce.initCause(e) ;
                            throw cce ;
                        }
                        break;
                    default:
                        throw new StreamCorruptedException("Unknown kind: "
                                                           + fields[i].type.kind().value());
                    }
                } catch (IllegalArgumentException e) {

                    ClassCastException cce = new ClassCastException("Assigning instance of class " + fields[i].id +
                        " to field " + currentClassDesc.getName() + '#' + fields[i].name);
                    cce.initCause( e ) ;
                    throw cce ;
                }
            }
        } catch(Throwable t){
            StreamCorruptedException sce = new StreamCorruptedException(t.getMessage());
            sce.initCause(t) ;
            throw sce ;
        }
    }

    private void skipCustomUsingFVD(ValueMember[] fields,
                                    com.sun.org.omg.SendingContext.CodeBase sender)
                                    throws InvalidClassException, StreamCorruptedException,
                                           ClassNotFoundException, IOException
    {
        readFormatVersion();
        boolean calledDefaultWriteObject = readBoolean();

        if (calledDefaultWriteObject)
            throwAwayData(fields, sender);

        if (getStreamFormatVersion() == 2) {

            ((ValueInputStream)getOrbStream()).start_value();
            ((ValueInputStream)getOrbStream()).end_value();
        }
    }


    private void throwAwayData(ValueMember[] fields,
                               com.sun.org.omg.SendingContext.CodeBase sender)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        for (int i = 0; i < fields.length; ++i) {

            try {

                switch (fields[i].type.kind().value()) {
                case TCKind._tk_octet:
                    orbStream.read_octet();
                    break;
                case TCKind._tk_boolean:
                    orbStream.read_boolean();
                    break;
                case TCKind._tk_char:
                    case TCKind._tk_wchar:
                    orbStream.read_wchar();
                    break;
                case TCKind._tk_short:
                    orbStream.read_short();
                    break;
                case TCKind._tk_long:
                    orbStream.read_long();
                    break;
                case TCKind._tk_longlong:
                    orbStream.read_longlong();
                    break;
                case TCKind._tk_float:
                    orbStream.read_float();
                    break;
                case TCKind._tk_double:
                    orbStream.read_double();
                    break;
                case TCKind._tk_value:
                case TCKind._tk_objref:
                case TCKind._tk_value_box:
                    Class type = null;
                    String id = fields[i].id;

                    try {
                        type = vhandler.getClassFromType(id);
                    }
                    catch(ClassNotFoundException cnfe){
                        type = null;
                    }
                    String signature = null;
                    if (type != null)
                        signature = ValueUtility.getSignature(fields[i]);

                    try {
                        if ((signature != null) && ( signature.equals("Ljava/lang/Object;") ||
                                                     signature.equals("Ljava/io/Serializable;") ||
                                                     signature.equals("Ljava/io/Externalizable;")) ) {
                            javax.rmi.CORBA.Util.readAny(orbStream);
                        }
                        else {
                            int callType = ValueHandlerImpl.kValueType;

                            if (!vhandler.isSequence(id)) {
                                FullValueDescription fieldFVD = sender.meta(fields[i].id);
                                if (kRemoteTypeCode == fields[i].type) {

                                    callType = ValueHandlerImpl.kRemoteType;
                                } else if (fieldFVD.is_abstract) {
                                    callType = ValueHandlerImpl.kAbstractType;
                                }
                            }

                            switch (callType) {
                            case ValueHandlerImpl.kRemoteType:
                                orbStream.read_Object();
                                break;
                            case ValueHandlerImpl.kAbstractType:
                                orbStream.read_abstract_interface();
                                break;
                            case ValueHandlerImpl.kValueType:
                                if (type != null) {
                                    orbStream.read_value(type);
                                } else {
                                    orbStream.read_value();
                                }
                                break;
                            default:
                                throw new StreamCorruptedException("Unknown callType: "
                                                                   + callType);
                            }
                        }

                    }
                    catch(IndirectionException cdrie) {
                        continue;
                    }

                    break;
                default:
                    throw new StreamCorruptedException("Unknown kind: "
                                                       + fields[i].type.kind().value());

                }
            } catch (IllegalArgumentException e) {

                ClassCastException cce = new ClassCastException("Assigning instance of class " +
                    fields[i].id + " to field " + currentClassDesc.getName() +
                    '#' + fields[i].name);
                cce.initCause(e) ;
                throw cce ;
            }
        }

    }

    private static void setObjectField(Object o, Class<?> c, String fieldName, Object v) {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            Class fieldCl = fld.getType();
            if(v != null && !fieldCl.isInstance(v)) {
                throw new Exception();
            }
            long key = bridge.objectFieldOffset( fld ) ;
            bridge.putObject( o, key, v ) ;
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetObjectField( e, fieldName,
                    o.toString(),
                    v.toString() ) ;
            } else {
                throw utilWrapper.errorSetObjectField( e, fieldName,
                    "null " + c.getName() + " object",
                    v.toString() ) ;
            }
        }
    }

    private static void setBooleanField(Object o, Class<?> c, String fieldName, boolean v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Boolean.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putBoolean( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
            throw utilWrapper.errorSetBooleanField( e, fieldName,
                o.toString(),
                new Boolean(v) ) ;
            } else {
                throw utilWrapper.errorSetBooleanField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Boolean(v) ) ;
            }
        }
    }

    private static void setByteField(Object o, Class<?> c, String fieldName, byte v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Byte.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putByte( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetByteField( e, fieldName,
                    o.toString(),
                    new Byte(v) ) ;
            } else {
                throw utilWrapper.errorSetByteField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Byte(v) ) ;
            }
        }
    }

    private static void setCharField(Object o, Class<?> c, String fieldName, char v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Character.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putChar( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetCharField( e, fieldName,
                    o.toString(),
                    new Character(v) ) ;
            } else {
                throw utilWrapper.errorSetCharField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Character(v) ) ;
            }
        }
    }

    private static void setShortField(Object o, Class<?> c, String fieldName, short v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Short.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putShort( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
            throw utilWrapper.errorSetShortField( e, fieldName,
                o.toString(),
                new Short(v) ) ;
            } else {
                throw utilWrapper.errorSetShortField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Short(v) ) ;
            }
        }
    }

    private static void setIntField(Object o, final Class<?> c, final String fieldName, int v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Integer.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putInt( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetIntField( e, fieldName,
                    o.toString(),
                    new Integer(v) ) ;
            } else {
                throw utilWrapper.errorSetIntField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Integer(v) ) ;
            }
        }
    }

    private static void setLongField(Object o, Class<?> c, String fieldName, long v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Long.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putLong( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetLongField( e, fieldName,
                    o.toString(),
                    new Long(v) ) ;
            } else {
                throw utilWrapper.errorSetLongField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Long(v) ) ;
            }
        }
    }

    private static void setFloatField(Object o, Class<?> c, String fieldName, float v)
    {
        try {
            Field fld = getDeclaredField( c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Float.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putFloat( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetFloatField( e, fieldName,
                    o.toString(),
                    new Float(v) ) ;
            } else {
                throw utilWrapper.errorSetFloatField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Float(v) ) ;
            }
        }
    }

    private static void setDoubleField(Object o, Class<?> c, String fieldName, double v)
    {
        try {
            Field fld = getDeclaredField(c, fieldName ) ;
            if ((fld != null) && (fld.getType() == Double.TYPE)) {
                long key = bridge.objectFieldOffset( fld ) ;
                bridge.putDouble( o, key, v ) ;
            } else {
                throw new InvalidObjectException("Field Type mismatch");
            }
        } catch (Exception e) {
            if (o != null) {
                throw utilWrapper.errorSetDoubleField( e, fieldName,
                    o.toString(),
                    new Double(v) ) ;
            } else {
                throw utilWrapper.errorSetDoubleField( e, fieldName,
                    "null " + c.getName() + " object",
                    new Double(v) ) ;
            }
        }
    }


    private static Field getDeclaredField(final Class<?> c,
                                           final String fieldName)
        throws PrivilegedActionException, NoSuchFieldException, SecurityException {
        if (System.getSecurityManager() == null) {
            return c.getDeclaredField(fieldName);
        } else {
            return AccessController
                .doPrivileged(new PrivilegedExceptionAction<Field>() {
                    public Field run()
                            throws NoSuchFieldException {
                        return c.getDeclaredField(fieldName);
                    }
                });
        }
    }


    static class ActiveRecursionManager
    {
        private Map<Integer, Object> offsetToObjectMap;

        public ActiveRecursionManager() {
            offsetToObjectMap = new HashMap<>();
        }

        public void addObject(int offset, Object value) {
            offsetToObjectMap.put(new Integer(offset), value);
        }

        public Object getObject(int offset) throws IOException {
            Integer position = new Integer(offset);

            if (!offsetToObjectMap.containsKey(position))
                throw new IOException("Invalid indirection to offset "
                                      + offset);

            return offsetToObjectMap.get(position);
        }

        public void removeObject(int offset) {
            offsetToObjectMap.remove(new Integer(offset));
        }

        public boolean containsObject(int offset) {
            return offsetToObjectMap.containsKey(new Integer(offset));
        }
    }
}
