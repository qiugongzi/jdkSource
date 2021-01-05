

package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;


public abstract interface SaslClientFactory {

    public abstract SaslClient createSaslClient(
        String[] mechanisms,
        String authorizationId,
        String protocol,
        String serverName,
        Map<String,?> props,
        CallbackHandler cbh) throws SaslException;


    public abstract String[] getMechanismNames(Map<String,?> props);
}
