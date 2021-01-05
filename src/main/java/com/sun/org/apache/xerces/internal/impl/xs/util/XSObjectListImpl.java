


package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;


public class XSObjectListImpl extends AbstractList implements XSObjectList {


    public static final XSObjectListImpl EMPTY_LIST = new XSObjectListImpl(new XSObject[0], 0);
    private static final ListIterator EMPTY_ITERATOR = new ListIterator() {
        public boolean hasNext() {
            return false;
        }
        public Object next() {
            throw new NoSuchElementException();
        }
        public boolean hasPrevious() {
            return false;
        }
        public Object previous() {
            throw new NoSuchElementException();
        }
        public int nextIndex() {
            return 0;
        }
        public int previousIndex() {
            return -1;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public void set(Object object) {
            throw new UnsupportedOperationException();
        }
        public void add(Object object) {
            throw new UnsupportedOperationException();
        }
    };

    private static final int DEFAULT_SIZE = 4;

    private XSObject[] fArray = null;
    private int fLength = 0;

    public XSObjectListImpl() {
        fArray = new XSObject[DEFAULT_SIZE];
        fLength = 0;
    }


    public XSObjectListImpl(XSObject[] array, int length) {
        fArray = array;
        fLength = length;
    }


    public int getLength() {
        return fLength;
    }


    public XSObject item(int index) {
        if (index < 0 || index >= fLength) {
            return null;
        }
        return fArray[index];
    }

    public void clearXSObjectList() {
        for (int i=0; i<fLength; i++) {
            fArray[i] = null;
        }
        fArray = null;
        fLength = 0;
    }

    public void addXSObject(XSObject object) {
       if (fLength == fArray.length) {
           XSObject[] temp = new XSObject[fLength + 4];
           System.arraycopy(fArray, 0, temp, 0, fLength);
           fArray = temp;
       }
       fArray[fLength++] = object;
    }

    public void addXSObject(int index, XSObject object) {
        fArray[index] = object;
    }



    public boolean contains(Object value) {
        return (value == null) ? containsNull() : containsObject(value);
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

    public Iterator iterator() {
        return listIterator0(0);
    }

    public ListIterator listIterator() {
        return listIterator0(0);
    }

    public ListIterator listIterator(int index) {
        if (index >= 0 && index < fLength) {
            return listIterator0(index);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    private ListIterator listIterator0(int index) {
        return fLength == 0 ? EMPTY_ITERATOR : new XSObjectListIterator(index);
    }

    private boolean containsObject(Object value) {
        for (int i = fLength - 1; i >= 0; --i) {
            if (value.equals(fArray[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNull() {
        for (int i = fLength - 1; i >= 0; --i) {
            if (fArray[i] == null) {
                return true;
            }
        }
        return false;
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

    private final class XSObjectListIterator implements ListIterator {
        private int index;
        public XSObjectListIterator(int index) {
            this.index = index;
        }
        public boolean hasNext() {
            return (index < fLength);
        }
        public Object next() {
            if (index < fLength) {
                return fArray[index++];
            }
            throw new NoSuchElementException();
        }
        public boolean hasPrevious() {
            return (index > 0);
        }
        public Object previous() {
            if (index > 0) {
                return fArray[--index];
            }
            throw new NoSuchElementException();
        }
        public int nextIndex() {
            return index;
        }
        public int previousIndex() {
            return index - 1;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public void set(Object o) {
            throw new UnsupportedOperationException();
        }
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }
    }

}