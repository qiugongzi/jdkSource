


package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;


public class UnionPattern extends Expression
{
    static final long serialVersionUID = -6670449967116905820L;


  private StepPattern[] m_patterns;


  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    for (int i = 0; i < m_patterns.length; i++)
    {
      m_patterns[i].fixupVariables(vars, globalsSize);
    }
  }



   public boolean canTraverseOutsideSubtree()
   {
     if(null != m_patterns)
     {
      int n = m_patterns.length;
      for (int i = 0; i < n; i++)
      {
        if(m_patterns[i].canTraverseOutsideSubtree())
          return true;
      }
     }
     return false;
   }


  public void setPatterns(StepPattern[] patterns)
  {
    m_patterns = patterns;
    if(null != patterns)
    {
        for(int i = 0; i < patterns.length; i++)
        {
                patterns[i].exprSetParent(this);
        }
    }

  }


  public StepPattern[] getPatterns()
  {
    return m_patterns;
  }


  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    XObject bestScore = null;
    int n = m_patterns.length;

    for (int i = 0; i < n; i++)
    {
      XObject score = m_patterns[i].execute(xctxt);

      if (score != NodeTest.SCORE_NONE)
      {
        if (null == bestScore)
          bestScore = score;
        else if (score.num() > bestScore.num())
          bestScore = score;
      }
    }

    if (null == bestScore)
    {
      bestScore = NodeTest.SCORE_NONE;
    }

    return bestScore;
  }

  class UnionPathPartOwner implements ExpressionOwner
  {
        int m_index;

        UnionPathPartOwner(int index)
        {
                m_index = index;
        }


    public Expression getExpression()
    {
      return m_patterns[m_index];
    }



    public void setExpression(Expression exp)
    {
        exp.exprSetParent(UnionPattern.this);
        m_patterns[m_index] = (StepPattern)exp;
    }
  }


  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
        visitor.visitUnionPattern(owner, this);
        if(null != m_patterns)
        {
                int n = m_patterns.length;
                for(int i = 0; i < n; i++)
                {
                        m_patterns[i].callVisitors(new UnionPathPartOwner(i), visitor);
                }
        }
  }


  public boolean deepEquals(Expression expr)
  {
        if(!isSameClass(expr))
                return false;

        UnionPattern up = (UnionPattern)expr;

        if(null != m_patterns)
        {
                int n = m_patterns.length;
                if((null == up.m_patterns) || (up.m_patterns.length != n))
                        return false;

                for(int i = 0; i < n; i++)
                {
                        if(!m_patterns[i].deepEquals(up.m_patterns[i]))
                                return false;
                }
        }
        else if(up.m_patterns != null)
                return false;

        return true;

  }


}
