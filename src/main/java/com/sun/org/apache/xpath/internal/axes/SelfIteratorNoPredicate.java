


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;


public class SelfIteratorNoPredicate extends LocPathIterator
{
    static final long serialVersionUID = -4226887905279814201L;


  SelfIteratorNoPredicate(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, false);
  }


  public SelfIteratorNoPredicate()
          throws javax.xml.transform.TransformerException
  {
    super(null);
  }



  public int nextNode()
  {
    if (m_foundLast)
      return DTM.NULL;

    int next;
    DTM dtm = m_cdtm;

    m_lastFetched = next = (DTM.NULL == m_lastFetched)
                           ? m_context
                           : DTM.NULL;

    if (DTM.NULL != next)
    {
      m_pos++;

      return next;
    }
    else
    {
      m_foundLast = true;

      return DTM.NULL;
    }
  }


  public int asNode(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
    return xctxt.getCurrentNode();
  }


  public int getLastPos(XPathContext xctxt)
  {
    return 1;
  }


}
