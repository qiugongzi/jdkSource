

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import org.w3c.dom.Element;


public interface AgreementMethod {


    byte[] getKANonce();


    void setKANonce(byte[] kanonce);


    Iterator<Element> getAgreementMethodInformation();


    void addAgreementMethodInformation(Element info);


    void revoveAgreementMethodInformation(Element info);


    KeyInfo getOriginatorKeyInfo();


    void setOriginatorKeyInfo(KeyInfo keyInfo);


    KeyInfo getRecipientKeyInfo();


    void setRecipientKeyInfo(KeyInfo keyInfo);


    String getAlgorithm();
}
