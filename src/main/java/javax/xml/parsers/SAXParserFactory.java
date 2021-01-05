

package javax.xml.parsers;

import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public abstract class SAXParserFactory {


    private boolean validating = false;


    private boolean namespaceAware = false;


    protected SAXParserFactory () {

    }



    public static SAXParserFactory newInstance() {
        return FactoryFinder.find(

                SAXParserFactory.class,

                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }


    public static SAXParserFactory newInstance(String factoryClassName, ClassLoader classLoader){
            return FactoryFinder.newInstance(SAXParserFactory.class,
                    factoryClassName, classLoader, false);
    }



    public abstract SAXParser newSAXParser()
        throws ParserConfigurationException, SAXException;




    public void setNamespaceAware(boolean awareness) {
        this.namespaceAware = awareness;
    }



    public void setValidating(boolean validating) {
        this.validating = validating;
    }



    public boolean isNamespaceAware() {
        return namespaceAware;
    }



    public boolean isValidating() {
        return validating;
    }


    public abstract void setFeature(String name, boolean value)
        throws ParserConfigurationException, SAXNotRecognizedException,
                SAXNotSupportedException;


    public abstract boolean getFeature(String name)
        throws ParserConfigurationException, SAXNotRecognizedException,
                SAXNotSupportedException;



    public Schema getSchema() {
        throw new UnsupportedOperationException(
            "This parser does not support specification \""
            + this.getClass().getPackage().getSpecificationTitle()
            + "\" version \""
            + this.getClass().getPackage().getSpecificationVersion()
            + "\""
            );
    }


    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException(
            "This parser does not support specification \""
            + this.getClass().getPackage().getSpecificationTitle()
            + "\" version \""
            + this.getClass().getPackage().getSpecificationVersion()
            + "\""
            );
    }


    public void setXIncludeAware(final boolean state) {
        if (state) {
            throw new UnsupportedOperationException(" setXIncludeAware " +
                "is not supported on this JAXP"  +
                " implementation or earlier: " + this.getClass());
        }
    }


    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException(
            "This parser does not support specification \""
            + this.getClass().getPackage().getSpecificationTitle()
            + "\" version \""
            + this.getClass().getPackage().getSpecificationVersion()
            + "\""
            );
    }
}
