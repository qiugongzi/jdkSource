

package org.xml.sax.ext;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;



public interface EntityResolver2 extends EntityResolver
{

    public InputSource getExternalSubset (String name, String baseURI)
    throws SAXException, IOException;


    public InputSource resolveEntity (
            String name,
            String publicId,
            String baseURI,
            String systemId
    ) throws SAXException, IOException;
}
