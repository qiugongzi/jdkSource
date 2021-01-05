

package com.sun.corba.se.impl.resolver ;

import com.sun.corba.se.spi.resolver.Resolver ;

import com.sun.corba.se.spi.orb.Operation ;

public class ORBDefaultInitRefResolverImpl implements Resolver {
    Operation urlHandler ;
    String orbDefaultInitRef ;

    public ORBDefaultInitRefResolverImpl( Operation urlHandler, String orbDefaultInitRef )
    {
        this.urlHandler = urlHandler ;


        this.orbDefaultInitRef = orbDefaultInitRef ;
    }

    public org.omg.CORBA.Object resolve( String ident )
    {

        if( orbDefaultInitRef == null ) {
            return null;
        }

        String urlString;




        if( orbDefaultInitRef.startsWith( "corbaloc:" ) ) {
            urlString = orbDefaultInitRef + "/" + ident;
        } else {
            urlString = orbDefaultInitRef + "#" + ident;
        }

        return (org.omg.CORBA.Object)urlHandler.operate( urlString ) ;
    }

    public java.util.Set list()
    {
        return new java.util.HashSet() ;
    }
}
