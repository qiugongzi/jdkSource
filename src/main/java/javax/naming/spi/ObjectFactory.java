

package javax.naming.spi;

import java.util.Hashtable;

import javax.naming.*;



public interface ObjectFactory {

    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?,?> environment)
        throws Exception;
}
