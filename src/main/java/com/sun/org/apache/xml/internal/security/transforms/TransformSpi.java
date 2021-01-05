

package com.sun.org.apache.xml.internal.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.xml.sax.SAXException;


public abstract class TransformSpi {


    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream os, Transform transformObject
    ) throws IOException, CanonicalizationException, InvalidCanonicalizerException,
        TransformationException, ParserConfigurationException, SAXException {
        throw new UnsupportedOperationException();
    }


    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, Transform transformObject
    ) throws IOException, CanonicalizationException, InvalidCanonicalizerException,
        TransformationException, ParserConfigurationException, SAXException {
        return enginePerformTransform(input, null, transformObject);
    }


    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input
    ) throws IOException, CanonicalizationException, InvalidCanonicalizerException,
        TransformationException, ParserConfigurationException, SAXException {
        return enginePerformTransform(input, null);
    }


    protected abstract String engineGetURI();
}
