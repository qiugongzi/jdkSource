

package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;


public class CoreDocumentImpl
        extends ParentNode implements Document {


    static final long serialVersionUID = 0;

    protected DocumentTypeImpl docType;


    protected ElementImpl docElement;


    transient NodeListCache fFreeNLCache;


    protected String encoding;


    protected String actualEncoding;


    protected String version;


    protected boolean standalone;


    protected String fDocumentURI;

    private Map<Node, Map<String, UserDataRecord>> nodeUserData;


    protected Map<String, Node> identifiers;

    transient DOMNormalizer domNormalizer = null;
    transient DOMConfigurationImpl fConfiguration = null;

    transient Object fXPathEvaluator = null;


    private final static int[] kidOK;


    protected int changes = 0;

    protected boolean allowGrammarAccess;


    protected boolean errorChecking = true;

    protected boolean ancestorChecking = true;

    protected boolean xmlVersionChanged = false ;


    private int documentNumber=0;
    private int nodeCounter = 0;
    private Map<Node, Integer> nodeTable;
    private boolean xml11Version = false; static {

        kidOK = new int[13];

        kidOK[DOCUMENT_NODE] =
        1 << ELEMENT_NODE | 1 << PROCESSING_INSTRUCTION_NODE |
        1 << COMMENT_NODE | 1 << DOCUMENT_TYPE_NODE;

        kidOK[DOCUMENT_FRAGMENT_NODE] =
        kidOK[ENTITY_NODE] =
        kidOK[ENTITY_REFERENCE_NODE] =
        kidOK[ELEMENT_NODE] =
        1 << ELEMENT_NODE | 1 << PROCESSING_INSTRUCTION_NODE |
        1 << COMMENT_NODE | 1 << TEXT_NODE |
        1 << CDATA_SECTION_NODE | 1 << ENTITY_REFERENCE_NODE ;


        kidOK[ATTRIBUTE_NODE] =
        1 << TEXT_NODE | 1 << ENTITY_REFERENCE_NODE;

        kidOK[DOCUMENT_TYPE_NODE] =
        kidOK[PROCESSING_INSTRUCTION_NODE] =
        kidOK[COMMENT_NODE] =
        kidOK[TEXT_NODE] =
        kidOK[CDATA_SECTION_NODE] =
        kidOK[NOTATION_NODE] =
        0;

    } private static final ObjectStreamField[] serialPersistentFields =
        new ObjectStreamField[] {
            new ObjectStreamField("docType", DocumentTypeImpl.class),
            new ObjectStreamField("docElement", ElementImpl.class),
            new ObjectStreamField("fFreeNLCache", NodeListCache.class),
            new ObjectStreamField("encoding", String.class),
            new ObjectStreamField("actualEncoding", String.class),
            new ObjectStreamField("version", String.class),
            new ObjectStreamField("standalone", boolean.class),
            new ObjectStreamField("fDocumentURI", String.class),
            new ObjectStreamField("userData", Hashtable.class),
            new ObjectStreamField("identifiers", Hashtable.class),
            new ObjectStreamField("changes", int.class),
            new ObjectStreamField("allowGrammarAccess", boolean.class),
            new ObjectStreamField("errorChecking", boolean.class),
            new ObjectStreamField("ancestorChecking", boolean.class),
            new ObjectStreamField("xmlVersionChanged", boolean.class),
            new ObjectStreamField("documentNumber", int.class),
            new ObjectStreamField("nodeCounter", int.class),
            new ObjectStreamField("nodeTable", Hashtable.class),
            new ObjectStreamField("xml11Version", boolean.class),
        };

    public CoreDocumentImpl() {
        this(false);
    }


    public CoreDocumentImpl(boolean grammarAccess) {
        super(null);
        ownerDocument = this;
        allowGrammarAccess = grammarAccess;
        String systemProp = SecuritySupport.getSystemProperty(Constants.SUN_DOM_PROPERTY_PREFIX+Constants.SUN_DOM_ANCESTOR_CHECCK);
        if (systemProp != null) {
            if (systemProp.equalsIgnoreCase("false")) {
                ancestorChecking = false;
            }
        }
    }


    public CoreDocumentImpl(DocumentType doctype) {
        this(doctype, false);
    }


    public CoreDocumentImpl(DocumentType doctype, boolean grammarAccess) {
        this(grammarAccess);
        if (doctype != null) {
            DocumentTypeImpl doctypeImpl;
            try {
                doctypeImpl = (DocumentTypeImpl) doctype;
            } catch (ClassCastException e) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
            doctypeImpl.ownerDocument = this;
            appendChild(doctype);
        }
    }

    final public Document getOwnerDocument() {
        return null;
    }


    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }


    public String getNodeName() {
        return "#document";
    }


    public Node cloneNode(boolean deep) {

        CoreDocumentImpl newdoc = new CoreDocumentImpl();
        callUserDataHandlers(this, newdoc, UserDataHandler.NODE_CLONED);
        cloneNode(newdoc, deep);

        return newdoc;

    } protected void cloneNode(CoreDocumentImpl newdoc, boolean deep) {

        if (needsSyncChildren()) {
            synchronizeChildren();
        }

        if (deep) {
            Map<Node, String> reversedIdentifiers = null;

            if (identifiers != null) {
                reversedIdentifiers = new HashMap<>(identifiers.size());
                for (String elementId : identifiers.keySet()) {
                    reversedIdentifiers.put(identifiers.get(elementId), elementId);
                }
            }

            for (ChildNode kid = firstChild; kid != null;
                    kid = kid.nextSibling) {
                newdoc.appendChild(newdoc.importNode(kid, true, true,
                        reversedIdentifiers));
            }
        }

        newdoc.allowGrammarAccess = allowGrammarAccess;
        newdoc.errorChecking = errorChecking;

    } public Node insertBefore(Node newChild, Node refChild)
            throws DOMException {

        int type = newChild.getNodeType();
        if (errorChecking) {
            if((type == Node.ELEMENT_NODE && docElement != null) ||
            (type == Node.DOCUMENT_TYPE_NODE && docType != null)) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, msg);
            }
        }
        if (newChild.getOwnerDocument() == null &&
        newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl) newChild).ownerDocument = this;
        }
        super.insertBefore(newChild,refChild);

        if (type == Node.ELEMENT_NODE) {
            docElement = (ElementImpl)newChild;
        }
        else if (type == Node.DOCUMENT_TYPE_NODE) {
            docType = (DocumentTypeImpl)newChild;
        }

        return newChild;

    } public Node removeChild(Node oldChild) throws DOMException {

        super.removeChild(oldChild);

        int type = oldChild.getNodeType();
        if(type == Node.ELEMENT_NODE) {
            docElement = null;
        }
        else if (type == Node.DOCUMENT_TYPE_NODE) {
            docType = null;
        }

        return oldChild;

    }   public Node replaceChild(Node newChild, Node oldChild)
            throws DOMException {

        if (newChild.getOwnerDocument() == null &&
        newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl) newChild).ownerDocument = this;
        }

        if (errorChecking &&((docType != null &&
            oldChild.getNodeType() != Node.DOCUMENT_TYPE_NODE &&
            newChild.getNodeType() == Node.DOCUMENT_TYPE_NODE)
            || (docElement != null &&
            oldChild.getNodeType() != Node.ELEMENT_NODE &&
            newChild.getNodeType() == Node.ELEMENT_NODE))) {

            throw new DOMException(
                    DOMException.HIERARCHY_REQUEST_ERR,
                    DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null));
        }
        super.replaceChild(newChild, oldChild);

        int type = oldChild.getNodeType();
        if(type == Node.ELEMENT_NODE) {
            docElement = (ElementImpl)newChild;
        }
        else if (type == Node.DOCUMENT_TYPE_NODE) {
            docType = (DocumentTypeImpl)newChild;
        }
        return oldChild;
    }   public String getTextContent() throws DOMException {
        return null;
    }


    public void setTextContent(String textContent)
            throws DOMException {
        }


    public Object getFeature(String feature, String version) {

        boolean anyVersion = version == null || version.length() == 0;

        if ((feature.equalsIgnoreCase("+XPath"))
                && (anyVersion || version.equals("3.0"))) {

            if (fXPathEvaluator != null) {
                return fXPathEvaluator;
            }

            try {
                Class xpathClass = ObjectFactory.findProviderClass (
                        "com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
                Constructor xpathClassConstr =
                    xpathClass.getConstructor(new Class[] { Document.class });

                Class interfaces[] = xpathClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (interfaces[i].getName().equals(
                            "org.w3c.dom.xpath.XPathEvaluator")) {
                        fXPathEvaluator = xpathClassConstr.newInstance(new Object[] { this });
                        return fXPathEvaluator;
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return super.getFeature(feature, version);
    }

    public Attr createAttribute(String name)
            throws DOMException {

        if (errorChecking && !isXMLName(name,xml11Version)) {
            String msg =
                DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "INVALID_CHARACTER_ERR",
                            null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new AttrImpl(this, name);

    } public CDATASection createCDATASection(String data)
            throws DOMException {
        return new CDATASectionImpl(this, data);
    }


    public Comment createComment(String data) {
        return new CommentImpl(this, data);
    }


    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(this);
    }


    public Element createElement(String tagName)
            throws DOMException {

        if (errorChecking && !isXMLName(tagName,xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new ElementImpl(this, tagName);

    } public EntityReference createEntityReference(String name)
            throws DOMException {

        if (errorChecking && !isXMLName(name,xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new EntityReferenceImpl(this, name);

    } public ProcessingInstruction createProcessingInstruction(String target,
            String data)
            throws DOMException {

        if (errorChecking && !isXMLName(target,xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new ProcessingInstructionImpl(this, target, data);

    } public Text createTextNode(String data) {
        return new TextImpl(this, data);
    }

    public DocumentType getDoctype() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return docType;
    }


    public Element getDocumentElement() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return docElement;
    }


    public NodeList getElementsByTagName(String tagname) {
        return new DeepNodeListImpl(this,tagname);
    }


    public DOMImplementation getImplementation() {
        return CoreDOMImplementationImpl.getDOMImplementation();
    }

    public void setErrorChecking(boolean check) {
        errorChecking = check;
    }


    public void setStrictErrorChecking(boolean check) {
        errorChecking = check;
    }


    public boolean getErrorChecking() {
        return errorChecking;
    }


    public boolean getStrictErrorChecking() {
        return errorChecking;
    }


    public String getInputEncoding() {
        return actualEncoding;
    }


    public void setInputEncoding(String value) {
        actualEncoding = value;
    }


    public void setXmlEncoding(String value) {
        encoding = value;
    }


    public void setEncoding(String value) {
        setXmlEncoding(value);
    }


    public String getXmlEncoding() {
        return encoding;
    }


    public String getEncoding() {
        return getXmlEncoding();
    }


    public void setXmlVersion(String value) {
        if(value.equals("1.0") || value.equals("1.1")){
            if(!getXmlVersion().equals(value)){
                xmlVersionChanged = true ;
                isNormalized(false);
                version = value;
            }
        }
        else{
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);

        }
        if((getXmlVersion()).equals("1.1")){
            xml11Version = true;
        }
        else{
            xml11Version = false;
        }
    }


    public void setVersion(String value) {
        setXmlVersion(value);
    }



    public String getXmlVersion() {
        return (version == null)?"1.0":version;
    }


    public String getVersion() {
        return getXmlVersion();
    }


    public void setXmlStandalone(boolean value)
            throws DOMException {
        standalone = value;
    }


    public void setStandalone(boolean value) {
        setXmlStandalone(value);
    }


    public boolean getXmlStandalone() {
        return standalone;
    }


    public boolean getStandalone() {
        return getXmlStandalone();
    }


    public String getDocumentURI(){
        return fDocumentURI;
    }



    public Node renameNode(Node n,String namespaceURI,String name)
    throws DOMException{

        if (errorChecking && n.getOwnerDocument() != this && n != this) {
            String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
        }
        switch (n.getNodeType()) {
            case ELEMENT_NODE: {
                ElementImpl el = (ElementImpl) n;
                if (el instanceof ElementNSImpl) {
                    ((ElementNSImpl) el).rename(namespaceURI, name);

                    callUserDataHandlers(el, null, UserDataHandler.NODE_RENAMED);
                }
                else {
                    if (namespaceURI == null) {
                        if (errorChecking) {
                            int colon1 = name.indexOf(':');
                            if(colon1 != -1){
                                String msg =
                                    DOMMessageFormatter.formatMessage(
                                                DOMMessageFormatter.DOM_DOMAIN,
                                                "NAMESPACE_ERR",
                                                null);
                                throw new DOMException(DOMException.NAMESPACE_ERR, msg);
                            }
                            if (!isXMLName(name,xml11Version)) {
                                String msg = DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "INVALID_CHARACTER_ERR", null);
                                throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
                                        msg);
                            }
                        }
                        el.rename(name);

                        callUserDataHandlers(el, null,
                                UserDataHandler.NODE_RENAMED);
                    }
                    else {
                        ElementNSImpl nel =
                            new ElementNSImpl(this, namespaceURI, name);

                        copyEventListeners(el, nel);

                        Map<String, UserDataRecord> data = removeUserDataTable(el);

                        Node parent = el.getParentNode();
                        Node nextSib = el.getNextSibling();
                        if (parent != null) {
                            parent.removeChild(el);
                        }
                        Node child = el.getFirstChild();
                        while (child != null) {
                            el.removeChild(child);
                            nel.appendChild(child);
                            child = el.getFirstChild();
                        }
                        nel.moveSpecifiedAttributes(el);

                        setUserDataTable(nel, data);

                        callUserDataHandlers(el, nel,
                                UserDataHandler.NODE_RENAMED);

                        if (parent != null) {
                            parent.insertBefore(nel, nextSib);
                        }
                        el = nel;
                    }
                }
                renamedElement((Element) n, el);
                return el;
            }
            case ATTRIBUTE_NODE: {
                AttrImpl at = (AttrImpl) n;

                Element el = at.getOwnerElement();
                if (el != null) {
                    el.removeAttributeNode(at);
                }
                if (n instanceof AttrNSImpl) {
                    ((AttrNSImpl) at).rename(namespaceURI, name);
                    if (el != null) {
                        el.setAttributeNodeNS(at);
                    }

                    callUserDataHandlers(at, null, UserDataHandler.NODE_RENAMED);
                }
                else {
                    if (namespaceURI == null) {
                        at.rename(name);
                        if (el != null) {
                            el.setAttributeNode(at);
                        }

                        callUserDataHandlers(at, null, UserDataHandler.NODE_RENAMED);
                    }
                    else {
                        AttrNSImpl nat = new AttrNSImpl(this, namespaceURI, name);

                        copyEventListeners(at, nat);

                        Map<String, UserDataRecord> data = removeUserDataTable(at);

                        Node child = at.getFirstChild();
                        while (child != null) {
                            at.removeChild(child);
                            nat.appendChild(child);
                            child = at.getFirstChild();
                        }

                        setUserDataTable(nat, data);

                        callUserDataHandlers(at, nat, UserDataHandler.NODE_RENAMED);

                        if (el != null) {
                            el.setAttributeNode(nat);
                        }
                        at = nat;
                    }
                }
                renamedAttrNode((Attr) n, at);

                return at;
            }
            default: {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        }

    }



    public void normalizeDocument(){
        if (isNormalized() && !isNormalizeDocRequired()) {
            return;
        }
        if (needsSyncChildren()) {
            synchronizeChildren();
        }

        if (domNormalizer == null) {
            domNormalizer = new DOMNormalizer();
        }

        if (fConfiguration == null) {
            fConfiguration =  new DOMConfigurationImpl();
        }
        else {
            fConfiguration.reset();
        }

        domNormalizer.normalizeDocument(this, fConfiguration);
        isNormalized(true);
        xmlVersionChanged = false ;
    }



    public DOMConfiguration getDomConfig(){
        if (fConfiguration == null) {
            fConfiguration = new DOMConfigurationImpl();
        }
        return fConfiguration;
    }



    public String getBaseURI() {
        if (fDocumentURI != null && fDocumentURI.length() != 0 ) {try {
                return new URI(fDocumentURI).toString();
            }
            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e){
                return null;
            }
        }
        return fDocumentURI;
    }


    public void setDocumentURI(String documentURI){
        fDocumentURI = documentURI;
    }


    public boolean getAsync() {
        return false;
    }


    public void setAsync(boolean async) {
        if (async) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        }
    }

    public void abort() {
    }


    public boolean load(String uri) {
        return false;
    }


    public boolean loadXML(String source) {
        return false;
    }


    public String saveXML(Node node)
            throws DOMException {
        if (errorChecking && node != null
                && this != node.getOwnerDocument()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
        }
        DOMImplementationLS domImplLS = (DOMImplementationLS) DOMImplementationImpl.getDOMImplementation();
        LSSerializer xmlWriter = domImplLS.createLSSerializer();
        if (node == null) {
            node = this;
        }
        return xmlWriter.writeToString(node);
    }


    void setMutationEvents(boolean set) {
        }


    boolean getMutationEvents() {
        return false;
    }

    public DocumentType createDocumentType(String qualifiedName,
            String publicID,
            String systemID)
            throws DOMException {

        return new DocumentTypeImpl(this, qualifiedName, publicID, systemID);

    } public Entity createEntity(String name)
            throws DOMException {

        if (errorChecking && !isXMLName(name, xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new EntityImpl(this, name);

    } public Notation createNotation(String name)
            throws DOMException {

        if (errorChecking && !isXMLName(name, xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new NotationImpl(this, name);

    } public ElementDefinitionImpl createElementDefinition(String name)
            throws DOMException {

        if (errorChecking && !isXMLName(name, xml11Version)) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_CHARACTER_ERR", null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        return new ElementDefinitionImpl(this, name);

    } protected int getNodeNumber() {
        if (documentNumber == 0) {

            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl) CoreDOMImplementationImpl.getDOMImplementation();
            documentNumber = cd.assignDocumentNumber();
        }
        return documentNumber;
    }


    protected int getNodeNumber(Node node) {

        int num;
        if (nodeTable == null) {
            nodeTable = new HashMap<>();
            num = --nodeCounter;
            nodeTable.put(node, new Integer(num));
        } else {
            Integer n = nodeTable.get(node);
            if (n == null) {
                num = --nodeCounter;
                nodeTable.put(node, num);
            } else {
                num = n.intValue();
            }
        }
        return num;
    }


    public Node importNode(Node source, boolean deep)
            throws DOMException {
        return importNode(source, deep, false, null);
    } private Node importNode(Node source, boolean deep, boolean cloningDoc,
            Map<Node, String> reversedIdentifiers)
            throws DOMException {
        Node newnode = null;
        Map<String, UserDataRecord> userData = null;

        if (source instanceof NodeImpl) {
            userData = ((NodeImpl) source).getUserDataRecord();
        }
        int type = source.getNodeType();
        switch (type) {
            case ELEMENT_NODE: {
                Element newElement;
                boolean domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
                if(domLevel20 == false || source.getLocalName() == null)
                    newElement = createElement(source.getNodeName());
                else
                    newElement = createElementNS(source.getNamespaceURI(),
                            source.getNodeName());

                NamedNodeMap sourceAttrs = source.getAttributes();
                if (sourceAttrs != null) {
                    int length = sourceAttrs.getLength();
                    for (int index = 0; index < length; index++) {
                        Attr attr = (Attr)sourceAttrs.item(index);

                        if (attr.getSpecified() || cloningDoc) {
                            Attr newAttr = (Attr)importNode(attr, true, cloningDoc,
                                    reversedIdentifiers);

                            if (domLevel20 == false ||
                            attr.getLocalName() == null)
                                newElement.setAttributeNode(newAttr);
                            else
                                newElement.setAttributeNodeNS(newAttr);
                            }
                        }
                    }

                if (reversedIdentifiers != null) {
                    String elementId = reversedIdentifiers.get(source);
                    if (elementId != null) {
                        if (identifiers == null) {
                            identifiers = new HashMap<>();
                        }

                        identifiers.put(elementId, newElement);
                    }
                }

                newnode = newElement;
                break;
            }

            case ATTRIBUTE_NODE: {

                if( source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0") ){
                    if (source.getLocalName() == null) {
                        newnode = createAttribute(source.getNodeName());
                    } else {
                        newnode = createAttributeNS(source.getNamespaceURI(),
                                source.getNodeName());
                    }
                }
                else {
                    newnode = createAttribute(source.getNodeName());
                }
                if (source instanceof AttrImpl) {
                    AttrImpl attr = (AttrImpl) source;
                    if (attr.hasStringValue()) {
                        AttrImpl newattr = (AttrImpl) newnode;
                        newattr.setValue(attr.getValue());
                        deep = false;
                    }
                    else {
                        deep = true;
                    }
                }
                else {
                    if (source.getFirstChild() == null) {
                        newnode.setNodeValue(source.getNodeValue());
                        deep = false;
                    } else {
                        deep = true;
                    }
                }
                break;
            }

            case TEXT_NODE: {
                newnode = createTextNode(source.getNodeValue());
                break;
            }

            case CDATA_SECTION_NODE: {
                newnode = createCDATASection(source.getNodeValue());
                break;
            }

            case ENTITY_REFERENCE_NODE: {
                newnode = createEntityReference(source.getNodeName());
                deep = false;
                break;
            }

            case ENTITY_NODE: {
                Entity srcentity = (Entity)source;
                EntityImpl newentity =
                (EntityImpl)createEntity(source.getNodeName());
                newentity.setPublicId(srcentity.getPublicId());
                newentity.setSystemId(srcentity.getSystemId());
                newentity.setNotationName(srcentity.getNotationName());
                newentity.isReadOnly(false);
                newnode = newentity;
                break;
            }

            case PROCESSING_INSTRUCTION_NODE: {
                newnode = createProcessingInstruction(source.getNodeName(),
                        source.getNodeValue());
                break;
            }

            case COMMENT_NODE: {
                newnode = createComment(source.getNodeValue());
                break;
            }

            case DOCUMENT_TYPE_NODE: {
                if (!cloningDoc) {
                    String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
                DocumentType srcdoctype = (DocumentType)source;
                DocumentTypeImpl newdoctype = (DocumentTypeImpl)
                createDocumentType(srcdoctype.getNodeName(),
                        srcdoctype.getPublicId(),
                        srcdoctype.getSystemId());
                NamedNodeMap smap = srcdoctype.getEntities();
                NamedNodeMap tmap = newdoctype.getEntities();
                if(smap != null) {
                    for(int i = 0; i < smap.getLength(); i++) {
                        tmap.setNamedItem(importNode(smap.item(i), true, true,
                                reversedIdentifiers));
                    }
                }
                smap = srcdoctype.getNotations();
                tmap = newdoctype.getNotations();
                if (smap != null) {
                    for(int i = 0; i < smap.getLength(); i++) {
                        tmap.setNamedItem(importNode(smap.item(i), true, true,
                                reversedIdentifiers));
                    }
                }

                newnode = newdoctype;
                break;
            }

            case DOCUMENT_FRAGMENT_NODE: {
                newnode = createDocumentFragment();
                break;
            }

            case NOTATION_NODE: {
                Notation srcnotation = (Notation)source;
                NotationImpl newnotation =
                (NotationImpl)createNotation(source.getNodeName());
                newnotation.setPublicId(srcnotation.getPublicId());
                newnotation.setSystemId(srcnotation.getSystemId());
                newnode = newnotation;
                break;
            }
            case DOCUMENT_NODE : default: {           String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        }

                if(userData != null)
                        callUserDataHandlers(source, newnode, UserDataHandler.NODE_IMPORTED,userData);

        if (deep) {
            for (Node srckid = source.getFirstChild();
                    srckid != null;
                    srckid = srckid.getNextSibling()) {
                newnode.appendChild(importNode(srckid, true, cloningDoc,
                        reversedIdentifiers));
            }
        }
        if (newnode.getNodeType() == Node.ENTITY_NODE) {
            ((NodeImpl)newnode).setReadOnly(true, true);
        }
        return newnode;

    } public Node adoptNode(Node source) {
        NodeImpl node;
        Map<String, UserDataRecord> userData;
        try {
            node = (NodeImpl) source;
        } catch (ClassCastException e) {
            return null;
        }

        if (source == null ) {
            return null;
        } else if (source.getOwnerDocument() != null) {

            DOMImplementation thisImpl = this.getImplementation();
            DOMImplementation otherImpl = source.getOwnerDocument().getImplementation();

            if (thisImpl != otherImpl) {

                if (thisImpl instanceof com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl &&
                        otherImpl instanceof com.sun.org.apache.xerces.internal.dom.DeferredDOMImplementationImpl) {
                    undeferChildren (node);
                } else if ( thisImpl instanceof com.sun.org.apache.xerces.internal.dom.DeferredDOMImplementationImpl
                        && otherImpl instanceof com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl) {
                    } else {
                    return null;
                }
            }
        }

        switch (node.getNodeType()) {
            case ATTRIBUTE_NODE: {
                AttrImpl attr = (AttrImpl) node;
                if( attr.getOwnerElement() != null){
                    attr.getOwnerElement().removeAttributeNode(attr);
                }
                attr.isSpecified(true);
                userData = node.getUserDataRecord();

                attr.setOwnerDocument(this);
                if (userData != null) {
                    setUserDataTable(node, userData);
                }
                break;
            }
            case ENTITY_NODE:
            case NOTATION_NODE:{
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);

            }
            case DOCUMENT_NODE:
            case DOCUMENT_TYPE_NODE: {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            case ENTITY_REFERENCE_NODE: {
                userData = node.getUserDataRecord();
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                Node child;
                while ((child = node.getFirstChild()) != null) {
                    node.removeChild(child);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    setUserDataTable(node, userData);
                }
                if (docType == null) {
                    break;
                }
                NamedNodeMap entities = docType.getEntities();
                Node entityNode = entities.getNamedItem(node.getNodeName());
                if (entityNode == null) {
                    break;
                }
                for (child = entityNode.getFirstChild();
                        child != null; child = child.getNextSibling()) {
                    Node childClone = child.cloneNode(true);
                    node.appendChild(childClone);
                }
                break;
            }
            case ELEMENT_NODE: {
                userData = node.getUserDataRecord();
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    setUserDataTable(node, userData);
                }
                ((ElementImpl)node).reconcileDefaultAttributes();
                break;
            }
            default: {
                userData = node.getUserDataRecord();
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    setUserDataTable(node, userData);
                }
            }
        }

                if (userData != null) {
            callUserDataHandlers(source, null, UserDataHandler.NODE_ADOPTED, userData);
        }

        return node;
    }


    protected void undeferChildren(Node node) {

        Node top = node;

        while (null != node) {

            if (((NodeImpl)node).needsSyncData()) {
                ((NodeImpl)node).synchronizeData();
            }

            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                int length = attributes.getLength();
                for (int i = 0; i < length; ++i) {
                    undeferChildren(attributes.item(i));
                }
            }

            Node nextNode = null;
            nextNode = node.getFirstChild();

            while (null == nextNode) {

                if (top.equals(node))
                    break;

                nextNode = node.getNextSibling();

                if (null == nextNode) {
                    node = node.getParentNode();

                    if ((null == node) || (top.equals(node))) {
                        nextNode = null;
                        break;
                    }
                }
            }

            node = nextNode;
        }
    }

    public Element getElementById(String elementId) {
        return getIdentifier(elementId);
    }


    protected final void clearIdentifiers(){
        if (identifiers != null){
            identifiers.clear();
        }
    }


    public void putIdentifier(String idName, Element element) {

        if (element == null) {
            removeIdentifier(idName);
            return;
        }

        if (needsSyncData()) {
            synchronizeData();
        }

        if (identifiers == null) {
            identifiers = new HashMap<>();
        }

        identifiers.put(idName, element);

    } public Element getIdentifier(String idName) {

        if (needsSyncData()) {
            synchronizeData();
        }

        if (identifiers == null) {
            return null;
        }
        Element elem = (Element) identifiers.get(idName);
        if (elem != null) {
            Node parent = elem.getParentNode();
            while (parent != null) {
                if (parent == this) {
                    return elem;
                }
                parent = parent.getParentNode();
            }
        }
        return null;
    } public void removeIdentifier(String idName) {

        if (needsSyncData()) {
            synchronizeData();
        }

        if (identifiers == null) {
            return;
        }

        identifiers.remove(idName);

    } public Element createElementNS(String namespaceURI, String qualifiedName)
            throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName);
    }


    public Element createElementNS(String namespaceURI, String qualifiedName,
            String localpart)
            throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName, localpart);
    }


    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
            throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName);
    }


    public Attr createAttributeNS(String namespaceURI, String qualifiedName,
            String localpart)
            throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName, localpart);
    }


    public NodeList getElementsByTagNameNS(String namespaceURI,
            String localName) {
        return new DeepNodeListImpl(this, namespaceURI, localName);
    }

    public Object clone() throws CloneNotSupportedException {
        CoreDocumentImpl newdoc = (CoreDocumentImpl) super.clone();
        newdoc.docType = null;
        newdoc.docElement = null;
        return newdoc;
    }

    public static final boolean isXMLName(String s, boolean xml11Version) {

        if (s == null) {
            return false;
        }
        if(!xml11Version)
            return XMLChar.isValidName(s);
        else
            return XML11Char.isXML11ValidName(s);

    } public static final boolean isValidQName(String prefix, String local, boolean xml11Version) {

        if (local == null) return false;
        boolean validNCName = false;

        if (!xml11Version) {
            validNCName = (prefix == null || XMLChar.isValidNCName(prefix))
                    && XMLChar.isValidNCName(local);
        }
        else {
            validNCName = (prefix == null || XML11Char.isXML11ValidNCName(prefix))
                    && XML11Char.isXML11ValidNCName(local);
        }

        return validNCName;
    }
    protected boolean isKidOK(Node parent, Node child) {
        if (allowGrammarAccess &&
        parent.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            return child.getNodeType() == Node.ELEMENT_NODE;
        }
        return 0 != (kidOK[parent.getNodeType()] & 1 << child.getNodeType());
    }


    protected void changed() {
        changes++;
    }


    protected int changes() {
        return changes;
    }

    NodeListCache getNodeListCache(ParentNode owner) {
        if (fFreeNLCache == null) {
            return new NodeListCache(owner);
        }
        NodeListCache c = fFreeNLCache;
        fFreeNLCache = fFreeNLCache.next;
        c.fChild = null;
        c.fChildIndex = -1;
        c.fLength = -1;
        if (c.fOwner != null) {
            c.fOwner.fNodeListCache = null;
        }
        c.fOwner = owner;
        return c;
    }


    void freeNodeListCache(NodeListCache c) {
        c.next = fFreeNLCache;
        fFreeNLCache = c;
    }




    public Object setUserData(Node n, String key,
            Object data, UserDataHandler handler) {
        if (data == null) {
            if (nodeUserData != null) {
                Map<String, UserDataRecord> t = nodeUserData.get(n);
                if (t != null) {
                    UserDataRecord r = t.remove(key);
                    if (r != null) {
                        return r.fData;
                    }
                }
            }
            return null;
        } else {
            Map<String, UserDataRecord> t;
            if (nodeUserData == null) {
                nodeUserData = new HashMap<>();
                t = new HashMap<>();
                nodeUserData.put(n, t);
            } else {
                t = nodeUserData.get(n);
                if (t == null) {
                    t = new HashMap<>();
                    nodeUserData.put(n, t);
                }
            }
            UserDataRecord r = t.put(key, new UserDataRecord(data, handler));
            if (r != null) {
                return r.fData;
            }
            return null;
        }
    }



    public Object getUserData(Node n, String key) {
        if (nodeUserData == null) {
            return null;
        }
        Map<String, UserDataRecord> t = nodeUserData.get(n);
        if (t == null) {
            return null;
        }
        UserDataRecord r = t.get(key);
        if (r != null) {
            return r.fData;
        }
        return null;
    }

    protected Map<String, UserDataRecord> getUserDataRecord(Node n) {
        if (nodeUserData == null) {
            return null;
        }
        Map<String, UserDataRecord> t = nodeUserData.get(n);
        if (t == null) {
            return null;
        }
        return t;
    }


    Map<String, UserDataRecord> removeUserDataTable(Node n) {
        if (nodeUserData == null) {
            return null;
        }
        return nodeUserData.get(n);
    }


    void setUserDataTable(Node n, Map<String, UserDataRecord> data) {
        if (nodeUserData == null) {
            nodeUserData = new HashMap<>();
        }

        if (data != null) {
            nodeUserData.put(n, data);
        }
    }


    void callUserDataHandlers(Node n, Node c, short operation) {
        if (nodeUserData == null) {
            return;
        }

        if (n instanceof NodeImpl) {
            Map<String, UserDataRecord> t = ((NodeImpl) n).getUserDataRecord();
            if (t == null || t.isEmpty()) {
                return;
            }
            callUserDataHandlers(n, c, operation, t);
        }
    }


    void callUserDataHandlers(Node n, Node c, short operation, Map<String, UserDataRecord> userData) {
        if (userData == null || userData.isEmpty()) {
            return;
        }
        for (String key : userData.keySet()) {
            UserDataRecord r = userData.get(key);
            if (r.fHandler != null) {
                r.fHandler.handle(operation, key, r.fData, n, c);
            }
        }
    }


    protected final void checkNamespaceWF( String qname, int colon1,
            int colon2) {

        if (!errorChecking) {
            return;
        }
        if (colon1 == 0 || colon1 == qname.length() - 1 || colon2 != colon1) {
            String msg =
            DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "NAMESPACE_ERR",
                            null);
            throw new DOMException(DOMException.NAMESPACE_ERR, msg);
        }
    }
    protected final void checkDOMNSErr(String prefix,
            String namespace) {
        if (errorChecking) {
            if (namespace == null) {
                String msg =
                DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.DOM_DOMAIN,
                                "NAMESPACE_ERR",
                                null);
                throw new DOMException(DOMException.NAMESPACE_ERR, msg);
            }
            else if (prefix.equals("xml")
                    && !namespace.equals(NamespaceContext.XML_URI)) {
                String msg =
                DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.DOM_DOMAIN,
                                "NAMESPACE_ERR",
                                null);
                throw new DOMException(DOMException.NAMESPACE_ERR, msg);
            }
            else if (
            prefix.equals("xmlns")
                    && !namespace.equals(NamespaceContext.XMLNS_URI)
                    || (!prefix.equals("xmlns")
                    && namespace.equals(NamespaceContext.XMLNS_URI))) {
                String msg =
                DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.DOM_DOMAIN,
                                "NAMESPACE_ERR",
                                null);
                throw new DOMException(DOMException.NAMESPACE_ERR, msg);
            }
        }
    }


    protected final void checkQName(String prefix, String local) {
        if (!errorChecking) {
            return;
        }

        boolean validNCName = false;
        if (!xml11Version) {
            validNCName = (prefix == null || XMLChar.isValidNCName(prefix))
                    && XMLChar.isValidNCName(local);
        }
        else {
            validNCName = (prefix == null || XML11Char.isXML11ValidNCName(prefix))
                    && XML11Char.isXML11ValidNCName(local);
        }

        if (!validNCName) {
            String msg =
            DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "INVALID_CHARACTER_ERR",
                            null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
    }


    boolean isXML11Version(){
        return xml11Version;
    }

    boolean isNormalizeDocRequired(){
        return true;
    }

    boolean isXMLVersionChanged(){
        return xmlVersionChanged ;
    }

    protected void setUserData(NodeImpl n, Object data) {
        setUserData(n, "XERCES1DOMUSERDATA", data, null);
    }


    protected Object getUserData(NodeImpl n) {
        return getUserData(n, "XERCES1DOMUSERDATA");
    }


    protected void addEventListener(NodeImpl node, String type,
            EventListener listener,
            boolean useCapture) {
        }

    protected void removeEventListener(NodeImpl node, String type,
            EventListener listener,
            boolean useCapture) {
        }

    protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {
        }

    protected boolean dispatchEvent(NodeImpl node, Event event) {
        return false;
    }

    void replacedText(NodeImpl node) {
    }


    void deletedText(NodeImpl node, int offset, int count) {
    }


    void insertedText(NodeImpl node, int offset, int count) {
    }


    void modifyingCharacterData(NodeImpl node, boolean replace) {
    }


    void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
    }


    void insertingNode(NodeImpl node, boolean replace) {
    }


    void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
    }


    void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
    }


    void removedNode(NodeImpl node, boolean replace) {
    }


    void replacingNode(NodeImpl node) {
    }


    void replacedNode(NodeImpl node) {
    }


    void replacingData(NodeImpl node) {
    }


    void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
    }



    void modifiedAttrValue(AttrImpl attr, String oldvalue) {
    }


    void setAttrNode(AttrImpl attr, AttrImpl previous) {
    }


    void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
    }


    void renamedAttrNode(Attr oldAt, Attr newAt) {
    }


    void renamedElement(Element oldEl, Element newEl) {
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<Node, Hashtable<String, UserDataRecord>> nud = null;
        if (nodeUserData != null) {
            nud = new Hashtable<>();
            for (Map.Entry<Node, Map<String, UserDataRecord>> e : nodeUserData.entrySet()) {
                nud.put(e.getKey(), new Hashtable<>(e.getValue()));
            }
        }

        Hashtable<String, Node> ids = (identifiers == null)? null : new Hashtable<>(identifiers);
        Hashtable<Node, Integer> nt = (nodeTable == null)? null : new Hashtable<>(nodeTable);

        ObjectOutputStream.PutField pf = out.putFields();
        pf.put("docType", docType);
        pf.put("docElement", docElement);
        pf.put("fFreeNLCache", fFreeNLCache);
        pf.put("encoding", encoding);
        pf.put("actualEncoding", actualEncoding);
        pf.put("version", version);
        pf.put("standalone", standalone);
        pf.put("fDocumentURI", fDocumentURI);

        pf.put("userData", nud);
        pf.put("identifiers", ids);
        pf.put("changes", changes);
        pf.put("allowGrammarAccess", allowGrammarAccess);
        pf.put("errorChecking", errorChecking);
        pf.put("ancestorChecking", ancestorChecking);
        pf.put("xmlVersionChanged", xmlVersionChanged);
        pf.put("documentNumber", documentNumber);
        pf.put("nodeCounter", nodeCounter);
        pf.put("nodeTable", nt);
        pf.put("xml11Version", xml11Version);
        out.writeFields();
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in)
                        throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gf = in.readFields();
        docType = (DocumentTypeImpl)gf.get("docType", null);
        docElement = (ElementImpl)gf.get("docElement", null);
        fFreeNLCache = (NodeListCache)gf.get("fFreeNLCache", null);
        encoding = (String)gf.get("encoding", null);
        actualEncoding = (String)gf.get("actualEncoding", null);
        version = (String)gf.get("version", null);
        standalone = gf.get("standalone", false);
        fDocumentURI = (String)gf.get("fDocumentURI", null);

        Hashtable<Node, Hashtable<String, UserDataRecord>> nud =
                (Hashtable<Node, Hashtable<String, UserDataRecord>>)gf.get("userData", null);

        Hashtable<String, Node> ids = (Hashtable<String, Node>)gf.get("identifiers", null);

        changes = gf.get("changes", 0);
        allowGrammarAccess = gf.get("allowGrammarAccess", false);
        errorChecking = gf.get("errorChecking", true);
        ancestorChecking = gf.get("ancestorChecking", true);
        xmlVersionChanged = gf.get("xmlVersionChanged", false);
        documentNumber = gf.get("documentNumber", 0);
        nodeCounter = gf.get("nodeCounter", 0);

        Hashtable<Node, Integer> nt = (Hashtable<Node, Integer>)gf.get("nodeTable", null);

        xml11Version = gf.get("xml11Version", false);

        if (nud != null) {
            nodeUserData = new HashMap<>();
            for (Map.Entry<Node, Hashtable<String, UserDataRecord>> e : nud.entrySet()) {
                nodeUserData.put(e.getKey(), new HashMap<>(e.getValue()));
            }
        }

        if (ids != null) identifiers = new HashMap<>(ids);
        if (nt != null) nodeTable = new HashMap<>(nt);
    }
}