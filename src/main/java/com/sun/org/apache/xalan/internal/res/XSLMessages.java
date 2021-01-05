


package com.sun.org.apache.xalan.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ListResourceBundle;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;


public class XSLMessages extends XPATHMessages {


    private static ListResourceBundle XSLTBundle = null;

    private static final String XSLT_ERROR_RESOURCES =
            "com.sun.org.apache.xalan.internal.res.XSLTErrorResources";


    public static String createMessage(String msgKey, Object args[]) {
        if (XSLTBundle == null) {
            XSLTBundle = SecuritySupport.getResourceBundle(XSLT_ERROR_RESOURCES);
        }

        if (XSLTBundle != null) {
            return createMsg(XSLTBundle, msgKey, args);
        } else {
            return "Could not load any resource bundles.";
        }
    }


    public static String createWarning(String msgKey, Object args[]) {
        if (XSLTBundle == null) {
            XSLTBundle = SecuritySupport.getResourceBundle(XSLT_ERROR_RESOURCES);
        }

        if (XSLTBundle != null) {
            return createMsg(XSLTBundle, msgKey, args);
        } else {
            return "Could not load any resource bundles.";
        }
    }
}
