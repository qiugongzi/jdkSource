


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;


public class XSCMLeaf
    extends CMNode {

    private Object fLeaf = null;


    private int fParticleId = -1;


    private int fPosition = -1;

    public XSCMLeaf(int type, Object leaf, int id, int position)  {
        super(type);

        fLeaf = leaf;
        fParticleId = id;
        fPosition = position;
    }

    final Object getLeaf() {
        return fLeaf;
    }

    final int getParticleId() {
        return fParticleId;
    }

    final int getPosition() {
        return fPosition;
    }

    final void setPosition(int newPosition) {
        fPosition = newPosition;
    }

    public boolean isNullable() {
        return (fPosition == -1);
    }

    public String toString() {
        StringBuffer strRet = new StringBuffer(fLeaf.toString());
        if (fPosition >= 0) {
            strRet.append
            (
                " (Pos:"
                + Integer.toString(fPosition)
                + ")"
            );
        }
        return strRet.toString();
    }

    protected void calcFirstPos(CMStateSet toSet) {
        if (fPosition == -1)
            toSet.zeroBits();

        else
            toSet.setBit(fPosition);
    }

    protected void calcLastPos(CMStateSet toSet) {
        if (fPosition == -1)
            toSet.zeroBits();

        else
            toSet.setBit(fPosition);
    }

}