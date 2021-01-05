

package com.sun.corba.se.impl.ior;

import java.util.ListIterator ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.HashMap ;

import java.io.StringWriter;
import java.io.IOException;

import javax.rmi.CORBA.Util;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import org.omg.IOP.TAG_INTERNET_IOP ;

import com.sun.corba.se.spi.ior.ObjectId ;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate ;
import com.sun.corba.se.spi.ior.TaggedProfile ;
import com.sun.corba.se.spi.ior.IOR ;
import com.sun.corba.se.spi.ior.IORTemplate ;
import com.sun.corba.se.spi.ior.IORTemplateList ;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder ;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase ;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;
import com.sun.corba.se.spi.ior.IORFactories ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;

import com.sun.corba.se.spi.orb.ORB;

import com.sun.corba.se.spi.logging.CORBALogDomains;

import com.sun.corba.se.impl.encoding.MarshalOutputStream;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;

import com.sun.corba.se.impl.orbutil.HexOutputStream;
import com.sun.corba.se.impl.orbutil.ORBConstants;

import com.sun.corba.se.impl.logging.IORSystemException ;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile ;


public class IORImpl extends IdentifiableContainerBase implements IOR
{
    private String typeId;
    private ORB factory = null ;
    private boolean isCachedHashValue = false;
    private int cachedHashValue;
    IORSystemException wrapper ;

    public ORB getORB()
    {
        return factory ;
    }


    private IORTemplateList iortemps = null ;

    public boolean equals( Object obj )
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof IOR))
            return false ;

        IOR other = (IOR)obj ;

        return super.equals( obj ) && typeId.equals( other.getTypeId() ) ;
    }

    public synchronized int hashCode()
    {
        if (! isCachedHashValue) {
              cachedHashValue =  (super.hashCode() ^ typeId.hashCode());
              isCachedHashValue = true;
        }
        return cachedHashValue;
    }


    public IORImpl( ORB orb )
    {
        this( orb, "" ) ;
    }

    public IORImpl( ORB orb, String typeid )
    {
        factory = orb ;
        wrapper = IORSystemException.get( orb,
            CORBALogDomains.OA_IOR ) ;
        this.typeId = typeid ;
    }


    public IORImpl( ORB orb, String typeId, IORTemplate iortemp, ObjectId id)
    {
        this( orb, typeId ) ;

        this.iortemps = IORFactories.makeIORTemplateList() ;
        this.iortemps.add( iortemp ) ;

        addTaggedProfiles( iortemp, id ) ;

        makeImmutable() ;
    }

    private void addTaggedProfiles( IORTemplate iortemp, ObjectId id )
    {
        ObjectKeyTemplate oktemp = iortemp.getObjectKeyTemplate() ;
        Iterator templateIterator = iortemp.iterator() ;

        while (templateIterator.hasNext()) {
            TaggedProfileTemplate ptemp =
                (TaggedProfileTemplate)(templateIterator.next()) ;

            TaggedProfile profile = ptemp.create( oktemp, id ) ;

            add( profile ) ;
        }
    }


    public IORImpl( ORB orb, String typeId, IORTemplateList iortemps, ObjectId id)
    {
        this( orb, typeId ) ;

        this.iortemps = iortemps ;

        Iterator iter = iortemps.iterator() ;
        while (iter.hasNext()) {
            IORTemplate iortemp = (IORTemplate)(iter.next()) ;
            addTaggedProfiles( iortemp, id ) ;
        }

        makeImmutable() ;
    }

    public IORImpl(InputStream is)
    {
        this( (ORB)(is.orb()), is.read_string() ) ;

        IdentifiableFactoryFinder finder =
            factory.getTaggedProfileFactoryFinder() ;

        EncapsulationUtility.readIdentifiableSequence( this, finder, is ) ;

        makeImmutable() ;
    }

    public String getTypeId()
    {
        return typeId ;
    }

    public void write(OutputStream os)
    {
        os.write_string( typeId ) ;
        EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }

    public String stringify()
    {
        StringWriter bs;

        MarshalOutputStream s =
            sun.corba.OutputStreamFactory.newEncapsOutputStream(factory);
        s.putEndian();
        write( (OutputStream)s );
        bs = new StringWriter();
        try {
            s.writeTo(new HexOutputStream(bs));
        } catch (IOException ex) {
            throw wrapper.stringifyWriteError( ex ) ;
        }

        return ORBConstants.STRINGIFY_PREFIX + bs;
    }

    public synchronized void makeImmutable()
    {
        makeElementsImmutable() ;

        if (iortemps != null)
            iortemps.makeImmutable() ;

        super.makeImmutable() ;
    }

    public org.omg.IOP.IOR getIOPIOR() {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream(factory);
        write(os);
        InputStream is = (InputStream) (os.create_input_stream());
        return org.omg.IOP.IORHelper.read(is);
    }

    public boolean isNil()
    {
        return ((size() == 0) );
    }

    public boolean isEquivalent(IOR ior)
    {
        Iterator myIterator = iterator() ;
        Iterator otherIterator = ior.iterator() ;
        while (myIterator.hasNext() && otherIterator.hasNext()) {
            TaggedProfile myProfile = (TaggedProfile)(myIterator.next()) ;
            TaggedProfile otherProfile = (TaggedProfile)(otherIterator.next()) ;
            if (!myProfile.isEquivalent( otherProfile ))
                return false ;
        }

        return myIterator.hasNext() == otherIterator.hasNext() ;
    }

    private void initializeIORTemplateList()
    {
        Map oktempToIORTemplate = new HashMap() ;

        iortemps = IORFactories.makeIORTemplateList() ;
        Iterator iter = iterator() ;
        ObjectId oid = null ; while (iter.hasNext()) {
            TaggedProfile prof = (TaggedProfile)(iter.next()) ;
            TaggedProfileTemplate ptemp = prof.getTaggedProfileTemplate() ;
            ObjectKeyTemplate oktemp = prof.getObjectKeyTemplate() ;

            if (oid == null)
                oid = prof.getObjectId() ;
            else if (!oid.equals( prof.getObjectId() ))
                throw wrapper.badOidInIorTemplateList() ;

            IORTemplate iortemp = (IORTemplate)(oktempToIORTemplate.get( oktemp )) ;
            if (iortemp == null) {
                iortemp = IORFactories.makeIORTemplate( oktemp ) ;
                oktempToIORTemplate.put( oktemp, iortemp ) ;
                iortemps.add( iortemp ) ;
            }

            iortemp.add( ptemp ) ;
        }

        iortemps.makeImmutable() ;
    }


    public synchronized IORTemplateList getIORTemplates()
    {
        if (iortemps == null)
            initializeIORTemplateList() ;

        return iortemps ;
    }


    public IIOPProfile getProfile()
    {
        IIOPProfile iop = null ;
        Iterator iter = iteratorById( TAG_INTERNET_IOP.value ) ;
        if (iter.hasNext())
            iop = (IIOPProfile)(iter.next()) ;

        if (iop != null)
            return iop ;

        throw wrapper.iorMustHaveIiopProfile() ;
    }
}
