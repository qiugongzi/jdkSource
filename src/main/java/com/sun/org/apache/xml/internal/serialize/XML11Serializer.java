




package com.sun.org.apache.xml.internal.serialize;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMError;


public class XML11Serializer
extends XMLSerializer {

    protected static final boolean DEBUG = false;

    protected NamespaceSupport fNSBinder;


    protected NamespaceSupport fLocalNSBinder;


    protected SymbolTable fSymbolTable;

    protected boolean fDOML1 = false;
    protected int fNamespaceCounter = 1;
    protected final static String PREFIX = "NS";


    protected boolean fNamespaces = false;


    private boolean fPreserveSpace;



    public XML11Serializer() {
        super( );
        _format.setVersion("1.1");
    }



    public XML11Serializer( OutputFormat format ) {
        super( format );
        _format.setVersion("1.1");
    }



    public XML11Serializer( Writer writer, OutputFormat format ) {
        super( writer, format );
        _format.setVersion("1.1");
    }



    public XML11Serializer( OutputStream output, OutputFormat format ) {
        super( output, format != null ? format : new OutputFormat( Method.XML, null, false ) );
        _format.setVersion("1.1");
    }

    public void characters( char[] chars, int start, int length )
        throws SAXException
    {
        ElementState state;

        try {
            state = content();

            if ( state.inCData || state.doCData ) {
                int          saveIndent;

                if ( ! state.inCData ) {
                    _printer.printText( "<![CDATA[" );
                    state.inCData = true;
                }
                saveIndent = _printer.getNextIndent();
                _printer.setNextIndent( 0 );
                char ch;
                final int end = start + length;
                for ( int index = start; index < end; ++index ) {
                    ch = chars[index];
                    if ( ch == ']' && index + 2 < end &&
                        chars[ index + 1 ] == ']' && chars[ index + 2 ] == '>' ) {
                        _printer.printText("]]]]><![CDATA[>");
                        index +=2;
                        continue;
                    }
                    if (!XML11Char.isXML11Valid(ch)) {
                        if (++index < end) {
                            surrogates(ch, chars[index]);
                        }
                        else {
                            fatalError("The character '"+(char)ch+"' is an invalid XML character");
                        }
                        continue;
                    } else {
                        if ( _encodingInfo.isPrintable((char)ch) && XML11Char.isXML11ValidLiteral(ch)) {
                            _printer.printText((char)ch);
                        } else {
                            _printer.printText("]]>&#x");
                            _printer.printText(Integer.toHexString(ch));
                            _printer.printText(";<![CDATA[");
                        }
                    }
                }
                _printer.setNextIndent( saveIndent );

            } else {

                int saveIndent;

                if ( state.preserveSpace ) {
                    saveIndent = _printer.getNextIndent();
                    _printer.setNextIndent( 0 );
                    printText( chars, start, length, true, state.unescaped );
                    _printer.setNextIndent( saveIndent );
                } else {
                    printText( chars, start, length, false, state.unescaped );
                }
            }
        } catch ( IOException except ) {
            throw new SAXException( except );
        }
    }


    protected void printEscaped( String source ) throws IOException {
        int length = source.length();
        for ( int i = 0 ; i < length ; ++i ) {
            int ch = source.charAt(i);
            if (!XML11Char.isXML11Valid(ch)) {
                if (++i <length) {
                    surrogates(ch, source.charAt(i));
                } else {
                    fatalError("The character '"+(char)ch+"' is an invalid XML character");
                }
                continue;
            }
            if (ch == '\n' || ch == '\r' || ch == '\t' || ch == 0x0085 || ch == 0x2028){
                                printHex(ch);
                        } else if (ch == '<') {
                                _printer.printText("&lt;");
                        } else if (ch == '&') {
                                _printer.printText("&amp;");
                        } else if (ch == '"') {
                                _printer.printText("&quot;");
                        } else if ((ch >= ' ' && _encodingInfo.isPrintable((char) ch))) {
                                _printer.printText((char) ch);
                        } else {
                                printHex(ch);
                        }
        }
    }

    protected final void printCDATAText(String text) throws IOException {
        int length = text.length();
        char ch;

        for (int index = 0; index < length; ++index) {
            ch = text.charAt(index);

            if (ch == ']'
                && index + 2 < length
                && text.charAt(index + 1) == ']'
                && text.charAt(index + 2) == '>') { if (fDOMErrorHandler != null){
                    if ((features & DOMSerializerImpl.SPLITCDATA) == 0
                    && (features & DOMSerializerImpl.WELLFORMED) == 0) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.SERIALIZER_DOMAIN,
                            "EndingCDATA",
                            null);
                    modifyDOMError(
                        msg,
                        DOMError.SEVERITY_FATAL_ERROR,
                        null, fCurrentNode);
                    boolean continueProcess =
                        fDOMErrorHandler.handleError(fDOMError);
                    if (!continueProcess) {
                        throw new IOException();
                    }
                } else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.SERIALIZER_DOMAIN,
                            "SplittingCDATA",
                            null);
                    modifyDOMError(
                        msg,
                        DOMError.SEVERITY_WARNING,
                        null, fCurrentNode);
                    fDOMErrorHandler.handleError(fDOMError);
                }
                }
                _printer.printText("]]]]><![CDATA[>");
                index += 2;
                continue;
            }

            if (!XML11Char.isXML11Valid(ch)) {
                if (++index < length) {
                    surrogates(ch, text.charAt(index));
                } else {
                    fatalError(
                        "The character '"
                            + (char) ch
                            + "' is an invalid XML character");
                }
                continue;
            } else {
                if (_encodingInfo.isPrintable((char) ch)
                    && XML11Char.isXML11ValidLiteral(ch)) {
                    _printer.printText((char) ch);
                } else {

                    _printer.printText("]]>&#x");
                    _printer.printText(Integer.toHexString(ch));
                    _printer.printText(";<![CDATA[");
                }
            }
        }
    }


    protected final void printXMLChar( int ch ) throws IOException {

        if (ch == '\r' || ch == 0x0085 || ch == 0x2028) {
                        printHex(ch);
        } else if ( ch == '<') {
            _printer.printText("&lt;");
        } else if (ch == '&') {
            _printer.printText("&amp;");
                } else if (ch == '>'){
                        _printer.printText("&gt;");
        } else if ( _encodingInfo.isPrintable((char)ch) && XML11Char.isXML11ValidLiteral(ch)) {
            _printer.printText((char)ch);
        } else {
             printHex(ch);
        }
    }



    protected final void surrogates(int high, int low) throws IOException{
        if (XMLChar.isHighSurrogate(high)) {
            if (!XMLChar.isLowSurrogate(low)) {
                fatalError("The character '"+(char)low+"' is an invalid XML character");
            }
            else {
                int supplemental = XMLChar.supplemental((char)high, (char)low);
                if (!XML11Char.isXML11Valid(supplemental)) {
                    fatalError("The character '"+(char)supplemental+"' is an invalid XML character");
                }
                else {
                    if (content().inCData ) {
                        _printer.printText("]]>&#x");
                        _printer.printText(Integer.toHexString(supplemental));
                        _printer.printText(";<![CDATA[");
                    }
                    else {
                                                printHex(supplemental);
                    }
                }
            }
        } else {
            fatalError("The character '"+(char)high+"' is an invalid XML character");
        }

    }


    protected void printText( String text, boolean preserveSpace, boolean unescaped )
    throws IOException {
        int index;
        char ch;
        int length = text.length();
        if ( preserveSpace ) {
            for ( index = 0 ; index < length ; ++index ) {
                ch = text.charAt( index );
                if (!XML11Char.isXML11Valid(ch)) {
                    if (++index <length) {
                        surrogates(ch, text.charAt(index));
                    } else {
                        fatalError("The character '"+(char)ch+"' is an invalid XML character");
                    }
                    continue;
                }
                if ( unescaped  && XML11Char.isXML11ValidLiteral(ch)) {
                    _printer.printText( ch );
                } else
                    printXMLChar( ch );
            }
        } else {
            for ( index = 0 ; index < length ; ++index ) {
                ch = text.charAt( index );
                if (!XML11Char.isXML11Valid(ch)) {
                    if (++index <length) {
                        surrogates(ch, text.charAt(index));
                    } else {
                        fatalError("The character '"+(char)ch+"' is an invalid XML character");
                    }
                    continue;
                }

                if ( unescaped && XML11Char.isXML11ValidLiteral(ch) )
                    _printer.printText( ch );
                else
                    printXMLChar( ch);
            }
        }
    }



    protected void printText( char[] chars, int start, int length,
                              boolean preserveSpace, boolean unescaped ) throws IOException {
        int index;
        char ch;

        if ( preserveSpace ) {
            while ( length-- > 0 ) {
                ch = chars[start++];
                if (!XML11Char.isXML11Valid(ch)) {
                    if ( length-- > 0) {
                        surrogates(ch, chars[start++]);
                    } else {
                        fatalError("The character '"+(char)ch+"' is an invalid XML character");
                    }
                    continue;
                }
                if ( unescaped && XML11Char.isXML11ValidLiteral(ch))
                    _printer.printText( ch );
                else
                    printXMLChar( ch );
            }
        } else {
            while ( length-- > 0 ) {
                ch = chars[start++];
                if (!XML11Char.isXML11Valid(ch)) {
                    if ( length-- > 0) {
                        surrogates(ch, chars[start++]);
                    } else {
                        fatalError("The character '"+(char)ch+"' is an invalid XML character");
                    }
                    continue;
                }

                if ( unescaped && XML11Char.isXML11ValidLiteral(ch))
                    _printer.printText( ch );
                else
                    printXMLChar( ch );
            }
        }
    }


    public boolean reset() {
        super.reset();
        return true;

    }

}
