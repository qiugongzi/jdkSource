

package javax.xml.bind;

import org.w3c.dom.Node;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;


public abstract class JAXBContext {


    public static final String JAXB_CONTEXT_FACTORY =
        "javax.xml.bind.context.factory";


    protected JAXBContext() {
    }



    public static JAXBContext newInstance( String contextPath )
        throws JAXBException {

        return newInstance( contextPath, getContextClassLoader());
    }


    public static JAXBContext newInstance( String contextPath, ClassLoader classLoader ) throws JAXBException {

        return newInstance(contextPath,classLoader,Collections.<String,Object>emptyMap());
    }


    public static JAXBContext newInstance( String contextPath, ClassLoader classLoader, Map<String,?>  properties  )
        throws JAXBException {

        return ContextFinder.find(

                        JAXB_CONTEXT_FACTORY,


                        contextPath,


                        classLoader,
                        properties );
    }

public static JAXBContext newInstance( Class... classesToBeBound )
        throws JAXBException {

        return newInstance(classesToBeBound,Collections.<String,Object>emptyMap());
    }


    public static JAXBContext newInstance( Class[] classesToBeBound, Map<String,?> properties )
        throws JAXBException {

        if (classesToBeBound == null) {
                throw new IllegalArgumentException();
        }

        for (int i = classesToBeBound.length - 1; i >= 0; i--) {
            if (classesToBeBound[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        return ContextFinder.find(classesToBeBound,properties);
    }


    public abstract Unmarshaller createUnmarshaller() throws JAXBException;



    public abstract Marshaller createMarshaller() throws JAXBException;



    public abstract Validator createValidator() throws JAXBException;


    public <T> Binder<T> createBinder(Class<T> domType) {
        throw new UnsupportedOperationException();
    }


    public Binder<Node> createBinder() {
        return createBinder(Node.class);
    }


    public JAXBIntrospector createJAXBIntrospector() {
        throw new UnsupportedOperationException();
    }


    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException  {
        throw new UnsupportedOperationException();
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return (ClassLoader) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public java.lang.Object run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    });
        }
    }

}
