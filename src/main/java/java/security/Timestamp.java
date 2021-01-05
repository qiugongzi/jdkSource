

package java.security;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertPath;
import java.security.cert.X509Extension;
import java.util.Date;
import java.util.List;



public final class Timestamp implements Serializable {

    private static final long serialVersionUID = -5502683707821851294L;


    private Date timestamp;


    private CertPath signerCertPath;


    private transient int myhash = -1;


    public Timestamp(Date timestamp, CertPath signerCertPath) {
        if (timestamp == null || signerCertPath == null) {
            throw new NullPointerException();
        }
        this.timestamp = new Date(timestamp.getTime()); this.signerCertPath = signerCertPath;
    }


    public Date getTimestamp() {
        return new Date(timestamp.getTime()); }


    public CertPath getSignerCertPath() {
        return signerCertPath;
    }


    public int hashCode() {
        if (myhash == -1) {
            myhash = timestamp.hashCode() + signerCertPath.hashCode();
        }
        return myhash;
    }


    public boolean equals(Object obj) {
        if (obj == null || (!(obj instanceof Timestamp))) {
            return false;
        }
        Timestamp that = (Timestamp)obj;

        if (this == that) {
            return true;
        }
        return (timestamp.equals(that.getTimestamp()) &&
            signerCertPath.equals(that.getSignerCertPath()));
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append("timestamp: " + timestamp);
        List<? extends Certificate> certs = signerCertPath.getCertificates();
        if (!certs.isEmpty()) {
            sb.append("TSA: " + certs.get(0));
        } else {
            sb.append("TSA: <empty>");
        }
        sb.append(")");
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        myhash = -1;
        timestamp = new Date(timestamp.getTime());
    }
}
