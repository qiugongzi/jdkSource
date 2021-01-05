

package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.keys.KeyInfo;


public interface EncryptedType {


    String getId();


    void setId(String id);


    String getType();


    void setType(String type);


    String getMimeType();


    void setMimeType(String type);


    String getEncoding();


    void setEncoding(String encoding);


    EncryptionMethod getEncryptionMethod();


    void setEncryptionMethod(EncryptionMethod method);


    KeyInfo getKeyInfo();


    void setKeyInfo(KeyInfo info);


    CipherData getCipherData();


    EncryptionProperties getEncryptionProperties();


    void setEncryptionProperties(EncryptionProperties properties);
}

