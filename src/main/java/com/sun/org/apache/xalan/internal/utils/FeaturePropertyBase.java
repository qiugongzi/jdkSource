

package com.sun.org.apache.xalan.internal.utils;

import com.sun.org.apache.xalan.internal.XalanConstants;


public abstract class FeaturePropertyBase {


    public static enum State {
        DEFAULT, FSP, JAXPDOTPROPERTIES, SYSTEMPROPERTY, APIPROPERTY
    }



    String[] values = null;

    State[] states = {State.DEFAULT, State.DEFAULT};



    public void setValue(Enum property, State state, String value) {
        if (state.compareTo(states[property.ordinal()]) >= 0) {
            values[property.ordinal()] = value;
            states[property.ordinal()] = state;
        }
    }


    public void setValue(int index, State state, String value) {
        if (state.compareTo(states[index]) >= 0) {
            values[index] = value;
            states[index] = state;
        }
    }


    public boolean setValue(String propertyName, State state, Object value) {
        int index = getIndex(propertyName);
        if (index > -1) {
            setValue(index, state, (String)value);
            return true;
        }
        return false;
    }


    public boolean setValue(String propertyName, State state, boolean value) {
        int index = getIndex(propertyName);
        if (index > -1) {
            if (value) {
                setValue(index, state, XalanConstants.FEATURE_TRUE);
            } else {
                setValue(index, state, XalanConstants.FEATURE_FALSE);
            }
            return true;
        }
        return false;
    }


    public String getValue(Enum property) {
        return values[property.ordinal()];
    }


    public String getValue(String property) {
        int index = getIndex(property);
        if (index > -1) {
            return getValueByIndex(index);
        }
        return null;
    }


    public String getValueAsString(String propertyName) {
        int index = getIndex(propertyName);
        if (index > -1) {
            return getValueByIndex(index);
        }

        return null;
    }


    public String getValueByIndex(int index) {
        return values[index];
    }


    public abstract int getIndex(String propertyName);

    public <E extends Enum<E>> int getIndex(Class<E> property, String propertyName) {
        for (Enum<E> enumItem : property.getEnumConstants()) {
            if (enumItem.toString().equals(propertyName)) {
                return enumItem.ordinal();
            }
        }
        return -1;
    };



    void getSystemProperty(Enum property, String systemProperty) {
        try {
            String value = SecuritySupport.getSystemProperty(systemProperty);
            if (value != null) {
                values[property.ordinal()] = value;
                states[property.ordinal()] = State.SYSTEMPROPERTY;
                return;
            }

            value = SecuritySupport.readJAXPProperty(systemProperty);
            if (value != null) {
                values[property.ordinal()] = value;
                states[property.ordinal()] = State.JAXPDOTPROPERTIES;
            }
        } catch (NumberFormatException e) {
            }
    }
}
