

package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.OBJ_ADAPTER ;
import org.omg.CORBA.UNKNOWN ;
import org.omg.CORBA.CompletionStatus ;

import org.omg.CORBA.portable.ServantObject;

import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.ForwardException ;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.OAInvocationInfo ;
import com.sun.corba.se.spi.oa.OADestroyed;

import com.sun.corba.se.spi.orb.ORB;

import com.sun.corba.se.spi.ior.IOR ;

import com.sun.corba.se.spi.logging.CORBALogDomains ;

import com.sun.corba.se.impl.logging.POASystemException ;
import com.sun.corba.se.impl.logging.ORBUtilSystemException ;

public class POALocalCRDImpl extends LocalClientRequestDispatcherBase
{
    private ORBUtilSystemException wrapper ;
    private POASystemException poaWrapper ;

    public POALocalCRDImpl( ORB orb, int scid, IOR ior)
    {
        super( (com.sun.corba.se.spi.orb.ORB)orb, scid, ior );
        wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.RPC_PROTOCOL ) ;
        poaWrapper = POASystemException.get( orb,
            CORBALogDomains.RPC_PROTOCOL ) ;
    }

    private OAInvocationInfo servantEnter( ObjectAdapter oa ) throws OADestroyed
    {
        oa.enter() ;

        OAInvocationInfo info = oa.makeInvocationInfo( objectId ) ;
        orb.pushInvocationInfo( info ) ;

        return info ;
    }

    private void servantExit( ObjectAdapter oa )
    {
        try {
            oa.returnServant();
        } finally {
            oa.exit() ;
            orb.popInvocationInfo() ;
        }
    }

    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
                                           Class expectedType)
    {
        ObjectAdapter oa = oaf.find( oaid ) ;
        OAInvocationInfo info = null ;

        try {
            info = servantEnter( oa ) ;
            info.setOperation( operation ) ;
        } catch ( OADestroyed ex ) {
            return servant_preinvoke(self, operation, expectedType);
        }

        try {
            try {
                oa.getInvocationServant( info );
                if (!checkForCompatibleServant( info, expectedType ))
                    return null ;
            } catch (Throwable thr) {
                servantExit( oa ) ;
                throw thr ;
            }
        } catch ( ForwardException ex ) {

            RuntimeException runexc = new RuntimeException("deal with this.");
            runexc.initCause( ex ) ;
            throw runexc ;
        } catch ( ThreadDeath ex ) {
            throw wrapper.runtimeexception( ex ) ;
        } catch ( Throwable t ) {
            if (t instanceof SystemException)
                throw (SystemException)t ;

            throw poaWrapper.localServantLookup( t ) ;
        }

        if (!checkForCompatibleServant( info, expectedType )) {
            servantExit( oa ) ;
            return null ;
        }

        return info;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj)
    {
        ObjectAdapter oa = orb.peekInvocationInfo().oa() ;
        servantExit( oa ) ;
    }
}

