



package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;


class WriterOutputBuffer implements OutputBuffer {
    private static final int KB = 1024;
    private static int BUFFER_SIZE = 4 * KB;

    static {
        final String osName = SecuritySupport.getSystemProperty("os.name");
        if (osName.equalsIgnoreCase("solaris")) {
            BUFFER_SIZE = 32 * KB;
        }
    }

    private Writer _writer;


    public WriterOutputBuffer(Writer writer) {
        _writer = new BufferedWriter(writer, BUFFER_SIZE);
    }

    public String close() {
        try {
            _writer.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return "";
    }

    public OutputBuffer append(String s) {
        try {
            _writer.write(s);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }

    public OutputBuffer append(char[] s, int from, int to) {
        try {
            _writer.write(s, from, to);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }

    public OutputBuffer append(char ch) {
        try {
            _writer.write(ch);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }
}
