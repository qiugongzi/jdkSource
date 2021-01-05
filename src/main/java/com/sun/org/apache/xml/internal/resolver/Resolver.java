

package com.sun.org.apache.xml.internal.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.OASISXMLCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import jdk.xml.internal.JdkXmlUtils;


public class Resolver extends Catalog {

  public static final int URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);


  public static final int SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);


  public static final int RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);


  public static final int SYSTEMREVERSE
    = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);


  public void setupReaders() {
    SAXParserFactory spf = JdkXmlUtils.getSAXFactory(catalogManager.overrideDefaultParser());
    spf.setValidating(false);

    SAXCatalogReader saxReader = new SAXCatalogReader(spf);

    saxReader.setCatalogParser(null, "XMLCatalog",
                               "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");

    saxReader.setCatalogParser(OASISXMLCatalogReader.namespaceName,
                               "catalog",
                               "com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader");

    addReader("application/xml", saxReader);

    TR9401CatalogReader textReader = new TR9401CatalogReader();
    addReader("text/plain", textReader);
  }


  public void addEntry(CatalogEntry entry) {
    int type = entry.getEntryType();

    if (type == URISUFFIX) {
      String suffix = normalizeURI(entry.getEntryArg(0));
      String fsi = makeAbsolute(normalizeURI(entry.getEntryArg(1)));

      entry.setEntryArg(1, fsi);

      catalogManager.debug.message(4, "URISUFFIX", suffix, fsi);
    } else if (type == SYSTEMSUFFIX) {
      String suffix = normalizeURI(entry.getEntryArg(0));
      String fsi = makeAbsolute(normalizeURI(entry.getEntryArg(1)));

      entry.setEntryArg(1, fsi);

      catalogManager.debug.message(4, "SYSTEMSUFFIX", suffix, fsi);
    }

    super.addEntry(entry);
  }


  public String resolveURI(String uri)
    throws MalformedURLException, IOException {

    String resolved = super.resolveURI(uri);
    if (resolved != null) {
      return resolved;
    }

    Enumeration en = catalogEntries.elements();
    while (en.hasMoreElements()) {
      CatalogEntry e = (CatalogEntry) en.nextElement();
      if (e.getEntryType() == RESOLVER) {
        resolved = resolveExternalSystem(uri, e.getEntryArg(0));
        if (resolved != null) {
          return resolved;
        }
      } else if (e.getEntryType() == URISUFFIX) {
        String suffix = e.getEntryArg(0);
        String result = e.getEntryArg(1);

        if (suffix.length() <= uri.length()
            && uri.substring(uri.length()-suffix.length()).equals(suffix)) {
          return result;
        }
      }
    }

    return resolveSubordinateCatalogs(Catalog.URI,
                                      null,
                                      null,
                                      uri);
  }


  public String resolveSystem(String systemId)
    throws MalformedURLException, IOException {

    String resolved = super.resolveSystem(systemId);
    if (resolved != null) {
      return resolved;
    }

    Enumeration en = catalogEntries.elements();
    while (en.hasMoreElements()) {
      CatalogEntry e = (CatalogEntry) en.nextElement();
      if (e.getEntryType() == RESOLVER) {
        resolved = resolveExternalSystem(systemId, e.getEntryArg(0));
        if (resolved != null) {
          return resolved;
        }
      } else if (e.getEntryType() == SYSTEMSUFFIX) {
        String suffix = e.getEntryArg(0);
        String result = e.getEntryArg(1);

        if (suffix.length() <= systemId.length()
            && systemId.substring(systemId.length()-suffix.length()).equals(suffix)) {
          return result;
        }
      }
    }

    return resolveSubordinateCatalogs(Catalog.SYSTEM,
                                      null,
                                      null,
                                      systemId);
  }


  public String resolvePublic(String publicId, String systemId)
    throws MalformedURLException, IOException {

    String resolved = super.resolvePublic(publicId, systemId);
    if (resolved != null) {
      return resolved;
    }

    Enumeration en = catalogEntries.elements();
    while (en.hasMoreElements()) {
      CatalogEntry e = (CatalogEntry) en.nextElement();
      if (e.getEntryType() == RESOLVER) {
        if (systemId != null) {
          resolved = resolveExternalSystem(systemId,
                                           e.getEntryArg(0));
          if (resolved != null) {
            return resolved;
          }
        }
        resolved = resolveExternalPublic(publicId, e.getEntryArg(0));
        if (resolved != null) {
          return resolved;
        }
      }
    }

    return resolveSubordinateCatalogs(Catalog.PUBLIC,
                                      null,
                                      publicId,
                                      systemId);
  }


    protected String resolveExternalSystem(String systemId, String resolver)
        throws MalformedURLException, IOException {
        Resolver r = queryResolver(resolver, "i2l", systemId, null);
        if (r != null) {
            return r.resolveSystem(systemId);
        } else {
            return null;
        }
    }


    protected String resolveExternalPublic(String publicId, String resolver)
        throws MalformedURLException, IOException {
        Resolver r = queryResolver(resolver, "fpi2l", publicId, null);
        if (r != null) {
            return r.resolvePublic(publicId, null);
        } else {
            return null;
        }
    }


    protected Resolver queryResolver(String resolver,
                                     String command,
                                     String arg1,
                                     String arg2) {
        InputStream iStream = null;
        String RFC2483 = resolver + "?command=" + command
            + "&format=tr9401&uri=" + arg1
            + "&uri2=" + arg2;
        String line = null;

        try {
            URL url = new URL(RFC2483);

            URLConnection urlCon = url.openConnection();

            urlCon.setUseCaches(false);

            Resolver r = (Resolver) newCatalog();

            String cType = urlCon.getContentType();

            if (cType.indexOf(";") > 0) {
                cType = cType.substring(0, cType.indexOf(";"));
            }

            r.parseCatalog(cType, urlCon.getInputStream());

            return r;
        } catch (CatalogException cex) {
          if (cex.getExceptionType() == CatalogException.UNPARSEABLE) {
            catalogManager.debug.message(1, "Unparseable catalog: " + RFC2483);
          } else if (cex.getExceptionType()
                     == CatalogException.UNKNOWN_FORMAT) {
            catalogManager.debug.message(1, "Unknown catalog format: " + RFC2483);
          }
          return null;
        } catch (MalformedURLException mue) {
            catalogManager.debug.message(1, "Malformed resolver URL: " + RFC2483);
            return null;
        } catch (IOException ie) {
            catalogManager.debug.message(1, "I/O Exception opening resolver: " + RFC2483);
            return null;
        }
    }


    private Vector appendVector(Vector vec, Vector appvec) {
        if (appvec != null) {
            for (int count = 0; count < appvec.size(); count++) {
                vec.addElement(appvec.elementAt(count));
            }
        }
        return vec;
    }


    public Vector resolveAllSystemReverse(String systemId)
        throws MalformedURLException, IOException {
        Vector resolved = new Vector();

        if (systemId != null) {
            Vector localResolved = resolveLocalSystemReverse(systemId);
            resolved = appendVector(resolved, localResolved);
        }

        Vector subResolved = resolveAllSubordinateCatalogs(SYSTEMREVERSE,
                                                           null,
                                                           null,
                                                           systemId);

        return appendVector(resolved, subResolved);
    }


    public String resolveSystemReverse(String systemId)
        throws MalformedURLException, IOException {
        Vector resolved = resolveAllSystemReverse(systemId);
        if (resolved != null && resolved.size() > 0) {
            return (String) resolved.elementAt(0);
        } else {
            return null;
        }
    }


    public Vector resolveAllSystem(String systemId)
        throws MalformedURLException, IOException {
        Vector resolutions = new Vector();

        if (systemId != null) {
            Vector localResolutions = resolveAllLocalSystem(systemId);
            resolutions = appendVector(resolutions, localResolutions);
        }

        Vector subResolutions = resolveAllSubordinateCatalogs(SYSTEM,
                                                              null,
                                                              null,
                                                              systemId);
        resolutions = appendVector(resolutions, subResolutions);

        if (resolutions.size() > 0) {
            return resolutions;
        } else {
            return null;
        }
    }


    private Vector resolveAllLocalSystem(String systemId) {
        Vector map = new Vector();
        String osname = SecuritySupport.getSystemProperty("os.name");
        boolean windows = (osname.indexOf("Windows") >= 0);
        Enumeration en = catalogEntries.elements();
        while (en.hasMoreElements()) {
            CatalogEntry e = (CatalogEntry) en.nextElement();
            if (e.getEntryType() == SYSTEM
                && (e.getEntryArg(0).equals(systemId)
                    || (windows
                        && e.getEntryArg(0).equalsIgnoreCase(systemId)))) {
                map.addElement(e.getEntryArg(1));
            }
        }
        if (map.size() == 0) {
            return null;
        } else {
            return map;
        }
    }


    private Vector resolveLocalSystemReverse(String systemId) {
        Vector map = new Vector();
        String osname = SecuritySupport.getSystemProperty("os.name");
        boolean windows = (osname.indexOf("Windows") >= 0);
        Enumeration en = catalogEntries.elements();
        while (en.hasMoreElements()) {
            CatalogEntry e = (CatalogEntry) en.nextElement();
            if (e.getEntryType() == SYSTEM
                && (e.getEntryArg(1).equals(systemId)
                    || (windows
                        && e.getEntryArg(1).equalsIgnoreCase(systemId)))) {
                map.addElement(e.getEntryArg(0));
            }
        }
        if (map.size() == 0) {
            return null;
        } else {
            return map;
        }
    }


    private synchronized Vector resolveAllSubordinateCatalogs(int entityType,
                                              String entityName,
                                              String publicId,
                                              String systemId)
        throws MalformedURLException, IOException {

        Vector resolutions = new Vector();

        for (int catPos = 0; catPos < catalogs.size(); catPos++) {
            Resolver c = null;

            try {
                c = (Resolver) catalogs.elementAt(catPos);
            } catch (ClassCastException e) {
                String catfile = (String) catalogs.elementAt(catPos);
                c = (Resolver) newCatalog();

                try {
                    c.parseCatalog(catfile);
                } catch (MalformedURLException mue) {
                    catalogManager.debug.message(1, "Malformed Catalog URL", catfile);
                } catch (FileNotFoundException fnfe) {
                    catalogManager.debug.message(1, "Failed to load catalog, file not found",
                          catfile);
                } catch (IOException ioe) {
                    catalogManager.debug.message(1, "Failed to load catalog, I/O error", catfile);
                }

                catalogs.setElementAt(c, catPos);
            }

            String resolved = null;

            if (entityType == DOCTYPE) {
                resolved = c.resolveDoctype(entityName,
                                            publicId,
                                            systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            } else if (entityType == DOCUMENT) {
                resolved = c.resolveDocument();
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            } else if (entityType == ENTITY) {
                resolved = c.resolveEntity(entityName,
                                           publicId,
                                           systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            } else if (entityType == NOTATION) {
                resolved = c.resolveNotation(entityName,
                                             publicId,
                                             systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            } else if (entityType == PUBLIC) {
                resolved = c.resolvePublic(publicId, systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            } else if (entityType == SYSTEM) {
                Vector localResolutions = c.resolveAllSystem(systemId);
                resolutions = appendVector(resolutions, localResolutions);
                break;
            } else if (entityType == SYSTEMREVERSE) {
                Vector localResolutions = c.resolveAllSystemReverse(systemId);
                resolutions = appendVector(resolutions, localResolutions);
            }
        }

        if (resolutions != null) {
            return resolutions;
        } else {
            return null;
        }
    }
}
