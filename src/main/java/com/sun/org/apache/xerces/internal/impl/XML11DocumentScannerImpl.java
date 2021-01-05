


package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;


public class XML11DocumentScannerImpl
    extends XMLDocumentScannerImpl {



    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();

    public XML11DocumentScannerImpl() {super();} protected int scanContent(XMLStringBuffer content) throws IOException, XNIException {

        fTempString.length = 0;
        int c = fEntityScanner.scanContent(fTempString);
        content.append(fTempString);

        if (c == '\r' || c == 0x85 || c == 0x2028) {
            fEntityScanner.scanChar(null);
            content.append((char)c);
            c = -1;
        }


        if (c == ']') {
            content.append((char)fEntityScanner.scanChar(null));
            fInScanContent = true;
            if (fEntityScanner.skipChar(']', null)) {
                content.append(']');
                while (fEntityScanner.skipChar(']', null)) {
                    content.append(']');
                }
                if (fEntityScanner.skipChar('>', null)) {
                    reportFatalError("CDEndInContent", null);
                }
            }

            fInScanContent = false;
            c = -1;
        }
        return c;

    } protected boolean scanAttributeValue(XMLString value,
                                      XMLString nonNormalizedValue,
                                      String atName,
                                      boolean checkEntities,String eleName, boolean isNSURI)
        throws IOException, XNIException
    {
        int quote = fEntityScanner.peekChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError("OpenQuoteExpected", new Object[]{eleName,atName});
        }

        fEntityScanner.scanChar(NameType.ATTRIBUTE);
        int entityDepth = fEntityDepth;

        int c = fEntityScanner.scanLiteral(quote, value, isNSURI);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** scanLiteral -> \""
                               + value.toString() + "\"");
        }

        int fromIndex = 0;
        if (c == quote && (fromIndex = isUnchangedByNormalization(value)) == -1) {

            nonNormalizedValue.setValues(value);
            int cquote = fEntityScanner.scanChar(NameType.ATTRIBUTE);
            if (cquote != quote) {
                reportFatalError("CloseQuoteExpected", new Object[]{eleName,atName});
            }
            return true;
        }
        fStringBuffer2.clear();
        fStringBuffer2.append(value);
        normalizeWhitespace(value, fromIndex);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** normalizeWhitespace -> \""
                               + value.toString() + "\"");
        }
        if (c != quote) {
            fScanningAttribute = true;
            fStringBuffer.clear();
            do {
                fStringBuffer.append(value);
                if (DEBUG_ATTR_NORMALIZATION) {
                    System.out.println("** value2: \""
                                       + fStringBuffer.toString() + "\"");
                }
                if (c == '&') {
                    fEntityScanner.skipChar('&', NameType.REFERENCE);
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append('&');
                    }
                    if (fEntityScanner.skipChar('#', NameType.REFERENCE)) {
                        if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append('#');
                        }
                        int ch = scanCharReferenceValue(fStringBuffer, fStringBuffer2);
                        if (ch != -1) {
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value3: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                    }
                    else {
                        String entityName = fEntityScanner.scanName(NameType.REFERENCE);
                        if (entityName == null) {
                            reportFatalError("NameRequiredInReference", null);
                        }
                        else if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(entityName);
                        }
                        if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
                            reportFatalError("SemicolonRequiredInReference",
                                             new Object []{entityName});
                        }
                        else if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(';');
                        }
                        if (resolveCharacter(entityName, fStringBuffer)) {
                            checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
                        }
                        else {
                            if (fEntityManager.isExternalEntity(entityName)) {
                                reportFatalError("ReferenceToExternalEntity",
                                                 new Object[] { entityName });
                            }
                            else {
                                if (!fEntityManager.isDeclaredEntity(entityName)) {
                                    if (checkEntities) {
                                        if (fValidation) {
                                            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                                       "EntityNotDeclared",
                                                                       new Object[]{entityName},
                                                                       XMLErrorReporter.SEVERITY_ERROR);
                                        }
                                    }
                                    else {
                                        reportFatalError("EntityNotDeclared",
                                                         new Object[]{entityName});
                                    }
                                }
                                fEntityManager.startEntity(true, entityName, true);
                            }
                        }
                    }
                }
                else if (c == '<') {
                    reportFatalError("LessthanInAttValue",
                                     new Object[] { eleName, atName });
                    fEntityScanner.scanChar(null);
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                }
                else if (c == '%' || c == ']') {
                    fEntityScanner.scanChar(null);
                    fStringBuffer.append((char)c);
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                    if (DEBUG_ATTR_NORMALIZATION) {
                        System.out.println("** valueF: \""
                                           + fStringBuffer.toString() + "\"");
                    }
                }
                else if (c == '\n' || c == '\r' || c == 0x85 || c == 0x2028) {
                    fEntityScanner.scanChar(null);
                    fStringBuffer.append(' ');
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append('\n');
                    }
                }
                else if (c != -1 && XMLChar.isHighSurrogate(c)) {
                    fStringBuffer3.clear();
                    if (scanSurrogates(fStringBuffer3)) {
                        fStringBuffer.append(fStringBuffer3);
                        if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(fStringBuffer3);
                        }
                        if (DEBUG_ATTR_NORMALIZATION) {
                            System.out.println("** valueI: \""
                                               + fStringBuffer.toString()
                                               + "\"");
                        }
                    }
                }
                else if (c != -1 && isInvalidLiteral(c)) {
                    reportFatalError("InvalidCharInAttValue",
                                     new Object[] {eleName, atName, Integer.toString(c, 16)});
                    fEntityScanner.scanChar(null);
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                }
                c = fEntityScanner.scanLiteral(quote, value, isNSURI);
                if (entityDepth == fEntityDepth) {
                    fStringBuffer2.append(value);
                }
                normalizeWhitespace(value);
            } while (c != quote || entityDepth != fEntityDepth);
            fStringBuffer.append(value);
            if (DEBUG_ATTR_NORMALIZATION) {
                System.out.println("** valueN: \""
                                   + fStringBuffer.toString() + "\"");
            }
            value.setValues(fStringBuffer);
            fScanningAttribute = false;
        }
        nonNormalizedValue.setValues(fStringBuffer2);

        int cquote = fEntityScanner.scanChar(null);
        if (cquote != quote) {
            reportFatalError("CloseQuoteExpected", new Object[]{eleName,atName});
        }
        return nonNormalizedValue.equals(value.ch, value.offset, value.length);
    } protected boolean scanPubidLiteral(XMLString literal)
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
        return (XML11Char.isXML11Invalid(value));
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
        return (version.equals("1.1") || version.equals("1.0"));
    } protected String getVersionNotSupportedKey () {
        return "VersionNotSupported11";
    } }