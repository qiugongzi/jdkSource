

package com.sun.org.apache.xml.internal.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecurityPermission;


public class JavaUtils {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(JavaUtils.class.getName());

    private static final SecurityPermission REGISTER_PERMISSION =
        new SecurityPermission(
            "com.sun.org.apache.xml.internal.security.register");

    private JavaUtils() {
        }


    public static byte[] getBytesFromFile(String fileName)
        throws FileNotFoundException, IOException {

        byte refBytes[] = null;

        FileInputStream fisRef = null;
        UnsyncByteArrayOutputStream baos = null;
        try {
            fisRef = new FileInputStream(fileName);
            baos = new UnsyncByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int len;

            while ((len = fisRef.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }

            refBytes = baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
            if (fisRef != null) {
                fisRef.close();
            }
        }

        return refBytes;
    }


    public static void writeBytesToFilename(String filename, byte[] bytes) {
        FileOutputStream fos = null;
        try {
            if (filename != null && bytes != null) {
                File f = new File(filename);

                fos = new FileOutputStream(f);

                fos.write(bytes);
                fos.close();
            } else {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "writeBytesToFilename got null byte[] pointed");
                }
            }
        } catch (IOException ex) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    if (log.isLoggable(java.util.logging.Level.FINE)) {
                        log.log(java.util.logging.Level.FINE, ioe.getMessage(), ioe);
                    }
                }
            }
        }
    }


    public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        UnsyncByteArrayOutputStream baos = null;

        byte[] retBytes = null;
        try {
            baos = new UnsyncByteArrayOutputStream();
            byte buf[] = new byte[4 * 1024];
            int len;

            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            retBytes = baos.toByteArray();
        } finally {
            baos.close();
        }

        return retBytes;
    }


    public static byte[] convertDsaASN1toXMLDSIG(byte[] asn1Bytes, int size)
        throws IOException
    {
        if (asn1Bytes[0] != 48 || asn1Bytes[1] != asn1Bytes.length - 2
            || asn1Bytes[2] != 2) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }

        byte rLength = asn1Bytes[3];
        int i;
        for (i = rLength; i > 0 && asn1Bytes[4 + rLength - i] == 0; i--);

        byte sLength = asn1Bytes[5 + rLength];
        int j;
        for (j = sLength;
             j > 0 && asn1Bytes[6 + rLength + sLength - j] == 0; j--);

        if (i > size || asn1Bytes[4 + rLength] != 2 || j > size) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        } else {
            byte[] xmldsigBytes = new byte[size * 2];
            System.arraycopy(asn1Bytes, 4 + rLength - i, xmldsigBytes,
                             size - i, i);
            System.arraycopy(asn1Bytes, 6 + rLength + sLength - j,
                             xmldsigBytes, size * 2 - j, j);
            return xmldsigBytes;
        }
    }


    public static byte[] convertDsaXMLDSIGtoASN1(byte[] xmldsigBytes, int size)
        throws IOException
    {
        int totalSize = size * 2;
        if (xmldsigBytes.length != totalSize) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }

        int i;
        for (i = size; i > 0 && xmldsigBytes[size - i] == 0; i--);

        int j = i;
        if (xmldsigBytes[size - i] < 0) {
            j++;
        }

        int k;
        for (k = size; k > 0 && xmldsigBytes[totalSize - k] == 0; k--);

        int l = k;
        if (xmldsigBytes[totalSize - k] < 0) {
            l++;
        }

        byte[] asn1Bytes = new byte[6 + j + l];
        asn1Bytes[0] = 48;
        asn1Bytes[1] = (byte)(4 + j + l);
        asn1Bytes[2] = 2;
        asn1Bytes[3] = (byte)j;
        System.arraycopy(xmldsigBytes, size - i, asn1Bytes, 4 + j - i, i);

        asn1Bytes[4 + j] = 2;
        asn1Bytes[5 + j] = (byte) l;
        System.arraycopy(xmldsigBytes, totalSize - k, asn1Bytes,
                         6 + j + l - k, k);

        return asn1Bytes;
    }


    public static void checkRegisterPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(REGISTER_PERMISSION);
        }
    }
}
