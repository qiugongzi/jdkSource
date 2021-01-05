

package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;



public class PropertyManager {


    public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    public static final String STAX_ENTITIES = "javax.xml.stream.entities";

    private static final String STRING_INTERNING = "http:private static final String SECURITY_MANAGER = Constants.SECURITY_MANAGER;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    HashMap supportedProps = new HashMap();

    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;

    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;


    public PropertyManager(int context) {
        switch(context){
            case CONTEXT_READER:{
                initConfigurableReaderProperties();
                break;
            }
            case CONTEXT_WRITER:{
                initWriterProps();
                break;
            }
        }
    }


    public PropertyManager(PropertyManager propertyManager){

        HashMap properties = propertyManager.getProperties();
        supportedProps.putAll(properties);
        fSecurityManager = (XMLSecurityManager)getProperty(SECURITY_MANAGER);
        fSecurityPropertyMgr = (XMLSecurityPropertyManager)getProperty(XML_SECURITY_PROPERTY_MANAGER);
    }

    private HashMap getProperties(){
        return supportedProps ;
    }



    private void initConfigurableReaderProperties(){
        supportedProps.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.REPORTER, null);
        supportedProps.put(XMLInputFactory.RESOLVER, null);
        supportedProps.put(XMLInputFactory.ALLOCATOR, null);
        supportedProps.put(STAX_NOTATIONS,null );

        supportedProps.put(Constants.SAX_FEATURE_PREFIX + Constants.STRING_INTERNING_FEATURE , new Boolean(true));
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE,  new Boolean(true)) ;
        supportedProps.put(Constants.ADD_NAMESPACE_DECL_AS_ATTRIBUTE ,  Boolean.FALSE) ;
        supportedProps.put(Constants.READER_IN_DEFINED_STATE, new Boolean(true));
        supportedProps.put(Constants.REUSE_INSTANCE, new Boolean(true));
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.STAX_REPORT_CDATA_EVENT , new Boolean(false));
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IGNORE_EXTERNAL_DTD, Boolean.FALSE);
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ATTDEF_FEATURE, new Boolean(false));
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE, new Boolean(false));
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_UNDECLARED_ELEMDEF_FEATURE, new Boolean(false));

        fSecurityManager = new XMLSecurityManager(true);
        supportedProps.put(SECURITY_MANAGER, fSecurityManager);
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        supportedProps.put(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
    }

    private void initWriterProps(){
        supportedProps.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES , Boolean.FALSE);
        supportedProps.put(Constants.ESCAPE_CHARACTERS , Boolean.TRUE);
        supportedProps.put(Constants.REUSE_INSTANCE, new Boolean(true));
    }


    public boolean containsProperty(String property){
        return supportedProps.containsKey(property) ||
                (fSecurityManager != null && fSecurityManager.getIndex(property) > -1) ||
                (fSecurityPropertyMgr!=null && fSecurityPropertyMgr.getIndex(property) > -1) ;
    }

    public Object getProperty(String property){
        return supportedProps.get(property);
    }

    public void setProperty(String property, Object value){
        String equivalentProperty = null ;
        if(property == XMLInputFactory.IS_NAMESPACE_AWARE || property.equals(XMLInputFactory.IS_NAMESPACE_AWARE)){
            equivalentProperty = Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE ;
        }
        else if(property == XMLInputFactory.IS_VALIDATING || property.equals(XMLInputFactory.IS_VALIDATING)){
            if( (value instanceof Boolean) && ((Boolean)value).booleanValue()){
                throw new java.lang.IllegalArgumentException("true value of isValidating not supported") ;
            }
        }
        else if(property == STRING_INTERNING || property.equals(STRING_INTERNING)){
            if( (value instanceof Boolean) && !((Boolean)value).booleanValue()){
                throw new java.lang.IllegalArgumentException("false value of " + STRING_INTERNING + "feature is not supported") ;
            }
        }
        else if(property == XMLInputFactory.RESOLVER || property.equals(XMLInputFactory.RESOLVER)){
            supportedProps.put( Constants.XERCES_PROPERTY_PREFIX + Constants.STAX_ENTITY_RESOLVER_PROPERTY , new StaxEntityResolverWrapper((XMLResolver)value)) ;
        }


        if (property.equals(Constants.SECURITY_MANAGER)) {
            fSecurityManager = XMLSecurityManager.convert(value, fSecurityManager);
            supportedProps.put(Constants.SECURITY_MANAGER, fSecurityManager);
            return;
        }
        if (property.equals(Constants.XML_SECURITY_PROPERTY_MANAGER)) {
            if (value == null) {
                fSecurityPropertyMgr = new XMLSecurityPropertyManager();
            } else {
                fSecurityPropertyMgr = (XMLSecurityPropertyManager)value;
            }
            supportedProps.put(Constants.XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
            return;
        }

        if (fSecurityManager == null ||
                !fSecurityManager.setLimit(property, XMLSecurityManager.State.APIPROPERTY, value)) {
            if (fSecurityPropertyMgr == null ||
                    !fSecurityPropertyMgr.setValue(property, XMLSecurityPropertyManager.State.APIPROPERTY, value)) {
                supportedProps.put(property, value);
            }
        }

        if(equivalentProperty != null){
            supportedProps.put(equivalentProperty, value ) ;
        }
    }

    public String toString(){
        return supportedProps.toString();
    }

}