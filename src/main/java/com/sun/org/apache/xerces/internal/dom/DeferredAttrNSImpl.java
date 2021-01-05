





package com.sun.org.apache.xerces.internal.dom;


public final class DeferredAttrNSImpl
    extends AttrNSImpl
    implements DeferredNode {

    static final long serialVersionUID = 6074924934945957154L;

    protected transient int fNodeIndex;

    DeferredAttrNSImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
            (DeferredDocumentImpl) ownerDocument();
        name = ownerDocument.getNodeName(fNodeIndex);

        int index = name.indexOf(':');
        if (index < 0) {
            localName = name;
        }
        else {
            localName = name.substring(index + 1);
        }

        int extra = ownerDocument.getNodeExtra(fNodeIndex);
        isSpecified((extra & SPECIFIED) != 0);
        isIdAttribute((extra & ID) != 0);

        namespaceURI = ownerDocument.getNodeURI(fNodeIndex);

        int extraNode = ownerDocument.getLastChild(fNodeIndex);
        type = ownerDocument.getTypeInfo(extraNode);
    } protected void synchronizeChildren() {
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) ownerDocument();
        ownerDocument.synchronizeChildren(this, fNodeIndex);
    } }