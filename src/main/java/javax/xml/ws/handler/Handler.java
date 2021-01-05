

package javax.xml.ws.handler;

import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;


public interface Handler<C extends MessageContext> {


  public boolean handleMessage(C context);


  public boolean handleFault(C context);


  public void close(MessageContext context);
}
