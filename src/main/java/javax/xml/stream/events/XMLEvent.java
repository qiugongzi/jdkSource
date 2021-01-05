



package javax.xml.stream.events;

import java.io.Writer;
import javax.xml.namespace.QName;

public interface XMLEvent extends javax.xml.stream.XMLStreamConstants {


  public int getEventType();


  javax.xml.stream.Location getLocation();


  public boolean isStartElement();


  public boolean isAttribute();


  public boolean isNamespace();



  public boolean isEndElement();


  public boolean isEntityReference();


  public boolean isProcessingInstruction();


  public boolean isCharacters();


  public boolean isStartDocument();


  public boolean isEndDocument();


  public StartElement asStartElement();


  public EndElement asEndElement();


  public Characters asCharacters();


  public QName getSchemaType();


  public void writeAsEncodedUnicode(Writer writer)
    throws javax.xml.stream.XMLStreamException;

}
