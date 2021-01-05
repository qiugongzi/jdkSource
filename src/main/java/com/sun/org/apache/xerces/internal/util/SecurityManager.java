


package com.sun.org.apache.xerces.internal.util;
import com.sun.org.apache.xerces.internal.impl.Constants;

public final class SecurityManager {

    private final static int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;


    private final static int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;

    private final static int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;


    private int entityExpansionLimit;


    private int maxOccurLimit;

        private int fElementAttributeLimit;
    public SecurityManager() {
        entityExpansionLimit = DEFAULT_ENTITY_EXPANSION_LIMIT;
        maxOccurLimit = DEFAULT_MAX_OCCUR_NODE_LIMIT ;
                fElementAttributeLimit = DEFAULT_ELEMENT_ATTRIBUTE_LIMIT;
                readSystemProperties();
    }


    public void setEntityExpansionLimit(int limit) {
        entityExpansionLimit = limit;
    }


    public int getEntityExpansionLimit() {
        return entityExpansionLimit;
    }


    public void setMaxOccurNodeLimit(int limit){
        maxOccurLimit = limit;
    }


    public int getMaxOccurNodeLimit(){
        return maxOccurLimit;
    }

    public int getElementAttrLimit(){
                return fElementAttributeLimit;
        }

        public void setElementAttrLimit(int limit){
                fElementAttributeLimit = limit;
        }

        private void readSystemProperties(){

                try {
                        String value = System.getProperty(Constants.ENTITY_EXPANSION_LIMIT);
                        if(value != null && !value.equals("")){
                                entityExpansionLimit = Integer.parseInt(value);
                                if (entityExpansionLimit < 0)
                                        entityExpansionLimit = DEFAULT_ENTITY_EXPANSION_LIMIT;
                        }
                        else
                                entityExpansionLimit = DEFAULT_ENTITY_EXPANSION_LIMIT;
                }catch(Exception ex){}

                try {
                        String value = System.getProperty(Constants.MAX_OCCUR_LIMIT);
                        if(value != null && !value.equals("")){
                                maxOccurLimit = Integer.parseInt(value);
                                if (maxOccurLimit < 0)
                                        maxOccurLimit = DEFAULT_MAX_OCCUR_NODE_LIMIT;
                        }
                        else
                                maxOccurLimit = DEFAULT_MAX_OCCUR_NODE_LIMIT;
                }catch(Exception ex){}

                try {
                        String value = System.getProperty(Constants.ELEMENT_ATTRIBUTE_LIMIT);
                        if(value != null && !value.equals("")){
                                fElementAttributeLimit = Integer.parseInt(value);
                                if ( fElementAttributeLimit < 0)
                                        fElementAttributeLimit = DEFAULT_ELEMENT_ATTRIBUTE_LIMIT;
                        }
                        else
                                fElementAttributeLimit = DEFAULT_ELEMENT_ATTRIBUTE_LIMIT;

                }catch(Exception ex){}

        }

}