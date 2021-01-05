

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;


public interface EncryptionProperties {


    String getId();


    void setId(String id);


    Iterator<EncryptionProperty> getEncryptionProperties();


    void addEncryptionProperty(EncryptionProperty property);


    void removeEncryptionProperty(EncryptionProperty property);
}

