

package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB ;
import com.sun.corba.se.impl.encoding.CDRInputStream ;
import com.sun.corba.se.impl.encoding.EncapsInputStream ;
import com.sun.corba.se.impl.encoding.EncapsOutputStream ;
import com.sun.corba.se.impl.orbutil.ORBUtility ;


public abstract class ServiceContext {

    protected ServiceContext() { }

    private void dprint( String msg )
    {
        ORBUtility.dprint( this, msg ) ;
    }


    protected ServiceContext(InputStream s, GIOPVersion gv) throws SystemException
    {
        in = s;
    }


    public abstract int getId() ;


    public void write(OutputStream s, GIOPVersion gv) throws SystemException
    {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream((ORB)(s.orb()), gv);
        os.putEndian() ;
        writeData( os ) ;
        byte[] data = os.toByteArray() ;

        s.write_long(getId());
        s.write_long(data.length);
        s.write_octet_array(data, 0, data.length);
    }


    protected abstract void writeData( OutputStream os ) ;


    protected InputStream in = null ;

    public String toString()
    {
        return "ServiceContext[ id=" + getId() + " ]" ;
    }
}
