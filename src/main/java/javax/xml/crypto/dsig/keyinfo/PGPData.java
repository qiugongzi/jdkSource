

package javax.xml.crypto.dsig.keyinfo;

import java.util.Collections;
import java.util.List;
import javax.xml.crypto.XMLStructure;


public interface PGPData extends XMLStructure {


    final static String TYPE = "http:byte[] getKeyId();


    byte[] getKeyPacket();


    @SuppressWarnings("rawtypes")
    List getExternalElements();
}
