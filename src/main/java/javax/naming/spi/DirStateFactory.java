
package javax.naming.spi;

import javax.naming.*;
import javax.naming.directory.Attributes;
import java.util.Hashtable;


public interface DirStateFactory extends StateFactory {

    public Result getStateToBind(Object obj, Name name, Context nameCtx,
                                 Hashtable<?,?> environment,
                                 Attributes inAttrs)
        throws NamingException;



    public static class Result {

        private Object obj;



        private Attributes attrs;


        public Result(Object obj, Attributes outAttrs) {
            this.obj = obj;
            this.attrs = outAttrs;
        }


        public Object getObject() { return obj; };


        public Attributes getAttributes() { return attrs; };

    }
}
