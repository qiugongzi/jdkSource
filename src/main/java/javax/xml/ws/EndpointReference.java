

package javax.xml.ws;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.StringWriter;


@XmlTransient public abstract class EndpointReference {
    protected EndpointReference(){}


    public static EndpointReference readFrom(Source eprInfoset) {
        return Provider.provider().readEndpointReference(eprInfoset);
    }


    public abstract void writeTo(Result result);



    public <T> T getPort(Class<T> serviceEndpointInterface,
                         WebServiceFeature... features) {
        return Provider.provider().getPort(this, serviceEndpointInterface,
                                           features);
    }


    public String toString() {
        StringWriter w = new StringWriter();
        writeTo(new StreamResult(w));
        return w.toString();
    }
}
