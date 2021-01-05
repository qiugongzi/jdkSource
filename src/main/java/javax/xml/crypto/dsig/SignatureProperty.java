

package javax.xml.crypto.dsig;

import javax.xml.crypto.XMLStructure;
import java.util.List;


public interface SignatureProperty extends XMLStructure {


    String getTarget();


    String getId();


    @SuppressWarnings("rawtypes")
    List getContent();
}
