

package javax.security.auth.spi;

import javax.security.auth.Subject;
import javax.security.auth.AuthPermission;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import java.util.Map;


public interface LoginModule {


    void initialize(Subject subject, CallbackHandler callbackHandler,
                    Map<String,?> sharedState,
                    Map<String,?> options);


    boolean login() throws LoginException;


    boolean commit() throws LoginException;


    boolean abort() throws LoginException;


    boolean logout() throws LoginException;
}
