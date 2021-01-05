


package com.sun.org.apache.xerces.internal.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class EntityResolverWrapper
    implements XMLEntityResolver {

    protected EntityResolver fEntityResolver;

    public EntityResolverWrapper() {}


    public EntityResolverWrapper(EntityResolver entityResolver) {
        setEntityResolver(entityResolver);
    } public void setEntityResolver(EntityResolver entityResolver) {
        fEntityResolver = entityResolver;
    } public EntityResolver getEntityResolver() {
        return fEntityResolver;
    } public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
        throws XNIException, IOException {

        String pubId = resourceIdentifier.getPublicId();
        String sysId = resourceIdentifier.getExpandedSystemId();
        if (pubId == null && sysId == null)
            return null;

        if (fEntityResolver != null && resourceIdentifier != null) {
            try {
                InputSource inputSource = fEntityResolver.resolveEntity(pubId, sysId);
                if (inputSource != null) {
                    String publicId = inputSource.getPublicId();
                    String systemId = inputSource.getSystemId();
                    String baseSystemId = resourceIdentifier.getBaseSystemId();
                    InputStream byteStream = inputSource.getByteStream();
                    Reader charStream = inputSource.getCharacterStream();
                    String encoding = inputSource.getEncoding();
                    XMLInputSource xmlInputSource =
                        new XMLInputSource(publicId, systemId, baseSystemId);
                    xmlInputSource.setByteStream(byteStream);
                    xmlInputSource.setCharacterStream(charStream);
                    xmlInputSource.setEncoding(encoding);
                    return xmlInputSource;
                }
            }

            catch (SAXException e) {
                Exception ex = e.getException();
                if (ex == null) {
                    ex = e;
                }
                throw new XNIException(ex);
            }
        }

        return null;

    } }
