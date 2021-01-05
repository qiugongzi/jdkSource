

package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.reflect.misc.ReflectUtil;



public class CatalogManager {
  private static String pFiles         = "xml.catalog.files";
  private static String pVerbosity     = "xml.catalog.verbosity";
  private static String pPrefer        = "xml.catalog.prefer";
  private static String pStatic        = "xml.catalog.staticCatalog";
  private static String pAllowPI       = "xml.catalog.allowPI";
  private static String pClassname     = "xml.catalog.className";
  private static String pIgnoreMissing = "xml.catalog.ignoreMissing";


  private static CatalogManager staticManager = new CatalogManager();


  private BootstrapResolver bResolver = new BootstrapResolver();


  private boolean ignoreMissingProperties
    = (SecuritySupport.getSystemProperty(pIgnoreMissing) != null
       || SecuritySupport.getSystemProperty(pFiles) != null);


  private ResourceBundle resources;


  private String propertyFile = "CatalogManager.properties";


  private URL propertyFileURI = null;


  private String defaultCatalogFiles = "./xcatalog";


  private String catalogFiles = null;


  private boolean fromPropertiesFile = false;


  private int defaultVerbosity = 1;


  private Integer verbosity = null;


  private boolean defaultPreferPublic = true;


  private Boolean preferPublic = null;


  private boolean defaultUseStaticCatalog = true;


  private Boolean useStaticCatalog = null;


  private static Catalog staticCatalog = null;


  private boolean defaultOasisXMLCatalogPI = true;


  private Boolean oasisXMLCatalogPI = null;


  private boolean defaultRelativeCatalogs = true;


  private Boolean relativeCatalogs = null;


  private String catalogClassName = null;

    private boolean overrideDefaultParser;


  public Debug debug = null;


  public CatalogManager() {
    init();
  }


  public CatalogManager(String propertyFile) {
    this.propertyFile = propertyFile;
    init();
  }

  private void init() {
    debug = new Debug();
    if (System.getSecurityManager() == null) {
        overrideDefaultParser = true;
    }
  }

  public void setBootstrapResolver(BootstrapResolver resolver) {
    bResolver = resolver;
  }


  public BootstrapResolver getBootstrapResolver() {
    return bResolver;
  }


  private synchronized void readProperties() {
    try {
      propertyFileURI = CatalogManager.class.getResource("/"+propertyFile);
      InputStream in =
        CatalogManager.class.getResourceAsStream("/"+propertyFile);
      if (in==null) {
        if (!ignoreMissingProperties) {
          System.err.println("Cannot find "+propertyFile);
          ignoreMissingProperties = true;
        }
        return;
      }
      resources = new PropertyResourceBundle(in);
    } catch (MissingResourceException mre) {
      if (!ignoreMissingProperties) {
        System.err.println("Cannot read "+propertyFile);
      }
    } catch (java.io.IOException e) {
      if (!ignoreMissingProperties) {
        System.err.println("Failure trying to read "+propertyFile);
      }
    }

    if (verbosity == null) {
      try {
        String verbStr = resources.getString("verbosity");
        int verb = Integer.parseInt(verbStr.trim());
        debug.setDebug(verb);
        verbosity = new Integer(verb);
      } catch (Exception e) {
        }
    }
  }


  public static CatalogManager getStaticManager() {
    return staticManager;
  }


  public boolean getIgnoreMissingProperties() {
    return ignoreMissingProperties;
  }


  public void setIgnoreMissingProperties(boolean ignore) {
    ignoreMissingProperties = ignore;
  }


  public void ignoreMissingProperties(boolean ignore) {
    setIgnoreMissingProperties(ignore);
  }


  private int queryVerbosity () {
    String defaultVerbStr = Integer.toString(defaultVerbosity);

    String verbStr = SecuritySupport.getSystemProperty(pVerbosity);

    if (verbStr == null) {
      if (resources==null) readProperties();
      if (resources != null) {
        try {
          verbStr = resources.getString("verbosity");
        } catch (MissingResourceException e) {
          verbStr = defaultVerbStr;
        }
      } else {
        verbStr = defaultVerbStr;
      }
    }

    int verb = defaultVerbosity;

    try {
      verb = Integer.parseInt(verbStr.trim());
    } catch (Exception e) {
      System.err.println("Cannot parse verbosity: \"" + verbStr + "\"");
    }

    if (verbosity == null) {
      debug.setDebug(verb);
      verbosity = new Integer(verb);
    }

    return verb;
  }


  public int getVerbosity() {
    if (verbosity == null) {
      verbosity = new Integer(queryVerbosity());
    }

    return verbosity.intValue();
  }


  public void setVerbosity (int verbosity) {
    this.verbosity = new Integer(verbosity);
    debug.setDebug(verbosity);
  }


  public int verbosity () {
    return getVerbosity();
  }


  private boolean queryRelativeCatalogs () {
    if (resources==null) readProperties();

    if (resources==null) return defaultRelativeCatalogs;

    try {
      String allow = resources.getString("relative-catalogs");
      return (allow.equalsIgnoreCase("true")
              || allow.equalsIgnoreCase("yes")
              || allow.equalsIgnoreCase("1"));
    } catch (MissingResourceException e) {
      return defaultRelativeCatalogs;
    }
  }


  public boolean getRelativeCatalogs () {
    if (relativeCatalogs == null) {
      relativeCatalogs = new Boolean(queryRelativeCatalogs());
    }

    return relativeCatalogs.booleanValue();
  }


  public void setRelativeCatalogs (boolean relative) {
    relativeCatalogs = new Boolean(relative);
  }


  public boolean relativeCatalogs () {
    return getRelativeCatalogs();
  }


  private String queryCatalogFiles () {
    String catalogList = SecuritySupport.getSystemProperty(pFiles);
    fromPropertiesFile = false;

    if (catalogList == null) {
      if (resources == null) readProperties();
      if (resources != null) {
        try {
          catalogList = resources.getString("catalogs");
          fromPropertiesFile = true;
        } catch (MissingResourceException e) {
          System.err.println(propertyFile + ": catalogs not found.");
          catalogList = null;
        }
      }
    }

    if (catalogList == null) {
      catalogList = defaultCatalogFiles;
    }

    return catalogList;
  }


  public Vector getCatalogFiles() {
    if (catalogFiles == null) {
      catalogFiles = queryCatalogFiles();
    }

    StringTokenizer files = new StringTokenizer(catalogFiles, ";");
    Vector catalogs = new Vector();
    while (files.hasMoreTokens()) {
      String catalogFile = files.nextToken();
      URL absURI = null;

      if (fromPropertiesFile && !relativeCatalogs()) {
        try {
          absURI = new URL(propertyFileURI, catalogFile);
          catalogFile = absURI.toString();
        } catch (MalformedURLException mue) {
          absURI = null;
        }
      }

      catalogs.add(catalogFile);
    }

    return catalogs;
  }


  public void setCatalogFiles(String fileList) {
    catalogFiles = fileList;
    fromPropertiesFile = false;
  }


  public Vector catalogFiles() {
    return getCatalogFiles();
  }


  private boolean queryPreferPublic () {
    String prefer = SecuritySupport.getSystemProperty(pPrefer);

    if (prefer == null) {
      if (resources==null) readProperties();
      if (resources==null) return defaultPreferPublic;
      try {
        prefer = resources.getString("prefer");
      } catch (MissingResourceException e) {
        return defaultPreferPublic;
      }
    }

    if (prefer == null) {
      return defaultPreferPublic;
    }

    return (prefer.equalsIgnoreCase("public"));
  }


  public boolean getPreferPublic () {
    if (preferPublic == null) {
      preferPublic = new Boolean(queryPreferPublic());
    }
    return preferPublic.booleanValue();
  }


  public void setPreferPublic (boolean preferPublic) {
    this.preferPublic = new Boolean(preferPublic);
  }


  public boolean preferPublic () {
    return getPreferPublic();
  }


  private boolean queryUseStaticCatalog () {
    String staticCatalog = SecuritySupport.getSystemProperty(pStatic);

    if (staticCatalog == null) {
      if (resources==null) readProperties();
      if (resources==null) return defaultUseStaticCatalog;
      try {
        staticCatalog = resources.getString("static-catalog");
      } catch (MissingResourceException e) {
        return defaultUseStaticCatalog;
      }
    }

    if (staticCatalog == null) {
      return defaultUseStaticCatalog;
    }

    return (staticCatalog.equalsIgnoreCase("true")
            || staticCatalog.equalsIgnoreCase("yes")
            || staticCatalog.equalsIgnoreCase("1"));
  }


  public boolean getUseStaticCatalog() {
    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(queryUseStaticCatalog());
    }

    return useStaticCatalog.booleanValue();
  }


  public void setUseStaticCatalog(boolean useStatic) {
    useStaticCatalog = new Boolean(useStatic);
  }


  public boolean staticCatalog() {
    return getUseStaticCatalog();
  }


  public Catalog getPrivateCatalog() {
    Catalog catalog = staticCatalog;

    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(getUseStaticCatalog());
    }

    if (catalog == null || !useStaticCatalog.booleanValue()) {

      try {
        String catalogClassName = getCatalogClassName();

        if (catalogClassName == null) {
          catalog = new Catalog();
        } else {
          try {
            catalog = (Catalog) ReflectUtil.forName(catalogClassName).newInstance();
          } catch (ClassNotFoundException cnfe) {
            debug.message(1,"Catalog class named '"
                          + catalogClassName
                          + "' could not be found. Using default.");
            catalog = new Catalog();
          } catch (ClassCastException cnfe) {
            debug.message(1,"Class named '"
                          + catalogClassName
                          + "' is not a Catalog. Using default.");
            catalog = new Catalog();
          }
        }

        catalog.setCatalogManager(this);
        catalog.setupReaders();
        catalog.loadSystemCatalogs();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      if (useStaticCatalog.booleanValue()) {
        staticCatalog = catalog;
      }
    }

    return catalog;
  }


  public Catalog getCatalog() {
    Catalog catalog = staticCatalog;

    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(getUseStaticCatalog());
    }

    if (catalog == null || !useStaticCatalog.booleanValue()) {
      catalog = getPrivateCatalog();
      if (useStaticCatalog.booleanValue()) {
        staticCatalog = catalog;
      }
    }

    return catalog;
  }


  public boolean queryAllowOasisXMLCatalogPI () {
    String allow = SecuritySupport.getSystemProperty(pAllowPI);

    if (allow == null) {
      if (resources==null) readProperties();
      if (resources==null) return defaultOasisXMLCatalogPI;
      try {
        allow = resources.getString("allow-oasis-xml-catalog-pi");
      } catch (MissingResourceException e) {
        return defaultOasisXMLCatalogPI;
      }
    }

    if (allow == null) {
      return defaultOasisXMLCatalogPI;
    }

    return (allow.equalsIgnoreCase("true")
            || allow.equalsIgnoreCase("yes")
            || allow.equalsIgnoreCase("1"));
  }


  public boolean getAllowOasisXMLCatalogPI () {
    if (oasisXMLCatalogPI == null) {
      oasisXMLCatalogPI = new Boolean(queryAllowOasisXMLCatalogPI());
    }

    return oasisXMLCatalogPI.booleanValue();
  }

    public boolean overrideDefaultParser() {
        return overrideDefaultParser;
  }

  public void setAllowOasisXMLCatalogPI(boolean allowPI) {
    oasisXMLCatalogPI = new Boolean(allowPI);
  }


  public boolean allowOasisXMLCatalogPI() {
    return getAllowOasisXMLCatalogPI();
  }


  public String queryCatalogClassName () {
    String className = SecuritySupport.getSystemProperty(pClassname);

    if (className == null) {
      if (resources==null) readProperties();
      if (resources==null) return null;
      try {
        return resources.getString("catalog-class-name");
      } catch (MissingResourceException e) {
        return null;
      }
    }

    return className;
  }


  public String getCatalogClassName() {
    if (catalogClassName == null) {
      catalogClassName = queryCatalogClassName();
    }

    return catalogClassName;
  }


  public void setCatalogClassName(String className) {
    catalogClassName = className;
  }


  public String catalogClassName() {
    return getCatalogClassName();
  }
}
