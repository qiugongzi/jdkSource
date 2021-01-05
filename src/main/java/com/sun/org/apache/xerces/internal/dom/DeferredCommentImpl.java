
    protected void synchronizeData() {


        needsSyncData(false);


        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) this.ownerDocument();
        data = ownerDocument.getNodeValueString(fNodeIndex);

    }

}
