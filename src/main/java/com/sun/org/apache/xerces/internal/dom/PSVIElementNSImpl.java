


package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.*;


public class PSVIElementNSImpl extends ElementNSImpl implements ElementPSVI {


    static final long serialVersionUID = 6815489624636016068L;


    public PSVIElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI,
                             String qualifiedName, String localName) {
        super(ownerDocument, namespaceURI, qualifiedName, localName);
    }


    public PSVIElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI,
                             String qualifiedName) {
        super(ownerDocument, namespaceURI, qualifiedName);
    }


    protected XSElementDeclaration fDeclaration = null;


    protected XSTypeDefinition fTypeDecl = null;


    protected boolean fNil = false;


    protected boolean fSpecified = true;


    protected String fNormalizedValue = null;


    protected Object fActualValue = null;


    protected short fActualValueType = XSConstants.UNAVAILABLE_DT;


    protected ShortList fItemValueTypes = null;


    protected XSNotationDeclaration fNotation = null;


    protected XSSimpleTypeDefinition fMemberType = null;


    protected short fValidationAttempted = ElementPSVI.VALIDATION_NONE;


    protected short fValidity = ElementPSVI.VALIDITY_NOTKNOWN;


    protected StringList fErrorCodes = null;


    protected String fValidationContext = null;


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
        return fErrorCodes;
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


    public XSModel getSchemaInformation() {
        return fSchemaInformation;
    }


    public void setPSVI(ElementPSVI elem) {
        this.fDeclaration = elem.getElementDeclaration();
        this.fNotation = elem.getNotation();
        this.fValidationContext = elem.getValidationContext();
        this.fTypeDecl = elem.getTypeDefinition();
        this.fSchemaInformation = elem.getSchemaInformation();
        this.fValidity = elem.getValidity();
        this.fValidationAttempted = elem.getValidationAttempted();
        this.fErrorCodes = elem.getErrorCodes();
        this.fNormalizedValue = elem.getSchemaNormalizedValue();
        this.fActualValue = elem.getActualNormalizedValue();
        this.fActualValueType = elem.getActualNormalizedValueType();
        this.fItemValueTypes = elem.getItemValueTypes();
        this.fMemberType = elem.getMemberTypeDefinition();
        this.fSpecified = elem.getIsSchemaSpecified();
        this.fNil = elem.getNil();
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
