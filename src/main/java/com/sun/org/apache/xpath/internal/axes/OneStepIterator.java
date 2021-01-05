


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;


public class OneStepIterator extends ChildTestIterator
{
    static final long serialVersionUID = 4623710779664998283L;

  protected int m_axis = -1;


  protected DTMAxisIterator m_iterator;


  OneStepIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis);
    int firstStepPos = OpMap.getFirstChildPos(opPos);

    m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);

  }



  public OneStepIterator(DTMAxisIterator iterator, int axis)
          throws javax.xml.transform.TransformerException
  {
    super(null);

    m_iterator = iterator;
    m_axis = axis;
    int whatToShow = DTMFilter.SHOW_ALL;
    initNodeTest(whatToShow);
  }


  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);
    if(m_axis > -1)
      m_iterator = m_cdtm.getAxisIterator(m_axis);
    m_iterator.setStartNode(m_context);
  }


  public void detach()
  {
    if(m_allowDetach)
    {
      if(m_axis > -1)
        m_iterator = null;

      super.detach();
    }
  }


  protected int getNextNode()
  {
    return m_lastFetched = m_iterator.next();
  }


  public Object clone() throws CloneNotSupportedException
  {
    OneStepIterator clone = (OneStepIterator) super.clone();

    if(m_iterator != null)
    {
      clone.m_iterator = m_iterator.cloneIterator();
    }
    return clone;
  }


  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    OneStepIterator clone = (OneStepIterator) super.cloneWithReset();
    clone.m_iterator = m_iterator;

    return clone;
  }




  public boolean isReverseAxes()
  {
    return m_iterator.isReverse();
  }


  protected int getProximityPosition(int predicateIndex)
  {
    if(!isReverseAxes())
      return super.getProximityPosition(predicateIndex);

    public int getLength()
  {
    if(!isReverseAxes())
      return super.getLength();

    boolean isPredicateTest = (this == m_execContext.getSubContextList());

    int predCount = getPredicateCount();

    if (-1 != m_length && isPredicateTest && m_predicateIndex < 1)
       return m_length;

    int count = 0;

    XPathContext xctxt = getXPathContext();
    try
    {
      OneStepIterator clone = (OneStepIterator) this.cloneWithReset();

      int root = getRoot();
      xctxt.pushCurrentNode(root);
      clone.setRoot(root, xctxt);

      clone.m_predCount = m_predicateIndex;

      int next;

      while (DTM.NULL != (next = clone.nextNode()))
      {
        count++;
      }
    }
    catch (CloneNotSupportedException cnse)
    {
       }
    finally
    {
      xctxt.popCurrentNode();
    }
    if (isPredicateTest && m_predicateIndex < 1)
      m_length = count;

    return count;
  }


  protected void countProximityPosition(int i)
  {
    if(!isReverseAxes())
      super.countProximityPosition(i);
    else if (i < m_proximityPositions.length)
      m_proximityPositions[i]--;
  }


  public void reset()
  {

    super.reset();
    if(null != m_iterator)
      m_iterator.reset();
  }


  public int getAxis()
  {
    return m_axis;
  }


  public boolean deepEquals(Expression expr)
  {
        if(!super.deepEquals(expr))
                return false;

        if(m_axis != ((OneStepIterator)expr).m_axis)
                return false;

        return true;
  }


}
