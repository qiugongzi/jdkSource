


package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


final class ValidatorImpl extends Validator implements PSVIProvider {

    private XMLSchemaValidatorComponentManager fComponentManager;


    private ValidatorHandlerImpl fSAXValidatorHelper;


    private DOMValidatorHelper fDOMValidatorHelper;


    private StreamValidatorHelper fStreamValidatorHelper;


    private StAXValidatorHelper fStaxValidatorHelper;


    private boolean fConfigurationChanged = false;


    private boolean fErrorHandlerChanged = false;


    private boolean fResourceResolverChanged = false;


    private static final String CURRENT_ELEMENT_NODE = Constants.XERCES_PROPERTY_PREFIX + Constants.CURRENT_ELEMENT_NODE_PROPERTY;

    public ValidatorImpl(XSGrammarPoolContainer grammarContainer) {
        fComponentManager = new XMLSchemaValidatorComponentManager(grammarContainer);
        setErrorHandler(null);
        setResourceResolver(null);
    }

    public void validate(Source source, Result result)
        throws SAXException, IOException {
        if (source instanceof SAXSource) {
            if (fSAXValidatorHelper == null) {
                fSAXValidatorHelper = new ValidatorHandlerImpl(fComponentManager);
            }
            fSAXValidatorHelper.validate(source, result);
        }
        else if (source instanceof DOMSource) {
            if (fDOMValidatorHelper == null) {
                fDOMValidatorHelper = new DOMValidatorHelper(fComponentManager);
            }
            fDOMValidatorHelper.validate(source, result);
        }
        else if (source instanceof StreamSource) {
            if (fStreamValidatorHelper == null) {
                fStreamValidatorHelper = new StreamValidatorHelper(fComponentManager);
            }
            fStreamValidatorHelper.validate(source, result);
        }
        else if (source instanceof StAXSource) {
            if (fStaxValidatorHelper == null) {
                fStaxValidatorHelper = new StAXValidatorHelper(fComponentManager);
            }
            fStaxValidatorHelper.validate(source, result);
        }
        else if (source == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    "SourceParameterNull", null));
        }
        else {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    "SourceNotAccepted", new Object [] {source.getClass().getName()}));
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        fErrorHandlerChanged = (errorHandler != null);
        fComponentManager.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return fComponentManager.getErrorHandler();
    }

    public void setResourceResolver(LSResourceResolver resourceResolver) {
        fResourceResolverChanged = (resourceResolver != null);
        fComponentManager.setResourceResolver(resourceResolver);
    }

    public LSResourceResolver getResourceResolver() {
        return fComponentManager.getResourceResolver();
    }

    public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return fComponentManager.getFeature(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }

    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            fComponentManager.setFeature(name, value);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key;
            if (e.getType() == Status.NOT_ALLOWED) {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    "jaxp-secureprocessing-feature", null));
            } else if (e.getType() == Status.NOT_RECOGNIZED) {
                key = "feature-not-recognized";
            } else {
                key = "feature-not-supported";
            }
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
        fConfigurationChanged = true;
    }

    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (CURRENT_ELEMENT_NODE.equals(name)) {
            return (fDOMValidatorHelper != null) ? fDOMValidatorHelper.getCurrentElement() : null;
        }
        try {
            return fComponentManager.getProperty(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }

    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            fComponentManager.setProperty(name, object);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
        fConfigurationChanged = true;
    }

    public void reset() {
        if (fConfigurationChanged) {
            fComponentManager.restoreInitialState();
            setErrorHandler(null);
            setResourceResolver(null);
            fConfigurationChanged = false;
            fErrorHandlerChanged = false;
            fResourceResolverChanged = false;
        }
        else {
            if (fErrorHandlerChanged) {
                setErrorHandler(null);
                fErrorHandlerChanged = false;
            }
            if (fResourceResolverChanged) {
                setResourceResolver(null);
                fResourceResolverChanged = false;
            }
        }
    }



    public ElementPSVI getElementPSVI() {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getElementPSVI() : null;
    }

    public AttributePSVI getAttributePSVI(int index) {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getAttributePSVI(index) : null;
    }

    public AttributePSVI getAttributePSVIByName(String uri, String localname) {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getAttributePSVIByName(uri, localname) : null;
    }

}