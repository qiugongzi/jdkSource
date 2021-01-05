

package com.sun.corba.se.impl.ior;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.omg.IOP.TAG_INTERNET_IOP ;

import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.spi.ior.TaggedComponent ;
import com.sun.corba.se.spi.ior.Identifiable ;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder ;
import com.sun.corba.se.spi.ior.WriteContents ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.impl.ior.FreezableList ;

import com.sun.corba.se.impl.encoding.CDROutputStream ;
import com.sun.corba.se.impl.encoding.EncapsOutputStream ;
import com.sun.corba.se.impl.encoding.EncapsInputStream ;

import sun.corba.EncapsInputStreamFactory;


public class EncapsulationUtility
{
    private EncapsulationUtility()
    {
    }


    public static void readIdentifiableSequence( List container,
        IdentifiableFactoryFinder finder, InputStream istr)
    {
        int count = istr.read_long() ;
        for (int ctr = 0; ctr<count; ctr++) {
            int id = istr.read_long() ;
            Identifiable obj = finder.create( id, istr ) ;
            container.add( obj ) ;
        }
    }


    public static  void writeIdentifiableSequence( List container, OutputStream os)
    {
        os.write_long( container.size() ) ;
        Iterator iter = container.iterator() ;
        while (iter.hasNext()) {
            Identifiable obj = (Identifiable)( iter.next() ) ;
            os.write_long( obj.getId() ) ;
            obj.write( os ) ;
        }
    }


    static public void writeOutputStream( OutputStream dataStream,
        OutputStream os )
    {
        byte[] data = ((CDROutputStream)dataStream).toByteArray() ;
        os.write_long( data.length ) ;
        os.write_octet_array( data, 0, data.length ) ;
    }


    static public InputStream getEncapsulationStream( InputStream is )
    {
        byte[] data = readOctets( is ) ;
        EncapsInputStream result = EncapsInputStreamFactory.newEncapsInputStream( is.orb(), data,
                data.length ) ;
        result.consumeEndian() ;
        return result ;
    }


    static public byte[] readOctets( InputStream is )
    {
        int len = is.read_ulong() ;
        byte[] data = new byte[len] ;
        is.read_octet_array( data, 0, len ) ;
        return data ;
    }

    static public void writeEncapsulation( WriteContents obj,
        OutputStream os )
    {
        EncapsOutputStream out =
            sun.corba.OutputStreamFactory.newEncapsOutputStream((ORB)os.orb());

        out.putEndian() ;

        obj.writeContents( out ) ;

        writeOutputStream( out, os ) ;
    }
}
