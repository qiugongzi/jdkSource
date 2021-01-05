



package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;


public interface XMLObject extends XMLStructure {


    final static String TYPE = "http:@SuppressWarnings("rawtypes")
    List getContent();


    String getId();


    String getMimeType();


    String getEncoding();
}
