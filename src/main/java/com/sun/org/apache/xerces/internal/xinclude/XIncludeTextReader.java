

package com.sun.org.apache.xerces.internal.xinclude;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;


public class XIncludeTextReader {

    private Reader fReader;
    private XIncludeHandler fHandler;
    private XMLInputSource fSource;
    private XMLErrorReporter fErrorReporter;
    private XMLString fTempString = new XMLString();


    public XIncludeTextReader(XMLInputSource source, XIncludeHandler handler, int bufferSize)
        throws IOException {
        fHandler = handler;
        fSource = source;
        fTempString = new XMLString(new char[bufferSize + 1], 0, 0);
    }


    public void setErrorReporter(XMLErrorReporter errorReporter) {
        fErrorReporter = errorReporter;
    }


    protected Reader getReader(XMLInputSource source) throws IOException {
        if (source.getCharacterStream() != null) {
            return source.getCharacterStream();
        }
        else {
            InputStream stream = null;

            String encoding = source.getEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            if (source.getByteStream() != null) {
                stream = source.getByteStream();
                if (!(stream instanceof BufferedInputStream)) {
                    stream = new BufferedInputStream(stream, fTempString.ch.length);
                }
            }
            else {
                String expandedSystemId = XMLEntityManager.expandSystemId(source.getSystemId(), source.getBaseSystemId(), false);

                URL url = new URL(expandedSystemId);
                URLConnection urlCon = url.openConnection();

                if (urlCon instanceof HttpURLConnection && source instanceof HTTPInputSource) {
                    final HttpURLConnection urlConnection = (HttpURLConnection) urlCon;
                    final HTTPInputSource httpInputSource = (HTTPInputSource) source;

                    Iterator propIter = httpInputSource.getHTTPRequestProperties();
                    while (propIter.hasNext()) {
                        Map.Entry entry = (Map.Entry) propIter.next();
                        urlConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }

                    boolean followRedirects = httpInputSource.getFollowHTTPRedirects();
                    if (!followRedirects) {
                        XMLEntityManager.setInstanceFollowRedirects(urlConnection, followRedirects);
                    }
                }

                stream = new BufferedInputStream(urlCon.getInputStream());

                String rawContentType = urlCon.getContentType();

                int index = (rawContentType != null) ? rawContentType.indexOf(';') : -1;

                String contentType = null;
                String charset = null;
                if (index != -1) {
                    contentType = rawContentType.substring(0, index).trim();

                    charset = rawContentType.substring(index + 1).trim();
                    if (charset.startsWith("charset=")) {
                        charset = charset.substring(8).trim();
                        if ((charset.charAt(0) == '"'
                            && charset.charAt(charset.length() - 1) == '"')
                            || (charset.charAt(0) == '\''
                                && charset.charAt(charset.length() - 1)
                                    == '\'')) {
                            charset =
                                charset.substring(1, charset.length() - 1);
                        }
                    }
                    else {
                        charset = null;
                    }
                }
                else {
                    contentType = rawContentType.trim();
                }

                String detectedEncoding = null;

                if (contentType.equals("text/xml")) {
                    if (charset != null) {
                        detectedEncoding = charset;
                    }
                    else {
                        detectedEncoding = "US-ASCII";
                    }
                }
                else if (contentType.equals("application/xml")) {
                    if (charset != null) {
                        detectedEncoding = charset;
                    }
                    else {
                        detectedEncoding = getEncodingName(stream);
                    }
                }
                else if (contentType.endsWith("+xml")) {
                    detectedEncoding = getEncodingName(stream);
                }

                if (detectedEncoding != null) {
                    encoding = detectedEncoding;
                }
                }

            encoding = encoding.toUpperCase(Locale.ENGLISH);

            encoding = consumeBOM(stream, encoding);

            if (encoding.equals("UTF-8")) {
                return new UTF8Reader(stream,
                    fTempString.ch.length,
                    fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN),
                    fErrorReporter.getLocale() );
            }

            String javaEncoding = EncodingMap.getIANA2JavaMapping(encoding);

            if (javaEncoding == null) {
                MessageFormatter aFormatter =
                    fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN);
                Locale aLocale = fErrorReporter.getLocale();
                throw new IOException( aFormatter.formatMessage( aLocale,
                    "EncodingDeclInvalid",
                    new Object[] {encoding} ) );
            }
            else if (javaEncoding.equals("ASCII")) {
                return new ASCIIReader(stream,
                    fTempString.ch.length,
                    fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN),
                    fErrorReporter.getLocale() );
            }

            return new InputStreamReader(stream, javaEncoding);
        }
    }


    protected String getEncodingName(InputStream stream) throws IOException {
        final byte[] b4 = new byte[4];
        String encoding = null;

        stream.mark(4);
        int count = stream.read(b4, 0, 4);
        stream.reset();
        if (count == 4) {
            encoding = getEncodingName(b4);
        }

        return encoding;
    }


    protected String consumeBOM(InputStream stream, String encoding)
        throws IOException {

        byte[] b = new byte[3];
        int count = 0;
        stream.mark(3);
        if (encoding.equals("UTF-8")) {
            count = stream.read(b, 0, 3);
            if (count == 3) {
                final int b0 = b[0] & 0xFF;
                final int b1 = b[1] & 0xFF;
                final int b2 = b[2] & 0xFF;
                if (b0 != 0xEF || b1 != 0xBB || b2 != 0xBF) {
                    stream.reset();
                }
            }
            else {
                stream.reset();
            }
        }
        else if (encoding.startsWith("UTF-16")) {
            count = stream.read(b, 0, 2);
            if (count == 2) {
                final int b0 = b[0] & 0xFF;
                final int b1 = b[1] & 0xFF;
                if (b0 == 0xFE && b1 == 0xFF) {
                    return "UTF-16BE";
                }
                else if (b0 == 0xFF && b1 == 0xFE) {
                    return "UTF-16LE";
                }
            }
            stream.reset();
        }
        return encoding;
    }


    protected String getEncodingName(byte[] b4) {

        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 0xFE && b1 == 0xFF) {
            return "UTF-16BE";
        }
        if (b0 == 0xFF && b1 == 0xFE) {
            return "UTF-16LE";
        }

        int b2 = b4[2] & 0xFF;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
            return "UTF-8";
        }

        int b3 = b4[3] & 0xFF;
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x00 && b3 == 0x3C) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x00 && b3 == 0x00) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x3C && b3 == 0x00) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x00) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
            return "UTF-16BE";
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
            return "UTF-16LE";
        }
        if (b0 == 0x4C && b1 == 0x6F && b2 == 0xA7 && b3 == 0x94) {
            return "CP037";
        }

        return null;

    } public void parse() throws IOException {

        fReader = getReader(fSource);
        fSource = null;
        int readSize = fReader.read(fTempString.ch, 0, fTempString.ch.length - 1);
        while (readSize != -1) {
            for (int i = 0; i < readSize; ++i) {
                char ch = fTempString.ch[i];
                if (!isValid(ch)) {
                    if (XMLChar.isHighSurrogate(ch)) {
                        int ch2;
                        if (++i < readSize) {
                            ch2 = fTempString.ch[i];
                        }
                        else {
                            ch2 = fReader.read();
                            if (ch2 != -1) {
                                fTempString.ch[readSize++] = (char) ch2;
                            }
                        }
                        if (XMLChar.isLowSurrogate(ch2)) {
                            int sup = XMLChar.supplemental(ch, (char)ch2);
                            if (!isValid(sup)) {
                                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                           "InvalidCharInContent",
                                                           new Object[] { Integer.toString(sup, 16) },
                                                           XMLErrorReporter.SEVERITY_FATAL_ERROR);
                            }
                        }
                        else {
                            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                       "InvalidCharInContent",
                                                       new Object[] { Integer.toString(ch2, 16) },
                                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
                        }
                    }
                    else {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "InvalidCharInContent",
                                                   new Object[] { Integer.toString(ch, 16) },
                                                   XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
            if (fHandler != null && readSize > 0) {
                fTempString.offset = 0;
                fTempString.length = readSize;
                fHandler.characters(
                    fTempString,
                    fHandler.modifyAugmentations(null, true));
            }
            readSize = fReader.read(fTempString.ch, 0, fTempString.ch.length - 1);
        }

    }


    public void setInputSource(XMLInputSource source) {
        fSource = source;
    }


    public void close() throws IOException {
        if (fReader != null) {
            fReader.close();
            fReader = null;
        }
    }


    protected boolean isValid(int ch) {
        return XMLChar.isValid(ch);
    }


    protected void setBufferSize(int bufferSize) {
        if (fTempString.ch.length != ++bufferSize) {
            fTempString.ch = new char[bufferSize];
        }
    }

}
