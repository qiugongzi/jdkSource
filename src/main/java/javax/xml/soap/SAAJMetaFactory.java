

package javax.xml.soap;



public abstract class SAAJMetaFactory {
    static private final String META_FACTORY_CLASS_PROPERTY =
        "javax.xml.soap.MetaFactory";
    static final String DEFAULT_META_FACTORY_CLASS =
        "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";


    static SAAJMetaFactory getInstance() throws SOAPException {
            try {
                SAAJMetaFactory instance =
                    (SAAJMetaFactory) FactoryFinder.find(
                        META_FACTORY_CLASS_PROPERTY,
                        DEFAULT_META_FACTORY_CLASS);
                return instance;
            } catch (Exception e) {
                throw new SOAPException(
                    "Unable to create SAAJ meta-factory" + e.getMessage());
            }
    }

    protected SAAJMetaFactory() { }


    protected abstract MessageFactory newMessageFactory(String protocol)
        throws SOAPException;


    protected abstract SOAPFactory newSOAPFactory(String protocol)
        throws SOAPException;
}
