


package com.sun.org.apache.xerces.internal.xni.parser;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.xni.XNIException;


public interface XMLPullParserConfiguration
    extends XMLParserConfiguration {

    public void setInputSource(XMLInputSource inputSource)
        throws XMLConfigurationException, IOException;


    public boolean parse(boolean complete) throws XNIException, IOException;


    public void cleanup();

}