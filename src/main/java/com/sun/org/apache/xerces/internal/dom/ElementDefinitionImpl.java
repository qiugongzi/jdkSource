


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ElementDefinitionImpl
    extends ParentNode {

    static final long serialVersionUID = -8373890672670022714L;

    protected String name;


    protected NamedNodeMapImpl attributes;

    public ElementDefinitionImpl(CoreDocumentImpl ownerDocument, String name) {
        super(ownerDocument);
        this.name = name;
        attributes = new NamedNodeMapImpl(ownerDocument);
    }

    public short getNodeType() {
        return NodeImpl.ELEMENT_DEFINITION_NODE;
    }


    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }


    public Node cloneNode(boolean deep) {

        ElementDefinitionImpl newnode =
            (ElementDefinitionImpl) super.cloneNode(deep);
        newnode.attributes = attributes.cloneMap(newnode);
        return newnode;

    } public NamedNodeMap getAttributes() {

        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return attributes;

    } }