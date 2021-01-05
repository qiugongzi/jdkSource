


package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Principal;


public class JMXPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = -4184480100214577411L;


    private String name;


    public JMXPrincipal(String name) {
        validate(name);
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public String toString() {
        return("JMXPrincipal:  " + name);
    }


    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof JMXPrincipal))
            return false;
        JMXPrincipal that = (JMXPrincipal)o;

        return (this.getName().equals(that.getName()));
    }


    public int hashCode() {
        return name.hashCode();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gf = ois.readFields();
        String principalName = (String)gf.get("name", null);
        try {
            validate(principalName);
            this.name = principalName;
        } catch (NullPointerException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    private static void validate(String name) throws NullPointerException {
        if (name == null)
            throw new NullPointerException("illegal null input");
    }
}
