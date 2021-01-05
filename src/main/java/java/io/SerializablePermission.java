

package java.io;

import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;





public final class SerializablePermission extends BasicPermission {

    private static final long serialVersionUID = 8537212141160296410L;


    private String actions;


    public SerializablePermission(String name)
    {
        super(name);
    }



    public SerializablePermission(String name, String actions)
    {
        super(name, actions);
    }
}
