


package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementation;
import java.util.Vector;


public class DOMXSImplementationSourceImpl
    extends DOMImplementationSourceImpl {


    public DOMImplementation getDOMImplementation(String features) {
        DOMImplementation impl = super.getDOMImplementation(features);
        if (impl != null){
            return impl;
        }
        impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }
        impl = XSImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }

        return null;
    }


    public DOMImplementationList getDOMImplementationList(String features) {
        final Vector implementations = new Vector();

        DOMImplementationList list = super.getDOMImplementationList(features);
        for (int i=0; i < list.getLength(); i++ ) {
            implementations.addElement(list.item(i));
        }

        DOMImplementation impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            implementations.addElement(impl);
        }

        impl = XSImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            implementations.addElement(impl);
        }
        return new DOMImplementationListImpl(implementations);
    }
}
