


package com.sun.org.apache.xpath.internal.compiler;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.ConfigurationError;


public class FuncLoader
{


  private int m_funcID;


  private String m_funcName;


  public String getName()
  {
    return m_funcName;
  }


  public FuncLoader(String funcName, int funcID)
  {

    super();

    m_funcID = funcID;
    m_funcName = funcName;
  }


  Function getFunction() throws TransformerException
  {
    try
    {
      String className = m_funcName;
      if (className.indexOf(".") < 0) {
        className = "com.sun.org.apache.xpath.internal.functions." + className;
      }
      String subString = className.substring(0,className.lastIndexOf('.'));
      if(!(subString.equals ("com.sun.org.apache.xalan.internal.templates") ||
           subString.equals ("com.sun.org.apache.xpath.internal.functions"))) {
            throw new TransformerException("Application can't install his own xpath function.");
      }

      return (Function) ObjectFactory.newInstance(className, true);

    }
    catch (ConfigurationError e)
    {
      throw new TransformerException(e.getException());
    }
  }
}
