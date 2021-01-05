



package javax.xml.stream;

import javax.xml.stream.events.XMLEvent;

import java.util.Iterator;


public interface XMLEventReader extends Iterator {

  public XMLEvent nextEvent() throws XMLStreamException;


  public boolean hasNext();


  public XMLEvent peek() throws XMLStreamException;


  public String getElementText() throws XMLStreamException;


  public XMLEvent nextTag() throws XMLStreamException;


  public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException;


  public void close() throws XMLStreamException;
}
