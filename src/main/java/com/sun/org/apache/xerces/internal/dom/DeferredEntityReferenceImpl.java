


package com.sun.org.apache.xerces.internal.dom;


public class DeferredEntityReferenceImpl
    extends EntityReferenceImpl
    implements DeferredNode {

    static final long serialVersionUID = 390319091370032223L;

    protected transient int fNodeIndex;

    DeferredEntityReferenceImpl(DeferredDocumentImpl ownerDocument,
                                int nodeIndex) {
        super(ownerDocument, null);

        fNodeIndex = nodeIndex;
        needsSyncData(true);

    } public int getNodeIndex() {
        return fNodeIndex;
    }

    protected void synchronizeData() {

        needsSyncData(false);

        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl)this.ownerDocument;
        name = ownerDocument.getNodeName(fNodeIndex);
        baseURI = ownerDocument.getNodeValue(fNodeIndex);

    } protected void synchronizeChildren() {

        needsSyncChildren(false);

        isReadOnly(false);
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) ownerDocument();
        ownerDocument.synchronizeChildren(this, fNodeIndex);
        setReadOnly(true, true);

    } }