

package com.sun.corba.se.impl.ior ;

import java.util.Iterator ;

import org.omg.CORBA.portable.InputStream ;
import org.omg.CORBA.portable.OutputStream ;
import org.omg.CORBA.portable.StreamableValue ;

import org.omg.CORBA.TypeCode ;

import org.omg.PortableInterceptor.ObjectReferenceTemplate ;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper ;

import com.sun.corba.se.spi.oa.ObjectAdapter ;

import com.sun.corba.se.spi.ior.ObjectId ;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;
import com.sun.corba.se.spi.ior.ObjectAdapterId ;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IORFactories;

import com.sun.corba.se.impl.orbutil.ORBUtility ;

import com.sun.corba.se.spi.orb.ORB ;


public class ObjectReferenceTemplateImpl extends ObjectReferenceProducerBase
    implements ObjectReferenceTemplate, StreamableValue
{
    transient private IORTemplate iorTemplate ;

    public ObjectReferenceTemplateImpl( InputStream is )
    {
        super( (ORB)(is.orb()) ) ;
        _read( is ) ;
    }

    public ObjectReferenceTemplateImpl( ORB orb, IORTemplate iortemp )
    {
        super( orb ) ;
        iorTemplate = iortemp ;
    }

    public boolean equals( Object obj )
    {
        if (!(obj instanceof ObjectReferenceTemplateImpl))
            return false ;

        ObjectReferenceTemplateImpl other = (ObjectReferenceTemplateImpl)obj ;

        return (iorTemplate != null) &&
            iorTemplate.equals( other.iorTemplate ) ;
    }

    public int hashCode()
    {
        return iorTemplate.hashCode() ;
    }

    public static final String repositoryId =
        "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" ;

    public String[] _truncatable_ids()
    {
        return new String[] { repositoryId } ;
    }

    public TypeCode _type()
    {
        return ObjectReferenceTemplateHelper.type() ;
    }


    public void _read( InputStream is )
    {
        org.omg.CORBA_2_3.portable.InputStream istr =
            (org.omg.CORBA_2_3.portable.InputStream)is ;
        iorTemplate = IORFactories.makeIORTemplate( istr ) ;
        orb = (ORB)(istr.orb()) ;
    }


    public void _write( OutputStream os )
    {
        org.omg.CORBA_2_3.portable.OutputStream ostr =
            (org.omg.CORBA_2_3.portable.OutputStream)os ;

        iorTemplate.write( ostr ) ;
    }

    public String server_id ()
    {
        int val = iorTemplate.getObjectKeyTemplate().getServerId() ;
        return Integer.toString( val ) ;
    }

    public String orb_id ()
    {
        return iorTemplate.getObjectKeyTemplate().getORBId() ;
    }

    public String[] adapter_name()
    {
        ObjectAdapterId poaid =
            iorTemplate.getObjectKeyTemplate().getObjectAdapterId() ;

        return poaid.getAdapterName() ;
    }

    public IORFactory getIORFactory()
    {
        return iorTemplate ;
    }

    public IORTemplateList getIORTemplateList()
    {
        IORTemplateList tl = IORFactories.makeIORTemplateList() ;
        tl.add( iorTemplate ) ;
        tl.makeImmutable() ;
        return tl ;
    }
}
