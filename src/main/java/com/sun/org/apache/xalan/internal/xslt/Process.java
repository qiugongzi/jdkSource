


package com.sun.org.apache.xalan.internal.xslt;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.org.apache.xalan.internal.Version;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.res.XSLTErrorResources;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;

import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class Process
{

  protected static void printArgOptions(ResourceBundle resbundle)
  {
    System.out.println(resbundle.getString("xslProc_option"));  System.out.println("\n\t\t\t" + resbundle.getString("xslProc_common_options") + "\n");
    System.out.println(resbundle.getString("optionXSLTC"));  System.out.println(resbundle.getString("optionIN"));  System.out.println(resbundle.getString("optionXSL"));  System.out.println(resbundle.getString("optionOUT"));  System.out.println(resbundle.getString("optionV"));  System.out.println(resbundle.getString("optionEDUMP"));  System.out.println(resbundle.getString("optionXML"));  System.out.println(resbundle.getString("optionTEXT"));  System.out.println(resbundle.getString("optionHTML"));  System.out.println(resbundle.getString("optionPARAM"));  System.out.println(resbundle.getString("optionMEDIA"));
    System.out.println(resbundle.getString("optionFLAVOR"));
    System.out.println(resbundle.getString("optionDIAG"));
    System.out.println(resbundle.getString("optionURIRESOLVER"));  System.out.println(resbundle.getString("optionENTITYRESOLVER"));  waitForReturnKey(resbundle);
    System.out.println(resbundle.getString("optionCONTENTHANDLER"));  System.out.println(resbundle.getString("optionSECUREPROCESSING")); System.out.println("\n\t\t\t" + resbundle.getString("xslProc_xsltc_options") + "\n");
    System.out.println(resbundle.getString("optionXO"));
    waitForReturnKey(resbundle);
    System.out.println(resbundle.getString("optionXD"));
    System.out.println(resbundle.getString("optionXJ"));
    System.out.println(resbundle.getString("optionXP"));
    System.out.println(resbundle.getString("optionXN"));
    System.out.println(resbundle.getString("optionXX"));
    System.out.println(resbundle.getString("optionXT"));
  }


  public static void _main(String argv[])
  {

    boolean doStackDumpOnError = false;
    boolean setQuietMode = false;
    boolean doDiag = false;
    String msg = null;
    boolean isSecureProcessing = false;

    java.io.PrintWriter diagnosticsWriter = new PrintWriter(System.err, true);
    java.io.PrintWriter dumpWriter = diagnosticsWriter;
    ResourceBundle resbundle =
      (SecuritySupport.getResourceBundle(
        com.sun.org.apache.xml.internal.utils.res.XResourceBundle.ERROR_RESOURCES));
    String flavor = "s2s";

    if (argv.length < 1)
    {
      printArgOptions(resbundle);
    }
    else
    {
        boolean useXSLTC = true;
      for (int i = 0; i < argv.length; i++)
      {
        if ("-XSLTC".equalsIgnoreCase(argv[i]))
        {
          useXSLTC = true;
        }
      }

      TransformerFactory tfactory;
      if (useXSLTC)
      {
         String key = "javax.xml.transform.TransformerFactory";
         String value = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
         Properties props = System.getProperties();
         props.put(key, value);
         System.setProperties(props);
      }

      try
      {
        tfactory = TransformerFactory.newInstance();
        tfactory.setErrorListener(new DefaultErrorHandler());
      }
      catch (TransformerFactoryConfigurationError pfe)
      {
        pfe.printStackTrace(dumpWriter);
msg = XSLMessages.createMessage(
            XSLTErrorResources.ER_NOT_SUCCESSFUL, null);
        diagnosticsWriter.println(msg);

        tfactory = null;  doExit(msg);
      }

      boolean formatOutput = false;
      boolean useSourceLocation = false;
      String inFileName = null;
      String outFileName = null;
      String dumpFileName = null;
      String xslFileName = null;
      String treedumpFileName = null;
      String outputType = null;
      String media = null;
      Vector params = new Vector();
      boolean quietConflictWarnings = false;
      URIResolver uriResolver = null;
      EntityResolver entityResolver = null;
      ContentHandler contentHandler = null;
      int recursionLimit=-1;

      for (int i = 0; i < argv.length; i++)
      {
        if ("-XSLTC".equalsIgnoreCase(argv[i]))
        {
          }
        else if ("-INDENT".equalsIgnoreCase(argv[i]))
        {
          int indentAmount;

          if (((i + 1) < argv.length) && (argv[i + 1].charAt(0) != '-'))
          {
            indentAmount = Integer.parseInt(argv[++i]);
          }
          else
          {
            indentAmount = 0;
          }

          }
        else if ("-IN".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
            inFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-IN" }));  }
        else if ("-MEDIA".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            media = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-MEDIA" }));  }
        else if ("-OUT".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
            outFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-OUT" }));  }
        else if ("-XSL".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
            xslFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-XSL" }));  }
        else if ("-FLAVOR".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            flavor = argv[++i];
          }
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-FLAVOR" }));  }
        else if ("-PARAM".equalsIgnoreCase(argv[i]))
        {
          if (i + 2 < argv.length)
          {
            String name = argv[++i];

            params.addElement(name);

            String expression = argv[++i];

            params.addElement(expression);
          }
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                new Object[]{ "-PARAM" }));  }
        else if ("-E".equalsIgnoreCase(argv[i]))
        {

          }
        else if ("-V".equalsIgnoreCase(argv[i]))
        {
          diagnosticsWriter.println(resbundle.getString("version")  + Version.getVersion() + ", " +


          resbundle.getString("version2"));  }
        else if ("-Q".equalsIgnoreCase(argv[i]))
        {
          setQuietMode = true;
        }
        else if ("-DIAG".equalsIgnoreCase(argv[i]))
        {
          doDiag = true;
        }
        else if ("-XML".equalsIgnoreCase(argv[i]))
        {
          outputType = "xml";
        }
        else if ("-TEXT".equalsIgnoreCase(argv[i]))
        {
          outputType = "text";
        }
        else if ("-HTML".equalsIgnoreCase(argv[i]))
        {
          outputType = "html";
        }
        else if ("-EDUMP".equalsIgnoreCase(argv[i]))
        {
          doStackDumpOnError = true;

          if (((i + 1) < argv.length) && (argv[i + 1].charAt(0) != '-'))
          {
            dumpFileName = argv[++i];
          }
        }
        else if ("-URIRESOLVER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              uriResolver = (URIResolver) ObjectFactory.newInstance(argv[++i], true);

              tfactory.setURIResolver(uriResolver);
            }
            catch (ConfigurationError cnfe)
            {
                msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                    new Object[]{ "-URIResolver" });
              System.err.println(msg);
              doExit(msg);
            }
          }
          else
          {
            msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                    new Object[]{ "-URIResolver" });  System.err.println(msg);
            doExit(msg);
          }
        }
        else if ("-ENTITYRESOLVER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              entityResolver = (EntityResolver) ObjectFactory.newInstance(argv[++i], true);
            }
            catch (ConfigurationError cnfe)
            {
                msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                    new Object[]{ "-EntityResolver" });
              System.err.println(msg);
              doExit(msg);
            }
          }
          else
          {
msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                    new Object[]{ "-EntityResolver" });
            System.err.println(msg);
            doExit(msg);
          }
        }
        else if ("-CONTENTHANDLER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              contentHandler = (ContentHandler) ObjectFactory.newInstance(argv[++i], true);
            }
            catch (ConfigurationError cnfe)
            {
                msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                    new Object[]{ "-ContentHandler" });
              System.err.println(msg);
              doExit(msg);
            }
          }
          else
          {
msg = XSLMessages.createMessage(
                    XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                    new Object[]{ "-ContentHandler" });
            System.err.println(msg);
            doExit(msg);
          }
        }
        else if ("-XO".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            if (i + 1 < argv.length && argv[i+1].charAt(0) != '-')
            {
              tfactory.setAttribute("generate-translet", "true");
              tfactory.setAttribute("translet-name", argv[++i]);
            }
            else
              tfactory.setAttribute("generate-translet", "true");
          }
          else
          {
            if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
             i++;
            printInvalidXalanOption("-XO");
          }
        }
        else if ("-XD".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            if (i + 1 < argv.length && argv[i+1].charAt(0) != '-')
              tfactory.setAttribute("destination-directory", argv[++i]);
            else
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                  new Object[]{ "-XD" }));  }
          else
          {
            if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
             i++;

            printInvalidXalanOption("-XD");
          }
        }
        else if ("-XJ".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            if (i + 1 < argv.length && argv[i+1].charAt(0) != '-')
            {
              tfactory.setAttribute("generate-translet", "true");
              tfactory.setAttribute("jar-name", argv[++i]);
            }
            else
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                  new Object[]{ "-XJ" }));  }
          else
          {
            if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
             i++;

            printInvalidXalanOption("-XJ");
          }

        }
        else if ("-XP".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            if (i + 1 < argv.length && argv[i+1].charAt(0) != '-')
              tfactory.setAttribute("package-name", argv[++i]);
            else
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
                  new Object[]{ "-XP" }));  }
          else
          {
            if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-')
             i++;

            printInvalidXalanOption("-XP");
          }

        }
        else if ("-XN".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            tfactory.setAttribute("enable-inlining", "true");
          }
          else
            printInvalidXalanOption("-XN");
        }
        else if ("-XX".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            tfactory.setAttribute("debug", "true");
          }
          else
            printInvalidXalanOption("-XX");
        }
        else if ("-XT".equalsIgnoreCase(argv[i]))
        {
          if (useXSLTC)
          {
            tfactory.setAttribute("auto-translet", "true");
          }
          else
            printInvalidXalanOption("-XT");
        }
        else if ("-SECURE".equalsIgnoreCase(argv[i]))
        {
          isSecureProcessing = true;
          try
          {
            tfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
          }
          catch (TransformerConfigurationException e) {}
        }
        else
          System.err.println(
            XSLMessages.createMessage(
              XSLTErrorResources.ER_INVALID_OPTION, new Object[]{ argv[i] }));  }

      if (inFileName == null && xslFileName == null)
      {
          msg = resbundle.getString("xslProc_no_input");
        System.err.println(msg);
        doExit(msg);
      }

      try
      {
        long start = System.currentTimeMillis();

        if (null != dumpFileName)
        {
          dumpWriter = new PrintWriter(new FileWriter(dumpFileName));
        }

        Templates stylesheet = null;

        if (null != xslFileName)
        {
          if (flavor.equals("d2d"))
          {

            DocumentBuilderFactory dfactory =
              DocumentBuilderFactory.newInstance();

            dfactory.setNamespaceAware(true);

            if (isSecureProcessing)
            {
              try
              {
                dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
              }
              catch (ParserConfigurationException pce) {}
            }

            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Node xslDOM = docBuilder.parse(new InputSource(xslFileName));

            stylesheet = tfactory.newTemplates(new DOMSource(xslDOM,
                    xslFileName));
          }
          else
          {
            stylesheet = tfactory.newTemplates(new StreamSource(xslFileName));
            }
        }

        PrintWriter resultWriter;
        StreamResult strResult;

        if (null != outFileName)
        {
          strResult = new StreamResult(new FileOutputStream(outFileName));
          strResult.setSystemId(outFileName);
        }
        else
        {
          strResult = new StreamResult(System.out);
          }

        SAXTransformerFactory stf = (SAXTransformerFactory) tfactory;

        if (null == stylesheet)
        {
          Source source =
            stf.getAssociatedStylesheet(new StreamSource(inFileName), media,
                                        null, null);

          if (null != source)
            stylesheet = tfactory.newTemplates(source);
          else
          {
            if (null != media)
              throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_STYLESHEET_IN_MEDIA, new Object[]{inFileName, media})); else
              throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_STYLESHEET_PI, new Object[]{inFileName})); }
        }

        if (null != stylesheet)
        {
          Transformer transformer = flavor.equals("th") ? null : stylesheet.newTransformer();
          transformer.setErrorListener(new DefaultErrorHandler());

          if (null != outputType)
          {
            transformer.setOutputProperty(OutputKeys.METHOD, outputType);
          }

          int nParams = params.size();

          for (int i = 0; i < nParams; i += 2)
          {
            transformer.setParameter((String) params.elementAt(i),
                                     (String) params.elementAt(i + 1));
          }

          if (uriResolver != null)
            transformer.setURIResolver(uriResolver);

          if (null != inFileName)
          {
            if (flavor.equals("d2d"))
            {

              DocumentBuilderFactory dfactory =
                DocumentBuilderFactory.newInstance();

              dfactory.setCoalescing(true);
              dfactory.setNamespaceAware(true);

              if (isSecureProcessing)
              {
                try
                {
                  dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                }
                catch (ParserConfigurationException pce) {}
              }

              DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

              if (entityResolver != null)
                docBuilder.setEntityResolver(entityResolver);

              Node xmlDoc = docBuilder.parse(new InputSource(inFileName));
              Document doc = docBuilder.newDocument();
              org.w3c.dom.DocumentFragment outNode =
                doc.createDocumentFragment();

              transformer.transform(new DOMSource(xmlDoc, inFileName),
                                    new DOMResult(outNode));

              Transformer serializer = stf.newTransformer();
              serializer.setErrorListener(new DefaultErrorHandler());

              Properties serializationProps =
                stylesheet.getOutputProperties();

              serializer.setOutputProperties(serializationProps);

              if (contentHandler != null)
              {
                SAXResult result = new SAXResult(contentHandler);

                serializer.transform(new DOMSource(outNode), result);
              }
              else
                serializer.transform(new DOMSource(outNode), strResult);
            }
            else if (flavor.equals("th"))
            {
              for (int i = 0; i < 1; i++) {
              XMLReader reader = null;

              try
              {
                javax.xml.parsers.SAXParserFactory factory =
                  javax.xml.parsers.SAXParserFactory.newInstance();

                factory.setNamespaceAware(true);

                if (isSecureProcessing)
                {
                  try
                  {
                    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                  }
                  catch (org.xml.sax.SAXException se) {}
                }

                javax.xml.parsers.SAXParser jaxpParser =
                  factory.newSAXParser();

                reader = jaxpParser.getXMLReader();
              }
              catch (javax.xml.parsers.ParserConfigurationException ex)
              {
                throw new org.xml.sax.SAXException(ex);
              }
              catch (javax.xml.parsers.FactoryConfigurationError ex1)
              {
                throw new org.xml.sax.SAXException(ex1.toString());
              }
              catch (NoSuchMethodError ex2){}
              catch (AbstractMethodError ame){}

              if (null == reader)
              {
                reader = XMLReaderFactory.createXMLReader();
              }

              TransformerHandler th = stf.newTransformerHandler(stylesheet);

              reader.setContentHandler(th);
              reader.setDTDHandler(th);

              if(th instanceof org.xml.sax.ErrorHandler)
                reader.setErrorHandler((org.xml.sax.ErrorHandler)th);

              try
              {
                reader.setProperty(
                  "http:}
              catch (org.xml.sax.SAXNotRecognizedException e){}
              catch (org.xml.sax.SAXNotSupportedException e){}
              try
              {
                reader.setFeature("http:true);
              } catch (org.xml.sax.SAXException se) {}

              th.setResult(strResult);

              reader.parse(new InputSource(inFileName));
              }
            }
            else
            {
              if (entityResolver != null)
              {
                XMLReader reader = null;

                try
                {
                  javax.xml.parsers.SAXParserFactory factory =
                    javax.xml.parsers.SAXParserFactory.newInstance();

                  factory.setNamespaceAware(true);

                  if (isSecureProcessing)
                  {
                    try
                    {
                      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                    }
                    catch (org.xml.sax.SAXException se) {}
                  }

                  javax.xml.parsers.SAXParser jaxpParser =
                    factory.newSAXParser();

                  reader = jaxpParser.getXMLReader();
                }
                catch (javax.xml.parsers.ParserConfigurationException ex)
                {
                  throw new org.xml.sax.SAXException(ex);
                }
                catch (javax.xml.parsers.FactoryConfigurationError ex1)
                {
                  throw new org.xml.sax.SAXException(ex1.toString());
                }
                catch (NoSuchMethodError ex2){}
                catch (AbstractMethodError ame){}

                if (null == reader)
                {
                  reader = XMLReaderFactory.createXMLReader();
                }

                reader.setEntityResolver(entityResolver);

                if (contentHandler != null)
                {
                  SAXResult result = new SAXResult(contentHandler);

                  transformer.transform(
                    new SAXSource(reader, new InputSource(inFileName)),
                    result);
                }
                else
                {
                  transformer.transform(
                    new SAXSource(reader, new InputSource(inFileName)),
                    strResult);
                }
              }
              else if (contentHandler != null)
              {
                SAXResult result = new SAXResult(contentHandler);

                transformer.transform(new StreamSource(inFileName), result);
              }
              else
              {
                transformer.transform(new StreamSource(inFileName),
                                      strResult);
                }
            }
          }
          else
          {
            StringReader reader =
              new StringReader("<?xml version=\"1.0\"?> <doc/>");

            transformer.transform(new StreamSource(reader), strResult);
          }
        }
        else
        {
msg = XSLMessages.createMessage(
                XSLTErrorResources.ER_NOT_SUCCESSFUL, null);
          diagnosticsWriter.println(msg);
          doExit(msg);
        }

        if (null != outFileName && strResult!=null)
        {
          java.io.OutputStream out = strResult.getOutputStream();
          java.io.Writer writer = strResult.getWriter();
          try
          {
            if (out != null) out.close();
            if (writer != null) writer.close();
          }
          catch(java.io.IOException ie) {}
        }

        long stop = System.currentTimeMillis();
        long millisecondsDuration = stop - start;

        if (doDiag)
        {
                Object[] msgArgs = new Object[]{ inFileName, xslFileName, new Long(millisecondsDuration) };
            msg = XSLMessages.createMessage("diagTiming", msgArgs);
                diagnosticsWriter.println('\n');
                diagnosticsWriter.println(msg);
        }

      }
      catch (Throwable throwable)
      {
        while (throwable
               instanceof com.sun.org.apache.xml.internal.utils.WrappedRuntimeException)
        {
          throwable =
            ((com.sun.org.apache.xml.internal.utils.WrappedRuntimeException) throwable).getException();
        }

        if ((throwable instanceof NullPointerException)
                || (throwable instanceof ClassCastException))
          doStackDumpOnError = true;

        diagnosticsWriter.println();

        if (doStackDumpOnError)
          throwable.printStackTrace(dumpWriter);
        else
        {
          DefaultErrorHandler.printLocation(diagnosticsWriter, throwable);
          diagnosticsWriter.println(
            XSLMessages.createMessage(XSLTErrorResources.ER_XSLT_ERROR, null)
            + " (" + throwable.getClass().getName() + "): "
            + throwable.getMessage());
        }

        if (null != dumpFileName)
        {
          dumpWriter.close();
        }

        doExit(throwable.getMessage());
      }

      if (null != dumpFileName)
      {
        dumpWriter.close();
      }

      if (null != diagnosticsWriter)
      {

        }

      }
  }


  static void doExit(String msg)
  {
    throw new RuntimeException(msg);
  }


  private static void waitForReturnKey(ResourceBundle resbundle)
  {
    System.out.println(resbundle.getString("xslProc_return_to_continue"));
    try
    {
      while (System.in.read() != '\n');
    }
    catch (java.io.IOException e) { }
  }


  private static void printInvalidXSLTCOption(String option)
  {
    System.err.println(XSLMessages.createMessage("xslProc_invalid_xsltc_option", new Object[]{option}));
  }


  private static void printInvalidXalanOption(String option)
  {
    System.err.println(XSLMessages.createMessage("xslProc_invalid_xalan_option", new Object[]{option}));
  }
}
