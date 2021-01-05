
package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.org.apache.xml.internal.resolver.CatalogException;

import java.io.InputStream;
import com.sun.org.apache.xml.internal.resolver.Catalog;


public interface CatalogReader {

    public void readCatalog(Catalog catalog, String fileUrl)
      throws MalformedURLException, IOException, CatalogException;


    public void readCatalog(Catalog catalog, InputStream is)
        throws IOException, CatalogException;
}
