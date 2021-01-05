


package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;


final class XMLSchemaValidatorComponentManager extends ParserConfigurationSettings implements
        XMLComponentManager {

    private static final String SCHEMA_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;


    private static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    private static final String SCHEMA_ELEMENT_DEFAULT =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT;


    private static final String USE_GRAMMAR_POOL_ONLY =
        Constants.XERCES_FEATURE_PREFIX + Constants.USE_GRAMMAR_POOL_ONLY_FEATURE;

    private static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    private static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    private static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    private static final String NAMESPACE_CONTEXT =
        Constants.XERCES_PROPERTY_PREFIX + Constants.NAMESPACE_CONTEXT_PROPERTY;


    private static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;


    private static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    private static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    private static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    private static final String LOCALE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.LOCALE_PROPERTY;

    private boolean _isSecureMode = false;


    private boolean fConfigUpdated = true;


    private boolean fUseGrammarPoolOnly;


    private final HashMap fComponents = new HashMap();

    private XMLEntityManager fEntityManager;


    private XMLErrorReporter fErrorReporter;


    private NamespaceContext fNamespaceContext;


    private XMLSchemaValidator fSchemaValidator;


    private ValidationManager fValidationManager;

    private final HashMap fInitFeatures = new HashMap();


    private final HashMap fInitProperties = new HashMap();


    private XMLSecurityManager fInitSecurityManager;


    private final XMLSecurityPropertyManager fSecurityPropertyMgr;

    private ErrorHandler fErrorHandler = null;


    private LSResourceResolver fResourceResolver = null;


    private Locale fLocale = null;


    public XMLSchemaValidatorComponentManager(XSGrammarPoolContainer grammarContainer) {
        fEntityManager = new XMLEntityManager();
        fComponents.put(ENTITY_MANAGER, fEntityManager);

        fErrorReporter = new XMLErrorReporter();
        fComponents.put(ERROR_REPORTER, fErrorReporter);

        fNamespaceContext = new NamespaceSupport();
        fComponents.put(NAMESPACE_CONTEXT, fNamespaceContext);

        fSchemaValidator = new XMLSchemaValidator();
        fComponents.put(SCHEMA_VALIDATOR, fSchemaValidator);

        fValidationManager = new ValidationManager();
        fComponents.put(VALIDATION_MANAGER, fValidationManager);

        fComponents.put(ENTITY_RESOLVER, null);
        fComponents.put(ERROR_HANDLER, null);

        fComponents.put(SYMBOL_TABLE, new SymbolTable());

        fComponents.put(XMLGRAMMAR_POOL, grammarContainer.getGrammarPool());
        fUseGrammarPoolOnly = grammarContainer.isFullyComposed();

        fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());

        addRecognizedParamsAndSetDefaults(fEntityManager, grammarContainer);
        addRecognizedParamsAndSetDefaults(fErrorReporter, grammarContainer);
        addRecognizedParamsAndSetDefaults(fSchemaValidator, grammarContainer);

        boolean secureProcessing = grammarContainer.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING);
        if (System.getSecurityManager() != null) {
            _isSecureMode = true;
            secureProcessing = true;
        }

        fInitSecurityManager = (XMLSecurityManager)
                grammarContainer.getProperty(SECURITY_MANAGER);
        if (fInitSecurityManager != null ) {
            fInitSecurityManager.setSecureProcessing(secureProcessing);
        } else {
            fInitSecurityManager = new XMLSecurityManager(secureProcessing);
        }

        setProperty(SECURITY_MANAGER, fInitSecurityManager);

        fSecurityPropertyMgr = (XMLSecurityPropertyManager)
                grammarContainer.getProperty(Constants.XML_SECURITY_PROPERTY_MANAGER);
        setProperty(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
    }


    public FeatureState getFeatureState(String featureId)
            throws XMLConfigurationException {
        if (PARSER_SETTINGS.equals(featureId)) {
            return FeatureState.is(fConfigUpdated);
        }
        else if (VALIDATION.equals(featureId) || SCHEMA_VALIDATION.equals(featureId)) {
            return FeatureState.is(true);
        }
        else if (USE_GRAMMAR_POOL_ONLY.equals(featureId)) {
            return FeatureState.is(fUseGrammarPoolOnly);
        }
        else if (XMLConstants.FEATURE_SECURE_PROCESSING.equals(featureId)) {
            return FeatureState.is(fInitSecurityManager.isSecureProcessing());
        }
        else if (SCHEMA_ELEMENT_DEFAULT.equals(featureId)) {
            return FeatureState.is(true); }
        return super.getFeatureState(featureId);
    }


    public void setFeature(String featureId, boolean value) throws XMLConfigurationException {
        if (PARSER_SETTINGS.equals(featureId)) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        else if (value == false && (VALIDATION.equals(featureId) || SCHEMA_VALIDATION.equals(featureId))) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        else if (USE_GRAMMAR_POOL_ONLY.equals(featureId) && value != fUseGrammarPoolOnly) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        if (XMLConstants.FEATURE_SECURE_PROCESSING.equals(featureId)) {
            if (_isSecureMode && !value) {
                throw new XMLConfigurationException(Status.NOT_ALLOWED, XMLConstants.FEATURE_SECURE_PROCESSING);
            }

            fInitSecurityManager.setSecureProcessing(value);
            setProperty(SECURITY_MANAGER, fInitSecurityManager);

            if (value && Constants.IS_JDK8_OR_ABOVE) {
                fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD,
                        XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);
                fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA,
                        XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);
                setProperty(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
            }

            return;
        }
        fConfigUpdated = true;
        fEntityManager.setFeature(featureId, value);
        fErrorReporter.setFeature(featureId, value);
        fSchemaValidator.setFeature(featureId, value);
        if (!fInitFeatures.containsKey(featureId)) {
            boolean current = super.getFeature(featureId);
            fInitFeatures.put(featureId, current ? Boolean.TRUE : Boolean.FALSE);
        }
        super.setFeature(featureId, value);
    }


    public PropertyState getPropertyState(String propertyId)
            throws XMLConfigurationException {
        if (LOCALE.equals(propertyId)) {
            return PropertyState.is(getLocale());
        }
        final Object component = fComponents.get(propertyId);
        if (component != null) {
            return PropertyState.is(component);
        }
        else if (fComponents.containsKey(propertyId)) {
            return PropertyState.is(null);
        }
        return super.getPropertyState(propertyId);
    }


    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        if ( ENTITY_MANAGER.equals(propertyId) || ERROR_REPORTER.equals(propertyId) ||
             NAMESPACE_CONTEXT.equals(propertyId) || SCHEMA_VALIDATOR.equals(propertyId) ||
             SYMBOL_TABLE.equals(propertyId) || VALIDATION_MANAGER.equals(propertyId) ||
             XMLGRAMMAR_POOL.equals(propertyId)) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, propertyId);
        }
        fConfigUpdated = true;
        fEntityManager.setProperty(propertyId, value);
        fErrorReporter.setProperty(propertyId, value);
        fSchemaValidator.setProperty(propertyId, value);
        if (ENTITY_RESOLVER.equals(propertyId) || ERROR_HANDLER.equals(propertyId) ||
                SECURITY_MANAGER.equals(propertyId)) {
            fComponents.put(propertyId, value);
            return;
        }
        else if (LOCALE.equals(propertyId)) {
            setLocale((Locale) value);
            fComponents.put(propertyId, value);
            return;
        }
        if (fInitSecurityManager == null ||
                !fInitSecurityManager.setLimit(propertyId, XMLSecurityManager.State.APIPROPERTY, value)) {
            if (fSecurityPropertyMgr == null ||
                    !fSecurityPropertyMgr.setValue(propertyId, XMLSecurityPropertyManager.State.APIPROPERTY, value)) {
                if (!fInitProperties.containsKey(propertyId)) {
                    fInitProperties.put(propertyId, super.getProperty(propertyId));
                }
                super.setProperty(propertyId, value);
            }
        }
    }


    public void addRecognizedParamsAndSetDefaults(XMLComponent component, XSGrammarPoolContainer grammarContainer) {

        final String[] recognizedFeatures = component.getRecognizedFeatures();
        addRecognizedFeatures(recognizedFeatures);

        final String[] recognizedProperties = component.getRecognizedProperties();
        addRecognizedProperties(recognizedProperties);

        setFeatureDefaults(component, recognizedFeatures, grammarContainer);
        setPropertyDefaults(component, recognizedProperties);
    }


    public void reset() throws XNIException {
        fNamespaceContext.reset();
        fValidationManager.reset();
        fEntityManager.reset(this);
        fErrorReporter.reset(this);
        fSchemaValidator.reset(this);
        fConfigUpdated = false;
    }

    void setErrorHandler(ErrorHandler errorHandler) {
        fErrorHandler = errorHandler;
        setProperty(ERROR_HANDLER, (errorHandler != null) ? new ErrorHandlerWrapper(errorHandler) :
                new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
    }

    ErrorHandler getErrorHandler() {
        return fErrorHandler;
    }

    void setResourceResolver(LSResourceResolver resourceResolver) {
        fResourceResolver = resourceResolver;
        setProperty(ENTITY_RESOLVER, new DOMEntityResolverWrapper(resourceResolver));
    }

    LSResourceResolver getResourceResolver() {
        return fResourceResolver;
    }

    void setLocale(Locale locale) {
        fLocale = locale;
        fErrorReporter.setLocale(locale);
    }

    Locale getLocale() {
        return fLocale;
    }


    void restoreInitialState() {
        fConfigUpdated = true;

        fComponents.put(ENTITY_RESOLVER, null);
        fComponents.put(ERROR_HANDLER, null);

        setLocale(null);
        fComponents.put(LOCALE, null);

        fComponents.put(SECURITY_MANAGER, fInitSecurityManager);

        setLocale(null);
        fComponents.put(LOCALE, null);

        if (!fInitFeatures.isEmpty()) {
            Iterator iter = fInitFeatures.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                boolean value = ((Boolean) entry.getValue()).booleanValue();
                super.setFeature(name, value);
            }
            fInitFeatures.clear();
        }
        if (!fInitProperties.isEmpty()) {
            Iterator iter = fInitProperties.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();
                super.setProperty(name, value);
            }
            fInitProperties.clear();
        }
    }


    private void setFeatureDefaults(final XMLComponent component,
            final String [] recognizedFeatures, XSGrammarPoolContainer grammarContainer) {
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                String featureId = recognizedFeatures[i];
                Boolean state = grammarContainer.getFeature(featureId);
                if (state == null) {
                    state = component.getFeatureDefault(featureId);
                }
                if (state != null) {
                    if (!fFeatures.containsKey(featureId)) {
                        fFeatures.put(featureId, state);
                        fConfigUpdated = true;
                    }
                }
            }
        }
    }


    private void setPropertyDefaults(final XMLComponent component, final String [] recognizedProperties) {
        if (recognizedProperties != null) {
            for (int i = 0; i < recognizedProperties.length; ++i) {
                String propertyId = recognizedProperties[i];
                Object value = component.getPropertyDefault(propertyId);
                if (value != null) {
                    if (!fProperties.containsKey(propertyId)) {
                        fProperties.put(propertyId, value);
                        fConfigUpdated = true;
                    }
                }
            }
        }
    }

}