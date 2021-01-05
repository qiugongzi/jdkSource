

package javax.naming.ldap;


public interface Control extends java.io.Serializable {

    public static final boolean CRITICAL = true;


    public static final boolean NONCRITICAL = false;


    public String getID();


    public boolean isCritical();


    public byte[] getEncodedValue();

    }
