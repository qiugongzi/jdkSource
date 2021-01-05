

package javax.security.auth.kerberos;

import java.io.File;
import java.security.AccessControlException;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;


public final class KeyTab {



    private final File file;

    private final KerberosPrincipal princ;

    private final boolean bound;

    static {
        KerberosSecrets.setJavaxSecurityAuthKerberosAccess(
                new JavaxSecurityAuthKerberosAccessImpl());
    }

    private KeyTab(KerberosPrincipal princ, File file, boolean bound) {
        this.princ = princ;
        this.file = file;
        this.bound = bound;
    }


    public static KeyTab getInstance(File file) {
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(null, file, true);
    }


    public static KeyTab getUnboundInstance(File file) {
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(null, file, false);
    }


    public static KeyTab getInstance(KerberosPrincipal princ, File file) {
        if (princ == null) {
            throw new NullPointerException("princ must be non null");
        }
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(princ, file, true);
    }


    public static KeyTab getInstance() {
        return new KeyTab(null, null, true);
    }


    public static KeyTab getUnboundInstance() {
        return new KeyTab(null, null, false);
    }


    public static KeyTab getInstance(KerberosPrincipal princ) {
        if (princ == null) {
            throw new NullPointerException("princ must be non null");
        }
        return new KeyTab(princ, null, true);
    }

    sun.security.krb5.internal.ktab.KeyTab takeSnapshot() {
        try {
            return sun.security.krb5.internal.ktab.KeyTab.getInstance(file);
        } catch (AccessControlException ace) {
            if (file != null) {
                throw ace;
            } else {
                AccessControlException ace2 = new AccessControlException(
                        "Access to default keytab denied (modified exception)");
                ace2.setStackTrace(ace.getStackTrace());
                throw ace2;
            }
        }
    }


    public KerberosKey[] getKeys(KerberosPrincipal principal) {
        try {
            if (princ != null && !principal.equals(princ)) {
                return new KerberosKey[0];
            }
            PrincipalName pn = new PrincipalName(principal.getName());
            EncryptionKey[] keys = takeSnapshot().readServiceKeys(pn);
            KerberosKey[] kks = new KerberosKey[keys.length];
            for (int i=0; i<kks.length; i++) {
                Integer tmp = keys[i].getKeyVersionNumber();
                kks[i] = new KerberosKey(
                        principal,
                        keys[i].getBytes(),
                        keys[i].getEType(),
                        tmp == null ? 0 : tmp.intValue());
                keys[i].destroy();
            }
            return kks;
        } catch (RealmException re) {
            return new KerberosKey[0];
        }
    }

    EncryptionKey[] getEncryptionKeys(PrincipalName principal) {
        return takeSnapshot().readServiceKeys(principal);
    }


    public boolean exists() {
        return !takeSnapshot().isMissing();
    }

    public String toString() {
        String s = (file == null) ? "Default keytab" : file.toString();
        if (!bound) return s;
        else if (princ == null) return s + " for someone";
        else return s + " for " + princ;
    }


    public int hashCode() {
        return Objects.hash(file, princ, bound);
    }


    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (! (other instanceof KeyTab)) {
            return false;
        }

        KeyTab otherKtab = (KeyTab) other;
        return Objects.equals(otherKtab.princ, princ) &&
                Objects.equals(otherKtab.file, file) &&
                bound == otherKtab.bound;
    }


    public KerberosPrincipal getPrincipal() {
        return princ;
    }


    public boolean isBound() {
        return bound;
    }
}
