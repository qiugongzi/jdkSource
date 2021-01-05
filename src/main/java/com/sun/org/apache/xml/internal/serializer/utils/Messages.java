


package com.sun.org.apache.xml.internal.serializer.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public final class Messages
{

    private final Locale m_locale = Locale.getDefault();


    private ListResourceBundle m_resourceBundle;


    private String m_resourceBundleName;




    Messages(String resourceBundle)
    {

        m_resourceBundleName = resourceBundle;
    }



    private Locale getLocale()
    {
        return m_locale;
    }


    public final String createMessage(String msgKey, Object args[])
    {
        if (m_resourceBundle == null)
            m_resourceBundle = SecuritySupport.getResourceBundle(m_resourceBundleName);

        if (m_resourceBundle != null)
        {
            return createMsg(m_resourceBundle, msgKey, args);
        }
        else
            return "Could not load the resource bundles: "+ m_resourceBundleName;
    }


    private final String createMsg(
        ListResourceBundle fResourceBundle,
        String msgKey,
        Object args[]) {

        String fmsg = null;
        boolean throwex = false;
        String msg = null;

        if (msgKey != null)
            msg = fResourceBundle.getString(msgKey);
        else
            msgKey = "";

        if (msg == null)
        {
            throwex = true;

            try
            {

                msg =
                    java.text.MessageFormat.format(
                        MsgKey.BAD_MSGKEY,
                        new Object[] { msgKey, m_resourceBundleName });
            }
            catch (Exception e)
            {

                msg =
                    "The message key '"
                        + msgKey
                        + "' is not in the message class '"
                        + m_resourceBundleName+"'";
            }
        }
        else if (args != null)
        {
            try
            {
                int n = args.length;

                for (int i = 0; i < n; i++)
                {
                    if (null == args[i])
                        args[i] = "";
                }

                fmsg = java.text.MessageFormat.format(msg, args);
                }
            catch (Exception e)
            {
                throwex = true;
                try
                {
                    fmsg =
                        java.text.MessageFormat.format(
                            MsgKey.BAD_MSGFORMAT,
                            new Object[] { msgKey, m_resourceBundleName });
                    fmsg += " " + msg;
                }
                catch (Exception formatfailed)
                {
                    fmsg =
                        "The format of message '"
                            + msgKey
                            + "' in message class '"
                            + m_resourceBundleName
                            + "' failed.";
                }
            }
        }
        else
            fmsg = msg;

        if (throwex)
        {
            throw new RuntimeException(fmsg);
        }

        return fmsg;
    }

}
