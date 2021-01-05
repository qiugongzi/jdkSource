


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;


public class XSDDescription extends XMLResourceIdentifierImpl
                implements XMLSchemaDescription {
    public final static short CONTEXT_INITIALIZE = -1;

    public final static short CONTEXT_INCLUDE   = 0;

    public final static short CONTEXT_REDEFINE  = 1;

    public final static short CONTEXT_IMPORT    = 2;

    public final static short CONTEXT_PREPARSE  = 3;

    public final static short CONTEXT_INSTANCE  = 4;

    public final static short CONTEXT_ELEMENT   = 5;

    public final static short CONTEXT_ATTRIBUTE = 6;

    public final static short CONTEXT_XSITYPE   = 7;

    protected short fContextType;
    protected String [] fLocationHints ;
    protected QName fTriggeringComponent;
    protected QName fEnclosedElementName;
    protected XMLAttributes  fAttributes;


    public String getGrammarType() {
        return XMLGrammarDescription.XML_SCHEMA;
    }


    public short getContextType() {
        return fContextType ;
    }


    public String getTargetNamespace() {
        return fNamespace;
    }


    public String[] getLocationHints() {
        return fLocationHints ;
    }


    public QName getTriggeringComponent() {
        return fTriggeringComponent ;
    }


    public QName getEnclosingElementName() {
        return fEnclosedElementName ;
    }


    public XMLAttributes getAttributes() {
        return fAttributes;
    }

    public boolean fromInstance() {
        return fContextType == CONTEXT_ATTRIBUTE ||
               fContextType == CONTEXT_ELEMENT ||
               fContextType == CONTEXT_INSTANCE ||
               fContextType == CONTEXT_XSITYPE;
    }


    public boolean isExternal() {
        return fContextType == CONTEXT_INCLUDE ||
               fContextType == CONTEXT_REDEFINE ||
               fContextType == CONTEXT_IMPORT ||
               fContextType == CONTEXT_ELEMENT ||
               fContextType == CONTEXT_ATTRIBUTE ||
               fContextType == CONTEXT_XSITYPE;
    }

    public boolean equals(Object descObj) {
        if(!(descObj instanceof XMLSchemaDescription)) return false;
        XMLSchemaDescription desc = (XMLSchemaDescription)descObj;
        if (fNamespace != null)
            return fNamespace.equals(desc.getTargetNamespace());
        else return desc.getTargetNamespace() == null;
    }


    public int hashCode() {
         return (fNamespace == null) ? 0 : fNamespace.hashCode();
    }

    public void setContextType(short contextType){
        fContextType = contextType ;
    }

    public void setTargetNamespace(String targetNamespace){
        fNamespace = targetNamespace ;
    }

    public void setLocationHints(String [] locationHints){
        int length = locationHints.length ;
        fLocationHints  = new String[length];
        System.arraycopy(locationHints, 0, fLocationHints, 0, length);
        }

    public void setTriggeringComponent(QName triggeringComponent){
        fTriggeringComponent = triggeringComponent ;
    }

    public void setEnclosingElementName(QName enclosedElementName){
        fEnclosedElementName = enclosedElementName ;
    }

    public void setAttributes(XMLAttributes attributes){
        fAttributes = attributes ;
    }


    public void reset(){
        super.clear();
        fContextType = CONTEXT_INITIALIZE;
        fLocationHints  = null ;
        fTriggeringComponent = null ;
        fEnclosedElementName = null ;
        fAttributes = null ;
    }

    public XSDDescription makeClone() {
        XSDDescription desc = new XSDDescription();
        desc.fAttributes = this.fAttributes;
        desc.fBaseSystemId = this.fBaseSystemId;
        desc.fContextType = this.fContextType;
        desc.fEnclosedElementName = this.fEnclosedElementName;
        desc.fExpandedSystemId = this.fExpandedSystemId;
        desc.fLiteralSystemId = this.fLiteralSystemId;
        desc.fLocationHints = this.fLocationHints;
        desc.fPublicId = this.fPublicId;
        desc.fNamespace = this.fNamespace;
        desc.fTriggeringComponent = this.fTriggeringComponent;
        return desc;
    }

}