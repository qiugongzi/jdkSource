


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.XSConstants;


public class AttributePSVImpl implements AttributePSVI {


    protected XSAttributeDeclaration fDeclaration = null;


    protected XSTypeDefinition fTypeDecl = null;


    protected boolean fSpecified = false;


    protected String fNormalizedValue = null;


    protected Object fActualValue = null;


    protected short fActualValueType = XSConstants.UNAVAILABLE_DT;


    protected ShortList fItemValueTypes = null;


    protected XSSimpleTypeDefinition fMemberType = null;


    protected short fValidationAttempted = AttributePSVI.VALIDATION_NONE;


    protected short fValidity = AttributePSVI.VALIDITY_NOTKNOWN;


    protected String[] fErrorCodes = null;


    protected String fValidationContext = null;

    public String getSchemaDefault() {
        return fDeclaration == null ? null : fDeclaration.getConstraintValue();
    }


    public String getSchemaNormalizedValue() {
        return fNormalizedValue;
    }


    public boolean getIsSchemaSpecified() {
        return fSpecified;
    }



    public short getValidationAttempted() {
        return fValidationAttempted;
    }


    public short getValidity() {
        return fValidity;
    }


    public StringList getErrorCodes() {
        if (fErrorCodes == null)
            return null;
        return new StringListImpl(fErrorCodes, fErrorCodes.length);
    }

    public String getValidationContext() {
        return fValidationContext;
    }


    public XSTypeDefinition getTypeDefinition() {
        return fTypeDecl;
    }


    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return fMemberType;
    }


    public XSAttributeDeclaration getAttributeDeclaration() {
        return fDeclaration;
    }


    public Object getActualNormalizedValue() {
        return this.fActualValue;
    }


    public short getActualNormalizedValueType() {
        return this.fActualValueType;
    }


    public ShortList getItemValueTypes() {
        return this.fItemValueTypes;
    }


    public void reset() {
        fNormalizedValue = null;
        fActualValue = null;
        fActualValueType = XSConstants.UNAVAILABLE_DT;
        fItemValueTypes = null;
        fDeclaration = null;
        fTypeDecl = null;
        fSpecified = false;
        fMemberType = null;
        fValidationAttempted = AttributePSVI.VALIDATION_NONE;
        fValidity = AttributePSVI.VALIDITY_NOTKNOWN;
        fErrorCodes = null;
        fValidationContext = null;
    }
}
