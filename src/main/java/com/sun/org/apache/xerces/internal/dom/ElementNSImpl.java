


package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;




public class ElementNSImpl
    extends ElementImpl {

    static final long serialVersionUID = -9142310625494392642L;
    static final String xmlURI = "http:protected String namespaceURI;


    protected String localName;


    transient XSTypeDefinition type;

    protected ElementNSImpl() {
        super();
    }

    protected ElementNSImpl(CoreDocumentImpl ownerDocument,
                            String namespaceURI,
                            String qualifiedName)
        throws DOMException
    {
        super(ownerDocument, qualifiedName);
        setName(namespaceURI, qualifiedName);
    }

        private void setName(String namespaceURI, String qname) {

                String prefix;
                this.namespaceURI = namespaceURI;
                if (namespaceURI != null) {
            this.namespaceURI =     (namespaceURI.length() == 0) ? null : namespaceURI;
                }

        int colon1, colon2 ;

        if(qname == null){
                                String msg =
                                        DOMMessageFormatter.formatMessage(
                                                DOMMessageFormatter.DOM_DOMAIN,
                                                "NAMESPACE_ERR",
                                                null);
                                throw new DOMException(DOMException.NAMESPACE_ERR, msg);
        }
        else{
                    colon1 = qname.indexOf(':');
                    colon2 = qname.lastIndexOf(':');
        }

                ownerDocument.checkNamespaceWF(qname, colon1, colon2);
                if (colon1 < 0) {
                        localName = qname;
                        if (ownerDocument.errorChecking) {
                            ownerDocument.checkQName(null, localName);
                            if (qname.equals("xmlns")
                                && (namespaceURI == null
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
                }else {
                    prefix = qname.substring(0, colon1);
                    localName = qname.substring(colon2 + 1);

                    if (ownerDocument.errorChecking) {
                        if( namespaceURI == null || ( prefix.equals("xml") && !namespaceURI.equals(NamespaceContext.XML_URI) )){
                            String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "NAMESPACE_ERR",
                                        null);
                            throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                        }

                        ownerDocument.checkQName(prefix, localName);
                        ownerDocument.checkDOMNSErr(prefix, namespaceURI);
                    }
                }
        }

    protected ElementNSImpl(CoreDocumentImpl ownerDocument,
                            String namespaceURI, String qualifiedName,
                            String localName)
        throws DOMException
    {
        super(ownerDocument, qualifiedName);

        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }

    protected ElementNSImpl(CoreDocumentImpl ownerDocument,
                            String value) {
        super(ownerDocument, value);
    }

    void rename(String namespaceURI, String qualifiedName)
    {
        if (needsSyncData()) {
            synchronizeData();
        }
                this.name = qualifiedName;
        setName(namespaceURI, qualifiedName);
        reconcileDefaultAttributes();
    }


    protected void setValues (CoreDocumentImpl ownerDocument,
                            String namespaceURI, String qualifiedName,
                            String localName){

        firstChild = null;
        previousSibling = null;
        nextSibling = null;
        fNodeListCache = null;

        attributes = null;
        super.flags = 0;
        setOwnerDocument(ownerDocument);

        needsSyncData(true);
        super.name = qualifiedName;
        this.localName = localName;
        this.namespaceURI = namespaceURI;

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
        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }
            if (prefix != null && prefix.length() != 0) {
                if (!CoreDocumentImpl.isXMLName(prefix,ownerDocument.isXML11Version())) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
                    throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
                }
                if (namespaceURI == null || prefix.indexOf(':') >=0) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                    throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                } else if (prefix.equals("xml")) {
                     if (!namespaceURI.equals(xmlURI)) {
                         String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NAMESPACE_ERR", null);
                         throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                     }
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



    public String getBaseURI() {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes != null) {
            Attr attrNode = (Attr)attributes.getNamedItemNS("http:if (attrNode != null) {
                String uri =  attrNode.getNodeValue();
                if (uri.length() != 0 ) {try {
                        uri = new URI(uri).toString();
                    }
                    catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
                        NodeImpl parentOrOwner = (parentNode() != null) ? parentNode() : ownerNode;

                        String parentBaseURI = (parentOrOwner != null) ? parentOrOwner.getBaseURI() : null;

                        if (parentBaseURI != null) {
                            try {
                                uri = new URI(new URI(parentBaseURI), uri).toString();
                            }
                            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException ex){
                                return null;
                            }
                            return uri;
                        }
                        return null;
                    }
                    return uri;
                }
            }
        }

        String parentElementBaseURI = (this.parentNode() != null) ? this.parentNode().getBaseURI() : null ;
        if(parentElementBaseURI != null){
            try {
                return new URI(parentElementBaseURI).toString();
            }
            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e){
                return null;
            }
        }
        String baseURI = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null ;

        if(baseURI != null){
            try {
                return new URI(baseURI).toString();
            }
            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e){
                return null;
            }
        }

        return null;

    }



    public String getTypeName() {
        if (type !=null){
            if (type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl) type).getTypeName();
            } else if (type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl) type).getTypeName();
            }
        }
        return null;
    }


    public String getTypeNamespace() {
        if (type !=null){
            return type.getNamespace();
        }
        return null;
    }


    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg,
            int derivationMethod) {
        if(needsSyncData()) {
            synchronizeData();
        }
        if (type != null) {
            if (type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl) type).isDOMDerivedFrom(
                        typeNamespaceArg, typeNameArg, derivationMethod);
            } else if (type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl) type).isDOMDerivedFrom(
                        typeNamespaceArg, typeNameArg, derivationMethod);
            }
        }
        return false;
    }


    public void setType(XSTypeDefinition type) {
        this.type = type;
    }
}
