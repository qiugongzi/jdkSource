

package javax.xml.transform;


public abstract class TransformerFactory {


    protected TransformerFactory() { }




    public static TransformerFactory newInstance()
        throws TransformerFactoryConfigurationError {

        return FactoryFinder.find(

            TransformerFactory.class,

            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    }


    public static TransformerFactory newInstance(String factoryClassName, ClassLoader classLoader)
        throws TransformerFactoryConfigurationError{

        return  FactoryFinder.newInstance(TransformerFactory.class,
                    factoryClassName, classLoader, false);
    }

    public abstract Transformer newTransformer(Source source)
        throws TransformerConfigurationException;


    public abstract Transformer newTransformer()
        throws TransformerConfigurationException;


    public abstract Templates newTemplates(Source source)
        throws TransformerConfigurationException;


    public abstract Source getAssociatedStylesheet(
        Source source,
        String media,
        String title,
        String charset)
        throws TransformerConfigurationException;


    public abstract void setURIResolver(URIResolver resolver);


    public abstract URIResolver getURIResolver();

    public abstract void setFeature(String name, boolean value)
                throws TransformerConfigurationException;


    public abstract boolean getFeature(String name);


    public abstract void setAttribute(String name, Object value);


    public abstract Object getAttribute(String name);


    public abstract void setErrorListener(ErrorListener listener);


    public abstract ErrorListener getErrorListener();

}
