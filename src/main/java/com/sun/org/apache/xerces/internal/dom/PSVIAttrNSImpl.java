


package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.*;


public class PSVIAttrNSImpl extends AttrNSImpl implements AttributePSVI {


    static final long serialVersionUID = -3241738699421018889L;


    public PSVIAttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI,
                          String qualifiedName, String localName) {
        super(ownerDocument, namespaceURI, qualifiedName, localName);
    }


    public PSVIAttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI,
                          String qualifiedName) {
        super(ownerDocument, namespaceURI, qualifiedName);
    }


    protected XSAttributeDeclaration fDeclaration = null;


    protected XSTypeDefinition fTypeDecl = null;


    protected boolean fSpecified = true;


    protected String fNormalizedValue = null;


    protected Object fActualValue = null;


    protected short fActualValueType = XSConstants.UNAVAILABLE_DT;


    protected ShortList fItemValueTypes = null;


    protected XSSimpleTypeDefinition fMemberType = null;


    protected short fValidationAttempted = AttributePSVI.VALIDATION_NONE;


    protected short fValidity = AttributePSVI.VALIDITY_NOTKNOWN;


    protected StringList fErrorCodes = null;


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
        return fErrorCodes;
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


    public void setPSVI(AttributePSVI attr) {
        this.fDeclaration = attr.getAttributeDeclaration();
        this.fValidationContext = attr.getValidationContext();
        this.fValidity = attr.getValidity();
        this.fValidationAttempted = attr.getValidationAttempted();
        this.fErrorCodes = attr.getErrorCodes();
        this.fNormalizedValue = attr.getSchemaNormalizedValue();
        this.fActualValue = attr.getActualNormalizedValue();
        this.fActualValueType = attr.getActualNormalizedValueType();
        this.fItemValueTypes = attr.getItemValueTypes();
        this.fTypeDecl = attr.getTypeDefinition();
        this.fMemberType = attr.getMemberTypeDefinition();
        this.fSpecified = attr.getIsSchemaSpecified();
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

    private void writeObject(ObjectOutputStream out)
        throws IOException {
        throw new NotSerializableException(getClass().getName());
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        throw new NotSerializableException(getClass().getName());
    }
}
