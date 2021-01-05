



package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;

import javax.xml.stream.events.XMLEvent;


public class XMLNSDocumentScannerImpl
        extends XMLDocumentScannerImpl {


    protected boolean fBindNamespaces;


    protected boolean fPerformValidation;



    protected boolean fNotAddNSDeclAsAttribute = false;


     private XMLDTDValidatorFilter fDTDValidator;


     private boolean fXmlnsDeclared = false;


    public void reset(PropertyManager propertyManager) {
        setPropertyManager(propertyManager);
        super.reset(propertyManager);
        fBindNamespaces = false;
        fNotAddNSDeclAsAttribute = !((Boolean)propertyManager.getProperty(Constants.ADD_NAMESPACE_DECL_AS_ATTRIBUTE)).booleanValue();
    }

    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {
        super.reset(componentManager);
        fNotAddNSDeclAsAttribute = false ;
        fPerformValidation = false;
        fBindNamespaces = false;
    }



    public int next() throws IOException, XNIException {
        if((fScannerLastState == XMLEvent.END_ELEMENT) && fBindNamespaces){
            fScannerLastState = -1;
            fNamespaceContext.popContext();
        }

        return fScannerLastState = super.next();
    }


    public void setDTDValidator(XMLDTDValidatorFilter dtd){
        fDTDValidator = dtd;
    }




    protected boolean scanStartElement()
    throws IOException, XNIException {

        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +">>> scanStartElement()");
        if(fSkip && !fAdd){
            QName name = fElementStack.getNext();

            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("Trying to skip String = " + name.rawname);
            }

            fSkip = fEntityScanner.skipString(name.rawname); if(fSkip){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element SUCESSFULLY skipped = " + name.rawname);
                }
                fElementStack.push();
                fElementQName = name;
            }else{
                fElementStack.reposition();
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element was NOT skipped, REPOSITIONING stack" );
                }
            }
        }

        if(!fSkip || fAdd){
            fElementQName = fElementStack.nextElement();
            if (fNamespaces) {
                fEntityScanner.scanQName(fElementQName, NameType.ELEMENTSTART);
            } else {
                String name = fEntityScanner.scanName(NameType.ELEMENTSTART);
                fElementQName.setValues(null, name, name, null);
            }

            if(DEBUG)System.out.println("Element scanned in start element is " + fElementQName.toString());
            if(DEBUG_SKIP_ALGORITHM){
                if(fAdd){
                    System.out.println("Elements are being ADDED -- elemet added is = " + fElementQName.rawname + " at count = " + fElementStack.fCount);
                }
            }

        }

        if(fAdd){
            fElementStack.matchElement(fElementQName);
        }

        fCurrentElement = fElementQName;

        String rawname = fElementQName.rawname;
        checkDepth(rawname);
        if (fBindNamespaces) {
            fNamespaceContext.pushContext();
            if (fScannerState == SCANNER_STATE_ROOT_ELEMENT) {
                if (fPerformValidation) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                            "MSG_GRAMMAR_NOT_FOUND",
                            new Object[]{ rawname},
                            XMLErrorReporter.SEVERITY_ERROR);

                            if (fDoctypeName == null || !fDoctypeName.equals(rawname)) {
                                fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,
                                        "RootElementTypeMustMatchDoctypedecl",
                                        new Object[]{fDoctypeName, rawname},
                                        XMLErrorReporter.SEVERITY_ERROR);
                            }
                }
            }
        }


        fEmptyElement = false;
        fAttributes.removeAllAttributes();

        if(!seekCloseOfStartTag()){
            fReadingAttributes = true;
            fAttributeCacheUsedCount =0;
            fStringBufferIndex =0;
            fAddDefaultAttr = true;
            fXmlnsDeclared = false;

            do {
                scanAttribute(fAttributes);
                if (fSecurityManager != null && (!fSecurityManager.isNoLimit(fElementAttributeLimit)) &&
                        fAttributes.getLength() > fElementAttributeLimit){
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                 "ElementAttributeLimit",
                                                 new Object[]{rawname, fElementAttributeLimit },
                                                 XMLErrorReporter.SEVERITY_FATAL_ERROR );
                }

            } while (!seekCloseOfStartTag());
            fReadingAttributes=false;
        }

        if (fBindNamespaces) {
            if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "ElementXMLNSPrefix",
                        new Object[]{fElementQName.rawname},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            String prefix = fElementQName.prefix != null
                    ? fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            fElementQName.uri = fNamespaceContext.getURI(prefix);
            fCurrentElement.uri = fElementQName.uri;

            if (fElementQName.prefix == null && fElementQName.uri != null) {
                fElementQName.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (fElementQName.prefix != null && fElementQName.uri == null) {
                fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "ElementPrefixUnbound",
                        new Object[]{fElementQName.prefix, fElementQName.rawname},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }

            int length = fAttributes.getLength();
            for (int i = 0; i < length; i++) {
                fAttributes.getName(i, fAttributeQName);

                String aprefix = fAttributeQName.prefix != null
                        ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                String uri = fNamespaceContext.getURI(aprefix);
                if (fAttributeQName.uri != null && fAttributeQName.uri == uri) {
                    continue;
                }
                if (aprefix != XMLSymbols.EMPTY_STRING) {
                    fAttributeQName.uri = uri;
                    if (uri == null) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                "AttributePrefixUnbound",
                                new Object[]{fElementQName.rawname,fAttributeQName.rawname,aprefix},
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                    fAttributes.setURI(i, uri);
                    }
            }

            if (length > 1) {
                QName name = fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                "AttributeNSNotUnique",
                                new Object[]{fElementQName.rawname, name.localpart, name.uri},
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    } else {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                "AttributeNotUnique",
                                new Object[]{fElementQName.rawname, name.rawname},
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
        }


        if (fEmptyElement) {
            fMarkupDepth--;

            if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
                reportFatalError("ElementEntityMismatch",
                        new Object[]{fCurrentElement.rawname});
            }
            if (fDocumentHandler != null) {
                if(DEBUG)
                    System.out.println("emptyElement = " + fElementQName);

                fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
            }

            fScanEndElement = true;
            fElementStack.popElement();

        } else {

            if(dtdGrammarUtil != null)
                dtdGrammarUtil.startElement(fElementQName,fAttributes);
            if(fDocumentHandler != null){
                if(DEBUG)
                    System.out.println("startElement = " + fElementQName);
                fDocumentHandler.startElement(fElementQName, fAttributes, null);
            }
        }


        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +"<<< scanStartElement(): "+fEmptyElement);
        return fEmptyElement;

    } protected void scanAttribute(XMLAttributesImpl attributes)
    throws IOException, XNIException {
        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +">>> scanAttribute()");

        fEntityScanner.scanQName(fAttributeQName, NameType.ATTRIBUTENAME);

        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('=', NameType.ATTRIBUTE)) {
            reportFatalError("EqRequiredInAttribute",
                    new Object[]{fCurrentElement.rawname,fAttributeQName.rawname});
        }
        fEntityScanner.skipSpaces();

        int attrIndex = 0 ;


        boolean isVC =  fHasExternalDTD && !fStandalone;

        XMLString tmpStr = getString();


        String localpart = fAttributeQName.localpart;
        String prefix = fAttributeQName.prefix != null
                ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        boolean isNSDecl = fBindNamespaces & (prefix == XMLSymbols.PREFIX_XMLNS ||
                    prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS);

        scanAttributeValue(tmpStr, fTempString2, fAttributeQName.rawname, attributes,
                attrIndex, isVC, fCurrentElement.rawname, isNSDecl);

        String value = null;
        if (fBindNamespaces) {
            if (isNSDecl) {
                if (tmpStr.length > fXMLNameLimit) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                            "MaxXMLNameLimit",
                            new Object[]{new String(tmpStr.ch,tmpStr.offset,tmpStr.length),
                            tmpStr.length, fXMLNameLimit,
                            fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT)},
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                String uri = fSymbolTable.addSymbol(tmpStr.ch,tmpStr.offset,tmpStr.length);
                value = uri;
                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                            "CantBindXMLNS",
                            new Object[]{fAttributeQName},
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (uri == NamespaceContext.XMLNS_URI) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                            "CantBindXMLNS",
                            new Object[]{fAttributeQName},
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                "CantBindXML",
                                new Object[]{fAttributeQName},
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                else {
                    if (uri ==NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                "CantBindXML",
                                new Object[]{fAttributeQName},
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                prefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
                if(prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS){
                    fAttributeQName.prefix = XMLSymbols.PREFIX_XMLNS;
                }
                if (uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                            "EmptyPrefixedAttName",
                            new Object[]{fAttributeQName},
                            XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (((com.sun.org.apache.xerces.internal.util.NamespaceSupport) fNamespaceContext).containsPrefixInCurrentContext(prefix)) {
                    reportFatalError("AttributeNotUnique",
                            new Object[]{fCurrentElement.rawname,
                            fAttributeQName.rawname});
                }

                boolean declared = fNamespaceContext.declarePrefix(prefix, uri.length() != 0 ? uri : null);

                if (!declared) { if (fXmlnsDeclared) {
                        reportFatalError("AttributeNotUnique",
                                new Object[]{fCurrentElement.rawname,
                                fAttributeQName.rawname});
                    }

                    fXmlnsDeclared = true;
                }

                if(fNotAddNSDeclAsAttribute){
                    return ;
                }
            }
        }

        if (fBindNamespaces) {
            attrIndex = attributes.getLength();
            attributes.addAttributeNS(fAttributeQName, XMLSymbols.fCDATASymbol, null);
        } else {
            int oldLen = attributes.getLength();
            attrIndex = attributes.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);

            if (oldLen == attributes.getLength()) {
                reportFatalError("AttributeNotUnique",
                        new Object[]{fCurrentElement.rawname,
                                fAttributeQName.rawname});
            }
        }

        attributes.setValue(attrIndex, value,tmpStr);
        attributes.setSpecified(attrIndex, true);

        if (fAttributeQName.prefix != null) {
            attributes.setURI(attrIndex, fNamespaceContext.getURI(fAttributeQName.prefix));
        }

        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +"<<< scanAttribute()");
    } protected Driver createContentDriver() {
        return new NSContentDriver();
    } protected final class NSContentDriver
            extends ContentDriver {

        protected boolean scanRootElementHook()
        throws IOException, XNIException {

            reconfigurePipeline();
            if (scanStartElement()) {
                setScannerState(SCANNER_STATE_TRAILING_MISC);
                setDriver(fTrailingMiscDriver);
                return true;
            }
            return false;

        } private void reconfigurePipeline() {
            if (fNamespaces && fDTDValidator == null) {
                fBindNamespaces = true;
            }
            else if (fNamespaces && !fDTDValidator.hasGrammar() ) {
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