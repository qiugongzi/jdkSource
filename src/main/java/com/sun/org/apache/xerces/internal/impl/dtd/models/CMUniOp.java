


package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLContentSpec;


public class CMUniOp extends CMNode
{
    public CMUniOp(int type, CMNode childNode)
    {
        super(type);

        if ((type() != XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE)
        &&  (type() != XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE)
        &&  (type() != XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE))
        {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }

        fChild = childNode;
    }


    final CMNode getChild()
    {
        return fChild;
    }


    public boolean isNullable()
    {
        if (type() == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE)
            return fChild.isNullable();
        else
            return true;
    }


    protected void calcFirstPos(CMStateSet toSet)
    {
        toSet.setTo(fChild.firstPos());
    }

    protected void calcLastPos(CMStateSet toSet)
    {
        toSet.setTo(fChild.lastPos());
    }


    private CMNode  fChild;
};
