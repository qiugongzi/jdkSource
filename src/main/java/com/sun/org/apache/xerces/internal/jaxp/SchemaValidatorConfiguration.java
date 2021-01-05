


package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;


final class SchemaValidatorConfiguration implements XMLComponentManager {

    private static final String SCHEMA_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;


    private static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    private static final String USE_GRAMMAR_POOL_ONLY =
        Constants.XERCES_FEATURE_PREFIX + Constants.USE_GRAMMAR_POOL_ONLY_FEATURE;


    private static final String PARSER_SETTINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;

    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    private static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    private static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    private final XMLComponentManager fParentComponentManager;


    private final XMLGrammarPool fGrammarPool;


    private final boolean fUseGrammarPoolOnly;


    private final ValidationManager fValidationManager;

    public SchemaValidatorConfiguration(XMLComponentManager parentManager,
            XSGrammarPoolContainer grammarContainer, ValidationManager validationManager) {
        fParentComponentManager = parentManager;
        fGrammarPool = grammarContainer.getGrammarPool();
        fUseGrammarPoolOnly = grammarContainer.isFullyComposed();
        fValidationManager = validationManager;
        try {
            XMLErrorReporter errorReporter = (XMLErrorReporter) fParentComponentManager.getProperty(ERROR_REPORTER);
            if (errorReporter != null) {
                errorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
            }
        }
        catch (XMLConfigurationException exc) {}
    }


    public boolean getFeature(String featureId)
            throws XMLConfigurationException {
        FeatureState state = getFeatureState(featureId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, featureId);
        }
        return state.state;
    }

    public FeatureState getFeatureState(String featureId) {
        if (PARSER_SETTINGS.equals(featureId)) {
            return fParentComponentManager.getFeatureState(featureId);
        }
        else if (VALIDATION.equals(featureId) || SCHEMA_VALIDATION.equals(featureId)) {
            return FeatureState.is(true);
        }
        else if (USE_GRAMMAR_POOL_ONLY.equals(featureId)) {
            return FeatureState.is(fUseGrammarPoolOnly);
        }
        return fParentComponentManager.getFeatureState(featureId);
    }

    public PropertyState getPropertyState(String propertyId) {
        if (XMLGRAMMAR_POOL.equals(propertyId)) {
            return PropertyState.is(fGrammarPool);
        }
        else if (VALIDATION_MANAGER.equals(propertyId)) {
            return PropertyState.is(fValidationManager);
        }
        return fParentComponentManager.getPropertyState(propertyId);
    }


    public Object getProperty(String propertyId)
            throws XMLConfigurationException {
        PropertyState state = getPropertyState(propertyId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, propertyId);
        }
        return state.state;
    }

    public boolean getFeature(String featureId, boolean defaultValue) {
        FeatureState state = getFeatureState(featureId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }

    public Object getProperty(String propertyId, Object defaultValue) {
        PropertyState state = getPropertyState(propertyId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }



}
