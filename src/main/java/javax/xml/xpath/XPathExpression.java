

package javax.xml.xpath;

import org.xml.sax.InputSource;
import javax.xml.namespace.QName;


public interface XPathExpression {


    public Object evaluate(Object item, QName returnType)
        throws XPathExpressionException;


    public String evaluate(Object item)
        throws XPathExpressionException;


    public Object evaluate(InputSource source, QName returnType)
        throws XPathExpressionException;


    public String evaluate(InputSource source)
        throws XPathExpressionException;
}
