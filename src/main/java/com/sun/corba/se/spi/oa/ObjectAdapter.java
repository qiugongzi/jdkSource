

package com.sun.corba.se.spi.oa ;

import org.omg.CORBA.Policy ;

import org.omg.PortableInterceptor.ObjectReferenceTemplate ;
import org.omg.PortableInterceptor.ObjectReferenceFactory ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.oa.OADestroyed ;

import com.sun.corba.se.spi.ior.IORTemplate ;

public interface ObjectAdapter
{
    ORB getORB() ;

    Policy getEffectivePolicy( int type ) ;


    IORTemplate getIORTemplate() ;

    int getManagerId() ;


    short getState() ;

    ObjectReferenceTemplate getAdapterTemplate() ;

    ObjectReferenceFactory getCurrentFactory() ;


    void setCurrentFactory( ObjectReferenceFactory factory ) ;

    org.omg.CORBA.Object getLocalServant( byte[] objectId ) ;


    void getInvocationServant( OAInvocationInfo info ) ;


    void enter( ) throws OADestroyed ;


    void exit( ) ;


    public void returnServant() ;


    OAInvocationInfo makeInvocationInfo( byte[] objectId ) ;


    String[] getInterfaces( Object servant, byte[] objectId ) ;
}
