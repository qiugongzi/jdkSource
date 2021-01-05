


package com.sun.org.apache.xerces.internal.util;


import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;



public class DOMEntityResolverWrapper
    implements XMLEntityResolver {

    private static final String XML_TYPE = "http:private static final String XSD_TYPE = "http:protected LSResourceResolver fEntityResolver;

    public DOMEntityResolverWrapper() {}


    public DOMEntityResolverWrapper(LSResourceResolver entityResolver) {
        setEntityResolver(entityResolver);
    } public void setEntityResolver(LSResourceResolver entityResolver) {
        fEntityResolver = entityResolver;
    } public LSResourceResolver getEntityResolver() {
        return fEntityResolver;
    } public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
        throws XNIException, IOException {
        if (fEntityResolver != null) {
            LSInput inputSource =
                resourceIdentifier == null
                    ? fEntityResolver.resolveResource(
                        null,
                        null,
                        null,
                        null,
                        null)
                    : fEntityResolver.resolveResource(
                        getType(resourceIdentifier),
                        resourceIdentifier.getNamespace(),
                        resourceIdentifier.getPublicId(),
                        resourceIdentifier.getLiteralSystemId(),
                        resourceIdentifier.getBaseSystemId());
            if (inputSource != null) {
                String publicId = inputSource.getPublicId();
                String systemId = inputSource.getSystemId();
                String baseSystemId = inputSource.getBaseURI();
                InputStream byteStream = inputSource.getByteStream();
                Reader charStream = inputSource.getCharacterStream();
                String encoding = inputSource.getEncoding();
                String data = inputSource.getStringData();


                XMLInputSource xmlInputSource =
                    new XMLInputSource(publicId, systemId, baseSystemId);

                if (charStream != null) {
                    xmlInputSource.setCharacterStream(charStream);
                }
                else if (byteStream != null) {
                    xmlInputSource.setByteStream((InputStream) byteStream);
                }
                else if (data != null && data.length() != 0) {
                    xmlInputSource.setCharacterStream(new StringReader(data));
                }
                xmlInputSource.setEncoding(encoding);
                return xmlInputSource;
            }
        }

        return null;

    } private String getType(XMLResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof XMLGrammarDescription) {
            XMLGrammarDescription desc = (XMLGrammarDescription) resourceIdentifier;
            if (XMLGrammarDescription.XML_SCHEMA.equals(desc.getGrammarType())) {
                return XSD_TYPE;
            }
        }
        return XML_TYPE;
    } }