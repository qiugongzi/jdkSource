

package com.sun.corba.se.spi.protocol;

import java.io.Closeable;

import org.omg.PortableInterceptor.ObjectReferenceTemplate ;
import org.omg.PortableInterceptor.Interceptor ;
import org.omg.PortableInterceptor.Current ;
import org.omg.PortableInterceptor.PolicyFactory ;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName ;

import org.omg.CORBA.NVList ;
import org.omg.CORBA.Any ;
import org.omg.CORBA.Policy ;
import org.omg.CORBA.PolicyError ;

import org.omg.CORBA.portable.RemarshalException;

import com.sun.corba.se.spi.oa.ObjectAdapter ;

import com.sun.corba.se.spi.protocol.CorbaMessageMediator ;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;

import com.sun.corba.se.impl.corba.RequestImpl ;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage ;


public interface PIHandler extends Closeable {

    public void initialize() ;

    public void destroyInterceptors() ;




    void objectAdapterCreated( ObjectAdapter oa )  ;


    void adapterManagerStateChanged( int managerId,
        short newState ) ;


    void adapterStateChanged( ObjectReferenceTemplate[] templates,
        short newState ) ;




    void disableInterceptorsThisThread() ;


    void enableInterceptorsThisThread() ;


    void invokeClientPIStartingPoint()
        throws RemarshalException ;


    Exception invokeClientPIEndingPoint(
        int replyStatus, Exception exception ) ;


    Exception makeCompletedClientRequest(
        int replyStatus, Exception exception ) ;


    void initiateClientPIRequest( boolean diiRequest ) ;


    void cleanupClientPIRequest() ;


    void setClientPIInfo( RequestImpl requestImpl ) ;


    void setClientPIInfo(CorbaMessageMediator messageMediator) ;




    void invokeServerPIStartingPoint() ;


    void invokeServerPIIntermediatePoint() ;


    void invokeServerPIEndingPoint( ReplyMessage replyMessage ) ;


    void initializeServerPIInfo( CorbaMessageMediator request,
        ObjectAdapter oa, byte[] objectId, ObjectKeyTemplate oktemp ) ;


    void setServerPIInfo( java.lang.Object servant,
                                    String targetMostDerivedInterface ) ;


    void setServerPIInfo( Exception exception ) ;


    void setServerPIInfo( NVList arguments ) ;


    void setServerPIExceptionInfo( Any exception ) ;


    void setServerPIInfo( Any result ) ;


    void cleanupServerPIRequest() ;

    Policy create_policy( int type, Any val ) throws PolicyError ;

    void register_interceptor( Interceptor interceptor, int type )
        throws DuplicateName ;

    Current getPICurrent() ;

    void registerPolicyFactory( int type, PolicyFactory factory ) ;

    int allocateServerRequestId() ;
}
