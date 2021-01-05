


package com.sun.org.apache.xpath.internal.functions;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;


public class FuncSystemProperty extends FunctionOneArg
{
    static final long serialVersionUID = 3694874980992204867L;

  static final String XSLT_PROPERTIES =
            "com/sun/org/apache/xalan/internal/res/XSLTInfo.properties";


  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    String fullName = m_arg0.execute(xctxt).str();
    int indexOfNSSep = fullName.indexOf(':');
    String result;
    String propName = "";

    Properties xsltInfo = new Properties();

    loadPropertyFile(XSLT_PROPERTIES, xsltInfo);

    if (indexOfNSSep > 0)
    {
      String prefix = (indexOfNSSep >= 0)
                      ? fullName.substring(0, indexOfNSSep) : "";
      String namespace;

      namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
      propName = (indexOfNSSep < 0)
                 ? fullName : fullName.substring(indexOfNSSep + 1);

      if (namespace.startsWith("http:|| namespace.equals("http:{
        result = xsltInfo.getProperty(propName);

        if (null == result)
        {
          warn(xctxt, XPATHErrorResources.WG_PROPERTY_NOT_SUPPORTED,
               new Object[]{ fullName });  return XString.EMPTYSTRING;
        }
      }
      else
      {
        warn(xctxt, XPATHErrorResources.WG_DONT_DO_ANYTHING_WITH_NS,
             new Object[]{ namespace,
                           fullName });  try
        {
          result = SecuritySupport.getSystemProperty(propName);

          if (null == result)
          {

            return XString.EMPTYSTRING;
          }
        }
        catch (SecurityException se)
        {
          warn(xctxt, XPATHErrorResources.WG_SECURITY_EXCEPTION,
               new Object[]{ fullName });  return XString.EMPTYSTRING;
        }
      }
    }
    else
    {
      try
      {
        result = SecuritySupport.getSystemProperty(fullName);

        if (null == result)
        {

          return XString.EMPTYSTRING;
        }
      }
      catch (SecurityException se)
      {
        warn(xctxt, XPATHErrorResources.WG_SECURITY_EXCEPTION,
             new Object[]{ fullName });  return XString.EMPTYSTRING;
      }
    }

    if (propName.equals("version") && result.length() > 0)
    {
      try
      {
        return new XString("1.0");
      }
      catch (Exception ex)
      {
        return new XString(result);
      }
    }
    else
      return new XString(result);
  }


  public void loadPropertyFile(String file, Properties target)
  {
    try
    {
      InputStream is = SecuritySupport.getResourceAsStream(ObjectFactory.findClassLoader(),
                                              file);

      BufferedInputStream bis = new BufferedInputStream(is);

      target.load(bis);  bis.close();  }
    catch (Exception ex)
    {
      throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(ex);
    }
  }
}
