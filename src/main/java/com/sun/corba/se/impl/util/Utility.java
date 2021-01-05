



package com.sun.corba.se.impl.util;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.Delegate;


import java.util.Hashtable;
import java.util.NoSuchElementException;

import java.rmi.Remote;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;

import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;

import org.omg.PortableServer.POA;

import com.sun.org.omg.SendingContext.CodeBase;

import com.sun.corba.se.spi.logging.CORBALogDomains ;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter ;

import com.sun.corba.se.impl.logging.UtilSystemException ;
import com.sun.corba.se.impl.logging.OMGSystemException ;


public final class Utility {

    public static final String STUB_PREFIX = "_";
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String DYNAMIC_STUB_SUFFIX = "_DynamicStub" ;
    public static final String IDL_STUB_SUFFIX = "Stub";
    public static final String TIE_SUFIX = "_Tie";
    private static IdentityHashtable tieCache = new IdentityHashtable();
    private static IdentityHashtable tieToStubCache = new IdentityHashtable();
    private static IdentityHashtable stubToTieCache = new IdentityHashtable();
    private static Object CACHE_MISS = new Object();
    private static UtilSystemException wrapper = UtilSystemException.get(
        CORBALogDomains.UTIL ) ;
    private static OMGSystemException omgWrapper = OMGSystemException.get(
        CORBALogDomains.UTIL ) ;


    public static Object autoConnect(Object obj, ORB orb, boolean convertToStub)
    {
        if (obj == null) {
            return obj;
        }

        if (StubAdapter.isStub(obj)) {
            try {
                StubAdapter.getDelegate(obj) ;
            } catch (BAD_OPERATION okay) {
                try {
                    StubAdapter.connect( obj, orb ) ;
                } catch (RemoteException e) {
                    throw wrapper.objectNotConnected( e,
                        obj.getClass().getName() ) ;
                }
            }

            return obj;
        }

        if (obj instanceof Remote) {
            Remote remoteObj = (Remote)obj;
            Tie theTie = Util.getTie(remoteObj);
            if (theTie != null) {
                try {
                    theTie.orb();
                } catch (SystemException okay) {
                    theTie.orb(orb);
                }

                if (convertToStub) {
                    Object result = loadStub(theTie,null,null,true);
                    if (result != null) {
                        return result;
                    } else {
                        throw wrapper.couldNotLoadStub(obj.getClass().getName());
                    }
                } else {
                    return StubAdapter.activateTie( theTie );
                }
            } else {
                throw wrapper.objectNotExported( obj.getClass().getName() ) ;
            }
        }

        return obj;
    }


    public static Tie loadTie(Remote obj) {
        Tie result = null;
        Class objClass = obj.getClass();

        synchronized (tieCache) {

            Object it = tieCache.get(obj);

            if (it == null) {

                try {

                    result = loadTie(objClass);

                    while (result == null &&
                           (objClass = objClass.getSuperclass()) != null &&
                           objClass != PortableRemoteObject.class &&
                           objClass != Object.class) {

                        result = loadTie(objClass);
                    }
                } catch (Exception ex) {
                    wrapper.loadTieFailed( ex, objClass.getName() ) ;
                }

                if (result == null) {

                    tieCache.put(obj,CACHE_MISS);

                } else {

                    tieCache.put(obj,result);
                }
            } else {

                if (it != CACHE_MISS) {
                    try {
                        result = (Tie) it.getClass().newInstance();
                    } catch (Exception e) {
                    }
                }
            }
        }

        return result;
    }


    private static Tie loadTie(Class theClass)
    {
        return com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory().
            getTie( theClass ) ;
    }


    public static void clearCaches() {
        synchronized (tieToStubCache) {
            tieToStubCache.clear();
        }
        synchronized (tieCache) {
            tieCache.clear();
        }
        synchronized (stubToTieCache) {
            stubToTieCache.clear();
        }
    }


    static Class loadClassOfType(String className, String remoteCodebase,
        ClassLoader loader, Class expectedType,
        ClassLoader expectedTypeClassLoader) throws ClassNotFoundException
    {
        Class loadedClass = null;

        try {
            try{
                if (!PackagePrefixChecker.hasOffendingPrefix(
                    PackagePrefixChecker.withoutPackagePrefix(className))){
                    loadedClass = Util.loadClass(
                        PackagePrefixChecker.withoutPackagePrefix(className),
                        remoteCodebase,
                        loader);
                } else {
                    loadedClass = Util.loadClass(className, remoteCodebase,
                        loader);
                }
            } catch (ClassNotFoundException cnfe) {
                loadedClass = Util.loadClass(className, remoteCodebase,
                    loader);
            }
            if (expectedType == null)
                return loadedClass;
        } catch (ClassNotFoundException cnfe) {
            if (expectedType == null)
                throw cnfe;
        }

        if (loadedClass == null || !expectedType.isAssignableFrom(loadedClass)){
            if (expectedType.getClassLoader() != expectedTypeClassLoader)
                throw new IllegalArgumentException(
                    "expectedTypeClassLoader not class loader of "  +
                    "expected Type.");

            if (expectedTypeClassLoader != null)
                loadedClass = expectedTypeClassLoader.loadClass(className);
            else {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null)
                    cl = ClassLoader.getSystemClassLoader();

                loadedClass = cl.loadClass(className);
            }
        }

        return loadedClass;
    }


    public static Class loadClassForClass (String className,
                                           String remoteCodebase,
                                           ClassLoader loader,
                                           Class relatedType,
                                           ClassLoader relatedTypeClassLoader)
        throws ClassNotFoundException
    {
        if (relatedType == null)
            return Util.loadClass(className, remoteCodebase, loader);

        Class loadedClass = null;
        try {
            loadedClass = Util.loadClass(className, remoteCodebase, loader);
        } catch (ClassNotFoundException cnfe) {
            if (relatedType.getClassLoader() == null)
                throw cnfe;
        }

        if (loadedClass == null ||
            (loadedClass.getClassLoader() != null &&
             loadedClass.getClassLoader().loadClass(relatedType.getName()) !=
                 relatedType))
        {
            if (relatedType.getClassLoader() != relatedTypeClassLoader)
                throw new IllegalArgumentException(
                    "relatedTypeClassLoader not class loader of relatedType.");

            if (relatedTypeClassLoader != null)
                loadedClass = relatedTypeClassLoader.loadClass(className);
        }

        return loadedClass;
    }


    public static BoxedValueHelper getHelper(Class clazz, String codebase,
        String repId)
    {
        String className = null;
        if (clazz != null) {
            className = clazz.getName();
            if (codebase == null)
                codebase = Util.getCodebase(clazz);
        } else {
            if (repId != null)
                className = RepositoryId.cache.getId(repId).getClassName();
            if (className == null) throw wrapper.unableLocateValueHelper(
                    CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ClassLoader clazzLoader =
                (clazz == null ? null : clazz.getClassLoader());
            Class helperClass =
                loadClassForClass(className+"Helper", codebase, clazzLoader,
                clazz, clazzLoader);
            return (BoxedValueHelper)helperClass.newInstance();

        } catch (ClassNotFoundException cnfe) {
            throw wrapper.unableLocateValueHelper( CompletionStatus.COMPLETED_MAYBE,
                cnfe );
        } catch (IllegalAccessException iae) {
            throw wrapper.unableLocateValueHelper( CompletionStatus.COMPLETED_MAYBE,
                iae );
        } catch (InstantiationException ie) {
            throw wrapper.unableLocateValueHelper( CompletionStatus.COMPLETED_MAYBE,
                ie );
        } catch (ClassCastException cce) {
            throw wrapper.unableLocateValueHelper( CompletionStatus.COMPLETED_MAYBE,
                cce );
        }
    }


    public static ValueFactory getFactory(Class clazz, String codebase,
                               ORB orb, String repId)
    {
        ValueFactory factory = null;
        if ((orb != null) && (repId != null)) {
            try {
                factory = ((org.omg.CORBA_2_3.ORB)orb).lookup_value_factory(
                    repId);
            } catch (org.omg.CORBA.BAD_PARAM ex) {
                }
        }

        String className = null;
        if (clazz != null) {
            className = clazz.getName();
            if (codebase == null)
                codebase = Util.getCodebase(clazz);
        } else {
            if (repId != null)
                className = RepositoryId.cache.getId(repId).getClassName();
            if (className == null) throw omgWrapper.unableLocateValueFactory(
                    CompletionStatus.COMPLETED_MAYBE);
        }

        if (factory != null &&
            (!factory.getClass().getName().equals(className+"DefaultFactory") ||
             (clazz == null && codebase == null)))
            return factory;

        try {
            ClassLoader clazzLoader =
                (clazz == null ? null : clazz.getClassLoader());
            Class factoryClass =
                loadClassForClass(className+"DefaultFactory", codebase,
                clazzLoader, clazz, clazzLoader);
            return (ValueFactory)factoryClass.newInstance();

        } catch (ClassNotFoundException cnfe) {
            throw omgWrapper.unableLocateValueFactory(
                CompletionStatus.COMPLETED_MAYBE, cnfe);
        } catch (IllegalAccessException iae) {
            throw omgWrapper.unableLocateValueFactory(
                CompletionStatus.COMPLETED_MAYBE, iae);
        } catch (InstantiationException ie) {
            throw omgWrapper.unableLocateValueFactory(
                CompletionStatus.COMPLETED_MAYBE, ie);
        } catch (ClassCastException cce) {
            throw omgWrapper.unableLocateValueFactory(
                CompletionStatus.COMPLETED_MAYBE, cce);
        }
    }



    public static Remote loadStub(Tie tie,
                                  PresentationManager.StubFactory stubFactory,
                                  String remoteCodebase,
                                  boolean onlyMostDerived)
    {
        StubEntry entry = null;

        synchronized (tieToStubCache) {
            Object cached = tieToStubCache.get(tie);
            if (cached == null) {
                entry = loadStubAndUpdateCache(
                        tie, stubFactory, remoteCodebase, onlyMostDerived);
            } else {
                if (cached != CACHE_MISS) {
                    entry = (StubEntry) cached;

                    if (!entry.mostDerived && onlyMostDerived) {
                        entry = loadStubAndUpdateCache(tie,null,
                            remoteCodebase,true);
                    } else if (stubFactory != null &&
                        !StubAdapter.getTypeIds(entry.stub)[0].equals(
                            stubFactory.getTypeIds()[0]) )
                    {
                        entry = loadStubAndUpdateCache(tie,null,
                            remoteCodebase,true);

                        if (entry == null) {
                            entry = loadStubAndUpdateCache(tie,stubFactory,
                                    remoteCodebase,onlyMostDerived);
                        }
                    } else {
                        try {
                            Delegate stubDel = StubAdapter.getDelegate(
                                entry.stub ) ;
                        } catch (Exception e2) {
                            try {
                                Delegate del = StubAdapter.getDelegate(
                                    tie ) ;
                                StubAdapter.setDelegate( entry.stub,
                                    del ) ;
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        }

        if (entry != null) {
            return (Remote)entry.stub;
        } else {
            return null;
        }
    }


    private static StubEntry loadStubAndUpdateCache (
        Tie tie, PresentationManager.StubFactory  stubFactory,
        String remoteCodebase, boolean onlyMostDerived)
    {
        org.omg.CORBA.Object stub = null;
        StubEntry entry = null;
        boolean tieIsStub = StubAdapter.isStub( tie ) ;

        if (stubFactory != null) {
            try {
                stub = stubFactory.makeStub();
            } catch (Throwable e) {
                wrapper.stubFactoryCouldNotMakeStub( e ) ;
                if (e instanceof ThreadDeath) {
                    throw (ThreadDeath) e;
                }
            }
        } else {
            String[] ids = null;
            if (tieIsStub) {
                ids = StubAdapter.getTypeIds( tie ) ;
            } else {
                ids = ((org.omg.PortableServer.Servant)tie).
                      _all_interfaces( null, null );
            }

            if (remoteCodebase == null) {
                remoteCodebase = Util.getCodebase(tie.getClass());
            }

            if (ids.length == 0) {
                stub = new org.omg.stub.java.rmi._Remote_Stub();
            } else {
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i].length() == 0) {
                        stub = new org.omg.stub.java.rmi._Remote_Stub();
                        break;
                    }

                    try {
                        PresentationManager.StubFactoryFactory stubFactoryFactory =
                            com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
                        RepositoryId rid = RepositoryId.cache.getId( ids[i] ) ;
                        String className = rid.getClassName() ;
                        boolean isIDLInterface = rid.isIDLType() ;
                        stubFactory = stubFactoryFactory.createStubFactory(
                            className, isIDLInterface, remoteCodebase, null,
                            tie.getClass().getClassLoader() ) ;
                        stub = stubFactory.makeStub();
                        break;
                    } catch (Exception e) {
                        wrapper.errorInMakeStubFromRepositoryId( e ) ;
                    }

                    if (onlyMostDerived)
                        break;
                }
            }
        }

        if (stub == null) {
            tieToStubCache.put(tie,CACHE_MISS);
        } else {
            if (tieIsStub) {
                try {
                    Delegate del = StubAdapter.getDelegate( tie ) ;
                    StubAdapter.setDelegate( stub, del ) ;
                } catch( Exception e1 ) {
                    synchronized (stubToTieCache) {
                        stubToTieCache.put(stub,tie);
                    }
                }
            } else {
                try {
                    Delegate delegate = StubAdapter.getDelegate( tie ) ;
                    StubAdapter.setDelegate( stub, delegate ) ;
                } catch( org.omg.CORBA.BAD_INV_ORDER bad) {
                    synchronized (stubToTieCache) {
                        stubToTieCache.put(stub,tie);
                    }
                } catch( Exception e ) {
                    throw wrapper.noPoa( e ) ;
                }
            }
            entry = new StubEntry(stub,onlyMostDerived);
            tieToStubCache.put(tie,entry);
        }

        return entry;
    }


    public static Tie getAndForgetTie (org.omg.CORBA.Object stub) {
        synchronized (stubToTieCache) {
            return (Tie) stubToTieCache.remove(stub);
        }
    }


    public static void purgeStubForTie (Tie tie) {
        StubEntry entry;
        synchronized (tieToStubCache) {
            entry = (StubEntry)tieToStubCache.remove(tie);
        }
        if (entry != null) {
            synchronized (stubToTieCache) {
                stubToTieCache.remove(entry.stub);
            }
        }
    }


    public static void purgeTieAndServant (Tie tie) {
        synchronized (tieCache) {
            Object target = tie.getTarget();
            if (target != null)
                tieCache.remove(target);
        }
    }


    public static String stubNameFromRepID (String repID) {

        RepositoryId id = RepositoryId.cache.getId(repID);
        String className = id.getClassName();

        if (id.isIDLType()) {
            className = idlStubName(className);
        } else {
            className = stubName(className);
        }
        return className;
    }



    public static Remote loadStub (org.omg.CORBA.Object narrowFrom,
                                   Class narrowTo)
    {
        Remote result = null;

        try {
            String codebase = null;
            try {
                Delegate delegate = StubAdapter.getDelegate( narrowFrom ) ;
                codebase = ((org.omg.CORBA_2_3.portable.Delegate)delegate).
                    get_codebase(narrowFrom);

            } catch (ClassCastException e) {
                wrapper.classCastExceptionInLoadStub( e ) ;
            }

            PresentationManager.StubFactoryFactory sff =
                com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory() ;
            PresentationManager.StubFactory sf = sff.createStubFactory(
                narrowTo.getName(), false, codebase, narrowTo,
                narrowTo.getClassLoader() ) ;
            result = (Remote)sf.makeStub() ;
            StubAdapter.setDelegate( result,
                StubAdapter.getDelegate( narrowFrom ) ) ;
        } catch (Exception err) {
            wrapper.exceptionInLoadStub( err ) ;
        }

        return result;
    }


    public static Class loadStubClass(String repID,
                                      String remoteCodebase,
                                      Class expectedType)
        throws ClassNotFoundException
    {
        if (repID.length() == 0) {
            throw new ClassNotFoundException();
        }

        String className = Utility.stubNameFromRepID(repID);
        ClassLoader expectedTypeClassLoader = (expectedType == null ? null :
            expectedType.getClassLoader());

        try {
              return loadClassOfType(className,
                                       remoteCodebase,
                                       expectedTypeClassLoader,
                                       expectedType,
                                       expectedTypeClassLoader);
        } catch (ClassNotFoundException e) {
            return loadClassOfType(PackagePrefixChecker.packagePrefix() + className,
                                   remoteCodebase,
                                   expectedTypeClassLoader,
                                   expectedType,
                                   expectedTypeClassLoader);
        }
    }


    public static String stubName (String className)
    {
        return stubName( className, false ) ;
    }

    public static String dynamicStubName( String className )
    {
        return stubName( className, true ) ;
    }

    private static String stubName( String className,
        boolean isDynamic )
    {
        String name = stubNameForCompiler( className, isDynamic ) ;
        if (PackagePrefixChecker.hasOffendingPrefix( name ))
            name = PackagePrefixChecker.packagePrefix() + name ;
        return name ;
    }

    public static String stubNameForCompiler (String className)
    {
        return stubNameForCompiler( className, false ) ;
    }

    private static String stubNameForCompiler( String className,
        boolean isDynamic )
    {
        int index = className.indexOf('$');
        if (index < 0) {
            index = className.lastIndexOf('.');
        }

        String suffix = isDynamic ? DYNAMIC_STUB_SUFFIX :
            RMI_STUB_SUFFIX ;

        if (index > 0) {
            return className.substring(0,index+1) + STUB_PREFIX +
                className.substring(index+1) + suffix;
        } else {
            return STUB_PREFIX + className + suffix;
        }
    }


    public static String tieName (String className)
    {
        return
            PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(className)) ?
            PackagePrefixChecker.packagePrefix() + tieNameForCompiler(className) :
            tieNameForCompiler(className);
    }

    public static String tieNameForCompiler (String className)
    {
        int index = className.indexOf('$');
        if (index < 0) {
            index = className.lastIndexOf('.');
        }
        if (index > 0) {
            return className.substring(0,index+1) +
                STUB_PREFIX +
                className.substring(index+1) +
                TIE_SUFIX;
        } else {
            return STUB_PREFIX +
                className +
                TIE_SUFIX;
        }
    }


    public static void throwNotSerializableForCorba(String className) {
        throw omgWrapper.notSerializable( CompletionStatus.COMPLETED_MAYBE,
            className ) ;
    }


    public static String idlStubName(String className)
    {
        String result = null;
        int index = className.lastIndexOf('.');
        if (index > 0) {
            result = className.substring(0,index+1) +
                STUB_PREFIX +
                className.substring(index+1) +
                IDL_STUB_SUFFIX;
        } else {
            result = STUB_PREFIX +
                className +
                IDL_STUB_SUFFIX;
        }
        return result;
    }

    public static void printStackTrace()
    {
        Throwable thr = new Throwable( "Printing stack trace:" ) ;
        thr.fillInStackTrace() ;
        thr.printStackTrace() ;
    }


    public static Object readObjectAndNarrow(InputStream in,
                                             Class narrowTo)
        throws ClassCastException
    {
        Object result = in.read_Object();
        if (result != null)
            return PortableRemoteObject.narrow(result, narrowTo);
        else
            return null;
    }


    public static Object readAbstractAndNarrow(
        org.omg.CORBA_2_3.portable.InputStream in, Class narrowTo)
        throws ClassCastException
    {
        Object result = in.read_abstract_interface();
        if (result != null)
            return PortableRemoteObject.narrow(result, narrowTo);
        else
            return null;
    }



    static int hexOf( char x )
    {
        int val;

        val = x - '0';
        if (val >=0 && val <= 9)
            return val;

        val = (x - 'a') + 10;
        if (val >= 10 && val <= 15)
            return val;

        val = (x - 'A') + 10;
        if (val >= 10 && val <= 15)
            return val;

        throw wrapper.badHexDigit() ;
    }
}

class StubEntry {
    org.omg.CORBA.Object stub;
    boolean mostDerived;

    StubEntry(org.omg.CORBA.Object stub, boolean mostDerived) {
        this.stub = stub;
        this.mostDerived = mostDerived;
    }
}
