

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;


public interface EncryptionMethod {

    String getAlgorithm();


    int getKeySize();


    void setKeySize(int size);


    byte[] getOAEPparams();


    void setOAEPparams(byte[] parameters);


    void setDigestAlgorithm(String digestAlgorithm);


    String getDigestAlgorithm();


    void setMGFAlgorithm(String mgfAlgorithm);


    String getMGFAlgorithm();


    Iterator<Element> getEncryptionMethodInformation();


    void addEncryptionMethodInformation(Element information);


    void removeEncryptionMethodInformation(Element information);
}

