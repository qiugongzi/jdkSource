





package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.NamedNodeMap;



public class DeferredElementNSImpl
    extends ElementNSImpl
    implements DeferredNode {

    static final long serialVersionUID = -5001885145370927385L;

    protected transient int fNodeIndex;

    DeferredElementNSImpl(DeferredDocumentImpl ownerDoc, int nodeIndex) {
        super(ownerDoc, null);

        fNodeIndex = nodeIndex;
        needsSyncChildren(true);

    } public final int getNodeIndex() {
        return fNodeIndex;
    }

    protected final void synchronizeData() {

        needsSyncData(false);

        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) this.ownerDocument;

        boolean orig = ownerDocument.mutationEvents;
        ownerDocument.mutationEvents = false;

        name = ownerDocument.getNodeName(fNodeIndex);

        int index = name.indexOf(':');
        if (index < 0) {
            localName = name;
        }
        else {
            localName = name.substring(index + 1);
        }

            namespaceURI = ownerDocument.getNodeURI(fNodeIndex);
        type = (XSTypeDefinition)ownerDocument.getTypeInfo(fNodeIndex);

        setupDefaultAttributes();
        int attrIndex = ownerDocument.getNodeExtra(fNodeIndex);
        if (attrIndex != -1) {
            NamedNodeMap attrs = getAttributes();
            boolean seenSchemaDefault = false;
            do {
                AttrImpl attr = (AttrImpl) ownerDocument.getNodeObject(attrIndex);
                if (!attr.getSpecified() && (seenSchemaDefault ||
                    (attr.getNamespaceURI() != null &&
                    attr.getNamespaceURI() != NamespaceContext.XMLNS_URI &&
                    attr.getName().indexOf(':') < 0))) {
                    seenSchemaDefault = true;
                    attrs.setNamedItemNS(attr);
                }
                else {
                    attrs.setNamedItem(attr);
                }
                attrIndex = ownerDocument.getPrevSibling(attrIndex);
            } while (attrIndex != -1);
        }

        ownerDocument.mutationEvents = orig;

    } protected final void synchronizeChildren() {
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) ownerDocument();
        ownerDocument.synchronizeChildren(this, fNodeIndex);
    } }