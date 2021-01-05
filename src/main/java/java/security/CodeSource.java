

package java.security;


import java.net.URL;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.*;
import sun.misc.IOUtils;



public class CodeSource implements java.io.Serializable {

    private static final long serialVersionUID = 4977541819976013951L;


    private URL location;


    private transient CodeSigner[] signers = null;


    private transient java.security.cert.Certificate certs[] = null;

    private transient SocketPermission sp;

    private transient CertificateFactory factory = null;


    public CodeSource(URL url, java.security.cert.Certificate certs[]) {
        this.location = url;

        if (certs != null) {
            this.certs = certs.clone();
        }
    }


    public CodeSource(URL url, CodeSigner[] signers) {
        this.location = url;

        if (signers != null) {
            this.signers = signers.clone();
        }
    }


    @Override
    public int hashCode() {
        if (location != null)
            return location.hashCode();
        else
            return 0;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof CodeSource))
            return false;

        CodeSource cs = (CodeSource) obj;

        if (location == null) {
            if (cs.location != null) return false;
        } else {
            if (!location.equals(cs.location)) return false;
        }

        return matchCerts(cs, true);
    }


    public final URL getLocation() {

        return this.location;
    }


    public final java.security.cert.Certificate[] getCertificates() {
        if (certs != null) {
            return certs.clone();

        } else if (signers != null) {
            ArrayList<java.security.cert.Certificate> certChains =
                        new ArrayList<>();
            for (int i = 0; i < signers.length; i++) {
                certChains.addAll(
                    signers[i].getSignerCertPath().getCertificates());
            }
            certs = certChains.toArray(
                        new java.security.cert.Certificate[certChains.size()]);
            return certs.clone();

        } else {
            return null;
        }
    }


    public final CodeSigner[] getCodeSigners() {
        if (signers != null) {
            return signers.clone();

        } else if (certs != null) {
            signers = convertCertArrayToSignerArray(certs);
            return signers.clone();

        } else {
            return null;
        }
    }



    public boolean implies(CodeSource codesource)
    {
        if (codesource == null)
            return false;

        return matchCerts(codesource, false) && matchLocation(codesource);
    }


    private boolean matchCerts(CodeSource that, boolean strict)
    {
        boolean match;

        if (certs == null && signers == null) {
            if (strict) {
                return (that.certs == null && that.signers == null);
            } else {
                return true;
            }
        } else if (signers != null && that.signers != null) {
            if (strict && signers.length != that.signers.length) {
                return false;
            }
            for (int i = 0; i < signers.length; i++) {
                match = false;
                for (int j = 0; j < that.signers.length; j++) {
                    if (signers[i].equals(that.signers[j])) {
                        match = true;
                        break;
                    }
                }
                if (!match) return false;
            }
            return true;

        } else if (certs != null && that.certs != null) {
            if (strict && certs.length != that.certs.length) {
                return false;
            }
            for (int i = 0; i < certs.length; i++) {
                match = false;
                for (int j = 0; j < that.certs.length; j++) {
                    if (certs[i].equals(that.certs[j])) {
                        match = true;
                        break;
                    }
                }
                if (!match) return false;
            }
            return true;
        }

        return false;
    }



    private boolean matchLocation(CodeSource that) {
        if (location == null)
            return true;

        if ((that == null) || (that.location == null))
            return false;

        if (location.equals(that.location))
            return true;

        if (!location.getProtocol().equalsIgnoreCase(that.location.getProtocol()))
            return false;

        int thisPort = location.getPort();
        if (thisPort != -1) {
            int thatPort = that.location.getPort();
            int port = thatPort != -1 ? thatPort
                                      : that.location.getDefaultPort();
            if (thisPort != port)
                return false;
        }

        if (location.getFile().endsWith("/-")) {
            String thisPath = location.getFile().substring(0,
                                            location.getFile().length()-1);
            if (!that.location.getFile().startsWith(thisPath))
                return false;
        } else if (location.getFile().endsWith("
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.location);

        if (this.certs != null && this.certs.length > 0) {
            for (int i = 0; i < this.certs.length; i++) {
                sb.append( " " + this.certs[i]);
            }

        } else if (this.signers != null && this.signers.length > 0) {
            for (int i = 0; i < this.signers.length; i++) {
                sb.append( " " + this.signers[i]);
            }
        } else {
            sb.append(" <no signer certificates>");
        }
        sb.append(")");
        return sb.toString();
    }


    private void writeObject(java.io.ObjectOutputStream oos)
        throws IOException
    {
        oos.defaultWriteObject(); if (certs == null || certs.length == 0) {
            oos.writeInt(0);
        } else {
            oos.writeInt(certs.length);
            for (int i = 0; i < certs.length; i++) {
                java.security.cert.Certificate cert = certs[i];
                try {
                    oos.writeUTF(cert.getType());
                    byte[] encoded = cert.getEncoded();
                    oos.writeInt(encoded.length);
                    oos.write(encoded);
                } catch (CertificateEncodingException cee) {
                    throw new IOException(cee.getMessage());
                }
            }
        }

        if (signers != null && signers.length > 0) {
            oos.writeObject(signers);
        }
    }


    private void readObject(java.io.ObjectInputStream ois)
        throws IOException, ClassNotFoundException
    {
        CertificateFactory cf;
        Hashtable<String, CertificateFactory> cfs = null;
        List<java.security.cert.Certificate> certList = null;

        ois.defaultReadObject(); int size = ois.readInt();
        if (size > 0) {
            cfs = new Hashtable<String, CertificateFactory>(3);
            certList = new ArrayList<>(size > 20 ? 20 : size);
        } else if (size < 0) {
            throw new IOException("size cannot be negative");
        }

        for (int i = 0; i < size; i++) {
            String certType = ois.readUTF();
            if (cfs.containsKey(certType)) {
                cf = cfs.get(certType);
            } else {
                try {
                    cf = CertificateFactory.getInstance(certType);
                } catch (CertificateException ce) {
                    throw new ClassNotFoundException
                        ("Certificate factory for " + certType + " not found");
                }
                cfs.put(certType, cf);
            }
            byte[] encoded = IOUtils.readNBytes(ois, ois.readInt());
            ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
            try {
                certList.add(cf.generateCertificate(bais));
            } catch (CertificateException ce) {
                throw new IOException(ce.getMessage());
            }
            bais.close();
        }

        if (certList != null) {
            this.certs = certList.toArray(
                    new java.security.cert.Certificate[size]);
        }
        try {
            this.signers = ((CodeSigner[])ois.readObject()).clone();
        } catch (IOException ioe) {
            }
    }


    private CodeSigner[] convertCertArrayToSignerArray(
        java.security.cert.Certificate[] certs) {

        if (certs == null) {
            return null;
        }

        try {
            if (factory == null) {
                factory = CertificateFactory.getInstance("X.509");
            }

            int i = 0;
            List<CodeSigner> signers = new ArrayList<>();
            while (i < certs.length) {
                List<java.security.cert.Certificate> certChain =
                        new ArrayList<>();
                certChain.add(certs[i++]); int j = i;

                while (j < certs.length &&
                    certs[j] instanceof X509Certificate &&
                    ((X509Certificate)certs[j]).getBasicConstraints() != -1) {
                    certChain.add(certs[j]);
                    j++;
                }
                i = j;
                CertPath certPath = factory.generateCertPath(certChain);
                signers.add(new CodeSigner(certPath, null));
            }

            if (signers.isEmpty()) {
                return null;
            } else {
                return signers.toArray(new CodeSigner[signers.size()]);
            }

        } catch (CertificateException e) {
            return null; }
    }
}
