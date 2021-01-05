
    public W3CDomHandler(DocumentBuilder builder) {
        if(builder==null)
            throw new IllegalArgumentException();
        this.builder = builder;
    }

    public DocumentBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }

    public DOMResult createUnmarshaller(ValidationEventHandler errorHandler) {
        if(builder==null)
            return new DOMResult();
        else
            return new DOMResult(builder.newDocument());
    }

    public Element getElement(DOMResult r) {


        Node n = r.getNode();
        if( n instanceof Document ) {
            return ((Document)n).getDocumentElement();
        }
        if( n instanceof Element )
            return (Element)n;
        if( n instanceof DocumentFragment )
            return (Element)n.getChildNodes().item(0);




        throw new IllegalStateException(n.toString());
    }

    public Source marshal(Element element, ValidationEventHandler errorHandler) {
        return new DOMSource(element);
    }
}
