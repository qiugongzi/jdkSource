
package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;


public class TR9401CatalogReader extends TextCatalogReader {


  public void readCatalog(Catalog catalog, InputStream is)
    throws MalformedURLException, IOException {

    catfile = is;

    if (catfile == null) {
      return;
    }

    Vector unknownEntry = null;

    try {
      while (true) {
        String token = nextToken();

        if (token == null) {
          if (unknownEntry != null) {
            catalog.unknownEntry(unknownEntry);
            unknownEntry = null;
          }
          catfile.close();
          catfile = null;
          return;
        }

        String entryToken = null;
        if (caseSensitive) {
          entryToken = token;
        } else {
          entryToken = token.toUpperCase();
        }

        if (entryToken.equals("DELEGATE")) {
          entryToken = "DELEGATE_PUBLIC";
        }

        try {
          int type = CatalogEntry.getEntryType(entryToken);
          int numArgs = CatalogEntry.getEntryArgCount(type);
          Vector args = new Vector();

          if (unknownEntry != null) {
            catalog.unknownEntry(unknownEntry);
            unknownEntry = null;
          }

          for (int count = 0; count < numArgs; count++) {
            args.addElement(nextToken());
          }

          catalog.addEntry(new CatalogEntry(entryToken, args));
        } catch (CatalogException cex) {
          if (cex.getExceptionType() == CatalogException.INVALID_ENTRY_TYPE) {
            if (unknownEntry == null) {
              unknownEntry = new Vector();
            }
            unknownEntry.addElement(token);
          } else if (cex.getExceptionType() == CatalogException.INVALID_ENTRY) {
            catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", token);
            unknownEntry = null;
          } else if (cex.getExceptionType() == CatalogException.UNENDED_COMMENT) {
            catalog.getCatalogManager().debug.message(1, cex.getMessage());
          }
        }
      }
    } catch (CatalogException cex2) {
      if (cex2.getExceptionType() == CatalogException.UNENDED_COMMENT) {
        catalog.getCatalogManager().debug.message(1, cex2.getMessage());
      }
    }

  }
}
