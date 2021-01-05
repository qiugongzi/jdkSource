

package com.sun.jmx.snmp.agent;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;




public abstract class SnmpMibGroup extends SnmpMibOid
    implements Serializable {

    protected Hashtable<Long, Long> subgroups = null;


    public abstract boolean      isTable(long arc);


    public abstract boolean      isVariable(long arc);


    public abstract boolean      isReadable(long arc);



    public abstract SnmpMibTable getTable(long arc);


    public void validateVarId(long arc, Object userData)
        throws SnmpStatusException {
        if (isVariable(arc) == false) {
            throw new SnmpStatusException(SnmpStatusException.noSuchObject);
        }
    }


    public boolean isNestedArc(long arc) {
        if (subgroups == null) return false;
        Object obj = subgroups.get(new Long(arc));
        return (obj != null);
    }


    @Override
    abstract public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    @Override
    abstract public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    @Override
    abstract public void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;

    @Override
    public void getRootOid(Vector<Integer> result) {
    }

    void registerNestedArc(long arc) {
        Long obj = new Long(arc);
        if (subgroups == null) subgroups = new Hashtable<>();
        subgroups.put(obj,obj);
    }

    protected void registerObject(long arc)
        throws IllegalAccessException {

        long[] oid = new long[1];
        oid[0] = arc;
        super.registerNode(oid,0,null);
    }

    @Override
    void registerNode(long[] oid, int cursor ,SnmpMibNode node)
        throws IllegalAccessException {
        super.registerNode(oid,cursor,node);
        if (cursor < 0) return;
        if (cursor >= oid.length) return;
        registerNestedArc(oid[cursor]);
    }

    @Override
    void findHandlingNode(SnmpVarBind varbind,
                          long[] oid, int depth,
                          SnmpRequestTree handlers)
        throws SnmpStatusException {

        int length = oid.length;

        if (handlers == null)
            throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);

        final Object data = handlers.getUserData();

        if (depth >= length) {
            throw new SnmpStatusException(SnmpStatusException.noAccess);
        }

        long arc = oid[depth];

        if (isNestedArc(arc)) {
            super.findHandlingNode(varbind,oid,depth,handlers);
        } else if (isTable(arc)) {
            SnmpMibTable table = getTable(arc);

            table.findHandlingNode(varbind,oid,depth+1,handlers);

        } else {
            validateVarId(arc, data);

            if (depth+2 > length) {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            }

            if (depth+2 < length) {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            }

            if (oid[depth+1] != 0L) {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            }

            handlers.add(this,depth,varbind);
        }
    }

    @Override
    long[] findNextHandlingNode(SnmpVarBind varbind,
                                long[] oid, int pos, int depth,
                                SnmpRequestTree handlers, AcmChecker checker)
        throws SnmpStatusException {

        int length = oid.length;
        SnmpMibNode node = null;

        if (handlers == null) {
            throw new SnmpStatusException(SnmpStatusException.noSuchObject);
        }

        final Object data = handlers.getUserData();
        final int pduVersion = handlers.getRequestPduVersion();


        if (pos >= length)
            return super.findNextHandlingNode(varbind,oid,pos,depth,
                                              handlers, checker);

        long arc = oid[pos];

        long[] result = null;

        try {

            if (isTable(arc)) {
                SnmpMibTable table = getTable(arc);

                checker.add(depth, arc);
                try {
                    result = table.findNextHandlingNode(varbind,oid,pos+1,
                                                        depth+1,handlers,
                                                        checker);
                }catch(SnmpStatusException ex) {
                    throw new SnmpStatusException(SnmpStatusException.noSuchObject);
                } finally {
                    checker.remove(depth);
                }
                result[depth] = arc;
                return result;
            } else if (isReadable(arc)) {
                if (pos == (length - 1)) {
                    result = new long[depth+2];
                    result[depth+1] = 0L;
                    result[depth] = arc;

                    checker.add(depth, result, depth, 2);
                    try {
                        checker.checkCurrentOid();
                    } catch(SnmpStatusException e) {
                        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
                    } finally {
                        checker.remove(depth,2);
                    }

                    handlers.add(this,depth,varbind);
                    return result;
                }

                } else if (isNestedArc(arc)) {
                final SnmpMibNode child = getChild(arc);

                if (child != null) {
                    checker.add(depth, arc);
                    try {
                        result = child.findNextHandlingNode(varbind,oid,pos+1,
                                                            depth+1,handlers,
                                                            checker);
                        result[depth] = arc;
                        return result;
                    } finally {
                        checker.remove(depth);
                    }
                }
            }

            throw new SnmpStatusException(SnmpStatusException.noSuchObject);

        } catch (SnmpStatusException e) {
            long[] newOid = new long[1];
            newOid[0] = getNextVarId(arc,data,pduVersion);
            return findNextHandlingNode(varbind,newOid,0,depth,
                                        handlers,checker);
        }
    }

}
