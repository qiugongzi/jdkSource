


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.compiler.Compiler;


public class WalkingIteratorSorted extends WalkingIterator
{
    static final long serialVersionUID = -4512512007542368213L;

protected boolean m_inNaturalOrderStatic = false;


  public WalkingIteratorSorted(PrefixResolver nscontext)
  {
    super(nscontext);
  }


  WalkingIteratorSorted(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, shouldLoadWalkers);
  }


  public boolean isDocOrdered()
  {
    return m_inNaturalOrderStatic;
  }



  boolean canBeWalkedInNaturalDocOrderStatic()
  {

    if (null != m_firstWalker)
    {
      AxesWalker walker = m_firstWalker;
      int prevAxis = -1;
      boolean prevIsSimpleDownAxis = true;

      for(int i = 0; null != walker; i++)
      {
        int axis = walker.getAxis();

        if(walker.isDocOrdered())
        {
          boolean isSimpleDownAxis = ((axis == Axis.CHILD)
                                   || (axis == Axis.SELF)
                                   || (axis == Axis.ROOT));
          if(isSimpleDownAxis || (axis == -1))
            walker = walker.getNextWalker();
          else
          {
            boolean isLastWalker = (null == walker.getNextWalker());
            if(isLastWalker)
            {
              if(walker.isDocOrdered() && (axis == Axis.DESCENDANT ||
                 axis == Axis.DESCENDANTORSELF || axis == Axis.DESCENDANTSFROMROOT
                 || axis == Axis.DESCENDANTSORSELFFROMROOT) || (axis == Axis.ATTRIBUTE))
                return true;
            }
            return false;
          }
        }
        else
          return false;
      }
      return true;
    }
    return false;
  }


public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);

    int analysis = getAnalysisBits();
    if(WalkerFactory.isNaturalDocOrder(analysis))
    {
        m_inNaturalOrderStatic = true;
    }
    else
    {
        m_inNaturalOrderStatic = false;
        }

  }

}
