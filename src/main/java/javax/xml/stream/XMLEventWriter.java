



package javax.xml.stream;

import javax.xml.stream.events.*;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.namespace.NamespaceContext;


public interface XMLEventWriter extends XMLEventConsumer {


  public void flush() throws XMLStreamException;


  public void close() throws XMLStreamException;


  public void add(XMLEvent event) throws XMLStreamException;



  public void add(XMLEventReader reader) throws XMLStreamException;


  public String getPrefix(String uri) throws XMLStreamException;


  public void setPrefix(String prefix, String uri) throws XMLStreamException;


  public void setDefaultNamespace(String uri) throws XMLStreamException;


  public void setNamespaceContext(NamespaceContext context)
    throws XMLStreamException;


  public NamespaceContext getNamespaceContext();


}
