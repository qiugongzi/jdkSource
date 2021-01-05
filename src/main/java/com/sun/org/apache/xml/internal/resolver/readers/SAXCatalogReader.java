

package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import sun.reflect.misc.ReflectUtil;


public class SAXCatalogReader implements CatalogReader, ContentHandler, DocumentHandler {

  protected SAXParserFactory parserFactory = null;


  protected String parserClass = null;


  protected Map<String, String> namespaceMap = new HashMap<>();


  private SAXCatalogParser saxParser = null;


  private boolean abandonHope = false;


  private Catalog catalog;


  public void setParserFactory(SAXParserFactory parserFactory) {
    this.parserFactory = parserFactory;
  }


  public void setParserClass(String parserClass) {
    this.parserClass = parserClass;
  }


  public SAXParserFactory getParserFactory() {
    return parserFactory;
  }


  public String getParserClass() {
    return parserClass;
  }


  protected Debug debug = CatalogManager.getStaticManager().debug;


  public SAXCatalogReader() {
    parserFactory = null;
    parserClass = null;
  }


  public SAXCatalogReader(SAXParserFactory parserFactory) {
    this.parserFactory = parserFactory;
  }


  public SAXCatalogReader(String parserClass) {
    this.parserClass = parserClass;
  }


  public void setCatalogParser(String namespaceURI,
                               String rootElement,
                               String parserClass) {
    if (namespaceURI == null) {
      namespaceMap.put(rootElement, parserClass);
    } else {
      namespaceMap.put("{"+namespaceURI+"}"+rootElement, parserClass);
    }
  }


  public String getCatalogParser(String namespaceURI,
                                 String rootElement) {
    if (namespaceURI == null) {
      return namespaceMap.get(rootElement);
    } else {
      return namespaceMap.get("{"+namespaceURI+"}"+rootElement);
    }
  }


  public void readCatalog(Catalog catalog, String fileUrl)
    throws MalformedURLException, IOException,
           CatalogException {

    URL url = null;

    try {
      url = new URL(fileUrl);
    } catch (MalformedURLException e) {
      url = new URL("file:}

    debug = catalog.getCatalogManager().debug;

    try {
      URLConnection urlCon = url.openConnection();
      readCatalog(catalog, urlCon.getInputStream());
    } catch (FileNotFoundException e) {
      catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found",
                    url.toString());
    }
  }


  public void readCatalog(Catalog catalog, InputStream is)
    throws IOException, CatalogException {

    if (parserFactory == null && parserClass == null) {
      debug.message(1, "Cannot read SAX catalog without a parser");
      throw new CatalogException(CatalogException.UNPARSEABLE);
    }

    debug = catalog.getCatalogManager().debug;
    EntityResolver bResolver = catalog.getCatalogManager().getBootstrapResolver();

    this.catalog = catalog;

    try {
      if (parserFactory != null) {
        SAXParser parser = parserFactory.newSAXParser();
        SAXParserHandler spHandler = new SAXParserHandler();
        spHandler.setContentHandler(this);
        if (bResolver != null) {
          spHandler.setEntityResolver(bResolver);
        }
        parser.parse(new InputSource(is), spHandler);
      } else {
        Parser parser = (Parser) ReflectUtil.forName(parserClass).newInstance();
        parser.setDocumentHandler(this);
        if (bResolver != null) {
          parser.setEntityResolver(bResolver);
        }
        parser.parse(new InputSource(is));
      }
    } catch (ClassNotFoundException cnfe) {
      throw new CatalogException(CatalogException.UNPARSEABLE);
    } catch (IllegalAccessException iae) {
      throw new CatalogException(CatalogException.UNPARSEABLE);
    } catch (InstantiationException ie) {
      throw new CatalogException(CatalogException.UNPARSEABLE);
    } catch (ParserConfigurationException pce) {
      throw new CatalogException(CatalogException.UNKNOWN_FORMAT);
    } catch (SAXException se) {
      Exception e = se.getException();
      UnknownHostException uhe = new UnknownHostException();
      FileNotFoundException fnfe = new FileNotFoundException();
      if (e != null) {
        if (e.getClass() == uhe.getClass()) {
          throw new CatalogException(CatalogException.PARSE_FAILED,
                                     e.toString());
        } else if (e.getClass() == fnfe.getClass()) {
          throw new CatalogException(CatalogException.PARSE_FAILED,
                                     e.toString());
        }
      }
      throw new CatalogException(se);
    }
  }

  public void setDocumentLocator (Locator locator) {
    if (saxParser != null) {
      saxParser.setDocumentLocator(locator);
    }
  }


  public void startDocument () throws SAXException {
    saxParser = null;
    abandonHope = false;
    return;
  }


  public void endDocument ()throws SAXException {
    if (saxParser != null) {
      saxParser.endDocument();
    }
  }


  public void startElement (String name,
                            AttributeList atts)
    throws SAXException {

    if (abandonHope) {
      return;
    }

    if (saxParser == null) {
      String prefix = "";
      if (name.indexOf(':') > 0) {
        prefix = name.substring(0, name.indexOf(':'));
      }

      String localName = name;
      if (localName.indexOf(':') > 0) {
        localName = localName.substring(localName.indexOf(':')+1);
      }

      String namespaceURI = null;
      if (prefix.equals("")) {
        namespaceURI = atts.getValue("xmlns");
      } else {
        namespaceURI = atts.getValue("xmlns:" + prefix);
      }

      String saxParserClass = getCatalogParser(namespaceURI,
                                               localName);

      if (saxParserClass == null) {
        abandonHope = true;
        if (namespaceURI == null) {
          debug.message(2, "No Catalog parser for " + name);
        } else {
          debug.message(2, "No Catalog parser for "
                        + "{" + namespaceURI + "}"
                        + name);
        }
        return;
      }

      try {
        saxParser = (SAXCatalogParser)
          ReflectUtil.forName(saxParserClass).newInstance();

        saxParser.setCatalog(catalog);
        saxParser.startDocument();
        saxParser.startElement(name, atts);
      } catch (ClassNotFoundException cnfe) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, cnfe.toString());
      } catch (InstantiationException ie) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, ie.toString());
      } catch (IllegalAccessException iae) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, iae.toString());
      } catch (ClassCastException cce ) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, cce.toString());
      }
    } else {
      saxParser.startElement(name, atts);
    }
  }


  public void startElement (String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts)
    throws SAXException {

    if (abandonHope) {
      return;
    }

    if (saxParser == null) {
      String saxParserClass = getCatalogParser(namespaceURI,
                                               localName);

      if (saxParserClass == null) {
        abandonHope = true;
        if (namespaceURI == null) {
          debug.message(2, "No Catalog parser for " + localName);
        } else {
          debug.message(2, "No Catalog parser for "
                        + "{" + namespaceURI + "}"
                        + localName);
        }
        return;
      }

      try {
        saxParser = (SAXCatalogParser)
          ReflectUtil.forName(saxParserClass).newInstance();

        saxParser.setCatalog(catalog);
        saxParser.startDocument();
        saxParser.startElement(namespaceURI, localName, qName, atts);
      } catch (ClassNotFoundException cnfe) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, cnfe.toString());
      } catch (InstantiationException ie) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, ie.toString());
      } catch (IllegalAccessException iae) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, iae.toString());
      } catch (ClassCastException cce ) {
        saxParser = null;
        abandonHope = true;
        debug.message(2, cce.toString());
      }
    } else {
      saxParser.startElement(namespaceURI, localName, qName, atts);
    }
  }


  public void endElement (String name) throws SAXException {
    if (saxParser != null) {
      saxParser.endElement(name);
    }
  }


  public void endElement (String namespaceURI,
                          String localName,
                          String qName) throws SAXException {
    if (saxParser != null) {
      saxParser.endElement(namespaceURI, localName, qName);
    }
  }


  public void characters (char ch[], int start, int length)
    throws SAXException {
    if (saxParser != null) {
      saxParser.characters(ch, start, length);
    }
  }


  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException {
    if (saxParser != null) {
      saxParser.ignorableWhitespace(ch, start, length);
    }
  }


  public void processingInstruction (String target, String data)
    throws SAXException {
    if (saxParser != null) {
      saxParser.processingInstruction(target, data);
    }
  }


  public void startPrefixMapping (String prefix, String uri)
    throws SAXException {
    if (saxParser != null) {
      saxParser.startPrefixMapping (prefix, uri);
    }
  }


  public void endPrefixMapping (String prefix)
    throws SAXException {
    if (saxParser != null) {
      saxParser.endPrefixMapping (prefix);
    }
  }


  public void skippedEntity (String name)
    throws SAXException {
    if (saxParser != null) {
      saxParser.skippedEntity(name);
    }
  }
}
