


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;


public class FilterExprIteratorSimple extends LocPathIterator
{
    static final long serialVersionUID = -6978977187025375579L;

  private Expression m_expr;


  transient private XNodeSet m_exprObj;

  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;


  public FilterExprIteratorSimple()
  {
    super(null);
  }


  public FilterExprIteratorSimple(Expression expr)
  {
    super(null);
    m_expr = expr;
  }


  public void setRoot(int context, Object environment)
  {
        super.setRoot(context, environment);
        m_exprObj = executeFilterExpr(context, m_execContext, getPrefixResolver(),
                          getIsTopLevel(), m_stackFrame, m_expr);
  }


  public static XNodeSet executeFilterExpr(int context, XPathContext xctxt,
                                                                                                PrefixResolver prefixResolver,
                                                                                                boolean isTopLevel,
                                                                                                int stackFrame,
                                                                                                Expression expr )
    throws com.sun.org.apache.xml.internal.utils.WrappedRuntimeException
  {
    PrefixResolver savedResolver = xctxt.getNamespaceContext();
    XNodeSet result = null;

    try
    {
      xctxt.pushCurrentNode(context);
      xctxt.setNamespaceContext(prefixResolver);

      if (isTopLevel)
      {
        VariableStack vars = xctxt.getVarStack();

        int savedStart = vars.getStackFrame();
        vars.setStackFrame(stackFrame);

        result = (com.sun.org.apache.xpath.internal.objects.XNodeSet) expr.execute(xctxt);
        result.setShouldCacheNodes(true);

        vars.setStackFrame(savedStart);
      }
      else
          result = (com.sun.org.apache.xpath.internal.objects.XNodeSet) expr.execute(xctxt);

    }
    catch (javax.xml.transform.TransformerException se)
    {

      throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(se);
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
    }
    return result;
  }


  public int nextNode()
  {
        if(m_foundLast)
                return DTM.NULL;

    int next;

    if (null != m_exprObj)
    {
      m_lastFetched = next = m_exprObj.nextNode();
    }
    else
      m_lastFetched = next = DTM.NULL;

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


  public void detach()
  {
    if(m_allowDetach)
    {
                super.detach();
                m_exprObj.detach();
                m_exprObj = null;
    }
  }


  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);
    m_expr.fixupVariables(vars, globalsSize);
  }


  public Expression getInnerExpression()
  {
    return m_expr;
  }


  public void setInnerExpression(Expression expr)
  {
    expr.exprSetParent(this);
    m_expr = expr;
  }


  public int getAnalysisBits()
  {
    if (null != m_expr && m_expr instanceof PathComponent)
    {
      return ((PathComponent) m_expr).getAnalysisBits();
    }
    return WalkerFactory.BIT_FILTER;
  }


  public boolean isDocOrdered()
  {
    return m_exprObj.isDocOrdered();
  }

  class filterExprOwner implements ExpressionOwner
  {

    public Expression getExpression()
    {
      return m_expr;
    }


    public void setExpression(Expression exp)
    {
      exp.exprSetParent(FilterExprIteratorSimple.this);
      m_expr = exp;
    }

  }


  public void callPredicateVisitors(XPathVisitor visitor)
  {
    m_expr.callVisitors(new filterExprOwner(), visitor);

    super.callPredicateVisitors(visitor);
  }


  public boolean deepEquals(Expression expr)
  {
    if (!super.deepEquals(expr))
      return false;

    FilterExprIteratorSimple fet = (FilterExprIteratorSimple) expr;
    if (!m_expr.deepEquals(fet.m_expr))
      return false;

    return true;
  }


  public int getAxis()
  {
        if(null != m_exprObj)
        return m_exprObj.getAxis();
    else
        return Axis.FILTEREDLIST;
  }


}
