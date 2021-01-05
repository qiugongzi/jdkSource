



package javax.xml.stream.events;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import java.util.Map;
import java.util.Iterator;


public interface StartElement extends XMLEvent {


  public QName getName();


  public Iterator getAttributes();


  public Iterator getNamespaces();


  public Attribute getAttributeByName(QName name);


  public NamespaceContext getNamespaceContext();


  public String getNamespaceURI(String prefix);
}
