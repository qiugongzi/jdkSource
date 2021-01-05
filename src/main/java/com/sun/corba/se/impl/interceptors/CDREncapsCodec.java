

package com.sun.corba.se.impl.interceptors;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.LocalObject;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.logging.CORBALogDomains;

import sun.corba.EncapsInputStreamFactory;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;


public final class CDREncapsCodec
    extends org.omg.CORBA.LocalObject
    implements Codec
{
    private ORB orb;
    ORBUtilSystemException wrapper;

    private GIOPVersion giopVersion;




    public CDREncapsCodec( ORB orb, int major, int minor ) {
        this.orb = orb;
        wrapper = ORBUtilSystemException.get(
            (com.sun.corba.se.spi.orb.ORB)orb, CORBALogDomains.RPC_PROTOCOL ) ;

        giopVersion = GIOPVersion.getInstance( (byte)major, (byte)minor );
    }


    public byte[] encode( Any data )
        throws InvalidTypeForEncoding
    {
        if ( data == null )
            throw wrapper.nullParam() ;
        return encodeImpl( data, true );
    }


    public Any decode ( byte[] data )
        throws FormatMismatch
    {
        if( data == null )
            throw wrapper.nullParam() ;
        return decodeImpl( data, null );
    }


    public byte[] encode_value( Any data )
        throws InvalidTypeForEncoding
    {
        if( data == null )
            throw wrapper.nullParam() ;
        return encodeImpl( data, false );
    }


    public Any decode_value( byte[] data, TypeCode tc )
        throws FormatMismatch, TypeMismatch
    {
        if( data == null )
            throw wrapper.nullParam() ;
        if( tc == null )
            throw  wrapper.nullParam() ;
        return decodeImpl( data, tc );
    }


    private byte[] encodeImpl( Any data, boolean sendTypeCode )
        throws InvalidTypeForEncoding
    {
        if( data == null )
            throw wrapper.nullParam() ;

        EncapsOutputStream cdrOut =
            sun.corba.OutputStreamFactory.newEncapsOutputStream(
            (com.sun.corba.se.spi.orb.ORB)orb, giopVersion );

        cdrOut.putEndian();

        if( sendTypeCode ) {
            cdrOut.write_TypeCode( data.type() );
        }

        data.write_value( cdrOut );

        return cdrOut.toByteArray();
    }


    private Any decodeImpl( byte[] data, TypeCode tc )
        throws FormatMismatch
    {
        if( data == null )
            throw wrapper.nullParam() ;

        AnyImpl any = null;  try {
            EncapsInputStream cdrIn = EncapsInputStreamFactory.newEncapsInputStream( orb, data,
                    data.length, giopVersion );


            cdrIn.consumeEndian();

            if( tc == null ) {
                tc = cdrIn.read_TypeCode();
            }

            any = new AnyImpl( (com.sun.corba.se.spi.orb.ORB)orb );
            any.read_value( cdrIn, tc );
        }
        catch( RuntimeException e ) {
            throw new FormatMismatch();
        }

        return any;
    }
}
