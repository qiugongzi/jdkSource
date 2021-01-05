

package com.sun.org.apache.xml.internal.security.encryption;


public interface EncryptedKey extends EncryptedType {


    String getRecipient();


    void setRecipient(String recipient);


    ReferenceList getReferenceList();


    void setReferenceList(ReferenceList list);


    String getCarriedName();


    void setCarriedName(String name);
}

