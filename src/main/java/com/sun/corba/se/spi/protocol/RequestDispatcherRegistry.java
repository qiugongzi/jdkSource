

package com.sun.corba.se.spi.protocol;

import java.util.Set;

import com.sun.corba.se.pept.protocol.ClientRequestDispatcher ;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory ;

import com.sun.corba.se.spi.oa.ObjectAdapterFactory ;


public interface RequestDispatcherRegistry {


    void registerClientRequestDispatcher( ClientRequestDispatcher csc, int scid) ;


    ClientRequestDispatcher getClientRequestDispatcher( int scid ) ;


    void registerLocalClientRequestDispatcherFactory( LocalClientRequestDispatcherFactory csc, int scid) ;


    LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory( int scid ) ;


    void registerServerRequestDispatcher( CorbaServerRequestDispatcher ssc, int scid) ;


    CorbaServerRequestDispatcher getServerRequestDispatcher(int scid) ;


    void registerServerRequestDispatcher( CorbaServerRequestDispatcher ssc, String name ) ;


    CorbaServerRequestDispatcher getServerRequestDispatcher( String name ) ;


    void registerObjectAdapterFactory( ObjectAdapterFactory oaf, int scid) ;


    ObjectAdapterFactory getObjectAdapterFactory( int scid ) ;


    Set<ObjectAdapterFactory> getObjectAdapterFactories();
}
