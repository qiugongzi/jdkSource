

package javax.xml.crypto.dsig.keyinfo;

import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Transform;
import java.util.List;


public interface RetrievalMethod extends URIReference, XMLStructure {


    @SuppressWarnings("rawtypes")
    List getTransforms();


    String getURI();


    Data dereference(XMLCryptoContext context) throws URIReferenceException;
}
