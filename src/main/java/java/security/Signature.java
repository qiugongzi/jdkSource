

package java.security;

import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.nio.ByteBuffer;

import java.security.Provider.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import sun.security.util.Debug;
import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;



public abstract class Signature extends SignatureSpi {

    private static final Debug debug =
                        Debug.getInstance("jca", "Signature");

    private static final Debug pdebug =
                        Debug.getInstance("provider", "Provider");
    private static final boolean skipDebug =
        Debug.isOn("engine=") && !Debug.isOn("signature");


    private String algorithm;

    Provider provider;


    protected final static int UNINITIALIZED = 0;


    protected final static int SIGN = 2;


    protected final static int VERIFY = 3;


    protected int state = UNINITIALIZED;


    protected Signature(String algorithm) {
        this.algorithm = algorithm;
    }

    private final static String RSA_SIGNATURE = "NONEwithRSA";

    private final static String RSA_CIPHER = "RSA/ECB/PKCS1Padding";

    private final static List<ServiceId> rsaIds = Arrays.asList(
        new ServiceId[] {
            new ServiceId("Signature", "NONEwithRSA"),
            new ServiceId("Cipher", "RSA/ECB/PKCS1Padding"),
            new ServiceId("Cipher", "RSA/ECB"),
            new ServiceId("Cipher", "RSAnew ServiceId("Cipher", "RSA"),
        }
    );


    public static Signature getInstance(String algorithm)
            throws NoSuchAlgorithmException {
        List<Service> list;
        if (algorithm.equalsIgnoreCase(RSA_SIGNATURE)) {
            list = GetInstance.getServices(rsaIds);
        } else {
            list = GetInstance.getServices("Signature", algorithm);
        }
        Iterator<Service> t = list.iterator();
        if (t.hasNext() == false) {
            throw new NoSuchAlgorithmException
                (algorithm + " Signature not available");
        }
        NoSuchAlgorithmException failure;
        do {
            Service s = t.next();
            if (isSpi(s)) {
                return new Delegate(s, t, algorithm);
            } else {
                try {
                    Instance instance =
                        GetInstance.getInstance(s, SignatureSpi.class);
                    return getInstance(instance, algorithm);
                } catch (NoSuchAlgorithmException e) {
                    failure = e;
                }
            }
        } while (t.hasNext());
        throw failure;
    }

    private static Signature getInstance(Instance instance, String algorithm) {
        Signature sig;
        if (instance.impl instanceof Signature) {
            sig = (Signature)instance.impl;
            sig.algorithm = algorithm;
        } else {
            SignatureSpi spi = (SignatureSpi)instance.impl;
            sig = new Delegate(spi, algorithm);
        }
        sig.provider = instance.provider;
        return sig;
    }

    private final static Map<String,Boolean> signatureInfo;

    static {
        signatureInfo = new ConcurrentHashMap<String,Boolean>();
        Boolean TRUE = Boolean.TRUE;
        signatureInfo.put("sun.security.provider.DSA$RawDSA", TRUE);
        signatureInfo.put("sun.security.provider.DSA$SHA1withDSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$MD2withRSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$MD5withRSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$SHA1withRSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$SHA256withRSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$SHA384withRSA", TRUE);
        signatureInfo.put("sun.security.rsa.RSASignature$SHA512withRSA", TRUE);
        signatureInfo.put("com.sun.net.ssl.internal.ssl.RSASignature", TRUE);
        signatureInfo.put("sun.security.pkcs11.P11Signature", TRUE);
    }

    private static boolean isSpi(Service s) {
        if (s.getType().equals("Cipher")) {
            return true;
        }
        String className = s.getClassName();
        Boolean result = signatureInfo.get(className);
        if (result == null) {
            try {
                Object instance = s.newInstance(null);
                boolean r = (instance instanceof SignatureSpi)
                                && (instance instanceof Signature == false);
                if ((debug != null) && (r == false)) {
                    debug.println("Not a SignatureSpi " + className);
                    debug.println("Delayed provider selection may not be "
                        + "available for algorithm " + s.getAlgorithm());
                }
                result = Boolean.valueOf(r);
                signatureInfo.put(className, result);
            } catch (Exception e) {
                return false;
            }
        }
        return result.booleanValue();
    }


    public static Signature getInstance(String algorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (algorithm.equalsIgnoreCase(RSA_SIGNATURE)) {
            if ((provider == null) || (provider.length() == 0)) {
                throw new IllegalArgumentException("missing provider");
            }
            Provider p = Security.getProvider(provider);
            if (p == null) {
                throw new NoSuchProviderException
                    ("no such provider: " + provider);
            }
            return getInstanceRSA(p);
        }
        Instance instance = GetInstance.getInstance
                ("Signature", SignatureSpi.class, algorithm, provider);
        return getInstance(instance, algorithm);
    }


    public static Signature getInstance(String algorithm, Provider provider)
            throws NoSuchAlgorithmException {
        if (algorithm.equalsIgnoreCase(RSA_SIGNATURE)) {
            if (provider == null) {
                throw new IllegalArgumentException("missing provider");
            }
            return getInstanceRSA(provider);
        }
        Instance instance = GetInstance.getInstance
                ("Signature", SignatureSpi.class, algorithm, provider);
        return getInstance(instance, algorithm);
    }

    private static Signature getInstanceRSA(Provider p)
            throws NoSuchAlgorithmException {
        Service s = p.getService("Signature", RSA_SIGNATURE);
        if (s != null) {
            Instance instance = GetInstance.getInstance(s, SignatureSpi.class);
            return getInstance(instance, RSA_SIGNATURE);
        }
        try {
            Cipher c = Cipher.getInstance(RSA_CIPHER, p);
            return new Delegate(new CipherAdapter(c), RSA_SIGNATURE);
        } catch (GeneralSecurityException e) {
            throw new NoSuchAlgorithmException("no such algorithm: "
                + RSA_SIGNATURE + " for provider " + p.getName(), e);
        }
    }


    public final Provider getProvider() {
        chooseFirstProvider();
        return this.provider;
    }

    private String getProviderName() {
        return (provider == null)  ? "(no provider)" : provider.getName();
    }

    void chooseFirstProvider() {
        }


    public final void initVerify(PublicKey publicKey)
            throws InvalidKeyException {
        engineInitVerify(publicKey);
        state = VERIFY;

        if (!skipDebug && pdebug != null) {
            pdebug.println("Signature." + algorithm +
                " verification algorithm from: " + getProviderName());
        }
    }


    public final void initVerify(Certificate certificate)
            throws InvalidKeyException {
        if (certificate instanceof java.security.cert.X509Certificate) {
            X509Certificate cert = (X509Certificate)certificate;
            Set<String> critSet = cert.getCriticalExtensionOIDs();

            if (critSet != null && !critSet.isEmpty()
                && critSet.contains("2.5.29.15")) {
                boolean[] keyUsageInfo = cert.getKeyUsage();
                if ((keyUsageInfo != null) && (keyUsageInfo[0] == false))
                    throw new InvalidKeyException("Wrong key usage");
            }
        }

        PublicKey publicKey = certificate.getPublicKey();
        engineInitVerify(publicKey);
        state = VERIFY;

        if (!skipDebug && pdebug != null) {
            pdebug.println("Signature." + algorithm +
                " verification algorithm from: " + getProviderName());
        }
    }


    public final void initSign(PrivateKey privateKey)
            throws InvalidKeyException {
        engineInitSign(privateKey);
        state = SIGN;

        if (!skipDebug && pdebug != null) {
            pdebug.println("Signature." + algorithm +
                " signing algorithm from: " + getProviderName());
        }
    }


    public final void initSign(PrivateKey privateKey, SecureRandom random)
            throws InvalidKeyException {
        engineInitSign(privateKey, random);
        state = SIGN;

        if (!skipDebug && pdebug != null) {
            pdebug.println("Signature." + algorithm +
                " signing algorithm from: " + getProviderName());
        }
    }


    public final byte[] sign() throws SignatureException {
        if (state == SIGN) {
            return engineSign();
        }
        throw new SignatureException("object not initialized for " +
                                     "signing");
    }


    public final int sign(byte[] outbuf, int offset, int len)
        throws SignatureException {
        if (outbuf == null) {
            throw new IllegalArgumentException("No output buffer given");
        }
        if (offset < 0 || len < 0) {
            throw new IllegalArgumentException("offset or len is less than 0");
        }
        if (outbuf.length - offset < len) {
            throw new IllegalArgumentException
                ("Output buffer too small for specified offset and length");
        }
        if (state != SIGN) {
            throw new SignatureException("object not initialized for " +
                                         "signing");
        }
        return engineSign(outbuf, offset, len);
    }


    public final boolean verify(byte[] signature) throws SignatureException {
        if (state == VERIFY) {
            return engineVerify(signature);
        }
        throw new SignatureException("object not initialized for " +
                                     "verification");
    }


    public final boolean verify(byte[] signature, int offset, int length)
        throws SignatureException {
        if (state == VERIFY) {
            if (signature == null) {
                throw new IllegalArgumentException("signature is null");
            }
            if (offset < 0 || length < 0) {
                throw new IllegalArgumentException
                    ("offset or length is less than 0");
            }
            if (signature.length - offset < length) {
                throw new IllegalArgumentException
                    ("signature too small for specified offset and length");
            }

            return engineVerify(signature, offset, length);
        }
        throw new SignatureException("object not initialized for " +
                                     "verification");
    }


    public final void update(byte b) throws SignatureException {
        if (state == VERIFY || state == SIGN) {
            engineUpdate(b);
        } else {
            throw new SignatureException("object not initialized for "
                                         + "signature or verification");
        }
    }


    public final void update(byte[] data) throws SignatureException {
        update(data, 0, data.length);
    }


    public final void update(byte[] data, int off, int len)
            throws SignatureException {
        if (state == SIGN || state == VERIFY) {
            if (data == null) {
                throw new IllegalArgumentException("data is null");
            }
            if (off < 0 || len < 0) {
                throw new IllegalArgumentException("off or len is less than 0");
            }
            if (data.length - off < len) {
                throw new IllegalArgumentException
                    ("data too small for specified offset and length");
            }
            engineUpdate(data, off, len);
        } else {
            throw new SignatureException("object not initialized for "
                                         + "signature or verification");
        }
    }


    public final void update(ByteBuffer data) throws SignatureException {
        if ((state != SIGN) && (state != VERIFY)) {
            throw new SignatureException("object not initialized for "
                                         + "signature or verification");
        }
        if (data == null) {
            throw new NullPointerException();
        }
        engineUpdate(data);
    }


    public final String getAlgorithm() {
        return this.algorithm;
    }


    public String toString() {
        String initState = "";
        switch (state) {
        case UNINITIALIZED:
            initState = "<not initialized>";
            break;
        case VERIFY:
            initState = "<initialized for verifying>";
            break;
        case SIGN:
            initState = "<initialized for signing>";
            break;
        }
        return "Signature object: " + getAlgorithm() + initState;
    }


    @Deprecated
    public final void setParameter(String param, Object value)
            throws InvalidParameterException {
        engineSetParameter(param, value);
    }


    public final void setParameter(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
        engineSetParameter(params);
    }


    public final AlgorithmParameters getParameters() {
        return engineGetParameters();
    }


    @Deprecated
    public final Object getParameter(String param)
            throws InvalidParameterException {
        return engineGetParameter(param);
    }


    public Object clone() throws CloneNotSupportedException {
        if (this instanceof Cloneable) {
            return super.clone();
        } else {
            throw new CloneNotSupportedException();
        }
    }



    @SuppressWarnings("deprecation")
    private static class Delegate extends Signature {

        private SignatureSpi sigSpi;

        private final Object lock;

        private Service firstService;

        private Iterator<Service> serviceIterator;

        Delegate(SignatureSpi sigSpi, String algorithm) {
            super(algorithm);
            this.sigSpi = sigSpi;
            this.lock = null; }

        Delegate(Service service,
                        Iterator<Service> iterator, String algorithm) {
            super(algorithm);
            this.firstService = service;
            this.serviceIterator = iterator;
            this.lock = new Object();
        }


        public Object clone() throws CloneNotSupportedException {
            chooseFirstProvider();
            if (sigSpi instanceof Cloneable) {
                SignatureSpi sigSpiClone = (SignatureSpi)sigSpi.clone();
                Signature that =
                    new Delegate(sigSpiClone, ((Signature)this).algorithm);
                that.provider = ((Signature)this).provider;
                return that;
            } else {
                throw new CloneNotSupportedException();
            }
        }

        private static SignatureSpi newInstance(Service s)
                throws NoSuchAlgorithmException {
            if (s.getType().equals("Cipher")) {
                try {
                    Cipher c = Cipher.getInstance(RSA_CIPHER, s.getProvider());
                    return new CipherAdapter(c);
                } catch (NoSuchPaddingException e) {
                    throw new NoSuchAlgorithmException(e);
                }
            } else {
                Object o = s.newInstance(null);
                if (o instanceof SignatureSpi == false) {
                    throw new NoSuchAlgorithmException
                        ("Not a SignatureSpi: " + o.getClass().getName());
                }
                return (SignatureSpi)o;
            }
        }

        private static int warnCount = 10;


        void chooseFirstProvider() {
            if (sigSpi != null) {
                return;
            }
            synchronized (lock) {
                if (sigSpi != null) {
                    return;
                }
                if (debug != null) {
                    int w = --warnCount;
                    if (w >= 0) {
                        debug.println("Signature.init() not first method "
                            + "called, disabling delayed provider selection");
                        if (w == 0) {
                            debug.println("Further warnings of this type will "
                                + "be suppressed");
                        }
                        new Exception("Call trace").printStackTrace();
                    }
                }
                Exception lastException = null;
                while ((firstService != null) || serviceIterator.hasNext()) {
                    Service s;
                    if (firstService != null) {
                        s = firstService;
                        firstService = null;
                    } else {
                        s = serviceIterator.next();
                    }
                    if (isSpi(s) == false) {
                        continue;
                    }
                    try {
                        sigSpi = newInstance(s);
                        provider = s.getProvider();
                        firstService = null;
                        serviceIterator = null;
                        return;
                    } catch (NoSuchAlgorithmException e) {
                        lastException = e;
                    }
                }
                ProviderException e = new ProviderException
                        ("Could not construct SignatureSpi instance");
                if (lastException != null) {
                    e.initCause(lastException);
                }
                throw e;
            }
        }

        private void chooseProvider(int type, Key key, SecureRandom random)
                throws InvalidKeyException {
            synchronized (lock) {
                if (sigSpi != null) {
                    init(sigSpi, type, key, random);
                    return;
                }
                Exception lastException = null;
                while ((firstService != null) || serviceIterator.hasNext()) {
                    Service s;
                    if (firstService != null) {
                        s = firstService;
                        firstService = null;
                    } else {
                        s = serviceIterator.next();
                    }
                    if (s.supportsParameter(key) == false) {
                        continue;
                    }
                    if (isSpi(s) == false) {
                        continue;
                    }
                    try {
                        SignatureSpi spi = newInstance(s);
                        init(spi, type, key, random);
                        provider = s.getProvider();
                        sigSpi = spi;
                        firstService = null;
                        serviceIterator = null;
                        return;
                    } catch (Exception e) {
                        if (lastException == null) {
                            lastException = e;
                        }
                    }
                }
                if (lastException instanceof InvalidKeyException) {
                    throw (InvalidKeyException)lastException;
                }
                if (lastException instanceof RuntimeException) {
                    throw (RuntimeException)lastException;
                }
                String k = (key != null) ? key.getClass().getName() : "(null)";
                throw new InvalidKeyException
                    ("No installed provider supports this key: "
                    + k, lastException);
            }
        }

        private final static int I_PUB     = 1;
        private final static int I_PRIV    = 2;
        private final static int I_PRIV_SR = 3;

        private void init(SignatureSpi spi, int type, Key  key,
                SecureRandom random) throws InvalidKeyException {
            switch (type) {
            case I_PUB:
                spi.engineInitVerify((PublicKey)key);
                break;
            case I_PRIV:
                spi.engineInitSign((PrivateKey)key);
                break;
            case I_PRIV_SR:
                spi.engineInitSign((PrivateKey)key, random);
                break;
            default:
                throw new AssertionError("Internal error: " + type);
            }
        }

        protected void engineInitVerify(PublicKey publicKey)
                throws InvalidKeyException {
            if (sigSpi != null) {
                sigSpi.engineInitVerify(publicKey);
            } else {
                chooseProvider(I_PUB, publicKey, null);
            }
        }

        protected void engineInitSign(PrivateKey privateKey)
                throws InvalidKeyException {
            if (sigSpi != null) {
                sigSpi.engineInitSign(privateKey);
            } else {
                chooseProvider(I_PRIV, privateKey, null);
            }
        }

        protected void engineInitSign(PrivateKey privateKey, SecureRandom sr)
                throws InvalidKeyException {
            if (sigSpi != null) {
                sigSpi.engineInitSign(privateKey, sr);
            } else {
                chooseProvider(I_PRIV_SR, privateKey, sr);
            }
        }

        protected void engineUpdate(byte b) throws SignatureException {
            chooseFirstProvider();
            sigSpi.engineUpdate(b);
        }

        protected void engineUpdate(byte[] b, int off, int len)
                throws SignatureException {
            chooseFirstProvider();
            sigSpi.engineUpdate(b, off, len);
        }

        protected void engineUpdate(ByteBuffer data) {
            chooseFirstProvider();
            sigSpi.engineUpdate(data);
        }

        protected byte[] engineSign() throws SignatureException {
            chooseFirstProvider();
            return sigSpi.engineSign();
        }

        protected int engineSign(byte[] outbuf, int offset, int len)
                throws SignatureException {
            chooseFirstProvider();
            return sigSpi.engineSign(outbuf, offset, len);
        }

        protected boolean engineVerify(byte[] sigBytes)
                throws SignatureException {
            chooseFirstProvider();
            return sigSpi.engineVerify(sigBytes);
        }

        protected boolean engineVerify(byte[] sigBytes, int offset, int length)
                throws SignatureException {
            chooseFirstProvider();
            return sigSpi.engineVerify(sigBytes, offset, length);
        }

        protected void engineSetParameter(String param, Object value)
                throws InvalidParameterException {
            chooseFirstProvider();
            sigSpi.engineSetParameter(param, value);
        }

        protected void engineSetParameter(AlgorithmParameterSpec params)
                throws InvalidAlgorithmParameterException {
            chooseFirstProvider();
            sigSpi.engineSetParameter(params);
        }

        protected Object engineGetParameter(String param)
                throws InvalidParameterException {
            chooseFirstProvider();
            return sigSpi.engineGetParameter(param);
        }

        protected AlgorithmParameters engineGetParameters() {
            chooseFirstProvider();
            return sigSpi.engineGetParameters();
        }
    }

    @SuppressWarnings("deprecation")
    private static class CipherAdapter extends SignatureSpi {

        private final Cipher cipher;

        private ByteArrayOutputStream data;

        CipherAdapter(Cipher cipher) {
            this.cipher = cipher;
        }

        protected void engineInitVerify(PublicKey publicKey)
                throws InvalidKeyException {
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            if (data == null) {
                data = new ByteArrayOutputStream(128);
            } else {
                data.reset();
            }
        }

        protected void engineInitSign(PrivateKey privateKey)
                throws InvalidKeyException {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            data = null;
        }

        protected void engineInitSign(PrivateKey privateKey,
                SecureRandom random) throws InvalidKeyException {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, random);
            data = null;
        }

        protected void engineUpdate(byte b) throws SignatureException {
            engineUpdate(new byte[] {b}, 0, 1);
        }

        protected void engineUpdate(byte[] b, int off, int len)
                throws SignatureException {
            if (data != null) {
                data.write(b, off, len);
                return;
            }
            byte[] out = cipher.update(b, off, len);
            if ((out != null) && (out.length != 0)) {
                throw new SignatureException
                    ("Cipher unexpectedly returned data");
            }
        }

        protected byte[] engineSign() throws SignatureException {
            try {
                return cipher.doFinal();
            } catch (IllegalBlockSizeException e) {
                throw new SignatureException("doFinal() failed", e);
            } catch (BadPaddingException e) {
                throw new SignatureException("doFinal() failed", e);
            }
        }

        protected boolean engineVerify(byte[] sigBytes)
                throws SignatureException {
            try {
                byte[] out = cipher.doFinal(sigBytes);
                byte[] dataBytes = data.toByteArray();
                data.reset();
                return MessageDigest.isEqual(out, dataBytes);
            } catch (BadPaddingException e) {
                return false;
            } catch (IllegalBlockSizeException e) {
                throw new SignatureException("doFinal() failed", e);
            }
        }

        protected void engineSetParameter(String param, Object value)
                throws InvalidParameterException {
            throw new InvalidParameterException("Parameters not supported");
        }

        protected Object engineGetParameter(String param)
                throws InvalidParameterException {
            throw new InvalidParameterException("Parameters not supported");
        }

    }

}
