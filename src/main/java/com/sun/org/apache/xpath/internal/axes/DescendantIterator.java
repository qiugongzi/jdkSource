


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import org.w3c.dom.DOMException;


public class DescendantIterator extends LocPathIterator
{
    static final long serialVersionUID = -1190338607743976938L;

  DescendantIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = OpMap.getFirstChildPos(opPos);
    int stepType = compiler.getOp(firstStepPos);

    boolean orSelf = (OpCodes.FROM_DESCENDANTS_OR_SELF == stepType);
    boolean fromRoot = false;
    if (OpCodes.FROM_SELF == stepType)
    {
      orSelf = true;
      }
    else if(OpCodes.FROM_ROOT == stepType)
    {
      fromRoot = true;
      int nextStepPos = compiler.getNextStepPos(firstStepPos);
      if(compiler.getOp(nextStepPos) == OpCodes.FROM_DESCENDANTS_OR_SELF)
        orSelf = true;
      }

    int nextStepPos = firstStepPos;
    while(true)
    {
      nextStepPos = compiler.getNextStepPos(nextStepPos);
      if(nextStepPos > 0)
      {
        int stepOp = compiler.getOp(nextStepPos);
        if(OpCodes.ENDOP != stepOp)
          firstStepPos = nextStepPos;
        else
          break;
      }
      else
        break;

    }

    if((analysis & WalkerFactory.BIT_CHILD) != 0)
      orSelf = false;

    if(fromRoot)
    {
      if(orSelf)
        m_axis = Axis.DESCENDANTSORSELFFROMROOT;
      else
        m_axis = Axis.DESCENDANTSFROMROOT;
    }
    else if(orSelf)
      m_axis = Axis.DESCENDANTORSELF;
    else
      m_axis = Axis.DESCENDANT;

    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (DTMFilter.SHOW_ATTRIBUTE | DTMFilter.SHOW_ELEMENT
                  | DTMFilter.SHOW_PROCESSING_INSTRUCTION))) ||
                   (whatToShow == DTMFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
  }


  public DescendantIterator()
  {
    super(null);
    m_axis = Axis.DESCENDANTSORSELFFROMROOT;
    int whatToShow = DTMFilter.SHOW_ALL;
    initNodeTest(whatToShow);
  }



  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    DescendantIterator clone = (DescendantIterator) super.cloneWithReset();
    clone.m_traverser = m_traverser;

    clone.resetProximityPositions();

    return clone;
  }


  public int nextNode()
  {
        if(m_foundLast)
                return DTM.NULL;

    if(DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

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
      do
      {
        if(0 == m_extendedTypeID)
        {
          next = m_lastFetched = (DTM.NULL == m_lastFetched)
                       ? m_traverser.first(m_context)
                       : m_traverser.next(m_context, m_lastFetched);
        }
        else
        {
          next = m_lastFetched = (DTM.NULL == m_lastFetched)
                       ? m_traverser.first(m_context, m_extendedTypeID)
                       : m_traverser.next(m_context, m_lastFetched,
                                          m_extendedTypeID);
        }

        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next))
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
        m_pos++;
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


  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);

    String localName = getLocalName();
    String namespace = getNamespace();
    int what = m_whatToShow;
    if(DTMFilter.SHOW_ALL == what
       || NodeTest.WILD.equals(localName)
       || NodeTest.WILD.equals(namespace))
    {
      m_extendedTypeID = 0;
    }
    else
    {
      int type = getNodeTypeTest(what);
      m_extendedTypeID = m_cdtm.getExpandedTypeID(namespace, localName, type);
    }

  }


  public int asNode(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
    if(getPredicateCount() > 0)
      return super.asNode(xctxt);

    int current = xctxt.getCurrentNode();

    DTM dtm = xctxt.getDTM(current);
    DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axis);

    String localName = getLocalName();
    String namespace = getNamespace();
    int what = m_whatToShow;

    if(DTMFilter.SHOW_ALL == what
       || localName == NodeTest.WILD
       || namespace == NodeTest.WILD)
    {
      return traverser.first(current);
    }
    else
    {
      int type = getNodeTypeTest(what);
      int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
      return traverser.first(current, extendedType);
    }
  }


  public void detach()
  {
    if (m_allowDetach) {
      m_traverser = null;
      m_extendedTypeID = 0;

      super.detach();
    }
  }


  public int getAxis()
  {
    return m_axis;
  }



  transient protected DTMAxisTraverser m_traverser;


  protected int m_axis;


  protected int m_extendedTypeID;


  public boolean deepEquals(Expression expr)
  {
        if(!super.deepEquals(expr))
                return false;

        if(m_axis != ((DescendantIterator)expr).m_axis)
                return false;

        return true;
  }


}
