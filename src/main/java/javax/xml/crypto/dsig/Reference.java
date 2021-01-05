

package javax.xml.crypto.dsig;

import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLStructure;
import java.io.InputStream;
import java.util.List;


public interface Reference extends URIReference, XMLStructure {


    @SuppressWarnings("rawtypes")
    List getTransforms();


    DigestMethod getDigestMethod();


    String getId();


    byte[] getDigestValue();


    byte[] getCalculatedDigestValue();


    boolean validate(XMLValidateContext validateContext)
        throws XMLSignatureException;


    Data getDereferencedData();


    InputStream getDigestInputStream();
}
