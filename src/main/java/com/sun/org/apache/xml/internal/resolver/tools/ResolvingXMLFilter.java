
package com.sun.org.apache.xml.internal.resolver.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;


public class ResolvingXMLFilter extends XMLFilterImpl {

  public static boolean suppressExplanation = false;


  CatalogManager catalogManager = CatalogManager.getStaticManager();


  private CatalogResolver catalogResolver = null;


  private CatalogResolver piCatalogResolver = null;


  private boolean allowXMLCatalogPI = false;


  private boolean oasisXMLCatalogPI = false;


  private URL baseURL = null;


  public ResolvingXMLFilter() {
    super();
    catalogResolver = new CatalogResolver(catalogManager);
  }


  public ResolvingXMLFilter(XMLReader parent) {
    super(parent);
    catalogResolver = new CatalogResolver(catalogManager);
  }


  public ResolvingXMLFilter(CatalogManager manager) {
    super();
    catalogManager = manager;
    catalogResolver = new CatalogResolver(catalogManager);
  }


  public ResolvingXMLFilter(XMLReader parent, CatalogManager manager) {
    super(parent);
    catalogManager = manager;
    catalogResolver = new CatalogResolver(catalogManager);
  }


  public Catalog getCatalog() {
    return catalogResolver.getCatalog();
  }


  public void parse(InputSource input)
    throws IOException, SAXException {
    allowXMLCatalogPI = true;

    setupBaseURI(input.getSystemId());

    try {
      super.parse(input);
    } catch (InternalError ie) {
      explain(input.getSystemId());
      throw ie;
    }
  }


  public void parse(String systemId)
    throws IOException, SAXException {
    allowXMLCatalogPI = true;

    setupBaseURI(systemId);

    try {
      super.parse(systemId);
    } catch (InternalError ie) {
      explain(systemId);
      throw ie;
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


  public void notationDecl (String name, String publicId, String systemId)
    throws SAXException {
    allowXMLCatalogPI = false;
    super.notationDecl(name,publicId,systemId);
  }


  public void unparsedEntityDecl (String name,
                                  String publicId,
                                  String systemId,
                                  String notationName)
    throws SAXException {
    allowXMLCatalogPI = false;
    super.unparsedEntityDecl (name, publicId, systemId, notationName);
  }


  public void startElement (String uri, String localName, String qName,
                            Attributes atts)
    throws SAXException {
    allowXMLCatalogPI = false;
    super.startElement(uri,localName,qName,atts);
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
      super.processingInstruction(target, pidata);
    }
  }


  private void setupBaseURI(String systemId) {
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
      System.out.println("XMLReader probably encountered bad URI in " + systemId);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    }
    suppressExplanation = true;
  }
}
