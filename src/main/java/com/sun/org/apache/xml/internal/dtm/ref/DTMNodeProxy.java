


package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.Vector;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.Objects;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.TypeInfo;


public class DTMNodeProxy
  implements Node, Document, Text, Element, Attr,
                   ProcessingInstruction, Comment, DocumentFragment
{


  public DTM dtm;


  int node;


  private static final String EMPTYSTRING = "";


  static final DOMImplementation implementation=new DTMNodeProxyImplementation();


  public DTMNodeProxy(DTM dtm, int node)
  {
    this.dtm = dtm;
    this.node = node;
  }


  public final DTM getDTM()
  {
    return dtm;
  }


  public final int getDTMNodeNumber()
  {
    return node;
  }


  public final boolean equals(Node node)
  {

    try
    {
      DTMNodeProxy dtmp = (DTMNodeProxy) node;

      return (dtmp.node == this.node) && (dtmp.dtm == this.dtm);
    }
    catch (ClassCastException cce)
    {
      return false;
    }
  }


  @Override
  public final boolean equals(Object node)
  {
      return node instanceof Node && equals((Node) node);
  }

  @Override
  public int hashCode() {
      int hash = 7;
      hash = 29 * hash + Objects.hashCode(this.dtm);
      hash = 29 * hash + this.node;
      return hash;
  }


  public final boolean sameNodeAs(Node other)
  {

    if (!(other instanceof DTMNodeProxy))
      return false;

    DTMNodeProxy that = (DTMNodeProxy) other;

    return this.dtm == that.dtm && this.node == that.node;
  }


  @Override
  public final String getNodeName()
  {
    return dtm.getNodeName(node);
  }


  @Override
  public final String getTarget()
  {
    return dtm.getNodeName(node);
  }  @Override
  public final String getLocalName()
  {
    return dtm.getLocalName(node);
  }


  @Override
  public final String getPrefix()
  {
    return dtm.getPrefix(node);
  }


  @Override
  public final void setPrefix(String prefix) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final String getNamespaceURI()
  {
    return dtm.getNamespaceURI(node);
  }


  public final boolean supports(String feature, String version)
  {
    return implementation.hasFeature(feature,version);
    }


  @Override
  public final boolean isSupported(String feature, String version)
  {
    return implementation.hasFeature(feature,version);
    }


  @Override
  public final String getNodeValue() throws DOMException
  {
    return dtm.getNodeValue(node);
  }


  public final String getStringValue() throws DOMException
  {
        return dtm.getStringValue(node).toString();
  }


  @Override
  public final void setNodeValue(String nodeValue) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final short getNodeType()
  {
    return (short) dtm.getNodeType(node);
  }


  @Override
  public final Node getParentNode()
  {

    if (getNodeType() == Node.ATTRIBUTE_NODE)
      return null;

    int newnode = dtm.getParent(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }


  public final Node getOwnerNode()
  {

    int newnode = dtm.getParent(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }


  @Override
  public final NodeList getChildNodes()
  {

    return new DTMChildIterNodeList(dtm,node);

    }


  @Override
  public final Node getFirstChild()
  {

    int newnode = dtm.getFirstChild(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }


  @Override
  public final Node getLastChild()
  {

    int newnode = dtm.getLastChild(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }


  @Override
  public final Node getPreviousSibling()
  {

    int newnode = dtm.getPreviousSibling(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }


  @Override
  public final Node getNextSibling()
  {

    if (dtm.getNodeType(node) == Node.ATTRIBUTE_NODE)
      return null;

    int newnode = dtm.getNextSibling(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  @Override
  public final NamedNodeMap getAttributes()
  {

    return new DTMNamedNodeMap(dtm, node);
  }


  @Override
  public boolean hasAttribute(String name)
  {
    return DTM.NULL != dtm.getAttributeNode(node,null,name);
  }


  @Override
  public boolean hasAttributeNS(String namespaceURI, String localName)
  {
    return DTM.NULL != dtm.getAttributeNode(node,namespaceURI,localName);
  }


  @Override
  public final Document getOwnerDocument()
  {
        return (Document)(dtm.getNode(dtm.getOwnerDocument(node)));
  }


  @Override
  public final Node insertBefore(Node newChild, Node refChild)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final Node replaceChild(Node newChild, Node oldChild)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final Node removeChild(Node oldChild) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final Node appendChild(Node newChild) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final boolean hasChildNodes()
  {
    return (DTM.NULL != dtm.getFirstChild(node));
  }


  @Override
  public final Node cloneNode(boolean deep)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final DocumentType getDoctype()
  {
    return null;
  }


  @Override
  public final DOMImplementation getImplementation()
  {
    return implementation;
  }


  @Override
  public final Element getDocumentElement()
  {
                int dochandle=dtm.getDocument();
                int elementhandle=DTM.NULL;
                for(int kidhandle=dtm.getFirstChild(dochandle);
                                kidhandle!=DTM.NULL;
                                kidhandle=dtm.getNextSibling(kidhandle))
                {
                        switch(dtm.getNodeType(kidhandle))
                        {
                        case Node.ELEMENT_NODE:
                                if(elementhandle!=DTM.NULL)
                                {
                                        elementhandle=DTM.NULL; kidhandle=dtm.getLastChild(dochandle); }
                                else
                                        elementhandle=kidhandle;
                                break;

                        case Node.COMMENT_NODE:
                        case Node.PROCESSING_INSTRUCTION_NODE:
                        case Node.DOCUMENT_TYPE_NODE:
                                break;

                        default:
                                elementhandle=DTM.NULL; kidhandle=dtm.getLastChild(dochandle); break;
                        }
                }
                if(elementhandle==DTM.NULL)
                        throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
                else
                        return (Element)(dtm.getNode(elementhandle));
  }


  @Override
  public final Element createElement(String tagName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final DocumentFragment createDocumentFragment()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Text createTextNode(String data)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Comment createComment(String data)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final CDATASection createCDATASection(String data)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final ProcessingInstruction createProcessingInstruction(
                                String target, String data) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Attr createAttribute(String name) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final EntityReference createEntityReference(String name)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  @Override
  public final NodeList getElementsByTagName(String tagname)
  {
       Vector listVector = new Vector();
       Node retNode = dtm.getNode(node);
       if (retNode != null)
       {
         boolean isTagNameWildCard = "*".equals(tagname);
         if (DTM.ELEMENT_NODE == retNode.getNodeType())
         {
           NodeList nodeList = retNode.getChildNodes();
           for (int i = 0; i < nodeList.getLength(); i++)
           {
             traverseChildren(listVector, nodeList.item(i), tagname,
                              isTagNameWildCard);
           }
         } else if (DTM.DOCUMENT_NODE == retNode.getNodeType()) {
           traverseChildren(listVector, dtm.getNode(node), tagname,
                            isTagNameWildCard);
         }
       }
       int size = listVector.size();
       NodeSet nodeSet = new NodeSet(size);
       for (int i = 0; i < size; i++)
       {
         nodeSet.addNode((Node) listVector.elementAt(i));
       }
       return (NodeList) nodeSet;
  }


  private final void traverseChildren
  (
    Vector listVector,
    Node tempNode,
    String tagname,
    boolean isTagNameWildCard) {
    if (tempNode == null)
    {
      return;
    }
    else
    {
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
            && (isTagNameWildCard || tempNode.getNodeName().equals(tagname)))
      {
        listVector.add(tempNode);
      }
      if(tempNode.hasChildNodes())
      {
        NodeList nodeList = tempNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
          traverseChildren(listVector, nodeList.item(i), tagname,
                           isTagNameWildCard);
        }
      }
    }
  }




  @Override
  public final Node importNode(Node importedNode, boolean deep)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  @Override
  public final Element createElementNS(
                 String namespaceURI, String qualifiedName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Attr createAttributeNS(
                  String namespaceURI, String qualifiedName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final NodeList getElementsByTagNameNS(String namespaceURI,
                                               String localName)
  {
    Vector listVector = new Vector();
    Node retNode = dtm.getNode(node);
    if (retNode != null)
    {
      boolean isNamespaceURIWildCard = "*".equals(namespaceURI);
      boolean isLocalNameWildCard    = "*".equals(localName);
      if (DTM.ELEMENT_NODE == retNode.getNodeType())
      {
        NodeList nodeList = retNode.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++)
        {
          traverseChildren(listVector, nodeList.item(i), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
        }
      }
      else if(DTM.DOCUMENT_NODE == retNode.getNodeType())
      {
        traverseChildren(listVector, dtm.getNode(node), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
      }
    }
    int size = listVector.size();
    NodeSet nodeSet = new NodeSet(size);
    for (int i = 0; i < size; i++)
    {
      nodeSet.addNode((Node)listVector.elementAt(i));
    }
    return (NodeList) nodeSet;
  }

  private final void traverseChildren
  (
   Vector listVector,
   Node tempNode,
   String namespaceURI,
   String localname,
   boolean isNamespaceURIWildCard,
   boolean isLocalNameWildCard)
   {
    if (tempNode == null)
    {
      return;
    }
    else
    {
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
              && (isLocalNameWildCard
                      || tempNode.getLocalName().equals(localname)))
      {
        String nsURI = tempNode.getNamespaceURI();
        if ((namespaceURI == null && nsURI == null)
               || isNamespaceURIWildCard
               || (namespaceURI != null && namespaceURI.equals(nsURI)))
        {
          listVector.add(tempNode);
        }
      }
      if(tempNode.hasChildNodes())
      {
        NodeList nl = tempNode.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++)
        {
          traverseChildren(listVector, nl.item(i), namespaceURI, localname,
                           isNamespaceURIWildCard, isLocalNameWildCard);
        }
      }
    }
  }

  @Override
  public final Element getElementById(String elementId)
  {
       return (Element) dtm.getNode(dtm.getElementById(elementId));
  }


  @Override
  public final Text splitText(int offset) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final String getData() throws DOMException
  {
    return dtm.getNodeValue(node);
  }


  @Override
  public final void setData(String data) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final int getLength()
  {
    return dtm.getNodeValue(node).length();
  }


  @Override
  public final String substringData(int offset, int count) throws DOMException
  {
    return getData().substring(offset,offset+count);
  }


  @Override
  public final void appendData(String arg) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final void insertData(int offset, String arg) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final void deleteData(int offset, int count) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final void replaceData(int offset, int count, String arg)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final String getTagName()
  {
    return dtm.getNodeName(node);
  }


  @Override
  public final String getAttribute(String name)
  {
    DTMNamedNodeMap  map = new DTMNamedNodeMap(dtm, node);
    Node n = map.getNamedItem(name);
    return (null == n) ? EMPTYSTRING : n.getNodeValue();
  }


  @Override
  public final void setAttribute(String name, String value)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final void removeAttribute(String name) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Attr getAttributeNode(String name)
  {
    DTMNamedNodeMap  map = new DTMNamedNodeMap(dtm, node);
    return (Attr)map.getNamedItem(name);
  }


  @Override
  public final Attr setAttributeNode(Attr newAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Attr removeAttributeNode(Attr oldAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public boolean hasAttributes()
  {
    return DTM.NULL != dtm.getFirstAttribute(node);
  }


  @Override
  public final void normalize()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final String getAttributeNS(String namespaceURI, String localName)
  {
    Node retNode = null;
    int n = dtm.getAttributeNode(node,namespaceURI,localName);
    if(n != DTM.NULL)
            retNode = dtm.getNode(n);
    return (null == retNode) ? EMPTYSTRING : retNode.getNodeValue();
  }


  @Override
  public final void setAttributeNS(
                                   String namespaceURI, String qualifiedName, String value)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final void removeAttributeNS(String namespaceURI, String localName)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Attr getAttributeNodeNS(String namespaceURI, String localName)
  {
       Attr retAttr = null;
       int n = dtm.getAttributeNode(node,namespaceURI,localName);
       if(n != DTM.NULL)
               retAttr = (Attr) dtm.getNode(n);
       return retAttr;

  }


  @Override
  public final Attr setAttributeNodeNS(Attr newAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final String getName()
  {
    return dtm.getNodeName(node);
  }


  @Override
  public final boolean getSpecified()
  {
    return true;
  }


  @Override
  public final String getValue()
  {
    return dtm.getNodeValue(node);
  }


  @Override
  public final void setValue(String value)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public final Element getOwnerElement()
  {
    if (getNodeType() != Node.ATTRIBUTE_NODE)
      return null;
    int newnode = dtm.getParent(node);
    return (newnode == DTM.NULL) ? null : (Element)(dtm.getNode(newnode));
  }


  @Override
  public Node adoptNode(Node source) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public String getInputEncoding()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  public void setEncoding(String encoding)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  public boolean getStandalone()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  public void setStandalone(boolean standalone)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public boolean getStrictErrorChecking()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  @Override
  public void setStrictErrorChecking(boolean strictErrorChecking)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  public String getVersion()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }


  public void setVersion(String version)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }



  static class DTMNodeProxyImplementation implements DOMImplementation
  {
    @Override
    public DocumentType createDocumentType(String qualifiedName,String publicId, String systemId)
    {
      throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
    }
    @Override
    public Document createDocument(String namespaceURI,String qualfiedName,DocumentType doctype)
    {
      throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
    }

    @Override
    public boolean hasFeature(String feature,String version)
    {
      if( ("CORE".equals(feature.toUpperCase()) || "XML".equals(feature.toUpperCase()))
                                        &&
          ("1.0".equals(version) || "2.0".equals(version)))
        return true;
      return false;
    }


    @Override
    public Object getFeature(String feature, String version) {
        return null; }

  }


@Override
    public Object setUserData(String key,
                              Object data,
                              UserDataHandler handler) {
        return getOwnerDocument().setUserData( key, data, handler);
    }


    @Override
    public Object getUserData(String key) {
        return getOwnerDocument().getUserData( key);
    }


    @Override
    public Object getFeature(String feature, String version) {
        return isSupported(feature, version) ? this : null;
    }


    @Override
    public boolean isEqualNode(Node arg) {
        if (arg == this) {
            return true;
        }
        if (arg.getNodeType() != getNodeType()) {
            return false;
        }
        if (getNodeName() == null) {
            if (arg.getNodeName() != null) {
                return false;
            }
        }
        else if (!getNodeName().equals(arg.getNodeName())) {
            return false;
        }

        if (getLocalName() == null) {
            if (arg.getLocalName() != null) {
                return false;
            }
        }
        else if (!getLocalName().equals(arg.getLocalName())) {
            return false;
        }

        if (getNamespaceURI() == null) {
            if (arg.getNamespaceURI() != null) {
                return false;
            }
        }
        else if (!getNamespaceURI().equals(arg.getNamespaceURI())) {
            return false;
        }

        if (getPrefix() == null) {
            if (arg.getPrefix() != null) {
                return false;
            }
        }
        else if (!getPrefix().equals(arg.getPrefix())) {
            return false;
        }

        if (getNodeValue() == null) {
            if (arg.getNodeValue() != null) {
                return false;
            }
        }
        else if (!getNodeValue().equals(arg.getNodeValue())) {
            return false;
        }


             return true;
    }


    @Override
    public String lookupNamespaceURI(String specifiedPrefix) {
        short type = this.getNodeType();
        switch (type) {
        case Node.ELEMENT_NODE : {

                String namespace = this.getNamespaceURI();
                String prefix = this.getPrefix();
                if (namespace !=null) {
                    if (specifiedPrefix== null && prefix==specifiedPrefix) {
                        return namespace;
                    } else if (prefix != null && prefix.equals(specifiedPrefix)) {
                        return namespace;
                    }
                }
                if (this.hasAttributes()) {
                    NamedNodeMap map = this.getAttributes();
                    int length = map.getLength();
                    for (int i=0;i<length;i++) {
                        Node attr = map.item(i);
                        String attrPrefix = attr.getPrefix();
                        String value = attr.getNodeValue();
                        namespace = attr.getNamespaceURI();
                        if (namespace !=null && namespace.equals("http:if (specifiedPrefix == null &&
                                attr.getNodeName().equals("xmlns")) {
                                return value;
                            } else if (attrPrefix !=null &&
                                       attrPrefix.equals("xmlns") &&
                                       attr.getLocalName().equals(specifiedPrefix)) {
                 return value;
                            }
                        }
                    }
                }


                return null;


            }

        case Node.ENTITY_NODE :
        case Node.NOTATION_NODE:
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
            return null;
        case Node.ATTRIBUTE_NODE:{
                if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
                    return getOwnerElement().lookupNamespaceURI(specifiedPrefix);

                }
                return null;
            }
        default:{

                return null;
            }

        }
    }



    @Override
    public boolean isDefaultNamespace(String namespaceURI){

        return false;


    }


    @Override
    public String lookupPrefix(String namespaceURI){

        if (namespaceURI == null) {
            return null;
        }

        short type = this.getNodeType();

        switch (type) {

        case Node.ENTITY_NODE :
        case Node.NOTATION_NODE:
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
            return null;
        case Node.ATTRIBUTE_NODE:{
                if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
                    return getOwnerElement().lookupPrefix(namespaceURI);

                }
                return null;
            }
        default:{

                return null;
            }
         }
    }


    @Override
    public boolean isSameNode(Node other) {
        return this == other;
    }


    @Override
    public void setTextContent(String textContent)
        throws DOMException {
        setNodeValue(textContent);
    }

    @Override
    public String getTextContent() throws DOMException {
        return dtm.getStringValue(node).toString();
    }


    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }


    @Override
    public String getBaseURI() {
        return null;
    }


    @Override
    public Node renameNode(Node n,
                           String namespaceURI,
                           String name)
                           throws DOMException{
        return n;
    }



    @Override
    public void normalizeDocument(){

    }

    @Override
    public DOMConfiguration getDomConfig(){
       return null;
    }



    protected String fDocumentURI;


    @Override
    public void setDocumentURI(String documentURI){
        fDocumentURI= documentURI;
    }


    @Override
    public String getDocumentURI(){
        return fDocumentURI;
    }


    protected String actualEncoding;


    public String getActualEncoding() {
        return actualEncoding;
    }


    public void setActualEncoding(String value) {
        actualEncoding = value;
    }


    @Override
    public Text replaceWholeText(String content)
                                 throws DOMException{

        return null; }


    @Override
    public String getWholeText(){


        return null; }


    @Override
    public boolean isElementContentWhitespace(){
        return false;
    }


    public void setIdAttribute(boolean id){
        }


    @Override
    public void setIdAttribute(String name, boolean makeId) {
        }



    @Override
    public void setIdAttributeNode(Attr at, boolean makeId) {
        }


    @Override
    public void setIdAttributeNS(String namespaceURI, String localName,
                                    boolean makeId) {
        }

    @Override
    public TypeInfo getSchemaTypeInfo(){
      return null; }

    @Override
    public boolean isId() {
        return false; }


    private String xmlEncoding;
    @Override
    public String getXmlEncoding( ) {
        return xmlEncoding;
    }
    public void setXmlEncoding( String xmlEncoding ) {
        this.xmlEncoding = xmlEncoding;
    }

    private boolean xmlStandalone;
    @Override
    public boolean getXmlStandalone() {
        return xmlStandalone;
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        this.xmlStandalone = xmlStandalone;
    }

    private String xmlVersion;
    @Override
    public String getXmlVersion() {
        return xmlVersion;
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        this.xmlVersion = xmlVersion;
    }

}
