

package com.sun.jmx.snmp.agent;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.Vector;

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpUnknownModelException;
import com.sun.jmx.snmp.internal.SnmpAccessControlModel;
import com.sun.jmx.snmp.internal.SnmpEngineImpl;


final class LongList {

    public static int DEFAULT_CAPACITY = 10;

    public static int DEFAULT_INCREMENT = 10;


    private final int DELTA;
    private int size;


    public  long[] list;

    LongList() {
        this(DEFAULT_CAPACITY,DEFAULT_INCREMENT);
    }

    LongList(int initialCapacity) {
        this(initialCapacity,DEFAULT_INCREMENT);
    }

    LongList(int initialCapacity, int delta) {
        size = 0;
        DELTA = delta;
        list = allocate(initialCapacity);
    }


    public final int size() { return size;}


    public final boolean add(final long o) {
        if (size >= list.length)
            resize();
        list[size++]=o;
        return true;
    }


    public final void add(final int index, final long o) {
        if (index >  size) throw new IndexOutOfBoundsException();
        if (index >= list.length) resize();
        if (index == size) {
            list[size++]=o;
            return;
        }

        java.lang.System.arraycopy(list,index,list,index+1,size-index);
        list[index]=o;
        size++;
    }


    public final void add(final int at,final long[] src, final int from,
                          final int count) {
        if (count <= 0) return;
        if (at > size) throw new IndexOutOfBoundsException();
        ensure(size+count);
        if (at < size) {
            java.lang.System.arraycopy(list,at,list,at+count,size-at);
        }
        java.lang.System.arraycopy(src,from,list,at,count);
        size+=count;
    }


    public final long remove(final int from, final int count) {
        if (count < 1 || from < 0) return -1;
        if (from+count > size) return -1;

        final long o = list[from];
        final int oldsize = size;
        size = size - count;

        if (from == size) return o;

        java.lang.System.arraycopy(list,from+count,list,from,
                                   size-from);
        return o;
    }


    public final long remove(final int index) {
        if (index >= size) return -1;
        final long o = list[index];
        list[index]=0;
        if (index == --size) return o;

        java.lang.System.arraycopy(list,index+1,list,index,
                                   size-index);
        return o;
    }


    public final long[] toArray(long[] a) {
        java.lang.System.arraycopy(list,0,a,0,size);
        return a;
    }


    public final long[] toArray() {
        return toArray(new long[size]);
    }


    private final void resize() {
        final long[] newlist = allocate(list.length + DELTA);
        java.lang.System.arraycopy(list,0,newlist,0,size);
        list = newlist;
    }


    private final void ensure(int length) {
        if (list.length < length) {
            final int min = list.length+DELTA;
            length=(length<min)?min:length;
            final long[] newlist = allocate(length);
            java.lang.System.arraycopy(list,0,newlist,0,size);
            list = newlist;
        }
    }


    private final long[] allocate(final int length) {
        return new long[length];
    }

}
