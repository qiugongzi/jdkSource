

package javax.xml.xpath;

import org.xml.sax.InputSource;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;


public interface XPath {


        public void reset();


    public void setXPathVariableResolver(XPathVariableResolver resolver);


    public XPathVariableResolver getXPathVariableResolver();


    public void setXPathFunctionResolver(XPathFunctionResolver resolver);


    public XPathFunctionResolver getXPathFunctionResolver();


    public void setNamespaceContext(NamespaceContext nsContext);


    public NamespaceContext getNamespaceContext();


    public XPathExpression compile(String expression)
        throws XPathExpressionException;


    public Object evaluate(String expression, Object item, QName returnType)
        throws XPathExpressionException;


    public String evaluate(String expression, Object item)
        throws XPathExpressionException;


    public Object evaluate(
        String expression,
        InputSource source,
        QName returnType)
        throws XPathExpressionException;


    public String evaluate(String expression, InputSource source)
        throws XPathExpressionException;
}
