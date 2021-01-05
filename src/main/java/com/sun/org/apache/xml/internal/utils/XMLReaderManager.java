


package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.XalanConstants;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import java.util.HashMap;

import javax.xml.XMLConstants;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class XMLReaderManager {

    private static final XMLReaderManager m_singletonManager =
                                                     new XMLReaderManager();
    private static final String property = "org.xml.sax.driver";


    private ThreadLocal<ReaderWrapper> m_readers;

    private boolean m_overrideDefaultParser;


    private HashMap m_inUse;

    private boolean _secureProcessing;

    private String _accessExternalDTD = XalanConstants.EXTERNAL_ACCESS_DEFAULT;

    private XMLSecurityManager _xmlSecurityManager;


    private XMLReaderManager() {
    }


    public static XMLReaderManager getInstance(boolean overrideDefaultParser) {
        m_singletonManager.setOverrideDefaultParser(overrideDefaultParser);
        return m_singletonManager;
    }


    public synchronized XMLReader getXMLReader() throws SAXException {
        XMLReader reader;

        if (m_readers == null) {
            m_readers = new ThreadLocal();
        }

        if (m_inUse == null) {
            m_inUse = new HashMap();
        }


        ReaderWrapper rw = m_readers.get();
        boolean threadHasReader = (rw != null);
        reader = threadHasReader ? rw.reader : null;
        String factory = SecuritySupport.getSystemProperty(property);
        if (threadHasReader && m_inUse.get(reader) != Boolean.TRUE &&
                (rw.overrideDefaultParser == m_overrideDefaultParser) &&
                ( factory == null || reader.getClass().getName().equals(factory))) {
            m_inUse.put(reader, Boolean.TRUE);
        } else {
            reader = JdkXmlUtils.getXMLReader(m_overrideDefaultParser, _secureProcessing);
            if (!threadHasReader) {
                m_readers.set(new ReaderWrapper(reader, m_overrideDefaultParser));
                m_inUse.put(reader, Boolean.TRUE);
            }
        }

        try {
            reader.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, _accessExternalDTD);
        } catch (SAXException se) {
            XMLSecurityManager.printWarning(reader.getClass().getName(),
                    XMLConstants.ACCESS_EXTERNAL_DTD, se);
        }

        String lastProperty = "";
        try {
            if (_xmlSecurityManager != null) {
                for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                    lastProperty = limit.apiProperty();
                    reader.setProperty(lastProperty,
                            _xmlSecurityManager.getLimitValueAsString(limit));
                }
                if (_xmlSecurityManager.printEntityCountInfo()) {
                    lastProperty = XalanConstants.JDK_ENTITY_COUNT_INFO;
                    reader.setProperty(XalanConstants.JDK_ENTITY_COUNT_INFO, XalanConstants.JDK_YES);
                }
            }
        } catch (SAXException se) {
            XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, se);
        }

        return reader;
    }


    public synchronized void releaseXMLReader(XMLReader reader) {
        ReaderWrapper rw = m_readers.get();
        if (rw.reader == reader && reader != null) {
            m_inUse.remove(reader);
        }
    }

    public boolean overrideDefaultParser() {
        return m_overrideDefaultParser;
    }


    public void setOverrideDefaultParser(boolean flag) {
        m_overrideDefaultParser = flag;
    }


    public void setFeature(String name, boolean value) {
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            _secureProcessing = value;
        }
    }


    public Object getProperty(String name) {
        if (name.equals(XMLConstants.ACCESS_EXTERNAL_DTD)) {
            return _accessExternalDTD;
        } else if (name.equals(XalanConstants.SECURITY_MANAGER)) {
            return _xmlSecurityManager;
        }
        return null;
    }


    public void setProperty(String name, Object value) {
        if (name.equals(XMLConstants.ACCESS_EXTERNAL_DTD)) {
            _accessExternalDTD = (String)value;
        } else if (name.equals(XalanConstants.SECURITY_MANAGER)) {
            _xmlSecurityManager = (XMLSecurityManager)value;
        }
    }

    class ReaderWrapper {
        XMLReader reader;
        boolean overrideDefaultParser;

        public ReaderWrapper(XMLReader reader, boolean overrideDefaultParser) {
            this.reader = reader;
            this.overrideDefaultParser = overrideDefaultParser;
        }
    }
}
