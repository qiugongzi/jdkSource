

package javax.xml.ws.handler.soap;

import javax.xml.soap.SOAPMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import java.util.Set;


public interface SOAPMessageContext
                    extends javax.xml.ws.handler.MessageContext {


  public SOAPMessage getMessage();


  public void setMessage(SOAPMessage message);


  public Object[] getHeaders(QName header, JAXBContext context,
    boolean allRoles);


  public Set<String> getRoles();
}
