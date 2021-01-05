


package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;


public class VariableStack implements Cloneable
{

  public static final int CLEARLIMITATION= 1024;


  public VariableStack()
  {
    reset();
  }


  public synchronized Object clone() throws CloneNotSupportedException
  {

    VariableStack vs = (VariableStack) super.clone();

    vs._stackFrames = (XObject[]) _stackFrames.clone();
    vs._links = (int[]) _links.clone();

    return vs;
  }


  XObject[] _stackFrames = new XObject[XPathContext.RECURSIONLIMIT * 2];


  int _frameTop;


  private int _currentFrameBottom;


  int[] _links = new int[XPathContext.RECURSIONLIMIT];


  int _linksTop;


  public XObject elementAt(final int i)
  {
    return _stackFrames[i];
  }


  public int size()
  {
    return _frameTop;
  }


  public void reset()
  {

    _frameTop = 0;
    _linksTop = 0;

    _links[_linksTop++] = 0;
    _stackFrames = new XObject[_stackFrames.length];
  }


  public void setStackFrame(int sf)
  {
    _currentFrameBottom = sf;
  }


  public int getStackFrame()
  {
    return _currentFrameBottom;
  }


  public int link(final int size)
  {

    _currentFrameBottom = _frameTop;
    _frameTop += size;

    if (_frameTop >= _stackFrames.length)
    {
      XObject newsf[] = new XObject[_stackFrames.length + XPathContext.RECURSIONLIMIT + size];

      System.arraycopy(_stackFrames, 0, newsf, 0, _stackFrames.length);

      _stackFrames = newsf;
    }

    if (_linksTop + 1 >= _links.length)
    {
      int newlinks[] = new int[_links.length + (CLEARLIMITATION * 2)];

      System.arraycopy(_links, 0, newlinks, 0, _links.length);

      _links = newlinks;
    }

    _links[_linksTop++] = _currentFrameBottom;

    return _currentFrameBottom;
  }


  public  void unlink()
  {
    _frameTop = _links[--_linksTop];
    _currentFrameBottom = _links[_linksTop - 1];
  }


  public  void unlink(int currentFrame)
  {
    _frameTop = _links[--_linksTop];
    _currentFrameBottom = currentFrame;
  }


  public void setLocalVariable(int index, XObject val)
  {
    _stackFrames[index + _currentFrameBottom] = val;
  }


  public void setLocalVariable(int index, XObject val, int stackFrame)
  {
    _stackFrames[index + stackFrame] = val;
  }


  public XObject getLocalVariable(XPathContext xctxt, int index)
          throws TransformerException
  {

    index += _currentFrameBottom;

    XObject val = _stackFrames[index];

    if(null == val)
      throw new TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_VARIABLE_ACCESSED_BEFORE_BIND, null),
                     xctxt.getSAXLocator());
      if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_stackFrames[index] = val.execute(xctxt));

    return val;
  }


  public XObject getLocalVariable(int index, int frame)
          throws TransformerException
  {

    index += frame;

    XObject val = _stackFrames[index];

    return val;
  }


  public XObject getLocalVariable(XPathContext xctxt, int index, boolean destructiveOK)
          throws TransformerException
  {

    index += _currentFrameBottom;

    XObject val = _stackFrames[index];

    if(null == val)
      throw new TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_VARIABLE_ACCESSED_BEFORE_BIND, null),
                     xctxt.getSAXLocator());
      if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_stackFrames[index] = val.execute(xctxt));

    return destructiveOK ? val : val.getFresh();
  }


  public boolean isLocalSet(int index) throws TransformerException
  {
    return (_stackFrames[index + _currentFrameBottom] != null);
  }


  private static XObject[] m_nulls = new XObject[CLEARLIMITATION];


  public void clearLocalSlots(int start, int len)
  {

    start += _currentFrameBottom;

    System.arraycopy(m_nulls, 0, _stackFrames, start, len);
  }


  public void setGlobalVariable(final int index, final XObject val)
  {
    _stackFrames[index] = val;
  }


  public XObject getGlobalVariable(XPathContext xctxt, final int index)
          throws TransformerException
  {

    XObject val = _stackFrames[index];

    if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_stackFrames[index] = val.execute(xctxt));

    return val;
  }


  public XObject getGlobalVariable(XPathContext xctxt, final int index, boolean destructiveOK)
          throws TransformerException
  {

    XObject val = _stackFrames[index];

    if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_stackFrames[index] = val.execute(xctxt));

    return destructiveOK ? val : val.getFresh();
  }


  public XObject getVariableOrParam(
          XPathContext xctxt, com.sun.org.apache.xml.internal.utils.QName qname)
            throws javax.xml.transform.TransformerException
  {

    throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_VAR_NOT_RESOLVABLE, new Object[]{qname.toString()})); }
}