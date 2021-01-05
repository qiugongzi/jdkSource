


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;


public class DeferredElementDefinitionImpl
    extends ElementDefinitionImpl
    implements DeferredNode {

    static final long serialVersionUID = 6703238199538041591L;

    protected transient int fNodeIndex;

    DeferredElementDefinitionImpl(DeferredDocumentImpl ownerDocument,
                                  int nodeIndex) {
        super(ownerDocument, null);

        fNodeIndex = nodeIndex;
        needsSyncData(true);
        needsSyncChildren(true);

    } public int getNodeIndex() {
        return fNodeIndex;
    }

    protected void synchronizeData() {

        needsSyncData(false);

        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl)this.ownerDocument;
        name = ownerDocument.getNodeName(fNodeIndex);

    } protected void synchronizeChildren() {

        boolean orig = ownerDocument.getMutationEvents();
        ownerDocument.setMutationEvents(false);

        needsSyncChildren(false);

        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl)this.ownerDocument;
        attributes = new NamedNodeMapImpl(ownerDocument);

        for (int nodeIndex = ownerDocument.getLastChild(fNodeIndex);
             nodeIndex != -1;
             nodeIndex = ownerDocument.getPrevSibling(nodeIndex)) {
            Node attr = ownerDocument.getNodeObject(nodeIndex);
            attributes.setNamedItem(attr);
        }

        ownerDocument.setMutationEvents(orig);

    } }