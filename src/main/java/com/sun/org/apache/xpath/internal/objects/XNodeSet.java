


package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;

import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;


public class XNodeSet extends NodeSequence
{
    static final long serialVersionUID = 1916026368035639667L;

  protected XNodeSet()
  {
  }


  public XNodeSet(DTMIterator val)
  {
        super();
        if(val instanceof XNodeSet)
        {
            final XNodeSet nodeSet = (XNodeSet) val;
            setIter(nodeSet.m_iter);
            m_dtmMgr = nodeSet.m_dtmMgr;
            m_last = nodeSet.m_last;
            if(!nodeSet.hasCache())
                nodeSet.setShouldCacheNodes(true);

            setObject(nodeSet.getIteratorCache());
        }
        else
        setIter(val);
  }


  public XNodeSet(XNodeSet val)
  {
        super();
    setIter(val.m_iter);
    m_dtmMgr = val.m_dtmMgr;
    m_last = val.m_last;
    if(!val.hasCache())
        val.setShouldCacheNodes(true);
    setObject(val.m_obj);
  }



  public XNodeSet(DTMManager dtmMgr)
  {
     this(DTM.NULL,dtmMgr);
  }


  public XNodeSet(int n, DTMManager dtmMgr)
  {

    super(new NodeSetDTM(dtmMgr));
    m_dtmMgr = dtmMgr;

    if (DTM.NULL != n)
    {
      ((NodeSetDTM) m_obj).addNode(n);
      m_last = 1;
    }
    else
        m_last = 0;
  }


  public int getType()
  {
    return CLASS_NODESET;
  }


  public String getTypeString()
  {
    return "#NODESET";
  }


  public double getNumberFromNode(int n)
  {
    XMLString xstr = m_dtmMgr.getDTM(n).getStringValue(n);
    return xstr.toDouble();
  }


  public double num()
  {

    int node = item(0);
    return (node != DTM.NULL) ? getNumberFromNode(node) : Double.NaN;
  }


  public double numWithSideEffects()
  {
    int node = nextNode();

    return (node != DTM.NULL) ? getNumberFromNode(node) : Double.NaN;
  }



  public boolean bool()
  {
    return (item(0) != DTM.NULL);
  }


  public boolean boolWithSideEffects()
  {
    return (nextNode() != DTM.NULL);
  }



  public XMLString getStringFromNode(int n)
  {
    if(DTM.NULL != n)
    {
      return m_dtmMgr.getDTM(n).getStringValue(n);
    }
    else
    {
      return com.sun.org.apache.xpath.internal.objects.XString.EMPTYSTRING;
    }
  }


  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    int node = item(0);

    if(node != DTM.NULL)
    {
      m_dtmMgr.getDTM(node).dispatchCharactersEvents(node, ch, false);
    }

  }


  public XMLString xstr()
  {
    int node = item(0);
    return (node != DTM.NULL) ? getStringFromNode(node) : XString.EMPTYSTRING;
  }


  public void appendToFsb(com.sun.org.apache.xml.internal.utils.FastStringBuffer fsb)
  {
    XString xstring = (XString)xstr();
    xstring.appendToFsb(fsb);
  }



  public String str()
  {
    int node = item(0);
    return (node != DTM.NULL) ? getStringFromNode(node).toString() : "";
  }


  public Object object()
  {
    if(null == m_obj)
        return this;
    else
        return m_obj;
  }

  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {
    return new com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator(iter());
  }


  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
    com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList nodelist = new com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList(this);
    XNodeSet clone = (XNodeSet)nodelist.getDTMIterator();
    SetVector(clone.getVector());
    return nodelist;
  }


public DTMIterator iterRaw()
  {
    return this;
  }

  public void release(DTMIterator iter)
  {
  }


  public DTMIterator iter()
  {
    try
    {
        if(hasCache())
                return cloneWithReset();
        else
                return this; }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException(cnse.getMessage());
    }
  }


  public XObject getFresh()
  {
    try
    {
        if(hasCache())
                return (XObject)cloneWithReset();
        else
                return this; }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException(cnse.getMessage());
    }
  }


  public NodeSetDTM mutableNodeset()
  {
    NodeSetDTM mnl;

    if(m_obj instanceof NodeSetDTM)
    {
      mnl = (NodeSetDTM) m_obj;
    }
    else
    {
      mnl = new NodeSetDTM(iter());
      setObject(mnl);
      setCurrentPos(0);
    }

    return mnl;
  }


  static final LessThanComparator S_LT = new LessThanComparator();


  static final LessThanOrEqualComparator S_LTE = new LessThanOrEqualComparator();


  static final GreaterThanComparator S_GT = new GreaterThanComparator();


  static final GreaterThanOrEqualComparator S_GTE =
    new GreaterThanOrEqualComparator();


  static final EqualComparator S_EQ = new EqualComparator();


  static final NotEqualComparator S_NEQ = new NotEqualComparator();


  public boolean compare(XObject obj2, Comparator comparator)
          throws javax.xml.transform.TransformerException
  {

    boolean result = false;
    int type = obj2.getType();

    if (XObject.CLASS_NODESET == type)
    {
      DTMIterator list1 = iterRaw();
      DTMIterator list2 = ((XNodeSet) obj2).iterRaw();
      int node1;
      java.util.Vector node2Strings = null;

      while (DTM.NULL != (node1 = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node1);

        if (null == node2Strings)
        {
          int node2;

          while (DTM.NULL != (node2 = list2.nextNode()))
          {
            XMLString s2 = getStringFromNode(node2);

            if (comparator.compareStrings(s1, s2))
            {
              result = true;

              break;
            }

            if (null == node2Strings)
              node2Strings = new java.util.Vector();

            node2Strings.addElement(s2);
          }
        }
        else
        {
          int n = node2Strings.size();

          for (int i = 0; i < n; i++)
          {
            if (comparator.compareStrings(s1, (XMLString)node2Strings.elementAt(i)))
            {
              result = true;

              break;
            }
          }
        }
      }
      list1.reset();
      list2.reset();
    }
    else if (XObject.CLASS_BOOLEAN == type)
    {

      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();

      result = comparator.compareNumbers(num1, num2);
    }
    else if (XObject.CLASS_NUMBER == type)
    {

      DTMIterator list1 = iterRaw();
      double num2 = obj2.num();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
        double num1 = getNumberFromNode(node);

        if (comparator.compareNumbers(num1, num2))
        {
          result = true;

          break;
        }
      }
      list1.reset();
    }
    else if (XObject.CLASS_RTREEFRAG == type)
    {
      XMLString s2 = obj2.xstr();
      DTMIterator list1 = iterRaw();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node);

        if (comparator.compareStrings(s1, s2))
        {
          result = true;

          break;
        }
      }
      list1.reset();
    }
    else if (XObject.CLASS_STRING == type)
    {

      XMLString s2 = obj2.xstr();
      DTMIterator list1 = iterRaw();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node);
        if (comparator.compareStrings(s1, s2))
        {
          result = true;

          break;
        }
      }
      list1.reset();
    }
    else
    {
      result = comparator.compareNumbers(this.num(), obj2.num());
    }

    return result;
  }


  public boolean lessThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_LT);
  }


  public boolean lessThanOrEqual(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_LTE);
  }


  public boolean greaterThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_GT);
  }


  public boolean greaterThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_GTE);
  }


  public boolean equals(XObject obj2)
  {
    try
    {
      return compare(obj2, S_EQ);
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(te);
    }
  }


  public boolean notEquals(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_NEQ);
  }
}


abstract class Comparator
{


  abstract boolean compareStrings(XMLString s1, XMLString s2);


  abstract boolean compareNumbers(double n1, double n2);
}


class LessThanComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() < s2.toDouble());
    }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 < n2;
  }
}


class LessThanOrEqualComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() <= s2.toDouble());
    }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 <= n2;
  }
}


class GreaterThanComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() > s2.toDouble());
    }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 > n2;
  }
}


class GreaterThanOrEqualComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() >= s2.toDouble());
    }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 >= n2;
  }
}


class EqualComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return s1.equals(s2);
  }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 == n2;
  }
}


class NotEqualComparator extends Comparator
{


  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return !s1.equals(s2);
  }


  boolean compareNumbers(double n1, double n2)
  {
    return n1 != n2;
  }
}
