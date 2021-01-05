

package javax.naming;

import java.util.Enumeration;


public interface NamingEnumeration<T> extends Enumeration<T> {

    public T next() throws NamingException;


    public boolean hasMore() throws NamingException;


    public void close() throws NamingException;
}
