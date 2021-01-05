


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.XMLConstants;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;



public class XMLSchemaLoader implements XMLGrammarLoader, XMLComponent,
XSLoader, DOMConfiguration {

    protected static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;


    protected static final String CONTINUE_AFTER_FATAL_ERROR =
        Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE;


    protected static final String ALLOW_JAVA_ENCODINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;


    protected static final String STANDARD_URI_CONFORMANT_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.STANDARD_URI_CONFORMANT_FEATURE;


    protected static final String VALIDATE_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE;


    protected static final String DISALLOW_DOCTYPE =
        Constants.XERCES_FEATURE_PREFIX + Constants.DISALLOW_DOCTYPE_DECL_FEATURE;


    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.GENERATE_SYNTHETIC_ANNOTATIONS_FEATURE;


    protected static final String HONOUR_ALL_SCHEMALOCATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.HONOUR_ALL_SCHEMALOCATIONS_FEATURE;

    protected static final String AUGMENT_PSVI =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;

    protected static final String PARSER_SETTINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;


    protected static final String NAMESPACE_GROWTH =
        Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_GROWTH_FEATURE;


    protected static final String TOLERATE_DUPLICATES =
        Constants.XERCES_FEATURE_PREFIX + Constants.TOLERATE_DUPLICATES_FEATURE;


    protected static final String SCHEMA_DV_FACTORY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_DV_FACTORY_PROPERTY;

    protected static final String OVERRIDE_PARSER = JdkXmlUtils.OVERRIDE_PARSER;

    private static final String[] RECOGNIZED_FEATURES = {
        SCHEMA_FULL_CHECKING,
        AUGMENT_PSVI,
        CONTINUE_AFTER_FATAL_ERROR,
        ALLOW_JAVA_ENCODINGS,
        STANDARD_URI_CONFORMANT_FEATURE,
        DISALLOW_DOCTYPE,
        GENERATE_SYNTHETIC_ANNOTATIONS,
        VALIDATE_ANNOTATIONS,
        HONOUR_ALL_SCHEMALOCATIONS,
        NAMESPACE_GROWTH,
        TOLERATE_DUPLICATES,
        OVERRIDE_PARSER
    };

    public static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    public static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    public static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    public static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    protected static final String SCHEMA_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;


    protected static final String SCHEMA_NONS_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;


    protected static final String JAXP_SCHEMA_SOURCE =
        Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE;

    protected static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    protected static final String LOCALE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.LOCALE_PROPERTY;

    protected static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;


    public static final String ACCESS_EXTERNAL_DTD = XMLConstants.ACCESS_EXTERNAL_DTD;


    public static final String ACCESS_EXTERNAL_SCHEMA = XMLConstants.ACCESS_EXTERNAL_SCHEMA;

    private static final String [] RECOGNIZED_PROPERTIES = {
        ENTITY_MANAGER,
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ERROR_HANDLER,
        ENTITY_RESOLVER,
        XMLGRAMMAR_POOL,
        SCHEMA_LOCATION,
        SCHEMA_NONS_LOCATION,
        JAXP_SCHEMA_SOURCE,
        SECURITY_MANAGER,
        LOCALE,
        SCHEMA_DV_FACTORY,
        XML_SECURITY_PROPERTY_MANAGER
    };

    private ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
    private SymbolTable fSymbolTable = null;
    private XMLErrorReporter fErrorReporter = new XMLErrorReporter ();
    private XMLEntityManager fEntityManager = null;
    private XMLEntityResolver fUserEntityResolver = null;
    private XMLGrammarPool fGrammarPool = null;
    private String fExternalSchemas = null;
    private String fExternalNoNSSchema = null;
    private Object fJAXPSource = null;
    private boolean fIsCheckedFully = false;
    private boolean fJAXPProcessed = false;
    private boolean fSettingsChanged = true;

    private XSDHandler fSchemaHandler;
    private XSGrammarBucket fGrammarBucket;
    private XSDeclarationPool fDeclPool = null;
    private SubstitutionGroupHandler fSubGroupHandler;
    private final CMNodeFactory fNodeFactory = new CMNodeFactory(); private CMBuilder fCMBuilder;
    private XSDDescription fXSDDescription = new XSDDescription();
    private String faccessExternalSchema = Constants.EXTERNAL_ACCESS_DEFAULT;

    private Map fJAXPCache;
    private Locale fLocale = Locale.getDefault();

    private DOMStringList fRecognizedParameters = null;


    private DOMErrorHandlerWrapper fErrorHandler = null;


    private DOMEntityResolverWrapper fResourceResolver = null;

    public XMLSchemaLoader() {
        this( new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }

    public XMLSchemaLoader(SymbolTable symbolTable) {
        this( symbolTable, null, new XMLEntityManager(), null, null, null);
    }


    XMLSchemaLoader(XMLErrorReporter errorReporter, XSGrammarBucket grammarBucket,
            SubstitutionGroupHandler sHandler, CMBuilder builder) {
        this(null, errorReporter, null, grammarBucket, sHandler, builder);
    }

    XMLSchemaLoader(SymbolTable symbolTable, XMLErrorReporter errorReporter,
            XMLEntityManager entityResolver, XSGrammarBucket grammarBucket,
            SubstitutionGroupHandler sHandler, CMBuilder builder) {

        fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
        fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null){
            fLoaderConfig.setProperty(SYMBOL_TABLE, symbolTable);
        }

        if(errorReporter == null) {
            errorReporter = new XMLErrorReporter ();
            errorReporter.setLocale(fLocale);
            errorReporter.setProperty(ERROR_HANDLER, new DefaultErrorHandler());

        }
        fErrorReporter = errorReporter;
        if(fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
        }
        fLoaderConfig.setProperty(ERROR_REPORTER, fErrorReporter);
        fEntityManager = entityResolver;
        if (fEntityManager != null){
            fLoaderConfig.setProperty(ENTITY_MANAGER, fEntityManager);
        }

        fLoaderConfig.setFeature(AUGMENT_PSVI, true);

        if(grammarBucket == null ) {
            grammarBucket = new XSGrammarBucket();
        }
        fGrammarBucket = grammarBucket;
        if(sHandler == null) {
            sHandler = new SubstitutionGroupHandler(fGrammarBucket);
        }
        fSubGroupHandler = sHandler;

        if(builder == null) {
            builder = new CMBuilder(fNodeFactory);
        }
        fCMBuilder = builder;
        fSchemaHandler = new XSDHandler(fGrammarBucket);
        if (fDeclPool != null) {
            fDeclPool.reset();
        }
        fJAXPCache = new HashMap();

        fSettingsChanged = true;
    }


    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public boolean getFeature(String featureId)
    throws XMLConfigurationException {
        return fLoaderConfig.getFeature(featureId);
    } public void setFeature(String featureId,
            boolean state) throws XMLConfigurationException {
        fSettingsChanged = true;
        if(featureId.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, state);
        }
        else if(featureId.equals(GENERATE_SYNTHETIC_ANNOTATIONS)) {
            fSchemaHandler.setGenerateSyntheticAnnotations(state);
        }
        fLoaderConfig.setFeature(featureId, state);
    } public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } public Object getProperty(String propertyId)
    throws XMLConfigurationException {
        return fLoaderConfig.getProperty(propertyId);
    } public void setProperty(String propertyId,
            Object state) throws XMLConfigurationException {
        fSettingsChanged = true;
        fLoaderConfig.setProperty(propertyId, state);
        if (propertyId.equals(JAXP_SCHEMA_SOURCE)) {
            fJAXPSource = state;
            fJAXPProcessed = false;
        }
        else if (propertyId.equals(XMLGRAMMAR_POOL)) {
            fGrammarPool = (XMLGrammarPool)state;
        }
        else if (propertyId.equals(SCHEMA_LOCATION)) {
            fExternalSchemas = (String)state;
        }
        else if (propertyId.equals(SCHEMA_NONS_LOCATION)) {
            fExternalNoNSSchema = (String) state;
        }
        else if (propertyId.equals(LOCALE)) {
            setLocale((Locale) state);
        }
        else if (propertyId.equals(ENTITY_RESOLVER)) {
            fEntityManager.setProperty(ENTITY_RESOLVER, state);
        }
        else if (propertyId.equals(ERROR_REPORTER)) {
            fErrorReporter = (XMLErrorReporter)state;
            if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
                fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
            }
        }
        else if (propertyId.equals(XML_SECURITY_PROPERTY_MANAGER)) {
            XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)state;
            faccessExternalSchema = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
        }
    } public void setLocale(Locale locale) {
        fLocale = locale;
        fErrorReporter.setLocale(locale);
    } public Locale getLocale() {
        return fLocale;
    } public void setErrorHandler(XMLErrorHandler errorHandler) {
        fErrorReporter.setProperty(ERROR_HANDLER, errorHandler);
    } public XMLErrorHandler getErrorHandler() {
        return fErrorReporter.getErrorHandler();
    } public void setEntityResolver(XMLEntityResolver entityResolver) {
        fUserEntityResolver = entityResolver;
        fLoaderConfig.setProperty(ENTITY_RESOLVER, entityResolver);
        fEntityManager.setProperty(ENTITY_RESOLVER, entityResolver);
    } public XMLEntityResolver getEntityResolver() {
        return fUserEntityResolver;
    } public void loadGrammar(XMLInputSource source[])
    throws IOException, XNIException {
        int numSource = source.length;
        for (int i = 0; i < numSource; ++i) {
            loadGrammar(source[i]);
        }
    }


    public Grammar loadGrammar(XMLInputSource source)
    throws IOException, XNIException {

        reset(fLoaderConfig);
        fSettingsChanged = false;
        XSDDescription desc = new XSDDescription();
        desc.fContextType = XSDDescription.CONTEXT_PREPARSE;
        desc.setBaseSystemId(source.getBaseSystemId());
        desc.setLiteralSystemId( source.getSystemId());
        Map locationPairs = new HashMap();
        processExternalHints(fExternalSchemas, fExternalNoNSSchema,
                locationPairs, fErrorReporter);
        SchemaGrammar grammar = loadSchema(desc, source, locationPairs);

        if(grammar != null && fGrammarPool != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA, fGrammarBucket.getGrammars());
            if(fIsCheckedFully && fJAXPCache.get(grammar) != grammar) {
                XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
            }
        }
        return grammar;
    } SchemaGrammar loadSchema(XSDDescription desc, XMLInputSource source,
            Map<String, LocationArray> locationPairs) throws IOException, XNIException {

        if(!fJAXPProcessed) {
            processJAXPSchemaSource(locationPairs);
        }

        if (desc.isExternal()) {
            String accessError = SecuritySupport.checkAccess(desc.getExpandedSystemId(), faccessExternalSchema, Constants.ACCESS_EXTERNAL_ALL);
            if (accessError != null) {
                throw new XNIException(fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        "schema_reference.access",
                        new Object[] { SecuritySupport.sanitizePath(desc.getExpandedSystemId()), accessError }, XMLErrorReporter.SEVERITY_ERROR));
            }
        }
        SchemaGrammar grammar = fSchemaHandler.parseSchema(source, desc, locationPairs);

        return grammar;
    } public static XMLInputSource resolveDocument(XSDDescription desc,
            Map<String, LocationArray> locationPairs,
            XMLEntityResolver entityResolver) throws IOException {
        String loc = null;
        if (desc.getContextType() == XSDDescription.CONTEXT_IMPORT ||
                desc.fromInstance()) {
            String namespace = desc.getTargetNamespace();
            String ns = namespace == null ? XMLSymbols.EMPTY_STRING : namespace;
            LocationArray tempLA = locationPairs.get(ns);
            if(tempLA != null)
                loc = tempLA.getFirstLocation();
        }

        if (loc == null) {
            String[] hints = desc.getLocationHints();
            if (hints != null && hints.length > 0)
                loc = hints[0];
        }

        String expandedLoc = XMLEntityManager.expandSystemId(loc, desc.getBaseSystemId(), false);
        desc.setLiteralSystemId(loc);
        desc.setExpandedSystemId(expandedLoc);
        return entityResolver.resolveEntity(desc);
    }

    public static void processExternalHints(String sl, String nsl,
            Map<String, XMLSchemaLoader.LocationArray> locations,
            XMLErrorReporter er) {
        if (sl != null) {
            try {
                XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
                attrDecl.fType.validate(sl, null, null);
                if (!tokenizeSchemaLocationStr(sl, locations)) {
                    er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                            "SchemaLocation",
                            new Object[]{sl},
                            XMLErrorReporter.SEVERITY_WARNING);
                }
            }
            catch (InvalidDatatypeValueException ex) {
                er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        ex.getKey(), ex.getArgs(),
                        XMLErrorReporter.SEVERITY_WARNING);
            }
        }

        if (nsl != null) {
            try {
                XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                attrDecl.fType.validate(nsl, null, null);
                LocationArray la = ((LocationArray)locations.get(XMLSymbols.EMPTY_STRING));
                if(la == null) {
                    la = new LocationArray();
                    locations.put(XMLSymbols.EMPTY_STRING, la);
                }
                la.addLocation(nsl);
            }
            catch (InvalidDatatypeValueException ex) {
                er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        ex.getKey(), ex.getArgs(),
                        XMLErrorReporter.SEVERITY_WARNING);
            }
        }
    }
    public static boolean tokenizeSchemaLocationStr(String schemaStr,
            Map<String, XMLSchemaLoader.LocationArray> locations) {
        if (schemaStr!= null) {
            StringTokenizer t = new StringTokenizer(schemaStr, " \n\t\r");
            String namespace, location;
            while (t.hasMoreTokens()) {
                namespace = t.nextToken ();
                if (!t.hasMoreTokens()) {
                    return false; }
                location = t.nextToken();
                LocationArray la = locations.get(namespace);
                if(la == null) {
                    la = new LocationArray();
                    locations.put(namespace, la);
                }
                la.addLocation(location);
            }
        }
        return true;
    } private void processJAXPSchemaSource(
            Map<String, LocationArray> locationPairs) throws IOException {
        fJAXPProcessed = true;
        if (fJAXPSource == null) {
            return;
        }

        Class componentType = fJAXPSource.getClass().getComponentType();
        XMLInputSource xis = null;
        String sid = null;
        if (componentType == null) {
            if(fJAXPSource instanceof InputStream ||
                    fJAXPSource instanceof InputSource) {
                SchemaGrammar g = (SchemaGrammar)fJAXPCache.get(fJAXPSource);
                if(g != null) {
                    fGrammarBucket.putGrammar(g);
                    return;
                }
            }
            fXSDDescription.reset();
            xis = xsdToXMLInputSource(fJAXPSource);
            sid = xis.getSystemId();
            fXSDDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
            if (sid != null) {
                fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            SchemaGrammar g = loadSchema(fXSDDescription, xis, locationPairs);
            if (g != null){
                if(fJAXPSource instanceof InputStream ||
                        fJAXPSource instanceof InputSource) {
                    fJAXPCache.put(fJAXPSource, g);
                    if(fIsCheckedFully) {
                        XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
                    }
                }
                fGrammarBucket.putGrammar(g);
            }
            return ;
        } else if ( (componentType != Object.class) &&
                (componentType != String.class) &&
                (componentType != File.class) &&
                (componentType != InputStream.class) &&
                (componentType != InputSource.class)
        ) {
            throw new XMLConfigurationException(
                    Status.NOT_SUPPORTED, "\""+JAXP_SCHEMA_SOURCE+
                    "\" property cannot have an array of type {"+componentType.getName()+
                    "}. Possible types of the array supported are Object, String, File, "+
            "InputStream, InputSource.");
        }

        Object[] objArr = (Object[]) fJAXPSource;
        Vector jaxpSchemaSourceNamespaces = new Vector() ;
        for (int i = 0; i < objArr.length; i++) {
            if(objArr[i] instanceof InputStream ||
                    objArr[i] instanceof InputSource) {
                SchemaGrammar g = (SchemaGrammar)fJAXPCache.get(objArr[i]);
                if (g != null) {
                    fGrammarBucket.putGrammar(g);
                    continue;
                }
            }
            fXSDDescription.reset();
            xis = xsdToXMLInputSource(objArr[i]);
            sid = xis.getSystemId();
            fXSDDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
            if (sid != null) {
                fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            String targetNamespace = null ;
            SchemaGrammar grammar = fSchemaHandler.parseSchema(xis,fXSDDescription, locationPairs);

            if(fIsCheckedFully) {
                XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
            }
            if(grammar != null){
                targetNamespace = grammar.getTargetNamespace() ;
                if(jaxpSchemaSourceNamespaces.contains(targetNamespace)){
                    throw new java.lang.IllegalArgumentException(
                            " When using array of Objects as the value of SCHEMA_SOURCE property , " +
                    "no two Schemas should share the same targetNamespace. " );
                }
                else{
                    jaxpSchemaSourceNamespaces.add(targetNamespace) ;
                }
                if(objArr[i] instanceof InputStream ||
                        objArr[i] instanceof InputSource) {
                    fJAXPCache.put(objArr[i], grammar);
                }
                fGrammarBucket.putGrammar(grammar);
            }
            else{
                }
        }
    }private XMLInputSource xsdToXMLInputSource(
            Object val)
    {
        if (val instanceof String) {
            String loc = (String) val;
            fXSDDescription.reset();
            fXSDDescription.setValues(null, loc, null, null);
            XMLInputSource xis = null;
            try {
                xis = fEntityManager.resolveEntity(fXSDDescription);
            } catch (IOException ex) {
                fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        "schema_reference.4",
                        new Object[] { loc }, XMLErrorReporter.SEVERITY_ERROR);
            }
            if (xis == null) {
                return new XMLInputSource(null, loc, null);
            }
            return xis;
        } else if (val instanceof InputSource) {
            return saxToXMLInputSource((InputSource) val);
        } else if (val instanceof InputStream) {
            return new XMLInputSource(null, null, null,
                    (InputStream) val, null);
        } else if (val instanceof File) {
            File file = (File) val;
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        "schema_reference.4", new Object[] { file.toString() },
                        XMLErrorReporter.SEVERITY_ERROR);
            }
            return new XMLInputSource(null, null, null, is, null);
        }
        throw new XMLConfigurationException(
                Status.NOT_SUPPORTED, "\""+JAXP_SCHEMA_SOURCE+
                "\" property cannot have a value of type {"+val.getClass().getName()+
                "}. Possible types of the value supported are String, File, InputStream, "+
        "InputSource OR an array of these types.");
    }


    private static XMLInputSource saxToXMLInputSource(InputSource sis) {
        String publicId = sis.getPublicId();
        String systemId = sis.getSystemId();

        Reader charStream = sis.getCharacterStream();
        if (charStream != null) {
            return new XMLInputSource(publicId, systemId, null, charStream,
                    null);
        }

        InputStream byteStream = sis.getByteStream();
        if (byteStream != null) {
            return new XMLInputSource(publicId, systemId, null, byteStream,
                    sis.getEncoding());
        }

        return new XMLInputSource(publicId, systemId, null);
    }

    public static class LocationArray{

        int length ;
        String [] locations = new String[2];

        public void resize(int oldLength , int newLength){
            String [] temp = new String[newLength] ;
            System.arraycopy(locations, 0, temp, 0, Math.min(oldLength, newLength));
            locations = temp ;
            length = Math.min(oldLength, newLength);
        }

        public void addLocation(String location){
            if(length >= locations.length ){
                resize(length, Math.max(1, length*2));
            }
            locations[length++] = location;
        }public String [] getLocationArray(){
            if(length < locations.length ){
                resize(locations.length, length);
            }
            return locations;
        }public String getFirstLocation(){
            return length > 0 ? locations[0] : null;
        }

        public int getLength(){
            return length ;
        }

    } public Boolean getFeatureDefault(String featureId) {
        if (featureId.equals(AUGMENT_PSVI)){
            return Boolean.TRUE;
        }
        return null;
    }


    public Object getPropertyDefault(String propertyId) {
        return null;
    }


    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {

        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty(XML_SECURITY_PROPERTY_MANAGER);
        if (spm == null) {
            spm = new XMLSecurityPropertyManager();
            setProperty(XML_SECURITY_PROPERTY_MANAGER, spm);
        }

        XMLSecurityManager sm = (XMLSecurityManager)componentManager.getProperty(SECURITY_MANAGER);
        if (sm == null)
            setProperty(SECURITY_MANAGER,new XMLSecurityManager(true));

        faccessExternalSchema = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);

        fGrammarBucket.reset();

        fSubGroupHandler.reset();

        boolean parser_settings = componentManager.getFeature(PARSER_SETTINGS, true);

        if (!parser_settings || !fSettingsChanged){
            fJAXPProcessed = false;
            initGrammarBucket();
            return;
        }

        fNodeFactory.reset(componentManager);

        fEntityManager = (XMLEntityManager)componentManager.getProperty(ENTITY_MANAGER);

        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);

        SchemaDVFactory dvFactory = null;
        dvFactory = fSchemaHandler.getDVFactory();
        if (dvFactory == null) {
            dvFactory = SchemaDVFactory.getInstance();
            fSchemaHandler.setDVFactory(dvFactory);
        }

        boolean psvi = componentManager.getFeature(AUGMENT_PSVI, false);

        if (!psvi) {
            if (fDeclPool != null) {
                fDeclPool.reset();
            }
            else {
                fDeclPool = new XSDeclarationPool();
            }
            fCMBuilder.setDeclPool(fDeclPool);
            fSchemaHandler.setDeclPool(fDeclPool);
            if (dvFactory instanceof SchemaDVFactoryImpl) {
                fDeclPool.setDVFactory((SchemaDVFactoryImpl)dvFactory);
                ((SchemaDVFactoryImpl)dvFactory).setDeclPool(fDeclPool);
            }
        } else {
            fCMBuilder.setDeclPool(null);
            fSchemaHandler.setDeclPool(null);
        }

        try {
            fExternalSchemas = (String) componentManager.getProperty(SCHEMA_LOCATION);
            fExternalNoNSSchema = (String) componentManager.getProperty(SCHEMA_NONS_LOCATION);
        } catch (XMLConfigurationException e) {
            fExternalSchemas = null;
            fExternalNoNSSchema = null;
        }

        fJAXPSource = componentManager.getProperty(JAXP_SCHEMA_SOURCE, null);
        fJAXPProcessed = false;

        fGrammarPool = (XMLGrammarPool) componentManager.getProperty(XMLGRAMMAR_POOL, null);
        initGrammarBucket();
        try {
            boolean fatalError = componentManager.getFeature(CONTINUE_AFTER_FATAL_ERROR, false);
            if (!fatalError) {
                fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, fatalError);
            }
        } catch (XMLConfigurationException e) {
        }
        fIsCheckedFully = componentManager.getFeature(SCHEMA_FULL_CHECKING, false);

        fSchemaHandler.setGenerateSyntheticAnnotations(componentManager.getFeature(GENERATE_SYNTHETIC_ANNOTATIONS, false));
        fSchemaHandler.reset(componentManager);
    }

    private void initGrammarBucket(){
        if(fGrammarPool != null) {
            Grammar [] initialGrammars = fGrammarPool.retrieveInitialGrammarSet(XMLGrammarDescription.XML_SCHEMA);
            for (int i = 0; i < initialGrammars.length; i++) {
                if (!fGrammarBucket.putGrammar((SchemaGrammar)(initialGrammars[i]), true)) {
                    fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                            "GrammarConflict", null,
                            XMLErrorReporter.SEVERITY_WARNING);
                }
            }
        }
    }



    public DOMConfiguration getConfig() {
        return this;
    }


    public XSModel load(LSInput is) {
        try {
            Grammar g = loadGrammar(dom2xmlInputSource(is));
            return ((XSGrammar) g).toXSModel();
        } catch (Exception e) {
            reportDOMFatalError(e);
            return null;
        }
    }


    public XSModel loadInputList(LSInputList is) {
        int length = is.getLength();
        SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; i++) {
            try {
                gs[i] = (SchemaGrammar) loadGrammar(dom2xmlInputSource(is.item(i)));
            } catch (Exception e) {
                reportDOMFatalError(e);
                return null;
            }
        }
        return new XSModelImpl(gs);
    }


    public XSModel loadURI(String uri) {
        try {
            Grammar g = loadGrammar(new XMLInputSource(null, uri, null));
            return ((XSGrammar)g).toXSModel();
        }
        catch (Exception e){
            reportDOMFatalError(e);
            return null;
        }
    }


    public XSModel loadURIList(StringList uriList) {
        int length = uriList.getLength();
        SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; i++) {
            try {
                gs[i] =
                    (SchemaGrammar) loadGrammar(new XMLInputSource(null, uriList.item(i), null));
            } catch (Exception e) {
                reportDOMFatalError(e);
                return null;
            }
        }
        return new XSModelImpl(gs);
    }

    void reportDOMFatalError(Exception e) {
                if (fErrorHandler != null) {
                    DOMErrorImpl error = new DOMErrorImpl();
                    error.fException = e;
                    error.fMessage = e.getMessage();
                    error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                    fErrorHandler.getErrorHandler().handleError(error);
                }
            }


    public boolean canSetParameter(String name, Object value) {
        if(value instanceof Boolean){
            if (name.equals(Constants.DOM_VALIDATE) ||
                name.equals(SCHEMA_FULL_CHECKING) ||
                name.equals(VALIDATE_ANNOTATIONS) ||
                name.equals(CONTINUE_AFTER_FATAL_ERROR) ||
                name.equals(ALLOW_JAVA_ENCODINGS) ||
                name.equals(STANDARD_URI_CONFORMANT_FEATURE) ||
                name.equals(GENERATE_SYNTHETIC_ANNOTATIONS) ||
                name.equals(HONOUR_ALL_SCHEMALOCATIONS) ||
                name.equals(NAMESPACE_GROWTH) ||
                name.equals(TOLERATE_DUPLICATES) ||
                name.equals(OVERRIDE_PARSER)) {
                return true;

            }
            return false;
        }
        if (name.equals(Constants.DOM_ERROR_HANDLER) ||
            name.equals(Constants.DOM_RESOURCE_RESOLVER) ||
            name.equals(SYMBOL_TABLE) ||
            name.equals(ERROR_REPORTER) ||
            name.equals(ERROR_HANDLER) ||
            name.equals(ENTITY_RESOLVER) ||
            name.equals(XMLGRAMMAR_POOL) ||
            name.equals(SCHEMA_LOCATION) ||
            name.equals(SCHEMA_NONS_LOCATION) ||
            name.equals(JAXP_SCHEMA_SOURCE) ||
            name.equals(SCHEMA_DV_FACTORY)) {
            return true;
        }
        return false;
    }


    public Object getParameter(String name) throws DOMException {

        if (name.equals(Constants.DOM_ERROR_HANDLER)){
            return (fErrorHandler != null) ? fErrorHandler.getErrorHandler() : null;
        }
        else if (name.equals(Constants.DOM_RESOURCE_RESOLVER)) {
            return (fResourceResolver != null) ? fResourceResolver.getEntityResolver() : null;
        }

        try {
            boolean feature = getFeature(name);
            return (feature) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            Object property;
            try {
                property = getProperty(name);
                return property;
            } catch (Exception ex) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        }
    }


    public DOMStringList getParameterNames() {
        if (fRecognizedParameters == null){
            Vector v = new Vector();
            v.add(Constants.DOM_VALIDATE);
            v.add(Constants.DOM_ERROR_HANDLER);
            v.add(Constants.DOM_RESOURCE_RESOLVER);
            v.add(SYMBOL_TABLE);
            v.add(ERROR_REPORTER);
            v.add(ERROR_HANDLER);
            v.add(ENTITY_RESOLVER);
            v.add(XMLGRAMMAR_POOL);
            v.add(SCHEMA_LOCATION);
            v.add(SCHEMA_NONS_LOCATION);
            v.add(JAXP_SCHEMA_SOURCE);
            v.add(SCHEMA_FULL_CHECKING);
            v.add(CONTINUE_AFTER_FATAL_ERROR);
            v.add(ALLOW_JAVA_ENCODINGS);
            v.add(STANDARD_URI_CONFORMANT_FEATURE);
            v.add(VALIDATE_ANNOTATIONS);
            v.add(GENERATE_SYNTHETIC_ANNOTATIONS);
            v.add(HONOUR_ALL_SCHEMALOCATIONS);
            v.add(NAMESPACE_GROWTH);
            v.add(TOLERATE_DUPLICATES);
            v.add(OVERRIDE_PARSER);
            fRecognizedParameters = new DOMStringListImpl(v);
        }
        return fRecognizedParameters;
    }


    public void setParameter(String name, Object value) throws DOMException {
        if (value instanceof Boolean) {
            boolean state = ((Boolean) value).booleanValue();
            if (name.equals("validate") && state) {
                return;
            }
            try {
                setFeature(name, state);
            } catch (Exception e) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            return;
        }
        if (name.equals(Constants.DOM_ERROR_HANDLER)) {
            if (value instanceof DOMErrorHandler) {
                try {
                    fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler) value);
                    setErrorHandler(fErrorHandler);
                } catch (XMLConfigurationException e) {
                }
            } else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            return;

        }
        if (name.equals(Constants.DOM_RESOURCE_RESOLVER)) {
            if (value instanceof LSResourceResolver) {
                try {
                    fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver) value);
                    setEntityResolver(fResourceResolver);
                }
                catch (XMLConfigurationException e) {}
            } else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            return;
        }

        try {
            setProperty(name, value);
        } catch (Exception ex) {

            String msg =
                DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_SUPPORTED",
                        new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);

        }

    }

        XMLInputSource dom2xmlInputSource(LSInput is) {
        XMLInputSource xis = null;



        if (is.getCharacterStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
                    is.getBaseURI(), is.getCharacterStream(),
            "UTF-16");
        }
        else if (is.getByteStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
                    is.getBaseURI(), is.getByteStream(),
                    is.getEncoding());
        }
        else if (is.getStringData() != null && is.getStringData().length() != 0) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
                    is.getBaseURI(), new StringReader(is.getStringData()),
            "UTF-16");
        }
        else {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
                    is.getBaseURI());
        }

        return xis;
    }

}