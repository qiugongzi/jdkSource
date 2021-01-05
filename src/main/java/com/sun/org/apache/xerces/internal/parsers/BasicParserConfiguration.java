


package com.sun.org.apache.xerces.internal.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;


public abstract class BasicParserConfiguration
    extends ParserConfigurationSettings
    implements XMLParserConfiguration {

    protected static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;


    protected static final String EXTERNAL_GENERAL_ENTITIES =
        Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;


    protected static final String EXTERNAL_PARAMETER_ENTITIES =
        Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;

    protected static final String XML_STRING =
        Constants.SAX_PROPERTY_PREFIX + Constants.XML_STRING_PROPERTY;


    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    protected static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    protected SymbolTable fSymbolTable;


    protected Locale fLocale;


    protected ArrayList fComponents;

    protected XMLDocumentHandler fDocumentHandler;


    protected XMLDTDHandler fDTDHandler;


    protected XMLDTDContentModelHandler fDTDContentModelHandler;


    protected XMLDocumentSource fLastComponent;

    protected BasicParserConfiguration() {
        this(null, null);
    } protected BasicParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null);
    } protected BasicParserConfiguration(SymbolTable symbolTable,
                                       XMLComponentManager parentSettings) {
        super(parentSettings);

        fComponents = new ArrayList();

        fFeatures = new HashMap();
        fProperties = new HashMap();

        final String[] recognizedFeatures = {
                PARSER_SETTINGS,
            VALIDATION,
            NAMESPACES,
            EXTERNAL_GENERAL_ENTITIES,
            EXTERNAL_PARAMETER_ENTITIES,
        };
        addRecognizedFeatures(recognizedFeatures);
        fFeatures.put(PARSER_SETTINGS, Boolean.TRUE);
        fFeatures.put(VALIDATION, Boolean.FALSE);
                fFeatures.put(NAMESPACES, Boolean.TRUE);
                fFeatures.put(EXTERNAL_GENERAL_ENTITIES, Boolean.TRUE);
                fFeatures.put(EXTERNAL_PARAMETER_ENTITIES, Boolean.TRUE);

        final String[] recognizedProperties = {
            XML_STRING,
            SYMBOL_TABLE,
            ERROR_HANDLER,
            ENTITY_RESOLVER,
        };
        addRecognizedProperties(recognizedProperties);

        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        fSymbolTable = symbolTable;
        fProperties.put(SYMBOL_TABLE, fSymbolTable);

    } protected void addComponent(XMLComponent component) {

        if (fComponents.contains(component)) {
            return;
        }
        fComponents.add(component);

        String[] recognizedFeatures = component.getRecognizedFeatures();
        addRecognizedFeatures(recognizedFeatures);

        String[] recognizedProperties = component.getRecognizedProperties();
        addRecognizedProperties(recognizedProperties);

        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; i++) {
                String featureId = recognizedFeatures[i];
                Boolean state = component.getFeatureDefault(featureId);
                if (state != null) {
                    super.setFeature(featureId, state.booleanValue());
                }
            }
        }
        if (recognizedProperties != null) {
            for (int i = 0; i < recognizedProperties.length; i++) {
                String propertyId = recognizedProperties[i];
                Object value = component.getPropertyDefault(propertyId);
                if (value != null) {
                    super.setProperty(propertyId, value);
                }
            }
        }

    } public abstract void parse(XMLInputSource inputSource)
        throws XNIException, IOException;


    public void setDocumentHandler(XMLDocumentHandler documentHandler) {
        fDocumentHandler = documentHandler;
        if (fLastComponent != null) {
            fLastComponent.setDocumentHandler(fDocumentHandler);
            if (fDocumentHandler !=null){
                fDocumentHandler.setDocumentSource(fLastComponent);
            }
        }
    } public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    } public void setDTDHandler(XMLDTDHandler dtdHandler) {
        fDTDHandler = dtdHandler;
    } public XMLDTDHandler getDTDHandler() {
        return fDTDHandler;
    } public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
        fDTDContentModelHandler = handler;
    } public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return fDTDContentModelHandler;
    } public void setEntityResolver(XMLEntityResolver resolver) {
        fProperties.put(ENTITY_RESOLVER, resolver);
    } public XMLEntityResolver getEntityResolver() {
        return (XMLEntityResolver)fProperties.get(ENTITY_RESOLVER);
    } public void setErrorHandler(XMLErrorHandler errorHandler) {
        fProperties.put(ERROR_HANDLER, errorHandler);
    } public XMLErrorHandler getErrorHandler() {
        return (XMLErrorHandler)fProperties.get(ERROR_HANDLER);
    } public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        int count = fComponents.size();
        for (int i = 0; i < count; i++) {
            XMLComponent c = (XMLComponent) fComponents.get(i);
            c.setFeature(featureId, state);
        }
        super.setFeature(featureId, state);

    } public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        int count = fComponents.size();
        for (int i = 0; i < count; i++) {
            XMLComponent c = (XMLComponent) fComponents.get(i);
            c.setProperty(propertyId, value);
        }

        super.setProperty(propertyId, value);

    } public void setLocale(Locale locale) throws XNIException {
        fLocale = locale;
    } public Locale getLocale() {
        return fLocale;
    } protected void reset() throws XNIException {

        int count = fComponents.size();
        for (int i = 0; i < count; i++) {
            XMLComponent c = (XMLComponent) fComponents.get(i);
            c.reset(this);
        }

    } protected PropertyState checkProperty(String propertyId)
        throws XMLConfigurationException {

        if (propertyId.startsWith(Constants.SAX_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.SAX_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.XML_STRING_PROPERTY.length() &&
                propertyId.endsWith(Constants.XML_STRING_PROPERTY)) {
                return PropertyState.NOT_SUPPORTED;
            }
        }

        return super.checkProperty(propertyId);

    } protected FeatureState checkFeature(String featureId)
        throws XMLConfigurationException {

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();

            if (suffixLength == Constants.PARSER_SETTINGS.length() &&
                featureId.endsWith(Constants.PARSER_SETTINGS)) {
                return FeatureState.NOT_SUPPORTED;
            }
        }

        return super.checkFeature(featureId);
     } }