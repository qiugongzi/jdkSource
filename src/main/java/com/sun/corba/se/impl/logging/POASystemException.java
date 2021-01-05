




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

import org.omg.CORBA.BAD_INV_ORDER ;
import org.omg.CORBA.BAD_OPERATION ;
import org.omg.CORBA.BAD_PARAM ;
import org.omg.CORBA.INTERNAL ;
import org.omg.CORBA.NO_IMPLEMENT ;
import org.omg.CORBA.OBJ_ADAPTER ;
import org.omg.CORBA.INITIALIZE ;
import org.omg.CORBA.TRANSIENT ;
import org.omg.CORBA.UNKNOWN ;
import org.omg.CORBA.OBJECT_NOT_EXIST ;

public class POASystemException extends LogWrapperBase {
    
    public POASystemException( Logger logger )
    {
        super( logger ) ;
    }
    
    private static LogWrapperFactory factory = new LogWrapperFactory() {
        public LogWrapperBase create( Logger logger )
        {
            return new POASystemException( logger ) ;
        }
    } ;
    
    public static POASystemException get( ORB orb, String logDomain )
    {
        POASystemException wrapper = 
            (POASystemException) orb.getLogWrapper( logDomain, 
                "POA", factory ) ;
        return wrapper ;
    } 
    
    public static POASystemException get( String logDomain )
    {
        POASystemException wrapper = 
            (POASystemException) ORB.staticGetLogWrapper( logDomain, 
                "POA", factory ) ;
        return wrapper ;
    } 
    



    
    public static final int SERVANT_MANAGER_ALREADY_SET = SUNVMCID.value + 1001 ;
    
    public BAD_INV_ORDER servantManagerAlreadySet( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( SERVANT_MANAGER_ALREADY_SET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantManagerAlreadySet",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet( CompletionStatus cs ) {
        return servantManagerAlreadySet( cs, null  ) ;
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet( Throwable t ) {
        return servantManagerAlreadySet( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet(  ) {
        return servantManagerAlreadySet( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int DESTROY_DEADLOCK = SUNVMCID.value + 1002 ;
    
    public BAD_INV_ORDER destroyDeadlock( CompletionStatus cs, Throwable t ) {
        BAD_INV_ORDER exc = new BAD_INV_ORDER( DESTROY_DEADLOCK, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.destroyDeadlock",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_INV_ORDER destroyDeadlock( CompletionStatus cs ) {
        return destroyDeadlock( cs, null  ) ;
    }
    
    public BAD_INV_ORDER destroyDeadlock( Throwable t ) {
        return destroyDeadlock( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_INV_ORDER destroyDeadlock(  ) {
        return destroyDeadlock( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int SERVANT_ORB = SUNVMCID.value + 1001 ;
    
    public BAD_OPERATION servantOrb( CompletionStatus cs, Throwable t ) {
        BAD_OPERATION exc = new BAD_OPERATION( SERVANT_ORB, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantOrb",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_OPERATION servantOrb( CompletionStatus cs ) {
        return servantOrb( cs, null  ) ;
    }
    
    public BAD_OPERATION servantOrb( Throwable t ) {
        return servantOrb( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_OPERATION servantOrb(  ) {
        return servantOrb( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_SERVANT = SUNVMCID.value + 1002 ;
    
    public BAD_OPERATION badServant( CompletionStatus cs, Throwable t ) {
        BAD_OPERATION exc = new BAD_OPERATION( BAD_SERVANT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.badServant",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_OPERATION badServant( CompletionStatus cs ) {
        return badServant( cs, null  ) ;
    }
    
    public BAD_OPERATION badServant( Throwable t ) {
        return badServant( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_OPERATION badServant(  ) {
        return badServant( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ILLEGAL_FORWARD_REQUEST = SUNVMCID.value + 1003 ;
    
    public BAD_OPERATION illegalForwardRequest( CompletionStatus cs, Throwable t ) {
        BAD_OPERATION exc = new BAD_OPERATION( ILLEGAL_FORWARD_REQUEST, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.illegalForwardRequest",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_OPERATION illegalForwardRequest( CompletionStatus cs ) {
        return illegalForwardRequest( cs, null  ) ;
    }
    
    public BAD_OPERATION illegalForwardRequest( Throwable t ) {
        return illegalForwardRequest( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_OPERATION illegalForwardRequest(  ) {
        return illegalForwardRequest( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int BAD_TRANSACTION_CONTEXT = SUNVMCID.value + 1001 ;
    
    public BAD_PARAM badTransactionContext( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( BAD_TRANSACTION_CONTEXT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.badTransactionContext",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM badTransactionContext( CompletionStatus cs ) {
        return badTransactionContext( cs, null  ) ;
    }
    
    public BAD_PARAM badTransactionContext( Throwable t ) {
        return badTransactionContext( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM badTransactionContext(  ) {
        return badTransactionContext( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_REPOSITORY_ID = SUNVMCID.value + 1002 ;
    
    public BAD_PARAM badRepositoryId( CompletionStatus cs, Throwable t ) {
        BAD_PARAM exc = new BAD_PARAM( BAD_REPOSITORY_ID, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.badRepositoryId",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public BAD_PARAM badRepositoryId( CompletionStatus cs ) {
        return badRepositoryId( cs, null  ) ;
    }
    
    public BAD_PARAM badRepositoryId( Throwable t ) {
        return badRepositoryId( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public BAD_PARAM badRepositoryId(  ) {
        return badRepositoryId( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int INVOKESETUP = SUNVMCID.value + 1001 ;
    
    public INTERNAL invokesetup( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( INVOKESETUP, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.invokesetup",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL invokesetup( CompletionStatus cs ) {
        return invokesetup( cs, null  ) ;
    }
    
    public INTERNAL invokesetup( Throwable t ) {
        return invokesetup( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL invokesetup(  ) {
        return invokesetup( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_LOCALREPLYSTATUS = SUNVMCID.value + 1002 ;
    
    public INTERNAL badLocalreplystatus( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( BAD_LOCALREPLYSTATUS, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.badLocalreplystatus",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL badLocalreplystatus( CompletionStatus cs ) {
        return badLocalreplystatus( cs, null  ) ;
    }
    
    public INTERNAL badLocalreplystatus( Throwable t ) {
        return badLocalreplystatus( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL badLocalreplystatus(  ) {
        return badLocalreplystatus( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PERSISTENT_SERVERPORT_ERROR = SUNVMCID.value + 1003 ;
    
    public INTERNAL persistentServerportError( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( PERSISTENT_SERVERPORT_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.persistentServerportError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL persistentServerportError( CompletionStatus cs ) {
        return persistentServerportError( cs, null  ) ;
    }
    
    public INTERNAL persistentServerportError( Throwable t ) {
        return persistentServerportError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL persistentServerportError(  ) {
        return persistentServerportError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_DISPATCH = SUNVMCID.value + 1004 ;
    
    public INTERNAL servantDispatch( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_DISPATCH, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantDispatch",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL servantDispatch( CompletionStatus cs ) {
        return servantDispatch( cs, null  ) ;
    }
    
    public INTERNAL servantDispatch( Throwable t ) {
        return servantDispatch( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL servantDispatch(  ) {
        return servantDispatch( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int WRONG_CLIENTSC = SUNVMCID.value + 1005 ;
    
    public INTERNAL wrongClientsc( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( WRONG_CLIENTSC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.wrongClientsc",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL wrongClientsc( CompletionStatus cs ) {
        return wrongClientsc( cs, null  ) ;
    }
    
    public INTERNAL wrongClientsc( Throwable t ) {
        return wrongClientsc( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL wrongClientsc(  ) {
        return wrongClientsc( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CANT_CLONE_TEMPLATE = SUNVMCID.value + 1006 ;
    
    public INTERNAL cantCloneTemplate( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( CANT_CLONE_TEMPLATE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.cantCloneTemplate",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL cantCloneTemplate( CompletionStatus cs ) {
        return cantCloneTemplate( cs, null  ) ;
    }
    
    public INTERNAL cantCloneTemplate( Throwable t ) {
        return cantCloneTemplate( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL cantCloneTemplate(  ) {
        return cantCloneTemplate( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POACURRENT_UNBALANCED_STACK = SUNVMCID.value + 1007 ;
    
    public INTERNAL poacurrentUnbalancedStack( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( POACURRENT_UNBALANCED_STACK, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poacurrentUnbalancedStack",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL poacurrentUnbalancedStack( CompletionStatus cs ) {
        return poacurrentUnbalancedStack( cs, null  ) ;
    }
    
    public INTERNAL poacurrentUnbalancedStack( Throwable t ) {
        return poacurrentUnbalancedStack( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL poacurrentUnbalancedStack(  ) {
        return poacurrentUnbalancedStack( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POACURRENT_NULL_FIELD = SUNVMCID.value + 1008 ;
    
    public INTERNAL poacurrentNullField( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( POACURRENT_NULL_FIELD, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poacurrentNullField",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL poacurrentNullField( CompletionStatus cs ) {
        return poacurrentNullField( cs, null  ) ;
    }
    
    public INTERNAL poacurrentNullField( Throwable t ) {
        return poacurrentNullField( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL poacurrentNullField(  ) {
        return poacurrentNullField( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_INTERNAL_GET_SERVANT_ERROR = SUNVMCID.value + 1009 ;
    
    public INTERNAL poaInternalGetServantError( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( POA_INTERNAL_GET_SERVANT_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaInternalGetServantError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL poaInternalGetServantError( CompletionStatus cs ) {
        return poaInternalGetServantError( cs, null  ) ;
    }
    
    public INTERNAL poaInternalGetServantError( Throwable t ) {
        return poaInternalGetServantError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL poaInternalGetServantError(  ) {
        return poaInternalGetServantError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int MAKE_FACTORY_NOT_POA = SUNVMCID.value + 1010 ;
    
    public INTERNAL makeFactoryNotPoa( CompletionStatus cs, Throwable t, Object arg0) {
        INTERNAL exc = new INTERNAL( MAKE_FACTORY_NOT_POA, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.WARNING, "POA.makeFactoryNotPoa",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL makeFactoryNotPoa( CompletionStatus cs, Object arg0) {
        return makeFactoryNotPoa( cs, null, arg0 ) ;
    }
    
    public INTERNAL makeFactoryNotPoa( Throwable t, Object arg0) {
        return makeFactoryNotPoa( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public INTERNAL makeFactoryNotPoa(  Object arg0) {
        return makeFactoryNotPoa( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    
    public static final int DUPLICATE_ORB_VERSION_SC = SUNVMCID.value + 1011 ;
    
    public INTERNAL duplicateOrbVersionSc( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( DUPLICATE_ORB_VERSION_SC, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.duplicateOrbVersionSc",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL duplicateOrbVersionSc( CompletionStatus cs ) {
        return duplicateOrbVersionSc( cs, null  ) ;
    }
    
    public INTERNAL duplicateOrbVersionSc( Throwable t ) {
        return duplicateOrbVersionSc( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL duplicateOrbVersionSc(  ) {
        return duplicateOrbVersionSc( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PREINVOKE_CLONE_ERROR = SUNVMCID.value + 1012 ;
    
    public INTERNAL preinvokeCloneError( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( PREINVOKE_CLONE_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.preinvokeCloneError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL preinvokeCloneError( CompletionStatus cs ) {
        return preinvokeCloneError( cs, null  ) ;
    }
    
    public INTERNAL preinvokeCloneError( Throwable t ) {
        return preinvokeCloneError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL preinvokeCloneError(  ) {
        return preinvokeCloneError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PREINVOKE_POA_DESTROYED = SUNVMCID.value + 1013 ;
    
    public INTERNAL preinvokePoaDestroyed( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( PREINVOKE_POA_DESTROYED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.preinvokePoaDestroyed",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL preinvokePoaDestroyed( CompletionStatus cs ) {
        return preinvokePoaDestroyed( cs, null  ) ;
    }
    
    public INTERNAL preinvokePoaDestroyed( Throwable t ) {
        return preinvokePoaDestroyed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL preinvokePoaDestroyed(  ) {
        return preinvokePoaDestroyed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PMF_CREATE_RETAIN = SUNVMCID.value + 1014 ;
    
    public INTERNAL pmfCreateRetain( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( PMF_CREATE_RETAIN, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.pmfCreateRetain",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL pmfCreateRetain( CompletionStatus cs ) {
        return pmfCreateRetain( cs, null  ) ;
    }
    
    public INTERNAL pmfCreateRetain( Throwable t ) {
        return pmfCreateRetain( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL pmfCreateRetain(  ) {
        return pmfCreateRetain( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PMF_CREATE_NON_RETAIN = SUNVMCID.value + 1015 ;
    
    public INTERNAL pmfCreateNonRetain( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( PMF_CREATE_NON_RETAIN, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.pmfCreateNonRetain",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL pmfCreateNonRetain( CompletionStatus cs ) {
        return pmfCreateNonRetain( cs, null  ) ;
    }
    
    public INTERNAL pmfCreateNonRetain( Throwable t ) {
        return pmfCreateNonRetain( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL pmfCreateNonRetain(  ) {
        return pmfCreateNonRetain( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POLICY_MEDIATOR_BAD_POLICY_IN_FACTORY = SUNVMCID.value + 1016 ;
    
    public INTERNAL policyMediatorBadPolicyInFactory( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( POLICY_MEDIATOR_BAD_POLICY_IN_FACTORY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.policyMediatorBadPolicyInFactory",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory( CompletionStatus cs ) {
        return policyMediatorBadPolicyInFactory( cs, null  ) ;
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory( Throwable t ) {
        return policyMediatorBadPolicyInFactory( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory(  ) {
        return policyMediatorBadPolicyInFactory( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_TO_ID_OAA = SUNVMCID.value + 1017 ;
    
    public INTERNAL servantToIdOaa( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_TO_ID_OAA, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantToIdOaa",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL servantToIdOaa( CompletionStatus cs ) {
        return servantToIdOaa( cs, null  ) ;
    }
    
    public INTERNAL servantToIdOaa( Throwable t ) {
        return servantToIdOaa( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL servantToIdOaa(  ) {
        return servantToIdOaa( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_TO_ID_SAA = SUNVMCID.value + 1018 ;
    
    public INTERNAL servantToIdSaa( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_TO_ID_SAA, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantToIdSaa",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL servantToIdSaa( CompletionStatus cs ) {
        return servantToIdSaa( cs, null  ) ;
    }
    
    public INTERNAL servantToIdSaa( Throwable t ) {
        return servantToIdSaa( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL servantToIdSaa(  ) {
        return servantToIdSaa( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_TO_ID_WP = SUNVMCID.value + 1019 ;
    
    public INTERNAL servantToIdWp( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_TO_ID_WP, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantToIdWp",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL servantToIdWp( CompletionStatus cs ) {
        return servantToIdWp( cs, null  ) ;
    }
    
    public INTERNAL servantToIdWp( Throwable t ) {
        return servantToIdWp( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL servantToIdWp(  ) {
        return servantToIdWp( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CANT_RESOLVE_ROOT_POA = SUNVMCID.value + 1020 ;
    
    public INTERNAL cantResolveRootPoa( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( CANT_RESOLVE_ROOT_POA, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.cantResolveRootPoa",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL cantResolveRootPoa( CompletionStatus cs ) {
        return cantResolveRootPoa( cs, null  ) ;
    }
    
    public INTERNAL cantResolveRootPoa( Throwable t ) {
        return cantResolveRootPoa( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL cantResolveRootPoa(  ) {
        return cantResolveRootPoa( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_MUST_BE_LOCAL = SUNVMCID.value + 1021 ;
    
    public INTERNAL servantMustBeLocal( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_MUST_BE_LOCAL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantMustBeLocal",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL servantMustBeLocal( CompletionStatus cs ) {
        return servantMustBeLocal( cs, null  ) ;
    }
    
    public INTERNAL servantMustBeLocal( Throwable t ) {
        return servantMustBeLocal( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL servantMustBeLocal(  ) {
        return servantMustBeLocal( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_PROFILES_IN_IOR = SUNVMCID.value + 1022 ;
    
    public INTERNAL noProfilesInIor( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( NO_PROFILES_IN_IOR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.noProfilesInIor",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL noProfilesInIor( CompletionStatus cs ) {
        return noProfilesInIor( cs, null  ) ;
    }
    
    public INTERNAL noProfilesInIor( Throwable t ) {
        return noProfilesInIor( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL noProfilesInIor(  ) {
        return noProfilesInIor( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int AOM_ENTRY_DEC_ZERO = SUNVMCID.value + 1023 ;
    
    public INTERNAL aomEntryDecZero( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( AOM_ENTRY_DEC_ZERO, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.aomEntryDecZero",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL aomEntryDecZero( CompletionStatus cs ) {
        return aomEntryDecZero( cs, null  ) ;
    }
    
    public INTERNAL aomEntryDecZero( Throwable t ) {
        return aomEntryDecZero( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL aomEntryDecZero(  ) {
        return aomEntryDecZero( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ADD_POA_INACTIVE = SUNVMCID.value + 1024 ;
    
    public INTERNAL addPoaInactive( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( ADD_POA_INACTIVE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.addPoaInactive",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL addPoaInactive( CompletionStatus cs ) {
        return addPoaInactive( cs, null  ) ;
    }
    
    public INTERNAL addPoaInactive( Throwable t ) {
        return addPoaInactive( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL addPoaInactive(  ) {
        return addPoaInactive( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ILLEGAL_POA_STATE_TRANS = SUNVMCID.value + 1025 ;
    
    public INTERNAL illegalPoaStateTrans( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( ILLEGAL_POA_STATE_TRANS, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.illegalPoaStateTrans",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL illegalPoaStateTrans( CompletionStatus cs ) {
        return illegalPoaStateTrans( cs, null  ) ;
    }
    
    public INTERNAL illegalPoaStateTrans( Throwable t ) {
        return illegalPoaStateTrans( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL illegalPoaStateTrans(  ) {
        return illegalPoaStateTrans( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNEXPECTED_EXCEPTION = SUNVMCID.value + 1026 ;
    
    public INTERNAL unexpectedException( CompletionStatus cs, Throwable t, Object arg0) {
        INTERNAL exc = new INTERNAL( UNEXPECTED_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = new Object[1] ;
            parameters[0] = arg0 ;
            doLog( Level.WARNING, "POA.unexpectedException",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL unexpectedException( CompletionStatus cs, Object arg0) {
        return unexpectedException( cs, null, arg0 ) ;
    }
    
    public INTERNAL unexpectedException( Throwable t, Object arg0) {
        return unexpectedException( CompletionStatus.COMPLETED_NO, t, arg0 ) ;
    }
    
    public INTERNAL unexpectedException(  Object arg0) {
        return unexpectedException( CompletionStatus.COMPLETED_NO, null, arg0 ) ;
    }
    



    
    public static final int SINGLE_THREAD_NOT_SUPPORTED = SUNVMCID.value + 1001 ;
    
    public NO_IMPLEMENT singleThreadNotSupported( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( SINGLE_THREAD_NOT_SUPPORTED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.singleThreadNotSupported",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT singleThreadNotSupported( CompletionStatus cs ) {
        return singleThreadNotSupported( cs, null  ) ;
    }
    
    public NO_IMPLEMENT singleThreadNotSupported( Throwable t ) {
        return singleThreadNotSupported( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT singleThreadNotSupported(  ) {
        return singleThreadNotSupported( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int METHOD_NOT_IMPLEMENTED = SUNVMCID.value + 1002 ;
    
    public NO_IMPLEMENT methodNotImplemented( CompletionStatus cs, Throwable t ) {
        NO_IMPLEMENT exc = new NO_IMPLEMENT( METHOD_NOT_IMPLEMENTED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.methodNotImplemented",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public NO_IMPLEMENT methodNotImplemented( CompletionStatus cs ) {
        return methodNotImplemented( cs, null  ) ;
    }
    
    public NO_IMPLEMENT methodNotImplemented( Throwable t ) {
        return methodNotImplemented( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public NO_IMPLEMENT methodNotImplemented(  ) {
        return methodNotImplemented( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int POA_LOOKUP_ERROR = SUNVMCID.value + 1001 ;
    
    public OBJ_ADAPTER poaLookupError( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_LOOKUP_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaLookupError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaLookupError( CompletionStatus cs ) {
        return poaLookupError( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaLookupError( Throwable t ) {
        return poaLookupError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaLookupError(  ) {
        return poaLookupError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_INACTIVE = SUNVMCID.value + 1002 ;
    
    public OBJ_ADAPTER poaInactive( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_INACTIVE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "POA.poaInactive",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaInactive( CompletionStatus cs ) {
        return poaInactive( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaInactive( Throwable t ) {
        return poaInactive( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaInactive(  ) {
        return poaInactive( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_NO_SERVANT_MANAGER = SUNVMCID.value + 1003 ;
    
    public OBJ_ADAPTER poaNoServantManager( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_NO_SERVANT_MANAGER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaNoServantManager",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaNoServantManager( CompletionStatus cs ) {
        return poaNoServantManager( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaNoServantManager( Throwable t ) {
        return poaNoServantManager( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaNoServantManager(  ) {
        return poaNoServantManager( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_NO_DEFAULT_SERVANT = SUNVMCID.value + 1004 ;
    
    public OBJ_ADAPTER poaNoDefaultServant( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_NO_DEFAULT_SERVANT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaNoDefaultServant",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaNoDefaultServant( CompletionStatus cs ) {
        return poaNoDefaultServant( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaNoDefaultServant( Throwable t ) {
        return poaNoDefaultServant( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaNoDefaultServant(  ) {
        return poaNoDefaultServant( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_SERVANT_NOT_UNIQUE = SUNVMCID.value + 1005 ;
    
    public OBJ_ADAPTER poaServantNotUnique( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_SERVANT_NOT_UNIQUE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaServantNotUnique",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaServantNotUnique( CompletionStatus cs ) {
        return poaServantNotUnique( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaServantNotUnique( Throwable t ) {
        return poaServantNotUnique( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaServantNotUnique(  ) {
        return poaServantNotUnique( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_WRONG_POLICY = SUNVMCID.value + 1006 ;
    
    public OBJ_ADAPTER poaWrongPolicy( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_WRONG_POLICY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaWrongPolicy",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaWrongPolicy( CompletionStatus cs ) {
        return poaWrongPolicy( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaWrongPolicy( Throwable t ) {
        return poaWrongPolicy( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaWrongPolicy(  ) {
        return poaWrongPolicy( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int FINDPOA_ERROR = SUNVMCID.value + 1007 ;
    
    public OBJ_ADAPTER findpoaError( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( FINDPOA_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.findpoaError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER findpoaError( CompletionStatus cs ) {
        return findpoaError( cs, null  ) ;
    }
    
    public OBJ_ADAPTER findpoaError( Throwable t ) {
        return findpoaError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER findpoaError(  ) {
        return findpoaError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_SERVANT_ACTIVATOR_LOOKUP_FAILED = SUNVMCID.value + 1009 ;
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_SERVANT_ACTIVATOR_LOOKUP_FAILED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaServantActivatorLookupFailed",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed( CompletionStatus cs ) {
        return poaServantActivatorLookupFailed( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed( Throwable t ) {
        return poaServantActivatorLookupFailed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed(  ) {
        return poaServantActivatorLookupFailed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_BAD_SERVANT_MANAGER = SUNVMCID.value + 1010 ;
    
    public OBJ_ADAPTER poaBadServantManager( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_BAD_SERVANT_MANAGER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaBadServantManager",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaBadServantManager( CompletionStatus cs ) {
        return poaBadServantManager( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaBadServantManager( Throwable t ) {
        return poaBadServantManager( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaBadServantManager(  ) {
        return poaBadServantManager( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_SERVANT_LOCATOR_LOOKUP_FAILED = SUNVMCID.value + 1011 ;
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_SERVANT_LOCATOR_LOOKUP_FAILED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaServantLocatorLookupFailed",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed( CompletionStatus cs ) {
        return poaServantLocatorLookupFailed( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed( Throwable t ) {
        return poaServantLocatorLookupFailed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed(  ) {
        return poaServantLocatorLookupFailed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_UNKNOWN_POLICY = SUNVMCID.value + 1012 ;
    
    public OBJ_ADAPTER poaUnknownPolicy( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_UNKNOWN_POLICY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaUnknownPolicy",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaUnknownPolicy( CompletionStatus cs ) {
        return poaUnknownPolicy( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaUnknownPolicy( Throwable t ) {
        return poaUnknownPolicy( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaUnknownPolicy(  ) {
        return poaUnknownPolicy( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int POA_NOT_FOUND = SUNVMCID.value + 1013 ;
    
    public OBJ_ADAPTER poaNotFound( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( POA_NOT_FOUND, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.poaNotFound",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER poaNotFound( CompletionStatus cs ) {
        return poaNotFound( cs, null  ) ;
    }
    
    public OBJ_ADAPTER poaNotFound( Throwable t ) {
        return poaNotFound( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER poaNotFound(  ) {
        return poaNotFound( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_LOOKUP = SUNVMCID.value + 1014 ;
    
    public OBJ_ADAPTER servantLookup( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( SERVANT_LOOKUP, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantLookup",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER servantLookup( CompletionStatus cs ) {
        return servantLookup( cs, null  ) ;
    }
    
    public OBJ_ADAPTER servantLookup( Throwable t ) {
        return servantLookup( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER servantLookup(  ) {
        return servantLookup( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int LOCAL_SERVANT_LOOKUP = SUNVMCID.value + 1015 ;
    
    public OBJ_ADAPTER localServantLookup( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( LOCAL_SERVANT_LOOKUP, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.localServantLookup",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER localServantLookup( CompletionStatus cs ) {
        return localServantLookup( cs, null  ) ;
    }
    
    public OBJ_ADAPTER localServantLookup( Throwable t ) {
        return localServantLookup( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER localServantLookup(  ) {
        return localServantLookup( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVANT_MANAGER_BAD_TYPE = SUNVMCID.value + 1016 ;
    
    public OBJ_ADAPTER servantManagerBadType( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( SERVANT_MANAGER_BAD_TYPE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantManagerBadType",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER servantManagerBadType( CompletionStatus cs ) {
        return servantManagerBadType( cs, null  ) ;
    }
    
    public OBJ_ADAPTER servantManagerBadType( Throwable t ) {
        return servantManagerBadType( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER servantManagerBadType(  ) {
        return servantManagerBadType( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int DEFAULT_POA_NOT_POAIMPL = SUNVMCID.value + 1017 ;
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( DEFAULT_POA_NOT_POAIMPL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.defaultPoaNotPoaimpl",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl( CompletionStatus cs ) {
        return defaultPoaNotPoaimpl( cs, null  ) ;
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl( Throwable t ) {
        return defaultPoaNotPoaimpl( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl(  ) {
        return defaultPoaNotPoaimpl( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int WRONG_POLICIES_FOR_THIS_OBJECT = SUNVMCID.value + 1018 ;
    
    public OBJ_ADAPTER wrongPoliciesForThisObject( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( WRONG_POLICIES_FOR_THIS_OBJECT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.wrongPoliciesForThisObject",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject( CompletionStatus cs ) {
        return wrongPoliciesForThisObject( cs, null  ) ;
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject( Throwable t ) {
        return wrongPoliciesForThisObject( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject(  ) {
        return wrongPoliciesForThisObject( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int THIS_OBJECT_SERVANT_NOT_ACTIVE = SUNVMCID.value + 1019 ;
    
    public OBJ_ADAPTER thisObjectServantNotActive( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( THIS_OBJECT_SERVANT_NOT_ACTIVE, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.thisObjectServantNotActive",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive( CompletionStatus cs ) {
        return thisObjectServantNotActive( cs, null  ) ;
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive( Throwable t ) {
        return thisObjectServantNotActive( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive(  ) {
        return thisObjectServantNotActive( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int THIS_OBJECT_WRONG_POLICY = SUNVMCID.value + 1020 ;
    
    public OBJ_ADAPTER thisObjectWrongPolicy( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( THIS_OBJECT_WRONG_POLICY, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.thisObjectWrongPolicy",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy( CompletionStatus cs ) {
        return thisObjectWrongPolicy( cs, null  ) ;
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy( Throwable t ) {
        return thisObjectWrongPolicy( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy(  ) {
        return thisObjectWrongPolicy( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NO_CONTEXT = SUNVMCID.value + 1021 ;
    
    public OBJ_ADAPTER noContext( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( NO_CONTEXT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "POA.noContext",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER noContext( CompletionStatus cs ) {
        return noContext( cs, null  ) ;
    }
    
    public OBJ_ADAPTER noContext( Throwable t ) {
        return noContext( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER noContext(  ) {
        return noContext( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int INCARNATE_RETURNED_NULL = SUNVMCID.value + 1022 ;
    
    public OBJ_ADAPTER incarnateReturnedNull( CompletionStatus cs, Throwable t ) {
        OBJ_ADAPTER exc = new OBJ_ADAPTER( INCARNATE_RETURNED_NULL, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.incarnateReturnedNull",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJ_ADAPTER incarnateReturnedNull( CompletionStatus cs ) {
        return incarnateReturnedNull( cs, null  ) ;
    }
    
    public OBJ_ADAPTER incarnateReturnedNull( Throwable t ) {
        return incarnateReturnedNull( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJ_ADAPTER incarnateReturnedNull(  ) {
        return incarnateReturnedNull( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int JTS_INIT_ERROR = SUNVMCID.value + 1001 ;
    
    public INITIALIZE jtsInitError( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( JTS_INIT_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.jtsInitError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE jtsInitError( CompletionStatus cs ) {
        return jtsInitError( cs, null  ) ;
    }
    
    public INITIALIZE jtsInitError( Throwable t ) {
        return jtsInitError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE jtsInitError(  ) {
        return jtsInitError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PERSISTENT_SERVERID_NOT_SET = SUNVMCID.value + 1002 ;
    
    public INITIALIZE persistentServeridNotSet( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( PERSISTENT_SERVERID_NOT_SET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.persistentServeridNotSet",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE persistentServeridNotSet( CompletionStatus cs ) {
        return persistentServeridNotSet( cs, null  ) ;
    }
    
    public INITIALIZE persistentServeridNotSet( Throwable t ) {
        return persistentServeridNotSet( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE persistentServeridNotSet(  ) {
        return persistentServeridNotSet( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int PERSISTENT_SERVERPORT_NOT_SET = SUNVMCID.value + 1003 ;
    
    public INITIALIZE persistentServerportNotSet( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( PERSISTENT_SERVERPORT_NOT_SET, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.persistentServerportNotSet",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE persistentServerportNotSet( CompletionStatus cs ) {
        return persistentServerportNotSet( cs, null  ) ;
    }
    
    public INITIALIZE persistentServerportNotSet( Throwable t ) {
        return persistentServerportNotSet( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE persistentServerportNotSet(  ) {
        return persistentServerportNotSet( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ORBD_ERROR = SUNVMCID.value + 1004 ;
    
    public INITIALIZE orbdError( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( ORBD_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.orbdError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE orbdError( CompletionStatus cs ) {
        return orbdError( cs, null  ) ;
    }
    
    public INITIALIZE orbdError( Throwable t ) {
        return orbdError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE orbdError(  ) {
        return orbdError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BOOTSTRAP_ERROR = SUNVMCID.value + 1005 ;
    
    public INITIALIZE bootstrapError( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( BOOTSTRAP_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.bootstrapError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE bootstrapError( CompletionStatus cs ) {
        return bootstrapError( cs, null  ) ;
    }
    
    public INITIALIZE bootstrapError( Throwable t ) {
        return bootstrapError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE bootstrapError(  ) {
        return bootstrapError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int POA_DISCARDING = SUNVMCID.value + 1001 ;
    
    public TRANSIENT poaDiscarding( CompletionStatus cs, Throwable t ) {
        TRANSIENT exc = new TRANSIENT( POA_DISCARDING, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "POA.poaDiscarding",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public TRANSIENT poaDiscarding( CompletionStatus cs ) {
        return poaDiscarding( cs, null  ) ;
    }
    
    public TRANSIENT poaDiscarding( Throwable t ) {
        return poaDiscarding( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public TRANSIENT poaDiscarding(  ) {
        return poaDiscarding( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int OTSHOOKEXCEPTION = SUNVMCID.value + 1001 ;
    
    public UNKNOWN otshookexception( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( OTSHOOKEXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.otshookexception",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN otshookexception( CompletionStatus cs ) {
        return otshookexception( cs, null  ) ;
    }
    
    public UNKNOWN otshookexception( Throwable t ) {
        return otshookexception( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN otshookexception(  ) {
        return otshookexception( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNKNOWN_SERVER_EXCEPTION = SUNVMCID.value + 1002 ;
    
    public UNKNOWN unknownServerException( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( UNKNOWN_SERVER_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.unknownServerException",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN unknownServerException( CompletionStatus cs ) {
        return unknownServerException( cs, null  ) ;
    }
    
    public UNKNOWN unknownServerException( Throwable t ) {
        return unknownServerException( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN unknownServerException(  ) {
        return unknownServerException( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNKNOWN_SERVERAPP_EXCEPTION = SUNVMCID.value + 1003 ;
    
    public UNKNOWN unknownServerappException( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( UNKNOWN_SERVERAPP_EXCEPTION, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.unknownServerappException",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN unknownServerappException( CompletionStatus cs ) {
        return unknownServerappException( cs, null  ) ;
    }
    
    public UNKNOWN unknownServerappException( Throwable t ) {
        return unknownServerappException( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN unknownServerappException(  ) {
        return unknownServerappException( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNKNOWN_LOCALINVOCATION_ERROR = SUNVMCID.value + 1004 ;
    
    public UNKNOWN unknownLocalinvocationError( CompletionStatus cs, Throwable t ) {
        UNKNOWN exc = new UNKNOWN( UNKNOWN_LOCALINVOCATION_ERROR, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.unknownLocalinvocationError",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public UNKNOWN unknownLocalinvocationError( CompletionStatus cs ) {
        return unknownLocalinvocationError( cs, null  ) ;
    }
    
    public UNKNOWN unknownLocalinvocationError( Throwable t ) {
        return unknownLocalinvocationError( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public UNKNOWN unknownLocalinvocationError(  ) {
        return unknownLocalinvocationError( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    



    
    public static final int ADAPTER_ACTIVATOR_NONEXISTENT = SUNVMCID.value + 1001 ;
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( ADAPTER_ACTIVATOR_NONEXISTENT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.adapterActivatorNonexistent",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent( CompletionStatus cs ) {
        return adapterActivatorNonexistent( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent( Throwable t ) {
        return adapterActivatorNonexistent( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent(  ) {
        return adapterActivatorNonexistent( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ADAPTER_ACTIVATOR_FAILED = SUNVMCID.value + 1002 ;
    
    public OBJECT_NOT_EXIST adapterActivatorFailed( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( ADAPTER_ACTIVATOR_FAILED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.adapterActivatorFailed",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed( CompletionStatus cs ) {
        return adapterActivatorFailed( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed( Throwable t ) {
        return adapterActivatorFailed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed(  ) {
        return adapterActivatorFailed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int BAD_SKELETON = SUNVMCID.value + 1003 ;
    
    public OBJECT_NOT_EXIST badSkeleton( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( BAD_SKELETON, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.badSkeleton",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST badSkeleton( CompletionStatus cs ) {
        return badSkeleton( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST badSkeleton( Throwable t ) {
        return badSkeleton( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST badSkeleton(  ) {
        return badSkeleton( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int NULL_SERVANT = SUNVMCID.value + 1004 ;
    
    public OBJECT_NOT_EXIST nullServant( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( NULL_SERVANT, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.FINE )) {
            Object[] parameters = null ;
            doLog( Level.FINE, "POA.nullServant",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST nullServant( CompletionStatus cs ) {
        return nullServant( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST nullServant( Throwable t ) {
        return nullServant( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST nullServant(  ) {
        return nullServant( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int ADAPTER_DESTROYED = SUNVMCID.value + 1005 ;
    
    public OBJECT_NOT_EXIST adapterDestroyed( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( ADAPTER_DESTROYED, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.adapterDestroyed",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed( CompletionStatus cs ) {
        return adapterDestroyed( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed( Throwable t ) {
        return adapterDestroyed( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed(  ) {
        return adapterDestroyed( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    
}
