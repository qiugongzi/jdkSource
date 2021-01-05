

package com.sun.org.apache.xpath.internal.jaxp;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import javax.xml.xpath.XPathExpression;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.*;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import com.sun.org.apache.xalan.internal.res.XSLMessages;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.NodeIterator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import java.io.IOException;

import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;


public class XPathImpl implements javax.xml.xpath.XPath {

    private XPathVariableResolver variableResolver;
    private XPathFunctionResolver functionResolver;
    private XPathVariableResolver origVariableResolver;
    private XPathFunctionResolver origFunctionResolver;
    private NamespaceContext namespaceContext=null;
    private JAXPPrefixResolver prefixResolver;
    private boolean featureSecureProcessing = false;
    private boolean overrideDefaultParser = true;
    private final JdkXmlFeatures featureManager;

    XPathImpl( XPathVariableResolver vr, XPathFunctionResolver fr ) {
        this(vr, fr, false, new JdkXmlFeatures(false));
    }

    XPathImpl( XPathVariableResolver vr, XPathFunctionResolver fr,
            boolean featureSecureProcessing, JdkXmlFeatures featureManager) {
        this.origVariableResolver = this.variableResolver = vr;
        this.origFunctionResolver = this.functionResolver = fr;
        this.featureSecureProcessing = featureSecureProcessing;
        this.featureManager = featureManager;
        this.overrideDefaultParser = featureManager.getFeature(
                JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    }


    public void setXPathVariableResolver(XPathVariableResolver resolver) {
        if ( resolver == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"XPathVariableResolver"} );
            throw new NullPointerException( fmsg );
        }
        this.variableResolver = resolver;
    }


    public XPathVariableResolver getXPathVariableResolver() {
        return variableResolver;
    }


    public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
        if ( resolver == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"XPathFunctionResolver"} );
            throw new NullPointerException( fmsg );
        }
        this.functionResolver = resolver;
    }


    public XPathFunctionResolver getXPathFunctionResolver() {
        return functionResolver;
    }


    public void setNamespaceContext(NamespaceContext nsContext) {
        if ( nsContext == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"NamespaceContext"} );
            throw new NullPointerException( fmsg );
        }
        this.namespaceContext = nsContext;
        this.prefixResolver = new JAXPPrefixResolver ( nsContext );
    }


    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    private static Document d = null;

    private DocumentBuilder getParser() {
        try {
            DocumentBuilderFactory dbf = JdkXmlUtils.getDOMFactory(overrideDefaultParser);
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }


    private XObject eval(String expression, Object contextItem)
        throws javax.xml.transform.TransformerException {
        com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath( expression,
            null, prefixResolver, com.sun.org.apache.xpath.internal.XPath.SELECT );
        com.sun.org.apache.xpath.internal.XPathContext xpathSupport = null;
        if ( functionResolver != null ) {
            JAXPExtensionsProvider jep = new JAXPExtensionsProvider(
                    functionResolver, featureSecureProcessing, featureManager );
            xpathSupport = new com.sun.org.apache.xpath.internal.XPathContext( jep );
        } else {
            xpathSupport = new com.sun.org.apache.xpath.internal.XPathContext();
        }

        XObject xobj = null;

        xpathSupport.setVarStack(new JAXPVariableStack(variableResolver));

        if ( contextItem instanceof Node ) {
            xobj = xpath.execute (xpathSupport, (Node)contextItem,
                    prefixResolver );
        } else {
            xobj = xpath.execute ( xpathSupport, DTM.NULL, prefixResolver );
        }

        return xobj;
    }


    public Object evaluate(String expression, Object item, QName returnType)
            throws XPathExpressionException {
        if ( expression == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"XPath expression"} );
            throw new NullPointerException ( fmsg );
        }
        if ( returnType == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"returnType"} );
            throw new NullPointerException ( fmsg );
        }
        if ( !isSupported ( returnType ) ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_UNSUPPORTED_RETURN_TYPE,
                    new Object[] { returnType.toString() } );
            throw new IllegalArgumentException ( fmsg );
        }

        try {

            XObject resultObject = eval( expression, item );
            return getResultAsType( resultObject, returnType );
        } catch ( java.lang.NullPointerException npe ) {
            throw new XPathExpressionException ( npe );
        } catch ( javax.xml.transform.TransformerException te ) {
            Throwable nestedException = te.getException();
            if ( nestedException instanceof javax.xml.xpath.XPathFunctionException ) {
                throw (javax.xml.xpath.XPathFunctionException)nestedException;
            } else {
                throw new XPathExpressionException ( te );
            }
        }

    }

    private boolean isSupported( QName returnType ) {
        if ( ( returnType.equals( XPathConstants.STRING ) ) ||
             ( returnType.equals( XPathConstants.NUMBER ) ) ||
             ( returnType.equals( XPathConstants.BOOLEAN ) ) ||
             ( returnType.equals( XPathConstants.NODE ) ) ||
             ( returnType.equals( XPathConstants.NODESET ) )  ) {

            return true;
        }
        return false;
     }

    private Object getResultAsType( XObject resultObject, QName returnType )
        throws javax.xml.transform.TransformerException {
        if ( returnType.equals( XPathConstants.STRING ) ) {
            return resultObject.str();
        }
        if ( returnType.equals( XPathConstants.NUMBER ) ) {
            return new Double ( resultObject.num());
        }
        if ( returnType.equals( XPathConstants.BOOLEAN ) ) {
            return new Boolean( resultObject.bool());
        }
        if ( returnType.equals( XPathConstants.NODESET ) ) {
            return resultObject.nodelist();
        }
        if ( returnType.equals( XPathConstants.NODE ) ) {
            NodeIterator ni = resultObject.nodeset();
            return ni.nextNode();
        }
        String fmsg = XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_UNSUPPORTED_RETURN_TYPE,
                new Object[] { returnType.toString()});
        throw new IllegalArgumentException( fmsg );
    }




    public String evaluate(String expression, Object item)
        throws XPathExpressionException {
        return (String)this.evaluate( expression, item, XPathConstants.STRING );
    }


    public XPathExpression compile(String expression)
        throws XPathExpressionException {
        if ( expression == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"XPath expression"} );
            throw new NullPointerException ( fmsg );
        }
        try {
            com.sun.org.apache.xpath.internal.XPath xpath = new XPath (expression, null,
                    prefixResolver, com.sun.org.apache.xpath.internal.XPath.SELECT );
            XPathExpressionImpl ximpl = new XPathExpressionImpl(xpath,
                    prefixResolver, functionResolver, variableResolver,
                    featureSecureProcessing, featureManager );
            return ximpl;
        } catch ( javax.xml.transform.TransformerException te ) {
            throw new XPathExpressionException ( te ) ;
        }
    }



    public Object evaluate(String expression, InputSource source,
            QName returnType) throws XPathExpressionException {
        if( source== null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"source"} );
            throw new NullPointerException ( fmsg );
        }
        if ( expression == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"XPath expression"} );
            throw new NullPointerException ( fmsg );
        }
        if ( returnType == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"returnType"} );
            throw new NullPointerException ( fmsg );
        }

        if ( !isSupported ( returnType ) ) {
            String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_UNSUPPORTED_RETURN_TYPE,
                    new Object[] { returnType.toString() } );
            throw new IllegalArgumentException ( fmsg );
        }

        try {

            Document document = getParser().parse( source );

            XObject resultObject = eval( expression, document );
            return getResultAsType( resultObject, returnType );
        } catch ( SAXException e ) {
            throw new XPathExpressionException ( e );
        } catch( IOException e ) {
            throw new XPathExpressionException ( e );
        } catch ( javax.xml.transform.TransformerException te ) {
            Throwable nestedException = te.getException();
            if ( nestedException instanceof javax.xml.xpath.XPathFunctionException ) {
                throw (javax.xml.xpath.XPathFunctionException)nestedException;
            } else {
                throw new XPathExpressionException ( te );
            }
        }

    }





    public String evaluate(String expression, InputSource source)
        throws XPathExpressionException {
        return (String)this.evaluate( expression, source, XPathConstants.STRING );
    }


    public void reset() {
        this.variableResolver = this.origVariableResolver;
        this.functionResolver = this.origFunctionResolver;
        this.namespaceContext = null;
    }

}
