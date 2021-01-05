


package com.sun.org.apache.xml.internal.serialize;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.util.EncodingMap;


public class EncodingInfo {

    private Object [] fArgsForMethod = null;

    String ianaName;
    String javaName;
    int lastPrintable;

    Object fCharsetEncoder = null;

    Object fCharToByteConverter = null;

    boolean fHaveTriedCToB = false;

    boolean fHaveTriedCharsetEncoder = false;


    public EncodingInfo(String ianaName, String javaName, int lastPrintable) {
        this.ianaName = ianaName;
        this.javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        this.lastPrintable = lastPrintable;
    }


    public String getIANAName() {
        return this.ianaName;
    }


    public Writer getWriter(OutputStream output)
        throws UnsupportedEncodingException {
        if (javaName != null)
            return new OutputStreamWriter(output, javaName);
        javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        if(javaName == null)
            return new OutputStreamWriter(output, "UTF8");
        return new OutputStreamWriter(output, javaName);
    }


    public boolean isPrintable(char ch) {
        if (ch <= this.lastPrintable) {
            return true;
        }
        return isPrintable0(ch);
    }


    private boolean isPrintable0(char ch) {

        if (fCharsetEncoder == null && CharsetMethods.fgNIOCharsetAvailable && !fHaveTriedCharsetEncoder) {
            if (fArgsForMethod == null) {
                fArgsForMethod = new Object [1];
            }
            try {
                fArgsForMethod[0] = javaName;
                Object charset = CharsetMethods.fgCharsetForNameMethod.invoke(null, fArgsForMethod);
                if (((Boolean) CharsetMethods.fgCharsetCanEncodeMethod.invoke(charset, (Object[]) null)).booleanValue()) {
                    fCharsetEncoder = CharsetMethods.fgCharsetNewEncoderMethod.invoke(charset, (Object[]) null);
                }
                else {
                    fHaveTriedCharsetEncoder = true;
                }
            }
            catch (Exception e) {
                fHaveTriedCharsetEncoder = true;
            }
        }
        if (fCharsetEncoder != null) {
            try {
                fArgsForMethod[0] = new Character(ch);
                return ((Boolean) CharsetMethods.fgCharsetEncoderCanEncodeMethod.invoke(fCharsetEncoder, fArgsForMethod)).booleanValue();
            }
            catch (Exception e) {
                fCharsetEncoder = null;
                fHaveTriedCharsetEncoder = false;
            }
        }

        if (fCharToByteConverter == null) {
            if (fHaveTriedCToB || !CharToByteConverterMethods.fgConvertersAvailable) {
                return false;
            }
            if (fArgsForMethod == null) {
                fArgsForMethod = new Object [1];
            }
            try {
                fArgsForMethod[0] = javaName;
                fCharToByteConverter = CharToByteConverterMethods.fgGetConverterMethod.invoke(null, fArgsForMethod);
            }
            catch (Exception e) {
                fHaveTriedCToB = true;
                return false;
            }
        }
        try {
            fArgsForMethod[0] = new Character(ch);
            return ((Boolean) CharToByteConverterMethods.fgCanConvertMethod.invoke(fCharToByteConverter, fArgsForMethod)).booleanValue();
        }
        catch (Exception e) {
            fCharToByteConverter = null;
            fHaveTriedCToB = false;
            return false;
        }
    }

    public static void testJavaEncodingName(String name)  throws UnsupportedEncodingException {
        final byte [] bTest = {(byte)'v', (byte)'a', (byte)'l', (byte)'i', (byte)'d'};
        String s = new String(bTest, name);
    }


    static class CharsetMethods {

        private static java.lang.reflect.Method fgCharsetForNameMethod = null;

        private static java.lang.reflect.Method fgCharsetCanEncodeMethod = null;

        private static java.lang.reflect.Method fgCharsetNewEncoderMethod = null;

        private static java.lang.reflect.Method fgCharsetEncoderCanEncodeMethod = null;

        private static boolean fgNIOCharsetAvailable = false;

        private CharsetMethods() {}

        static {
            try {
                Class charsetClass = Class.forName("java.nio.charset.Charset");
                Class charsetEncoderClass = Class.forName("java.nio.charset.CharsetEncoder");
                fgCharsetForNameMethod = charsetClass.getMethod("forName", new Class [] {String.class});
                fgCharsetCanEncodeMethod = charsetClass.getMethod("canEncode", new Class [] {});
                fgCharsetNewEncoderMethod = charsetClass.getMethod("newEncoder", new Class [] {});
                fgCharsetEncoderCanEncodeMethod = charsetEncoderClass.getMethod("canEncode", new Class [] {Character.TYPE});
                fgNIOCharsetAvailable = true;
            }
            catch (Exception exc) {
                fgCharsetForNameMethod = null;
                fgCharsetCanEncodeMethod = null;
                fgCharsetEncoderCanEncodeMethod = null;
                fgCharsetNewEncoderMethod = null;
                fgNIOCharsetAvailable = false;
            }
        }
    }


    static class CharToByteConverterMethods {

        private static java.lang.reflect.Method fgGetConverterMethod = null;

        private static java.lang.reflect.Method fgCanConvertMethod = null;

        private static boolean fgConvertersAvailable = false;

        private CharToByteConverterMethods() {}

        static {
            try {
                Class clazz = Class.forName("sun.io.CharToByteConverter");
                fgGetConverterMethod = clazz.getMethod("getConverter", new Class [] {String.class});
                fgCanConvertMethod = clazz.getMethod("canConvert", new Class [] {Character.TYPE});
                fgConvertersAvailable = true;
            }
            catch (Exception exc) {
                fgGetConverterMethod = null;
                fgCanConvertMethod = null;
                fgConvertersAvailable = false;
            }
        }
    }
}
