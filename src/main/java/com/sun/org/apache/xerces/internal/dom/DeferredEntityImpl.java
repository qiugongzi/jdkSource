


package com.sun.org.apache.xerces.internal.dom;



public class DeferredEntityImpl
    extends EntityImpl
    implements DeferredNode {

    static final long serialVersionUID = 4760180431078941638L;

    protected transient int fNodeIndex;

    DeferredEntityImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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

        publicId    = ownerDocument.getNodeValue(fNodeIndex);
        systemId    = ownerDocument.getNodeURI(fNodeIndex);
        int extraDataIndex = ownerDocument.getNodeExtra(fNodeIndex);
        ownerDocument.getNodeType(extraDataIndex);

        notationName = ownerDocument.getNodeName(extraDataIndex);

        version     = ownerDocument.getNodeValue(extraDataIndex);
        encoding    = ownerDocument.getNodeURI(extraDataIndex);

        int extraIndex2 = ownerDocument.getNodeExtra(extraDataIndex);
        baseURI = ownerDocument.getNodeName(extraIndex2);
        inputEncoding = ownerDocument.getNodeValue(extraIndex2);

    } protected void synchronizeChildren() {

        needsSyncChildren(false);

        isReadOnly(false);
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) ownerDocument();
        ownerDocument.synchronizeChildren(this, fNodeIndex);
        setReadOnly(true, true);

    } }