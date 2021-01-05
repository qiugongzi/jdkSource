

package com.sun.org.apache.xml.internal.security.transforms.implementations;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class TransformBase64Decode extends TransformSpi {


    public static final String implementedTransformURI =
        Transforms.TRANSFORM_BASE64_DECODE;


    protected String engineGetURI() {
        return TransformBase64Decode.implementedTransformURI;
    }


    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, Transform transformObject
    ) throws IOException, CanonicalizationException, TransformationException {
        return enginePerformTransform(input, null, transformObject);
    }

    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream os, Transform transformObject
    ) throws IOException, CanonicalizationException, TransformationException {
        try {
            if (input.isElement()) {
                Node el = input.getSubNode();
                if (input.getSubNode().getNodeType() == Node.TEXT_NODE) {
                    el = el.getParentNode();
                }
                StringBuilder sb = new StringBuilder();
                traverseElement((Element)el, sb);
                if (os == null) {
                    byte[] decodedBytes = Base64.decode(sb.toString());
                    return new XMLSignatureInput(decodedBytes);
                }
                Base64.decode(sb.toString(), os);
                XMLSignatureInput output = new XMLSignatureInput((byte[])null);
                output.setOutputStream(os);
                return output;
            }

            if (input.isOctetStream() || input.isNodeSet()) {
                if (os == null) {
                    byte[] base64Bytes = input.getBytes();
                    byte[] decodedBytes = Base64.decode(base64Bytes);
                    return new XMLSignatureInput(decodedBytes);
                }
                if (input.isByteArray() || input.isNodeSet()) {
                    Base64.decode(input.getBytes(), os);
                } else {
                    Base64.decode(new BufferedInputStream(input.getOctetStreamReal()), os);
                }
                XMLSignatureInput output = new XMLSignatureInput((byte[])null);
                output.setOutputStream(os);
                return output;
            }

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                Document doc =
                    dbf.newDocumentBuilder().parse(input.getOctetStream());

                Element rootNode = doc.getDocumentElement();
                StringBuilder sb = new StringBuilder();
                traverseElement(rootNode, sb);
                byte[] decodedBytes = Base64.decode(sb.toString());
                return new XMLSignatureInput(decodedBytes);
            } catch (ParserConfigurationException e) {
                throw new TransformationException("c14n.Canonicalizer.Exception",e);
            } catch (SAXException e) {
                throw new TransformationException("SAX exception", e);
            }
        } catch (Base64DecodingException e) {
            throw new TransformationException("Base64Decoding", e);
        }
    }

    void traverseElement(org.w3c.dom.Element node, StringBuilder sb) {
        Node sibling = node.getFirstChild();
        while (sibling != null) {
            switch (sibling.getNodeType()) {
            case Node.ELEMENT_NODE:
                traverseElement((Element)sibling, sb);
                break;
            case Node.TEXT_NODE:
                sb.append(((Text)sibling).getData());
            }
            sibling = sibling.getNextSibling();
        }
    }
}
