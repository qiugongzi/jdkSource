

package javax.naming.ldap;

import javax.naming.ReferralException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Hashtable;



public abstract class LdapReferralException extends ReferralException {

    protected LdapReferralException(String explanation) {
        super(explanation);
    }


    protected LdapReferralException() {
        super();
    }


    public abstract Context getReferralContext() throws NamingException;


    public abstract Context
        getReferralContext(Hashtable<?,?> env)
        throws NamingException;


    public abstract Context
        getReferralContext(Hashtable<?,?> env,
                           Control[] reqCtls)
        throws NamingException;

    private static final long serialVersionUID = -1668992791764950804L;
}
