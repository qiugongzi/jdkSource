
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    private SchemaDOMImplementation() {}

    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype)
            throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId)
            throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    public Object getFeature(String feature, String version) {
        if (singleton.hasFeature(feature, version)) {
            return singleton;
        }
        return null;
    }

    public boolean hasFeature(String feature, String version) {
        final boolean anyVersion = version == null || version.length() == 0;
        return (feature.equalsIgnoreCase("Core") || feature.equalsIgnoreCase("XML")) &&
            (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0"));
    }

}
