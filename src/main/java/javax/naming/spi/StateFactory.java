
package javax.naming.spi;

import javax.naming.*;
import java.util.Hashtable;


public interface StateFactory {

    public Object getStateToBind(Object obj, Name name, Context nameCtx,
                                 Hashtable<?,?> environment)
        throws NamingException;
}
