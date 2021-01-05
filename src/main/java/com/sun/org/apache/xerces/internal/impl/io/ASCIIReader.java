


package com.sun.org.apache.xerces.internal.impl.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;

import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;


public class ASCIIReader
    extends Reader {

    public static final int DEFAULT_BUFFER_SIZE = 2048;

    protected InputStream fInputStream;


    protected byte[] fBuffer;

    private MessageFormatter fFormatter = null;

    private Locale fLocale = null;

    public ASCIIReader(InputStream inputStream, MessageFormatter messageFormatter,
            Locale locale) {
        this(inputStream, DEFAULT_BUFFER_SIZE, messageFormatter, locale);
    } public ASCIIReader(InputStream inputStream, int size,
            MessageFormatter messageFormatter, Locale locale) {
        fInputStream = inputStream;
        BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
        fBuffer = ba.getByteBuffer(size);
        if (fBuffer == null) {
            fBuffer = new byte[size];
        }
        fFormatter = messageFormatter;
        fLocale = locale;
    } public int read() throws IOException {
        int b0 = fInputStream.read();
        if (b0 >= 0x80) {
            throw new MalformedByteSequenceException(fFormatter,
                fLocale, XMLMessageFormatter.XML_DOMAIN,
                "InvalidASCII", new Object [] {Integer.toString(b0)});
        }
        return b0;
    } public int read(char ch[], int offset, int length) throws IOException {
        if (length > fBuffer.length) {
            length = fBuffer.length;
        }
        int count = fInputStream.read(fBuffer, 0, length);
        for (int i = 0; i < count; i++) {
            int b0 = fBuffer[i];
            if (b0 < 0) {
                throw new MalformedByteSequenceException(fFormatter,
                    fLocale, XMLMessageFormatter.XML_DOMAIN,
                    "InvalidASCII", new Object [] {Integer.toString(b0 & 0x0FF)});
            }
            ch[offset + i] = (char)b0;
        }
        return count;
    } public long skip(long n) throws IOException {
        return fInputStream.skip(n);
    } public boolean ready() throws IOException {
            return false;
    } public boolean markSupported() {
            return fInputStream.markSupported();
    } public void mark(int readAheadLimit) throws IOException {
            fInputStream.mark(readAheadLimit);
    } public void reset() throws IOException {
        fInputStream.reset();
    } public void close() throws IOException {
         BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
         ba.returnByteBuffer(fBuffer);
         fBuffer = null;
         fInputStream.close();
     } }