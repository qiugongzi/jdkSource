


package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM.CharacterNodeHandler;
import javax.xml.transform.Result;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;



public class TreeWalker
{


  private ContentHandler m_contentHandler = null;


   private LocatorImpl m_locator = new LocatorImpl();


  public ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }


  public void setContentHandler(ContentHandler ch)
  {
    m_contentHandler = ch;
  }


  public TreeWalker(ContentHandler contentHandler, String systemId)
  {
    this.m_contentHandler = contentHandler;
    if (m_contentHandler != null) {
        m_contentHandler.setDocumentLocator(m_locator);
    }
    if (systemId != null) {
        m_locator.setSystemId(systemId);
    }
  }


  public TreeWalker(ContentHandler contentHandler)
  {
      this(contentHandler, null);
  }


  public void traverse(Node pos) throws org.xml.sax.SAXException
  {
        this.m_contentHandler.startDocument();

        traverseFragment(pos);

        this.m_contentHandler.endDocument();
  }


  public void traverseFragment(Node pos) throws org.xml.sax.SAXException
  {
    Node top = pos;

    while (null != pos)
    {
      startNode(pos);

      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        endNode(pos);

        if (top.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || (top.equals(pos)))
          {
            if (null != pos)
              endNode(pos);

            nextNode = null;

            break;
          }
        }
      }

      pos = nextNode;
    }
  }


  public void traverse(Node pos, Node top) throws org.xml.sax.SAXException
  {

        this.m_contentHandler.startDocument();

    while (null != pos)
    {
      startNode(pos);

      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        endNode(pos);

        if ((null != top) && top.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || ((null != top) && top.equals(pos)))
          {
            nextNode = null;

            break;
          }
        }
      }

      pos = nextNode;
    }
    this.m_contentHandler.endDocument();
  }

  boolean nextIsRaw = false;


  private final void dispatachChars(Node node)
     throws org.xml.sax.SAXException
  {
    if(m_contentHandler instanceof CharacterNodeHandler)
    {
      ((CharacterNodeHandler)m_contentHandler).characters(node);
    }
    else
    {
      String data = ((Text) node).getData();
      this.m_contentHandler.characters(data.toCharArray(), 0, data.length());
    }
  }


  protected void startNode(Node node) throws org.xml.sax.SAXException
  {

    if (m_contentHandler instanceof NodeConsumer)
    {
      ((NodeConsumer) m_contentHandler).setOriginatingNode(node);
    }

                if (node instanceof Locator)
                {
                        Locator loc = (Locator)node;
                        m_locator.setColumnNumber(loc.getColumnNumber());
                        m_locator.setLineNumber(loc.getLineNumber());
                        m_locator.setPublicId(loc.getPublicId());
                        m_locator.setSystemId(loc.getSystemId());
                }
                else
                {
                        m_locator.setColumnNumber(0);
      m_locator.setLineNumber(0);
                }

    switch (node.getNodeType())
    {
    case Node.COMMENT_NODE :
    {
      String data = ((Comment) node).getData();

      if (m_contentHandler instanceof LexicalHandler)
      {
        LexicalHandler lh = ((LexicalHandler) this.m_contentHandler);

        lh.comment(data.toCharArray(), 0, data.length());
      }
    }
    break;
    case Node.DOCUMENT_FRAGMENT_NODE :

      break;
    case Node.DOCUMENT_NODE :

      break;
    case Node.ELEMENT_NODE :
      NamedNodeMap atts = ((Element) node).getAttributes();
      int nAttrs = atts.getLength();

      for (int i = 0; i < nAttrs; i++)
      {
        Node attr = atts.item(i);
        String attrName = attr.getNodeName();

        if (attrName.equals("xmlns") || attrName.startsWith("xmlns:"))
        {
          int index;
          String prefix = (index = attrName.indexOf(":")) < 0
                          ? "" : attrName.substring(index + 1);

          this.m_contentHandler.startPrefixMapping(prefix,
                                                   attr.getNodeValue());
        }

      }

      String ns = DOM2Helper.getNamespaceOfNode(node);
      if(null == ns)
        ns = "";
      this.m_contentHandler.startElement(ns,
                                         DOM2Helper.getLocalNameOfNode(node),
                                         node.getNodeName(),
                                         new AttList(atts));
      break;
    case Node.PROCESSING_INSTRUCTION_NODE :
    {
      ProcessingInstruction pi = (ProcessingInstruction) node;
      String name = pi.getNodeName();

      if (name.equals("xslt-next-is-raw"))
      {
        nextIsRaw = true;
      }
      else
      {
        this.m_contentHandler.processingInstruction(pi.getNodeName(),
                                                    pi.getData());
      }
    }
    break;
    case Node.CDATA_SECTION_NODE :
    {
      boolean isLexH = (m_contentHandler instanceof LexicalHandler);
      LexicalHandler lh = isLexH
                          ? ((LexicalHandler) this.m_contentHandler) : null;

      if (isLexH)
      {
        lh.startCDATA();
      }

      dispatachChars(node);

      {
        if (isLexH)
        {
          lh.endCDATA();
        }
      }
    }
    break;
    case Node.TEXT_NODE :
    {
      if (nextIsRaw)
      {
        nextIsRaw = false;

        m_contentHandler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, "");
        dispatachChars(node);
        m_contentHandler.processingInstruction(Result.PI_ENABLE_OUTPUT_ESCAPING, "");
      }
      else
      {
        dispatachChars(node);
      }
    }
    break;
    case Node.ENTITY_REFERENCE_NODE :
    {
      EntityReference eref = (EntityReference) node;

      if (m_contentHandler instanceof LexicalHandler)
      {
        ((LexicalHandler) this.m_contentHandler).startEntity(
          eref.getNodeName());
      }
      else
      {

        }
    }
    break;
    default :
    }
  }


  protected void endNode(Node node) throws org.xml.sax.SAXException
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_NODE :
      break;

    case Node.ELEMENT_NODE :
      String ns = DOM2Helper.getNamespaceOfNode(node);
      if(null == ns)
        ns = "";
      this.m_contentHandler.endElement(ns,
              DOM2Helper.getLocalNameOfNode(node),
              node.getNodeName());

      NamedNodeMap atts = ((Element) node).getAttributes();
      int nAttrs = atts.getLength();

      for (int i = 0; i < nAttrs; i++)
      {
        Node attr = atts.item(i);
        String attrName = attr.getNodeName();

        if (attrName.equals("xmlns") || attrName.startsWith("xmlns:"))
        {
          int index;
          String prefix = (index = attrName.indexOf(":")) < 0
                          ? "" : attrName.substring(index + 1);

          this.m_contentHandler.endPrefixMapping(prefix);
        }
      }
      break;
    case Node.CDATA_SECTION_NODE :
      break;
    case Node.ENTITY_REFERENCE_NODE :
    {
      EntityReference eref = (EntityReference) node;

      if (m_contentHandler instanceof LexicalHandler)
      {
        LexicalHandler lh = ((LexicalHandler) this.m_contentHandler);

        lh.endEntity(eref.getNodeName());
      }
    }
    break;
    default :
    }
  }
}