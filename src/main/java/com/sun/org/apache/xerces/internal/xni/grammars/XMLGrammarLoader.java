


package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XNIException;

import java.io.IOException;
import java.util.Locale;



public interface XMLGrammarLoader {


    public String[] getRecognizedFeatures();


    public boolean getFeature(String featureId)
            throws XMLConfigurationException;


    public void setFeature(String featureId,
                boolean state) throws XMLConfigurationException;


    public String[] getRecognizedProperties();


    public Object getProperty(String propertyId)
            throws XMLConfigurationException;


    public void setProperty(String propertyId,
                Object state) throws XMLConfigurationException;


    public void setLocale(Locale locale);


    public Locale getLocale();


    public void setErrorHandler(XMLErrorHandler errorHandler);


    public XMLErrorHandler getErrorHandler();


    public void setEntityResolver(XMLEntityResolver entityResolver);


    public XMLEntityResolver getEntityResolver();


    public Grammar loadGrammar(XMLInputSource source)
        throws IOException, XNIException;
}