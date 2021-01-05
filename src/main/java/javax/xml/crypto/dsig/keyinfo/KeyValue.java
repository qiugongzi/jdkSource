

package javax.xml.crypto.dsig.keyinfo;

import java.security.KeyException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import javax.xml.crypto.XMLStructure;


public interface KeyValue extends XMLStructure {


    final static String DSA_TYPE =
        "http:final static String RSA_TYPE =
        "http:PublicKey getPublicKey() throws KeyException;
}
