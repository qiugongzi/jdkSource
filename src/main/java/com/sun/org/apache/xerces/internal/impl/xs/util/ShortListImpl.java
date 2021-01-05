


package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.util.AbstractList;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSException;


public final class ShortListImpl extends AbstractList implements ShortList {


    public static final ShortListImpl EMPTY_LIST = new ShortListImpl(new short[0], 0);

    private final short[] fArray;
    private final int fLength;


    public ShortListImpl(short[] array, int length) {
        fArray = array;
        fLength = length;
    }


    public int getLength() {
        return fLength;
    }


    public boolean contains(short item) {
        for (int i = 0; i < fLength; i++) {
            if (fArray[i] == item) {
                return true;
            }
        }
        return false;
    }

    public short item(int index) throws XSException {
        if (index < 0 || index >= fLength) {
            throw new XSException(XSException.INDEX_SIZE_ERR, null);
        }
        return fArray[index];
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ShortList)) {
            return false;
        }
        ShortList rhs = (ShortList)obj;

        if (fLength != rhs.getLength()) {
            return false;
        }
        for (int i = 0;i < fLength; ++i) {
            if (fArray[i] != rhs.item(i)) {
                return false;
            }
        }
        return true;
    }



    public Object get(int index) {
        if (index >= 0 && index < fLength) {
            return new Short(fArray[index]);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }

}