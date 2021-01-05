


package com.sun.org.apache.xpath.internal.objects;

import java.io.Serializable;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathException;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;


public class XObject extends Expression implements Serializable, Cloneable
{
    static final long serialVersionUID = -821887098985662951L;


  protected Object m_obj;  public XObject(){}


  public XObject(Object obj)
  {
    setObject(obj);
  }

  protected void setObject(Object obj) {
      m_obj = obj;
  }


  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return this;
  }


  public void allowDetachToRelease(boolean allowRelease){}


  public void detach(){}


  public void destruct()
  {

    if (null != m_obj)
    {
      allowDetachToRelease(true);
      detach();

      setObject(null);
    }
  }


  public void reset()
  {
  }


  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    xstr().dispatchCharactersEvents(ch);
  }


  static public XObject create(Object val)
  {
    return XObjectFactory.create(val);
  }


  static public XObject create(Object val, XPathContext xctxt)
  {
    return XObjectFactory.create(val, xctxt);
  }


  public static final int CLASS_NULL = -1;


  public static final int CLASS_UNKNOWN = 0;


  public static final int CLASS_BOOLEAN = 1;


  public static final int CLASS_NUMBER = 2;


  public static final int CLASS_STRING = 3;


  public static final int CLASS_NODESET = 4;


  public static final int CLASS_RTREEFRAG = 5;


  public static final int CLASS_UNRESOLVEDVARIABLE = 600;


  public int getType()
  {
    return CLASS_UNKNOWN;
  }


  public String getTypeString()
  {
    return "#UNKNOWN (" + object().getClass().getName() + ")";
  }


  public double num() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER,
          new Object[]{ getTypeString() });  return 0.0;
  }


  public double numWithSideEffects()  throws javax.xml.transform.TransformerException
  {
    return num();
  }


  public boolean bool() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER,
          new Object[]{ getTypeString() });  return false;
  }


  public boolean boolWithSideEffects() throws javax.xml.transform.TransformerException
  {
    return bool();
  }



  public XMLString xstr()
  {
    return XMLStringFactoryImpl.getFactory().newstr(str());
  }


  public String str()
  {
    return (m_obj != null) ? m_obj.toString() : "";
  }


  public String toString()
  {
    return str();
  }


  public int rtf(XPathContext support)
  {

    int result = rtf();

    if (DTM.NULL == result)
    {
      DTM frag = support.createDocumentFragment();

      frag.appendTextChild(str());

      result = frag.getDocument();
    }

    return result;
  }


  public DocumentFragment rtree(XPathContext support)
  {
    DocumentFragment docFrag = null;
    int result = rtf();

    if (DTM.NULL == result)
    {
      DTM frag = support.createDocumentFragment();

      frag.appendTextChild(str());

      docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
    }
    else
    {
      DTM frag = support.getDTM(result);
      docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
    }

    return docFrag;
  }



  public DocumentFragment rtree()
  {
    return null;
  }


  public int rtf()
  {
    return DTM.NULL;
  }


  public Object object()
  {
    return m_obj;
  }


  public DTMIterator iter() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  return null;
  }


  public XObject getFresh()
  {
    return this;
  }



  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  return null;
  }


  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  return null;
  }



  public NodeSetDTM mutableNodeset()
          throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_MUTABLENODELIST,
          new Object[]{ getTypeString() });  return (NodeSetDTM) m_obj;
  }


  public Object castToType(int t, XPathContext support)
          throws javax.xml.transform.TransformerException
  {

    Object result;

    switch (t)
    {
    case CLASS_STRING :
      result = str();
      break;
    case CLASS_NUMBER :
      result = new Double(num());
      break;
    case CLASS_NODESET :
      result = iter();
      break;
    case CLASS_BOOLEAN :
      result = new Boolean(bool());
      break;
    case CLASS_UNKNOWN :
      result = m_obj;
      break;

    default :
      error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
            new Object[]{ getTypeString(),
                          Integer.toString(t) });  result = null;
    }

    return result;
  }


  public boolean lessThan(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThan(this);

    return this.num() < obj2.num();
  }


  public boolean lessThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThanOrEqual(this);

    return this.num() <= obj2.num();
  }


  public boolean greaterThan(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThan(this);

    return this.num() > obj2.num();
  }


  public boolean greaterThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThanOrEqual(this);

    return this.num() >= obj2.num();
  }


  public boolean equals(XObject obj2)
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.equals(this);

    if (null != m_obj)
    {
      return m_obj.equals(obj2.m_obj);
    }
    else
    {
      return obj2.m_obj == null;
    }
  }


  public boolean notEquals(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.notEquals(this);

    return !equals(obj2);
  }


  protected void error(String msg)
          throws javax.xml.transform.TransformerException
  {
    error(msg, null);
  }


  protected void error(String msg, Object[] args)
          throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);

    {
      throw new XPathException(fmsg, this);
    }
  }



  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    }



  public void appendToFsb(com.sun.org.apache.xml.internal.utils.FastStringBuffer fsb)
  {
    fsb.append(str());
  }


  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
        assertion(false, "callVisitors should not be called for this object!!!");
  }

  public boolean deepEquals(Expression expr)
  {
        if(!isSameClass(expr))
                return false;

        if(!this.equals((XObject)expr))
                return false;

        return true;
  }

}
