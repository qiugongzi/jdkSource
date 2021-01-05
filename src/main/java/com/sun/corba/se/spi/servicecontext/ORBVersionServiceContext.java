

package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.orb.ORBVersion ;
import com.sun.corba.se.spi.orb.ORBVersionFactory ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.servicecontext.ServiceContext ;
import com.sun.corba.se.impl.orbutil.ORBConstants ;

public class ORBVersionServiceContext extends ServiceContext {

    public ORBVersionServiceContext( )
    {
        version = ORBVersionFactory.getORBVersion() ;
    }

    public ORBVersionServiceContext( ORBVersion ver )
    {
        this.version = ver ;
    }

    public ORBVersionServiceContext(InputStream is, GIOPVersion gv)
    {
        super(is, gv) ;





        version = ORBVersionFactory.create( in ) ;
    }


    public static final int SERVICE_CONTEXT_ID = ORBConstants.TAG_ORB_VERSION ;
    public int getId() { return SERVICE_CONTEXT_ID ; }

    public void writeData( OutputStream os ) throws SystemException
    {
        version.write( os ) ;
    }

    public ORBVersion getVersion()
    {
        return version ;
    }


    private ORBVersion version = ORBVersionFactory.getORBVersion() ;

    public String toString()
    {
        return "ORBVersionServiceContext[ version=" + version + " ]" ;
    }
}
