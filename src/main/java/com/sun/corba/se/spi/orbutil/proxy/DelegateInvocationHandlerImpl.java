

package com.sun.corba.se.spi.orbutil.proxy ;

import java.io.Serializable ;

import java.util.Map ;
import java.util.LinkedHashMap ;

import java.lang.reflect.Proxy ;
import java.lang.reflect.Method ;
import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.InvocationTargetException ;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission ;

public abstract class DelegateInvocationHandlerImpl
{
    private DelegateInvocationHandlerImpl() {}

    public static InvocationHandler create( final Object delegate )
    {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(new DynamicAccessPermission("access"));
        }
        return new InvocationHandler() {
            public Object invoke( Object proxy, Method method, Object[] args )
                throws Throwable
            {


                try {
                    return method.invoke( delegate, args ) ;
                } catch (InvocationTargetException ite) {


                    throw ite.getCause() ;
                }
            }
        } ;
    }
}
