


package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import com.sun.org.apache.xml.internal.res.XMLErrorResources;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import com.sun.org.apache.xml.internal.utils.TreeWalker;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;


public class DOM2DTM extends DTMDefaultBaseIterators
{
  static final boolean JJK_DEBUG=false;
  static final boolean JJK_NEWCODE=true;


  static final String NAMESPACE_DECL_NS="http:transient private Node m_pos;

  private int m_last_parent=0;

  private int m_last_kid=NULL;


  transient private Node m_root;


  boolean m_processedFirstElement=false;


  transient private boolean m_nodesAreProcessed;


  protected Vector m_nodes = new Vector();


  public DOM2DTM(DTMManager mgr, DOMSource domSource,
                 int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing)
  {
    super(mgr, domSource, dtmIdentity, whiteSpaceFilter,
          xstringfactory, doIndexing);

    m_pos=m_root = domSource.getNode();
    m_last_parent=m_last_kid=NULL;
    m_last_kid=addNode(m_root, m_last_parent,m_last_kid, NULL);

    if(ELEMENT_NODE == m_root.getNodeType())
    {
      NamedNodeMap attrs=m_root.getAttributes();
      int attrsize=(attrs==null) ? 0 : attrs.getLength();
      if(attrsize>0)
      {
        int attrIndex=NULL; for(int i=0;i<attrsize;++i)
        {
          attrIndex=addNode(attrs.item(i),0,attrIndex,NULL);
          m_firstch.setElementAt(DTM.NULL,attrIndex);
        }
        m_nextsib.setElementAt(DTM.NULL,attrIndex);

        } } m_nodesAreProcessed = false;
  }


  protected int addNode(Node node, int parentIndex,
                        int previousSibling, int forceNodeType)
  {
    int nodeIndex = m_nodes.size();

    if(m_dtmIdent.size() == (nodeIndex>>>DTMManager.IDENT_DTM_NODE_BITS))
    {
      try
      {
        if(m_mgr==null)
          throw new ClassCastException();

                                DTMManagerDefault mgrD=(DTMManagerDefault)m_mgr;
        int id=mgrD.getFirstFreeDTMID();
        mgrD.addDTM(this,id,nodeIndex);
        m_dtmIdent.addElement(id<<DTMManager.IDENT_DTM_NODE_BITS);
      }
      catch(ClassCastException e)
      {
        error(XMLMessages.createXMLMessage(XMLErrorResources.ER_NO_DTMIDS_AVAIL, null));}
    }

    m_size++;
    int type;
    if(NULL==forceNodeType)
        type = node.getNodeType();
    else
        type=forceNodeType;

    if (Node.ATTRIBUTE_NODE == type)
    {
      String name = node.getNodeName();

      if (name.startsWith("xmlns:") || name.equals("xmlns"))
      {
        type = DTM.NAMESPACE_NODE;
      }
    }

    m_nodes.addElement(node);

    m_firstch.setElementAt(NOTPROCESSED,nodeIndex);
    m_nextsib.setElementAt(NOTPROCESSED,nodeIndex);
    m_prevsib.setElementAt(previousSibling,nodeIndex);
    m_parent.setElementAt(parentIndex,nodeIndex);

    if(DTM.NULL != parentIndex &&
       type != DTM.ATTRIBUTE_NODE &&
       type != DTM.NAMESPACE_NODE)
    {
      if(NOTPROCESSED == m_firstch.elementAt(parentIndex))
        m_firstch.setElementAt(nodeIndex,parentIndex);
    }

    String nsURI = node.getNamespaceURI();

    String localName =  (type == Node.PROCESSING_INSTRUCTION_NODE) ?
                         node.getNodeName() :
                         node.getLocalName();

    if(((type == Node.ELEMENT_NODE) || (type == Node.ATTRIBUTE_NODE))
        && null == localName)
      localName = node.getNodeName(); ExpandedNameTable exnt = m_expandedNameTable;

    if(node.getLocalName()==null &&
       (type==Node.ELEMENT_NODE || type==Node.ATTRIBUTE_NODE))
      {
        }

    int expandedNameID = (null != localName)
       ? exnt.getExpandedTypeID(nsURI, localName, type) :
         exnt.getExpandedTypeID(type);

    m_exptype.setElementAt(expandedNameID,nodeIndex);

    indexNode(expandedNameID, nodeIndex);

    if (DTM.NULL != previousSibling)
      m_nextsib.setElementAt(nodeIndex,previousSibling);

    if (type == DTM.NAMESPACE_NODE)
        declareNamespaceInContext(parentIndex,nodeIndex);

    return nodeIndex;
  }


  public int getNumberOfNodes()
  {
    return m_nodes.size();
  }


  protected boolean nextNode()
  {
    if (m_nodesAreProcessed)
      return false;

    Node pos=m_pos;
    Node next=null;
    int nexttype=NULL;

    do
      {
        if (pos.hasChildNodes())
          {
            next = pos.getFirstChild();

            if(next!=null && DOCUMENT_TYPE_NODE==next.getNodeType())
              next=next.getNextSibling();

            if(ENTITY_REFERENCE_NODE!=pos.getNodeType())
              {
                m_last_parent=m_last_kid;
                m_last_kid=NULL;
                if(null != m_wsfilter)
                {
                  short wsv =
                    m_wsfilter.getShouldStripSpace(makeNodeHandle(m_last_parent),this);
                  boolean shouldStrip = (DTMWSFilter.INHERIT == wsv)
                    ? getShouldStripWhitespace()
                    : (DTMWSFilter.STRIP == wsv);
                  pushShouldStripWhitespace(shouldStrip);
                } }
          }

        else
          {
            if(m_last_kid!=NULL)
              {
                if(m_firstch.elementAt(m_last_kid)==NOTPROCESSED)
                  m_firstch.setElementAt(NULL,m_last_kid);
              }

            while(m_last_parent != NULL)
              {
                next = pos.getNextSibling();
                if(next!=null && DOCUMENT_TYPE_NODE==next.getNodeType())
                  next=next.getNextSibling();

                if(next!=null)
                  break; pos=pos.getParentNode();
                if(pos==null)
                  {
                    if(JJK_DEBUG)
                      {
                        System.out.println("***** DOM2DTM Pop Control Flow problem");
                        for(;;); }
                  }

                if(pos!=null && ENTITY_REFERENCE_NODE == pos.getNodeType())
                  {
                    if(JJK_DEBUG)
                      System.out.println("***** DOM2DTM popping EntRef");
                  }
                else
                  {
                    popShouldStripWhitespace();
                    if(m_last_kid==NULL)
                      m_firstch.setElementAt(NULL,m_last_parent); else
                      m_nextsib.setElementAt(NULL,m_last_kid); m_last_parent=m_parent.elementAt(m_last_kid=m_last_parent);
                  }
              }
            if(m_last_parent==NULL)
              next=null;
          }

        if(next!=null)
          nexttype=next.getNodeType();

        if (ENTITY_REFERENCE_NODE == nexttype)
          pos=next;
      }
    while (ENTITY_REFERENCE_NODE == nexttype);

    if(next==null)
      {
        m_nextsib.setElementAt(NULL,0);
        m_nodesAreProcessed = true;
        m_pos=null;

        if(JJK_DEBUG)
          {
            System.out.println("***** DOM2DTM Crosscheck:");
            for(int i=0;i<m_nodes.size();++i)
              System.out.println(i+":\t"+m_firstch.elementAt(i)+"\t"+m_nextsib.elementAt(i));
          }

        return false;
      }

    boolean suppressNode=false;
    Node lastTextNode=null;

    nexttype=next.getNodeType();

    if(TEXT_NODE == nexttype || CDATA_SECTION_NODE == nexttype)
      {
        suppressNode=((null != m_wsfilter) && getShouldStripWhitespace());

        Node n=next;
        while(n!=null)
          {
            lastTextNode=n;
            if(TEXT_NODE == n.getNodeType())
              nexttype=TEXT_NODE;
            suppressNode &=
              XMLCharacterRecognizer.isWhiteSpace(n.getNodeValue());

            n=logicalNextDOMTextNode(n);
          }
      }

    else if(PROCESSING_INSTRUCTION_NODE==nexttype)
      {
        suppressNode = (pos.getNodeName().toLowerCase().equals("xml"));
      }


    if(!suppressNode)
      {
        int nextindex=addNode(next,m_last_parent,m_last_kid,
                              nexttype);

        m_last_kid=nextindex;

        if(ELEMENT_NODE == nexttype)
          {
            int attrIndex=NULL; NamedNodeMap attrs=next.getAttributes();
            int attrsize=(attrs==null) ? 0 : attrs.getLength();
            if(attrsize>0)
              {
                for(int i=0;i<attrsize;++i)
                  {
                    attrIndex=addNode(attrs.item(i),
                                      nextindex,attrIndex,NULL);
                    m_firstch.setElementAt(DTM.NULL,attrIndex);

                    if(!m_processedFirstElement
                       && "xmlns:xml".equals(attrs.item(i).getNodeName()))
                      m_processedFirstElement=true;
                  }
                } if(!m_processedFirstElement)
            {
              attrIndex=addNode(new DOM2DTMdefaultNamespaceDeclarationNode(
                                                                                                                                        (Element)next,"xml",NAMESPACE_DECL_NS,
                                                                                                                                        makeNodeHandle(((attrIndex==NULL)?nextindex:attrIndex)+1)
                                                                                                                                        ),
                                nextindex,attrIndex,NULL);
              m_firstch.setElementAt(DTM.NULL,attrIndex);
              m_processedFirstElement=true;
            }
            if(attrIndex!=NULL)
              m_nextsib.setElementAt(DTM.NULL,attrIndex);
          } } if(TEXT_NODE == nexttype || CDATA_SECTION_NODE == nexttype)
      {
        next=lastTextNode;      }

    m_pos=next;
    return true;
  }



  public Node getNode(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);

    return (Node) m_nodes.elementAt(identity);
  }


  protected Node lookupNode(int nodeIdentity)
  {
    return (Node) m_nodes.elementAt(nodeIdentity);
  }


  protected int getNextNodeIdentity(int identity)
  {

    identity += 1;

    if (identity >= m_nodes.size())
    {
      if (!nextNode())
        identity = DTM.NULL;
    }

    return identity;
  }


  private int getHandleFromNode(Node node)
  {
    if (null != node)
    {
      int len = m_nodes.size();
      boolean isMore;
      int i = 0;
      do
      {
        for (; i < len; i++)
        {
          if (m_nodes.elementAt(i) == node)
            return makeNodeHandle(i);
        }

        isMore = nextNode();

        len = m_nodes.size();

      }
      while(isMore || i < len);
    }

    return DTM.NULL;
  }


  public int getHandleOfNode(Node node)
  {
    if (null != node)
    {
      if((m_root==node) ||
         (m_root.getNodeType()==DOCUMENT_NODE &&
          m_root==node.getOwnerDocument()) ||
         (m_root.getNodeType()!=DOCUMENT_NODE &&
          m_root.getOwnerDocument()==node.getOwnerDocument())
         )
        {
          for(Node cursor=node;
              cursor!=null;
              cursor=
                (cursor.getNodeType()!=ATTRIBUTE_NODE)
                ? cursor.getParentNode()
                : ((org.w3c.dom.Attr)cursor).getOwnerElement())
            {
              if(cursor==m_root)
                return getHandleFromNode(node);
            } } } return DTM.NULL;
  }


  public int getAttributeNode(int nodeHandle, String namespaceURI,
                              String name)
  {

    if (null == namespaceURI)
      namespaceURI = "";

    int type = getNodeType(nodeHandle);

    if (DTM.ELEMENT_NODE == type)
    {

      int identity = makeNodeIdentity(nodeHandle);

      while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
      {
        type = _type(identity);

                                if (type == DTM.ATTRIBUTE_NODE || type==DTM.NAMESPACE_NODE)
        {
          Node node = lookupNode(identity);
          String nodeuri = node.getNamespaceURI();

          if (null == nodeuri)
            nodeuri = "";

          String nodelocalname = node.getLocalName();

          if (nodeuri.equals(namespaceURI) && name.equals(nodelocalname))
            return makeNodeHandle(identity);
        }

        else {
          break;
        }
      }
    }

    return DTM.NULL;
  }


  public XMLString getStringValue(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);
    Node node = getNode(nodeHandle);
    if(DTM.ELEMENT_NODE == type || DTM.DOCUMENT_NODE == type
    || DTM.DOCUMENT_FRAGMENT_NODE == type)
    {
      FastStringBuffer buf = StringBufferPool.get();
      String s;

      try
      {
        getNodeData(node, buf);

        s = (buf.length() > 0) ? buf.toString() : "";
      }
      finally
      {
        StringBufferPool.free(buf);
      }

      return m_xstrf.newstr( s );
    }
    else if(TEXT_NODE == type || CDATA_SECTION_NODE == type)
    {
      FastStringBuffer buf = StringBufferPool.get();
      while(node!=null)
      {
        buf.append(node.getNodeValue());
        node=logicalNextDOMTextNode(node);
      }
      String s=(buf.length() > 0) ? buf.toString() : "";
      StringBufferPool.free(buf);
      return m_xstrf.newstr( s );
    }
    else
      return m_xstrf.newstr( node.getNodeValue() );
  }


  public boolean isWhitespace(int nodeHandle)
  {
        int type = getNodeType(nodeHandle);
    Node node = getNode(nodeHandle);
        if(TEXT_NODE == type || CDATA_SECTION_NODE == type)
    {
      FastStringBuffer buf = StringBufferPool.get();
      while(node!=null)
      {
        buf.append(node.getNodeValue());
        node=logicalNextDOMTextNode(node);
      }
     boolean b = buf.isWhitespace(0, buf.length());
      StringBufferPool.free(buf);
     return b;
    }
    return false;
  }


  protected static void getNodeData(Node node, FastStringBuffer buf)
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        getNodeData(child, buf);
      }
    }
    break;
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
    case Node.ATTRIBUTE_NODE :  buf.append(node.getNodeValue());
      break;
    case Node.PROCESSING_INSTRUCTION_NODE :
      break;
    default :
      break;
    }
  }


  public String getNodeName(int nodeHandle)
  {

    Node node = getNode(nodeHandle);

    return node.getNodeName();
  }


  public String getNodeNameX(int nodeHandle)
  {

    String name;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    {
      Node node = getNode(nodeHandle);

      name = node.getNodeName();
      if(name.startsWith("xmlns:"))
      {
        name = QName.getLocalPart(name);
      }
      else if(name.equals("xmlns"))
      {
        name = "";
      }
    }
    break;
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.PROCESSING_INSTRUCTION_NODE :
    {
      Node node = getNode(nodeHandle);

      name = node.getNodeName();
    }
    break;
    default :
      name = "";
    }

    return name;
  }


  public String getLocalName(int nodeHandle)
  {
    if(JJK_NEWCODE)
    {
      int id=makeNodeIdentity(nodeHandle);
      if(NULL==id) return null;
      Node newnode=(Node)m_nodes.elementAt(id);
      String newname=newnode.getLocalName();
      if (null == newname)
      {
        String qname = newnode.getNodeName();
        if('#'==qname.charAt(0))
        {
          newname="";
        }
        else
        {
          int index = qname.indexOf(':');
          newname = (index < 0) ? qname : qname.substring(index + 1);
        }
      }
      return newname;
    }
    else
    {
      String name;
      short type = getNodeType(nodeHandle);
      switch (type)
      {
      case DTM.ATTRIBUTE_NODE :
      case DTM.ELEMENT_NODE :
      case DTM.ENTITY_REFERENCE_NODE :
      case DTM.NAMESPACE_NODE :
      case DTM.PROCESSING_INSTRUCTION_NODE :
        {
          Node node = getNode(nodeHandle);

          name = node.getLocalName();

          if (null == name)
          {
            String qname = node.getNodeName();
            int index = qname.indexOf(':');

            name = (index < 0) ? qname : qname.substring(index + 1);
          }
        }
        break;
      default :
        name = "";
      }
      return name;
    }
  }


  public String getPrefix(int nodeHandle)
  {

    String prefix;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    {
      Node node = getNode(nodeHandle);

      String qname = node.getNodeName();
      int index = qname.indexOf(':');

      prefix = (index < 0) ? "" : qname.substring(index + 1);
    }
    break;
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    {
      Node node = getNode(nodeHandle);

      String qname = node.getNodeName();
      int index = qname.indexOf(':');

      prefix = (index < 0) ? "" : qname.substring(0, index);
    }
    break;
    default :
      prefix = "";
    }

    return prefix;
  }


  public String getNamespaceURI(int nodeHandle)
  {
    if(JJK_NEWCODE)
    {
      int id=makeNodeIdentity(nodeHandle);
      if(id==NULL) return null;
      Node node=(Node)m_nodes.elementAt(id);
      return node.getNamespaceURI();
    }
    else
    {
      String nsuri;
      short type = getNodeType(nodeHandle);

      switch (type)
      {
      case DTM.ATTRIBUTE_NODE :
      case DTM.ELEMENT_NODE :
      case DTM.ENTITY_REFERENCE_NODE :
      case DTM.NAMESPACE_NODE :
      case DTM.PROCESSING_INSTRUCTION_NODE :
        {
          Node node = getNode(nodeHandle);

          nsuri = node.getNamespaceURI();

          }
        break;
      default :
        nsuri = null;
      }

      return nsuri;
    }

  }


  private Node logicalNextDOMTextNode(Node n)
  {
        Node p=n.getNextSibling();
        if(p==null)
        {
                for(n=n.getParentNode();
                        n!=null && ENTITY_REFERENCE_NODE == n.getNodeType();
                        n=n.getParentNode())
                {
                        p=n.getNextSibling();
                        if(p!=null)
                                break;
                }
        }
        n=p;
        while(n!=null && ENTITY_REFERENCE_NODE == n.getNodeType())
        {
                if(n.hasChildNodes())
                        n=n.getFirstChild();
                else
                        n=n.getNextSibling();
        }
        if(n!=null)
        {
                int ntype=n.getNodeType();
                if(TEXT_NODE != ntype && CDATA_SECTION_NODE != ntype)
                        n=null;
        }
        return n;
  }


  public String getNodeValue(int nodeHandle)
  {
    int type = _exptype(makeNodeIdentity(nodeHandle));
    type=(NULL != type) ? getNodeType(nodeHandle) : NULL;

    if(TEXT_NODE!=type && CDATA_SECTION_NODE!=type)
      return getNode(nodeHandle).getNodeValue();

    Node node = getNode(nodeHandle);
    Node n=logicalNextDOMTextNode(node);
    if(n==null)
      return node.getNodeValue();

    FastStringBuffer buf = StringBufferPool.get();
        buf.append(node.getNodeValue());
    while(n!=null)
    {
      buf.append(n.getNodeValue());
      n=logicalNextDOMTextNode(n);
    }
    String s = (buf.length() > 0) ? buf.toString() : "";
    StringBufferPool.free(buf);
    return s;
  }


  public String getDocumentTypeDeclarationSystemIdentifier()
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getSystemId();
      }
    }

    return null;
  }


  public String getDocumentTypeDeclarationPublicIdentifier()
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getPublicId();
      }
    }

    return null;
  }


  public int getElementById(String elementId)
  {

    Document doc = (m_root.getNodeType() == Node.DOCUMENT_NODE)
        ? (Document) m_root : m_root.getOwnerDocument();

    if(null != doc)
    {
      Node elem = doc.getElementById(elementId);
      if(null != elem)
      {
        int elemHandle = getHandleFromNode(elem);

        if(DTM.NULL == elemHandle)
        {
          int identity = m_nodes.size()-1;
          while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
          {
            Node node = getNode(identity);
            if(node == elem)
            {
              elemHandle = getHandleFromNode(elem);
              break;
            }
           }
        }

        return elemHandle;
      }

    }
    return DTM.NULL;
  }


  public String getUnparsedEntityURI(String name)
  {

    String url = "";
    Document doc = (m_root.getNodeType() == Node.DOCUMENT_NODE)
        ? (Document) m_root : m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType doctype = doc.getDoctype();

      if (null != doctype)
      {
        NamedNodeMap entities = doctype.getEntities();
        if(null == entities)
          return url;
        Entity entity = (Entity) entities.getNamedItem(name);
        if(null == entity)
          return url;

        String notationName = entity.getNotationName();

        if (null != notationName)  {
          url = entity.getSystemId();

          if (null == url)
          {
            url = entity.getPublicId();
          }
          else
          {
            }
        }
      }
    }

    return url;
  }


  public boolean isAttributeSpecified(int attributeHandle)
  {
    int type = getNodeType(attributeHandle);

    if (DTM.ATTRIBUTE_NODE == type)
    {
      Attr attr = (Attr)getNode(attributeHandle);
      return attr.getSpecified();
    }
    return false;
  }


  public void setIncrementalSAXSource(IncrementalSAXSource source)
  {
  }


  public org.xml.sax.ContentHandler getContentHandler()
  {
      return null;
  }


  public org.xml.sax.ext.LexicalHandler getLexicalHandler()
  {

    return null;
  }



  public org.xml.sax.EntityResolver getEntityResolver()
  {

    return null;
  }


  public org.xml.sax.DTDHandler getDTDHandler()
  {

    return null;
  }


  public org.xml.sax.ErrorHandler getErrorHandler()
  {

    return null;
  }


  public org.xml.sax.ext.DeclHandler getDeclHandler()
  {

    return null;
  }


  public boolean needsTwoThreads()
  {
    return false;
  }

  private static boolean isSpace(char ch)
  {
    return XMLCharacterRecognizer.isWhiteSpace(ch);  }


  public void dispatchCharactersEvents(
          int nodeHandle, org.xml.sax.ContentHandler ch,
          boolean normalize)
            throws org.xml.sax.SAXException
  {
    if(normalize)
    {
      XMLString str = getStringValue(nodeHandle);
      str = str.fixWhiteSpace(true, true, false);
      str.dispatchCharactersEvents(ch);
    }
    else
    {
      int type = getNodeType(nodeHandle);
      Node node = getNode(nodeHandle);
      dispatchNodeData(node, ch, 0);
          if(TEXT_NODE == type || CDATA_SECTION_NODE == type)
          {
                  while( null != (node=logicalNextDOMTextNode(node)) )
                  {
                      dispatchNodeData(node, ch, 0);
                  }
          }
    }
  }


  protected static void dispatchNodeData(Node node,
                                         org.xml.sax.ContentHandler ch,
                                         int depth)
            throws org.xml.sax.SAXException
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        dispatchNodeData(child, ch, depth+1);
      }
    }
    break;
    case Node.PROCESSING_INSTRUCTION_NODE : case Node.COMMENT_NODE :
      if(0 != depth)
        break;
        case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
    case Node.ATTRIBUTE_NODE :
      String str = node.getNodeValue();
      if(ch instanceof CharacterNodeHandler)
      {
        ((CharacterNodeHandler)ch).characters(node);
      }
      else
      {
        ch.characters(str.toCharArray(), 0, str.length());
      }
      break;
default :
      break;
    }
  }

  TreeWalker m_walker = new TreeWalker(null);


  public void dispatchToEvents(int nodeHandle, org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    TreeWalker treeWalker = m_walker;
    ContentHandler prevCH = treeWalker.getContentHandler();

    if(null != prevCH)
    {
      treeWalker = new TreeWalker(null);
    }
    treeWalker.setContentHandler(ch);

    try
    {
      Node node = getNode(nodeHandle);
      treeWalker.traverseFragment(node);
    }
    finally
    {
      treeWalker.setContentHandler(null);
    }
  }

  public interface CharacterNodeHandler
  {
    public void characters(Node node)
            throws org.xml.sax.SAXException;
  }


  public void setProperty(String property, Object value)
  {
  }


  public SourceLocator getSourceLocatorFor(int node)
  {
    return null;
  }

}