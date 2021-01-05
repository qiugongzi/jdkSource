

package javax.xml.crypto.dsig;

import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;


public interface Transform extends XMLStructure, AlgorithmMethod {


    final static String BASE64 = "http:final static String ENVELOPED =
        "http:final static String XPATH = "http:final static String XPATH2 = "http:final static String XSLT = "http:AlgorithmParameterSpec getParameterSpec();


    public abstract Data transform(Data data, XMLCryptoContext context)
        throws TransformException;


    public abstract Data transform
        (Data data, XMLCryptoContext context, OutputStream os)
        throws TransformException;
}
