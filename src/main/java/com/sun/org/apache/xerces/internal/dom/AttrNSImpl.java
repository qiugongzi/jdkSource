


package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import org.w3c.dom.DOMException;


public class AttrNSImpl
    extends AttrImpl {

    static final long serialVersionUID = -781906615369795414L;

    static final String xmlnsURI = "http:static final String xmlURI = "http:protected String namespaceURI;


    protected String localName;


    public AttrNSImpl(){}


    protected AttrNSImpl(CoreDocumentImpl ownerDocument,
                         String namespaceURI,
                         String qualifiedName) {

        super(ownerDocument, qualifiedName);
        setName(namespaceURI, qualifiedName);
    }

    private void setName(String namespaceURI, String qname){
        CoreDocumentImpl ownerDocument = ownerDocument();
        String prefix;
        this.namespaceURI = namespaceURI;
        if (namespaceURI !=null) {
            this.namespaceURI = (namespaceURI.length() == 0)? null
                    : namespaceURI;

        }
        int colon1 = qname.indexOf(':');
        int colon2 = qname.lastIndexOf(':');
        ownerDocument.checkNamespaceWF(qname, colon1, colon2);
        if (colon1 < 0) {
            localName = qname;
            if (ownerDocument.errorChecking) {
                ownerDocument.checkQName(null, localName);

                if (qname.equals("xmlns") && (namespaceURI == null
                    || !namespaceURI.equals(NamespaceContext.XMLNS_URI))
                    || (namespaceURI!=null && namespaceURI.equals(NamespaceContext.XMLNS_URI)
                    && !qname.equals("xmlns"))) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.DOM_DOMAIN,
                                "NAMESPACE_ERR",
                                null);
                    throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                }
            }
        }
        else {
            prefix = qname.substring(0, colon1);
            localName = qname.substring(colon2+1);
            ownerDocument.checkQName(prefix, localName);
            ownerDocument.checkDOMNSErr(prefix, namespaceURI);
        }
    }

    public AttrNSImpl(CoreDocumentImpl ownerDocument,
                         String namespaceURI,
                         String qualifiedName,
                         String localName) {
        super(ownerDocument, qualifiedName);

        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }

    protected AttrNSImpl(CoreDocumentImpl ownerDocument,
                         String value) {
        super(ownerDocument, value);
    }

    void rename(String namespaceURI, String qualifiedName) {
        if (needsSyncData()) {
            synchronizeData();
        }
                this.name = qualifiedName;
        setName(namespaceURI, qualifiedName);
    }


    public void setValues (CoreDocumentImpl ownerDocument,
                         String namespaceURI,
                         String qualifiedName,
                         String localName){

        super.textNode = null;
        super.flags = 0;
        isSpecified(true);
        hasStringValue(true);
        super.setOwnerDocument(ownerDocument);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        super.name = qualifiedName;
        super.value = null;
    }

    public String getNamespaceURI()
    {
        if (needsSyncData()) {
            synchronizeData();
        }
        return namespaceURI;
    }


    public String getPrefix()
    {
        if (needsSyncData()) {
            synchronizeData();
        }
        int index = name.indexOf(':');
        return index < 0 ? null : name.substring(0, index);
    }


    public void setPrefix(String prefix)
        throws DOMException
    {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (ownerDocument().errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
            }
            if (prefix != null && prefix.length() != 0) {

                if (!CoreDocumentImpl.isXMLName(prefix,ownerDocument().isXML11Version())) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
                    throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
                }
                if (namespaceURI == null || prefix.indexOf(':') >=0) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                    throw new DOMException(DOMException.NAMESPACE_ERR, msg);

                }
               if (prefix.equals("xmlns")) {
                    if (!namespaceURI.equals(xmlnsURI)){
                        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                        throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                    }
                } else if (prefix.equals("xml")) {
                    if (!namespaceURI.equals(xmlURI)) {
                        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                        throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                    }
                }else if (name.equals("xmlns")) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                    throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                }
            }
        }

        if (prefix !=null && prefix.length() != 0) {
            name = prefix + ":" + localName;
        }
        else {
            name = localName;
        }
    }


    public String getLocalName()
    {
        if (needsSyncData()) {
            synchronizeData();
        }
        return localName;
    }



    public String getTypeName() {
        if (type !=null){
            if (type instanceof XSSimpleTypeDecl){
                return ((XSSimpleTypeDecl)type).getName();
            }
            return (String)type;
        }
        return null;
    }


    public boolean isDerivedFrom(String typeNamespaceArg,
                                 String typeNameArg,
                                 int derivationMethod) {
        if (type != null) {
            if (type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl) type).isDOMDerivedFrom(
                        typeNamespaceArg, typeNameArg, derivationMethod);
            }
        }
        return false;
    }


    public String getTypeNamespace() {
        if (type !=null) {
            if (type instanceof XSSimpleTypeDecl){
                return ((XSSimpleTypeDecl)type).getNamespace();
            }
            return DTD_URI;
        }
        return null;
    }

}
