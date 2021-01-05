


package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;


public class DatatypeException extends Exception {


    static final long serialVersionUID = 1940805832730465578L;

    protected String key;
    protected Object[] args;


    public DatatypeException(String key, Object[] args) {
        super(key);
        this.key = key;
        this.args = args;
    }


    public String getKey() {
        return key;
    }


    public Object[] getArgs() {
        return args;
    }


    public String getMessage() {
        ResourceBundle resourceBundle = null;
        resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
        if (resourceBundle == null)
            throw new MissingResourceException("Property file not found!", "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);

        String msg = resourceBundle.getString(key);
        if (msg == null) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);
        }

        if (args != null) {
            try {
                msg = java.text.MessageFormat.format(msg, args);
            } catch (Exception e) {
                msg = resourceBundle.getString("FormatFailed");
                msg += " " + resourceBundle.getString(key);
            }
        }

        return msg;
    }
}
