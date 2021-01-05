

package javax.xml.crypto;


public interface XMLCryptoContext {


    String getBaseURI();


    void setBaseURI(String baseURI);


    KeySelector getKeySelector();


    void setKeySelector(KeySelector ks);


    URIDereferencer getURIDereferencer();


    void setURIDereferencer(URIDereferencer dereferencer);


    String getNamespacePrefix(String namespaceURI, String defaultPrefix);


    String putNamespacePrefix(String namespaceURI, String prefix);


    String getDefaultNamespacePrefix();


    void setDefaultNamespacePrefix(String defaultPrefix);


    Object setProperty(String name, Object value);


    Object getProperty(String name);


    Object get(Object key);


    Object put(Object key, Object value);
}
