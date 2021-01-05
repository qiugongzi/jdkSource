



package com.sun.org.apache.xerces.internal.dom;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class DOMMessageFormatter {
    public static final String DOM_DOMAIN = "http:public static final String XML_DOMAIN = "http:public static final String SERIALIZER_DOMAIN = "http:private static ResourceBundle domResourceBundle = null;
    private static ResourceBundle xmlResourceBundle = null;
    private static ResourceBundle serResourceBundle = null;
    private static Locale locale = null;


    DOMMessageFormatter(){
        locale = Locale.getDefault();
    }

    public static String formatMessage(String domain,
    String key, Object[] arguments)
    throws MissingResourceException {
        ResourceBundle resourceBundle = getResourceBundle(domain);
        if(resourceBundle == null){
            init();
            resourceBundle = getResourceBundle(domain);
            if(resourceBundle == null)
                throw new MissingResourceException("Unknown domain" + domain, null, key);
        }
        String msg;
        try {
            msg = key + ": " + resourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = java.text.MessageFormat.format(msg, arguments);
                }
                catch (Exception e) {
                    msg = resourceBundle.getString("FormatFailed");
                    msg += " " + resourceBundle.getString(key);
                }
            }
        } catch (MissingResourceException e) {
            msg = resourceBundle.getString("BadMessageKey");
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

    static ResourceBundle getResourceBundle(String domain){
        if(domain == DOM_DOMAIN || domain.equals(DOM_DOMAIN))
            return domResourceBundle;
        else if( domain == XML_DOMAIN || domain.equals(XML_DOMAIN))
            return xmlResourceBundle;
        else if(domain == SERIALIZER_DOMAIN || domain.equals(SERIALIZER_DOMAIN))
            return serResourceBundle;
        return null;
    }

    public static void init(){
        if (locale != null) {
            domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", locale);
            serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", locale);
            xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
        }else{
            domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
            serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
            xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
        }
    }


    public static void setLocale(Locale dlocale){
        locale = dlocale;
    }
}
