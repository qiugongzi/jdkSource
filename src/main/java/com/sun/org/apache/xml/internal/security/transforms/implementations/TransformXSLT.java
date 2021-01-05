

package com.sun.org.apache.xml.internal.security.transforms.implementations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;


public class TransformXSLT extends TransformSpi {


    public static final String implementedTransformURI =
        Transforms.TRANSFORM_XSLT;

    static final String XSLTSpecNS              = "http:static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET          = "stylesheet";

    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(TransformXSLT.class.getName());


    protected String engineGetURI() {
        return implementedTransformURI;
    }

    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream baos, Transform transformObject
    ) throws IOException, TransformationException {
        try {
            Element transformElement = transformObject.getElement();

            Element xsltElement =
                XMLUtils.selectNode(transformElement.getFirstChild(), XSLTSpecNS, "stylesheet", 0);

            if (xsltElement == null) {
                Object exArgs[] = { "xslt:stylesheet", "Transform" };

                throw new TransformationException("xml.WrongContent", exArgs);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);


            Source xmlSource =
                new StreamSource(new ByteArrayInputStream(input.getBytes()));
            Source stylesheet;


            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(xsltElement);
                StreamResult result = new StreamResult(os);

                transformer.transform(source, result);

                stylesheet =
                    new StreamSource(new ByteArrayInputStream(os.toByteArray()));
            }

            Transformer transformer = tFactory.newTransformer(stylesheet);

            try {
                transformer.setOutputProperty("{http:} catch (Exception e) {
                log.log(java.util.logging.Level.WARNING, "Unable to set Xalan line-separator property: " + e.getMessage());
            }

            if (baos == null) {
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                StreamResult outputTarget = new StreamResult(baos1);
                transformer.transform(xmlSource, outputTarget);
                return new XMLSignatureInput(baos1.toByteArray());
            }
            StreamResult outputTarget = new StreamResult(baos);

            transformer.transform(xmlSource, outputTarget);
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setOutputStream(baos);
            return output;
        } catch (XMLSecurityException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        } catch (TransformerConfigurationException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        } catch (TransformerException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        }
    }
}
