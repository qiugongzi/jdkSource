

package com.sun.corba.se.impl.naming.pcosnaming;


import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CosNaming.NamingContextExtPackage.*;

import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;

import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.logging.CORBALogDomains;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.logging.NamingSystemException;

import java.io.Serializable;
import java.util.Hashtable;




public class NamingContextImpl
    extends NamingContextExtPOA
    implements NamingContextDataStore, Serializable
{

    private transient ORB orb;

    private final String objKey;

    private final Hashtable theHashtable = new Hashtable( );

    private transient NameService theNameServiceHandle;

    private transient ServantManagerImpl theServantManagerImplHandle;

    private transient com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl insImpl;

    private transient NamingSystemException readWrapper ;

    private transient NamingSystemException updateWrapper ;

    private static POA biPOA = null;



    public NamingContextImpl(ORB orb, String objKey,
        NameService theNameService, ServantManagerImpl theServantManagerImpl  )
        throws Exception
    {
        super();

        this.orb = orb;
        readWrapper = NamingSystemException.get( orb,
            CORBALogDomains.NAMING_READ ) ;
        updateWrapper = NamingSystemException.get( orb,
            CORBALogDomains.NAMING_UPDATE ) ;

        debug = true ; this.objKey = objKey;
        theNameServiceHandle = theNameService;
        theServantManagerImplHandle = theServantManagerImpl;
        insImpl =
            new com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl();
    }

    com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl getINSImpl( )
    {
        if( insImpl == null )
        {
            insImpl =
                new com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl();
        }
        return insImpl;
    }


    public void setRootNameService( NameService theNameService ) {
        theNameServiceHandle = theNameService;
    }

    public void setORB( ORB theOrb ) {
        orb = theOrb;
    }

    public void setServantManagerImpl(
                ServantManagerImpl theServantManagerImpl )
    {
        theServantManagerImplHandle = theServantManagerImpl;
    }

    public POA getNSPOA( ) {
        return theNameServiceHandle.getNSPOA( );
    }





   public void bind(NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if( obj == null ) {
            throw updateWrapper.objectIsNull() ;
        }

        if (debug)
            dprint("bind " + nameToString(n) + " to " + obj);
        NamingContextDataStore impl = (NamingContextDataStore)this;
        doBind(impl,n,obj,false,BindingType.nobject);
    }


   public void bind_context(NameComponent[] n, NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if( nc == null ) {
            throw updateWrapper.objectIsNull() ;
        }
        NamingContextDataStore impl = (NamingContextDataStore)this;
        doBind(impl,n,nc,false,BindingType.ncontext);
    }


   public  void rebind(NameComponent[] n, org.omg.CORBA.Object obj)
        throws       org.omg.CosNaming.NamingContextPackage.NotFound,
                     org.omg.CosNaming.NamingContextPackage.CannotProceed,
                     org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if( obj == null )
        {
            throw updateWrapper.objectIsNull() ;
        }
        try {
            if (debug)
                dprint("rebind " + nameToString(n) + " to " + obj);
            NamingContextDataStore impl = (NamingContextDataStore)this;
            doBind(impl,n,obj,true,BindingType.nobject);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound ex) {
            throw updateWrapper.namingCtxRebindAlreadyBound( ex ) ;
        }
    }


   public  void rebind_context(NameComponent[] n, NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        try {
            if (debug)
                dprint("rebind_context " + nameToString(n) + " to " + nc);
            NamingContextDataStore impl = (NamingContextDataStore)this;
            doBind(impl,n,nc,true,BindingType.ncontext);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound ex) {
            throw updateWrapper.namingCtxRebindAlreadyBound( ex ) ;
        }
    }


   public  org.omg.CORBA.Object resolve(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if (debug)
            dprint("resolve " + nameToString(n));
        NamingContextDataStore impl = (NamingContextDataStore)this;
        return doResolve(impl,n);
    }


   public  void unbind(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if (debug)
            dprint("unbind " + nameToString(n));
        NamingContextDataStore impl = (NamingContextDataStore)this;
        doUnbind(impl,n);
    }


    public  void list(int how_many, BindingListHolder bl, BindingIteratorHolder bi)
    {
        if (debug)
            dprint("list(" + how_many + ")");
        NamingContextDataStore impl = (NamingContextDataStore)this;
        synchronized (impl) {
            impl.List(how_many,bl,bi);
        }
        if (debug && bl.value != null)
            dprint("list(" + how_many + ") -> bindings[" + bl.value.length +
                   "] + iterator: " + bi.value);
    }



    public synchronized NamingContext new_context()
    {
        if (debug)
            dprint("new_context()");
        NamingContextDataStore impl = (NamingContextDataStore)this;
        synchronized (impl) {
            return impl.NewContext();
        }
    }



    public  NamingContext bind_new_context(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        NamingContext nc = null;
        NamingContext rnc = null;
        try {
            if (debug)
                dprint("bind_new_context " + nameToString(n));
            nc = this.new_context();
            this.bind_context(n,nc);
            rnc = nc;
            nc = null;
        } finally {
            try {
                if(nc != null)
                    nc.destroy();
            } catch (org.omg.CosNaming.NamingContextPackage.NotEmpty e) {
            }
        }
        return rnc;
    }


    public  void destroy()
        throws org.omg.CosNaming.NamingContextPackage.NotEmpty
    {
        if (debug)
            dprint("destroy ");
        NamingContextDataStore impl = (NamingContextDataStore)this;
        synchronized (impl) {
            if (impl.IsEmpty() == true)
                impl.Destroy();
            else
                throw new org.omg.CosNaming.NamingContextPackage.NotEmpty();
        }
    }


    private void doBind(NamingContextDataStore impl,
                              NameComponent[] n,
                              org.omg.CORBA.Object obj,
                              boolean rebind,
                              org.omg.CosNaming.BindingType bt)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if (n.length < 1)
            throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

    if (n.length == 1) {
            if( (n[0].id.length() == 0) && (n[0].kind.length() == 0) )
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

            synchronized (impl) {
                BindingTypeHolder bth = new BindingTypeHolder();
                if (rebind) {
                    org.omg.CORBA.Object objRef = impl.Resolve( n[0], bth );
                    if( objRef != null ) {
                        if ( bth.value.value() == BindingType.nobject.value() ) {
                            if ( bt.value() == BindingType.ncontext.value() ) {
                                throw new NotFound(NotFoundReason.not_context, n);
                            }
                        } else {
                            if ( bt.value() == BindingType.nobject.value() ) {
                                throw new NotFound(NotFoundReason.not_object, n);
                            }
                        }
                        impl.Unbind(n[0]);
                    }
                } else {
                    if (impl.Resolve(n[0],bth) != null)
                        throw new org.omg.CosNaming.NamingContextPackage.AlreadyBound();
                }

                impl.Bind(n[0],obj,bt);
            }
        } else {
            NamingContext context = resolveFirstAsContext(impl,n);

            NameComponent[] tail = new NameComponent[n.length - 1];
            System.arraycopy(n,1,tail,0,n.length-1);

      switch (bt.value()) {
            case BindingType._nobject:
                {
                    if (rebind)
                        context.rebind(tail,obj);
                    else
                        context.bind(tail,obj);
                }
                break;
            case BindingType._ncontext:
                {
                    NamingContext objContext = (NamingContext)obj;
                    if (rebind)
                        context.rebind_context(tail,objContext);
                    else
                        context.bind_context(tail,objContext);
                }
                break;
            default:
                throw updateWrapper.namingCtxBadBindingtype() ;
            }
        }
    }



    public static org.omg.CORBA.Object doResolve(NamingContextDataStore impl,
                                                 NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        org.omg.CORBA.Object obj = null;
        BindingTypeHolder bth = new BindingTypeHolder();

        if (n.length < 1)
            throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

        if (n.length == 1) {
            synchronized (impl) {
                obj = impl.Resolve(n[0],bth);
            }
            if (obj == null) {
                throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.missing_node,n);
            }
            return obj;
        } else {
            if ( (n[1].id.length() == 0) && (n[1].kind.length() == 0 ) )
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

            NamingContext context = resolveFirstAsContext(impl,n);

            NameComponent[] tail = new NameComponent[n.length -1];
            System.arraycopy(n,1,tail,0,n.length-1);

            return context.resolve(tail);
        }
    }


    public static void doUnbind(NamingContextDataStore impl,
                                NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if (n.length < 1)
            throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

        if (n.length == 1) {
            if ( (n[0].id.length() == 0) && (n[0].kind.length() == 0 ) )
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();

            org.omg.CORBA.Object objRef = null;
            synchronized (impl) {
                objRef = impl.Unbind(n[0]);
            }

            if (objRef == null)
                throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.missing_node,n);
            return;
        } else {
            NamingContext context = resolveFirstAsContext(impl,n);

            NameComponent[] tail = new NameComponent[n.length - 1];
            System.arraycopy(n,1,tail,0,n.length-1);

      context.unbind(tail);
        }
    }


    protected static NamingContext resolveFirstAsContext(NamingContextDataStore impl,
                                                         NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound {
        org.omg.CORBA.Object topRef = null;
        BindingTypeHolder bth = new BindingTypeHolder();
        NamingContext context = null;

        synchronized (impl) {
            topRef = impl.Resolve(n[0],bth);
            if (topRef == null) {
                throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.missing_node,n);
            }
        }

        if (bth.value != BindingType.ncontext) {
            throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.not_context,n);
        }

        try {
            context = NamingContextHelper.narrow(topRef);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.not_context,n);
        }

        return context;
    }

    public static String nameToString(NameComponent[] name)
    {
        StringBuffer s = new StringBuffer("{");
        if (name != null || name.length > 0) {
            for (int i=0;i<name.length;i++) {
                if (i>0)
                    s.append(",");
                s.append("[").
                    append(name[i].id).
                    append(",").
                    append(name[i].kind).
                    append("]");
            }
        }
        s.append("}");
        return s.toString();
    }

    private static boolean debug ;

    private static void dprint(String msg) {
        NamingUtils.dprint("NamingContextImpl("  +
                           Thread.currentThread().getName() + " at " +
                           System.currentTimeMillis() +
                           " ems): " + msg);
    }



    public void Bind(NameComponent n, org.omg.CORBA.Object obj, BindingType bt)
    {
        if( obj == null ) {
            return;
        }

        InternalBindingKey key = new InternalBindingKey(n);
        InternalBindingValue value;

        try {
            if( bt.value() == BindingType._nobject ) {
                value = new InternalBindingValue(bt, orb.object_to_string(obj) );
                value.setObjectRef( obj );
            } else {
                String theNCKey = theNameServiceHandle.getObjectKey( obj );
                value = new InternalBindingValue( bt, theNCKey );
                value.setObjectRef( obj );
            }

            InternalBindingValue oldValue =
                (InternalBindingValue)this.theHashtable.put(key,value);

            if( oldValue != null) {
                throw updateWrapper.namingCtxRebindAlreadyBound() ;
            } else {
                try {
                    theServantManagerImplHandle.updateContext( objKey, this );
                } catch( Exception e ) {
                    throw updateWrapper.bindUpdateContextFailed( e ) ;
                }
            }
        } catch( Exception e ) {
            throw updateWrapper.bindFailure( e ) ;
        }
    }


    public Object Resolve(NameComponent n, BindingTypeHolder bth)
        throws SystemException
    {
        if( ( n.id.length() == 0 ) &&( n.kind.length() == 0 ) ) {
            bth.value = BindingType.ncontext;
            return theNameServiceHandle.getObjectReferenceFromKey(
                this.objKey );
        }

        InternalBindingKey key = new InternalBindingKey(n);
        InternalBindingValue value =
            (InternalBindingValue) this.theHashtable.get(key);

        if( value == null ) {
            return null;
        }

        Object theObjectFromStringifiedReference = null;
        bth.value = value.theBindingType;

        try {
            if( value.strObjectRef.startsWith( "NC" ) ) {
                bth.value = BindingType.ncontext;
                return theNameServiceHandle.getObjectReferenceFromKey( value.strObjectRef );
            } else {
                theObjectFromStringifiedReference = value.getObjectRef( );

                if (theObjectFromStringifiedReference == null ) {
                    try {
                        theObjectFromStringifiedReference =
                        orb.string_to_object( value.strObjectRef );
                        value.setObjectRef( theObjectFromStringifiedReference );
                    } catch( Exception e ) {
                        throw readWrapper.resolveConversionFailure(
                            CompletionStatus.COMPLETED_MAYBE, e );
                    }
                }
            }
        } catch ( Exception e ) {
            throw readWrapper.resolveFailure(
                CompletionStatus.COMPLETED_MAYBE, e );
        }

        return theObjectFromStringifiedReference;
    }



    public Object Unbind(NameComponent n) throws SystemException
    {
        try {
            InternalBindingKey key = new InternalBindingKey(n);
            InternalBindingValue value = null;

            try {
                value = (InternalBindingValue) this.theHashtable.remove(key);
            } catch( Exception e ) {
                }

            theServantManagerImplHandle.updateContext( objKey, this );

            if( value == null ) {
                return null;
            }

            if( value.strObjectRef.startsWith( "NC" ) ) {
                theServantManagerImplHandle.readInContext( value.strObjectRef );
                Object theObjectFromStringfiedReference =
                theNameServiceHandle.getObjectReferenceFromKey( value.strObjectRef );
                return theObjectFromStringfiedReference;
            } else {
                Object theObjectFromStringifiedReference = value.getObjectRef( );

                if( theObjectFromStringifiedReference == null ) {
                    theObjectFromStringifiedReference =
                    orb.string_to_object( value.strObjectRef );
                }

                return theObjectFromStringifiedReference;
            }
        } catch( Exception e ) {
            throw updateWrapper.unbindFailure( CompletionStatus.COMPLETED_MAYBE, e );
        }
    }



    public void List(int how_many, BindingListHolder bl,
                     BindingIteratorHolder bi) throws SystemException
    {
        if( biPOA == null ) {
            createbiPOA( );
        }
        try {
            PersistentBindingIterator bindingIterator =
                new PersistentBindingIterator(this.orb,
                (Hashtable)this.theHashtable.clone(), biPOA);
            bindingIterator.list(how_many,bl);

            byte[] objectId = biPOA.activate_object( bindingIterator );
            org.omg.CORBA.Object obj = biPOA.id_to_reference( objectId );

            org.omg.CosNaming.BindingIterator bindingRef =
                org.omg.CosNaming.BindingIteratorHelper.narrow( obj );

            bi.value = bindingRef;
        } catch (org.omg.CORBA.SystemException e) {
            throw e;
        } catch( Exception e ) {
            throw readWrapper.transNcListGotExc( e ) ;
        }
    }

    private synchronized void createbiPOA( ) {
        if( biPOA != null ) {
            return;
        }
        try {
            POA rootPOA = (POA) orb.resolve_initial_references(
                ORBConstants.ROOT_POA_NAME );
            rootPOA.the_POAManager().activate( );

            int i = 0;
            Policy[] poaPolicy = new Policy[3];
            poaPolicy[i++] = rootPOA.create_lifespan_policy(
                LifespanPolicyValue.TRANSIENT);
            poaPolicy[i++] = rootPOA.create_id_assignment_policy(
                IdAssignmentPolicyValue.SYSTEM_ID);
            poaPolicy[i++] = rootPOA.create_servant_retention_policy(
                ServantRetentionPolicyValue.RETAIN);
            biPOA = rootPOA.create_POA("BindingIteratorPOA", null, poaPolicy );
            biPOA.the_POAManager().activate( );
        } catch( Exception e ) {
            throw readWrapper.namingCtxBindingIteratorCreate( e ) ;
        }
    }



    public NamingContext NewContext() throws SystemException
    {
        try {
            return  theNameServiceHandle.NewContext( );
        } catch( org.omg.CORBA.SystemException e ) {
            throw e;
        } catch( Exception e ) {
            throw updateWrapper.transNcNewctxGotExc( e ) ;
        }
     }



    public void Destroy() throws SystemException
    {
        }


    public String to_string(org.omg.CosNaming.NameComponent[] n)
         throws org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if ( (n == null ) || (n.length == 0) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }

        String theStringifiedName = getINSImpl().convertToString( n );

        if( theStringifiedName == null )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }

        return theStringifiedName;
    }


    public org.omg.CosNaming.NameComponent[] to_name(String sn)
         throws org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if  ( (sn == null ) || (sn.length() == 0) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }
        org.omg.CosNaming.NameComponent[] theNameComponents =
                getINSImpl().convertToNameComponent( sn );
        if( ( theNameComponents == null ) || (theNameComponents.length == 0 ) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }
        for( int i = 0; i < theNameComponents.length; i++ ) {
            if ( ( ( theNameComponents[i].id  == null )
                 ||( theNameComponents[i].id.length() == 0 ) )
               &&( ( theNameComponents[i].kind == null )
                 ||( theNameComponents[i].kind.length() == 0 ) ) ) {
                throw new InvalidName();
            }
        }
        return theNameComponents;
    }



    public String to_url(String addr, String sn)
        throws org.omg.CosNaming.NamingContextExtPackage.InvalidAddress,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if  ( (sn == null ) || (sn.length() == 0) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }
        if( addr == null )
        {
                throw new org.omg.CosNaming.NamingContextExtPackage.InvalidAddress();
        }
        String urlBasedAddress = null;
        try {
            urlBasedAddress = getINSImpl().createURLBasedAddress( addr, sn );
        } catch (Exception e ) {
            urlBasedAddress = null;
        }
        try {
            INSURLHandler.getINSURLHandler().parseURL( urlBasedAddress );
        } catch( BAD_PARAM e ) {
            throw new
                org.omg.CosNaming.NamingContextExtPackage.InvalidAddress();
        }
        return urlBasedAddress;
    }


    public org.omg.CORBA.Object resolve_str(String sn)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        org.omg.CORBA.Object theObject = null;
        if  ( (sn == null ) || (sn.length() == 0) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }
        org.omg.CosNaming.NameComponent[] theNameComponents =
                getINSImpl().convertToNameComponent( sn );
        if( ( theNameComponents == null ) || (theNameComponents.length == 0 ) )
        {
                throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
        }
        theObject = resolve( theNameComponents );
        return theObject;
    }


    public boolean IsEmpty()
    {
        return this.theHashtable.isEmpty();
    }


    public void printSize( )
    {
        System.out.println( "Hashtable Size = " + theHashtable.size( ) );
        java.util.Enumeration e = theHashtable.keys( );
        for( ; e.hasMoreElements(); )
        {
              InternalBindingValue thevalue =
                        (InternalBindingValue) this.theHashtable.get(e.nextElement());
                if( thevalue != null )
                {
                        System.out.println( "value = " + thevalue.strObjectRef);
                }
        }
    }

}
