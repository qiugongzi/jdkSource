




package com.sun.corba.se.impl.logging ;

import java.util.logging.Logger ;
import java.util.logging.Level ;

import org.omg.CORBA.OMGVMCID ;
import com.sun.corba.se.impl.util.SUNVMCID ;
import org.omg.CORBA.CompletionStatus ;
import org.omg.CORBA.SystemException ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.logging.LogWrapperFactory;

import com.sun.corba.se.spi.logging.LogWrapperBase;

import org.omg.CORBA.BAD_CONTEXT ;
import org.omg.CORBA.BAD_INV_ORDER ;
import org.omg.CORBA.BAD_OPERATION ;
import org.omg.CORBA.BAD_PARAM ;
import org.omg.CORBA.BAD_TYPECODE ;
import org.omg.CORBA.DATA_CONVERSION ;
import org.omg.CORBA.IMP_LIMIT ;
import org.omg.CORBA.INITIALIZE ;
import org.omg.CORBA.INV_OBJREF ;
import org.omg.CORBA.INV_POLICY ;
import org.omg.CORBA.INTERNAL ;
import org.omg.CORBA.INTF_REPOS ;
import org.omg.CORBA.MARSHAL ;
import org.omg.CORBA.NO_IMPLEMENT ;
import org.omg.CORBA.NO_RESOURCES ;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK ;
import org.omg.CORBA.TRANSIENT ;
import org.omg.CORBA.OBJECT_NOT_EXIST ;
import org.omg.CORBA.OBJ_ADAPTER ;
import org.omg.CORBA.UNKNOWN ;

public class OMGSystemException extends LogWrapperBase {
    
    public OMGSystemException( Logger logger )
    {
        super( logger ) ;
    }
    
    private static LogWrapperFactory factory = new LogWrapperFactory() {
        public LogWrapperBase create( Logger logger )
        {
            return new OMGSystemException( logger ) ;
        }
    } ;
    
    public static OMGSystemException get( ORB orb, String logDomain )
    {
        OMGSystemException wrapper = 
            (OMGSystemException) orb.getLogWrapper( logDomain, 
                "OMG", factory ) ;
        return wrapper ;
    } 
    
    public static OMGSystemException get( String logDomain )
    {
        OMGSystemException wrapper = 
            (OMGSystemException) ORB.staticGetLogWrapper( logDomain, 
                "OMG", factory ) ;
        return wrapper ;
    } 
    



    
    public static final int IDL_CONTEXT_NOT_FOUND = OMGVMCID.value + 1 ;
    
    public BAD_CONTEXT idlContextNotFound( CompletionStatus cs, Throwable t ) {
        BAD_CONTEXT exc = new BAD_CONTEXT( IDL_CONTEXT_NOT_FOUND, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.idlContextNotFound",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_CONTEXT idlContextNotFound( CompletionStatus cs ) {
        return idlContextNotFound( cs, null  ) ;
    }
    
    public BAD_CONTEXT idlContextNotFound( Throwable t ) {
        return idlContextNotFound( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_CONTEXT idlContextNotFound(  ) {
        return idlContextNotFound( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_MATCHING_IDL_CONTEXT = OMGVMCID.value + 2 ;
    
    public BAD_CONTEXT noMatchingIdlContext( CompletionStatus cs, Throwable t ) {
        BAD_CONTEXT exc = new BAD_CONTEXT( NO_MATCHING_IDL_CONTEXT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noMatchingIdlContext",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_CONTEXT noMatchingIdlContext( CompletionStatus cs ) {
        return noMatchingIdlContext( cs, null  ) ;
    }
    
    public BAD_CONTEXT noMatchingIdlContext( Throwable t ) {
        return noMatchingIdlContext( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_CONTEXT noMatchingIdlContext(  ) {
        return noMatchingIdlContext( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int DEP_PREVENT_DESTRUCTION = OMGVMCID.value + 1 ;
    
    public BAD_INV_ORDER depPreventDestruction( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( DEP_PREVENT_DESTRUCTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.depPreventDestruction",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER depPreventDestruction( CompletionStatus cs ) {
        return depPreventDestruction( cs, null  ) ;
    }
    
    public BAD_INV_ORDER depPreventDestruction( Throwable t ) {
        return depPreventDestruction( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER depPreventDestruction(  ) {
        return depPreventDestruction( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int DESTROY_INDESTRUCTIBLE = OMGVMCID.value + 2 ;
    
    public BAD_INV_ORDER destroyIndestructible( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( DESTROY_INDESTRUCTIBLE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.destroyIndestructible",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER destroyIndestructible( CompletionStatus cs ) {
        return destroyIndestructible( cs, null  ) ;
    }
    
    public BAD_INV_ORDER destroyIndestructible( Throwable t ) {
        return destroyIndestructible( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER destroyIndestructible(  ) {
        return destroyIndestructible( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK = OMGVMCID.value + 3 ;
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.shutdownWaitForCompletionDeadlock",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock( CompletionStatus cs ) {
        return shutdownWaitForCompletionDeadlock( cs, null  ) ;
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock( Throwable t ) {
        return shutdownWaitForCompletionDeadlock( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(  ) {
        return shutdownWaitForCompletionDeadlock( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_OPERATION_AFTER_SHUTDOWN = OMGVMCID.value + 4 ;
    
    public BAD_INV_ORDER badOperationAfterShutdown( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_OPERATION_AFTER_SHUTDOWN, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badOperationAfterShutdown",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown( CompletionStatus cs ) {
        return badOperationAfterShutdown( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown( Throwable t ) {
        return badOperationAfterShutdown( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown(  ) {
        return badOperationAfterShutdown( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_INVOKE = OMGVMCID.value + 5 ;
    
    public BAD_INV_ORDER badInvoke( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_INVOKE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badInvoke",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badInvoke( CompletionStatus cs ) {
        return badInvoke( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badInvoke( Throwable t ) {
        return badInvoke( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badInvoke(  ) {
        return badInvoke( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_SET_SERVANT_MANAGER = OMGVMCID.value + 6 ;
    
    public BAD_INV_ORDER badSetServantManager( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_SET_SERVANT_MANAGER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badSetServantManager",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badSetServantManager( CompletionStatus cs ) {
        return badSetServantManager( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badSetServantManager( Throwable t ) {
        return badSetServantManager( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badSetServantManager(  ) {
        return badSetServantManager( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_ARGUMENTS_CALL = OMGVMCID.value + 7 ;
    
    public BAD_INV_ORDER badArgumentsCall( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_ARGUMENTS_CALL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badArgumentsCall",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badArgumentsCall( CompletionStatus cs ) {
        return badArgumentsCall( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badArgumentsCall( Throwable t ) {
        return badArgumentsCall( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badArgumentsCall(  ) {
        return badArgumentsCall( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_CTX_CALL = OMGVMCID.value + 8 ;
    
    public BAD_INV_ORDER badCtxCall( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_CTX_CALL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badCtxCall",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badCtxCall( CompletionStatus cs ) {
        return badCtxCall( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badCtxCall( Throwable t ) {
        return badCtxCall( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badCtxCall(  ) {
        return badCtxCall( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_RESULT_CALL = OMGVMCID.value + 9 ;
    
    public BAD_INV_ORDER badResultCall( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_RESULT_CALL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badResultCall",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badResultCall( CompletionStatus cs ) {
        return badResultCall( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badResultCall( Throwable t ) {
        return badResultCall( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badResultCall(  ) {
        return badResultCall( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_SEND = OMGVMCID.value + 10 ;
    
    public BAD_INV_ORDER badSend( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_SEND, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badSend",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badSend( CompletionStatus cs ) {
        return badSend( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badSend( Throwable t ) {
        return badSend( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badSend(  ) {
        return badSend( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_POLL_BEFORE = OMGVMCID.value + 11 ;
    
    public BAD_INV_ORDER badPollBefore( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_POLL_BEFORE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badPollBefore",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badPollBefore( CompletionStatus cs ) {
        return badPollBefore( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badPollBefore( Throwable t ) {
        return badPollBefore( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badPollBefore(  ) {
        return badPollBefore( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_POLL_AFTER = OMGVMCID.value + 12 ;
    
    public BAD_INV_ORDER badPollAfter( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_POLL_AFTER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badPollAfter",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badPollAfter( CompletionStatus cs ) {
        return badPollAfter( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badPollAfter( Throwable t ) {
        return badPollAfter( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badPollAfter(  ) {
        return badPollAfter( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_POLL_SYNC = OMGVMCID.value + 13 ;
    
    public BAD_INV_ORDER badPollSync( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( BAD_POLL_SYNC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badPollSync",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER badPollSync( CompletionStatus cs ) {
        return badPollSync( cs, null  ) ;
    }
    
    public BAD_INV_ORDER badPollSync( Throwable t ) {
        return badPollSync( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER badPollSync(  ) {
        return badPollSync( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_PI_CALL1 = OMGVMCID.value + 14 ;
    
    public BAD_INV_ORDER invalidPiCall1( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( INVALID_PI_CALL1, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.invalidPiCall1",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER invalidPiCall1( CompletionStatus cs ) {
        return invalidPiCall1( cs, null  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall1( Throwable t ) {
        return invalidPiCall1( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall1(  ) {
        return invalidPiCall1( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_PI_CALL2 = OMGVMCID.value + 14 ;
    
    public BAD_INV_ORDER invalidPiCall2( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( INVALID_PI_CALL2, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.invalidPiCall2",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER invalidPiCall2( CompletionStatus cs ) {
        return invalidPiCall2( cs, null  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall2( Throwable t ) {
        return invalidPiCall2( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall2(  ) {
        return invalidPiCall2( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_PI_CALL3 = OMGVMCID.value + 14 ;
    
    public BAD_INV_ORDER invalidPiCall3( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( INVALID_PI_CALL3, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.invalidPiCall3",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER invalidPiCall3( CompletionStatus cs ) {
        return invalidPiCall3( cs, null  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall3( Throwable t ) {
        return invalidPiCall3( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall3(  ) {
        return invalidPiCall3( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_PI_CALL4 = OMGVMCID.value + 14 ;
    
    public BAD_INV_ORDER invalidPiCall4( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( INVALID_PI_CALL4, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.invalidPiCall4",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER invalidPiCall4( CompletionStatus cs ) {
        return invalidPiCall4( cs, null  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall4( Throwable t ) {
        return invalidPiCall4( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER invalidPiCall4(  ) {
        return invalidPiCall4( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVICE_CONTEXT_ADD_FAILED = OMGVMCID.value + 15 ;
    
    public BAD_INV_ORDER serviceContextAddFailed( CompletionStatus cs, Throwable t, Object arg0) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( SERVICE_CONTEXT_ADD_FAILED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.FINE, "OMG.serviceContextAddFailed",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER serviceContextAddFailed( CompletionStatus cs, Object arg0) {
        return serviceContextAddFailed( cs, null, arg0 ) ;
    }
    
    public BAD_INV_ORDER serviceContextAddFailed( Throwable t, Object arg0) {
        return serviceContextAddFailed( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public BAD_INV_ORDER serviceContextAddFailed(  Object arg0) {
        return serviceContextAddFailed( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int POLICY_FACTORY_REG_FAILED = OMGVMCID.value + 16 ;
    
    public BAD_INV_ORDER policyFactoryRegFailed( CompletionStatus cs, Throwable t, Object arg0) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( POLICY_FACTORY_REG_FAILED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.WARNING, "OMG.policyFactoryRegFailed",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed( CompletionStatus cs, Object arg0) {
        return policyFactoryRegFailed( cs, null, arg0 ) ;
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed( Throwable t, Object arg0) {
        return policyFactoryRegFailed( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed(  Object arg0) {
        return policyFactoryRegFailed( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int CREATE_POA_DESTROY = OMGVMCID.value + 17 ;
    
    public BAD_INV_ORDER createPoaDestroy( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( CREATE_POA_DESTROY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.createPoaDestroy",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER createPoaDestroy( CompletionStatus cs ) {
        return createPoaDestroy( cs, null  ) ;
    }
    
    public BAD_INV_ORDER createPoaDestroy( Throwable t ) {
        return createPoaDestroy( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER createPoaDestroy(  ) {
        return createPoaDestroy( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PRIORITY_REASSIGN = OMGVMCID.value + 18 ;
    
    public BAD_INV_ORDER priorityReassign( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( PRIORITY_REASSIGN, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.priorityReassign",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER priorityReassign( CompletionStatus cs ) {
        return priorityReassign( cs, null  ) ;
    }
    
    public BAD_INV_ORDER priorityReassign( Throwable t ) {
        return priorityReassign( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER priorityReassign(  ) {
        return priorityReassign( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_START_OUTSIZE = OMGVMCID.value + 19 ;
    
    public BAD_INV_ORDER xaStartOutsize( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( XA_START_OUTSIZE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaStartOutsize",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER xaStartOutsize( CompletionStatus cs ) {
        return xaStartOutsize( cs, null  ) ;
    }
    
    public BAD_INV_ORDER xaStartOutsize( Throwable t ) {
        return xaStartOutsize( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER xaStartOutsize(  ) {
        return xaStartOutsize( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_START_PROTO = OMGVMCID.value + 20 ;
    
    public BAD_INV_ORDER xaStartProto( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( XA_START_PROTO, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaStartProto",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER xaStartProto( CompletionStatus cs ) {
        return xaStartProto( cs, null  ) ;
    }
    
    public BAD_INV_ORDER xaStartProto( Throwable t ) {
        return xaStartProto( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER xaStartProto(  ) {
        return xaStartProto( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int BAD_SERVANT_MANAGER_TYPE = OMGVMCID.value + 1 ;
    
    public BAD_OPERATION badServantManagerType( CompletionStatus cs, Throwable t ) {
        BAD_OPERATION exc = new BAD_OPERATION( BAD_SERVANT_MANAGER_TYPE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badServantManagerType",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_OPERATION badServantManagerType( CompletionStatus cs ) {
        return badServantManagerType( cs, null  ) ;
    }
    
    public BAD_OPERATION badServantManagerType( Throwable t ) {
        return badServantManagerType( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_OPERATION badServantManagerType(  ) {
        return badServantManagerType( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int OPERATION_UNKNOWN_TO_TARGET = OMGVMCID.value + 2 ;
    
    public BAD_OPERATION operationUnknownToTarget( CompletionStatus cs, Throwable t ) {
        BAD_OPERATION exc = new BAD_OPERATION( OPERATION_UNKNOWN_TO_TARGET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.operationUnknownToTarget",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_OPERATION operationUnknownToTarget( CompletionStatus cs ) {
        return operationUnknownToTarget( cs, null  ) ;
    }
    
    public BAD_OPERATION operationUnknownToTarget( Throwable t ) {
        return operationUnknownToTarget( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_OPERATION operationUnknownToTarget(  ) {
        return operationUnknownToTarget( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int UNABLE_REGISTER_VALUE_FACTORY = OMGVMCID.value + 1 ;
    
    public BAD_PARAM unableRegisterValueFactory( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( UNABLE_REGISTER_VALUE_FACTORY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.unableRegisterValueFactory",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM unableRegisterValueFactory( CompletionStatus cs ) {
        return unableRegisterValueFactory( cs, null  ) ;
    }
    
    public BAD_PARAM unableRegisterValueFactory( Throwable t ) {
        return unableRegisterValueFactory( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM unableRegisterValueFactory(  ) {
        return unableRegisterValueFactory( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int RID_ALREADY_DEFINED = OMGVMCID.value + 2 ;
    
    public BAD_PARAM ridAlreadyDefined( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( RID_ALREADY_DEFINED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.ridAlreadyDefined",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM ridAlreadyDefined( CompletionStatus cs ) {
        return ridAlreadyDefined( cs, null  ) ;
    }
    
    public BAD_PARAM ridAlreadyDefined( Throwable t ) {
        return ridAlreadyDefined( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM ridAlreadyDefined(  ) {
        return ridAlreadyDefined( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NAME_USED_IFR = OMGVMCID.value + 3 ;
    
    public BAD_PARAM nameUsedIfr( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( NAME_USED_IFR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.nameUsedIfr",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM nameUsedIfr( CompletionStatus cs ) {
        return nameUsedIfr( cs, null  ) ;
    }
    
    public BAD_PARAM nameUsedIfr( Throwable t ) {
        return nameUsedIfr( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM nameUsedIfr(  ) {
        return nameUsedIfr( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TARGET_NOT_CONTAINER = OMGVMCID.value + 4 ;
    
    public BAD_PARAM targetNotContainer( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TARGET_NOT_CONTAINER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.targetNotContainer",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM targetNotContainer( CompletionStatus cs ) {
        return targetNotContainer( cs, null  ) ;
    }
    
    public BAD_PARAM targetNotContainer( Throwable t ) {
        return targetNotContainer( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM targetNotContainer(  ) {
        return targetNotContainer( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NAME_CLASH = OMGVMCID.value + 5 ;
    
    public BAD_PARAM nameClash( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( NAME_CLASH, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.nameClash",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM nameClash( CompletionStatus cs ) {
        return nameClash( cs, null  ) ;
    }
    
    public BAD_PARAM nameClash( Throwable t ) {
        return nameClash( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM nameClash(  ) {
        return nameClash( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NOT_SERIALIZABLE = OMGVMCID.value + 6 ;
    
    public BAD_PARAM notSerializable( CompletionStatus cs, Throwable t, Object arg0) {
        BAD_PARAM exc = new BAD_PARAM( NOT_SERIALIZABLE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.WARNING, "OMG.notSerializable",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM notSerializable( CompletionStatus cs, Object arg0) {
        return notSerializable( cs, null, arg0 ) ;
    }
    
    public BAD_PARAM notSerializable( Throwable t, Object arg0) {
        return notSerializable( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public BAD_PARAM notSerializable(  Object arg0) {
        return notSerializable( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int SO_BAD_SCHEME_NAME = OMGVMCID.value + 7 ;
    
    public BAD_PARAM soBadSchemeName( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SO_BAD_SCHEME_NAME, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.soBadSchemeName",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM soBadSchemeName( CompletionStatus cs ) {
        return soBadSchemeName( cs, null  ) ;
    }
    
    public BAD_PARAM soBadSchemeName( Throwable t ) {
        return soBadSchemeName( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM soBadSchemeName(  ) {
        return soBadSchemeName( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SO_BAD_ADDRESS = OMGVMCID.value + 8 ;
    
    public BAD_PARAM soBadAddress( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SO_BAD_ADDRESS, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.soBadAddress",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM soBadAddress( CompletionStatus cs ) {
        return soBadAddress( cs, null  ) ;
    }
    
    public BAD_PARAM soBadAddress( Throwable t ) {
        return soBadAddress( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM soBadAddress(  ) {
        return soBadAddress( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SO_BAD_SCHEMA_SPECIFIC = OMGVMCID.value + 9 ;
    
    public BAD_PARAM soBadSchemaSpecific( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SO_BAD_SCHEMA_SPECIFIC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.soBadSchemaSpecific",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM soBadSchemaSpecific( CompletionStatus cs ) {
        return soBadSchemaSpecific( cs, null  ) ;
    }
    
    public BAD_PARAM soBadSchemaSpecific( Throwable t ) {
        return soBadSchemaSpecific( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM soBadSchemaSpecific(  ) {
        return soBadSchemaSpecific( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SO_NON_SPECIFIC = OMGVMCID.value + 10 ;
    
    public BAD_PARAM soNonSpecific( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SO_NON_SPECIFIC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.soNonSpecific",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM soNonSpecific( CompletionStatus cs ) {
        return soNonSpecific( cs, null  ) ;
    }
    
    public BAD_PARAM soNonSpecific( Throwable t ) {
        return soNonSpecific( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM soNonSpecific(  ) {
        return soNonSpecific( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int IR_DERIVE_ABS_INT_BASE = OMGVMCID.value + 11 ;
    
    public BAD_PARAM irDeriveAbsIntBase( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( IR_DERIVE_ABS_INT_BASE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.irDeriveAbsIntBase",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM irDeriveAbsIntBase( CompletionStatus cs ) {
        return irDeriveAbsIntBase( cs, null  ) ;
    }
    
    public BAD_PARAM irDeriveAbsIntBase( Throwable t ) {
        return irDeriveAbsIntBase( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM irDeriveAbsIntBase(  ) {
        return irDeriveAbsIntBase( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int IR_VALUE_SUPPORT = OMGVMCID.value + 12 ;
    
    public BAD_PARAM irValueSupport( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( IR_VALUE_SUPPORT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.irValueSupport",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM irValueSupport( CompletionStatus cs ) {
        return irValueSupport( cs, null  ) ;
    }
    
    public BAD_PARAM irValueSupport( Throwable t ) {
        return irValueSupport( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM irValueSupport(  ) {
        return irValueSupport( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INCOMPLETE_TYPECODE = OMGVMCID.value + 13 ;
    
    public BAD_PARAM incompleteTypecode( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( INCOMPLETE_TYPECODE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.incompleteTypecode",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM incompleteTypecode( CompletionStatus cs ) {
        return incompleteTypecode( cs, null  ) ;
    }
    
    public BAD_PARAM incompleteTypecode( Throwable t ) {
        return incompleteTypecode( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM incompleteTypecode(  ) {
        return incompleteTypecode( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_OBJECT_ID = OMGVMCID.value + 14 ;
    
    public BAD_PARAM invalidObjectId( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( INVALID_OBJECT_ID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.invalidObjectId",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM invalidObjectId( CompletionStatus cs ) {
        return invalidObjectId( cs, null  ) ;
    }
    
    public BAD_PARAM invalidObjectId( Throwable t ) {
        return invalidObjectId( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM invalidObjectId(  ) {
        return invalidObjectId( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TYPECODE_BAD_NAME = OMGVMCID.value + 15 ;
    
    public BAD_PARAM typecodeBadName( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TYPECODE_BAD_NAME, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.typecodeBadName",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM typecodeBadName( CompletionStatus cs ) {
        return typecodeBadName( cs, null  ) ;
    }
    
    public BAD_PARAM typecodeBadName( Throwable t ) {
        return typecodeBadName( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM typecodeBadName(  ) {
        return typecodeBadName( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TYPECODE_BAD_REPID = OMGVMCID.value + 16 ;
    
    public BAD_PARAM typecodeBadRepid( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TYPECODE_BAD_REPID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.typecodeBadRepid",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM typecodeBadRepid( CompletionStatus cs ) {
        return typecodeBadRepid( cs, null  ) ;
    }
    
    public BAD_PARAM typecodeBadRepid( Throwable t ) {
        return typecodeBadRepid( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM typecodeBadRepid(  ) {
        return typecodeBadRepid( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TYPECODE_INV_MEMBER = OMGVMCID.value + 17 ;
    
    public BAD_PARAM typecodeInvMember( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TYPECODE_INV_MEMBER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.typecodeInvMember",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM typecodeInvMember( CompletionStatus cs ) {
        return typecodeInvMember( cs, null  ) ;
    }
    
    public BAD_PARAM typecodeInvMember( Throwable t ) {
        return typecodeInvMember( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM typecodeInvMember(  ) {
        return typecodeInvMember( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TC_UNION_DUP_LABEL = OMGVMCID.value + 18 ;
    
    public BAD_PARAM tcUnionDupLabel( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TC_UNION_DUP_LABEL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.tcUnionDupLabel",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM tcUnionDupLabel( CompletionStatus cs ) {
        return tcUnionDupLabel( cs, null  ) ;
    }
    
    public BAD_PARAM tcUnionDupLabel( Throwable t ) {
        return tcUnionDupLabel( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM tcUnionDupLabel(  ) {
        return tcUnionDupLabel( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TC_UNION_INCOMPATIBLE = OMGVMCID.value + 19 ;
    
    public BAD_PARAM tcUnionIncompatible( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TC_UNION_INCOMPATIBLE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.tcUnionIncompatible",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM tcUnionIncompatible( CompletionStatus cs ) {
        return tcUnionIncompatible( cs, null  ) ;
    }
    
    public BAD_PARAM tcUnionIncompatible( Throwable t ) {
        return tcUnionIncompatible( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM tcUnionIncompatible(  ) {
        return tcUnionIncompatible( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int TC_UNION_BAD_DISC = OMGVMCID.value + 20 ;
    
    public BAD_PARAM tcUnionBadDisc( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( TC_UNION_BAD_DISC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.tcUnionBadDisc",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM tcUnionBadDisc( CompletionStatus cs ) {
        return tcUnionBadDisc( cs, null  ) ;
    }
    
    public BAD_PARAM tcUnionBadDisc( Throwable t ) {
        return tcUnionBadDisc( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM tcUnionBadDisc(  ) {
        return tcUnionBadDisc( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SET_EXCEPTION_BAD_ANY = OMGVMCID.value + 21 ;
    
    public BAD_PARAM setExceptionBadAny( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SET_EXCEPTION_BAD_ANY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.setExceptionBadAny",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM setExceptionBadAny( CompletionStatus cs ) {
        return setExceptionBadAny( cs, null  ) ;
    }
    
    public BAD_PARAM setExceptionBadAny( Throwable t ) {
        return setExceptionBadAny( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM setExceptionBadAny(  ) {
        return setExceptionBadAny( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SET_EXCEPTION_UNLISTED = OMGVMCID.value + 22 ;
    
    public BAD_PARAM setExceptionUnlisted( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( SET_EXCEPTION_UNLISTED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.setExceptionUnlisted",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM setExceptionUnlisted( CompletionStatus cs ) {
        return setExceptionUnlisted( cs, null  ) ;
    }
    
    public BAD_PARAM setExceptionUnlisted( Throwable t ) {
        return setExceptionUnlisted( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM setExceptionUnlisted(  ) {
        return setExceptionUnlisted( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_CLIENT_WCHAR_CODESET_CTX = OMGVMCID.value + 23 ;
    
    public BAD_PARAM noClientWcharCodesetCtx( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( NO_CLIENT_WCHAR_CODESET_CTX, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noClientWcharCodesetCtx",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM noClientWcharCodesetCtx( CompletionStatus cs ) {
        return noClientWcharCodesetCtx( cs, null  ) ;
    }
    
    public BAD_PARAM noClientWcharCodesetCtx( Throwable t ) {
        return noClientWcharCodesetCtx( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM noClientWcharCodesetCtx(  ) {
        return noClientWcharCodesetCtx( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ILLEGAL_SERVICE_CONTEXT = OMGVMCID.value + 24 ;
    
    public BAD_PARAM illegalServiceContext( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( ILLEGAL_SERVICE_CONTEXT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.illegalServiceContext",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM illegalServiceContext( CompletionStatus cs ) {
        return illegalServiceContext( cs, null  ) ;
    }
    
    public BAD_PARAM illegalServiceContext( Throwable t ) {
        return illegalServiceContext( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM illegalServiceContext(  ) {
        return illegalServiceContext( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ENUM_OUT_OF_RANGE = OMGVMCID.value + 25 ;
    
    public BAD_PARAM enumOutOfRange( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( ENUM_OUT_OF_RANGE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.enumOutOfRange",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM enumOutOfRange( CompletionStatus cs ) {
        return enumOutOfRange( cs, null  ) ;
    }
    
    public BAD_PARAM enumOutOfRange( Throwable t ) {
        return enumOutOfRange( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM enumOutOfRange(  ) {
        return enumOutOfRange( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_SERVICE_CONTEXT_ID = OMGVMCID.value + 26 ;
    
    public BAD_PARAM invalidServiceContextId( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( INVALID_SERVICE_CONTEXT_ID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.invalidServiceContextId",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM invalidServiceContextId( CompletionStatus cs ) {
        return invalidServiceContextId( cs, null  ) ;
    }
    
    public BAD_PARAM invalidServiceContextId( Throwable t ) {
        return invalidServiceContextId( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM invalidServiceContextId(  ) {
        return invalidServiceContextId( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int RIR_WITH_NULL_OBJECT = OMGVMCID.value + 27 ;
    
    public BAD_PARAM rirWithNullObject( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( RIR_WITH_NULL_OBJECT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.rirWithNullObject",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM rirWithNullObject( CompletionStatus cs ) {
        return rirWithNullObject( cs, null  ) ;
    }
    
    public BAD_PARAM rirWithNullObject( Throwable t ) {
        return rirWithNullObject( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM rirWithNullObject(  ) {
        return rirWithNullObject( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_COMPONENT_ID = OMGVMCID.value + 28 ;
    
    public BAD_PARAM invalidComponentId( CompletionStatus cs, Throwable t, Object arg0) {
        BAD_PARAM exc = new BAD_PARAM( INVALID_COMPONENT_ID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.FINE, "OMG.invalidComponentId",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM invalidComponentId( CompletionStatus cs, Object arg0) {
        return invalidComponentId( cs, null, arg0 ) ;
    }
    
    public BAD_PARAM invalidComponentId( Throwable t, Object arg0) {
        return invalidComponentId( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public BAD_PARAM invalidComponentId(  Object arg0) {
        return invalidComponentId( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int INVALID_PROFILE_ID = OMGVMCID.value + 29 ;
    
    public BAD_PARAM invalidProfileId( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( INVALID_PROFILE_ID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.invalidProfileId",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM invalidProfileId( CompletionStatus cs ) {
        return invalidProfileId( cs, null  ) ;
    }
    
    public BAD_PARAM invalidProfileId( Throwable t ) {
        return invalidProfileId( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM invalidProfileId(  ) {
        return invalidProfileId( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POLICY_TYPE_DUPLICATE = OMGVMCID.value + 30 ;
    
    public BAD_PARAM policyTypeDuplicate( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( POLICY_TYPE_DUPLICATE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.policyTypeDuplicate",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM policyTypeDuplicate( CompletionStatus cs ) {
        return policyTypeDuplicate( cs, null  ) ;
    }
    
    public BAD_PARAM policyTypeDuplicate( Throwable t ) {
        return policyTypeDuplicate( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM policyTypeDuplicate(  ) {
        return policyTypeDuplicate( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_ONEWAY_DEFINITION = OMGVMCID.value + 31 ;
    
    public BAD_PARAM badOnewayDefinition( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( BAD_ONEWAY_DEFINITION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badOnewayDefinition",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM badOnewayDefinition( CompletionStatus cs ) {
        return badOnewayDefinition( cs, null  ) ;
    }
    
    public BAD_PARAM badOnewayDefinition( Throwable t ) {
        return badOnewayDefinition( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM badOnewayDefinition(  ) {
        return badOnewayDefinition( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int DII_FOR_IMPLICIT_OPERATION = OMGVMCID.value + 32 ;
    
    public BAD_PARAM diiForImplicitOperation( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( DII_FOR_IMPLICIT_OPERATION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.diiForImplicitOperation",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM diiForImplicitOperation( CompletionStatus cs ) {
        return diiForImplicitOperation( cs, null  ) ;
    }
    
    public BAD_PARAM diiForImplicitOperation( Throwable t ) {
        return diiForImplicitOperation( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM diiForImplicitOperation(  ) {
        return diiForImplicitOperation( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_CALL_INVAL = OMGVMCID.value + 33 ;
    
    public BAD_PARAM xaCallInval( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( XA_CALL_INVAL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaCallInval",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM xaCallInval( CompletionStatus cs ) {
        return xaCallInval( cs, null  ) ;
    }
    
    public BAD_PARAM xaCallInval( Throwable t ) {
        return xaCallInval( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM xaCallInval(  ) {
        return xaCallInval( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNION_BAD_DISCRIMINATOR = OMGVMCID.value + 34 ;
    
    public BAD_PARAM unionBadDiscriminator( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( UNION_BAD_DISCRIMINATOR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.unionBadDiscriminator",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM unionBadDiscriminator( CompletionStatus cs ) {
        return unionBadDiscriminator( cs, null  ) ;
    }
    
    public BAD_PARAM unionBadDiscriminator( Throwable t ) {
        return unionBadDiscriminator( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM unionBadDiscriminator(  ) {
        return unionBadDiscriminator( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CTX_ILLEGAL_PROPERTY_NAME = OMGVMCID.value + 35 ;
    
    public BAD_PARAM ctxIllegalPropertyName( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( CTX_ILLEGAL_PROPERTY_NAME, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.ctxIllegalPropertyName",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM ctxIllegalPropertyName( CompletionStatus cs ) {
        return ctxIllegalPropertyName( cs, null  ) ;
    }
    
    public BAD_PARAM ctxIllegalPropertyName( Throwable t ) {
        return ctxIllegalPropertyName( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM ctxIllegalPropertyName(  ) {
        return ctxIllegalPropertyName( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CTX_ILLEGAL_SEARCH_STRING = OMGVMCID.value + 36 ;
    
    public BAD_PARAM ctxIllegalSearchString( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( CTX_ILLEGAL_SEARCH_STRING, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.ctxIllegalSearchString",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM ctxIllegalSearchString( CompletionStatus cs ) {
        return ctxIllegalSearchString( cs, null  ) ;
    }
    
    public BAD_PARAM ctxIllegalSearchString( Throwable t ) {
        return ctxIllegalSearchString( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM ctxIllegalSearchString(  ) {
        return ctxIllegalSearchString( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CTX_ILLEGAL_NAME = OMGVMCID.value + 37 ;
    
    public BAD_PARAM ctxIllegalName( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( CTX_ILLEGAL_NAME, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.ctxIllegalName",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM ctxIllegalName( CompletionStatus cs ) {
        return ctxIllegalName( cs, null  ) ;
    }
    
    public BAD_PARAM ctxIllegalName( Throwable t ) {
        return ctxIllegalName( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM ctxIllegalName(  ) {
        return ctxIllegalName( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CTX_NON_EMPTY = OMGVMCID.value + 38 ;
    
    public BAD_PARAM ctxNonEmpty( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( CTX_NON_EMPTY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.ctxNonEmpty",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM ctxNonEmpty( CompletionStatus cs ) {
        return ctxNonEmpty( cs, null  ) ;
    }
    
    public BAD_PARAM ctxNonEmpty( Throwable t ) {
        return ctxNonEmpty( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM ctxNonEmpty(  ) {
        return ctxNonEmpty( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INVALID_STREAM_FORMAT_VERSION = OMGVMCID.value + 39 ;
    
    public BAD_PARAM invalidStreamFormatVersion( CompletionStatus cs, Throwable t, Object arg0) {
        BAD_PARAM exc = new BAD_PARAM( INVALID_STREAM_FORMAT_VERSION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.WARNING, "OMG.invalidStreamFormatVersion",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM invalidStreamFormatVersion( CompletionStatus cs, Object arg0) {
        return invalidStreamFormatVersion( cs, null, arg0 ) ;
    }
    
    public BAD_PARAM invalidStreamFormatVersion( Throwable t, Object arg0) {
        return invalidStreamFormatVersion( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public BAD_PARAM invalidStreamFormatVersion(  Object arg0) {
        return invalidStreamFormatVersion( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int NOT_A_VALUEOUTPUTSTREAM = OMGVMCID.value + 40 ;
    
    public BAD_PARAM notAValueoutputstream( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( NOT_A_VALUEOUTPUTSTREAM, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.notAValueoutputstream",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM notAValueoutputstream( CompletionStatus cs ) {
        return notAValueoutputstream( cs, null  ) ;
    }
    
    public BAD_PARAM notAValueoutputstream( Throwable t ) {
        return notAValueoutputstream( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM notAValueoutputstream(  ) {
        return notAValueoutputstream( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NOT_A_VALUEINPUTSTREAM = OMGVMCID.value + 41 ;
    
    public BAD_PARAM notAValueinputstream( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( NOT_A_VALUEINPUTSTREAM, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.notAValueinputstream",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM notAValueinputstream( CompletionStatus cs ) {
        return notAValueinputstream( cs, null  ) ;
    }
    
    public BAD_PARAM notAValueinputstream( Throwable t ) {
        return notAValueinputstream( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM notAValueinputstream(  ) {
        return notAValueinputstream( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int MARSHALL_INCOMPLETE_TYPECODE = OMGVMCID.value + 1 ;
    
    public BAD_TYPECODE marshallIncompleteTypecode( CompletionStatus cs, Throwable t ) {
        BAD_TYPECODE exc = new BAD_TYPECODE( MARSHALL_INCOMPLETE_TYPECODE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.marshallIncompleteTypecode",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode( CompletionStatus cs ) {
        return marshallIncompleteTypecode( cs, null  ) ;
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode( Throwable t ) {
        return marshallIncompleteTypecode( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode(  ) {
        return marshallIncompleteTypecode( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_MEMBER_TYPECODE = OMGVMCID.value + 2 ;
    
    public BAD_TYPECODE badMemberTypecode( CompletionStatus cs, Throwable t ) {
        BAD_TYPECODE exc = new BAD_TYPECODE( BAD_MEMBER_TYPECODE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badMemberTypecode",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_TYPECODE badMemberTypecode( CompletionStatus cs ) {
        return badMemberTypecode( cs, null  ) ;
    }
    
    public BAD_TYPECODE badMemberTypecode( Throwable t ) {
        return badMemberTypecode( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_TYPECODE badMemberTypecode(  ) {
        return badMemberTypecode( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ILLEGAL_PARAMETER = OMGVMCID.value + 3 ;
    
    public BAD_TYPECODE illegalParameter( CompletionStatus cs, Throwable t ) {
        BAD_TYPECODE exc = new BAD_TYPECODE( ILLEGAL_PARAMETER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.illegalParameter",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_TYPECODE illegalParameter( CompletionStatus cs ) {
        return illegalParameter( cs, null  ) ;
    }
    
    public BAD_TYPECODE illegalParameter( Throwable t ) {
        return illegalParameter( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_TYPECODE illegalParameter(  ) {
        return illegalParameter( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int CHAR_NOT_IN_CODESET = OMGVMCID.value + 1 ;
    
    public DATA_CONVERSION charNotInCodeset( CompletionStatus cs, Throwable t ) {
        DATA_CONVERSION exc = new DATA_CONVERSION( CHAR_NOT_IN_CODESET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.charNotInCodeset",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public DATA_CONVERSION charNotInCodeset( CompletionStatus cs ) {
        return charNotInCodeset( cs, null  ) ;
    }
    
    public DATA_CONVERSION charNotInCodeset( Throwable t ) {
        return charNotInCodeset( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public DATA_CONVERSION charNotInCodeset(  ) {
        return charNotInCodeset( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PRIORITY_MAP_FAILRE = OMGVMCID.value + 2 ;
    
    public DATA_CONVERSION priorityMapFailre( CompletionStatus cs, Throwable t ) {
        DATA_CONVERSION exc = new DATA_CONVERSION( PRIORITY_MAP_FAILRE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.priorityMapFailre",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public DATA_CONVERSION priorityMapFailre( CompletionStatus cs ) {
        return priorityMapFailre( cs, null  ) ;
    }
    
    public DATA_CONVERSION priorityMapFailre( Throwable t ) {
        return priorityMapFailre( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public DATA_CONVERSION priorityMapFailre(  ) {
        return priorityMapFailre( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int NO_USABLE_PROFILE = OMGVMCID.value + 1 ;
    
    public IMP_LIMIT noUsableProfile( CompletionStatus cs, Throwable t ) {
        IMP_LIMIT exc = new IMP_LIMIT( NO_USABLE_PROFILE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noUsableProfile",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public IMP_LIMIT noUsableProfile( CompletionStatus cs ) {
        return noUsableProfile( cs, null  ) ;
    }
    
    public IMP_LIMIT noUsableProfile( Throwable t ) {
        return noUsableProfile( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public IMP_LIMIT noUsableProfile(  ) {
        return noUsableProfile( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int PRIORITY_RANGE_RESTRICT = OMGVMCID.value + 1 ;
    
    public INITIALIZE priorityRangeRestrict( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( PRIORITY_RANGE_RESTRICT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.priorityRangeRestrict",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE priorityRangeRestrict( CompletionStatus cs ) {
        return priorityRangeRestrict( cs, null  ) ;
    }
    
    public INITIALIZE priorityRangeRestrict( Throwable t ) {
        return priorityRangeRestrict( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE priorityRangeRestrict(  ) {
        return priorityRangeRestrict( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int NO_SERVER_WCHAR_CODESET_CMP = OMGVMCID.value + 1 ;
    
    public INV_OBJREF noServerWcharCodesetCmp( CompletionStatus cs, Throwable t ) {
        INV_OBJREF exc = new INV_OBJREF( NO_SERVER_WCHAR_CODESET_CMP, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noServerWcharCodesetCmp",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INV_OBJREF noServerWcharCodesetCmp( CompletionStatus cs ) {
        return noServerWcharCodesetCmp( cs, null  ) ;
    }
    
    public INV_OBJREF noServerWcharCodesetCmp( Throwable t ) {
        return noServerWcharCodesetCmp( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INV_OBJREF noServerWcharCodesetCmp(  ) {
        return noServerWcharCodesetCmp( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CODESET_COMPONENT_REQUIRED = OMGVMCID.value + 2 ;
    
    public INV_OBJREF codesetComponentRequired( CompletionStatus cs, Throwable t ) {
        INV_OBJREF exc = new INV_OBJREF( CODESET_COMPONENT_REQUIRED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.codesetComponentRequired",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INV_OBJREF codesetComponentRequired( CompletionStatus cs ) {
        return codesetComponentRequired( cs, null  ) ;
    }
    
    public INV_OBJREF codesetComponentRequired( Throwable t ) {
        return codesetComponentRequired( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INV_OBJREF codesetComponentRequired(  ) {
        return codesetComponentRequired( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int IOR_POLICY_RECONCILE_ERROR = OMGVMCID.value + 1 ;
    
    public INV_POLICY iorPolicyReconcileError( CompletionStatus cs, Throwable t ) {
        INV_POLICY exc = new INV_POLICY( IOR_POLICY_RECONCILE_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.iorPolicyReconcileError",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INV_POLICY iorPolicyReconcileError( CompletionStatus cs ) {
        return iorPolicyReconcileError( cs, null  ) ;
    }
    
    public INV_POLICY iorPolicyReconcileError( Throwable t ) {
        return iorPolicyReconcileError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INV_POLICY iorPolicyReconcileError(  ) {
        return iorPolicyReconcileError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POLICY_UNKNOWN = OMGVMCID.value + 2 ;
    
    public INV_POLICY policyUnknown( CompletionStatus cs, Throwable t ) {
        INV_POLICY exc = new INV_POLICY( POLICY_UNKNOWN, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.policyUnknown",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INV_POLICY policyUnknown( CompletionStatus cs ) {
        return policyUnknown( cs, null  ) ;
    }
    
    public INV_POLICY policyUnknown( Throwable t ) {
        return policyUnknown( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INV_POLICY policyUnknown(  ) {
        return policyUnknown( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_POLICY_FACTORY = OMGVMCID.value + 3 ;
    
    public INV_POLICY noPolicyFactory( CompletionStatus cs, Throwable t ) {
        INV_POLICY exc = new INV_POLICY( NO_POLICY_FACTORY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noPolicyFactory",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INV_POLICY noPolicyFactory( CompletionStatus cs ) {
        return noPolicyFactory( cs, null  ) ;
    }
    
    public INV_POLICY noPolicyFactory( Throwable t ) {
        return noPolicyFactory( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INV_POLICY noPolicyFactory(  ) {
        return noPolicyFactory( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int XA_RMERR = OMGVMCID.value + 1 ;
    
    public INTERNAL xaRmerr( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( XA_RMERR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaRmerr",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL xaRmerr( CompletionStatus cs ) {
        return xaRmerr( cs, null  ) ;
    }
    
    public INTERNAL xaRmerr( Throwable t ) {
        return xaRmerr( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL xaRmerr(  ) {
        return xaRmerr( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_RMFAIL = OMGVMCID.value + 2 ;
    
    public INTERNAL xaRmfail( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( XA_RMFAIL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaRmfail",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL xaRmfail( CompletionStatus cs ) {
        return xaRmfail( cs, null  ) ;
    }
    
    public INTERNAL xaRmfail( Throwable t ) {
        return xaRmfail( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL xaRmfail(  ) {
        return xaRmfail( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int NO_IR = OMGVMCID.value + 1 ;
    
    public INTF_REPOS noIr( CompletionStatus cs, Throwable t ) {
        INTF_REPOS exc = new INTF_REPOS( NO_IR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noIr",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTF_REPOS noIr( CompletionStatus cs ) {
        return noIr( cs, null  ) ;
    }
    
    public INTF_REPOS noIr( Throwable t ) {
        return noIr( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTF_REPOS noIr(  ) {
        return noIr( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_INTERFACE_IN_IR = OMGVMCID.value + 2 ;
    
    public INTF_REPOS noInterfaceInIr( CompletionStatus cs, Throwable t ) {
        INTF_REPOS exc = new INTF_REPOS( NO_INTERFACE_IN_IR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noInterfaceInIr",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTF_REPOS noInterfaceInIr( CompletionStatus cs ) {
        return noInterfaceInIr( cs, null  ) ;
    }
    
    public INTF_REPOS noInterfaceInIr( Throwable t ) {
        return noInterfaceInIr( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTF_REPOS noInterfaceInIr(  ) {
        return noInterfaceInIr( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int UNABLE_LOCATE_VALUE_FACTORY = OMGVMCID.value + 1 ;
    
    public MARSHAL unableLocateValueFactory( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( UNABLE_LOCATE_VALUE_FACTORY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.unableLocateValueFactory",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL unableLocateValueFactory( CompletionStatus cs ) {
        return unableLocateValueFactory( cs, null  ) ;
    }
    
    public MARSHAL unableLocateValueFactory( Throwable t ) {
        return unableLocateValueFactory( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL unableLocateValueFactory(  ) {
        return unableLocateValueFactory( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SET_RESULT_BEFORE_CTX = OMGVMCID.value + 2 ;
    
    public MARSHAL setResultBeforeCtx( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( SET_RESULT_BEFORE_CTX, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.setResultBeforeCtx",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL setResultBeforeCtx( CompletionStatus cs ) {
        return setResultBeforeCtx( cs, null  ) ;
    }
    
    public MARSHAL setResultBeforeCtx( Throwable t ) {
        return setResultBeforeCtx( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL setResultBeforeCtx(  ) {
        return setResultBeforeCtx( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_NVLIST = OMGVMCID.value + 3 ;
    
    public MARSHAL badNvlist( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( BAD_NVLIST, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badNvlist",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL badNvlist( CompletionStatus cs ) {
        return badNvlist( cs, null  ) ;
    }
    
    public MARSHAL badNvlist( Throwable t ) {
        return badNvlist( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL badNvlist(  ) {
        return badNvlist( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NOT_AN_OBJECT_IMPL = OMGVMCID.value + 4 ;
    
    public MARSHAL notAnObjectImpl( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( NOT_AN_OBJECT_IMPL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.notAnObjectImpl",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL notAnObjectImpl( CompletionStatus cs ) {
        return notAnObjectImpl( cs, null  ) ;
    }
    
    public MARSHAL notAnObjectImpl( Throwable t ) {
        return notAnObjectImpl( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL notAnObjectImpl(  ) {
        return notAnObjectImpl( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int WCHAR_BAD_GIOP_VERSION_SENT = OMGVMCID.value + 5 ;
    
    public MARSHAL wcharBadGiopVersionSent( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( WCHAR_BAD_GIOP_VERSION_SENT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.wcharBadGiopVersionSent",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL wcharBadGiopVersionSent( CompletionStatus cs ) {
        return wcharBadGiopVersionSent( cs, null  ) ;
    }
    
    public MARSHAL wcharBadGiopVersionSent( Throwable t ) {
        return wcharBadGiopVersionSent( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL wcharBadGiopVersionSent(  ) {
        return wcharBadGiopVersionSent( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int WCHAR_BAD_GIOP_VERSION_RETURNED = OMGVMCID.value + 6 ;
    
    public MARSHAL wcharBadGiopVersionReturned( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( WCHAR_BAD_GIOP_VERSION_RETURNED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.wcharBadGiopVersionReturned",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL wcharBadGiopVersionReturned( CompletionStatus cs ) {
        return wcharBadGiopVersionReturned( cs, null  ) ;
    }
    
    public MARSHAL wcharBadGiopVersionReturned( Throwable t ) {
        return wcharBadGiopVersionReturned( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL wcharBadGiopVersionReturned(  ) {
        return wcharBadGiopVersionReturned( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNSUPPORTED_FORMAT_VERSION = OMGVMCID.value + 7 ;
    
    public MARSHAL unsupportedFormatVersion( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( UNSUPPORTED_FORMAT_VERSION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.unsupportedFormatVersion",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL unsupportedFormatVersion( CompletionStatus cs ) {
        return unsupportedFormatVersion( cs, null  ) ;
    }
    
    public MARSHAL unsupportedFormatVersion( Throwable t ) {
        return unsupportedFormatVersion( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL unsupportedFormatVersion(  ) {
        return unsupportedFormatVersion( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1 = OMGVMCID.value + 8 ;
    
    public MARSHAL rmiiiopOptionalDataIncompatible1( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible1",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1( CompletionStatus cs ) {
        return rmiiiopOptionalDataIncompatible1( cs, null  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1( Throwable t ) {
        return rmiiiopOptionalDataIncompatible1( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1(  ) {
        return rmiiiopOptionalDataIncompatible1( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE2 = OMGVMCID.value + 8 ;
    
    public MARSHAL rmiiiopOptionalDataIncompatible2( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE2, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible2",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2( CompletionStatus cs ) {
        return rmiiiopOptionalDataIncompatible2( cs, null  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2( Throwable t ) {
        return rmiiiopOptionalDataIncompatible2( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2(  ) {
        return rmiiiopOptionalDataIncompatible2( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE3 = OMGVMCID.value + 8 ;
    
    public MARSHAL rmiiiopOptionalDataIncompatible3( CompletionStatus cs, Throwable t ) {
        MARSHAL exc = new MARSHAL( RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE3, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.rmiiiopOptionalDataIncompatible3",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3( CompletionStatus cs ) {
        return rmiiiopOptionalDataIncompatible3( cs, null  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3( Throwable t ) {
        return rmiiiopOptionalDataIncompatible3( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3(  ) {
        return rmiiiopOptionalDataIncompatible3( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int MISSING_LOCAL_VALUE_IMPL = OMGVMCID.value + 1 ;
    
    public NO_IMPLEMENT missingLocalValueImpl( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( MISSING_LOCAL_VALUE_IMPL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.missingLocalValueImpl",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT missingLocalValueImpl( CompletionStatus cs ) {
        return missingLocalValueImpl( cs, null  ) ;
    }
    
    public NO_IMPLEMENT missingLocalValueImpl( Throwable t ) {
        return missingLocalValueImpl( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT missingLocalValueImpl(  ) {
        return missingLocalValueImpl( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INCOMPATIBLE_VALUE_IMPL = OMGVMCID.value + 2 ;
    
    public NO_IMPLEMENT incompatibleValueImpl( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( INCOMPATIBLE_VALUE_IMPL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.incompatibleValueImpl",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT incompatibleValueImpl( CompletionStatus cs ) {
        return incompatibleValueImpl( cs, null  ) ;
    }
    
    public NO_IMPLEMENT incompatibleValueImpl( Throwable t ) {
        return incompatibleValueImpl( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT incompatibleValueImpl(  ) {
        return incompatibleValueImpl( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_USABLE_PROFILE_2 = OMGVMCID.value + 3 ;
    
    public NO_IMPLEMENT noUsableProfile2( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( NO_USABLE_PROFILE_2, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noUsableProfile2",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT noUsableProfile2( CompletionStatus cs ) {
        return noUsableProfile2( cs, null  ) ;
    }
    
    public NO_IMPLEMENT noUsableProfile2( Throwable t ) {
        return noUsableProfile2( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT noUsableProfile2(  ) {
        return noUsableProfile2( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int DII_LOCAL_OBJECT = OMGVMCID.value + 4 ;
    
    public NO_IMPLEMENT diiLocalObject( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( DII_LOCAL_OBJECT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.diiLocalObject",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT diiLocalObject( CompletionStatus cs ) {
        return diiLocalObject( cs, null  ) ;
    }
    
    public NO_IMPLEMENT diiLocalObject( Throwable t ) {
        return diiLocalObject( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT diiLocalObject(  ) {
        return diiLocalObject( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BIO_RESET = OMGVMCID.value + 5 ;
    
    public NO_IMPLEMENT bioReset( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( BIO_RESET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.bioReset",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT bioReset( CompletionStatus cs ) {
        return bioReset( cs, null  ) ;
    }
    
    public NO_IMPLEMENT bioReset( Throwable t ) {
        return bioReset( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT bioReset(  ) {
        return bioReset( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BIO_META_NOT_AVAILABLE = OMGVMCID.value + 6 ;
    
    public NO_IMPLEMENT bioMetaNotAvailable( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( BIO_META_NOT_AVAILABLE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.bioMetaNotAvailable",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable( CompletionStatus cs ) {
        return bioMetaNotAvailable( cs, null  ) ;
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable( Throwable t ) {
        return bioMetaNotAvailable( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable(  ) {
        return bioMetaNotAvailable( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BIO_GENOMIC_NO_ITERATOR = OMGVMCID.value + 7 ;
    
    public NO_IMPLEMENT bioGenomicNoIterator( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( BIO_GENOMIC_NO_ITERATOR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.bioGenomicNoIterator",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator( CompletionStatus cs ) {
        return bioGenomicNoIterator( cs, null  ) ;
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator( Throwable t ) {
        return bioGenomicNoIterator( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator(  ) {
        return bioGenomicNoIterator( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int PI_OPERATION_NOT_SUPPORTED1 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported1( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED1, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported1",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported1( CompletionStatus cs ) {
        return piOperationNotSupported1( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported1( Throwable t ) {
        return piOperationNotSupported1( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported1(  ) {
        return piOperationNotSupported1( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED2 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported2( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED2, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported2",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported2( CompletionStatus cs ) {
        return piOperationNotSupported2( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported2( Throwable t ) {
        return piOperationNotSupported2( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported2(  ) {
        return piOperationNotSupported2( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED3 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported3( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED3, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported3",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported3( CompletionStatus cs ) {
        return piOperationNotSupported3( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported3( Throwable t ) {
        return piOperationNotSupported3( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported3(  ) {
        return piOperationNotSupported3( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED4 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported4( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED4, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported4",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported4( CompletionStatus cs ) {
        return piOperationNotSupported4( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported4( Throwable t ) {
        return piOperationNotSupported4( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported4(  ) {
        return piOperationNotSupported4( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED5 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported5( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED5, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported5",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported5( CompletionStatus cs ) {
        return piOperationNotSupported5( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported5( Throwable t ) {
        return piOperationNotSupported5( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported5(  ) {
        return piOperationNotSupported5( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED6 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported6( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED6, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported6",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported6( CompletionStatus cs ) {
        return piOperationNotSupported6( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported6( Throwable t ) {
        return piOperationNotSupported6( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported6(  ) {
        return piOperationNotSupported6( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED7 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported7( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED7, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported7",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported7( CompletionStatus cs ) {
        return piOperationNotSupported7( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported7( Throwable t ) {
        return piOperationNotSupported7( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported7(  ) {
        return piOperationNotSupported7( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_OPERATION_NOT_SUPPORTED8 = OMGVMCID.value + 1 ;
    
    public NO_RESOURCES piOperationNotSupported8( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( PI_OPERATION_NOT_SUPPORTED8, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.piOperationNotSupported8",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES piOperationNotSupported8( CompletionStatus cs ) {
        return piOperationNotSupported8( cs, null  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported8( Throwable t ) {
        return piOperationNotSupported8( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES piOperationNotSupported8(  ) {
        return piOperationNotSupported8( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_CONNECTION_PRIORITY = OMGVMCID.value + 2 ;
    
    public NO_RESOURCES noConnectionPriority( CompletionStatus cs, Throwable t ) {
        NO_RESOURCES exc = new NO_RESOURCES( NO_CONNECTION_PRIORITY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noConnectionPriority",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_RESOURCES noConnectionPriority( CompletionStatus cs ) {
        return noConnectionPriority( cs, null  ) ;
    }
    
    public NO_RESOURCES noConnectionPriority( Throwable t ) {
        return noConnectionPriority( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_RESOURCES noConnectionPriority(  ) {
        return noConnectionPriority( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int XA_RB = OMGVMCID.value + 1 ;
    
    public TRANSACTION_ROLLEDBACK xaRb( CompletionStatus cs, Throwable t ) {
        TRANSACTION_ROLLEDBACK exc = new TRANSACTION_ROLLEDBACK( XA_RB, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaRb",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSACTION_ROLLEDBACK xaRb( CompletionStatus cs ) {
        return xaRb( cs, null  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaRb( Throwable t ) {
        return xaRb( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaRb(  ) {
        return xaRb( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_NOTA = OMGVMCID.value + 2 ;
    
    public TRANSACTION_ROLLEDBACK xaNota( CompletionStatus cs, Throwable t ) {
        TRANSACTION_ROLLEDBACK exc = new TRANSACTION_ROLLEDBACK( XA_NOTA, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaNota",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSACTION_ROLLEDBACK xaNota( CompletionStatus cs ) {
        return xaNota( cs, null  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaNota( Throwable t ) {
        return xaNota( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaNota(  ) {
        return xaNota( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int XA_END_TRUE_ROLLBACK_DEFERRED = OMGVMCID.value + 3 ;
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred( CompletionStatus cs, Throwable t ) {
        TRANSACTION_ROLLEDBACK exc = new TRANSACTION_ROLLEDBACK( XA_END_TRUE_ROLLBACK_DEFERRED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.xaEndTrueRollbackDeferred",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred( CompletionStatus cs ) {
        return xaEndTrueRollbackDeferred( cs, null  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred( Throwable t ) {
        return xaEndTrueRollbackDeferred( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(  ) {
        return xaEndTrueRollbackDeferred( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int POA_REQUEST_DISCARD = OMGVMCID.value + 1 ;
    
    public TRANSIENT poaRequestDiscard( CompletionStatus cs, Throwable t ) {
        TRANSIENT exc = new TRANSIENT( POA_REQUEST_DISCARD, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.poaRequestDiscard",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSIENT poaRequestDiscard( CompletionStatus cs ) {
        return poaRequestDiscard( cs, null  ) ;
    }
    
    public TRANSIENT poaRequestDiscard( Throwable t ) {
        return poaRequestDiscard( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSIENT poaRequestDiscard(  ) {
        return poaRequestDiscard( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_USABLE_PROFILE_3 = OMGVMCID.value + 2 ;
    
    public TRANSIENT noUsableProfile3( CompletionStatus cs, Throwable t ) {
        TRANSIENT exc = new TRANSIENT( NO_USABLE_PROFILE_3, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noUsableProfile3",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSIENT noUsableProfile3( CompletionStatus cs ) {
        return noUsableProfile3( cs, null  ) ;
    }
    
    public TRANSIENT noUsableProfile3( Throwable t ) {
        return noUsableProfile3( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSIENT noUsableProfile3(  ) {
        return noUsableProfile3( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int REQUEST_CANCELLED = OMGVMCID.value + 3 ;
    
    public TRANSIENT requestCancelled( CompletionStatus cs, Throwable t ) {
        TRANSIENT exc = new TRANSIENT( REQUEST_CANCELLED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.requestCancelled",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSIENT requestCancelled( CompletionStatus cs ) {
        return requestCancelled( cs, null  ) ;
    }
    
    public TRANSIENT requestCancelled( Throwable t ) {
        return requestCancelled( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSIENT requestCancelled(  ) {
        return requestCancelled( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_DESTROYED = OMGVMCID.value + 4 ;
    
    public TRANSIENT poaDestroyed( CompletionStatus cs, Throwable t ) {
        TRANSIENT exc = new TRANSIENT( POA_DESTROYED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.poaDestroyed",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSIENT poaDestroyed( CompletionStatus cs ) {
        return poaDestroyed( cs, null  ) ;
    }
    
    public TRANSIENT poaDestroyed( Throwable t ) {
        return poaDestroyed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSIENT poaDestroyed(  ) {
        return poaDestroyed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int UNREGISTERED_VALUE_AS_OBJREF = OMGVMCID.value + 1 ;
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( UNREGISTERED_VALUE_AS_OBJREF, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.unregisteredValueAsObjref",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref( CompletionStatus cs ) {
        return unregisteredValueAsObjref( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref( Throwable t ) {
        return unregisteredValueAsObjref( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref(  ) {
        return unregisteredValueAsObjref( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_OBJECT_ADAPTOR = OMGVMCID.value + 2 ;
    
    public OBJECT_NOT_EXIST noObjectAdaptor( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( NO_OBJECT_ADAPTOR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.noObjectAdaptor",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor( CompletionStatus cs ) {
        return noObjectAdaptor( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor( Throwable t ) {
        return noObjectAdaptor( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor(  ) {
        return noObjectAdaptor( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BIO_NOT_AVAILABLE = OMGVMCID.value + 3 ;
    
    public OBJECT_NOT_EXIST bioNotAvailable( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( BIO_NOT_AVAILABLE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.bioNotAvailable",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable( CompletionStatus cs ) {
        return bioNotAvailable( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable( Throwable t ) {
        return bioNotAvailable( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable(  ) {
        return bioNotAvailable( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int OBJECT_ADAPTER_INACTIVE = OMGVMCID.value + 4 ;
    
    public OBJECT_NOT_EXIST objectAdapterInactive( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( OBJECT_ADAPTER_INACTIVE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.objectAdapterInactive",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive( CompletionStatus cs ) {
        return objectAdapterInactive( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive( Throwable t ) {
        return objectAdapterInactive( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive(  ) {
        return objectAdapterInactive( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int ADAPTER_ACTIVATOR_EXCEPTION = OMGVMCID.value + 1 ;
    
    public OBJ_ADAPTER adapterActivatorException( CompletionStatus cs, Throwable t, Object arg0, Object arg1) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( ADAPTER_ACTIVATOR_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[2] ;
            parameters[0] = arg0 ;
            parameters[1] = arg1 ;
            doLog( Level.WARNING, "OMG.adapterActivatorException",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER adapterActivatorException( CompletionStatus cs, Object arg0, Object arg1) {
        return adapterActivatorException( cs, null, arg0, arg1 ) ;
    }
    
    public OBJ_ADAPTER adapterActivatorException( Throwable t, Object arg0, Object arg1) {
        return adapterActivatorException( CompletionStatus.COMPLETED_NO, t, arg0, arg1 ) ;
    }
    
    public OBJ_ADAPTER adapterActivatorException(  Object arg0, Object arg1) {
        return adapterActivatorException( CompletionStatus.COMPLETED_NO, null, arg0, arg1 ) ;
    }
    
    public static final int BAD_SERVANT_TYPE = OMGVMCID.value + 2 ;
    
    public OBJ_ADAPTER badServantType( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( BAD_SERVANT_TYPE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badServantType",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER badServantType( CompletionStatus cs ) {
        return badServantType( cs, null  ) ;
    }
    
    public OBJ_ADAPTER badServantType( Throwable t ) {
        return badServantType( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER badServantType(  ) {
        return badServantType( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_DEFAULT_SERVANT = OMGVMCID.value + 3 ;
    
    public OBJ_ADAPTER noDefaultServant( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( NO_DEFAULT_SERVANT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noDefaultServant",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER noDefaultServant( CompletionStatus cs ) {
        return noDefaultServant( cs, null  ) ;
    }
    
    public OBJ_ADAPTER noDefaultServant( Throwable t ) {
        return noDefaultServant( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER noDefaultServant(  ) {
        return noDefaultServant( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_SERVANT_MANAGER = OMGVMCID.value + 4 ;
    
    public OBJ_ADAPTER noServantManager( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( NO_SERVANT_MANAGER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.noServantManager",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER noServantManager( CompletionStatus cs ) {
        return noServantManager( cs, null  ) ;
    }
    
    public OBJ_ADAPTER noServantManager( Throwable t ) {
        return noServantManager( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER noServantManager(  ) {
        return noServantManager( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_POLICY_INCARNATE = OMGVMCID.value + 5 ;
    
    public OBJ_ADAPTER badPolicyIncarnate( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( BAD_POLICY_INCARNATE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.badPolicyIncarnate",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER badPolicyIncarnate( CompletionStatus cs ) {
        return badPolicyIncarnate( cs, null  ) ;
    }
    
    public OBJ_ADAPTER badPolicyIncarnate( Throwable t ) {
        return badPolicyIncarnate( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER badPolicyIncarnate(  ) {
        return badPolicyIncarnate( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_EXC_COMP_ESTABLISHED = OMGVMCID.value + 6 ;
    
    public OBJ_ADAPTER piExcCompEstablished( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( PI_EXC_COMP_ESTABLISHED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.piExcCompEstablished",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER piExcCompEstablished( CompletionStatus cs ) {
        return piExcCompEstablished( cs, null  ) ;
    }
    
    public OBJ_ADAPTER piExcCompEstablished( Throwable t ) {
        return piExcCompEstablished( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER piExcCompEstablished(  ) {
        return piExcCompEstablished( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NULL_SERVANT_RETURNED = OMGVMCID.value + 7 ;
    
    public OBJ_ADAPTER nullServantReturned( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( NULL_SERVANT_RETURNED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.nullServantReturned",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER nullServantReturned( CompletionStatus cs ) {
        return nullServantReturned( cs, null  ) ;
    }
    
    public OBJ_ADAPTER nullServantReturned( Throwable t ) {
        return nullServantReturned( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER nullServantReturned(  ) {
        return nullServantReturned( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int UNKNOWN_USER_EXCEPTION = OMGVMCID.value + 1 ;
    
    public UNKNOWN unknownUserException( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( UNKNOWN_USER_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "OMG.unknownUserException",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN unknownUserException( CompletionStatus cs ) {
        return unknownUserException( cs, null  ) ;
    }
    
    public UNKNOWN unknownUserException( Throwable t ) {
        return unknownUserException( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN unknownUserException(  ) {
        return unknownUserException( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNSUPPORTED_SYSTEM_EXCEPTION = OMGVMCID.value + 2 ;
    
    public UNKNOWN unsupportedSystemException( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( UNSUPPORTED_SYSTEM_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.unsupportedSystemException",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN unsupportedSystemException( CompletionStatus cs ) {
        return unsupportedSystemException( cs, null  ) ;
    }
    
    public UNKNOWN unsupportedSystemException( Throwable t ) {
        return unsupportedSystemException( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN unsupportedSystemException(  ) {
        return unsupportedSystemException( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PI_UNKNOWN_USER_EXCEPTION = OMGVMCID.value + 3 ;
    
    public UNKNOWN piUnknownUserException( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( PI_UNKNOWN_USER_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "OMG.piUnknownUserException",
                parameters, OMGSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN piUnknownUserException( CompletionStatus cs ) {
        return piUnknownUserException( cs, null  ) ;
    }
    
    public UNKNOWN piUnknownUserException( Throwable t ) {
        return piUnknownUserException( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN piUnknownUserException(  ) {
        return piUnknownUserException( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    
}
