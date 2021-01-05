

package javax.naming.ldap;

import javax.naming.NamingException;


public interface ExtendedRequest extends java.io.Serializable {


    public String getID();


    public byte[] getEncodedValue();


    public ExtendedResponse createExtendedResponse(String id,
                byte[] berValue, int offset, int length) throws NamingException;

    }
