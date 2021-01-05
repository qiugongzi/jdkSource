


package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;


public class XPathException extends TransformerException
{
    static final long serialVersionUID = 4263549717619045963L;


  Object m_styleNode = null;


  public Object getStylesheetNode()
  {
    return m_styleNode;
  }


  public void setStylesheetNode(Object styleNode)
  {
    m_styleNode = styleNode;
  }



  protected Exception m_exception;


  public XPathException(String message, ExpressionNode ex)
  {
    super(message);
    this.setLocator(ex);
    setStylesheetNode(getStylesheetNode(ex));
  }


  public XPathException(String message)
  {
    super(message);
  }



  public org.w3c.dom.Node getStylesheetNode(ExpressionNode ex)
  {

    ExpressionNode owner = getExpressionOwner(ex);

    if (null != owner && owner instanceof org.w3c.dom.Node)
    {
                return ((org.w3c.dom.Node)owner);
    }
    return null;

  }


  protected ExpressionNode getExpressionOwner(ExpressionNode ex)
  {
        ExpressionNode parent = ex.exprGetParent();
        while((null != parent) && (parent instanceof Expression))
                parent = parent.exprGetParent();
        return parent;
  }




  public XPathException(String message, Object styleNode)
  {

    super(message);

    m_styleNode = styleNode;
  }


  public XPathException(String message, Node styleNode, Exception e)
  {

    super(message);

    m_styleNode = styleNode;
    this.m_exception = e;
  }


  public XPathException(String message, Exception e)
  {

    super(message);

    this.m_exception = e;
  }


  public void printStackTrace(java.io.PrintStream s)
  {

    if (s == null)
      s = System.err;

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}

    Throwable exception = m_exception;

    for (int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");
      exception.printStackTrace(s);

      if (exception instanceof TransformerException)
      {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }
  }


  public String getMessage()
  {

    String lastMessage = super.getMessage();
    Throwable exception = m_exception;

    while (null != exception)
    {
      String nextMessage = exception.getMessage();

      if (null != nextMessage)
        lastMessage = nextMessage;

      if (exception instanceof TransformerException)
      {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }

    return (null != lastMessage) ? lastMessage : "";
  }


  public void printStackTrace(java.io.PrintWriter s)
  {

    if (s == null)
      s = new java.io.PrintWriter(System.err);

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}


    boolean isJdk14OrHigher = false;
    try {
        Throwable.class.getMethod("getCause", (Class[]) null);
        isJdk14OrHigher = true;
    } catch (NoSuchMethodException nsme) {
        }

    if (!isJdk14OrHigher) {

      Throwable exception = m_exception;

      for (int i = 0; (i < 10) && (null != exception); i++)
      {
        s.println("---------");

        try
        {
          exception.printStackTrace(s);
        }
        catch (Exception e)
        {
          s.println("Could not print stack trace...");
        }

        if (exception instanceof TransformerException)
        {
          TransformerException se = (TransformerException) exception;
          Throwable prev = exception;

          exception = se.getException();

          if (prev == exception)
          {
            exception = null;

            break;
          }
        }
        else
        {
          exception = null;
        }
      }
    }
  }


  public Throwable getException()
  {
    return m_exception;
  }
}
