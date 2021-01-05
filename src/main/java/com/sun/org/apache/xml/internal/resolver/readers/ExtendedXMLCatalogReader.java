
package com.sun.org.apache.xml.internal.resolver.readers;

import java.util.Vector;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.Resolver;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;

import org.xml.sax.*;
import org.w3c.dom.*;


public class ExtendedXMLCatalogReader extends OASISXMLCatalogReader {

  public static final String extendedNamespaceName = "http:public void startElement (String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts)
    throws SAXException {

    boolean inExtension = inExtensionNamespace();

    super.startElement(namespaceURI, localName, qName, atts);

    int entryType = -1;
    Vector entryArgs = new Vector();

    if (namespaceURI != null && extendedNamespaceName.equals(namespaceURI)
        && !inExtension) {
      if (atts.getValue("xml:base") != null) {
        String baseURI = atts.getValue("xml:base");
        entryType = Catalog.BASE;
        entryArgs.add(baseURI);
        baseURIStack.push(baseURI);

        debug.message(4, "xml:base", baseURI);

        try {
          CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
          catalog.addEntry(ce);
        } catch (CatalogException cex) {
          if (cex.getExceptionType() == CatalogException.INVALID_ENTRY_TYPE) {
            debug.message(1, "Invalid catalog entry type", localName);
          } else if (cex.getExceptionType() == CatalogException.INVALID_ENTRY) {
            debug.message(1, "Invalid catalog entry (base)", localName);
          }
        }

        entryType = -1;
        entryArgs = new Vector();
      } else {
        baseURIStack.push(baseURIStack.peek());
      }

      if (localName.equals("uriSuffix")) {
        if (checkAttributes(atts, "suffix", "uri")) {
          entryType = Resolver.URISUFFIX;
          entryArgs.add(atts.getValue("suffix"));
          entryArgs.add(atts.getValue("uri"));

          debug.message(4, "uriSuffix",
                        atts.getValue("suffix"),
                        atts.getValue("uri"));
        }
      } else if (localName.equals("systemSuffix")) {
        if (checkAttributes(atts, "suffix", "uri")) {
          entryType = Resolver.SYSTEMSUFFIX;
          entryArgs.add(atts.getValue("suffix"));
          entryArgs.add(atts.getValue("uri"));

          debug.message(4, "systemSuffix",
                        atts.getValue("suffix"),
                        atts.getValue("uri"));
        }
      } else {
        debug.message(1, "Invalid catalog entry type", localName);
      }

      if (entryType >= 0) {
        try {
          CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
          catalog.addEntry(ce);
        } catch (CatalogException cex) {
          if (cex.getExceptionType() == CatalogException.INVALID_ENTRY_TYPE) {
            debug.message(1, "Invalid catalog entry type", localName);
          } else if (cex.getExceptionType() == CatalogException.INVALID_ENTRY) {
            debug.message(1, "Invalid catalog entry", localName);
          }
        }
      }
    }
  }


  public void endElement (String namespaceURI,
                          String localName,
                          String qName)
    throws SAXException {

    super.endElement(namespaceURI, localName, qName);

    boolean inExtension = inExtensionNamespace();

    int entryType = -1;
    Vector entryArgs = new Vector();

    if (namespaceURI != null
        && (extendedNamespaceName.equals(namespaceURI))
        && !inExtension) {

      String popURI = (String) baseURIStack.pop();
      String baseURI = (String) baseURIStack.peek();

      if (!baseURI.equals(popURI)) {
        entryType = catalog.BASE;
        entryArgs.add(baseURI);

        debug.message(4, "(reset) xml:base", baseURI);

        try {
          CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
          catalog.addEntry(ce);
        } catch (CatalogException cex) {
          if (cex.getExceptionType() == CatalogException.INVALID_ENTRY_TYPE) {
            debug.message(1, "Invalid catalog entry type", localName);
          } else if (cex.getExceptionType() == CatalogException.INVALID_ENTRY) {
            debug.message(1, "Invalid catalog entry (rbase)", localName);
          }
        }
      }
    }
  }
}
