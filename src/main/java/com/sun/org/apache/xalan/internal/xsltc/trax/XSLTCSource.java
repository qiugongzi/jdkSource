




package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

import org.xml.sax.SAXException;


public final class XSLTCSource implements Source {

    private String     _systemId = null;
    private Source     _source   = null;
    private ThreadLocal _dom     = new ThreadLocal();


    public XSLTCSource(String systemId)
    {
        _systemId = systemId;
    }


    public XSLTCSource(Source source)
    {
        _source = source;
    }


    public void setSystemId(String systemId) {
        _systemId = systemId;
        if (_source != null) {
            _source.setSystemId(systemId);
        }
    }


    public String getSystemId() {
        if (_source != null) {
            return _source.getSystemId();
        }
        else {
            return(_systemId);
        }
    }


    protected DOM getDOM(XSLTCDTMManager dtmManager, AbstractTranslet translet)
        throws SAXException
    {
        SAXImpl idom = (SAXImpl)_dom.get();

        if (idom != null) {
            if (dtmManager != null) {
                idom.migrateTo(dtmManager);
            }
        }
        else {
            Source source = _source;
            if (source == null) {
                if (_systemId != null && _systemId.length() > 0) {
                    source = new StreamSource(_systemId);
                }
                else {
                    ErrorMsg err = new ErrorMsg(ErrorMsg.XSLTC_SOURCE_ERR);
                    throw new SAXException(err.toString());
                }
            }

            DOMWSFilter wsfilter = null;
            if (translet != null && translet instanceof StripFilter) {
                wsfilter = new DOMWSFilter(translet);
            }

            boolean hasIdCall = (translet != null) ? translet.hasIdCall() : false;

            if (dtmManager == null) {
                dtmManager = XSLTCDTMManager.newInstance();
            }

            idom = (SAXImpl)dtmManager.getDTM(source, true, wsfilter, false, false, hasIdCall);

            String systemId = getSystemId();
            if (systemId != null) {
                idom.setDocumentURI(systemId);
            }
            _dom.set(idom);
        }
        return idom;
    }

}
