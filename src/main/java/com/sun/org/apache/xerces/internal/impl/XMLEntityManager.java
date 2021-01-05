


package com.sun.org.apache.xerces.internal.impl ;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.*;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.*;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import javax.xml.stream.XMLInputFactory;



public class XMLEntityManager implements XMLComponent, XMLEntityResolver {

    public static final int DEFAULT_BUFFER_SIZE = 8192;


    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;


    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;

    protected static final String VALIDATION =
            Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    protected boolean fStrictURI;



    protected static final String EXTERNAL_GENERAL_ENTITIES =
            Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;


    protected static final String EXTERNAL_PARAMETER_ENTITIES =
            Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;


    protected static final String ALLOW_JAVA_ENCODINGS =
            Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;


    protected static final String WARN_ON_DUPLICATE_ENTITYDEF =
            Constants.XERCES_FEATURE_PREFIX +Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE;


    protected static final String LOAD_EXTERNAL_DTD =
            Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE;

    protected static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String STANDARD_URI_CONFORMANT =
            Constants.XERCES_FEATURE_PREFIX +Constants.STANDARD_URI_CONFORMANT_FEATURE;


    protected static final String ENTITY_RESOLVER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    protected static final String STAX_ENTITY_RESOLVER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.STAX_ENTITY_RESOLVER_PROPERTY;

    protected static final String VALIDATION_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    protected static final String BUFFER_SIZE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.BUFFER_SIZE_PROPERTY;


    protected static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    protected static final String PARSER_SETTINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;


    static final String EXTERNAL_ACCESS_DEFAULT = Constants.EXTERNAL_ACCESS_DEFAULT;

    private static final String[] RECOGNIZED_FEATURES = {
                VALIDATION,
                EXTERNAL_GENERAL_ENTITIES,
                EXTERNAL_PARAMETER_ENTITIES,
                ALLOW_JAVA_ENCODINGS,
                WARN_ON_DUPLICATE_ENTITYDEF,
                STANDARD_URI_CONFORMANT
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
                null,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.FALSE
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
                SYMBOL_TABLE,
                ERROR_REPORTER,
                ENTITY_RESOLVER,
                VALIDATION_MANAGER,
                BUFFER_SIZE,
                SECURITY_MANAGER,
                XML_SECURITY_PROPERTY_MANAGER
    };


    private static final Object[] PROPERTY_DEFAULTS = {
                null,
                null,
                null,
                null,
                new Integer(DEFAULT_BUFFER_SIZE),
                null,
                null
    };

    private static final String XMLEntity = "[xml]".intern();
    private static final String DTDEntity = "[dtd]".intern();

    private static final boolean DEBUG_BUFFER = false;


    protected boolean fWarnDuplicateEntityDef;


    private static final boolean DEBUG_ENTITIES = false;


    private static final boolean DEBUG_ENCODINGS = false;

    private static final boolean DEBUG_RESOLVER = false ;

    protected boolean fValidation;


    protected boolean fExternalGeneralEntities;


    protected boolean fExternalParameterEntities;


    protected boolean fAllowJavaEncodings = true ;


    protected boolean fLoadExternalDTD = true;

    protected SymbolTable fSymbolTable;


    protected XMLErrorReporter fErrorReporter;


    protected XMLEntityResolver fEntityResolver;



    protected StaxEntityResolverWrapper fStaxEntityResolver;


    protected PropertyManager fPropertyManager ;


    boolean fSupportDTD = true;
    boolean fReplaceEntityReferences = true;
    boolean fSupportExternalEntities = true;


    protected String fAccessExternalDTD = EXTERNAL_ACCESS_DEFAULT;

    protected ValidationManager fValidationManager;

    protected int fBufferSize = DEFAULT_BUFFER_SIZE;


    protected XMLSecurityManager fSecurityManager = null;

    protected XMLLimitAnalyzer fLimitAnalyzer = null;

    protected int entityExpansionIndex;


    protected boolean fStandalone;

    protected boolean fInExternalSubset = false;


    protected XMLEntityHandler fEntityHandler;


    protected XMLEntityScanner fEntityScanner ;


    protected XMLEntityScanner fXML10EntityScanner;


    protected XMLEntityScanner fXML11EntityScanner;


    protected int fEntityExpansionCount = 0;

    protected Map<String, Entity> fEntities = new HashMap<>();


    protected Stack<Entity> fEntityStack = new Stack<>();


    protected Entity.ScannedEntity fCurrentEntity = null;


    boolean fISCreatedByResolver = false;

    protected XMLEntityStorage fEntityStorage ;

    protected final Object [] defaultEncoding = new Object[]{"UTF-8", null};


    private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();


    private final Augmentations fEntityAugs = new AugmentationsImpl();


    private CharacterBufferPool fBufferPool = new CharacterBufferPool(fBufferSize, DEFAULT_INTERNAL_BUFFER_SIZE);

    public XMLEntityManager() {
        fSecurityManager = new XMLSecurityManager(true);
        fEntityStorage = new XMLEntityStorage(this) ;
        setScannerVersion(Constants.XML_VERSION_1_0);
    } public XMLEntityManager(PropertyManager propertyManager) {
        fPropertyManager = propertyManager ;
        fEntityStorage = new XMLEntityStorage(this) ;
        fEntityScanner = new XMLEntityScanner(propertyManager, this) ;
        reset(propertyManager);
    } public void addInternalEntity(String name, String text) {
        if (!fEntities.containsKey(name)) {
            Entity entity = new Entity.InternalEntity(name, text, fInExternalSubset);
            fEntities.put(name, entity);
        } else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                        "MSG_DUPLICATE_ENTITY_DEFINITION",
                        new Object[]{ name },
                        XMLErrorReporter.SEVERITY_WARNING );
            }
        }

    } public void addExternalEntity(String name,
            String publicId, String literalSystemId,
            String baseSystemId) throws IOException {
        if (!fEntities.containsKey(name)) {
            if (baseSystemId == null) {
                int size = fEntityStack.size();
                if (size == 0 && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
                    baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                for (int i = size - 1; i >= 0 ; i--) {
                    Entity.ScannedEntity externalEntity =
                            (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
                        baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
                        break;
                    }
                }
            }
            Entity entity = new Entity.ExternalEntity(name,
                    new XMLEntityDescriptionImpl(name, publicId, literalSystemId, baseSystemId,
                    expandSystemId(literalSystemId, baseSystemId, false)), null, fInExternalSubset);
            fEntities.put(name, entity);
        } else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                        "MSG_DUPLICATE_ENTITY_DEFINITION",
                        new Object[]{ name },
                        XMLErrorReporter.SEVERITY_WARNING );
            }
        }

    } public void addUnparsedEntity(String name,
            String publicId, String systemId,
            String baseSystemId, String notation) {
        if (!fEntities.containsKey(name)) {
            Entity.ExternalEntity entity = new Entity.ExternalEntity(name,
                    new XMLEntityDescriptionImpl(name, publicId, systemId, baseSystemId, null),
                    notation, fInExternalSubset);
            fEntities.put(name, entity);
        } else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                        "MSG_DUPLICATE_ENTITY_DEFINITION",
                        new Object[]{ name },
                        XMLErrorReporter.SEVERITY_WARNING );
            }
        }
    } public XMLEntityStorage getEntityStore(){
        return fEntityStorage ;
    }


    public XMLEntityScanner getEntityScanner(){
        if(fEntityScanner == null) {
            if(fXML10EntityScanner == null) {
                fXML10EntityScanner = new XMLEntityScanner();
            }
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML10EntityScanner;
        }
        return fEntityScanner;

    }

    public void setScannerVersion(short version) {

        if(version == Constants.XML_VERSION_1_0) {
            if(fXML10EntityScanner == null) {
                fXML10EntityScanner = new XMLEntityScanner();
            }
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML10EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        } else {
            if(fXML11EntityScanner == null) {
                fXML11EntityScanner = new XML11EntityScanner();
            }
            fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML11EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        }

    }


    public String setupCurrentEntity(boolean reference, String name, XMLInputSource xmlInputSource,
            boolean literal, boolean isExternal)
            throws IOException, XNIException {
        final String publicId = xmlInputSource.getPublicId();
        String literalSystemId = xmlInputSource.getSystemId();
        String baseSystemId = xmlInputSource.getBaseSystemId();
        String encoding = xmlInputSource.getEncoding();
        final boolean encodingExternallySpecified = (encoding != null);
        Boolean isBigEndian = null;

        InputStream stream = null;
        Reader reader = xmlInputSource.getCharacterStream();

        String expandedSystemId = expandSystemId(literalSystemId, baseSystemId, fStrictURI);
        if (baseSystemId == null) {
            baseSystemId = expandedSystemId;
        }
        if (reader == null) {
            stream = xmlInputSource.getByteStream();
            if (stream == null) {
                URL location = new URL(expandedSystemId);
                URLConnection connect = location.openConnection();
                if (!(connect instanceof HttpURLConnection)) {
                    stream = connect.getInputStream();
                }
                else {
                    boolean followRedirects = true;

                    if (xmlInputSource instanceof HTTPInputSource) {
                        final HttpURLConnection urlConnection = (HttpURLConnection) connect;
                        final HTTPInputSource httpInputSource = (HTTPInputSource) xmlInputSource;

                        Iterator<Map.Entry<String, String>> propIter = httpInputSource.getHTTPRequestProperties();
                        while (propIter.hasNext()) {
                            Map.Entry<String, String> entry = propIter.next();
                            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                        }

                        followRedirects = httpInputSource.getFollowHTTPRedirects();
                        if (!followRedirects) {
                            setInstanceFollowRedirects(urlConnection, followRedirects);
                        }
                    }

                    stream = connect.getInputStream();

                    if (followRedirects) {
                        String redirect = connect.getURL().toString();
                        if (!redirect.equals(expandedSystemId)) {
                            literalSystemId = redirect;
                            expandedSystemId = redirect;
                        }
                    }
                }
            }

            stream = new RewindableInputStream(stream);

            if (encoding == null) {
                final byte[] b4 = new byte[4];
                int count = 0;
                for (; count<4; count++ ) {
                    b4[count] = (byte)stream.read();
                }
                if (count == 4) {
                    Object [] encodingDesc = getEncodingName(b4, count);
                    encoding = (String)(encodingDesc[0]);
                    isBigEndian = (Boolean)(encodingDesc[1]);

                    stream.reset();
                    if (count > 2 && encoding.equals("UTF-8")) {
                        int b0 = b4[0] & 0xFF;
                        int b1 = b4[1] & 0xFF;
                        int b2 = b4[2] & 0xFF;
                        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
                            stream.skip(3);
                        }
                    }
                    reader = createReader(stream, encoding, isBigEndian);
                } else {
                    reader = createReader(stream, encoding, isBigEndian);
                }
            }

            else {
                encoding = encoding.toUpperCase(Locale.ENGLISH);

                if (encoding.equals("UTF-8")) {
                    final int[] b3 = new int[3];
                    int count = 0;
                    for (; count < 3; ++count) {
                        b3[count] = stream.read();
                        if (b3[count] == -1)
                            break;
                    }
                    if (count == 3) {
                        if (b3[0] != 0xEF || b3[1] != 0xBB || b3[2] != 0xBF) {
                            stream.reset();
                        }
                    } else {
                        stream.reset();
                    }
                }
                else if (encoding.equals("UTF-16")) {
                    final int[] b4 = new int[4];
                    int count = 0;
                    for (; count < 4; ++count) {
                        b4[count] = stream.read();
                        if (b4[count] == -1)
                            break;
                    }
                    stream.reset();

                    String utf16Encoding = "UTF-16";
                    if (count >= 2) {
                        final int b0 = b4[0];
                        final int b1 = b4[1];
                        if (b0 == 0xFE && b1 == 0xFF) {
                            utf16Encoding = "UTF-16BE";
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b0 == 0xFF && b1 == 0xFE) {
                            utf16Encoding = "UTF-16LE";
                            isBigEndian = Boolean.FALSE;
                        }
                        else if (count == 4) {
                            final int b2 = b4[2];
                            final int b3 = b4[3];
                            if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
                                utf16Encoding = "UTF-16BE";
                                isBigEndian = Boolean.TRUE;
                            }
                            if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
                                utf16Encoding = "UTF-16LE";
                                isBigEndian = Boolean.FALSE;
                            }
                        }
                    }
                    reader = createReader(stream, utf16Encoding, isBigEndian);
                }
                else if (encoding.equals("ISO-10646-UCS-4")) {
                    final int[] b4 = new int[4];
                    int count = 0;
                    for (; count < 4; ++count) {
                        b4[count] = stream.read();
                        if (b4[count] == -1)
                            break;
                    }
                    stream.reset();

                    if (count == 4) {
                        if (b4[0] == 0x00 && b4[1] == 0x00 && b4[2] == 0x00 && b4[3] == 0x3C) {
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b4[0] == 0x3C && b4[1] == 0x00 && b4[2] == 0x00 && b4[3] == 0x00) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }
                else if (encoding.equals("ISO-10646-UCS-2")) {
                    final int[] b4 = new int[4];
                    int count = 0;
                    for (; count < 4; ++count) {
                        b4[count] = stream.read();
                        if (b4[count] == -1)
                            break;
                    }
                    stream.reset();

                    if (count == 4) {
                        if (b4[0] == 0x00 && b4[1] == 0x3C && b4[2] == 0x00 && b4[3] == 0x3F) {
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b4[0] == 0x3C && b4[1] == 0x00 && b4[2] == 0x3F && b4[3] == 0x00) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }

                reader = createReader(stream, encoding, isBigEndian);
            }

            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ no longer wrapping reader in OneCharReader");
            }
            }

        if (fCurrentEntity != null) {
            fEntityStack.push(fCurrentEntity);
        }

        fCurrentEntity = new Entity.ScannedEntity(reference, name,
                new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId),
                stream, reader, encoding, literal, encodingExternallySpecified, isExternal);
        fCurrentEntity.setEncodingExternallySpecified(encodingExternallySpecified);
        fEntityScanner.setCurrentEntity(fCurrentEntity);
        fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        if (fLimitAnalyzer != null) {
            fLimitAnalyzer.startEntity(name);
        }
        return encoding;
    } public boolean isExternalEntity(String entityName) {

        Entity entity = fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isExternal();
    }


    public boolean isEntityDeclInExternalSubset(String entityName) {

        Entity entity = fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isEntityDeclInExternalSubset();
    }



    public void setStandalone(boolean standalone) {
        fStandalone = standalone;
    }
    public boolean isStandalone() {
        return fStandalone;
    }  public boolean isDeclaredEntity(String entityName) {

        Entity entity = fEntities.get(entityName);
        return entity != null;
    }

    public boolean isUnparsedEntity(String entityName) {

        Entity entity = fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isUnparsed();
    }



    public XMLResourceIdentifier getCurrentResourceIdentifier() {
        return fResourceIdentifier;
    }



    public void setEntityHandler(com.sun.org.apache.xerces.internal.impl.XMLEntityHandler entityHandler) {
        fEntityHandler = (XMLEntityHandler) entityHandler;
    } public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier resourceIdentifier) throws java.io.IOException{

        if(resourceIdentifier == null ) return null;

        String publicId = resourceIdentifier.getPublicId();
        String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        boolean needExpand = (expandedSystemId == null);
        if (baseSystemId == null && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
            baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null)
                needExpand = true;
        }
        if (needExpand)
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId,false);

        StaxXMLInputSource staxInputSource = null;
        XMLInputSource xmlInputSource = null;

        XMLResourceIdentifierImpl ri = null;

        if (resourceIdentifier instanceof XMLResourceIdentifierImpl) {
            ri = (XMLResourceIdentifierImpl)resourceIdentifier;
        } else {
            fResourceIdentifier.clear();
            ri = fResourceIdentifier;
        }
        ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        if(DEBUG_RESOLVER){
            System.out.println("BEFORE Calling resolveEntity") ;
        }

        fISCreatedByResolver = false;
        if(fStaxEntityResolver != null){
            staxInputSource = fStaxEntityResolver.resolveEntity(ri);
            if(staxInputSource != null) {
                fISCreatedByResolver = true;
            }
        }

        if(fEntityResolver != null){
            xmlInputSource = fEntityResolver.resolveEntity(ri);
            if(xmlInputSource != null) {
                fISCreatedByResolver = true;
            }
        }

        if(xmlInputSource != null){
            staxInputSource = new StaxXMLInputSource(xmlInputSource, fISCreatedByResolver);
        }

        if (staxInputSource == null) {
            staxInputSource = new StaxXMLInputSource(new XMLInputSource(publicId, literalSystemId, baseSystemId));
        }else if(staxInputSource.hasXMLStreamOrXMLEventReader()){
            }

        if (DEBUG_RESOLVER) {
            System.err.println("XMLEntityManager.resolveEntity(" + publicId + ")");
            System.err.println(" = " + xmlInputSource);
        }

        return staxInputSource;

    }


    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
        if(resourceIdentifier == null ) return null;
        String publicId = resourceIdentifier.getPublicId();
        String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();

        boolean needExpand = (expandedSystemId == null);
        if (baseSystemId == null && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
            baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null)
                needExpand = true;
        }
        if (needExpand)
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId,false);

        XMLInputSource xmlInputSource = null;

        if (fEntityResolver != null) {
            resourceIdentifier.setBaseSystemId(baseSystemId);
            resourceIdentifier.setExpandedSystemId(expandedSystemId);
            xmlInputSource = fEntityResolver.resolveEntity(resourceIdentifier);
        }

        if (xmlInputSource == null) {
            xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId);
        }

        if (DEBUG_RESOLVER) {
            System.err.println("XMLEntityManager.resolveEntity(" + publicId + ")");
            System.err.println(" = " + xmlInputSource);
        }

        return xmlInputSource;

    } public void startEntity(boolean isGE, String entityName, boolean literal)
    throws IOException, XNIException {

        Entity entity = fEntityStorage.getEntity(entityName);
        if (entity == null) {
            if (fEntityHandler != null) {
                String encoding = null;
                fResourceIdentifier.clear();
                fEntityAugs.removeAllItems();
                fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                fEntityAugs.removeAllItems();
                fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                fEntityHandler.endEntity(entityName, fEntityAugs);
            }
            return;
        }

        boolean external = entity.isExternal();
        Entity.ExternalEntity externalEntity = null;
        String extLitSysId = null, extBaseSysId = null, expandedSystemId = null;
        if (external) {
            externalEntity = (Entity.ExternalEntity)entity;
            extLitSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null);
            extBaseSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null);
            expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
            boolean unparsed = entity.isUnparsed();
            boolean parameter = entityName.startsWith("%");
            boolean general = !parameter;
            if (unparsed || (general && !fExternalGeneralEntities) ||
                    (parameter && !fExternalParameterEntities) ||
                    !fSupportDTD || !fSupportExternalEntities) {

                if (fEntityHandler != null) {
                    fResourceIdentifier.clear();
                    final String encoding = null;
                    fResourceIdentifier.setValues(
                            (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                            extLitSysId, extBaseSysId, expandedSystemId);
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.endEntity(entityName, fEntityAugs);
                }
                return;
            }
        }

        int size = fEntityStack.size();
        for (int i = size; i >= 0; i--) {
            Entity activeEntity = i == size
                    ? fCurrentEntity
                    : (Entity)fEntityStack.elementAt(i);
            if (activeEntity.name == entityName) {
                String path = entityName;
                for (int j = i + 1; j < size; j++) {
                    activeEntity = (Entity)fEntityStack.elementAt(j);
                    path = path + " -> " + activeEntity.name;
                }
                path = path + " -> " + fCurrentEntity.name;
                path = path + " -> " + entityName;
                fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                        "RecursiveReference",
                        new Object[] { entityName, path },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);

                        if (fEntityHandler != null) {
                            fResourceIdentifier.clear();
                            final String encoding = null;
                            if (external) {
                                fResourceIdentifier.setValues(
                                        (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                                        extLitSysId, extBaseSysId, expandedSystemId);
                            }
                            fEntityAugs.removeAllItems();
                            fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                            fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                            fEntityAugs.removeAllItems();
                            fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                            fEntityHandler.endEntity(entityName, fEntityAugs);
                        }

                        return;
            }
        }

        StaxXMLInputSource staxInputSource = null;
        XMLInputSource xmlInputSource = null ;

        if (external) {
            staxInputSource = resolveEntityAsPerStax(externalEntity.entityLocation);

            xmlInputSource = staxInputSource.getXMLInputSource() ;
            if (!fISCreatedByResolver) {
                if (fLoadExternalDTD) {
                    String accessError = SecuritySupport.checkAccess(expandedSystemId, fAccessExternalDTD, Constants.ACCESS_EXTERNAL_ALL);
                    if (accessError != null) {
                        fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                                "AccessExternalEntity",
                                new Object[] { SecuritySupport.sanitizePath(expandedSystemId), accessError },
                                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
        }
        else {
            Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
            Reader reader = new StringReader(internalEntity.text);
            xmlInputSource = new XMLInputSource(null, null, null, reader, null);
        }

        startEntity(isGE, entityName, xmlInputSource, literal, external);

    } public void startDocumentEntity(XMLInputSource xmlInputSource)
    throws IOException, XNIException {
        startEntity(false, XMLEntity, xmlInputSource, false, true);
    } public void startDTDEntity(XMLInputSource xmlInputSource)
    throws IOException, XNIException {
        startEntity(false, DTDEntity, xmlInputSource, false, true);
    } public void startExternalSubset() {
        fInExternalSubset = true;
    }

    public void endExternalSubset() {
        fInExternalSubset = false;
    }


    public void startEntity(boolean isGE, String name,
            XMLInputSource xmlInputSource,
            boolean literal, boolean isExternal)
            throws IOException, XNIException {

        String encoding = setupCurrentEntity(isGE, name, xmlInputSource, literal, isExternal);

        fEntityExpansionCount++;
        if(fLimitAnalyzer != null) {
           fLimitAnalyzer.addValue(entityExpansionIndex, name, 1);
        }
        if( fSecurityManager != null && fSecurityManager.isOverLimit(entityExpansionIndex, fLimitAnalyzer)){
            fSecurityManager.debugPrint(fLimitAnalyzer);
            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,"EntityExpansionLimit",
                    new Object[]{fSecurityManager.getLimitValueByIndex(entityExpansionIndex)},
                                             XMLErrorReporter.SEVERITY_FATAL_ERROR );
            fEntityExpansionCount = 0;
        }

        if (fEntityHandler != null) {
            fEntityHandler.startEntity(name, fResourceIdentifier, encoding, null);
        }

    } public Entity.ScannedEntity getCurrentEntity(){
        return fCurrentEntity ;
    }


    public Entity.ScannedEntity getTopLevelEntity() {
        return (Entity.ScannedEntity)
            (fEntityStack.empty() ? null : fEntityStack.elementAt(0));
    }



    public void closeReaders() {

    }

    public void endEntity() throws IOException, XNIException {

        if (DEBUG_BUFFER) {
            System.out.print("(endEntity: ");
            print();
            System.out.println();
        }
        Entity.ScannedEntity entity = fEntityStack.size() > 0 ? (Entity.ScannedEntity)fEntityStack.pop() : null ;


        if(fCurrentEntity != null){
            try{
                if (fLimitAnalyzer != null) {
                    fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, fCurrentEntity.name);
                    if (fCurrentEntity.name.equals("[xml]")) {
                        fSecurityManager.debugPrint(fLimitAnalyzer);
                    }
                }
                fCurrentEntity.close();
            }catch(IOException ex){
                throw new XNIException(ex);
            }
        }

        if (fEntityHandler != null) {
            if(entity == null){
                fEntityAugs.removeAllItems();
                fEntityAugs.putItem(Constants.LAST_ENTITY, Boolean.TRUE);
                fEntityHandler.endEntity(fCurrentEntity.name, fEntityAugs);
                fEntityAugs.removeAllItems();
            }else{
                fEntityHandler.endEntity(fCurrentEntity.name, null);
            }
        }
        boolean documentEntity = fCurrentEntity.name == XMLEntity;

        fCurrentEntity = entity;
        fEntityScanner.setCurrentEntity(fCurrentEntity);

        if(fCurrentEntity == null & !documentEntity){
            throw new EOFException() ;
        }

        if (DEBUG_BUFFER) {
            System.out.print(")endEntity: ");
            print();
            System.out.println();
        }

    } public void reset(PropertyManager propertyManager){
        fSymbolTable = (SymbolTable)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY);
        fErrorReporter = (XMLErrorReporter)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY);
        try {
            fStaxEntityResolver = (StaxEntityResolverWrapper)propertyManager.getProperty(STAX_ENTITY_RESOLVER);
        } catch (XMLConfigurationException e) {
            fStaxEntityResolver = null;
        }

        fSupportDTD = ((Boolean)propertyManager.getProperty(XMLInputFactory.SUPPORT_DTD)).booleanValue();
        fReplaceEntityReferences = ((Boolean)propertyManager.getProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES)).booleanValue();
        fSupportExternalEntities = ((Boolean)propertyManager.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)).booleanValue();

        fLoadExternalDTD = !((Boolean)propertyManager.getProperty(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IGNORE_EXTERNAL_DTD)).booleanValue();

        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager) propertyManager.getProperty(XML_SECURITY_PROPERTY_MANAGER);
        fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);

        fSecurityManager = (XMLSecurityManager)propertyManager.getProperty(SECURITY_MANAGER);

        fLimitAnalyzer = new XMLLimitAnalyzer();
        fEntityStorage.reset(propertyManager);
        fEntityScanner.reset(propertyManager);

        fEntities.clear();
        fEntityStack.removeAllElements();
        fCurrentEntity = null;
        fValidation = false;
        fExternalGeneralEntities = true;
        fExternalParameterEntities = true;
        fAllowJavaEncodings = true ;
    }


    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {

        boolean parser_settings = componentManager.getFeature(PARSER_SETTINGS, true);

        if (!parser_settings) {
            reset();
            if(fEntityScanner != null){
                fEntityScanner.reset(componentManager);
            }
            if(fEntityStorage != null){
                fEntityStorage.reset(componentManager);
            }
            return;
        }

        fValidation = componentManager.getFeature(VALIDATION, false);
        fExternalGeneralEntities = componentManager.getFeature(EXTERNAL_GENERAL_ENTITIES, true);
        fExternalParameterEntities = componentManager.getFeature(EXTERNAL_PARAMETER_ENTITIES, true);

        fAllowJavaEncodings = componentManager.getFeature(ALLOW_JAVA_ENCODINGS, false);
        fWarnDuplicateEntityDef = componentManager.getFeature(WARN_ON_DUPLICATE_ENTITYDEF, false);
        fStrictURI = componentManager.getFeature(STANDARD_URI_CONFORMANT, false);
        fLoadExternalDTD = componentManager.getFeature(LOAD_EXTERNAL_DTD, true);

        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        fEntityResolver = (XMLEntityResolver)componentManager.getProperty(ENTITY_RESOLVER, null);
        fStaxEntityResolver = (StaxEntityResolverWrapper)componentManager.getProperty(STAX_ENTITY_RESOLVER, null);
        fValidationManager = (ValidationManager)componentManager.getProperty(VALIDATION_MANAGER, null);
        fSecurityManager = (XMLSecurityManager)componentManager.getProperty(SECURITY_MANAGER, null);
        entityExpansionIndex = fSecurityManager.getIndex(Constants.JDK_ENTITY_EXPANSION_LIMIT);

        fSupportDTD = true;
        fReplaceEntityReferences = true;
        fSupportExternalEntities = true;

        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager) componentManager.getProperty(XML_SECURITY_PROPERTY_MANAGER, null);
        if (spm == null) {
            spm = new XMLSecurityPropertyManager();
        }
        fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);

        reset();

        fEntityScanner.reset(componentManager);
        fEntityStorage.reset(componentManager);

    } public void reset() {
        fLimitAnalyzer = new XMLLimitAnalyzer();
        fStandalone = false;
        fEntities.clear();
        fEntityStack.removeAllElements();
        fEntityExpansionCount = 0;

        fCurrentEntity = null;
        if(fXML10EntityScanner != null){
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
        }
        if(fXML11EntityScanner != null) {
            fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
        }

        if (DEBUG_ENTITIES) {
            addInternalEntity("text", "Hello, World.");
            addInternalEntity("empty-element", "<foo/>");
            addInternalEntity("balanced-element", "<foo></foo>");
            addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
            addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
            addInternalEntity("unbalanced-entity", "<foo>");
            addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
            addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
            addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
            try {
                addExternalEntity("external-text", null, "external-text.ent", "test/external-text.xml");
                addExternalEntity("external-balanced-element", null, "external-balanced-element.ent", "test/external-balanced-element.xml");
                addExternalEntity("one", null, "ent/one.ent", "test/external-entity.xml");
                addExternalEntity("two", null, "ent/two.ent", "test/ent/one.xml");
            }
            catch (IOException ex) {
                }
        }

        fEntityHandler = null;

        }

    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();
            if (suffixLength == Constants.ALLOW_JAVA_ENCODINGS_FEATURE.length() &&
                featureId.endsWith(Constants.ALLOW_JAVA_ENCODINGS_FEATURE)) {
                fAllowJavaEncodings = state;
            }
            if (suffixLength == Constants.LOAD_EXTERNAL_DTD_FEATURE.length() &&
                featureId.endsWith(Constants.LOAD_EXTERNAL_DTD_FEATURE)) {
                fLoadExternalDTD = state;
                return;
            }
        }

    } public void setProperty(String propertyId, Object value){
        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.SYMBOL_TABLE_PROPERTY.length() &&
                propertyId.endsWith(Constants.SYMBOL_TABLE_PROPERTY)) {
                fSymbolTable = (SymbolTable)value;
                return;
            }
            if (suffixLength == Constants.ERROR_REPORTER_PROPERTY.length() &&
                propertyId.endsWith(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter)value;
                return;
            }
            if (suffixLength == Constants.ENTITY_RESOLVER_PROPERTY.length() &&
                propertyId.endsWith(Constants.ENTITY_RESOLVER_PROPERTY)) {
                fEntityResolver = (XMLEntityResolver)value;
                return;
            }
            if (suffixLength == Constants.BUFFER_SIZE_PROPERTY.length() &&
                propertyId.endsWith(Constants.BUFFER_SIZE_PROPERTY)) {
                Integer bufferSize = (Integer)value;
                if (bufferSize != null &&
                    bufferSize.intValue() > DEFAULT_XMLDECL_BUFFER_SIZE) {
                    fBufferSize = bufferSize.intValue();
                    fEntityScanner.setBufferSize(fBufferSize);
                    fBufferPool.setExternalBufferSize(fBufferSize);
                }
            }
            if (suffixLength == Constants.SECURITY_MANAGER_PROPERTY.length() &&
                propertyId.endsWith(Constants.SECURITY_MANAGER_PROPERTY)) {
                fSecurityManager = (XMLSecurityManager)value;
            }
        }

        if (propertyId.equals(XML_SECURITY_PROPERTY_MANAGER))
        {
            XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
            fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        }
    }

    public void setLimitAnalyzer(XMLLimitAnalyzer fLimitAnalyzer) {
        this.fLimitAnalyzer = fLimitAnalyzer;
    }


    public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } public Boolean getFeatureDefault(String featureId) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
            if (RECOGNIZED_FEATURES[i].equals(featureId)) {
                return FEATURE_DEFAULTS[i];
            }
        }
        return null;
    } public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
            if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    } public static String expandSystemId(String systemId) {
        return expandSystemId(systemId, null);
    } private static String gUserDir;
    private static URI gUserDirURI;
    private static boolean gNeedEscaping[] = new boolean[128];
    private static char gAfterEscaping1[] = new char[128];
    private static char gAfterEscaping2[] = new char[128];
    private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
                                     '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static {
        for (int i = 0; i <= 0x1f; i++) {
            gNeedEscaping[i] = true;
            gAfterEscaping1[i] = gHexChs[i >> 4];
            gAfterEscaping2[i] = gHexChs[i & 0xf];
        }
        gNeedEscaping[0x7f] = true;
        gAfterEscaping1[0x7f] = '7';
        gAfterEscaping2[0x7f] = 'F';
        char[] escChs = {' ', '<', '>', '#', '%', '"', '{', '}',
                         '|', '\\', '^', '~', '[', ']', '`'};
        int len = escChs.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = escChs[i];
            gNeedEscaping[ch] = true;
            gAfterEscaping1[ch] = gHexChs[ch >> 4];
            gAfterEscaping2[ch] = gHexChs[ch & 0xf];
        }
    }

    private static synchronized URI getUserDir() throws URI.MalformedURIException {
        String userDir = "";
        try {
            userDir = SecuritySupport.getSystemProperty("user.dir");
        }
        catch (SecurityException se) {
        }

        if (userDir.length() == 0)
            return new URI("file", "", "", null, null);
        if (gUserDirURI != null && userDir.equals(gUserDir)) {
            return gUserDirURI;
        }

        gUserDir = userDir;

        char separator = java.io.File.separatorChar;
        userDir = userDir.replace(separator, '/');

        int len = userDir.length(), ch;
        StringBuffer buffer = new StringBuffer(len*3);
        if (len >= 2 && userDir.charAt(1) == ':') {
            ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
                buffer.append('/');
            }
        }

        int i = 0;
        for (; i < len; i++) {
            ch = userDir.charAt(i);
            if (ch >= 128)
                break;
            if (gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(gAfterEscaping1[ch]);
                buffer.append(gAfterEscaping2[ch]);
                }
            else {
                buffer.append((char)ch);
            }
        }

        if (i < len) {
            byte[] bytes = null;
            byte b;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                return new URI("file", "", userDir, null, null);
            }
            len = bytes.length;

            for (i = 0; i < len; i++) {
                b = bytes[i];
                if (b < 0) {
                    ch = b + 256;
                    buffer.append('%');
                    buffer.append(gHexChs[ch >> 4]);
                    buffer.append(gHexChs[ch & 0xf]);
                }
                else if (gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(gAfterEscaping1[b]);
                    buffer.append(gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }

        if (!userDir.endsWith("/"))
            buffer.append('/');

        gUserDirURI = new URI("file", "", buffer.toString(), null, null);

        return gUserDirURI;
    }


    public static void absolutizeAgainstUserDir(URI uri)
        throws URI.MalformedURIException {
        uri.absolutize(getUserDir());
    }


    public static String expandSystemId(String systemId, String baseSystemId) {

        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        try {
            URI uri = new URI(systemId);
            if (uri != null) {
                return systemId;
            }
        } catch (URI.MalformedURIException e) {
            }
        String id = fixURI(systemId);

        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 ||
                    baseSystemId.equals(systemId)) {
                String dir = getUserDir().toString();
                base = new URI("file", "", dir, null, null);
            } else {
                try {
                    base = new URI(fixURI(baseSystemId));
                } catch (URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(':') != -1) {
                        base = new URI("file", "", fixURI(baseSystemId), null, null);
                    } else {
                        String dir = getUserDir().toString();
                        dir = dir + fixURI(baseSystemId);
                        base = new URI("file", "", dir, null, null);
                    }
                }
            }
            uri = new URI(base, id);
        } catch (Exception e) {
            }

        if (uri == null) {
            return systemId;
        }
        return uri.toString();

    } public static String expandSystemId(String systemId, String baseSystemId,
                                        boolean strict)
            throws URI.MalformedURIException {

        if (systemId == null) {
            return null;
        }

        if (strict) {
            try {
                new URI(systemId);
                return systemId;
            }
            catch (URI.MalformedURIException ex) {
            }
            URI base = null;
            if (baseSystemId == null || baseSystemId.length() == 0) {
                base = new URI("file", "", getUserDir().toString(), null, null);
            }
            else {
                try {
                    base = new URI(baseSystemId);
                }
                catch (URI.MalformedURIException e) {
                    String dir = getUserDir().toString();
                    dir = dir + baseSystemId;
                    base = new URI("file", "", dir, null, null);
                }
            }
            URI uri = new URI(base, systemId);
            return uri.toString();

            }

        try {
             return expandSystemIdStrictOff(systemId, baseSystemId);
        }
        catch (URI.MalformedURIException e) {

            try {
                return expandSystemIdStrictOff1(systemId, baseSystemId);
            } catch (URISyntaxException ex) {
                }
        }
        if (systemId.length() == 0) {
            return systemId;
        }

        String id = fixURI(systemId);

        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 ||
                baseSystemId.equals(systemId)) {
                base = getUserDir();
            }
            else {
                try {
                    base = new URI(fixURI(baseSystemId).trim());
                }
                catch (URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(':') != -1) {
                        base = new URI("file", "", fixURI(baseSystemId).trim(), null, null);
                    }
                    else {
                        base = new URI(getUserDir(), fixURI(baseSystemId));
                    }
                }
             }
             uri = new URI(base, id.trim());
        }
        catch (Exception e) {
            }

        if (uri == null) {
            return systemId;
        }
        return uri.toString();

    } private static String expandSystemIdStrictOn(String systemId, String baseSystemId)
        throws URI.MalformedURIException {

        URI systemURI = new URI(systemId, true);
        if (systemURI.isAbsoluteURI()) {
            return systemId;
        }

        URI baseURI = null;
        if (baseSystemId == null || baseSystemId.length() == 0) {
            baseURI = getUserDir();
        }
        else {
            baseURI = new URI(baseSystemId, true);
            if (!baseURI.isAbsoluteURI()) {
                baseURI.absolutize(getUserDir());
            }
        }

        systemURI.absolutize(baseURI);

        return systemURI.toString();

        } public static void setInstanceFollowRedirects(HttpURLConnection urlCon, boolean followRedirects) {
        try {
            Method method = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", new Class[] {Boolean.TYPE});
            method.invoke(urlCon, new Object[] {followRedirects ? Boolean.TRUE : Boolean.FALSE});
        }
        catch (Exception exc) {}
    }



    private static String expandSystemIdStrictOff(String systemId, String baseSystemId)
        throws URI.MalformedURIException {

        URI systemURI = new URI(systemId, true);
        if (systemURI.isAbsoluteURI()) {
            if (systemURI.getScheme().length() > 1) {
                return systemId;
            }

            throw new URI.MalformedURIException();
        }

        URI baseURI = null;
        if (baseSystemId == null || baseSystemId.length() == 0) {
            baseURI = getUserDir();
        }
        else {
            baseURI = new URI(baseSystemId, true);
            if (!baseURI.isAbsoluteURI()) {
                baseURI.absolutize(getUserDir());
            }
        }

        systemURI.absolutize(baseURI);

        return systemURI.toString();

        } private static String expandSystemIdStrictOff1(String systemId, String baseSystemId)
        throws URISyntaxException, URI.MalformedURIException {

            java.net.URI systemURI = new java.net.URI(systemId);
        if (systemURI.isAbsolute()) {
            if (systemURI.getScheme().length() > 1) {
                return systemId;
            }

            throw new URISyntaxException(systemId, "the scheme's length is only one character");
        }

        URI baseURI = null;
        if (baseSystemId == null || baseSystemId.length() == 0) {
            baseURI = getUserDir();
        }
        else {
            baseURI = new URI(baseSystemId, true);
            if (!baseURI.isAbsoluteURI()) {
                baseURI.absolutize(getUserDir());
            }
        }

        systemURI = (new java.net.URI(baseURI.toString())).resolve(systemURI);

        return systemURI.toString();

        } protected Object[] getEncodingName(byte[] b4, int count) {

        if (count < 2) {
            return defaultEncoding;
        }

        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 0xFE && b1 == 0xFF) {
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0xFF && b1 == 0xFE) {
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }

        if (count < 3) {
            return defaultEncoding;
        }

        int b2 = b4[2] & 0xFF;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
            return defaultEncoding;
        }

        if (count < 4) {
            return defaultEncoding;
        }

        int b3 = b4[3] & 0xFF;
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x00 && b3 == 0x3C) {
            return new Object [] {"ISO-10646-UCS-4", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x00 && b3 == 0x00) {
            return new Object [] {"ISO-10646-UCS-4", new Boolean(false)};
        }
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x3C && b3 == 0x00) {
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x00) {
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }
        if (b0 == 0x4C && b1 == 0x6F && b2 == 0xA7 && b3 == 0x94) {
            return new Object [] {"CP037", null};
        }

        return defaultEncoding;

    } protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian)
    throws IOException {

        if (encoding == null) {
            encoding = "UTF-8";
        }

        String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating UTF8Reader");
            }
            return new UTF8Reader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale() );
        }
        if (ENCODING.equals("US-ASCII")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating ASCIIReader");
            }
            return new ASCIIReader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale());
        }
        if(ENCODING.equals("ISO-10646-UCS-4")) {
            if(isBigEndian != null) {
                boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS4BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS4LE);
                }
            } else {
                fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                        "EncodingByteOrderUnsupported",
                        new Object[] { encoding },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
        }
        if(ENCODING.equals("ISO-10646-UCS-2")) {
            if(isBigEndian != null) { boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS2BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS2LE);
                }
            } else {
                fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                        "EncodingByteOrderUnsupported",
                        new Object[] { encoding },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
        }

        boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (fAllowJavaEncodings && !validJava)) {
            fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                    "EncodingDeclInvalid",
                    new Object[] { encoding },
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    encoding = "ISO-8859-1";
        }

        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if(fAllowJavaEncodings) {
                javaEncoding = encoding;
            } else {
                fErrorReporter.reportError(this.getEntityScanner(),XMLMessageFormatter.XML_DOMAIN,
                        "EncodingDeclInvalid",
                        new Object[] { encoding },
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                        javaEncoding = "ISO8859_1";
            }
        }
        if (DEBUG_ENCODINGS) {
            System.out.print("$$$ creating Java InputStreamReader: encoding="+javaEncoding);
            if (javaEncoding == encoding) {
                System.out.print(" (IANA encoding)");
            }
            System.out.println();
        }
        return new BufferedReader( new InputStreamReader(inputStream, javaEncoding));

    } public String getPublicId() {
        return (fCurrentEntity != null && fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getPublicId() : null;
    } public String getExpandedSystemId() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.entityLocation != null &&
                    fCurrentEntity.entityLocation.getExpandedSystemId() != null ) {
                return fCurrentEntity.entityLocation.getExpandedSystemId();
            } else {
                int size = fEntityStack.size();
                for (int i = size - 1; i >= 0 ; i--) {
                    Entity.ScannedEntity externalEntity =
                            (Entity.ScannedEntity)fEntityStack.elementAt(i);

                    if (externalEntity.entityLocation != null &&
                            externalEntity.entityLocation.getExpandedSystemId() != null) {
                        return externalEntity.entityLocation.getExpandedSystemId();
                    }
                }
            }
        }
        return null;
    } public String getLiteralSystemId() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.entityLocation != null &&
                    fCurrentEntity.entityLocation.getLiteralSystemId() != null ) {
                return fCurrentEntity.entityLocation.getLiteralSystemId();
            } else {
                int size = fEntityStack.size();
                for (int i = size - 1; i >= 0 ; i--) {
                    Entity.ScannedEntity externalEntity =
                            (Entity.ScannedEntity)fEntityStack.elementAt(i);

                    if (externalEntity.entityLocation != null &&
                            externalEntity.entityLocation.getLiteralSystemId() != null) {
                        return externalEntity.entityLocation.getLiteralSystemId();
                    }
                }
            }
        }
        return null;
    } public int getLineNumber() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.isExternal()) {
                return fCurrentEntity.lineNumber;
            } else {
                int size = fEntityStack.size();
                for (int i=size-1; i>0 ; i--) {
                    Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    if (firstExternalEntity.isExternal()) {
                        return firstExternalEntity.lineNumber;
                    }
                }
            }
        }

        return -1;

    } public int getColumnNumber() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.isExternal()) {
                return fCurrentEntity.columnNumber;
            } else {
                int size = fEntityStack.size();
                for (int i=size-1; i>0 ; i--) {
                    Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    if (firstExternalEntity.isExternal()) {
                        return firstExternalEntity.columnNumber;
                    }
                }
            }
        }

        return -1;
    } protected static String fixURI(String str) {

        str = str.replace(java.io.File.separatorChar, '/');

        if (str.length() >= 2) {
            char ch1 = str.charAt(1);
            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            }
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }

        int pos = str.indexOf(' ');
        if (pos >= 0) {
            StringBuilder sb = new StringBuilder(str.length());
            for (int i = 0; i < pos; i++)
                sb.append(str.charAt(i));
            sb.append("%20");
            for (int i = pos+1; i < str.length(); i++) {
                if (str.charAt(i) == ' ')
                    sb.append("%20");
                else
                    sb.append(str.charAt(i));
            }
            str = sb.toString();
        }

        return str;

    } final void print() {
        if (DEBUG_BUFFER) {
            if (fCurrentEntity != null) {
                System.out.print('[');
                System.out.print(fCurrentEntity.count);
                System.out.print(' ');
                System.out.print(fCurrentEntity.position);
                if (fCurrentEntity.count > 0) {
                    System.out.print(" \"");
                    for (int i = 0; i < fCurrentEntity.count; i++) {
                        if (i == fCurrentEntity.position) {
                            System.out.print('^');
                        }
                        char c = fCurrentEntity.ch[i];
                        switch (c) {
                            case '\n': {
                                System.out.print("\\n");
                                break;
                            }
                            case '\r': {
                                System.out.print("\\r");
                                break;
                            }
                            case '\t': {
                                System.out.print("\\t");
                                break;
                            }
                            case '\\': {
                                System.out.print("\\\\");
                                break;
                            }
                            default: {
                                System.out.print(c);
                            }
                        }
                    }
                    if (fCurrentEntity.position == fCurrentEntity.count) {
                        System.out.print('^');
                    }
                    System.out.print('"');
                }
                System.out.print(']');
                System.out.print(" @ ");
                System.out.print(fCurrentEntity.lineNumber);
                System.out.print(',');
                System.out.print(fCurrentEntity.columnNumber);
            } else {
                System.out.print("*NO CURRENT ENTITY*");
            }
        }
    } private static class CharacterBuffer {


        private char[] ch;


        private boolean isExternal;

        public CharacterBuffer(boolean isExternal, int size) {
            this.isExternal = isExternal;
            ch = new char[size];
        }
    }



    private static class CharacterBufferPool {

        private static final int DEFAULT_POOL_SIZE = 3;

        private CharacterBuffer[] fInternalBufferPool;
        private CharacterBuffer[] fExternalBufferPool;

        private int fExternalBufferSize;
        private int fInternalBufferSize;
        private int poolSize;

        private int fInternalTop;
        private int fExternalTop;

        public CharacterBufferPool(int externalBufferSize, int internalBufferSize) {
            this(DEFAULT_POOL_SIZE, externalBufferSize, internalBufferSize);
        }

        public CharacterBufferPool(int poolSize, int externalBufferSize, int internalBufferSize) {
            fExternalBufferSize = externalBufferSize;
            fInternalBufferSize = internalBufferSize;
            this.poolSize = poolSize;
            init();
        }


        private void init() {
            fInternalBufferPool = new CharacterBuffer[poolSize];
            fExternalBufferPool = new CharacterBuffer[poolSize];
            fInternalTop = -1;
            fExternalTop = -1;
        }


        public CharacterBuffer getBuffer(boolean external) {
            if (external) {
                if (fExternalTop > -1) {
                    return (CharacterBuffer)fExternalBufferPool[fExternalTop--];
                }
                else {
                    return new CharacterBuffer(true, fExternalBufferSize);
                }
            }
            else {
                if (fInternalTop > -1) {
                    return (CharacterBuffer)fInternalBufferPool[fInternalTop--];
                }
                else {
                    return new CharacterBuffer(false, fInternalBufferSize);
                }
            }
        }


        public void returnToPool(CharacterBuffer buffer) {
            if (buffer.isExternal) {
                if (fExternalTop < fExternalBufferPool.length - 1) {
                    fExternalBufferPool[++fExternalTop] = buffer;
                }
            }
            else if (fInternalTop < fInternalBufferPool.length - 1) {
                fInternalBufferPool[++fInternalTop] = buffer;
            }
        }


        public void setExternalBufferSize(int bufferSize) {
            fExternalBufferSize = bufferSize;
            fExternalBufferPool = new CharacterBuffer[poolSize];
            fExternalTop = -1;
        }
    }



    protected final class RewindableInputStream extends InputStream {

        private InputStream fInputStream;
        private byte[] fData;
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;

        public RewindableInputStream(InputStream is) {
            fData = new byte[DEFAULT_XMLDECL_BUFFER_SIZE];
            fInputStream = is;
            fStartOffset = 0;
            fEndOffset = -1;
            fOffset = 0;
            fLength = 0;
            fMark = 0;
        }

        public void setStartOffset(int offset) {
            fStartOffset = offset;
        }

        public void rewind() {
            fOffset = fStartOffset;
        }

        public int read() throws IOException {
            int b = 0;
            if (fOffset < fLength) {
                return fData[fOffset++] & 0xff;
            }
            if (fOffset == fEndOffset) {
                return -1;
            }
            if (fOffset == fData.length) {
                byte[] newData = new byte[fOffset << 1];
                System.arraycopy(fData, 0, newData, 0, fOffset);
                fData = newData;
            }
            b = fInputStream.read();
            if (b == -1) {
                fEndOffset = fOffset;
                return -1;
            }
            fData[fLength++] = (byte)b;
            fOffset++;
            return b & 0xff;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }



                if(fCurrentEntity.mayReadChunks || !fCurrentEntity.xmlDeclChunkRead) {

                    if (!fCurrentEntity.xmlDeclChunkRead)
                    {
                        fCurrentEntity.xmlDeclChunkRead = true;
                        len = Entity.ScannedEntity.DEFAULT_XMLDECL_BUFFER_SIZE;
                    }
                    return fInputStream.read(b, off, len);
                }

                int returnedVal = read();
                if(returnedVal == -1) {
                  fEndOffset = fOffset;
                  return -1;
                }
                b[off] = (byte)returnedVal;
                return 1;

            }
            if (len < bytesLeft) {
                if (len <= 0) {
                    return 0;
                }
            } else {
                len = bytesLeft;
            }
            if (b != null) {
                System.arraycopy(fData, fOffset, b, off, len);
            }
            fOffset += len;
            return len;
        }

        public long skip(long n)
        throws IOException {
            int bytesLeft;
            if (n <= 0) {
                return 0;
            }
            bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return 0;
                }
                return fInputStream.skip(n);
            }
            if (n <= bytesLeft) {
                fOffset += n;
                return n;
            }
            fOffset += bytesLeft;
            if (fOffset == fEndOffset) {
                return bytesLeft;
            }
            n -= bytesLeft;

            return fInputStream.skip(n) + bytesLeft;
        }

        public int available() throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }
                return fCurrentEntity.mayReadChunks ? fInputStream.available()
                : 0;
            }
            return bytesLeft;
        }

        public void mark(int howMuch) {
            fMark = fOffset;
        }

        public void reset() {
            fOffset = fMark;
            }

        public boolean markSupported() {
            return true;
        }

        public void close() throws IOException {
            if (fInputStream != null) {
                fInputStream.close();
                fInputStream = null;
            }
        }
    } public void test(){
        fEntityStorage.addExternalEntity("entityUsecase1",null,
                "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt",
                "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");

        fEntityStorage.addInternalEntity("entityUsecase2","<Test>value</Test>");
        fEntityStorage.addInternalEntity("entityUsecase3","value3");
        fEntityStorage.addInternalEntity("text", "Hello World.");
        fEntityStorage.addInternalEntity("empty-element", "<foo/>");
        fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
        fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
        fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
        fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
        fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
        fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
        fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
        fEntityStorage.addInternalEntity("ch","&#x00A9;");
        fEntityStorage.addInternalEntity("ch1","&#84;");
        fEntityStorage.addInternalEntity("% ch2","param");
    }

}