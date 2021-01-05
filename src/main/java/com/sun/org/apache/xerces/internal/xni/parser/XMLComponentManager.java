


package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;


public interface XMLComponentManager {

    public boolean getFeature(String featureId)
        throws XMLConfigurationException;


    public boolean getFeature(String featureId, boolean defaultValue);


    public Object getProperty(String propertyId)
        throws XMLConfigurationException;


    public Object getProperty(String propertyId, Object defaultObject);

    public FeatureState getFeatureState(String featureId);

    public PropertyState getPropertyState(String propertyId);

}