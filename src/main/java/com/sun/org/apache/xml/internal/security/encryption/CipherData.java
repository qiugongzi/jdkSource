

package com.sun.org.apache.xml.internal.security.encryption;


public interface CipherData {


    int VALUE_TYPE = 0x00000001;


    int REFERENCE_TYPE = 0x00000002;


    int getDataType();


    CipherValue getCipherValue();


    void setCipherValue(CipherValue value) throws XMLEncryptionException;


    CipherReference getCipherReference();


    void setCipherReference(CipherReference reference) throws XMLEncryptionException;
}

