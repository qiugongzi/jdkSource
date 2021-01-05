

package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;


public interface KeyInfo extends XMLStructure {


    @SuppressWarnings("rawtypes")
    List getContent();


    String getId();


    void marshal(XMLStructure parent, XMLCryptoContext context)
        throws MarshalException;
}
