


package com.sun.org.apache.xpath.internal.jaxp;

import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import com.sun.org.apache.xalan.internal.res.XSLMessages;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import java.util.Vector;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import jdk.xml.internal.JdkXmlFeatures;


public class JAXPExtensionsProvider implements ExtensionsProvider {

    private final XPathFunctionResolver resolver;
    private boolean extensionInvocationDisabled = false;

    public JAXPExtensionsProvider(XPathFunctionResolver resolver) {
        this.resolver = resolver;
        this.extensionInvocationDisabled = false;
    }

    public JAXPExtensionsProvider(XPathFunctionResolver resolver,
        boolean featureSecureProcessing, JdkXmlFeatures featureManager ) {
        this.resolver = resolver;
        if (featureSecureProcessing &&
                !featureManager.getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION)) {
            this.extensionInvocationDisabled = true;
        }
    }



    public boolean functionAvailable(String ns, String funcName)
          throws javax.xml.transform.TransformerException {
      try {
        if ( funcName == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                new Object[] {"Function Name"} );
            throw new NullPointerException ( fmsg );
        }
        javax.xml.namespace.QName myQName = new QName( ns, funcName );
        javax.xml.xpath.XPathFunction xpathFunction =
            resolver.resolveFunction ( myQName, 0 );
        if (  xpathFunction == null ) {
            return false;
        }
        return true;
      } catch ( Exception e ) {
        return false;
      }


    }



    public boolean elementAvailable(String ns, String elemName)
          throws javax.xml.transform.TransformerException {
        return false;
    }


    public Object extFunction(String ns, String funcName, Vector argVec,
        Object methodKey) throws javax.xml.transform.TransformerException {
        try {

            if ( funcName == null ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"Function Name"} );
                throw new NullPointerException ( fmsg );
            }
            javax.xml.namespace.QName myQName = new QName( ns, funcName );

            if ( extensionInvocationDisabled ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,
                    new Object[] { myQName.toString() } );
                throw new XPathFunctionException ( fmsg );
            }

            int arity = argVec.size();

            javax.xml.xpath.XPathFunction xpathFunction =
                resolver.resolveFunction ( myQName, arity );

            ArrayList argList = new ArrayList( arity);
            for ( int i=0; i<arity; i++ ) {
                Object argument = argVec.elementAt( i );
                if ( argument instanceof XNodeSet ) {
                    argList.add ( i, ((XNodeSet)argument).nodelist() );
                } else if ( argument instanceof XObject ) {
                    Object passedArgument = ((XObject)argument).object();
                    argList.add ( i, passedArgument );
                } else {
                    argList.add ( i, argument );
                }
            }

            return ( xpathFunction.evaluate ( argList ));
        } catch ( XPathFunctionException xfe ) {
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException ( xfe );
        } catch ( Exception e ) {
            throw new javax.xml.transform.TransformerException ( e );
        }

    }


    public Object extFunction(FuncExtFunction extFunction,
                              Vector argVec)
        throws javax.xml.transform.TransformerException {
        try {
            String namespace = extFunction.getNamespace();
            String functionName = extFunction.getFunctionName();
            int arity = extFunction.getArgCount();
            javax.xml.namespace.QName myQName =
                new javax.xml.namespace.QName( namespace, functionName );

            if ( extensionInvocationDisabled ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,                    new Object[] { myQName.toString() } );
                throw new XPathFunctionException ( fmsg );
            }

            XPathFunction xpathFunction =
                resolver.resolveFunction( myQName, arity );

            ArrayList argList = new ArrayList( arity);
            for ( int i=0; i<arity; i++ ) {
                Object argument = argVec.elementAt( i );
                if ( argument instanceof XNodeSet ) {
                    argList.add ( i, ((XNodeSet)argument).nodelist() );
                } else if ( argument instanceof XObject ) {
                    Object passedArgument = ((XObject)argument).object();
                    argList.add ( i, passedArgument );
                } else {
                    argList.add ( i, argument );
                }
            }

            return ( xpathFunction.evaluate ( argList ));

        } catch ( XPathFunctionException xfe ) {
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException ( xfe );
        } catch ( Exception e ) {
            throw new javax.xml.transform.TransformerException ( e );
        }
    }

}
