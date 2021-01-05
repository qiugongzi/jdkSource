

package com.sun.corba.se.impl.presentation.rmi ;

import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Proxy ;
import java.lang.reflect.Method ;

import org.omg.CORBA.portable.ObjectImpl ;

import java.io.ObjectStreamException ;
import java.io.Serializable ;

import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator ;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub ;

import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler ;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory ;
import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl ;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler ;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl ;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory
{
    private final PresentationManager.ClassData classData ;
    private final PresentationManager pm ;
    private Class[] proxyInterfaces ;

    public InvocationHandlerFactoryImpl( PresentationManager pm,
        PresentationManager.ClassData classData )
    {
        this.classData = classData ;
        this.pm = pm ;

        Class[] remoteInterfaces =
            classData.getIDLNameTranslator().getInterfaces() ;
        proxyInterfaces = new Class[ remoteInterfaces.length + 1 ] ;
        for (int ctr=0; ctr<remoteInterfaces.length; ctr++)
            proxyInterfaces[ctr] = remoteInterfaces[ctr] ;

        proxyInterfaces[remoteInterfaces.length] = DynamicStub.class ;
    }

    private class CustomCompositeInvocationHandlerImpl extends
        CompositeInvocationHandlerImpl implements LinkedInvocationHandler,
        Serializable
    {
        private transient DynamicStub stub ;

        public void setProxy( Proxy proxy )
        {
            ((DynamicStubImpl)stub).setSelf( (DynamicStub)proxy ) ;
        }

        public Proxy getProxy()
        {
            return (Proxy)((DynamicStubImpl)stub).getSelf() ;
        }

        public CustomCompositeInvocationHandlerImpl( DynamicStub stub )
        {
            this.stub = stub ;
        }


        public Object writeReplace() throws ObjectStreamException
        {
            return stub ;
        }
    }

    public InvocationHandler getInvocationHandler()
    {
        final DynamicStub stub = new DynamicStubImpl(
            classData.getTypeIds() ) ;

        return getInvocationHandler( stub ) ;
    }

    InvocationHandler getInvocationHandler( DynamicStub stub )
    {
        final InvocationHandler dynamicStubHandler =
            DelegateInvocationHandlerImpl.create( stub ) ;

        final InvocationHandler stubMethodHandler = new StubInvocationHandlerImpl(
            pm, classData, stub ) ;

        final CompositeInvocationHandler handler =
            new CustomCompositeInvocationHandlerImpl( stub ) ;

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
        handler.addInvocationHandler( DynamicStub.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( org.omg.CORBA.Object.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( Object.class,
            dynamicStubHandler ) ;
                return null;
            }
        });


        handler.setDefaultHandler( stubMethodHandler ) ;

        return handler ;
    }

    public Class[] getProxyInterfaces()
    {
        return proxyInterfaces ;
    }
}
