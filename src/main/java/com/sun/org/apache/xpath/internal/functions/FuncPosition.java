


package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;


public class FuncPosition extends Function
{
    static final long serialVersionUID = -9092846348197271582L;
  private boolean m_isTopLevel;


  public void postCompileStep(Compiler compiler)
  {
    m_isTopLevel = compiler.getLocationPathDepth() == -1;
  }


  public int getPositionInContextNodeList(XPathContext xctxt)
  {

    SubContextList iter = m_isTopLevel ? null : xctxt.getSubContextList();

    if (null != iter)
    {
      int prox = iter.getProximityPosition(xctxt);

      return prox;
    }

    DTMIterator cnl = xctxt.getContextNodeList();

    if (null != cnl)
    {
      int n = cnl.getCurrentNode();
      if(n == DTM.NULL)
      {
        if(cnl.getCurrentPos() == 0)
          return 0;

        try
        {
          cnl = cnl.cloneWithReset();
        }
        catch(CloneNotSupportedException cnse)
        {
          throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(cnse);
        }
        int currentNode = xctxt.getContextNode();
        while(DTM.NULL != (n = cnl.nextNode()))
        {
          if(n == currentNode)
            break;
        }
      }
      return cnl.getCurrentPos();
    }

    return -1;
  }


  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    double pos = (double) getPositionInContextNodeList(xctxt);

    return new XNumber(pos);
  }


  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    }
}
