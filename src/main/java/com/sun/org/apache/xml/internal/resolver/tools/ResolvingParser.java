

package com.sun.org.apache.xml.internal.resolver.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Locale;

import org.xml.sax.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.AttributeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import jdk.xml.internal.JdkXmlUtils;


public class ResolvingParser
  implements Parser, DTDHandler, DocumentHandler, EntityResolver {

  public static boolean namespaceAware = true;


  public static boolean validating = false;


  public static boolean suppressExplanation = false;


  private SAXParser saxParser = null;


  private Parser parser = null;


  private DocumentHandler documentHandler = null;


  private DTDHandler dtdHandler = null;


  private CatalogManager catalogManager = CatalogManager.getStaticManager();


  private CatalogResolver catalogResolver = null;


  private CatalogResolver piCatalogResolver = null;


  private boolean allowXMLCatalogPI = false;


  private boolean oasisXMLCatalogPI = false;


  private URL baseURL = null;


  public ResolvingParser() {
    initParser();
  }


  public ResolvingParser(CatalogManager manager) {
    catalogManager = manager;
    initParser();
  }


  private void initParser() {
    catalogResolver = new CatalogResolver(catalogManager);
    SAXParserFactory spf = JdkXmlUtils.getSAXFactory(catalogManager.overrideDefaultParser());
    spf.setValidating(validating);

    try {
      saxParser = spf.newSAXParser();
      parser = saxParser.getParser();
      documentHandler = null;
      dtdHandler = null;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public Catalog getCatalog() {
    return catalogResolver.getCatalog();
  }


  public void parse(InputSource input)
    throws IOException,
           SAXException {
    setupParse(input.getSystemId());
    try {
      parser.parse(input);
    } catch (InternalError ie) {
      explain(input.getSystemId());
      throw ie;
    }
  }


  public void parse(String systemId)
    throws IOException,
           SAXException {
    setupParse(systemId);
    try {
      parser.parse(systemId);
    } catch (InternalError ie) {
      explain(systemId);
      throw ie;
    }
  }


  public void setDocumentHandler(DocumentHandler handler) {
    documentHandler = handler;
  }


  public void setDTDHandler(DTDHandler handler) {
    dtdHandler = handler;
  }


  public void setEntityResolver(EntityResolver resolver) {
    }


  public void setErrorHandler(ErrorHandler handler) {
    parser.setErrorHandler(handler);
  }


  public void setLocale(Locale locale) throws SAXException {
    parser.setLocale(locale);
  }


  public void characters(char[] ch, int start, int length)
    throws SAXException {
    if (documentHandler != null) {
      documentHandler.characters(ch,start,length);
    }
  }


  public void endDocument() throws SAXException {
    if (documentHandler != null) {
      documentHandler.endDocument();
    }
  }


  public void endElement(String name) throws SAXException {
    if (documentHandler != null) {
      documentHandler.endElement(name);
    }
  }


  public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException {
    if (documentHandler != null) {
      documentHandler.ignorableWhitespace(ch,start,length);
    }
  }


  public void processingInstruction(String target, String pidata)
    throws SAXException {

    if (target.equals("oasis-xml-catalog")) {
      URL catalog = null;
      String data = pidata;

      int pos = data.indexOf("catalog=");
      if (pos >= 0) {
        data = data.substring(pos+8);
        if (data.length() > 1) {
          String quote = data.substring(0,1);
          data = data.substring(1);
          pos = data.indexOf(quote);
          if (pos >= 0) {
            data = data.substring(0, pos);
            try {
              if (baseURL != null) {
                catalog = new URL(baseURL, data);
              } else {
                catalog = new URL(data);
              }
            } catch (MalformedURLException mue) {
              }
          }
        }
      }

      if (allowXMLCatalogPI) {
        if (catalogManager.getAllowOasisXMLCatalogPI()) {
          catalogManager.debug.message(4,"oasis-xml-catalog PI", pidata);

          if (catalog != null) {
            try {
              catalogManager.debug.message(4,"oasis-xml-catalog", catalog.toString());
              oasisXMLCatalogPI = true;

              if (piCatalogResolver == null) {
                piCatalogResolver = new CatalogResolver(true);
              }

              piCatalogResolver.getCatalog().parseCatalog(catalog.toString());
            } catch (Exception e) {
              catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: "
                            + catalog.toString());
            }
          } else {
            catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + pidata);
          }
        } else {
          catalogManager.debug.message(4,"PI oasis-xml-catalog ignored: " + pidata);
        }
      } else {
        catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: "
                      + pidata);
      }
    } else {
      if (documentHandler != null) {
        documentHandler.processingInstruction(target, pidata);
      }
    }
  }


  public void setDocumentLocator(Locator locator) {
    if (documentHandler != null) {
      documentHandler.setDocumentLocator(locator);
    }
  }


  public void startDocument() throws SAXException {
    if (documentHandler != null) {
      documentHandler.startDocument();
    }
  }


  public void startElement(String name, AttributeList atts)
    throws SAXException {
    allowXMLCatalogPI = false;
    if (documentHandler != null) {
      documentHandler.startElement(name,atts);
    }
  }


  public void notationDecl (String name, String publicId, String systemId)
    throws SAXException {
    allowXMLCatalogPI = false;
    if (dtdHandler != null) {
      dtdHandler.notationDecl(name,publicId,systemId);
    }
  }


  public void unparsedEntityDecl (String name,
                                  String publicId,
                                  String systemId,
                                  String notationName)
    throws SAXException {
    allowXMLCatalogPI = false;
    if (dtdHandler != null) {
      dtdHandler.unparsedEntityDecl (name, publicId, systemId, notationName);
    }
  }


  public InputSource resolveEntity (String publicId, String systemId) {
    allowXMLCatalogPI = false;
    String resolved = catalogResolver.getResolvedEntity(publicId, systemId);

    if (resolved == null && piCatalogResolver != null) {
      resolved = piCatalogResolver.getResolvedEntity(publicId, systemId);
    }

    if (resolved != null) {
      try {
        InputSource iSource = new InputSource(resolved);
        iSource.setPublicId(publicId);

        URL url = new URL(resolved);
        InputStream iStream = url.openStream();
        iSource.setByteStream(iStream);

        return iSource;
      } catch (Exception e) {
        catalogManager.debug.message(1, "Failed to create InputSource", resolved);
        return null;
      }
    } else {
      return null;
    }
  }


  private void setupParse(String systemId) {
    allowXMLCatalogPI = true;
    parser.setEntityResolver(this);
    parser.setDocumentHandler(this);
    parser.setDTDHandler(this);

    URL cwd = null;

    try {
      cwd = FileURL.makeURL("basename");
    } catch (MalformedURLException mue) {
      cwd = null;
    }

    try {
      baseURL = new URL(systemId);
    } catch (MalformedURLException mue) {
      if (cwd != null) {
        try {
          baseURL = new URL(cwd, systemId);
        } catch (MalformedURLException mue2) {
          baseURL = null;
        }
      } else {
        baseURL = null;
      }
    }
  }


  private void explain(String systemId) {
    if (!suppressExplanation) {
      System.out.println("Parser probably encountered bad URI in " + systemId);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    }
  }
}
