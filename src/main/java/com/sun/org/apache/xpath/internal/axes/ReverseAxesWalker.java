


package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xpath.internal.XPathContext;


public class ReverseAxesWalker extends AxesWalker
{
    static final long serialVersionUID = 2847007647832768941L;


  ReverseAxesWalker(LocPathIterator locPathIterator, int axis)
  {
    super(locPathIterator, axis);
  }


  public void setRoot(int root)
  {
    super.setRoot(root);
    m_iterator = getDTM(root).getAxisIterator(m_axis);
    m_iterator.setStartNode(root);
  }


  public void detach()
  {
    m_iterator = null;
    super.detach();
  }


  protected int getNextNode()
  {
    if (m_foundLast)
      return DTM.NULL;

    int next = m_iterator.next();

    if (m_isFresh)
      m_isFresh = false;

    if (DTM.NULL == next)
      this.m_foundLast = true;

    return next;
  }



  public boolean isReverseAxes()
  {
    return true;
  }

protected int getProximityPosition(int predicateIndex)
  {
    protected void countProximityPosition(int i)
  {
    if (i < m_proximityPositions.length)
      m_proximityPositions[i]--;
  }


  public int getLastPos(XPathContext xctxt)
  {

    int count = 0;
    AxesWalker savedWalker = wi().getLastUsedWalker();

    try
    {
      ReverseAxesWalker clone = (ReverseAxesWalker) this.clone();

      clone.setRoot(this.getRoot());

      clone.setPredicateCount(this.getPredicateCount() - 1);

      clone.setPrevWalker(null);
      clone.setNextWalker(null);
      wi().setLastUsedWalker(clone);

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
      wi().setLastUsedWalker(savedWalker);
    }

    return count;
  }


  public boolean isDocOrdered()
  {
    return false;  }


  protected DTMAxisIterator m_iterator;
}
