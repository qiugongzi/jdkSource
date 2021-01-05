

package com.sun.org.apache.xml.internal.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class DocumentSerializer extends AbstractSerializer {

    protected DocumentBuilderFactory dbf;


    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException {
        byte[] fragment = createContext(source, ctx);
        return deserialize(ctx, new InputSource(new ByteArrayInputStream(fragment)));
    }


    public Node deserialize(String source, Node ctx) throws XMLEncryptionException {
        String fragment = createContext(source, ctx);
        return deserialize(ctx, new InputSource(new StringReader(fragment)));
    }


    private Node deserialize(Node ctx, InputSource inputSource) throws XMLEncryptionException {
        try {
            if (dbf == null) {
                dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                dbf.setAttribute("http:dbf.setValidating(false);
            }
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(inputSource);

            Document contextDocument = null;
            if (Node.DOCUMENT_NODE == ctx.getNodeType()) {
                contextDocument = (Document)ctx;
            } else {
                contextDocument = ctx.getOwnerDocument();
            }

            Element fragElt =
                    (Element) contextDocument.importNode(d.getDocumentElement(), true);
            DocumentFragment result = contextDocument.createDocumentFragment();
            Node child = fragElt.getFirstChild();
            while (child != null) {
                fragElt.removeChild(child);
                result.appendChild(child);
                child = fragElt.getFirstChild();
            }
            return result;
        } catch (SAXException se) {
            throw new XMLEncryptionException("empty", se);
        } catch (ParserConfigurationException pce) {
            throw new XMLEncryptionException("empty", pce);
        } catch (IOException ioe) {
            throw new XMLEncryptionException("empty", ioe);
        }
    }

}
