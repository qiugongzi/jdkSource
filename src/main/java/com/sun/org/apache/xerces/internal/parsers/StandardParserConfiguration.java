


package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;


public class StandardParserConfiguration
    extends DTDConfiguration {

    protected static final String NORMALIZE_DATA =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;



    protected static final String SCHEMA_ELEMENT_DEFAULT =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT;



    protected static final String SCHEMA_AUGMENT_PSVI =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;



    protected static final String XMLSCHEMA_VALIDATION =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;


    protected static final String XMLSCHEMA_FULL_CHECKING =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;


    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.GENERATE_SYNTHETIC_ANNOTATIONS_FEATURE;


    protected static final String VALIDATE_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE;


    protected static final String HONOUR_ALL_SCHEMALOCATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.HONOUR_ALL_SCHEMALOCATIONS_FEATURE;


    protected static final String NAMESPACE_GROWTH =
        Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_GROWTH_FEATURE;


    protected static final String TOLERATE_DUPLICATES =
        Constants.XERCES_FEATURE_PREFIX + Constants.TOLERATE_DUPLICATES_FEATURE;

    protected static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;


    protected static final String SCHEMA_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;


    protected static final String SCHEMA_NONS_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;


    protected static final String SCHEMA_DV_FACTORY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_DV_FACTORY_PROPERTY;

    protected XMLSchemaValidator fSchemaValidator;

    public StandardParserConfiguration() {
        this(null, null, null);
    } public StandardParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } public StandardParserConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } public StandardParserConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool,
                                       XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        final String[] recognizedFeatures = {
            NORMALIZE_DATA,
            SCHEMA_ELEMENT_DEFAULT,
            SCHEMA_AUGMENT_PSVI,
            GENERATE_SYNTHETIC_ANNOTATIONS,
            VALIDATE_ANNOTATIONS,
            HONOUR_ALL_SCHEMALOCATIONS,
            NAMESPACE_GROWTH,
            TOLERATE_DUPLICATES,
            XMLSCHEMA_VALIDATION,
            XMLSCHEMA_FULL_CHECKING,
        };
        addRecognizedFeatures(recognizedFeatures);

        setFeature(SCHEMA_ELEMENT_DEFAULT, true);
        setFeature(NORMALIZE_DATA, true);
        setFeature(SCHEMA_AUGMENT_PSVI, true);
        setFeature(GENERATE_SYNTHETIC_ANNOTATIONS, false);
        setFeature(VALIDATE_ANNOTATIONS, false);
        setFeature(HONOUR_ALL_SCHEMALOCATIONS, false);
        setFeature(NAMESPACE_GROWTH, false);
        setFeature(TOLERATE_DUPLICATES, false);

        final String[] recognizedProperties = {
            SCHEMA_LOCATION,
            SCHEMA_NONS_LOCATION,
            SCHEMA_DV_FACTORY,
            };

                        addRecognizedProperties(recognizedProperties);

    } protected void configurePipeline() {
        super.configurePipeline();
        if ( getFeature(XMLSCHEMA_VALIDATION )) {
            if (fSchemaValidator == null) {
                fSchemaValidator = new XMLSchemaValidator();

                fProperties.put(SCHEMA_VALIDATOR, fSchemaValidator);
                addComponent(fSchemaValidator);
                 if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
                    XSMessageFormatter xmft = new XSMessageFormatter();
                    fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, xmft);
                }

            }
            fLastComponent = fSchemaValidator;
            fNamespaceBinder.setDocumentHandler(fSchemaValidator);

            fSchemaValidator.setDocumentHandler(fDocumentHandler);
            fSchemaValidator.setDocumentSource(fNamespaceBinder);
        }


    } protected FeatureState checkFeature(String featureId)
        throws XMLConfigurationException {

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();

            if (suffixLength == Constants.SCHEMA_VALIDATION_FEATURE.length() &&
                featureId.endsWith(Constants.SCHEMA_VALIDATION_FEATURE)) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == Constants.SCHEMA_FULL_CHECKING.length() &&
                featureId.endsWith(Constants.SCHEMA_FULL_CHECKING)) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == Constants.SCHEMA_NORMALIZED_VALUE.length() &&
                featureId.endsWith(Constants.SCHEMA_NORMALIZED_VALUE)) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == Constants.SCHEMA_ELEMENT_DEFAULT.length() &&
                featureId.endsWith(Constants.SCHEMA_ELEMENT_DEFAULT)) {
                return FeatureState.RECOGNIZED;
            }
        }

        return super.checkFeature(featureId);

    } protected PropertyState checkProperty(String propertyId)
        throws XMLConfigurationException {

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.SCHEMA_LOCATION.length() &&
                propertyId.endsWith(Constants.SCHEMA_LOCATION)) {
                return PropertyState.RECOGNIZED;
            }
            if (suffixLength == Constants.SCHEMA_NONS_LOCATION.length() &&
                propertyId.endsWith(Constants.SCHEMA_NONS_LOCATION)) {
                return PropertyState.RECOGNIZED;
            }
        }

        if (propertyId.startsWith(Constants.JAXP_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.JAXP_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.SCHEMA_SOURCE.length() &&
                propertyId.endsWith(Constants.SCHEMA_SOURCE)) {
                return PropertyState.RECOGNIZED;
            }
        }

        return super.checkProperty(propertyId);

    } }