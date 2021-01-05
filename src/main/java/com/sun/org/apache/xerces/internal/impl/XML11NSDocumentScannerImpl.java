



package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import javax.xml.stream.events.XMLEvent;



public class XML11NSDocumentScannerImpl extends XML11DocumentScannerImpl {


    protected boolean fBindNamespaces;


    protected boolean fPerformValidation;

    private XMLDTDValidatorFilter fDTDValidator;


    private boolean fSawSpace;



    public void setDTDValidator(XMLDTDValidatorFilter validator) {
        fDTDValidator = validator;
    }


    protected boolean scanStartElement() throws IOException, XNIException {

        if (DEBUG_START_END_ELEMENT)
            System.out.println(">>> scanStartElementNS()");
                fEntityScanner.scanQName(fElementQName, NameType.ELEMENTSTART);
        String rawname = fElementQName.rawname;
        if (fBindNamespaces) {
            fNamespaceContext.pushContext();
            if (fScannerState == SCANNER_STATE_ROOT_ELEMENT) {
                if (fPerformValidation) {
                    fErrorReporter.reportError(
                        XMLMessageFormatter.XML_DOMAIN,
                        "MSG_GRAMMAR_NOT_FOUND",
                        new Object[] { rawname },
                        XMLErrorReporter.SEVERITY_ERROR);

                    if (fDoctypeName == null
                        || !fDoctypeName.equals(rawname)) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XML_DOMAIN,
                            "RootElementTypeMustMatchDoctypedecl",
                            new Object[] { fDoctypeName, rawname },
                            XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
            }
        }

        fCurrentElement = fElementStack.pushElement(fElementQName);

        boolean empty = false;
        fAttributes.removeAllAttributes();
        do {
            boolean sawSpace = fEntityScanner.skipSpaces();

            int c = fEntityScanner.peekChar();
            if (c == '>') {
                fEntityScanner.scanChar(null);
                break;
            } else if (c == '/') {
                fEntityScanner.scanChar(null);
                if (!fEntityScanner.skipChar('>', null)) {
                    reportFatalError(
                        "ElementUnterminated",
                        new Object[] { rawname });
                }
                empty = true;
                break;
            } else if (!isValidNameStartChar(c) || !sawSpace) {
                if (!isValidNameStartHighSurrogate(c) || !sawSpace) {
                    reportFatalError(
                        "ElementUnterminated",
                        new Object[] { rawname });
                }
            }

            scanAttribute(fAttributes);
            if (fSecurityManager != null && (!fSecurityManager.isNoLimit(fElementAttributeLimit)) &&
                    fAttributes.getLength() > fElementAttributeLimit){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                             "ElementAttributeLimit",
                                             new Object[]{rawname, new Integer(fElementAttributeLimit) },
                                             XMLErrorReporter.SEVERITY_FATAL_ERROR );
            }

        } while (true);

        if (fBindNamespaces) {
            if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                fErrorReporter.reportError(
                    XMLMessageFormatter.XMLNS_DOMAIN,
                    "ElementXMLNSPrefix",
                    new Object[] { fElementQName.rawname },
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            String prefix =
                fElementQName.prefix != null
                    ? fElementQName.prefix
                    : XMLSymbols.EMPTY_STRING;
            fElementQName.uri = fNamespaceContext.getURI(prefix);
            fCurrentElement.uri = fElementQName.uri;

            if (fElementQName.prefix == null && fElementQName.uri != null) {
                fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (fElementQName.prefix != null && fElementQName.uri == null) {
                fErrorReporter.reportError(
                    XMLMessageFormatter.XMLNS_DOMAIN,
                    "ElementPrefixUnbound",
                    new Object[] {
                        fElementQName.prefix,
                        fElementQName.rawname },
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            int length = fAttributes.getLength();
            for (int i = 0; i < length; i++) {
                fAttributes.getName(i, fAttributeQName);

                String aprefix =
                    fAttributeQName.prefix != null
                        ? fAttributeQName.prefix
                        : XMLSymbols.EMPTY_STRING;
                String uri = fNamespaceContext.getURI(aprefix);
                if (fAttributeQName.uri != null
                    && fAttributeQName.uri == uri) {
                    continue;
                }
                if (aprefix != XMLSymbols.EMPTY_STRING) {
                    fAttributeQName.uri = uri;
                    if (uri == null) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributePrefixUnbound",
                            new Object[] {
                                fElementQName.rawname,
                                fAttributeQName.rawname,
                                aprefix },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                    fAttributes.setURI(i, uri);
                }
            }

            if (length > 1) {
                QName name = fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributeNSNotUnique",
                            new Object[] {
                                fElementQName.rawname,
                                name.localpart,
                                name.uri },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    } else {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributeNotUnique",
                            new Object[] {
                                fElementQName.rawname,
                                name.rawname },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
        }

        if (empty) {
            fMarkupDepth--;

            if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
                reportFatalError(
                    "ElementEntityMismatch",
                    new Object[] { fCurrentElement.rawname });
            }

            if (fDocumentHandler != null) {
                fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
            }


            fScanEndElement = true;

            fElementStack.popElement();
        } else {
            if(dtdGrammarUtil != null) {
                dtdGrammarUtil.startElement(fElementQName, fAttributes);
            }

            if (fDocumentHandler != null) {
                fDocumentHandler.startElement(fElementQName, fAttributes, null);
            }
        }

        if (DEBUG_START_END_ELEMENT)
            System.out.println("<<< scanStartElement(): " + empty);
        return empty;

    } protected void scanStartElementName ()
        throws IOException, XNIException {
        fEntityScanner.scanQName(fElementQName, NameType.ELEMENTSTART);
        fSawSpace = fEntityScanner.skipSpaces();
    } protected boolean scanStartElementAfterName()
        throws IOException, XNIException {

        String rawname = fElementQName.rawname;
        if (fBindNamespaces) {
            fNamespaceContext.pushContext();
            if (fScannerState == SCANNER_STATE_ROOT_ELEMENT) {
                if (fPerformValidation) {
                    fErrorReporter.reportError(
                        XMLMessageFormatter.XML_DOMAIN,
                        "MSG_GRAMMAR_NOT_FOUND",
                        new Object[] { rawname },
                        XMLErrorReporter.SEVERITY_ERROR);

                    if (fDoctypeName == null
                        || !fDoctypeName.equals(rawname)) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XML_DOMAIN,
                            "RootElementTypeMustMatchDoctypedecl",
                            new Object[] { fDoctypeName, rawname },
                            XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
            }
        }

        fCurrentElement = fElementStack.pushElement(fElementQName);

        boolean empty = false;
        fAttributes.removeAllAttributes();
        do {

            int c = fEntityScanner.peekChar();
            if (c == '>') {
                fEntityScanner.scanChar(null);
                break;
            } else if (c == '/') {
                fEntityScanner.scanChar(null);
                if (!fEntityScanner.skipChar('>', null)) {
                    reportFatalError(
                        "ElementUnterminated",
                        new Object[] { rawname });
                }
                empty = true;
                break;
            } else if (!isValidNameStartChar(c) || !fSawSpace) {
                if (!isValidNameStartHighSurrogate(c) || !fSawSpace) {
                    reportFatalError(
                        "ElementUnterminated",
                        new Object[] { rawname });
                }
            }

            scanAttribute(fAttributes);

            fSawSpace = fEntityScanner.skipSpaces();

        } while (true);

        if (fBindNamespaces) {
            if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                fErrorReporter.reportError(
                    XMLMessageFormatter.XMLNS_DOMAIN,
                    "ElementXMLNSPrefix",
                    new Object[] { fElementQName.rawname },
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            String prefix =
                fElementQName.prefix != null
                    ? fElementQName.prefix
                    : XMLSymbols.EMPTY_STRING;
            fElementQName.uri = fNamespaceContext.getURI(prefix);
            fCurrentElement.uri = fElementQName.uri;

            if (fElementQName.prefix == null && fElementQName.uri != null) {
                fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (fElementQName.prefix != null && fElementQName.uri == null) {
                fErrorReporter.reportError(
                    XMLMessageFormatter.XMLNS_DOMAIN,
                    "ElementPrefixUnbound",
                    new Object[] {
                        fElementQName.prefix,
                        fElementQName.rawname },
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            int length = fAttributes.getLength();
            for (int i = 0; i < length; i++) {
                fAttributes.getName(i, fAttributeQName);

                String aprefix =
                    fAttributeQName.prefix != null
                        ? fAttributeQName.prefix
                        : XMLSymbols.EMPTY_STRING;
                String uri = fNamespaceContext.getURI(aprefix);
                if (fAttributeQName.uri != null
                    && fAttributeQName.uri == uri) {
                    continue;
                }
                if (aprefix != XMLSymbols.EMPTY_STRING) {
                    fAttributeQName.uri = uri;
                    if (uri == null) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributePrefixUnbound",
                            new Object[] {
                                fElementQName.rawname,
                                fAttributeQName.rawname,
                                aprefix },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                    fAttributes.setURI(i, uri);
                }
            }

            if (length > 1) {
                QName name = fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributeNSNotUnique",
                            new Object[] {
                                fElementQName.rawname,
                                name.localpart,
                                name.uri },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    } else {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "AttributeNotUnique",
                            new Object[] {
                                fElementQName.rawname,
                                name.rawname },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
        }

        if (fDocumentHandler != null) {
            if (empty) {

                fMarkupDepth--;

                if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
                    reportFatalError(
                        "ElementEntityMismatch",
                        new Object[] { fCurrentElement.rawname });
                }

                fDocumentHandler.emptyElement(fElementQName, fAttributes, null);

                if (fBindNamespaces) {
                    fNamespaceContext.popContext();
                }
                fElementStack.popElement();
            } else {
                fDocumentHandler.startElement(fElementQName, fAttributes, null);
            }
        }

        if (DEBUG_START_END_ELEMENT)
            System.out.println("<<< scanStartElementAfterName(): " + empty);
        return empty;

    } protected void scanAttribute(XMLAttributesImpl attributes)
        throws IOException, XNIException {
        if (DEBUG_START_END_ELEMENT)
            System.out.println(">>> scanAttribute()");

        fEntityScanner.scanQName(fAttributeQName, NameType.ATTRIBUTENAME);

        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('=', NameType.ATTRIBUTE)) {
            reportFatalError(
                "EqRequiredInAttribute",
                new Object[] {
                    fCurrentElement.rawname,
                    fAttributeQName.rawname });
        }
        fEntityScanner.skipSpaces();

        int attrIndex;

        if (fBindNamespaces) {
            attrIndex = attributes.getLength();
            attributes.addAttributeNS(
                fAttributeQName,
                XMLSymbols.fCDATASymbol,
                null);
        } else {
            int oldLen = attributes.getLength();
            attrIndex =
                attributes.addAttribute(
                    fAttributeQName,
                    XMLSymbols.fCDATASymbol,
                    null);

            if (oldLen == attributes.getLength()) {
                reportFatalError(
                    "AttributeNotUnique",
                    new Object[] {
                        fCurrentElement.rawname,
                        fAttributeQName.rawname });
            }
        }

        boolean isVC = fHasExternalDTD && !fStandalone;


        String localpart = fAttributeQName.localpart;
        String prefix = fAttributeQName.prefix != null
                ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        boolean isNSDecl = fBindNamespaces & (prefix == XMLSymbols.PREFIX_XMLNS ||
                    prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS);

        scanAttributeValue(this.fTempString, fTempString2, fAttributeQName.rawname,
            isVC, fCurrentElement.rawname, isNSDecl);
        String value = fTempString.toString();
        attributes.setValue(attrIndex, value);
        attributes.setNonNormalizedValue(attrIndex, fTempString2.toString());
        attributes.setSpecified(attrIndex, true);

        if (fBindNamespaces) {
            if (isNSDecl) {
                if (value.length() > fXMLNameLimit) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                            "MaxXMLNameLimit",
                            new Object[]{value, value.length(), fXMLNameLimit,
                            fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT)},
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                String uri = fSymbolTable.addSymbol(value);

                if (prefix == XMLSymbols.PREFIX_XMLNS
                    && localpart == XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(
                        XMLMessageFormatter.XMLNS_DOMAIN,
                        "CantBindXMLNS",
                        new Object[] { fAttributeQName },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (uri == NamespaceContext.XMLNS_URI) {
                    fErrorReporter.reportError(
                        XMLMessageFormatter.XMLNS_DOMAIN,
                        "CantBindXMLNS",
                        new Object[] { fAttributeQName },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "CantBindXML",
                            new Object[] { fAttributeQName },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                else {
                    if (uri == NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(
                            XMLMessageFormatter.XMLNS_DOMAIN,
                            "CantBindXML",
                            new Object[] { fAttributeQName },
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }

                prefix =
                    localpart != XMLSymbols.PREFIX_XMLNS
                        ? localpart
                        : XMLSymbols.EMPTY_STRING;

                fNamespaceContext.declarePrefix(
                    prefix,
                    uri.length() != 0 ? uri : null);
                attributes.setURI(
                    attrIndex,
                    fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));

            } else {
                if (fAttributeQName.prefix != null) {
                    attributes.setURI(
                        attrIndex,
                        fNamespaceContext.getURI(fAttributeQName.prefix));
                }
            }
        }

        if (DEBUG_START_END_ELEMENT)
            System.out.println("<<< scanAttribute()");
    } protected int scanEndElement() throws IOException, XNIException {
        if (DEBUG_START_END_ELEMENT)
            System.out.println(">>> scanEndElement()");

        QName endElementName = fElementStack.popElement();

        if (!fEntityScanner.skipString(endElementName.rawname)) {
             reportFatalError(
                "ETagRequired",
                new Object[] { endElementName.rawname });
        }

        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('>', NameType.ELEMENTEND)) {
            reportFatalError(
                "ETagUnterminated",
                new Object[] { endElementName.rawname });
        }
        fMarkupDepth--;

        fMarkupDepth--;

        if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
            reportFatalError(
                "ElementEntityMismatch",
                new Object[] { endElementName.rawname });
        }

        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(endElementName, null);



        }

        if(dtdGrammarUtil != null)
            dtdGrammarUtil.endElement(endElementName);

        return fMarkupDepth;

    } public void reset(XMLComponentManager componentManager)
        throws XMLConfigurationException {

        super.reset(componentManager);
        fPerformValidation = false;
        fBindNamespaces = false;
    }


    protected Driver createContentDriver() {
        return new NS11ContentDriver();
    } public int next() throws IOException, XNIException {
        if((fScannerLastState == XMLEvent.END_ELEMENT) && fBindNamespaces){
            fScannerLastState = -1;
            fNamespaceContext.popContext();
        }

        return fScannerLastState = super.next();
    }



    protected final class NS11ContentDriver extends ContentDriver {

        protected boolean scanRootElementHook()
            throws IOException, XNIException {

            if (fExternalSubsetResolver != null && !fSeenDoctypeDecl
                && !fDisallowDoctype && (fValidation || fLoadExternalDTD)) {
                scanStartElementName();
                resolveExternalSubsetAndRead();
                reconfigurePipeline();
                if (scanStartElementAfterName()) {
                    setScannerState(SCANNER_STATE_TRAILING_MISC);
                    setDriver(fTrailingMiscDriver);
                    return true;
                }
            }
            else {
                reconfigurePipeline();
                if (scanStartElement()) {
                    setScannerState(SCANNER_STATE_TRAILING_MISC);
                    setDriver(fTrailingMiscDriver);
                    return true;
                }
            }
            return false;

        } private void reconfigurePipeline() {
            if (fDTDValidator == null) {
                fBindNamespaces = true;
            }
            else if (!fDTDValidator.hasGrammar()) {
                fBindNamespaces = true;
                fPerformValidation = fDTDValidator.validate();
                XMLDocumentSource source = fDTDValidator.getDocumentSource();
                XMLDocumentHandler handler = fDTDValidator.getDocumentHandler();
                source.setDocumentHandler(handler);
                if (handler != null)
                    handler.setDocumentSource(source);
                fDTDValidator.setDocumentSource(null);
                fDTDValidator.setDocumentHandler(null);
            }
        } }
}
