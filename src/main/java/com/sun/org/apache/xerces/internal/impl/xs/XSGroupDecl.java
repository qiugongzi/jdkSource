


package com.sun.org.apache.xerces.internal.impl.xs;


import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;


public class XSGroupDecl implements XSModelGroupDefinition {

    public String fName = null;
    public String fTargetNamespace = null;
    public XSModelGroupImpl fModelGroup = null;
    public XSObjectList fAnnotations = null;
    private XSNamespaceItem fNamespaceItem = null;


    public short getType() {
        return XSConstants.MODEL_GROUP_DEFINITION;
    }


    public String getName() {
        return fName;
    }


    public String getNamespace() {
        return fTargetNamespace;
    }


    public XSModelGroup getModelGroup() {
        return fModelGroup;
    }


    public XSAnnotation getAnnotation() {
        return (fAnnotations != null) ? (XSAnnotation) fAnnotations.item(0) : null;
    }


    public XSObjectList getAnnotations() {
        return (fAnnotations != null) ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }


    public XSNamespaceItem getNamespaceItem() {
        return fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem namespaceItem) {
        fNamespaceItem = namespaceItem;
    }

}