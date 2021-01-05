

package com.sun.org.apache.xml.internal.security.utils.resolver;

import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;


public abstract class ResourceResolverSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ResourceResolverSpi.class.getName());


    protected java.util.Map<String, String> properties = null;


    @Deprecated
    protected final boolean secureValidation = true;


    @Deprecated
    public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
        throws ResourceResolverException {
        throw new UnsupportedOperationException();
    }


    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {
        return engineResolve(context.attr, context.baseUri);
    }


    public void engineSetProperty(String key, String value) {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        properties.put(key, value);
    }


    public String engineGetProperty(String key) {
        if (properties == null) {
            return null;
        }
        return properties.get(key);
    }


    public void engineAddProperies(Map<String, String> newProperties) {
        if (newProperties != null && !newProperties.isEmpty()) {
            if (properties == null) {
                properties = new HashMap<String, String>();
            }
            properties.putAll(newProperties);
        }
    }


    public boolean engineIsThreadSafe() {
        return false;
    }


    @Deprecated
    public boolean engineCanResolve(Attr uri, String BaseURI) {
        throw new UnsupportedOperationException();
    }


    public boolean engineCanResolveURI(ResourceResolverContext context) {
        return engineCanResolve( context.attr, context.baseUri );
    }


    public String[] engineGetPropertyKeys() {
        return new String[0];
    }


    public boolean understandsProperty(String propertyToTest) {
        String[] understood = this.engineGetPropertyKeys();

        if (understood != null) {
            for (int i = 0; i < understood.length; i++) {
                if (understood[i].equals(propertyToTest)) {
                    return true;
                }
            }
        }

        return false;
    }



    public static String fixURI(String str) {

        str = str.replace(java.io.File.separatorChar, '/');

        if (str.length() >= 4) {

            char ch0 = Character.toUpperCase(str.charAt(0));
            char ch1 = str.charAt(1);
            char ch2 = str.charAt(2);
            char ch3 = str.charAt(3);
            boolean isDosFilename = ((('A' <= ch0) && (ch0 <= 'Z'))
                && (ch1 == ':') && (ch2 == '/')
                && (ch3 != '/'));

            if (isDosFilename && log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Found DOS filename: " + str);
            }
        }

        if (str.length() >= 2) {
            char ch1 = str.charAt(1);

            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));

                if (('A' <= ch0) && (ch0 <= 'Z')) {
                    str = "/" + str;
                }
            }
        }

        return str;
    }
}
