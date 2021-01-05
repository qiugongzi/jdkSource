

package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;

import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableServer.POA;


public interface NamingContextDataStore {

    void Bind(NameComponent n, org.omg.CORBA.Object obj, BindingType bt)
        throws org.omg.CORBA.SystemException;


    org.omg.CORBA.Object Resolve(NameComponent n,BindingTypeHolder bth)
        throws org.omg.CORBA.SystemException;


    org.omg.CORBA.Object Unbind(NameComponent n)
        throws org.omg.CORBA.SystemException;


    void List(int how_many, BindingListHolder bl, BindingIteratorHolder bi)
        throws org.omg.CORBA.SystemException;


    NamingContext NewContext()
        throws org.omg.CORBA.SystemException;


    void Destroy()
        throws org.omg.CORBA.SystemException;


    boolean IsEmpty();

    POA getNSPOA( );
}
