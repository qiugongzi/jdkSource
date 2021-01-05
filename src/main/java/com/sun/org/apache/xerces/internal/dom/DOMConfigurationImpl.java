


package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSResourceResolver;




public class DOMConfigurationImpl extends ParserConfigurationSettings
    implements XMLParserConfiguration, DOMConfiguration {

    protected static final String XERCES_VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    protected static final String XERCES_NAMESPACES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;

    protected static final String SCHEMA =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;

    protected static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;

    protected static final String DYNAMIC_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.DYNAMIC_VALIDATION_FEATURE;

    protected static final String NORMALIZE_DATA =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;


    protected static final String SEND_PSVI =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;

    protected final static String DTD_VALIDATOR_FACTORY_PROPERTY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.DATATYPE_VALIDATOR_FACTORY_PROPERTY;


    protected static final String NAMESPACE_GROWTH =
        Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_GROWTH_FEATURE;

    protected static final String TOLERATE_DUPLICATES =
        Constants.XERCES_FEATURE_PREFIX + Constants.TOLERATE_DUPLICATES_FEATURE;

    protected static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String XML_STRING =
        Constants.SAX_PROPERTY_PREFIX + Constants.XML_STRING_PROPERTY;


    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String GRAMMAR_POOL =
    Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    protected static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    protected static final String JAXP_SCHEMA_LANGUAGE =
    Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE;


    protected static final String JAXP_SCHEMA_SOURCE =
    Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE;

    protected static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    protected static final String SCHEMA_DV_FACTORY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_DV_FACTORY_PROPERTY;


    private static final String SECURITY_MANAGER = Constants.SECURITY_MANAGER;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    XMLDocumentHandler fDocumentHandler;


    protected short features = 0;

    protected final static short NAMESPACES          = 0x1<<0;
    protected final static short DTNORMALIZATION     = 0x1<<1;
    protected final static short ENTITIES            = 0x1<<2;
    protected final static short CDATA               = 0x1<<3;
    protected final static short SPLITCDATA          = 0x1<<4;
    protected final static short COMMENTS            = 0x1<<5;
    protected final static short VALIDATE            = 0x1<<6;
    protected final static short PSVI                = 0x1<<7;
    protected final static short WELLFORMED          = 0x1<<8;
    protected final static short NSDECL              = 0x1<<9;

    protected final static short INFOSET_TRUE_PARAMS = NAMESPACES | COMMENTS | WELLFORMED | NSDECL;
    protected final static short INFOSET_FALSE_PARAMS = ENTITIES | DTNORMALIZATION | CDATA;
    protected final static short INFOSET_MASK = INFOSET_TRUE_PARAMS | INFOSET_FALSE_PARAMS;

    protected SymbolTable fSymbolTable;


    protected ArrayList fComponents;

    protected ValidationManager fValidationManager;


    protected Locale fLocale;


    protected XMLErrorReporter fErrorReporter;

    protected final DOMErrorHandlerWrapper fErrorHandlerWrapper =
                new DOMErrorHandlerWrapper();

    private DOMStringList fRecognizedParameters;


    protected DOMConfigurationImpl() {
        this(null, null);
    } protected DOMConfigurationImpl(SymbolTable symbolTable) {
        this(symbolTable, null);
    } protected DOMConfigurationImpl(SymbolTable symbolTable,
                                    XMLComponentManager parentSettings) {
        super(parentSettings);


        fFeatures = new HashMap();
        fProperties = new HashMap();

        final String[] recognizedFeatures = {
            XERCES_VALIDATION,
            XERCES_NAMESPACES,
            SCHEMA,
            SCHEMA_FULL_CHECKING,
            DYNAMIC_VALIDATION,
            NORMALIZE_DATA,
            SEND_PSVI,
            NAMESPACE_GROWTH,
            TOLERATE_DUPLICATES,
            JdkXmlUtils.OVERRIDE_PARSER
        };
        addRecognizedFeatures(recognizedFeatures);

        setFeature(XERCES_VALIDATION, false);
        setFeature(SCHEMA, false);
        setFeature(SCHEMA_FULL_CHECKING, false);
        setFeature(DYNAMIC_VALIDATION, false);
        setFeature(NORMALIZE_DATA, false);
        setFeature(XERCES_NAMESPACES, true);
        setFeature(SEND_PSVI, true);
        setFeature(NAMESPACE_GROWTH, false);
        setFeature(JdkXmlUtils.OVERRIDE_PARSER, JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);

        final String[] recognizedProperties = {
            XML_STRING,
            SYMBOL_TABLE,
            ERROR_HANDLER,
            ENTITY_RESOLVER,
            ERROR_REPORTER,
            ENTITY_MANAGER,
            VALIDATION_MANAGER,
            GRAMMAR_POOL,
            JAXP_SCHEMA_SOURCE,
            JAXP_SCHEMA_LANGUAGE,
            DTD_VALIDATOR_FACTORY_PROPERTY,
            SCHEMA_DV_FACTORY,
            SECURITY_MANAGER,
            XML_SECURITY_PROPERTY_MANAGER
        };
        addRecognizedProperties(recognizedProperties);

        features |= NAMESPACES;
        features |= ENTITIES;
        features |= COMMENTS;
        features |= CDATA;
        features |= SPLITCDATA;
        features |= WELLFORMED;
        features |= NSDECL;

        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        fSymbolTable = symbolTable;

        fComponents = new ArrayList();

        setProperty(SYMBOL_TABLE, fSymbolTable);
        fErrorReporter = new XMLErrorReporter();
        setProperty(ERROR_REPORTER, fErrorReporter);
        addComponent(fErrorReporter);

        setProperty(DTD_VALIDATOR_FACTORY_PROPERTY, DTDDVFactory.getInstance());

        XMLEntityManager manager =  new XMLEntityManager();
        setProperty(ENTITY_MANAGER, manager);
        addComponent(manager);

        fValidationManager = createValidationManager();
        setProperty(VALIDATION_MANAGER, fValidationManager);

        setProperty(SECURITY_MANAGER, new XMLSecurityManager(true));

        setProperty(Constants.XML_SECURITY_PROPERTY_MANAGER,
                new XMLSecurityPropertyManager());

        if (fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN) == null) {
            XMLMessageFormatter xmft = new XMLMessageFormatter();
            fErrorReporter.putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, xmft);
            fErrorReporter.putMessageFormatter(XMLMessageFormatter.XMLNS_DOMAIN, xmft);
        }

        if (fErrorReporter.getMessageFormatter("http:MessageFormatter xmft = null;
            try {
               xmft = (MessageFormatter)(
                    ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter", true));
            } catch (Exception exception){
            }

             if (xmft !=  null) {
                 fErrorReporter.putMessageFormatter("http:}
        }


        try {
            setLocale(Locale.getDefault());
        }
        catch (XNIException e) {
            }


    } public void parse(XMLInputSource inputSource)
        throws XNIException, IOException{
        }


    public void setDocumentHandler(XMLDocumentHandler documentHandler) {
        fDocumentHandler = documentHandler;
    } public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    } public void setDTDHandler(XMLDTDHandler dtdHandler) {
        } public XMLDTDHandler getDTDHandler() {
        return null;
    } public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
        } public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return null;
    } public void setEntityResolver(XMLEntityResolver resolver) {
        if (resolver !=null) {
            fProperties.put(ENTITY_RESOLVER, resolver);
        }
    } public XMLEntityResolver getEntityResolver() {
        return (XMLEntityResolver)fProperties.get(ENTITY_RESOLVER);
    } public void setErrorHandler(XMLErrorHandler errorHandler) {
        if (errorHandler != null) {
            fProperties.put(ERROR_HANDLER, errorHandler);
        }
    } public XMLErrorHandler getErrorHandler() {
        return (XMLErrorHandler)fProperties.get(ERROR_HANDLER);
    } public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        super.setFeature(featureId, state);

    } public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        super.setProperty(propertyId, value);

    } public void setLocale(Locale locale) throws XNIException {
        fLocale = locale;
        fErrorReporter.setLocale(locale);

    } public Locale getLocale() {
        return fLocale;
    } public void setParameter(String name, Object value) throws DOMException {
        boolean found = true;

        if(value instanceof Boolean){
                        boolean state = ((Boolean)value).booleanValue();

            if (name.equalsIgnoreCase(Constants.DOM_COMMENTS)) {
                features = (short) (state ? features | COMMENTS : features & ~COMMENTS);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)) {
                setFeature(NORMALIZE_DATA, state);
                features =
                    (short) (state ? features | DTNORMALIZATION : features & ~DTNORMALIZATION);
                if (state) {
                    features = (short) (features | VALIDATE);
                }
            }
            else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACES)) {
                features = (short) (state ? features | NAMESPACES : features & ~NAMESPACES);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)) {
                features = (short) (state ? features | CDATA : features & ~CDATA);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_ENTITIES)) {
                features = (short) (state ? features | ENTITIES : features & ~ENTITIES);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)) {
                features = (short) (state ? features | SPLITCDATA : features & ~SPLITCDATA);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_VALIDATE)) {
                features = (short) (state ? features | VALIDATE : features & ~VALIDATE);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_WELLFORMED)) {
                features = (short) (state ? features | WELLFORMED : features & ~WELLFORMED );
            }
            else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)) {
                features = (short) (state ? features | NSDECL : features & ~NSDECL);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_INFOSET)) {
                if (state) {
                    features = (short) (features | INFOSET_TRUE_PARAMS);
                    features = (short) (features & ~INFOSET_FALSE_PARAMS);
                    setFeature(NORMALIZE_DATA, false);
                }
            }
            else if (name.equalsIgnoreCase(Constants.DOM_NORMALIZE_CHARACTERS)
                    || name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                    || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                    || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                    ) {
                if (state) { String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            }
                        else if ( name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)) {
                if (!state) { String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                   throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            }
            else if (name.equalsIgnoreCase(SEND_PSVI) ){
                if (!state) { String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            }
            else if (name.equalsIgnoreCase(Constants.DOM_PSVI)){
                  features = (short) (state ? features | PSVI : features & ~PSVI);
            }
            else {
                found = false;

            }

        }

                if (!found || !(value instanceof Boolean))  { found = true;

            if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER)) {
                if (value instanceof DOMErrorHandler || value == null) {
                    fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)value);
                    setErrorHandler(fErrorHandlerWrapper);
                }

                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }
            }
            else if (name.equalsIgnoreCase(Constants.DOM_RESOURCE_RESOLVER)) {
                if (value instanceof LSResourceResolver || value == null) {
                    try {
                        setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver) value));
                    }
                    catch (XMLConfigurationException e) {}
                }
                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }

            }
            else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_LOCATION)) {
                if (value instanceof String || value == null) {
                    try {
                        setProperty(
                            Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE,
                            value);
                    }
                    catch (XMLConfigurationException e) {}
                }
                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }

            }
            else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_TYPE)) {
                if (value instanceof String || value == null) {
                    try {
                        if (value == null) {
                            setProperty(
                                Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE,
                                null);
                        }
                        else if (value.equals(Constants.NS_XMLSCHEMA)) {
                            setProperty(
                                Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE,
                                Constants.NS_XMLSCHEMA);
                        }
                        else if (value.equals(Constants.NS_DTD)) {
                            setProperty(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE,
                                                Constants.NS_DTD);
                        }
                    }
                    catch (XMLConfigurationException e) {}
                }
                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }

            }
            else if (name.equalsIgnoreCase(SYMBOL_TABLE)){
                if (value instanceof SymbolTable){
                    setProperty(SYMBOL_TABLE, value);
                }
                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }
            }
            else if (name.equalsIgnoreCase (GRAMMAR_POOL)){
                if (value instanceof XMLGrammarPool){
                    setProperty(GRAMMAR_POOL, value);
                }
                else {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "TYPE_MISMATCH_ERR",
                            new Object[] { name });
                    throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
                }

            }
            else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_FOUND",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            }
        }

    }



        public Object getParameter(String name) throws DOMException {

                if (name.equalsIgnoreCase(Constants.DOM_COMMENTS)) {
                        return ((features & COMMENTS) != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACES)) {
                        return (features & NAMESPACES) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)) {
                        return (features & DTNORMALIZATION) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)) {
                        return (features & CDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_ENTITIES)) {
                        return (features & ENTITIES) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)) {
                        return (features & SPLITCDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_VALIDATE)) {
                        return (features & VALIDATE) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_WELLFORMED)) {
                        return (features & WELLFORMED) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)) {
                    return (features & NSDECL) != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_INFOSET)) {
                        return (features & INFOSET_MASK) == INFOSET_TRUE_PARAMS ? Boolean.TRUE : Boolean.FALSE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_NORMALIZE_CHARACTERS)
                                || name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                                || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                                || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                ) {
                        return Boolean.FALSE;
                }
        else if (name.equalsIgnoreCase(SEND_PSVI)) {
            return Boolean.TRUE;
        }
        else if (name.equalsIgnoreCase(Constants.DOM_PSVI)) {
            return (features & PSVI) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)) {
                        return Boolean.TRUE;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER)) {
            return fErrorHandlerWrapper.getErrorHandler();
                }
                else if (name.equalsIgnoreCase(Constants.DOM_RESOURCE_RESOLVER)) {
                        XMLEntityResolver entityResolver = getEntityResolver();
                        if (entityResolver != null && entityResolver instanceof DOMEntityResolverWrapper) {
                                return ((DOMEntityResolverWrapper) entityResolver).getEntityResolver();
                        }
                        return null;
                }
                else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_TYPE)) {
                        return getProperty(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE);
                }
                else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_LOCATION)) {
                        return getProperty(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE);
                }
        else if (name.equalsIgnoreCase(SYMBOL_TABLE)){
            return getProperty(SYMBOL_TABLE);
        }
        else if (name.equalsIgnoreCase(GRAMMAR_POOL)){
            return getProperty(GRAMMAR_POOL);
        }
                else {
                        String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "FEATURE_NOT_FOUND",
                                        new Object[] { name });
                        throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
                }

        }


        public boolean canSetParameter(String name, Object value) {

        if (value == null){
            return true ;
        }
        if( value instanceof Boolean ){
            if (name.equalsIgnoreCase(Constants.DOM_COMMENTS)
                || name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)
                || name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)
                || name.equalsIgnoreCase(Constants.DOM_ENTITIES)
                || name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)
                || name.equalsIgnoreCase(Constants.DOM_NAMESPACES)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE)
                || name.equalsIgnoreCase(Constants.DOM_WELLFORMED)
                || name.equalsIgnoreCase(Constants.DOM_INFOSET)
                || name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)
                ) {
                return true;
            }else if (
                name.equalsIgnoreCase(Constants.DOM_NORMALIZE_CHARACTERS)
                    || name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                    || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                    || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                    ) {
                    return (value.equals(Boolean.TRUE)) ? false : true;
            }else if( name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)
                    || name.equalsIgnoreCase(SEND_PSVI)
                    ) {
                    return (value.equals(Boolean.TRUE)) ? true : false;
            }else {
                return false ;
            }
        }
                else if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER)) {
            return (value instanceof DOMErrorHandler) ? true : false ;
        }
        else if (name.equalsIgnoreCase(Constants.DOM_RESOURCE_RESOLVER)) {
            return (value instanceof LSResourceResolver) ? true : false ;
        }
        else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_LOCATION)) {
            return (value instanceof String) ? true : false ;
        }
        else if (name.equalsIgnoreCase(Constants.DOM_SCHEMA_TYPE)) {
            return ( (value instanceof String) && value.equals(Constants.NS_XMLSCHEMA) ) ? true : false ;
        }
        else if (name.equalsIgnoreCase(SYMBOL_TABLE)){
            return (value instanceof SymbolTable) ? true : false ;
        }
        else if (name.equalsIgnoreCase (GRAMMAR_POOL)){
            return (value instanceof XMLGrammarPool) ? true : false ;
        }
        else {
            return false ;
        }

        } public DOMStringList getParameterNames() {
        if (fRecognizedParameters == null){
                        Vector parameters = new Vector();

                        parameters.add(Constants.DOM_COMMENTS);
                        parameters.add(Constants.DOM_DATATYPE_NORMALIZATION);
                        parameters.add(Constants.DOM_CDATA_SECTIONS);
                        parameters.add(Constants.DOM_ENTITIES);
                        parameters.add(Constants.DOM_SPLIT_CDATA);
                        parameters.add(Constants.DOM_NAMESPACES);
                        parameters.add(Constants.DOM_VALIDATE);

                        parameters.add(Constants.DOM_INFOSET);
                        parameters.add(Constants.DOM_NORMALIZE_CHARACTERS);
                        parameters.add(Constants.DOM_CANONICAL_FORM);
                        parameters.add(Constants.DOM_VALIDATE_IF_SCHEMA);
                        parameters.add(Constants.DOM_CHECK_CHAR_NORMALIZATION);
                        parameters.add(Constants.DOM_WELLFORMED);

                        parameters.add(Constants.DOM_NAMESPACE_DECLARATIONS);
                        parameters.add(Constants.DOM_ELEMENT_CONTENT_WHITESPACE);

                        parameters.add(Constants.DOM_ERROR_HANDLER);
                        parameters.add(Constants.DOM_SCHEMA_TYPE);
                        parameters.add(Constants.DOM_SCHEMA_LOCATION);
                        parameters.add(Constants.DOM_RESOURCE_RESOLVER);

                        parameters.add(GRAMMAR_POOL);
                        parameters.add(SYMBOL_TABLE);
                        parameters.add(SEND_PSVI);

                        fRecognizedParameters = new DOMStringListImpl(parameters);

        }

        return fRecognizedParameters;
    }protected void reset() throws XNIException {

        if (fValidationManager != null)
            fValidationManager.reset();

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

    } protected void addComponent(XMLComponent component) {

        if (fComponents.contains(component)) {
            return;
        }
        fComponents.add(component);

        String[] recognizedFeatures = component.getRecognizedFeatures();
        addRecognizedFeatures(recognizedFeatures);

        String[] recognizedProperties = component.getRecognizedProperties();
        addRecognizedProperties(recognizedProperties);

    } protected ValidationManager createValidationManager(){
        return new ValidationManager();
    }

}