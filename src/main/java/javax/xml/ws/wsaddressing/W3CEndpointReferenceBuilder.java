

package javax.xml.ws.wsaddressing;


import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.Provider;



public final class W3CEndpointReferenceBuilder {

    public W3CEndpointReferenceBuilder() {
        referenceParameters = new ArrayList<Element>();
        metadata = new ArrayList<Element>();
        attributes = new HashMap<QName, String>();
        elements = new ArrayList<Element>();
    }


    public W3CEndpointReferenceBuilder address(String address) {
        this.address = address;
        return this;
    }


    public W3CEndpointReferenceBuilder interfaceName(QName interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }


    public W3CEndpointReferenceBuilder serviceName(QName serviceName) {
        this.serviceName = serviceName;
        return this;
    }


    public W3CEndpointReferenceBuilder endpointName(QName endpointName) {
        if (serviceName == null) {
            throw new IllegalStateException("The W3CEndpointReferenceBuilder's serviceName must be set before setting the endpointName: "+endpointName);
        }

        this.endpointName = endpointName;
        return this;
    }


    public W3CEndpointReferenceBuilder wsdlDocumentLocation(String wsdlDocumentLocation) {
        this.wsdlDocumentLocation = wsdlDocumentLocation;
        return this;
    }


    public W3CEndpointReferenceBuilder referenceParameter(Element referenceParameter) {
        if (referenceParameter == null)
            throw new java.lang.IllegalArgumentException("The referenceParameter cannot be null.");
        referenceParameters.add(referenceParameter);
        return this;
    }


    public W3CEndpointReferenceBuilder metadata(Element metadataElement) {
        if (metadataElement == null)
            throw new java.lang.IllegalArgumentException("The metadataElement cannot be null.");
        metadata.add(metadataElement);
        return this;
    }


    public W3CEndpointReferenceBuilder element(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("The extension element cannot be null.");
        }
        elements.add(element);
        return this;
    }


    public W3CEndpointReferenceBuilder attribute(QName name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("The extension attribute name or value cannot be null.");
        }
        attributes.put(name, value);
        return this;
    }


    public W3CEndpointReference build() {
        if (elements.isEmpty() && attributes.isEmpty() && interfaceName == null) {
            return Provider.provider().createW3CEndpointReference(address,
                serviceName, endpointName, metadata, wsdlDocumentLocation,
                referenceParameters);
        }
        return Provider.provider().createW3CEndpointReference(address,
                interfaceName, serviceName, endpointName, metadata, wsdlDocumentLocation,
                referenceParameters, elements, attributes);
    }

    private String address;
    private List<Element> referenceParameters;
    private List<Element> metadata;
    private QName interfaceName;
    private QName serviceName;
    private QName endpointName;
    private String wsdlDocumentLocation;
    private Map<QName,String> attributes;
    private List<Element> elements;
}
