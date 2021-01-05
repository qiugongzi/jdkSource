


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSConstants;


public class ElementPSVImpl implements ElementPSVI {


    protected XSElementDeclaration fDeclaration = null;


    protected XSTypeDefinition fTypeDecl = null;


    protected boolean fNil = false;


    protected boolean fSpecified = false;


    protected String fNormalizedValue = null;


    protected Object fActualValue = null;


    protected short fActualValueType = XSConstants.UNAVAILABLE_DT;


    protected ShortList fItemValueTypes = null;


    protected XSNotationDeclaration fNotation = null;


    protected XSSimpleTypeDefinition fMemberType = null;


    protected short fValidationAttempted = ElementPSVI.VALIDATION_NONE;


    protected short fValidity = ElementPSVI.VALIDITY_NOTKNOWN;


    protected String[] fErrorCodes = null;


    protected String fValidationContext = null;


    protected SchemaGrammar[] fGrammars = null;


    protected XSModel fSchemaInformation = null;

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


    public boolean getNil() {
        return fNil;
    }


    public XSNotationDeclaration getNotation() {
        return fNotation;
    }


    public XSTypeDefinition getTypeDefinition() {
        return fTypeDecl;
    }


    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return fMemberType;
    }


    public XSElementDeclaration getElementDeclaration() {
        return fDeclaration;
    }


    public synchronized XSModel getSchemaInformation() {
        if (fSchemaInformation == null && fGrammars != null) {
            fSchemaInformation = new XSModelImpl(fGrammars);
        }
        return fSchemaInformation;
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
        fDeclaration = null;
        fTypeDecl = null;
        fNil = false;
        fSpecified = false;
        fNotation = null;
        fMemberType = null;
        fValidationAttempted = ElementPSVI.VALIDATION_NONE;
        fValidity = ElementPSVI.VALIDITY_NOTKNOWN;
        fErrorCodes = null;
        fValidationContext = null;
        fNormalizedValue = null;
        fActualValue = null;
        fActualValueType = XSConstants.UNAVAILABLE_DT;
        fItemValueTypes = null;
    }

}
