

package javax.xml.ws.spi;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.WebServiceFeature;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;



public abstract class ServiceDelegate {

    protected ServiceDelegate() {
    }


    public abstract <T> T getPort(QName portName,
            Class<T> serviceEndpointInterface);


    public abstract <T> T getPort(QName portName,
            Class<T> serviceEndpointInterface, WebServiceFeature... features);


    public abstract <T> T getPort(EndpointReference endpointReference,
           Class<T> serviceEndpointInterface, WebServiceFeature... features);



    public abstract <T> T getPort(Class<T> serviceEndpointInterface);



    public abstract <T> T getPort(Class<T> serviceEndpointInterface,
            WebServiceFeature... features);



    public abstract void addPort(QName portName, String bindingId,
            String endpointAddress);




    public abstract <T> Dispatch<T> createDispatch(QName portName, Class<T> type,
            Service.Mode mode);


    public abstract <T> Dispatch<T> createDispatch(QName portName, Class<T> type,
            Service.Mode mode, WebServiceFeature... features);


    public abstract <T> Dispatch<T> createDispatch(EndpointReference endpointReference,
            Class<T> type, Service.Mode mode,
            WebServiceFeature... features);




    public abstract Dispatch<Object> createDispatch(QName portName,
            JAXBContext context, Service.Mode mode);



    public abstract Dispatch<Object> createDispatch(QName portName,
            JAXBContext context, Service.Mode mode, WebServiceFeature... features);


    public abstract Dispatch<Object> createDispatch(EndpointReference endpointReference,
            JAXBContext context, Service.Mode mode,
            WebServiceFeature... features);



    public abstract QName getServiceName();


    public abstract Iterator<javax.xml.namespace.QName> getPorts();


    public abstract java.net.URL getWSDLDocumentLocation();


    public abstract HandlerResolver getHandlerResolver();


    public abstract void setHandlerResolver(HandlerResolver handlerResolver);


    public abstract java.util.concurrent.Executor getExecutor();


    public abstract void setExecutor(java.util.concurrent.Executor executor);

}
