

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;


public interface EncryptionProperty {


    String getTarget();


    void setTarget(String target);


    String getId();


    void setId(String id);


    String getAttribute(String attribute);


    void setAttribute(String attribute, String value);


    Iterator<Element> getEncryptionInformation();


    void addEncryptionInformation(Element information);


    void removeEncryptionInformation(Element information);
}
