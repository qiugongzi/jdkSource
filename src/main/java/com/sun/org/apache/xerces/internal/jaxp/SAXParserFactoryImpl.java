


package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public class SAXParserFactoryImpl extends SAXParserFactory {


    private static final String VALIDATION_FEATURE =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    private static final String NAMESPACES_FEATURE =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;


    private static final String XINCLUDE_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_FEATURE;

    private Map<String, Boolean> features;
    private Schema grammar;
    private boolean isXIncludeAware;


    private boolean fSecureProcess = true;


    public SAXParser newSAXParser()
        throws ParserConfigurationException
    {
        SAXParser saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, features, fSecureProcess);
        } catch (SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }


    private SAXParserImpl newSAXParserImpl()
        throws ParserConfigurationException, SAXNotRecognizedException,
        SAXNotSupportedException
    {
        SAXParserImpl saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, features);
        } catch (SAXNotSupportedException e) {
            throw e;
        } catch (SAXNotRecognizedException e) {
            throw e;
        } catch (SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }


    public void setFeature(String name, boolean value)
        throws ParserConfigurationException, SAXNotRecognizedException,
                SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            if (System.getSecurityManager() != null && (!value)) {
                throw new ParserConfigurationException(
                        SAXMessageFormatter.formatMessage(null,
                        "jaxp-secureprocessing-feature", null));
            }
            fSecureProcess = value;
            putInFeatures(name, value);
            return;
        }

        putInFeatures(name, value);
        try {
            newSAXParserImpl();
        } catch (SAXNotSupportedException e) {
            features.remove(name);
            throw e;
        } catch (SAXNotRecognizedException e) {
            features.remove(name);
            throw e;
        }
    }


    public boolean getFeature(String name)
        throws ParserConfigurationException, SAXNotRecognizedException,
                SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return fSecureProcess;
        }
        return newSAXParserImpl().getXMLReader().getFeature(name);
    }

    public Schema getSchema() {
        return grammar;
    }

    public void setSchema(Schema grammar) {
        this.grammar = grammar;
    }

    public boolean isXIncludeAware() {
        return getFromFeatures(XINCLUDE_FEATURE);
    }

    public void setXIncludeAware(boolean state) {
        putInFeatures(XINCLUDE_FEATURE, state);
    }


    public void setValidating(boolean validating) {
        putInFeatures(VALIDATION_FEATURE, validating);
    }

    public boolean isValidating() {
         return getFromFeatures(VALIDATION_FEATURE);
    }

    private void putInFeatures(String name, boolean value){
         if (features == null) {
            features = new HashMap<>();
        }
        features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    private boolean getFromFeatures(String name){
         if (features == null){
            return false;
         }
         else {
             Boolean value = features.get(name);
             return (value == null) ? false : value;
         }
    }

    public boolean isNamespaceAware() {
        return getFromFeatures(NAMESPACES_FEATURE);
    }

    public void setNamespaceAware(boolean awareness) {
       putInFeatures(NAMESPACES_FEATURE, awareness);
    }

 }
