


package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;


public class XML11DTDScannerImpl
    extends XMLDTDScannerImpl {


    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    public XML11DTDScannerImpl() {super();} public XML11DTDScannerImpl(SymbolTable symbolTable,
                XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
        super(symbolTable, errorReporter, entityManager);
    }

    protected boolean scanPubidLiteral(XMLString literal)
        throws IOException, XNIException
    {
        int quote = fEntityScanner.scanChar(null);
        if (quote != '\'' && quote != '"') {
            reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }

        fStringBuffer.clear();
        boolean skipSpace = true;
        boolean dataok = true;
        while (true) {
            int c = fEntityScanner.scanChar(null);
            if (c == ' ' || c == '\n' || c == '\r' || c == 0x85 || c == 0x2028) {
                if (!skipSpace) {
                    fStringBuffer.append(' ');
                    skipSpace = true;
                }
            }
            else if (c == quote) {
                if (skipSpace) {
                    fStringBuffer.length--;
                }
                literal.setValues(fStringBuffer);
                break;
            }
            else if (XMLChar.isPubid(c)) {
                fStringBuffer.append((char)c);
                skipSpace = false;
            }
            else if (c == -1) {
                reportFatalError("PublicIDUnterminated", null);
                return false;
            }
            else {
                dataok = false;
                reportFatalError("InvalidCharInPublicID",
                                 new Object[]{Integer.toHexString(c)});
            }
        }
        return dataok;
   }


    protected void normalizeWhitespace(XMLString value) {
        int end = value.offset + value.length;
        for (int i = value.offset; i < end; ++i) {
            int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                value.ch[i] = ' ';
            }
        }
    }


    protected void normalizeWhitespace(XMLString value, int fromIndex) {
        int end = value.offset + value.length;
        for (int i = value.offset + fromIndex; i < end; ++i) {
            int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                value.ch[i] = ' ';
            }
        }
    }


    protected int isUnchangedByNormalization(XMLString value) {
        int end = value.offset + value.length;
        for (int i = value.offset; i < end; ++i) {
            int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                return i - value.offset;
            }
        }
        return -1;
    }

    protected boolean isInvalid(int value) {
        return (!XML11Char.isXML11Valid(value));
    } protected boolean isInvalidLiteral(int value) {
        return (!XML11Char.isXML11ValidLiteral(value));
    } protected boolean isValidNameChar(int value) {
        return (XML11Char.isXML11Name(value));
    } protected boolean isValidNameStartChar(int value) {
        return (XML11Char.isXML11NameStart(value));
    } protected boolean isValidNCName(int value) {
        return (XML11Char.isXML11NCName(value));
    } protected boolean isValidNameStartHighSurrogate(int value) {
        return XML11Char.isXML11NameHighSurrogate(value);
    } protected boolean versionSupported(String version) {
        return version.equals("1.1") || version.equals ("1.0");
    } protected String getVersionNotSupportedKey () {
        return "VersionNotSupported11";
    } }