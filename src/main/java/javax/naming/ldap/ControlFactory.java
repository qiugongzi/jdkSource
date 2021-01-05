

package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.Context;

import java.util.Hashtable;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;




public abstract class ControlFactory {

    protected ControlFactory() {
    }


    public abstract Control getControlInstance(Control ctl) throws NamingException;


    public static Control getControlInstance(Control ctl, Context ctx,
                                             Hashtable<?,?> env)
        throws NamingException {

        FactoryEnumeration factories = ResourceManager.getFactories(
            LdapContext.CONTROL_FACTORIES, env, ctx);

        if (factories == null) {
            return ctl;
        }

        Control answer = null;
        ControlFactory factory;
        while (answer == null && factories.hasMore()) {
            factory = (ControlFactory)factories.next();
            answer = factory.getControlInstance(ctl);
        }

        return (answer != null)? answer : ctl;
    }
}
