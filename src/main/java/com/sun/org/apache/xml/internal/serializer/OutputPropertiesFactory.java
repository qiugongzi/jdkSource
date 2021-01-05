


package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;


public final class OutputPropertiesFactory
{

    private static final String
      S_BUILTIN_EXTENSIONS_URL = "http:private static final String
      S_BUILTIN_OLD_EXTENSIONS_URL = "http:/
    public static final String S_BUILTIN_EXTENSIONS_UNIVERSAL =
        "{" + S_BUILTIN_EXTENSIONS_URL + "}";

    public static final String S_KEY_INDENT_AMOUNT =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "indent-amount";


    public static final String S_KEY_LINE_SEPARATOR =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "line-separator";



    public static final String S_KEY_CONTENT_HANDLER =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "content-handler";


    public static final String S_KEY_ENTITIES =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "entities";


    public static final String S_USE_URL_ESCAPING =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "use-url-escaping";


    public static final String S_OMIT_META_TAG =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "omit-meta-tag";


    public static final String S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL =
        "{" + S_BUILTIN_OLD_EXTENSIONS_URL + "}";


    public static final int S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN =
        S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL.length();



    public static final String ORACLE_IS_STANDALONE = "http:/
    private static Integer m_synch_object = new Integer(1);


    private static final String PROP_DIR = "com/sun/org/apache/xml/internal/serializer/";

    private static final String PROP_FILE_XML = "output_xml.properties";

    private static final String PROP_FILE_TEXT = "output_text.properties";

    private static final String PROP_FILE_HTML = "output_html.properties";

    private static final String PROP_FILE_UNKNOWN = "output_unknown.properties";

    /
    private static Properties m_xml_properties = null;


    private static Properties m_html_properties = null;


    private static Properties m_text_properties = null;


    private static Properties m_unknown_properties = null;

    private static final Class
        ACCESS_CONTROLLER_CLASS = findAccessControllerClass();

    private static Class findAccessControllerClass() {
        try
        {
            return Class.forName("java.security.AccessController");
        }
        catch (Exception e)
        {
            }

        return null;
    }


    static public final Properties getDefaultMethodProperties(String method)
    {
        String fileName = null;
        Properties defaultProperties = null;
        try
        {
            synchronized (m_synch_object)
            {
                if (null == m_xml_properties) {
                    fileName = PROP_FILE_XML;
                    m_xml_properties = loadPropertiesFile(fileName, null);
                }
            }

            if (method.equals(Method.XML))
            {
                defaultProperties = m_xml_properties;
            }
            else if (method.equals(Method.HTML))
            {
                if (null == m_html_properties) {
                    fileName = PROP_FILE_HTML;
                    m_html_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                }

                defaultProperties = m_html_properties;
            }
            else if (method.equals(Method.TEXT))
            {
                if (null == m_text_properties) {
                    fileName = PROP_FILE_TEXT;
                    m_text_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                    if (null
                        == m_text_properties.getProperty(OutputKeys.ENCODING))
                    {
                        String mimeEncoding = Encodings.getMimeEncoding(null);
                        m_text_properties.put(
                            OutputKeys.ENCODING,
                            mimeEncoding);
                    }
                }

                defaultProperties = m_text_properties;
            }
            else if (method.equals(com.sun.org.apache.xml.internal.serializer.Method.UNKNOWN))
            {
                if (null == m_unknown_properties) {
                    fileName = PROP_FILE_UNKNOWN;
                    m_unknown_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                }

                defaultProperties = m_unknown_properties;
            }
            else
            {
                defaultProperties = m_xml_properties;
            }
        }
        catch (IOException ioe)
        {
            throw new WrappedRuntimeException(
                Utils.messages.createMessage(
                    MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                    new Object[] { fileName, method }),
                ioe);
        }
        return new Properties(defaultProperties);
    }


    static private Properties loadPropertiesFile(
        final String resourceName,
        Properties defaults)
        throws IOException
    {

        Properties props = new Properties(defaults);

        InputStream is = null;
        BufferedInputStream bis = null;

        try
        {
            if (ACCESS_CONTROLLER_CLASS != null)
            {
                is = (InputStream) AccessController
                    .doPrivileged(new PrivilegedAction() {
                        public Object run()
                        {
                            return OutputPropertiesFactory.class
                                .getResourceAsStream(resourceName);
                        }
                    });
            }
            else
            {
                is = OutputPropertiesFactory.class
                    .getResourceAsStream(resourceName);
            }

            bis = new BufferedInputStream(is);
            props.load(bis);
        }
        catch (IOException ioe)
        {
            if (defaults == null)
            {
                throw ioe;
            }
            else
            {
                throw new WrappedRuntimeException(
                    Utils.messages.createMessage(
                        MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                        new Object[] { resourceName }),
                    ioe);
                }
        }
        catch (SecurityException se)
        {
            if (defaults == null)
            {
                throw se;
            }
            else
            {
                throw new WrappedRuntimeException(
                    Utils.messages.createMessage(
                        MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                        new Object[] { resourceName }),
                    se);
                }
        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
            if (is != null)
            {
                is.close();
            }
        }

        Enumeration keys = ((Properties) props.clone()).keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            String value = null;
            try
            {
                value = SecuritySupport.getSystemProperty(key);
            }
            catch (SecurityException se)
            {
                }
            if (value == null)
                value = (String) props.get(key);

            String newKey = fixupPropertyString(key, true);
            String newValue = null;
            try
            {
                newValue = SecuritySupport.getSystemProperty(newKey);
            }
            catch (SecurityException se)
            {
                }
            if (newValue == null)
                newValue = fixupPropertyString(value, false);
            else
                newValue = fixupPropertyString(newValue, false);

            if (key != newKey || value != newValue)
            {
                props.remove(key);
                props.put(newKey, newValue);
            }

        }

        return props;
    }


    static private String fixupPropertyString(String s, boolean doClipping)
    {
        int index;
        if (doClipping && s.startsWith(S_XSLT_PREFIX))
        {
            s = s.substring(S_XSLT_PREFIX_LEN);
        }
        if (s.startsWith(S_XALAN_PREFIX))
        {
            s =
                S_BUILTIN_EXTENSIONS_UNIVERSAL
                    + s.substring(S_XALAN_PREFIX_LEN);
        }
        if ((index = s.indexOf("\\u003a")) > 0)
        {
            String temp = s.substring(index + 6);
            s = s.substring(0, index) + ":" + temp;

        }
        return s;
    }

}
