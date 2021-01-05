


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;


public class XSAttributeUseImpl implements XSAttributeUse {

    public XSAttributeDecl fAttrDecl = null;
    public short fUse = SchemaSymbols.USE_OPTIONAL;
    public short fConstraintType = XSConstants.VC_NONE;
    public ValidatedInfo fDefault = null;
    public XSObjectList fAnnotations = null;

    public void reset(){
        fDefault = null;
        fAttrDecl = null;
        fUse = SchemaSymbols.USE_OPTIONAL;
        fConstraintType = XSConstants.VC_NONE;
        fAnnotations = null;
    }


    public short getType() {
        return XSConstants.ATTRIBUTE_USE;
    }


    public String getName() {
        return null;
    }


    public String getNamespace() {
        return null;
    }


    public boolean getRequired() {
        return fUse == SchemaSymbols.USE_REQUIRED;
    }


    public XSAttributeDeclaration getAttrDeclaration() {
        return fAttrDecl;
    }


    public short getConstraintType() {
        return fConstraintType;
    }


    public String getConstraintValue() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.stringValue();
    }


    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    public Object getActualVC() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.actualValue;
    }

    public short getActualVCType() {
        return getConstraintType() == XSConstants.VC_NONE ?
               XSConstants.UNAVAILABLE_DT :
               fDefault.actualValueType;
    }

    public ShortList getItemValueTypes() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.itemValueTypes;
    }


    public XSObjectList getAnnotations() {
        return (fAnnotations != null) ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

}