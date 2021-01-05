

package java.security.cert;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;

import sun.misc.IOUtils;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.InvalidityDateExtension;


public class CertificateRevokedException extends CertificateException {

    private static final long serialVersionUID = 7839996631571608627L;


    private Date revocationDate;

    private final CRLReason reason;

    private final X500Principal authority;

    private transient Map<String, Extension> extensions;


    public CertificateRevokedException(Date revocationDate, CRLReason reason,
        X500Principal authority, Map<String, Extension> extensions) {
        if (revocationDate == null || reason == null || authority == null ||
            extensions == null) {
            throw new NullPointerException();
        }
        this.revocationDate = new Date(revocationDate.getTime());
        this.reason = reason;
        this.authority = authority;
        this.extensions = Collections.checkedMap(new HashMap<>(),
                                                 String.class, Extension.class);
        this.extensions.putAll(extensions);
    }


    public Date getRevocationDate() {
        return (Date) revocationDate.clone();
    }


    public CRLReason getRevocationReason() {
        return reason;
    }


    public X500Principal getAuthorityName() {
        return authority;
    }


    public Date getInvalidityDate() {
        Extension ext = getExtensions().get("2.5.29.24");
        if (ext == null) {
            return null;
        } else {
            try {
                Date invalidity = InvalidityDateExtension.toImpl(ext).get("DATE");
                return new Date(invalidity.getTime());
            } catch (IOException ioe) {
                return null;
            }
        }
    }


    public Map<String, Extension> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    @Override
    public String getMessage() {
        return "Certificate has been revoked, reason: "
               + reason + ", revocation date: " + revocationDate
               + ", authority: " + authority + ", extension OIDs: "
               + extensions.keySet();
    }


    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();

        oos.writeInt(extensions.size());

        for (Map.Entry<String, Extension> entry : extensions.entrySet()) {
            Extension ext = entry.getValue();
            oos.writeObject(ext.getId());
            oos.writeBoolean(ext.isCritical());
            byte[] extVal = ext.getValue();
            oos.writeInt(extVal.length);
            oos.write(extVal);
        }
    }


    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

        revocationDate = new Date(revocationDate.getTime());

        int size = ois.readInt();
        if (size == 0) {
            extensions = Collections.emptyMap();
        } else if (size < 0) {
            throw new IOException("size cannot be negative");
        } else {
            extensions = new HashMap<>(size > 20 ? 20 : size);
        }

        for (int i = 0; i < size; i++) {
            String oid = (String) ois.readObject();
            boolean critical = ois.readBoolean();
            byte[] extVal = IOUtils.readNBytes(ois, ois.readInt());
            Extension ext = sun.security.x509.Extension.newExtension
                (new ObjectIdentifier(oid), critical, extVal);
            extensions.put(oid, ext);
        }
    }
}
