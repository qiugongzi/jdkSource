

package com.sun.corba.se.impl.ior;

import java.io.IOException ;

import org.omg.CORBA.MARSHAL ;
import org.omg.CORBA.OctetSeqHolder ;
import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.spi.ior.ObjectId ;
import com.sun.corba.se.spi.ior.ObjectKey ;
import com.sun.corba.se.spi.ior.ObjectKeyFactory ;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;

import com.sun.corba.se.spi.orb.ORB ;
import com.sun.corba.se.spi.logging.CORBALogDomains ;

import com.sun.corba.se.impl.orbutil.ORBConstants ;

import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate ;
import com.sun.corba.se.impl.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.impl.ior.WireObjectKeyTemplate ;
import com.sun.corba.se.impl.ior.ObjectIdImpl ;
import com.sun.corba.se.impl.ior.ObjectKeyImpl ;
import com.sun.corba.se.impl.logging.IORSystemException ;

import com.sun.corba.se.impl.encoding.EncapsInputStream ;
import sun.corba.EncapsInputStreamFactory;



interface Handler {
    ObjectKeyTemplate handle( int magic, int scid,
        InputStream is, OctetSeqHolder osh ) ;
}


public class ObjectKeyFactoryImpl implements ObjectKeyFactory
{
    public static final int MAGIC_BASE                  = 0xAFABCAFE ;

    public static final int JAVAMAGIC_OLD               = MAGIC_BASE ;

    public static final int JAVAMAGIC_NEW               = MAGIC_BASE + 1 ;

    public static final int JAVAMAGIC_NEWER             = MAGIC_BASE + 2 ;

    public static final int MAX_MAGIC                   = JAVAMAGIC_NEWER ;

    public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;

    private final ORB orb ;
    private IORSystemException wrapper ;

    public ObjectKeyFactoryImpl( ORB orb )
    {
        this.orb = orb ;
        wrapper = IORSystemException.get( orb,
            CORBALogDomains.OA_IOR ) ;
    }

    private Handler fullKey = new Handler() {
        public ObjectKeyTemplate handle( int magic, int scid,
            InputStream is, OctetSeqHolder osh ) {
                ObjectKeyTemplate oktemp = null ;

                if ((scid >= ORBConstants.FIRST_POA_SCID) &&
                    (scid <= ORBConstants.MAX_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER)
                        oktemp = new POAObjectKeyTemplate( orb, magic, scid, is, osh ) ;
                    else
                        oktemp = new OldPOAObjectKeyTemplate( orb, magic, scid, is, osh ) ;
                } else if ((scid >= 0) && (scid < ORBConstants.FIRST_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER)
                        oktemp = new JIDLObjectKeyTemplate( orb, magic, scid, is, osh ) ;
                    else
                        oktemp = new OldJIDLObjectKeyTemplate( orb, magic, scid, is, osh );
                }

                return oktemp ;
            }
        } ;


    private Handler oktempOnly = new Handler() {
        public ObjectKeyTemplate handle( int magic, int scid,
            InputStream is, OctetSeqHolder osh ) {
                ObjectKeyTemplate oktemp = null ;

                if ((scid >= ORBConstants.FIRST_POA_SCID) &&
                    (scid <= ORBConstants.MAX_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER)
                        oktemp = new POAObjectKeyTemplate( orb, magic, scid, is ) ;
                    else
                        oktemp = new OldPOAObjectKeyTemplate( orb, magic, scid, is ) ;
                } else if ((scid >= 0) && (scid < ORBConstants.FIRST_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER)
                        oktemp = new JIDLObjectKeyTemplate( orb, magic, scid, is ) ;
                    else
                        oktemp = new OldJIDLObjectKeyTemplate( orb, magic, scid, is ) ;
                }

                return oktemp ;
            }
        } ;


    private boolean validMagic( int magic )
    {
        return (magic >= MAGIC_BASE) && (magic <= MAX_MAGIC) ;
    }


    private ObjectKeyTemplate create( InputStream is, Handler handler,
        OctetSeqHolder osh )
    {
        ObjectKeyTemplate oktemp = null ;

        try {
            is.mark(0) ;
            int magic = is.read_long() ;

            if (validMagic( magic )) {
                int scid = is.read_long() ;
                oktemp = handler.handle( magic, scid, is, osh ) ;
            }
        } catch (MARSHAL mexc) {
            }

        if (oktemp == null)
            try {
                is.reset() ;
            } catch (IOException exc) {
                }

        return oktemp ;
    }

    public ObjectKey create( byte[] key )
    {
        OctetSeqHolder osh = new OctetSeqHolder() ;
        EncapsInputStream is = EncapsInputStreamFactory.newEncapsInputStream( orb, key, key.length );

        ObjectKeyTemplate oktemp = create( is, fullKey, osh ) ;
        if (oktemp == null)
            oktemp = new WireObjectKeyTemplate( is, osh ) ;

        ObjectId oid = new ObjectIdImpl( osh.value ) ;
        return new ObjectKeyImpl( oktemp, oid ) ;
    }

    public ObjectKeyTemplate createTemplate( InputStream is )
    {
        ObjectKeyTemplate oktemp = create( is, oktempOnly, null ) ;
        if (oktemp == null)
            oktemp = new WireObjectKeyTemplate( orb ) ;

        return oktemp ;
    }
}
