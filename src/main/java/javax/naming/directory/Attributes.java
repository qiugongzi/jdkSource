


package javax.naming.directory;

import java.util.Hashtable;
import java.util.Enumeration;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;



public interface Attributes extends Cloneable, java.io.Serializable {

    boolean isCaseIgnored();


    int size();


    Attribute get(String attrID);


    NamingEnumeration<? extends Attribute> getAll();


    NamingEnumeration<String> getIDs();


    Attribute put(String attrID, Object val);


    Attribute put(Attribute attr);


    Attribute remove(String attrID);


    Object clone();


    }
