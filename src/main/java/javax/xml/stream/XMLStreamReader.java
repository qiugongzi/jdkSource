



package javax.xml.stream;

import java.io.Reader;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;


public interface XMLStreamReader extends XMLStreamConstants {

  public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException;


  public int next() throws XMLStreamException;


  public void require(int type, String namespaceURI, String localName) throws XMLStreamException;


  public String getElementText() throws XMLStreamException;


  public int nextTag() throws XMLStreamException;


  public boolean hasNext() throws XMLStreamException;


  public void close() throws XMLStreamException;


  public String getNamespaceURI(String prefix);


  public boolean isStartElement();


  public boolean isEndElement();


  public boolean isCharacters();


  public boolean isWhiteSpace();



  public String getAttributeValue(String namespaceURI,
                                  String localName);


  public int getAttributeCount();


  public QName getAttributeName(int index);


  public String getAttributeNamespace(int index);


  public String getAttributeLocalName(int index);


  public String getAttributePrefix(int index);


  public String getAttributeType(int index);


  public String getAttributeValue(int index);


  public boolean isAttributeSpecified(int index);


  public int getNamespaceCount();


  public String getNamespacePrefix(int index);


  public String getNamespaceURI(int index);


  public NamespaceContext getNamespaceContext();


  public int getEventType();


  public String getText();


  public char[] getTextCharacters();


   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
     throws XMLStreamException;


  public int getTextStart();


  public int getTextLength();


  public String getEncoding();


  public boolean hasText();


  public Location getLocation();


  public QName getName();


  public String getLocalName();


  public boolean hasName();


  public String getNamespaceURI();


  public String getPrefix();


  public String getVersion();


  public boolean isStandalone();


  public boolean standaloneSet();


  public String getCharacterEncodingScheme();


  public String getPITarget();


  public String getPIData();
}
