

package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import jdk.xml.internal.JdkXmlUtils;


public class CatalogResolver implements EntityResolver, URIResolver {

  public boolean namespaceAware = true;


  public boolean validating = false;


  private Catalog catalog = null;


  private CatalogManager catalogManager = CatalogManager.getStaticManager();


  public CatalogResolver() {
    initializeCatalogs(false);
  }


  public CatalogResolver(boolean privateCatalog) {
    initializeCatalogs(privateCatalog);
  }


  public CatalogResolver(CatalogManager manager) {
    catalogManager = manager;
    initializeCatalogs(!catalogManager.getUseStaticCatalog());
  }


  private void initializeCatalogs(boolean privateCatalog) {
    catalog = catalogManager.getCatalog();
  }


  public Catalog getCatalog() {
    return catalog;
  }


  public String getResolvedEntity (String publicId, String systemId) {
    String resolved = null;

    if (catalog == null) {
      catalogManager.debug.message(1, "Catalog resolution attempted with null catalog; ignored");
      return null;
    }

    if (systemId != null) {
      try {
        resolved = catalog.resolveSystem(systemId);
      } catch (MalformedURLException me) {
        catalogManager.debug.message(1, "Malformed URL exception trying to resolve",
                      publicId);
        resolved = null;
      } catch (IOException ie) {
        catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
        resolved = null;
      }
    }

    if (resolved == null) {
      if (publicId != null) {
        try {
          resolved = catalog.resolvePublic(publicId, systemId);
        } catch (MalformedURLException me) {
          catalogManager.debug.message(1, "Malformed URL exception trying to resolve",
                        publicId);
        } catch (IOException ie) {
          catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
        }
      }

      if (resolved != null) {
        catalogManager.debug.message(2, "Resolved public", publicId, resolved);
      }
    } else {
      catalogManager.debug.message(2, "Resolved system", systemId, resolved);
    }

    return resolved;
  }


  public InputSource resolveEntity (String publicId, String systemId) {
    String resolved = getResolvedEntity(publicId, systemId);

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
    }

    return null;
  }


  public Source resolve(String href, String base)
    throws TransformerException {

    String uri = href;
    String fragment = null;
    int hashPos = href.indexOf("#");
    if (hashPos >= 0) {
      uri = href.substring(0, hashPos);
      fragment = href.substring(hashPos+1);
    }

    String result = null;

    try {
      result = catalog.resolveURI(href);
    } catch (Exception e) {
      }

    if (result == null) {
      try {
        URL url = null;

        if (base==null) {
          url = new URL(uri);
          result = url.toString();
        } else {
          URL baseURL = new URL(base);
          url = (href.length()==0 ? baseURL : new URL(baseURL, uri));
          result = url.toString();
        }
      } catch (java.net.MalformedURLException mue) {
        String absBase = makeAbsolute(base);
        if (!absBase.equals(base)) {
          return resolve(href, absBase);
        } else {
          throw new TransformerException("Malformed URL "
                                         + href + "(base " + base + ")",
                                         mue);
        }
      }
    }

    catalogManager.debug.message(2, "Resolved URI", href, result);

    SAXSource source = new SAXSource();
    source.setInputSource(new InputSource(result));
    setEntityResolver(source);
    return source;
  }


  private void setEntityResolver(SAXSource source) throws TransformerException {
    XMLReader reader = source.getXMLReader();
    if (reader == null) {
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(catalogManager.overrideDefaultParser());
      try {
        reader = spf.newSAXParser().getXMLReader();
      }
      catch (ParserConfigurationException ex) {
        throw new TransformerException(ex);
      }
      catch (SAXException ex) {
        throw new TransformerException(ex);
      }
    }
    reader.setEntityResolver(this);
    source.setXMLReader(reader);
  }


  private String makeAbsolute(String uri) {
    if (uri == null) {
      uri = "";
    }

    try {
      URL url = new URL(uri);
      return url.toString();
    } catch (MalformedURLException mue) {
      try {
        URL fileURL = FileURL.makeURL(uri);
        return fileURL.toString();
      } catch (MalformedURLException mue2) {
        return uri;
      }
    }
  }
}
