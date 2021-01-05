

package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;


public class XIncludeParserConfiguration extends XML11Configuration {

    private XIncludeHandler fXIncludeHandler;


    protected static final String ALLOW_UE_AND_NOTATION_EVENTS =
        Constants.SAX_FEATURE_PREFIX + Constants.ALLOW_DTD_EVENTS_AFTER_ENDDTD_FEATURE;


    protected static final String XINCLUDE_FIXUP_BASE_URIS =
        Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_FIXUP_BASE_URIS_FEATURE;


    protected static final String XINCLUDE_FIXUP_LANGUAGE =
        Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_FIXUP_LANGUAGE_FEATURE;


    protected static final String XINCLUDE_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XINCLUDE_HANDLER_PROPERTY;


    protected static final String NAMESPACE_CONTEXT =
        Constants.XERCES_PROPERTY_PREFIX + Constants.NAMESPACE_CONTEXT_PROPERTY;


    public XIncludeParserConfiguration() {
        this(null, null, null);
    } public XIncludeParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } public XIncludeParserConfiguration(
        SymbolTable symbolTable,
        XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } public XIncludeParserConfiguration(
        SymbolTable symbolTable,
        XMLGrammarPool grammarPool,
        XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        fXIncludeHandler = new XIncludeHandler();
        addCommonComponent(fXIncludeHandler);

        final String[] recognizedFeatures = {
            ALLOW_UE_AND_NOTATION_EVENTS,
            XINCLUDE_FIXUP_BASE_URIS,
            XINCLUDE_FIXUP_LANGUAGE
        };
        addRecognizedFeatures(recognizedFeatures);

        final String[] recognizedProperties =
            { XINCLUDE_HANDLER, NAMESPACE_CONTEXT };
        addRecognizedProperties(recognizedProperties);

        setFeature(ALLOW_UE_AND_NOTATION_EVENTS, true);
        setFeature(XINCLUDE_FIXUP_BASE_URIS, true);
        setFeature(XINCLUDE_FIXUP_LANGUAGE, true);

        setProperty(XINCLUDE_HANDLER, fXIncludeHandler);
        setProperty(NAMESPACE_CONTEXT, new XIncludeNamespaceSupport());
    } protected void configurePipeline() {
        super.configurePipeline();

        fDTDScanner.setDTDHandler(fDTDProcessor);
        fDTDProcessor.setDTDSource(fDTDScanner);
        fDTDProcessor.setDTDHandler(fXIncludeHandler);
        fXIncludeHandler.setDTDSource(fDTDProcessor);
                fXIncludeHandler.setDTDHandler(fDTDHandler);
        if (fDTDHandler != null) {
            fDTDHandler.setDTDSource(fXIncludeHandler);
        }

        XMLDocumentSource prev = null;
        if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
            prev = fSchemaValidator.getDocumentSource();
        }
        else {
            prev = fLastComponent;
            fLastComponent = fXIncludeHandler;
        }

         if (prev != null) {
            XMLDocumentHandler next = prev.getDocumentHandler();
            prev.setDocumentHandler(fXIncludeHandler);
            fXIncludeHandler.setDocumentSource(prev);
            if (next != null) {
                fXIncludeHandler.setDocumentHandler(next);
                next.setDocumentSource(fXIncludeHandler);
            }
         }
         else {
            setDocumentHandler(fXIncludeHandler);
         }

    } protected void configureXML11Pipeline() {
                super.configureXML11Pipeline();

        fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
                fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);
                fXML11DTDProcessor.setDTDHandler(fXIncludeHandler);
                fXIncludeHandler.setDTDSource(fXML11DTDProcessor);
                fXIncludeHandler.setDTDHandler(fDTDHandler);
                if (fDTDHandler != null) {
                        fDTDHandler.setDTDSource(fXIncludeHandler);
                }

                XMLDocumentSource prev = null;
                if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
                        prev = fSchemaValidator.getDocumentSource();
                }
                else {
                        prev = fLastComponent;
                        fLastComponent = fXIncludeHandler;
                }

                XMLDocumentHandler next = prev.getDocumentHandler();
                prev.setDocumentHandler(fXIncludeHandler);
                fXIncludeHandler.setDocumentSource(prev);
                if (next != null) {
                        fXIncludeHandler.setDocumentHandler(next);
                        next.setDocumentSource(fXIncludeHandler);
                }

        } public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        if (propertyId.equals(XINCLUDE_HANDLER)) {
        }

        super.setProperty(propertyId, value);
    } }
