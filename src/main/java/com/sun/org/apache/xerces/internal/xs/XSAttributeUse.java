


package com.sun.org.apache.xerces.internal.xs;


public interface XSAttributeUse extends XSObject {

    public boolean getRequired();


    public XSAttributeDeclaration getAttrDeclaration();


    public short getConstraintType();


    public String getConstraintValue();


    public Object getActualVC()
                                       throws XSException;


    public short getActualVCType()
                                       throws XSException;


    public ShortList getItemValueTypes()
                                       throws XSException;


    public XSObjectList getAnnotations();
}
