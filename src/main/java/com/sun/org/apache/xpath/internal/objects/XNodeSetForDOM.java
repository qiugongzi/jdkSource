


package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;


public class XNodeSetForDOM extends XNodeSet
{
    static final long serialVersionUID = -8396190713754624640L;
  Object m_origObj;

  public XNodeSetForDOM(Node node, DTMManager dtmMgr)
  {
    m_dtmMgr = dtmMgr;
    m_origObj = node;
    int dtmHandle = dtmMgr.getDTMHandleFromNode(node);
    setObject(new NodeSetDTM(dtmMgr));
    ((NodeSetDTM) m_obj).addNode(dtmHandle);
  }


  public XNodeSetForDOM(XNodeSet val)
  {
        super(val);
        if(val instanceof XNodeSetForDOM)
        m_origObj = ((XNodeSetForDOM)val).m_origObj;
  }

  public XNodeSetForDOM(NodeList nodeList, XPathContext xctxt)
  {
    m_dtmMgr = xctxt.getDTMManager();
    m_origObj = nodeList;

    com.sun.org.apache.xpath.internal.NodeSetDTM nsdtm=new com.sun.org.apache.xpath.internal.NodeSetDTM(nodeList, xctxt);
    m_last=nsdtm.getLength();
    setObject(nsdtm);
  }

  public XNodeSetForDOM(NodeIterator nodeIter, XPathContext xctxt)
  {
    m_dtmMgr = xctxt.getDTMManager();
    m_origObj = nodeIter;

    com.sun.org.apache.xpath.internal.NodeSetDTM nsdtm=new com.sun.org.apache.xpath.internal.NodeSetDTM(nodeIter, xctxt);
    m_last=nsdtm.getLength();
    setObject(nsdtm);
  }


  public Object object()
  {
    return m_origObj;
  }


  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeIterator)
                   ? (NodeIterator)m_origObj : super.nodeset();
  }


  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeList)
                   ? (NodeList)m_origObj : super.nodelist();
  }



}
