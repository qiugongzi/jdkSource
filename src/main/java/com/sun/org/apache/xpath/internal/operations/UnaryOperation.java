


package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;


public abstract class UnaryOperation extends Expression implements ExpressionOwner
{
    static final long serialVersionUID = 6536083808424286166L;


  protected Expression m_right;


  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_right.fixupVariables(vars, globalsSize);
  }


  public boolean canTraverseOutsideSubtree()
  {

    if (null != m_right && m_right.canTraverseOutsideSubtree())
      return true;

    return false;
  }


  public void setRight(Expression r)
  {
    m_right = r;
    r.exprSetParent(this);
  }


  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    return operate(m_right.execute(xctxt));
  }


  public abstract XObject operate(XObject right)
    throws javax.xml.transform.TransformerException;


  public Expression getOperand(){
    return m_right;
  }


  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
        if(visitor.visitUnaryOperation(owner, this))
        {
                m_right.callVisitors(this, visitor);
        }
  }



  public Expression getExpression()
  {
    return m_right;
  }


  public void setExpression(Expression exp)
  {
        exp.exprSetParent(this);
        m_right = exp;
  }


  public boolean deepEquals(Expression expr)
  {
        if(!isSameClass(expr))
                return false;

        if(!m_right.deepEquals(((UnaryOperation)expr).m_right))
                return false;

        return true;
  }


}
