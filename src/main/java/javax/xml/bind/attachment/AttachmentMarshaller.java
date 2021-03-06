

package javax.xml.bind.attachment;

import javax.activation.DataHandler;
import javax.xml.bind.Marshaller;


public abstract class AttachmentMarshaller {


    public abstract String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName);


    public abstract String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName);


    public boolean isXOPPackage() { return false; }


    public abstract String addSwaRefAttachment(DataHandler data);
}
