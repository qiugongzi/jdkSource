

package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;


public abstract class TransformService implements Transform {

    private String algorithm;
    private String mechanism;
    private Provider provider;


    protected TransformService() {}


    public static TransformService getInstance
        (String algorithm, String mechanismType)
        throws NoSuchAlgorithmException {
        if (mechanismType == null || algorithm == null) {
            throw new NullPointerException();
        }
        boolean dom = false;
        if (mechanismType.equals("DOM")) {
            dom = true;
        }
        List<Service> services = GetInstance.getServices("TransformService", algorithm);
        for (Iterator<Service> t = services.iterator(); t.hasNext(); ) {
            Service s = t.next();
            String value = s.getAttribute("MechanismType");
            if ((value == null && dom) ||
                (value != null && value.equals(mechanismType))) {
                Instance instance = GetInstance.getInstance(s, null);
                TransformService ts = (TransformService) instance.impl;
                ts.algorithm = algorithm;
                ts.mechanism = mechanismType;
                ts.provider = instance.provider;
                return ts;
            }
        }
        throw new NoSuchAlgorithmException
            (algorithm + " algorithm and " + mechanismType
                 + " mechanism not available");
    }


    public static TransformService getInstance
        (String algorithm, String mechanismType, Provider provider)
        throws NoSuchAlgorithmException {
        if (mechanismType == null || algorithm == null || provider == null) {
            throw new NullPointerException();
        }

        boolean dom = false;
        if (mechanismType.equals("DOM")) {
            dom = true;
        }
        Service s = GetInstance.getService
            ("TransformService", algorithm, provider);
        String value = s.getAttribute("MechanismType");
        if ((value == null && dom) ||
            (value != null && value.equals(mechanismType))) {
            Instance instance = GetInstance.getInstance(s, null);
            TransformService ts = (TransformService) instance.impl;
            ts.algorithm = algorithm;
            ts.mechanism = mechanismType;
            ts.provider = instance.provider;
            return ts;
        }
        throw new NoSuchAlgorithmException
            (algorithm + " algorithm and " + mechanismType
                 + " mechanism not available");
    }


    public static TransformService getInstance
        (String algorithm, String mechanismType, String provider)
        throws NoSuchAlgorithmException, NoSuchProviderException {
        if (mechanismType == null || algorithm == null || provider == null) {
            throw new NullPointerException();
        } else if (provider.length() == 0) {
            throw new NoSuchProviderException();
        }
        boolean dom = false;
        if (mechanismType.equals("DOM")) {
            dom = true;
        }
        Service s = GetInstance.getService
            ("TransformService", algorithm, provider);
        String value = s.getAttribute("MechanismType");
        if ((value == null && dom) ||
            (value != null && value.equals(mechanismType))) {
            Instance instance = GetInstance.getInstance(s, null);
            TransformService ts = (TransformService) instance.impl;
            ts.algorithm = algorithm;
            ts.mechanism = mechanismType;
            ts.provider = instance.provider;
            return ts;
        }
        throw new NoSuchAlgorithmException
            (algorithm + " algorithm and " + mechanismType
                 + " mechanism not available");
    }

    private static class MechanismMapEntry implements Map.Entry<String,String> {
        private final String mechanism;
        private final String algorithm;
        private final String key;
        MechanismMapEntry(String algorithm, String mechanism) {
            this.algorithm = algorithm;
            this.mechanism = mechanism;
            this.key = "TransformService." + algorithm + " MechanismType";
        }
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            return (getKey()==null ?
                    e.getKey()==null : getKey().equals(e.getKey())) &&
                   (getValue()==null ?
                    e.getValue()==null : getValue().equals(e.getValue()));
        }
        public String getKey() {
            return key;
        }
        public String getValue() {
            return mechanism;
        }
        public String setValue(String value) {
            throw new UnsupportedOperationException();
        }
        public int hashCode() {
            return (getKey()==null ? 0 : getKey().hashCode()) ^
                   (getValue()==null ? 0 : getValue().hashCode());
        }
    }


    public final String getMechanismType() {
        return mechanism;
    }


    public final String getAlgorithm() {
        return algorithm;
    }


    public final Provider getProvider() {
        return provider;
    }


    public abstract void init(TransformParameterSpec params)
        throws InvalidAlgorithmParameterException;


    public abstract void marshalParams
        (XMLStructure parent, XMLCryptoContext context)
        throws MarshalException;


    public abstract void init(XMLStructure parent, XMLCryptoContext context)
        throws InvalidAlgorithmParameterException;
}
