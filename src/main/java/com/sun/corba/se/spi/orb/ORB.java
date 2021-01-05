

package com.sun.corba.se.spi.orb;

import java.util.Map ;
import java.util.HashMap ;
import java.util.Properties ;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger ;

import java.security.AccessController ;
import java.security.PrivilegedAction ;

import org.omg.CORBA.TCKind ;

import com.sun.corba.se.pept.broker.Broker ;
import com.sun.corba.se.pept.transport.ByteBufferPool;

import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry ;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory ;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;
import com.sun.corba.se.spi.protocol.PIHandler ;
import com.sun.corba.se.spi.resolver.LocalResolver ;
import com.sun.corba.se.spi.resolver.Resolver ;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory ;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.monitoring.MonitoringConstants;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;

import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder ;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder ;
import com.sun.corba.se.spi.ior.ObjectKey ;
import com.sun.corba.se.spi.ior.ObjectKeyFactory ;
import com.sun.corba.se.spi.ior.IOR ;

import com.sun.corba.se.spi.orb.Operation ;
import com.sun.corba.se.spi.orb.ORBData ;
import com.sun.corba.se.spi.orb.ORBVersion ;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;

import com.sun.corba.se.spi.oa.OAInvocationInfo ;
import com.sun.corba.se.spi.transport.CorbaTransportManager;

import com.sun.corba.se.spi.logging.LogWrapperFactory ;
import com.sun.corba.se.spi.logging.LogWrapperBase ;
import com.sun.corba.se.spi.logging.CORBALogDomains ;

import com.sun.corba.se.spi.copyobject.CopierManager ;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults ;

import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry ;

import com.sun.corba.se.impl.corba.TypeCodeImpl ;
import com.sun.corba.se.impl.corba.TypeCodeFactory ;

import com.sun.corba.se.impl.orbutil.ORBConstants ;

import com.sun.corba.se.impl.oa.poa.BadServerIdHandler ;

import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;

import com.sun.corba.se.impl.logging.ORBUtilSystemException ;
import com.sun.corba.se.impl.logging.OMGSystemException ;

import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl ;

import sun.awt.AppContext;
import sun.corba.SharedSecrets;

public abstract class ORB extends com.sun.corba.se.org.omg.CORBA.ORB
    implements Broker, TypeCodeFactory
{
    public static boolean ORBInitDebug = false;

    public boolean transportDebugFlag = false ;
    public boolean subcontractDebugFlag = false ;
    public boolean poaDebugFlag = false ;
    public boolean poaConcurrencyDebugFlag = false ;
    public boolean poaFSMDebugFlag = false ;
    public boolean orbdDebugFlag = false ;
    public boolean namingDebugFlag = false ;
    public boolean serviceContextDebugFlag = false ;
    public boolean transientObjectManagerDebugFlag = false ;
    public boolean giopVersionDebugFlag = false;
    public boolean shutdownDebugFlag = false;
    public boolean giopDebugFlag = false;
    public boolean invocationTimingDebugFlag = false ;
    public boolean orbInitDebugFlag = false;

    protected static ORBUtilSystemException staticWrapper ;
    protected ORBUtilSystemException wrapper ;
    protected OMGSystemException omgWrapper ;

    private Map typeCodeMap ;

    private TypeCodeImpl[] primitiveTypeCodeConstants ;

    ByteBufferPool byteBufferPool;

    public abstract boolean isLocalHost( String hostName ) ;
    public abstract boolean isLocalServerId( int subcontractId, int serverId ) ;

    public abstract OAInvocationInfo peekInvocationInfo() ;
    public abstract void pushInvocationInfo( OAInvocationInfo info ) ;
    public abstract OAInvocationInfo popInvocationInfo() ;

    public abstract CorbaTransportManager getCorbaTransportManager();
    public abstract LegacyServerSocketManager getLegacyServerSocketManager();

    private Map wrapperMap ;

    static class Holder {
        static final PresentationManager defaultPresentationManager =
            setupPresentationManager();
    }
    private static final Object pmLock = new Object();

    private static Map staticWrapperMap = new ConcurrentHashMap();

    protected MonitoringManager monitoringManager;

    private static PresentationManager setupPresentationManager() {
        staticWrapper = ORBUtilSystemException.get(
            CORBALogDomains.RPC_PRESENTATION ) ;

        boolean useDynamicStub =
            ((Boolean)AccessController.doPrivileged(
                new PrivilegedAction() {
                    public java.lang.Object run() {
                        return Boolean.valueOf( Boolean.getBoolean (
                            ORBConstants.USE_DYNAMIC_STUB_PROPERTY ) ) ;
                    }
                }
            )).booleanValue() ;

        PresentationManager.StubFactoryFactory dynamicStubFactoryFactory =
            (PresentationManager.StubFactoryFactory)AccessController.doPrivileged(
                new PrivilegedAction() {
                    public java.lang.Object run() {
                        PresentationManager.StubFactoryFactory sff =
                            PresentationDefaults.getProxyStubFactoryFactory() ;

                        String className = System.getProperty(
                            ORBConstants.DYNAMIC_STUB_FACTORY_FACTORY_CLASS,
                            "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl" ) ;

                        try {
                            Class<?> cls = SharedSecrets.getJavaCorbaAccess().loadClass( className ) ;
                            sff = (PresentationManager.StubFactoryFactory)cls.newInstance() ;
                        } catch (Exception exc) {
                            staticWrapper.errorInSettingDynamicStubFactoryFactory(
                                exc, className ) ;
                        }

                        return sff ;
                    }
                }
            ) ;

        PresentationManager pm = new PresentationManagerImpl( useDynamicStub ) ;
        pm.setStubFactoryFactory( false,
            PresentationDefaults.getStaticStubFactoryFactory() ) ;
        pm.setStubFactoryFactory( true, dynamicStubFactoryFactory ) ;
        return pm;
    }

    public void destroy() {
        wrapper = null;
        omgWrapper = null;
        typeCodeMap = null;
        primitiveTypeCodeConstants = null;
        byteBufferPool = null;
    }


    public static PresentationManager getPresentationManager()
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && AppContext.getAppContexts().size() > 0) {
            AppContext ac = AppContext.getAppContext();
            if (ac != null) {
                synchronized (pmLock) {
                    PresentationManager pm =
                        (PresentationManager) ac.get(PresentationManager.class);
                    if (pm == null) {
                        pm = setupPresentationManager();
                        ac.put(PresentationManager.class, pm);
                    }
                    return pm;
                }
            }
        }

        return Holder.defaultPresentationManager;
    }


    public static PresentationManager.StubFactoryFactory
        getStubFactoryFactory()
    {
        PresentationManager gPM = getPresentationManager();
        boolean useDynamicStubs = gPM.useDynamicStubs() ;
        return gPM.getStubFactoryFactory( useDynamicStubs ) ;
    }

    protected ORB()
    {
        wrapperMap = new ConcurrentHashMap();
        wrapper = ORBUtilSystemException.get( this,
            CORBALogDomains.RPC_PRESENTATION ) ;
        omgWrapper = OMGSystemException.get( this,
            CORBALogDomains.RPC_PRESENTATION ) ;

        typeCodeMap = new HashMap();

        primitiveTypeCodeConstants = new TypeCodeImpl[] {
            new TypeCodeImpl(this, TCKind._tk_null),
            new TypeCodeImpl(this, TCKind._tk_void),
            new TypeCodeImpl(this, TCKind._tk_short),
            new TypeCodeImpl(this, TCKind._tk_long),
            new TypeCodeImpl(this, TCKind._tk_ushort),
            new TypeCodeImpl(this, TCKind._tk_ulong),
            new TypeCodeImpl(this, TCKind._tk_float),
            new TypeCodeImpl(this, TCKind._tk_double),
            new TypeCodeImpl(this, TCKind._tk_boolean),
            new TypeCodeImpl(this, TCKind._tk_char),
            new TypeCodeImpl(this, TCKind._tk_octet),
            new TypeCodeImpl(this, TCKind._tk_any),
            new TypeCodeImpl(this, TCKind._tk_TypeCode),
            new TypeCodeImpl(this, TCKind._tk_Principal),
            new TypeCodeImpl(this, TCKind._tk_objref),
            null,       null,       null,       new TypeCodeImpl(this, TCKind._tk_string),
            null,       null,       null,       null,       new TypeCodeImpl(this, TCKind._tk_longlong),
            new TypeCodeImpl(this, TCKind._tk_ulonglong),
            new TypeCodeImpl(this, TCKind._tk_longdouble),
            new TypeCodeImpl(this, TCKind._tk_wchar),
            new TypeCodeImpl(this, TCKind._tk_wstring),
            new TypeCodeImpl(this, TCKind._tk_fixed),
            new TypeCodeImpl(this, TCKind._tk_value),
            new TypeCodeImpl(this, TCKind._tk_value_box),
            new TypeCodeImpl(this, TCKind._tk_native),
            new TypeCodeImpl(this, TCKind._tk_abstract_interface)
        } ;

        monitoringManager =
            MonitoringFactories.getMonitoringManagerFactory( ).
                createMonitoringManager(
                MonitoringConstants.DEFAULT_MONITORING_ROOT,
                MonitoringConstants.DEFAULT_MONITORING_ROOT_DESCRIPTION);
    }

    public TypeCodeImpl get_primitive_tc(int kind)
    {
        synchronized (this) {
            checkShutdownState();
        }
        try {
            return primitiveTypeCodeConstants[kind] ;
        } catch (Throwable t) {
            throw wrapper.invalidTypecodeKind( t, new Integer(kind) ) ;
        }
    }

    public synchronized void setTypeCode(String id, TypeCodeImpl code)
    {
        checkShutdownState();
        typeCodeMap.put(id, code);
    }

    public synchronized TypeCodeImpl getTypeCode(String id)
    {
        checkShutdownState();
        return (TypeCodeImpl)typeCodeMap.get(id);
    }

    public MonitoringManager getMonitoringManager( ) {
        synchronized (this) {
            checkShutdownState();
        }
        return monitoringManager;
    }

    public abstract void set_parameters( Properties props ) ;

    public abstract ORBVersion getORBVersion() ;
    public abstract void setORBVersion( ORBVersion version ) ;

    public abstract IOR getFVDCodeBaseIOR() ;


    public abstract void handleBadServerId( ObjectKey okey ) ;
    public abstract void setBadServerIdHandler( BadServerIdHandler handler ) ;
    public abstract void initBadServerIdHandler() ;

    public abstract void notifyORB() ;

    public abstract PIHandler getPIHandler() ;

    public abstract void checkShutdownState();

    public abstract boolean isDuringDispatch() ;
    public abstract void startingDispatch();
    public abstract void finishedDispatch();


    public abstract int getTransientServerId();

    public abstract ServiceContextRegistry getServiceContextRegistry() ;

    public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();

    public abstract ORBData getORBData() ;

    public abstract void setClientDelegateFactory( ClientDelegateFactory factory ) ;

    public abstract ClientDelegateFactory getClientDelegateFactory() ;

    public abstract void setCorbaContactInfoListFactory( CorbaContactInfoListFactory factory ) ;

    public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory() ;

    public abstract void setResolver( Resolver resolver ) ;


    public abstract Resolver getResolver() ;


    public abstract void setLocalResolver( LocalResolver resolver ) ;


    public abstract LocalResolver getLocalResolver() ;


    public abstract void setURLOperation( Operation stringToObject ) ;


    public abstract Operation getURLOperation() ;


    public abstract void setINSDelegate( CorbaServerRequestDispatcher insDelegate ) ;

    public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() ;
    public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder() ;
    public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() ;

    public abstract ObjectKeyFactory getObjectKeyFactory() ;
    public abstract void setObjectKeyFactory( ObjectKeyFactory factory ) ;

    public Logger getLogger( String domain )
    {
        synchronized (this) {
            checkShutdownState();
        }
        ORBData odata = getORBData() ;

        String ORBId ;
        if (odata == null)
            ORBId = "_INITIALIZING_" ;
        else {
            ORBId = odata.getORBId() ;
            if (ORBId.equals(""))
                ORBId = "_DEFAULT_" ;
        }

        return getCORBALogger( ORBId, domain ) ;
    }

    public static Logger staticGetLogger( String domain )
    {
        return getCORBALogger( "_CORBA_", domain ) ;
    }

    private static Logger getCORBALogger( String ORBId, String domain )
    {
        String fqLogDomain = CORBALogDomains.TOP_LEVEL_DOMAIN + "." +
            ORBId + "." + domain;

        return Logger.getLogger( fqLogDomain, ORBConstants.LOG_RESOURCE_FILE );
    }


    public LogWrapperBase getLogWrapper( String logDomain,
        String exceptionGroup, LogWrapperFactory factory )
    {
        StringPair key = new StringPair( logDomain, exceptionGroup ) ;

        LogWrapperBase logWrapper = (LogWrapperBase)wrapperMap.get( key );
        if (logWrapper == null) {
            logWrapper = factory.create( getLogger( logDomain ) );
            wrapperMap.put( key, logWrapper );
        }

        return logWrapper;
    }


    public static LogWrapperBase staticGetLogWrapper( String logDomain,
        String exceptionGroup, LogWrapperFactory factory )
    {
        StringPair key = new StringPair( logDomain, exceptionGroup ) ;

        LogWrapperBase logWrapper = (LogWrapperBase)staticWrapperMap.get( key );
        if (logWrapper == null) {
            logWrapper = factory.create( staticGetLogger( logDomain ) );
            staticWrapperMap.put( key, logWrapper );
        }

        return logWrapper;
    }

    public ByteBufferPool getByteBufferPool()
    {
        synchronized (this) {
            checkShutdownState();
        }
        if (byteBufferPool == null)
            byteBufferPool = new ByteBufferPoolImpl(this);

        return byteBufferPool;
    }

    public abstract void setThreadPoolManager(ThreadPoolManager mgr);

    public abstract ThreadPoolManager getThreadPoolManager();

    public abstract CopierManager getCopierManager() ;


    public abstract void validateIORClass(String iorClassName);

}

