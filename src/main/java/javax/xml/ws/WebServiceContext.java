

package javax.xml.ws;

import java.security.Principal;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;



public interface WebServiceContext {


    public MessageContext getMessageContext();


    public Principal getUserPrincipal();


    public boolean isUserInRole(String role);


    public EndpointReference getEndpointReference(Element... referenceParameters);


    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz,
            Element... referenceParameters);
}
