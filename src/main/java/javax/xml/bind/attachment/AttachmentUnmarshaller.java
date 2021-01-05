

package javax.xml.bind.attachment;

import javax.activation.DataHandler;


public abstract class AttachmentUnmarshaller {

   public abstract DataHandler getAttachmentAsDataHandler(String cid);


    public abstract byte[] getAttachmentAsByteArray(String cid);


    public boolean isXOPPackage() { return false; }
}
