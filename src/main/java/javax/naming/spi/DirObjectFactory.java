

package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.Attributes;



public interface DirObjectFactory extends ObjectFactory {

    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?,?> environment,
                                    Attributes attrs)
        throws Exception;
}
