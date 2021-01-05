
package com.sun.org.apache.xml.internal.resolver.readers;

import java.util.Vector;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import org.xml.sax.*;

import javax.xml.parsers.*;


public class XCatalogReader extends SAXCatalogReader implements SAXCatalogParser {

  protected Catalog catalog = null;


  public void setCatalog (Catalog catalog) {
    this.catalog = catalog;
  }


  public Catalog getCatalog () {
    return catalog;
  }


  public XCatalogReader(SAXParserFactory parserFactory) {
    super(parserFactory);
  }

  public void setDocumentLocator (Locator locator) {
    return;
  }


  public void startDocument ()
    throws SAXException {
    return;
  }


  public void endDocument ()
    throws SAXException {
    return;
  }


  public void startElement (String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts)
    throws SAXException {

    int entryType = -1;
    Vector entryArgs = new Vector();

    if (localName.equals("Base")) {
      entryType = catalog.BASE;
      entryArgs.add(atts.getValue("HRef"));

      catalog.getCatalogManager().debug.message(4, "Base", atts.getValue("HRef"));
    } else if (localName.equals("Delegate")) {
      entryType = catalog.DELEGATE_PUBLIC;
      entryArgs.add(atts.getValue("PublicId"));
      entryArgs.add(atts.getValue("HRef"));

      catalog.getCatalogManager().debug.message(4, "Delegate",
                    PublicId.normalize(atts.getValue("PublicId")),
                    atts.getValue("HRef"));
    } else if (localName.equals("Extend")) {
      entryType = catalog.CATALOG;
      entryArgs.add(atts.getValue("HRef"));

      catalog.getCatalogManager().debug.message(4, "Extend", atts.getValue("HRef"));
    } else if (localName.equals("Map")) {
      entryType = catalog.PUBLIC;
      entryArgs.add(atts.getValue("PublicId"));
      entryArgs.add(atts.getValue("HRef"));

      catalog.getCatalogManager().debug.message(4, "Map",
                    PublicId.normalize(atts.getValue("PublicId")),
                    atts.getValue("HRef"));
    } else if (localName.equals("Remap")) {
      entryType = catalog.SYSTEM;
      entryArgs.add(atts.getValue("SystemId"));
      entryArgs.add(atts.getValue("HRef"));

      catalog.getCatalogManager().debug.message(4, "Remap",
                    atts.getValue("SystemId"),
                    atts.getValue("HRef"));
    } else if (localName.equals("XMLCatalog")) {
      } else {
      catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
    }

    if (entryType >= 0) {
      try {
        CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
        catalog.addEntry(ce);
      } catch (CatalogException cex) {
        if (cex.getExceptionType() == CatalogException.INVALID_ENTRY_TYPE) {
          catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
        } else if (cex.getExceptionType() == CatalogException.INVALID_ENTRY) {
          catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", localName);
        }
      }
    }
    }


    public void endElement (String namespaceURI,
                            String localName,
                            String qName)
      throws SAXException {
      return;
    }


  public void characters (char ch[], int start, int length)
    throws SAXException {
    return;
  }


  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException {
    return;
  }


  public void processingInstruction (String target, String data)
    throws SAXException {
    return;
  }
}
