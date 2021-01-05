


package com.sun.org.apache.xml.internal.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;



public final class Encodings extends Object
{


    private static final int m_defaultLastPrintable = 0x7F;


    private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";


    private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";



    static Writer getWriter(OutputStream output, String encoding)
        throws UnsupportedEncodingException
    {

        final EncodingInfo ei = _encodingInfos.findEncoding(toUpperCaseFast(encoding));
        if (ei != null) {
            try {
                return new BufferedWriter(new OutputStreamWriter(
                        output, ei.javaName));
            } catch (UnsupportedEncodingException usee) {
                }
        }

        return new BufferedWriter(new OutputStreamWriter(output, encoding));
    }



    public static int getLastPrintable()
    {
        return m_defaultLastPrintable;
    }




    static EncodingInfo getEncodingInfo(String encoding)
    {
        EncodingInfo ei;

        String normalizedEncoding = toUpperCaseFast(encoding);
        ei = _encodingInfos.findEncoding(normalizedEncoding);
        if (ei == null) {
            try {
                final Charset c = Charset.forName(encoding);
                final String name = c.name();
                ei = new EncodingInfo(name, name);
                _encodingInfos.putEncoding(normalizedEncoding, ei);
            } catch (IllegalCharsetNameException | UnsupportedCharsetException x) {
                ei = new EncodingInfo(null,null);
            }
        }

        return ei;
    }


    static private String toUpperCaseFast(final String s) {

        boolean different = false;
        final int mx = s.length();
                char[] chars = new char[mx];
        for (int i=0; i < mx; i++) {
                char ch = s.charAt(i);
            if ('a' <= ch && ch <= 'z') {
                ch = (char) (ch + ('A' - 'a'));
                        different = true; }
                chars[i] = ch;
        }

        final String upper;
        if (different)
                upper = String.valueOf(chars);
        else
                upper = s;

        return upper;
    }


    static final String DEFAULT_MIME_ENCODING = "UTF-8";


    static String getMimeEncoding(String encoding)
    {

        if (null == encoding)
        {
            try
            {

                encoding = SecuritySupport.getSystemProperty("file.encoding", "UTF8");

                if (null != encoding)
                {


                    String jencoding =
                        (encoding.equalsIgnoreCase("Cp1252")
                            || encoding.equalsIgnoreCase("ISO8859_1")
                            || encoding.equalsIgnoreCase("8859_1")
                            || encoding.equalsIgnoreCase("UTF8"))
                            ? DEFAULT_MIME_ENCODING
                            : convertJava2MimeEncoding(encoding);

                    encoding =
                        (null != jencoding) ? jencoding : DEFAULT_MIME_ENCODING;
                }
                else
                {
                    encoding = DEFAULT_MIME_ENCODING;
                }
            }
            catch (SecurityException se)
            {
                encoding = DEFAULT_MIME_ENCODING;
            }
        }
        else
        {
            encoding = convertJava2MimeEncoding(encoding);
        }

        return encoding;
    }


    private static String convertJava2MimeEncoding(String encoding)
    {
        final EncodingInfo enc =
             _encodingInfos.getEncodingFromJavaKey(toUpperCaseFast(encoding));
        if (null != enc)
            return enc.name;
        return encoding;
    }


    public static String convertMime2JavaEncoding(String encoding)
    {
        final EncodingInfo info = _encodingInfos.findEncoding(toUpperCaseFast(encoding));
        return info != null ? info.javaName : encoding;
    }

    private final static class EncodingInfos {
        private final Map<String, EncodingInfo> _encodingTableKeyJava = new HashMap<>();
        private final Map<String, EncodingInfo> _encodingTableKeyMime = new HashMap<>();
        private final Map<String, EncodingInfo> _encodingDynamicTable =
                Collections.synchronizedMap(new HashMap<String, EncodingInfo>());

        private EncodingInfos() {
            loadEncodingInfo();
        }

        private InputStream openEncodingsFileStream() throws MalformedURLException, IOException {
            String urlString = null;
            InputStream is = null;

            try {
                urlString = SecuritySupport.getSystemProperty(ENCODINGS_PROP, "");
            } catch (SecurityException e) {
            }

            if (urlString != null && urlString.length() > 0) {
                URL url = new URL(urlString);
                is = url.openStream();
            }

            if (is == null) {
                is = SecuritySupport.getResourceAsStream(ENCODINGS_FILE);
            }
            return is;
        }

        private Properties loadProperties() throws MalformedURLException, IOException {
            Properties props = new Properties();
            try (InputStream is = openEncodingsFileStream()) {
                if (is != null) {
                    props.load(is);
                } else {
                    }
            }
            return props;
        }

        private String[] parseMimeTypes(String val) {
            int pos = val.indexOf(' ');
            if (pos < 0) {
                return new String[] { val };
                }
            StringTokenizer st =
                    new StringTokenizer(val.substring(0, pos), ",");
            String[] values = new String[st.countTokens()];
            for (int i=0; st.hasMoreTokens(); i++) {
                values[i] = st.nextToken();
            }
            return values;
        }

        private String findCharsetNameFor(String name) {
            try {
                return Charset.forName(name).name();
            } catch (Exception x) {
                return null;
            }
        }

        private String findCharsetNameFor(String javaName, String[] mimes) {
            String cs = findCharsetNameFor(javaName);
            if (cs != null) return javaName;
            for (String m : mimes) {
                cs = findCharsetNameFor(m);
                if (cs != null) break;
            }
            return cs;
        }


        private void loadEncodingInfo() {
            try {
                final Properties props = loadProperties();

                Enumeration keys = props.keys();
                Map<String, EncodingInfo> canonicals = new HashMap<>();
                while (keys.hasMoreElements()) {
                    final String javaName = (String) keys.nextElement();
                    final String[] mimes = parseMimeTypes(props.getProperty(javaName));

                    final String charsetName = findCharsetNameFor(javaName, mimes);
                    if (charsetName != null) {
                        final String kj = toUpperCaseFast(javaName);
                        final String kc = toUpperCaseFast(charsetName);
                        for (int i = 0; i < mimes.length; ++i) {
                            final String mimeName = mimes[i];
                            final String km = toUpperCaseFast(mimeName);
                            EncodingInfo info = new EncodingInfo(mimeName, charsetName);
                            _encodingTableKeyMime.put(km, info);
                            if (!canonicals.containsKey(kc)) {
                                canonicals.put(kc, info);
                                _encodingTableKeyJava.put(kc, info);
                            }
                            _encodingTableKeyJava.put(kj, info);
                        }
                    } else {
                        }
                }

                for (Entry<String, EncodingInfo> e : _encodingTableKeyJava.entrySet()) {
                    e.setValue(canonicals.get(toUpperCaseFast(e.getValue().javaName)));
                }

            } catch (java.net.MalformedURLException mue) {
                throw new com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException(mue);
            } catch (java.io.IOException ioe) {
                throw new com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException(ioe);
            }
        }

        EncodingInfo findEncoding(String normalizedEncoding) {
            EncodingInfo info = _encodingTableKeyJava.get(normalizedEncoding);
            if (info == null) {
                info = _encodingTableKeyMime.get(normalizedEncoding);
            }
            if (info == null) {
                info = _encodingDynamicTable.get(normalizedEncoding);
            }
            return info;
        }

        EncodingInfo getEncodingFromMimeKey(String normalizedMimeName) {
            return _encodingTableKeyMime.get(normalizedMimeName);
        }

        EncodingInfo getEncodingFromJavaKey(String normalizedJavaName) {
            return _encodingTableKeyJava.get(normalizedJavaName);
        }

        void putEncoding(String key, EncodingInfo info) {
            _encodingDynamicTable.put(key, info);
        }
    }


    static boolean isHighUTF16Surrogate(char ch) {
        return ('\uD800' <= ch && ch <= '\uDBFF');
    }

    static boolean isLowUTF16Surrogate(char ch) {
        return ('\uDC00' <= ch && ch <= '\uDFFF');
    }

    static int toCodePoint(char highSurrogate, char lowSurrogate) {
        int codePoint =
            ((highSurrogate - 0xd800) << 10)
                + (lowSurrogate - 0xdc00)
                + 0x10000;
        return codePoint;
    }

    static int toCodePoint(char ch) {
        int codePoint = ch;
        return codePoint;
    }

    private final static EncodingInfos _encodingInfos = new EncodingInfos();

}
