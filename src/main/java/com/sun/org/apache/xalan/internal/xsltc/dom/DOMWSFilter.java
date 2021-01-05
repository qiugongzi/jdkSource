


package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import java.util.HashMap;
import java.util.Map;


public class DOMWSFilter implements DTMWSFilter {

    private AbstractTranslet m_translet;
    private StripFilter m_filter;

    private Map<DTM, short[]> m_mappings;

    private DTM m_currentDTM;
    private short[] m_currentMapping;


    public DOMWSFilter(AbstractTranslet translet) {
        m_translet = translet;
        m_mappings = new HashMap<>();

        if (translet instanceof StripFilter) {
            m_filter = (StripFilter) translet;
        }
    }


    public short getShouldStripSpace(int node, DTM dtm) {
        if (m_filter != null && dtm instanceof DOM) {
            DOM dom = (DOM)dtm;
            int type = 0;

            if (dtm instanceof DOMEnhancedForDTM) {
                DOMEnhancedForDTM mappableDOM = (DOMEnhancedForDTM)dtm;

                short[] mapping;
                if (dtm == m_currentDTM) {
                    mapping = m_currentMapping;
                }
                else {
                    mapping = m_mappings.get(dtm);
                    if (mapping == null) {
                        mapping = mappableDOM.getMapping(
                                     m_translet.getNamesArray(),
                                     m_translet.getUrisArray(),
                                     m_translet.getTypesArray());
                        m_mappings.put(dtm, mapping);
                        m_currentDTM = dtm;
                        m_currentMapping = mapping;
                    }
                }

                int expType = mappableDOM.getExpandedTypeID(node);

                if (expType >= 0 && expType < mapping.length)
                  type = mapping[expType];
                else
                  type = -1;

            }
            else {
                return INHERIT;
            }

            if (m_filter.stripSpace(dom, node, type)) {
                return STRIP;
            } else {
                return NOTSTRIP;
            }
        } else {
            return NOTSTRIP;
        }
    }
}
