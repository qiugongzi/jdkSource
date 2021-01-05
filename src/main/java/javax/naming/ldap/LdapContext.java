

package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.util.Hashtable;



public interface LdapContext extends DirContext {

    public ExtendedResponse extendedOperation(ExtendedRequest request)
        throws NamingException;


    public LdapContext newInstance(Control[] requestControls)
        throws NamingException;


    public void reconnect(Control[] connCtls) throws NamingException;


    public Control[] getConnectControls() throws NamingException;


    public void setRequestControls(Control[] requestControls)
        throws NamingException;


    public Control[] getRequestControls() throws NamingException;


    public Control[] getResponseControls() throws NamingException;


    static final String CONTROL_FACTORIES = "java.naming.factory.control";
}
