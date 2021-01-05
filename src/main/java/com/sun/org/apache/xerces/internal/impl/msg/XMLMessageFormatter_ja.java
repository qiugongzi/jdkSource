


package com.sun.org.apache.xerces.internal.impl.msg;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;


public class XMLMessageFormatter_ja implements MessageFormatter {

    public static final String XML_DOMAIN = "http:public static final String XMLNS_DOMAIN = "http:private Locale fLocale = null;
    private ResourceBundle fResourceBundle = null;

    public String formatMessage(Locale locale, String key, Object[] arguments)
        throws MissingResourceException {

        if (fResourceBundle == null || locale != fLocale) {
            if (locale != null) {
                fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
                fLocale = locale;
            }
            if (fResourceBundle == null)
                fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
        }

        String msg;
        try {
            msg = fResourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = java.text.MessageFormat.format(msg, arguments);
                }
                catch (Exception e) {
                    msg = fResourceBundle.getString("FormatFailed");
                    msg += " " + fResourceBundle.getString(key);
                }
            }
        }

        catch (MissingResourceException e) {
            msg = fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg, key);
        }

        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                StringBuffer str = new StringBuffer(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(String.valueOf(arguments[i]));
                }
            }
        }

        return msg;
    }

}
