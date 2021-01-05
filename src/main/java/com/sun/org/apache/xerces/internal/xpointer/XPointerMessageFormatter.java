

package com.sun.org.apache.xerces.internal.xpointer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;


class XPointerMessageFormatter implements MessageFormatter {

    public static final String XPOINTER_DOMAIN = "http:private Locale fLocale = null;

    private ResourceBundle fResourceBundle = null;


    public String formatMessage(Locale locale, String key, Object[] arguments)
            throws MissingResourceException {

        if (fResourceBundle == null || locale != fLocale) {
            if (locale != null) {
                fResourceBundle = SecuritySupport.getResourceBundle(
                        "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", locale);
                fLocale = locale;
            }
            if (fResourceBundle == null)
                fResourceBundle = SecuritySupport.getResourceBundle(
                        "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages");
        }

        String msg = fResourceBundle.getString(key);
        if (arguments != null) {
            try {
                msg = java.text.MessageFormat.format(msg, arguments);
            } catch (Exception e) {
                msg = fResourceBundle.getString("FormatFailed");
                msg += " " + fResourceBundle.getString(key);
            }
        }

        if (msg == null) {
            msg = fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg,
                    "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", key);
        }

        return msg;
    }
}
