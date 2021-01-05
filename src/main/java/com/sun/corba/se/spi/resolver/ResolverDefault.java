

package com.sun.corba.se.spi.resolver ;

import java.io.File ;

import com.sun.corba.se.impl.resolver.LocalResolverImpl ;
import com.sun.corba.se.impl.resolver.ORBInitRefResolverImpl ;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl ;
import com.sun.corba.se.impl.resolver.BootstrapResolverImpl ;
import com.sun.corba.se.impl.resolver.CompositeResolverImpl ;
import com.sun.corba.se.impl.resolver.INSURLOperationImpl ;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl ;
import com.sun.corba.se.impl.resolver.FileResolverImpl ;

import com.sun.corba.se.spi.orb.ORB ;
import com.sun.corba.se.spi.orb.Operation ;
import com.sun.corba.se.spi.orb.StringPair ;


public class ResolverDefault {

    public static LocalResolver makeLocalResolver( )
    {
        return new LocalResolverImpl() ;
    }


    public static Resolver makeORBInitRefResolver( Operation urlOperation,
        StringPair[] initRefs )
    {
        return new ORBInitRefResolverImpl( urlOperation, initRefs ) ;
    }

    public static Resolver makeORBDefaultInitRefResolver( Operation urlOperation,
        String defaultInitRef )
    {
        return new ORBDefaultInitRefResolverImpl( urlOperation,
            defaultInitRef ) ;
    }


    public static Resolver makeBootstrapResolver( ORB orb, String host, int port )
    {
        return new BootstrapResolverImpl( orb, host, port ) ;
    }


    public static Resolver makeCompositeResolver( Resolver first, Resolver second )
    {
        return new CompositeResolverImpl( first, second ) ;
    }

    public static Operation makeINSURLOperation( ORB orb, Resolver bootstrapResolver )
    {
        return new INSURLOperationImpl(
            (com.sun.corba.se.spi.orb.ORB)orb, bootstrapResolver ) ;
    }

    public static LocalResolver makeSplitLocalResolver( Resolver resolver,
        LocalResolver localResolver )
    {
        return new SplitLocalResolverImpl( resolver, localResolver ) ;
    }

    public static Resolver makeFileResolver( ORB orb, File file )
    {
        return new FileResolverImpl( orb, file ) ;
    }
}
