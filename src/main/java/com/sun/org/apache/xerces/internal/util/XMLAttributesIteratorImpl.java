

package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator ;
import java.util.NoSuchElementException;

import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl ;





public class XMLAttributesIteratorImpl extends XMLAttributesImpl implements Iterator {

    protected int fCurrent = 0 ;

    protected XMLAttributesImpl.Attribute fLastReturnedItem ;


    public XMLAttributesIteratorImpl() {
    }

    public boolean hasNext() {
        return fCurrent < getLength() ? true : false ;
    }public Object next() {
        if(hasNext()){
            return fLastReturnedItem = fAttributes[fCurrent++] ;
        }
        else{
            throw new NoSuchElementException() ;
        }
    }public void remove() {
        if(fLastReturnedItem == fAttributes[fCurrent - 1]){
            removeAttributeAt(fCurrent--) ;
        }
        else {
            throw new IllegalStateException();
        }
    }public void removeAllAttributes() {
        super.removeAllAttributes() ;
        fCurrent = 0 ;
    }



}
