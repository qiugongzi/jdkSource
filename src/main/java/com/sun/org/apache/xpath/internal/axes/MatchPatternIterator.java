


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;


public class MatchPatternIterator extends LocPathIterator
{
    static final long serialVersionUID = -5201153767396296474L;


  protected StepPattern m_pattern;


  protected int m_superAxis = -1;


  protected DTMAxisTraverser m_traverser;


  private static final boolean DEBUG = false;

MatchPatternIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = OpMap.getFirstChildPos(opPos);

    m_pattern = WalkerFactory.loadSteps(this, compiler, firstStepPos, 0);

    boolean fromRoot = false;
    boolean walkBack = false;
    boolean walkDescendants = false;
    boolean walkAttributes = false;

    if (0 != (analysis & (WalkerFactory.BIT_ROOT |
                          WalkerFactory.BIT_ANY_DESCENDANT_FROM_ROOT)))
      fromRoot = true;

    if (0 != (analysis
              & (WalkerFactory.BIT_ANCESTOR
                 | WalkerFactory.BIT_ANCESTOR_OR_SELF
                 | WalkerFactory.BIT_PRECEDING
                 | WalkerFactory.BIT_PRECEDING_SIBLING
                 | WalkerFactory.BIT_FOLLOWING
                 | WalkerFactory.BIT_FOLLOWING_SIBLING
                 | WalkerFactory.BIT_PARENT | WalkerFactory.BIT_FILTER)))
      walkBack = true;

    if (0 != (analysis
              & (WalkerFactory.BIT_DESCENDANT_OR_SELF
                 | WalkerFactory.BIT_DESCENDANT
                 | WalkerFactory.BIT_CHILD)))
      walkDescendants = true;

    if (0 != (analysis
              & (WalkerFactory.BIT_ATTRIBUTE | WalkerFactory.BIT_NAMESPACE)))
      walkAttributes = true;

    if(false || DEBUG)
    {
      System.out.print("analysis: "+Integer.toBinaryString(analysis));
      System.out.println(", "+WalkerFactory.getAnalysisString(analysis));
    }

    if(fromRoot || walkBack)
    {
      if(walkAttributes)
      {
        m_superAxis = Axis.ALL;
      }
      else
      {
        m_superAxis = Axis.DESCENDANTSFROMROOT;
      }
    }
    else if(walkDescendants)
    {
      if(walkAttributes)
      {
        m_superAxis = Axis.ALLFROMNODE;
      }
      else
      {
        m_superAxis = Axis.DESCENDANTORSELF;
      }
    }
    else
    {
      m_superAxis = Axis.ALL;
    }
    if(false || DEBUG)
    {
      System.out.println("axis: "+Axis.getNames(m_superAxis));
    }

  }



  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(m_superAxis);
  }


  public void detach()
  {
    if(m_allowDetach)
    {
      m_traverser = null;

      super.detach();
    }
  }


  protected int getNextNode()
  {
    m_lastFetched = (DTM.NULL == m_lastFetched)
                     ? m_traverser.first(m_context)
                     : m_traverser.next(m_context, m_lastFetched);
    return m_lastFetched;
  }


  public int nextNode()
  {
        if(m_foundLast)
                return DTM.NULL;

    int next;

    com.sun.org.apache.xpath.internal.VariableStack vars;
    int savedStart;
    if (-1 != m_stackFrame)
    {
      vars = m_execContext.getVarStack();

      savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);
    }
    else
    {
      vars = null;
      savedStart = 0;
    }

    try
    {
      if(DEBUG)
        System.out.println("m_pattern"+m_pattern.toString());

      do
      {
        next = getNextNode();

        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next, m_execContext))
            break;
          else
            continue;
        }
        else
          break;
      }
      while (next != DTM.NULL);

      if (DTM.NULL != next)
      {
        if(DEBUG)
        {
          System.out.println("next: "+next);
          System.out.println("name: "+m_cdtm.getNodeName(next));
        }
        incrementCurrentPos();

        return next;
      }
      else
      {
        m_foundLast = true;

        return DTM.NULL;
      }
    }
    finally
    {
      if (-1 != m_stackFrame)
      {
        vars.setStackFrame(savedStart);
      }
    }

  }


  public short acceptNode(int n, XPathContext xctxt)
  {

    try
    {
      xctxt.pushCurrentNode(n);
      xctxt.pushIteratorRoot(m_context);
      if(DEBUG)
      {
        System.out.println("traverser: "+m_traverser);
        System.out.print("node: "+n);
        System.out.println(", "+m_cdtm.getNodeName(n));
        System.out.println("pattern: "+m_pattern.toString());
        m_pattern.debugWhatToShow(m_pattern.getWhatToShow());
      }

      XObject score = m_pattern.execute(xctxt);

      if(DEBUG)
      {
        System.out.println("score: "+score);
        System.out.println("skip: "+(score == NodeTest.SCORE_NONE));
      }

      return (score == NodeTest.SCORE_NONE) ? DTMIterator.FILTER_SKIP
                    : DTMIterator.FILTER_ACCEPT;
    }
    catch (javax.xml.transform.TransformerException se)
    {

      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popIteratorRoot();
    }

  }

}
