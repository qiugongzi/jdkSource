


package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;



public class Variable extends Expression implements PathComponent
{
    static final long serialVersionUID = -4334975375609297049L;

  private boolean m_fixUpWasCalled = false;


  protected QName m_qname;


  protected int m_index;


  public void setIndex(int index)
  {
        m_index = index;
  }


  public int getIndex()
  {
        return m_index;
  }


  public void setIsGlobal(boolean isGlobal)
  {
        m_isGlobal = isGlobal;
  }


  public boolean getGlobal()
  {
        return m_isGlobal;
  }





  protected boolean m_isGlobal = false;


  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_fixUpWasCalled = true;
    int sz = vars.size();

    for (int i = vars.size()-1; i >= 0; i--)
    {
      QName qn = (QName)vars.elementAt(i);
      if(qn.equals(m_qname))
      {

        if(i < globalsSize)
        {
          m_isGlobal = true;
          m_index = i;
        }
        else
        {
          m_index = i-globalsSize;
        }

        return;
      }
    }

    java.lang.String msg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULD_NOT_FIND_VAR,
                                             new Object[]{m_qname.toString()});

    TransformerException te = new TransformerException(msg, this);

    throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(te);

  }



  public void setQName(QName qname)
  {
    m_qname = qname;
  }


  public QName getQName()
  {
    return m_qname;
  }


  public XObject execute(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
        return execute(xctxt, false);
  }



  public XObject execute(XPathContext xctxt, boolean destructiveOK) throws javax.xml.transform.TransformerException
  {
    com.sun.org.apache.xml.internal.utils.PrefixResolver xprefixResolver = xctxt.getNamespaceContext();

    XObject result;
    if(m_fixUpWasCalled)
    {
      if(m_isGlobal)
        result = xctxt.getVarStack().getGlobalVariable(xctxt, m_index, destructiveOK);
      else
        result = xctxt.getVarStack().getLocalVariable(xctxt, m_index, destructiveOK);
    }
    else {
        result = xctxt.getVarStack().getVariableOrParam(xctxt,m_qname);
    }

      if (null == result)
      {
        warn(xctxt, XPATHErrorResources.WG_ILLEGAL_VARIABLE_REFERENCE,
             new Object[]{ m_qname.getLocalPart() });  result = new XNodeSet(xctxt.getDTMManager());
      }

      return result;
}


  public boolean isStableNumber()
  {
    return true;
  }


  public int getAnalysisBits()
  {

    return WalkerFactory.BIT_FILTER;
  }



  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
        visitor.visitVariableRef(owner, this);
  }

  public boolean deepEquals(Expression expr)
  {
        if(!isSameClass(expr))
                return false;

        if(!m_qname.equals(((Variable)expr).m_qname))
                return false;

    return true;
  }

  static final java.lang.String PSUEDOVARNAMESPACE = "http:public boolean isPsuedoVarRef()
  {
        java.lang.String ns = m_qname.getNamespaceURI();
        if((null != ns) && ns.equals(PSUEDOVARNAMESPACE))
        {
                if(m_qname.getLocalName().startsWith("#"))
                        return true;
        }
        return false;
  }


}
