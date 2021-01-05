

package com.sun.org.apache.xml.internal.security.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class I18n {


    public static final String NOT_INITIALIZED_MSG =
        "You must initialize the xml-security library correctly before you use it. "
        + "Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that "
        + "before you use any functionality from that library.";


    private static ResourceBundle resourceBundle;


    private static boolean alreadyInitialized = false;


    private I18n() {
        }


    public static String translate(String message, Object[] args) {
        return getExceptionMessage(message, args);
    }


    public static String translate(String message) {
        return getExceptionMessage(message);
    }


    public static String getExceptionMessage(String msgID) {
        try {
            return resourceBundle.getString(msgID);
        } catch (Throwable t) {
            if (com.sun.org.apache.xml.internal.security.Init.isInitialized()) {
                return "No message with ID \"" + msgID
                + "\" found in resource bundle \""
                + Constants.exceptionMessagesResourceBundleBase + "\"";
            }
            return I18n.NOT_INITIALIZED_MSG;
        }
    }


    public static String getExceptionMessage(String msgID, Exception originalException) {
        try {
            Object exArgs[] = { originalException.getMessage() };
            return MessageFormat.format(resourceBundle.getString(msgID), exArgs);
        } catch (Throwable t) {
            if (com.sun.org.apache.xml.internal.security.Init.isInitialized()) {
                return "No message with ID \"" + msgID
                + "\" found in resource bundle \""
                + Constants.exceptionMessagesResourceBundleBase
                + "\". Original Exception was a "
                + originalException.getClass().getName() + " and message "
                + originalException.getMessage();
            }
            return I18n.NOT_INITIALIZED_MSG;
        }
    }


    public static String getExceptionMessage(String msgID, Object exArgs[]) {
        try {
            return MessageFormat.format(resourceBundle.getString(msgID), exArgs);
        } catch (Throwable t) {
            if (com.sun.org.apache.xml.internal.security.Init.isInitialized()) {
                return "No message with ID \"" + msgID
                + "\" found in resource bundle \""
                + Constants.exceptionMessagesResourceBundleBase + "\"";
            }
            return I18n.NOT_INITIALIZED_MSG;
        }
    }


    public synchronized static void init(String languageCode, String countryCode) {
        if (alreadyInitialized) {
            return;
        }

        I18n.resourceBundle =
            ResourceBundle.getBundle(
                Constants.exceptionMessagesResourceBundleBase,
                new Locale(languageCode, countryCode)
            );
        alreadyInitialized = true;
    }
}
