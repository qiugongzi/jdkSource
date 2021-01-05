

package javax.naming.directory;

import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;


public interface Attribute extends Cloneable, java.io.Serializable {

    NamingEnumeration<?> getAll() throws NamingException;


    Object get() throws NamingException;


    int size();


    String getID();


    boolean contains(Object attrVal);

    boolean add(Object attrVal);


    boolean remove(Object attrval);


    void clear();



    DirContext getAttributeSyntaxDefinition() throws NamingException;


    DirContext getAttributeDefinition() throws NamingException;


    Object clone();

    boolean isOrdered();


    Object get(int ix) throws NamingException;


    Object remove(int ix);


    void add(int ix, Object attrVal);



    Object set(int ix, Object attrVal);


    static final long serialVersionUID = 8707690322213556804L;
}
