
    protected void synchronizeData() {


        needsSyncData(false);


        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) this.ownerDocument();
        target  = ownerDocument.getNodeName(fNodeIndex);
        data = ownerDocument.getNodeValueString(fNodeIndex);

    }

}
