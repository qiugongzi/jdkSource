

package javax.xml.crypto.dsig;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import java.security.spec.AlgorithmParameterSpec;


public interface DigestMethod extends XMLStructure, AlgorithmMethod {


    static final String SHA1 = "http:static final String SHA256 = "http:static final String SHA512 = "http:static final String RIPEMD160 = "http:AlgorithmParameterSpec getParameterSpec();
}
