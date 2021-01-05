




package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import org.xml.sax.XMLFilter;


public class SmartTransformerFactoryImpl extends SAXTransformerFactory
{

    private static final String CLASS_NAME = "SmartTransformerFactoryImpl";

    private SAXTransformerFactory _xsltcFactory = null;
    private SAXTransformerFactory _xalanFactory = null;
    private SAXTransformerFactory _currFactory = null;
    private ErrorListener      _errorlistener = null;
    private URIResolver        _uriresolver = null;


    private boolean featureSecureProcessing = false;


    public SmartTransformerFactoryImpl() { }

    private void createXSLTCTransformerFactory() {
        _xsltcFactory = new TransformerFactoryImpl();
        _currFactory = _xsltcFactory;
    }

    private void createXalanTransformerFactory() {
        final String xalanMessage =
            "com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl "+
            "could not create an "+
            "com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.";
        try {
            Class xalanFactClass = ObjectFactory.findProviderClass(
                "com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl",
                true);
            _xalanFactory = (SAXTransformerFactory)
                xalanFactClass.newInstance();
        }
        catch (ClassNotFoundException e) {
            System.err.println(xalanMessage);
        }
        catch (InstantiationException e) {
            System.err.println(xalanMessage);
        }
        catch (IllegalAccessException e) {
            System.err.println(xalanMessage);
        }
        _currFactory = _xalanFactory;
    }

    public void setErrorListener(ErrorListener listener)
        throws IllegalArgumentException
    {
        _errorlistener = listener;
    }

    public ErrorListener getErrorListener() {
        return _errorlistener;
    }

    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        if ((name.equals("translet-name")) || (name.equals("debug"))) {
            if (_xsltcFactory == null) {
                createXSLTCTransformerFactory();
            }
            return _xsltcFactory.getAttribute(name);
        }
        else {
            if (_xalanFactory == null) {
                createXalanTransformerFactory();
            }
            return _xalanFactory.getAttribute(name);
        }
    }

    public void setAttribute(String name, Object value)
        throws IllegalArgumentException {
        if ((name.equals("translet-name")) || (name.equals("debug"))) {
            if (_xsltcFactory == null) {
                createXSLTCTransformerFactory();
            }
            _xsltcFactory.setAttribute(name, value);
        }
        else {
            if (_xalanFactory == null) {
                createXalanTransformerFactory();
            }
            _xalanFactory.setAttribute(name, value);
        }
    }


    public void setFeature(String name, boolean value)
        throws TransformerConfigurationException {

        if (name == null) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_SET_FEATURE_NULL_NAME);
            throw new NullPointerException(err.toString());
        }
        else if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            featureSecureProcessing = value;
            return;
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_UNSUPPORTED_FEATURE, name);
            throw new TransformerConfigurationException(err.toString());
        }
    }


    public boolean getFeature(String name) {
        String[] features = {
            DOMSource.FEATURE,
            DOMResult.FEATURE,
            SAXSource.FEATURE,
            SAXResult.FEATURE,
            StreamSource.FEATURE,
            StreamResult.FEATURE
        };

        if (name == null) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_GET_FEATURE_NULL_NAME);
            throw new NullPointerException(err.toString());
        }

        for (int i = 0; i < features.length; i++) {
            if (name.equals(features[i]))
                return true;
        }

        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return featureSecureProcessing;
        }

        return false;
    }

    public URIResolver getURIResolver() {
        return _uriresolver;
    }

    public void setURIResolver(URIResolver resolver) {
        _uriresolver = resolver;
    }

    public Source getAssociatedStylesheet(Source source, String media,
                                          String title, String charset)
        throws TransformerConfigurationException
    {
        if (_currFactory == null) {
            createXSLTCTransformerFactory();
        }
        return _currFactory.getAssociatedStylesheet(source, media,
                title, charset);
    }


    public Transformer newTransformer()
        throws TransformerConfigurationException
    {
        if (_xalanFactory == null) {
            createXalanTransformerFactory();
        }
        if (_errorlistener != null) {
            _xalanFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xalanFactory.setURIResolver(_uriresolver);
        }
        _currFactory = _xalanFactory;
        return _currFactory.newTransformer();
    }


    public Transformer newTransformer(Source source) throws
        TransformerConfigurationException
    {
        if (_xalanFactory == null) {
            createXalanTransformerFactory();
        }
        if (_errorlistener != null) {
            _xalanFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xalanFactory.setURIResolver(_uriresolver);
        }
        _currFactory = _xalanFactory;
        return _currFactory.newTransformer(source);
    }


    public Templates newTemplates(Source source)
        throws TransformerConfigurationException
    {
        if (_xsltcFactory == null) {
            createXSLTCTransformerFactory();
        }
        if (_errorlistener != null) {
            _xsltcFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xsltcFactory.setURIResolver(_uriresolver);
        }
        _currFactory = _xsltcFactory;
        return _currFactory.newTemplates(source);
    }


    public TemplatesHandler newTemplatesHandler()
        throws TransformerConfigurationException
    {
        if (_xsltcFactory == null) {
            createXSLTCTransformerFactory();
        }
        if (_errorlistener != null) {
            _xsltcFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xsltcFactory.setURIResolver(_uriresolver);
        }
        return _xsltcFactory.newTemplatesHandler();
    }


    public TransformerHandler newTransformerHandler()
        throws TransformerConfigurationException
    {
        if (_xalanFactory == null) {
            createXalanTransformerFactory();
        }
        if (_errorlistener != null) {
            _xalanFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xalanFactory.setURIResolver(_uriresolver);
        }
        return _xalanFactory.newTransformerHandler();
    }


    public TransformerHandler newTransformerHandler(Source src)
        throws TransformerConfigurationException
    {
        if (_xalanFactory == null) {
            createXalanTransformerFactory();
        }
        if (_errorlistener != null) {
            _xalanFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xalanFactory.setURIResolver(_uriresolver);
        }
        return _xalanFactory.newTransformerHandler(src);
    }



    public TransformerHandler newTransformerHandler(Templates templates)
        throws TransformerConfigurationException
    {
        if (_xsltcFactory == null) {
            createXSLTCTransformerFactory();
        }
        if (_errorlistener != null) {
            _xsltcFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xsltcFactory.setURIResolver(_uriresolver);
        }
        return _xsltcFactory.newTransformerHandler(templates);
    }



    public XMLFilter newXMLFilter(Source src)
        throws TransformerConfigurationException {
        if (_xsltcFactory == null) {
            createXSLTCTransformerFactory();
        }
        if (_errorlistener != null) {
            _xsltcFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xsltcFactory.setURIResolver(_uriresolver);
        }
        Templates templates = _xsltcFactory.newTemplates(src);
        if (templates == null ) return null;
        return newXMLFilter(templates);
    }


    public XMLFilter newXMLFilter(Templates templates)
        throws TransformerConfigurationException {
        try {
            return new com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter(templates);
        }
        catch(TransformerConfigurationException e1) {
            if (_xsltcFactory == null) {
                createXSLTCTransformerFactory();
            }
            ErrorListener errorListener = _xsltcFactory.getErrorListener();
            if(errorListener != null) {
                try {
                    errorListener.fatalError(e1);
                    return null;
                }
                catch( TransformerException e2) {
                    new TransformerConfigurationException(e2);
                }
            }
            throw e1;
        }
    }
}
