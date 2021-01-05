

package com.sun.org.apache.xerces.internal.util;

import javax.xml.transform.Source;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;


public final class XMLInputSourceAdaptor implements Source {

    public final XMLInputSource fSource;

    public XMLInputSourceAdaptor( XMLInputSource core ) {
        fSource = core;
    }

    public void setSystemId(String systemId) {
        fSource.setSystemId(systemId);
    }

    public String getSystemId() {
        try {
            return XMLEntityManager.expandSystemId(
                    fSource.getSystemId(), fSource.getBaseSystemId(), false);
        } catch (MalformedURIException e) {
            return fSource.getSystemId();
        }
    }
}
