


package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;

import com.sun.org.apache.xerces.internal.xs.LSInputList;
import org.w3c.dom.ls.LSInput;


public final class LSInputListImpl extends AbstractList implements LSInputList {


    public static final LSInputListImpl EMPTY_LIST = new LSInputListImpl(new LSInput[0], 0);

    private final LSInput[] fArray;
    private final int fLength;


    public LSInputListImpl(LSInput[] array, int length) {
        fArray = array;
        fLength = length;
    }


    public int getLength() {
        return fLength;
    }


    public LSInput item(int index) {
        if (index < 0 || index >= fLength) {
            return null;
        }
        return fArray[index];
    }



    public Object get(int index) {
        if (index >= 0 && index < fLength) {
            return fArray[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }

    public Object[] toArray() {
        Object[] a = new Object[fLength];
        toArray0(a);
        return a;
    }

    public Object[] toArray(Object[] a) {
        if (a.length < fLength) {
            Class arrayClass = a.getClass();
            Class componentType = arrayClass.getComponentType();
            a = (Object[]) Array.newInstance(componentType, fLength);
        }
        toArray0(a);
        if (a.length > fLength) {
            a[fLength] = null;
        }
        return a;
    }

    private void toArray0(Object[] a) {
        if (fLength > 0) {
            System.arraycopy(fArray, 0, a, 0, fLength);
        }
    }

}