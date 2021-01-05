


package com.sun.org.apache.xerces.internal.xni.parser;

import java.io.IOException;
import java.util.Locale;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;


public interface XMLParserConfiguration
    extends XMLComponentManager {

    public void parse(XMLInputSource inputSource)
        throws XNIException, IOException;

    public void addRecognizedFeatures(String[] featureIds);


    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException;


    public boolean getFeature(String featureId)
        throws XMLConfigurationException;


    public void addRecognizedProperties(String[] propertyIds);


    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException;


    public Object getProperty(String propertyId)
        throws XMLConfigurationException;

    public void setErrorHandler(XMLErrorHandler errorHandler);


    public XMLErrorHandler getErrorHandler();


    public void setDocumentHandler(XMLDocumentHandler documentHandler);


    public XMLDocumentHandler getDocumentHandler();


    public void setDTDHandler(XMLDTDHandler dtdHandler);


    public XMLDTDHandler getDTDHandler();


    public void setDTDContentModelHandler(XMLDTDContentModelHandler dtdContentModelHandler);


    public XMLDTDContentModelHandler getDTDContentModelHandler();

    public void setEntityResolver(XMLEntityResolver entityResolver);


    public XMLEntityResolver getEntityResolver();


    public void setLocale(Locale locale) throws XNIException;


    public Locale getLocale();

}