



package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;


public final class AttributesImplSerializer extends AttributesImpl
{

    private final Map<String, Integer> m_indexFromQName = new HashMap<>();

    private final StringBuffer m_buff = new StringBuffer();


    private static final int MAX = 12;


    private static final int MAXMinus1 = MAX - 1;


    public final int getIndex(String qname)
    {
        int index;

        if (super.getLength() < MAX)
        {
            index = super.getIndex(qname);
            return index;
        }
        Integer i = m_indexFromQName.get(qname);
        if (i == null)
            index = -1;
        else
            index = i.intValue();
        return index;
    }

    public final void addAttribute(
        String uri,
        String local,
        String qname,
        String type,
        String val)
    {
        int index = super.getLength();
        super.addAttribute(uri, local, qname, type, val);
        if (index < MAXMinus1)
        {
            return;
        }
        else if (index == MAXMinus1)
        {
            switchOverToHash(MAX);
        }
        else
        {


            Integer i = index;
            m_indexFromQName.put(qname, i);


            m_buff.setLength(0);
            m_buff.append('{').append(uri).append('}').append(local);
            String key = m_buff.toString();
            m_indexFromQName.put(key, i);
        }
    }


    private void switchOverToHash(int numAtts)
    {
        for (int index = 0; index < numAtts; index++)
        {
            String qName = super.getQName(index);
            Integer i = index;
            m_indexFromQName.put(qName, i);

            String uri = super.getURI(index);
            String local = super.getLocalName(index);
            m_buff.setLength(0);
            m_buff.append('{').append(uri).append('}').append(local);
            String key = m_buff.toString();
            m_indexFromQName.put(key, i);
        }
    }


    public final void clear()
    {

        int len = super.getLength();
        super.clear();
        if (MAX <= len)
        {
            m_indexFromQName.clear();
        }

    }


    public final void setAttributes(Attributes atts)
    {

        super.setAttributes(atts);

        int numAtts = atts.getLength();
        if (MAX <= numAtts)
            switchOverToHash(numAtts);

    }


    public final int getIndex(String uri, String localName)
    {
        int index;

        if (super.getLength() < MAX)
        {
            index = super.getIndex(uri,localName);
            return index;
        }
        m_buff.setLength(0);
        m_buff.append('{').append(uri).append('}').append(localName);
        String key = m_buff.toString();
        Integer i = m_indexFromQName.get(key);
        if (i == null)
            index = -1;
        else
            index = i;
        return index;
    }
}
