

package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;


public abstract interface SaslServerFactory {

    public abstract SaslServer createSaslServer(
        String mechanism,
        String protocol,
        String serverName,
        Map<String,?> props,
        CallbackHandler cbh) throws SaslException;


    public abstract String[] getMechanismNames(Map<String,?> props);
}
