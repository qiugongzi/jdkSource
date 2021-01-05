

package javax.xml.ws.spi;

import java.net.URL;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.w3c.dom.Element;


public abstract class Provider {


    static public final String JAXWSPROVIDER_PROPERTY
            = "javax.xml.ws.spi.Provider";


    static final String DEFAULT_JAXWSPROVIDER
            = "com.sun"+".xml.internal.ws.spi.ProviderImpl";


    static private final Method loadMethod;
    static private final Method iteratorMethod;
    static {
        Method tLoadMethod = null;
        Method tIteratorMethod = null;
        try {
            Class<?> clazz = Class.forName("java.util.ServiceLoader");
            tLoadMethod = clazz.getMethod("load", Class.class);
            tIteratorMethod = clazz.getMethod("iterator");
        } catch(ClassNotFoundException ce) {
            } catch(NoSuchMethodException ne) {
            }
        loadMethod = tLoadMethod;
        iteratorMethod = tIteratorMethod;
    }



    protected Provider() {
    }


    public static Provider provider() {
        try {
            Object provider = getProviderUsingServiceLoader();
            if (provider == null) {
                provider = FactoryFinder.find(JAXWSPROVIDER_PROPERTY, DEFAULT_JAXWSPROVIDER);
            }
            if (!(provider instanceof Provider)) {
                Class pClass = Provider.class;
                String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if(loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                URL targetTypeURL  = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast" +
                       provider.getClass().getClassLoader().getResource(classnameAsResource) +
                       "to" + targetTypeURL.toString() );
            }
            return (Provider) provider;
        } catch (WebServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WebServiceException("Unable to createEndpointReference Provider", ex);
        }
    }


    private static Provider getProviderUsingServiceLoader() {
        if (loadMethod != null) {
            Object loader;
            try {
                loader = loadMethod.invoke(null, Provider.class);
            } catch (Exception e) {
                throw new WebServiceException("Cannot invoke java.util.ServiceLoader#load()", e);
            }

            Iterator<Provider> it;
            try {
                it = (Iterator<Provider>)iteratorMethod.invoke(loader);
            } catch(Exception e) {
                throw new WebServiceException("Cannot invoke java.util.ServiceLoader#iterator()", e);
            }
            return it.hasNext() ? it.next() : null;
        }
        return null;
    }


    public abstract ServiceDelegate createServiceDelegate(
            java.net.URL wsdlDocumentLocation,
            QName serviceName, Class<? extends Service> serviceClass);


    public ServiceDelegate createServiceDelegate(
            java.net.URL wsdlDocumentLocation,
            QName serviceName, Class<? extends Service> serviceClass, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }



    public abstract Endpoint createEndpoint(String bindingId,
            Object implementor);



    public abstract Endpoint createAndPublishEndpoint(String address,
            Object implementor);


    public abstract EndpointReference readEndpointReference(javax.xml.transform.Source eprInfoset);



    public abstract <T> T getPort(EndpointReference endpointReference,
            Class<T> serviceEndpointInterface,
            WebServiceFeature... features);


    public abstract W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName,
            List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters);



    public W3CEndpointReference createW3CEndpointReference(String address,
            QName interfaceName, QName serviceName, QName portName,
            List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters,
            List<Element> elements, Map<QName, String> attributes) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }


    public Endpoint createAndPublishEndpoint(String address,
            Object implementor, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }


    public Endpoint createEndpoint(String bindingId, Object implementor,
            WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }


    public Endpoint createEndpoint(String bindingId, Class<?> implementorClass,
            Invoker invoker, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }

}
