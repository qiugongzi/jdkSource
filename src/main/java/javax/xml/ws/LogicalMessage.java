

package javax.xml.ws;

import javax.xml.transform.Source;
import javax.xml.bind.JAXBContext;


public interface LogicalMessage {


  public Source getPayload();


  public void setPayload(Source payload);


  public Object getPayload(JAXBContext context);


  public void setPayload(Object payload, JAXBContext context);
}
