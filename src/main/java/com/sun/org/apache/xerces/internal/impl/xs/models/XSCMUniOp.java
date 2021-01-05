


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;


public class XSCMUniOp extends CMNode {
    public XSCMUniOp(int type, CMNode childNode) {
        super(type);

        if ((type() != XSParticleDecl.PARTICLE_ZERO_OR_ONE)
        &&  (type() != XSParticleDecl.PARTICLE_ZERO_OR_MORE)
        &&  (type() != XSParticleDecl.PARTICLE_ONE_OR_MORE)) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }

        fChild = childNode;
    }


    final CMNode getChild() {
        return fChild;
    }


    public boolean isNullable() {
        if (type() == XSParticleDecl.PARTICLE_ONE_OR_MORE)
                return fChild.isNullable();
            else
                return true;
    }


    protected void calcFirstPos(CMStateSet toSet) {
        toSet.setTo(fChild.firstPos());
    }

    protected void calcLastPos(CMStateSet toSet) {
        toSet.setTo(fChild.lastPos());
    }


    @Override
    public void setUserData(Object userData) {
        super.setUserData(userData);
        fChild.setUserData(userData);
    }


    private CMNode  fChild;
}