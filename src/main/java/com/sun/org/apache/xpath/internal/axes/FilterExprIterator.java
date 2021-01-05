


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;

public class FilterExprIterator extends BasicTestIterator
{
    static final long serialVersionUID = 2552176105165737614L;

  private Expression m_expr;


  transient private XNodeSet m_exprObj;

  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;


  public FilterExprIterator()
  {
    super(null);
  }


  public FilterExprIterator(Expression expr)
  {
    super(null);
    m_expr = expr;
  }


  public void setRoot(int context, Object environment)
  {
        super.setRoot(context, environment);

        m_exprObj = FilterExprIteratorSimple.executeFilterExpr(context,
                          m_execContext, getPrefixResolver(),
                          getIsTopLevel(), m_stackFrame, m_expr);
   }



  protected int getNextNode()
  {
    if (null != m_exprObj)
    {
      m_lastFetched = m_exprObj.nextNode();
    }
    else
      m_lastFetched = DTM.NULL;

    return m_lastFetched;
  }


  public void detach()
  {
        super.detach();
        m_exprObj.detach();
        m_exprObj = null;
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
      exp.exprSetParent(FilterExprIterator.this);
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

    FilterExprIterator fet = (FilterExprIterator) expr;
    if (!m_expr.deepEquals(fet.m_expr))
      return false;

    return true;
  }

}
