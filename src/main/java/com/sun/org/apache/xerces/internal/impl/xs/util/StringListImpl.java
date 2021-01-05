


package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.xs.StringList;


public final class StringListImpl extends AbstractList implements StringList {


    public static final StringListImpl EMPTY_LIST = new StringListImpl(new String[0], 0);

    private final String[] fArray;
    private final int fLength;

    private final Vector fVector;

    public StringListImpl(Vector v) {
        fVector = v;
        fLength = (v == null) ? 0 : v.size();
        fArray = null;
    }


    public StringListImpl(String[] array, int length) {
        fArray = array;
        fLength = length;
        fVector = null;
    }


    public int getLength() {
        return fLength;
    }


    public boolean contains(String item) {
        if (fVector != null) {
            return fVector.contains(item);
        }
        if (item == null) {
            for (int i = 0; i < fLength; i++) {
                if (fArray[i] == null)
                    return true;
            }
        }
        else {
            for (int i = 0; i < fLength; i++) {
                if (item.equals(fArray[i]))
                    return true;
            }
        }
        return false;
    }

    public String item(int index) {
        if (index < 0 || index >= fLength) {
            return null;
        }
        if (fVector != null) {
            return (String)fVector.elementAt(index);
        }
        return fArray[index];
    }



    public Object get(int index) {
        if (index >= 0 && index < fLength) {
            if (fVector != null) {
                return fVector.elementAt(index);
            }
            return fArray[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }

    public Object[] toArray() {
        if (fVector != null) {
            return fVector.toArray();
        }
        Object[] a = new Object[fLength];
        toArray0(a);
        return a;
    }

    public Object[] toArray(Object[] a) {
        if (fVector != null) {
            return fVector.toArray(a);
        }
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