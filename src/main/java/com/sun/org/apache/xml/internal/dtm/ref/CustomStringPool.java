


package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.HashMap;
import java.util.Map;


public class CustomStringPool extends DTMStringPool {

    final Map<String, Integer> m_stringToInt = new HashMap<>();
    public static final int NULL = -1;

    public CustomStringPool() {
        super();
    }

    public void removeAllElements() {
        m_intToString.removeAllElements();
        if (m_stringToInt != null) {
            m_stringToInt.clear();
        }
    }


    @Override
    public String indexToString(int i)
            throws java.lang.ArrayIndexOutOfBoundsException {
        return (String) m_intToString.elementAt(i);
    }


    @Override
    public int stringToIndex(String s) {
        if (s == null) {
            return NULL;
        }
        Integer iobj = m_stringToInt.get(s);
        if (iobj == null) {
            m_intToString.addElement(s);
            iobj = m_intToString.size();
            m_stringToInt.put(s, iobj);
        }
        return iobj;
    }
}
