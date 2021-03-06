

package javax.security.auth.kerberos;

import java.io.*;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.util.*;



public final class KerberosPrincipal
    implements java.security.Principal, java.io.Serializable {

    private static final long serialVersionUID = -7374788026156829911L;

    public static final int KRB_NT_UNKNOWN =   0;



    public static final int KRB_NT_PRINCIPAL = 1;


    public static final int KRB_NT_SRV_INST =  2;



    public static final int KRB_NT_SRV_HST =   3;



    public static final int KRB_NT_SRV_XHST =  4;



    public static final int KRB_NT_UID = 5;

    private transient String fullName;

    private transient String realm;

    private transient int nameType;



    public KerberosPrincipal(String name) {
        this(name, KRB_NT_PRINCIPAL);
    }



    public KerberosPrincipal(String name, int nameType) {

        PrincipalName krb5Principal = null;

        try {
            krb5Principal  = new PrincipalName(name,nameType);
        } catch (KrbException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (krb5Principal.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                try {
                    sm.checkPermission(new ServicePermission(
                            "@" + krb5Principal.getRealmAsString(), "-"));
                } catch (SecurityException se) {
                    throw new SecurityException("Cannot read realm info");
                }
            }
        }
        this.nameType = nameType;
        fullName = krb5Principal.toString();
        realm = krb5Principal.getRealmString();
    }

    public String getRealm() {
        return realm;
    }


    public int hashCode() {
        return getName().hashCode();
    }


    public boolean equals(Object other) {

        if (other == this)
            return true;

        if (! (other instanceof KerberosPrincipal)) {
            return false;
        }
        String myFullName = getName();
        String otherFullName = ((KerberosPrincipal) other).getName();
        return myFullName.equals(otherFullName);
    }


    private void writeObject(ObjectOutputStream oos)
            throws IOException {

        PrincipalName krb5Principal;
        try {
            krb5Principal  = new PrincipalName(fullName, nameType);
            oos.writeObject(krb5Principal.asn1Encode());
            oos.writeObject(krb5Principal.getRealm().asn1Encode());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        byte[] asn1EncPrincipal = (byte [])ois.readObject();
        byte[] encRealm = (byte [])ois.readObject();
        try {
           Realm realmObject = new Realm(new DerValue(encRealm));
           PrincipalName krb5Principal = new PrincipalName(
                   new DerValue(asn1EncPrincipal), realmObject);
           realm = realmObject.toString();
           fullName = krb5Principal.toString();
           nameType = krb5Principal.getNameType();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    public String getName() {
        return fullName;
    }


    public int getNameType() {
        return nameType;
    }

    public String toString() {
        return getName();
    }
}
