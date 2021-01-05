

package com.sun.corba.se.impl.orb ;

import org.omg.CORBA.INITIALIZE ;

import java.util.Properties ;
import java.util.List ;
import java.util.LinkedList ;
import java.util.Iterator ;

import java.lang.reflect.Array ;

import com.sun.corba.se.spi.orb.Operation ;
import com.sun.corba.se.spi.orb.StringPair ;
import com.sun.corba.se.spi.logging.CORBALogDomains ;

import com.sun.corba.se.impl.orbutil.ObjectUtility ;
import com.sun.corba.se.impl.logging.ORBUtilSystemException ;

public class PrefixParserAction extends ParserActionBase {
    private Class componentType ;
    private ORBUtilSystemException wrapper ;

    public PrefixParserAction( String propertyName,
        Operation operation, String fieldName, Class componentType )
    {
        super( propertyName, true, operation, fieldName ) ;
        this.componentType = componentType ;
        this.wrapper = ORBUtilSystemException.get(
            CORBALogDomains.ORB_LIFECYCLE ) ;
    }


    public Object apply( Properties props )
    {
        String prefix = getPropertyName() ;
        int prefixLength = prefix.length() ;
        if (prefix.charAt( prefixLength - 1 ) != '.') {
            prefix += '.' ;
            prefixLength++ ;
        }

        List matches = new LinkedList() ;

        Iterator iter = props.keySet().iterator() ;
        while (iter.hasNext()) {
            String key = (String)(iter.next()) ;
            if (key.startsWith( prefix )) {
                String suffix = key.substring( prefixLength ) ;
                String value = props.getProperty( key ) ;
                StringPair data = new StringPair( suffix, value ) ;
                Object result = getOperation().operate( data ) ;
                matches.add( result ) ;
            }
        }

        int size = matches.size() ;
        if (size > 0) {
            Object result = null ;
            try {
                result = Array.newInstance( componentType, size ) ;
            } catch (Throwable thr) {
                throw wrapper.couldNotCreateArray( thr,
                    getPropertyName(), componentType,
                    new Integer( size ) ) ;
            }

            Iterator iter2 = matches.iterator() ;
            int ctr = 0 ;
            while (iter2.hasNext()) {
                Object obj = iter2.next() ;

                try {
                    Array.set( result, ctr, obj ) ;
                } catch (Throwable thr) {
                    throw wrapper.couldNotSetArray( thr,
                        getPropertyName(), new Integer(ctr),
                        componentType, new Integer(size),
                        obj.toString() ) ;
                }
                ctr++ ;
            }

            return result ;
        } else
            return null ;
    }
}
