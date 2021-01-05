

package com.sun.corba.se.impl.presentation.rmi ;

import java.lang.reflect.Field ;

import java.util.Hashtable;

import javax.naming.*;
import javax.naming.spi.StateFactory;

import java.security.AccessController ;
import java.security.PrivilegedAction ;

import javax.rmi.PortableRemoteObject ;

import com.sun.corba.se.spi.orb.ORB;

import java.rmi.Remote;
import java.rmi.server.ExportException;

import com.sun.jndi.cosnaming.CNCtx ;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter ;



public class JNDIStateFactoryImpl implements StateFactory
{
    private static final Field orbField ;

    static {
        orbField = (Field) AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    Field fld = null ;
                    try {
                        Class cls = CNCtx.class ;
                        fld = cls.getDeclaredField( "_orb" ) ;
                        fld.setAccessible( true ) ;
                    } catch (Exception exc) {
                        }
                    return fld ;
                }
            }
        ) ;
    }

    public JNDIStateFactoryImpl()
    {
    }


    public Object getStateToBind(Object orig, Name name, Context ctx,
        Hashtable<?,?> env) throws NamingException
    {
        if (orig instanceof org.omg.CORBA.Object)
            return orig ;

        if (!(orig instanceof Remote))
            return null ;

        ORB orb = getORB( ctx ) ;
        if (orb == null)
            return null ;

        Remote stub = null;

        try {
            stub = PortableRemoteObject.toStub( (Remote)orig ) ;
        } catch (Exception exc) {
            return null ;
        }

        if (StubAdapter.isStub( stub )) {
            try {
                StubAdapter.connect( stub, orb ) ;
            } catch (Exception exc) {
                if (!(exc instanceof java.rmi.RemoteException)) {
                    return null ;
                }

                }
        }

        return stub ;
    }

    private ORB getORB( Context ctx )
    {
        ORB orb = null ;

        try {
            orb = (ORB)orbField.get( ctx ) ;
        } catch (Exception exc) {
            }

        return orb ;
    }
}
