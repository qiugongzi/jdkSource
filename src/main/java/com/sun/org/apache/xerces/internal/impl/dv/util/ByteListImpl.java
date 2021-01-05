

package com.sun.org.apache.xerces.internal.impl.dv.util;

import java.util.AbstractList;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;


public class ByteListImpl extends AbstractList implements ByteList {

    protected final byte[] data;

    protected String canonical;

    public ByteListImpl(byte[] data) {
        this.data = data;
    }


    public int getLength() {
        return data.length;
    }


    public boolean contains(byte item) {
        for (int i = 0; i < data.length; ++i) {
            if (data[i] == item) {
                return true;
            }
        }
        return false;
    }


    public byte item(int index)
        throws XSException {

        if(index < 0 || index > data.length - 1) {
            throw new XSException(XSException.INDEX_SIZE_ERR, null);
        }
        return data[index];
    }



    public Object get(int index) {
        if (index >= 0 && index < data.length) {
            return new Byte(data[index]);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }
}
