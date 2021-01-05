


package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    private Map<String, Object> attributes;
    private Map<String, Boolean> features;
    private Schema grammar;
    private boolean isXIncludeAware;


    private boolean fSecureProcess = true;


    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException
    {

        if (grammar != null && attributes != null) {
            if (attributes.containsKey(JAXPConstants.JAXP_SCHEMA_LANGUAGE)) {
                throw new ParserConfigurationException(
                        SAXMessageFormatter.formatMessage(null,
                        "schema-already-specified", new Object[] {JAXPConstants.JAXP_SCHEMA_LANGUAGE}));
            }
            else if (attributes.containsKey(JAXPConstants.JAXP_SCHEMA_SOURCE)) {
                throw new ParserConfigurationException(
                        SAXMessageFormatter.formatMessage(null,
                        "schema-already-specified", new Object[] {JAXPConstants.JAXP_SCHEMA_SOURCE}));
            }
        }

        try {
            return new DocumentBuilderImpl(this, attributes, features, fSecureProcess);
        } catch (SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
    }


    public void setAttribute(String name, Object value)
        throws IllegalArgumentException
    {
        if (value == null) {
            if (attributes != null) {
                attributes.remove(name);
            }
            return;
        }

        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(name, value);

        try {
            new DocumentBuilderImpl(this, attributes, features);
        } catch (Exception e) {
            attributes.remove(name);
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        if (attributes != null) {
            Object val = attributes.get(name);
            if (val != null) {
                return val;
            }
        }

        DOMParser domParser = null;
        try {
            domParser =
                new DocumentBuilderImpl(this, attributes, features).getDOMParser();
            return domParser.getProperty(name);
        } catch (SAXException se1) {
            try {
                boolean result = domParser.getFeature(name);
                return result ? Boolean.TRUE : Boolean.FALSE;
            } catch (SAXException se2) {
                throw new IllegalArgumentException(se1.getMessage());
            }
        }
    }

    public Schema getSchema() {
        return grammar;
    }

    public void setSchema(Schema grammar) {
        this.grammar = grammar;
    }

    public boolean isXIncludeAware() {
        return this.isXIncludeAware;
    }

    public void setXIncludeAware(boolean state) {
        this.isXIncludeAware = state;
    }

    public boolean getFeature(String name)
        throws ParserConfigurationException {
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return fSecureProcess;
        }
        if (features != null) {
            Boolean val = features.get(name);
            if (val != null) {
                return val;
            }
        }
        try {
            DOMParser domParser = new DocumentBuilderImpl(this, attributes, features).getDOMParser();
            return domParser.getFeature(name);
        }
        catch (SAXException e) {
            throw new ParserConfigurationException(e.getMessage());
        }
    }

    public void setFeature(String name, boolean value)
        throws ParserConfigurationException {
        if (features == null) {
            features = new HashMap<>();
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            if (System.getSecurityManager() != null && (!value)) {
                throw new ParserConfigurationException(
                        SAXMessageFormatter.formatMessage(null,
                        "jaxp-secureprocessing-feature", null));
            }
            fSecureProcess = value;
            features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
            return;
        }

        features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
        try {
            new DocumentBuilderImpl(this, attributes, features);
        }
        catch (SAXNotSupportedException e) {
            features.remove(name);
            throw new ParserConfigurationException(e.getMessage());
        }
        catch (SAXNotRecognizedException e) {
            features.remove(name);
            throw new ParserConfigurationException(e.getMessage());
        }
    }
}
