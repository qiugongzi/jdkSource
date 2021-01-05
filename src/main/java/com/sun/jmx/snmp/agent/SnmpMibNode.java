


package com.sun.jmx.snmp.agent;



import java.io.Serializable;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;



public abstract class SnmpMibNode implements Serializable {

    public long getNextVarId(long id, Object userData)
        throws SnmpStatusException {
        return getNextIdentifier(varList,id);
    }


    public long getNextVarId(long id, Object userData, int pduVersion)
        throws SnmpStatusException {
        long varid=id;
        do {
            varid = getNextVarId(varid,userData);
        } while (skipVariable(varid,userData,pduVersion));

        return varid;
    }


    protected boolean skipVariable(long id, Object userData, int pduVersion) {
        return false;
    }


    void findHandlingNode(SnmpVarBind varbind,
                          long[] oid, int depth,
                          SnmpRequestTree handlers)
        throws SnmpStatusException {
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }


    long[] findNextHandlingNode(SnmpVarBind varbind,
                                 long[] oid, int pos, int depth,
                                 SnmpRequestTree handlers, AcmChecker checker)
        throws SnmpStatusException {
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }


    public abstract void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    public abstract void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    public abstract void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    static public void sort(int array[]) {
        QuickSort(array, 0, array.length - 1);
    }


    public void getRootOid(Vector<Integer> result) {
        return;
    }

    static void QuickSort(int a[], int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        int mid;

        if ( hi0 > lo0) {


            mid = a[ ( lo0 + hi0 ) / 2 ];

            while( lo <= hi ) {

                while( ( lo < hi0 )  && ( a[lo] < mid ))
                    ++lo;


                while( ( hi > lo0 ) && ( a[hi] > mid ))
                    --hi;

                if( lo <= hi ) {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }


            if( lo0 < hi )
                QuickSort( a, lo0, hi );


            if( lo < hi0 )
                QuickSort( a, lo, hi0 );

        }
    }

    final static protected int getNextIdentifier(int table[], long value)
        throws SnmpStatusException {

        final int[] a = table;
        final int val= (int) value;

        if (a == null) {
            throw new SnmpStatusException(SnmpStatusException.noSuchObject);
        }

        int low= 0;
        int max= a.length;
        int curr= low + (max-low)/2;
        int elmt= 0;

        if (max < 1) {
            throw new SnmpStatusException(SnmpStatusException.noSuchObject);
        }

        if (a[max-1] <= val) {
            throw new SnmpStatusException(SnmpStatusException.noSuchObject);
        }

        while (low <= max) {
            elmt= a[curr];
            if (val == elmt) {
                curr++;
                return a[curr];
            }
            if (elmt < val) {
                low= curr +1;
            } else {
                max= curr -1;
            }
            curr= low + (max-low)/2;
        }
        return a[curr];
    }


    final static private void swap(int a[], int i, int j) {
        int T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;
    }

    protected int[] varList;
}
