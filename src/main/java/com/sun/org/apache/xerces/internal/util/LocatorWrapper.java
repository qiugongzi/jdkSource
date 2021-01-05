

package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;


public class LocatorWrapper implements XMLLocator {

    private final Locator locator;

    public LocatorWrapper( Locator _loc ) { this.locator=_loc; }

    public int getColumnNumber()  { return locator.getColumnNumber(); }
    public int getLineNumber()    { return locator.getLineNumber(); }
    public String getBaseSystemId() { return null; }
    public String getExpandedSystemId() { return locator.getSystemId(); }
    public String getLiteralSystemId() { return locator.getSystemId(); }
    public String getPublicId()   { return locator.getPublicId(); }
    public String getEncoding() { return null; }


    public int getCharacterOffset() {
        return -1;
    }


    public String getXMLVersion() {
        return null;
    }

}
