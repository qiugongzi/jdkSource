


package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class NamespaceMappings
{

    private int count;


    private HashMap m_namespaces = new HashMap();


    private Stack m_nodeStack = new Stack();

    private static final String EMPTYSTRING = "";
    private static final String XML_PREFIX = "xml"; public NamespaceMappings()
    {
        initNamespaces();
    }


    private void initNamespaces()
    {


        Stack stack;
        m_namespaces.put(EMPTYSTRING, stack = new Stack());
        stack.push(new MappingRecord(EMPTYSTRING,EMPTYSTRING,0));

        m_namespaces.put(XML_PREFIX, stack = new Stack());
        stack.push(new MappingRecord( XML_PREFIX,
            "http:m_nodeStack.push(new MappingRecord(null,null,-1));

    }


    public String lookupNamespace(String prefix)
    {
        final Stack stack = (Stack) m_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ?
            ((MappingRecord) stack.peek()).m_uri : null;
    }

    MappingRecord getMappingFromPrefix(String prefix) {
        final Stack stack = (Stack) m_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ?
            ((MappingRecord) stack.peek()) : null;
    }


    public String lookupPrefix(String uri)
    {
        String foundPrefix = null;
        Iterator<String> itr = m_namespaces.keySet().iterator();
        while (itr.hasNext()) {
            String prefix = itr.next();
            String uri2 = lookupNamespace(prefix);
            if (uri2 != null && uri2.equals(uri))
            {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }

    MappingRecord getMappingFromURI(String uri)
    {
        MappingRecord foundMap = null;
        Iterator<String> itr = m_namespaces.keySet().iterator();
        while (itr.hasNext())
        {
            String prefix = itr.next();
            MappingRecord map2 = getMappingFromPrefix(prefix);
            if (map2 != null && (map2.m_uri).equals(uri))
            {
                foundMap = map2;
                break;
            }
        }
        return foundMap;
    }


    boolean popNamespace(String prefix)
    {
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        if ((stack = (Stack) m_namespaces.get(prefix)) != null)
        {
            stack.pop();
            return true;
        }
        return false;
    }


    boolean pushNamespace(String prefix, String uri, int elemDepth)
    {
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        if ((stack = (Stack) m_namespaces.get(prefix)) == null)
        {
            m_namespaces.put(prefix, stack = new Stack());
        }

        if (!stack.empty() && uri.equals(((MappingRecord)stack.peek()).m_uri))
        {
            return false;
        }
        MappingRecord map = new MappingRecord(prefix,uri,elemDepth);
        stack.push(map);
        m_nodeStack.push(map);
        return true;
    }


    void popNamespaces(int elemDepth, ContentHandler saxHandler)
    {
        while (true)
        {
            if (m_nodeStack.isEmpty())
                return;
            MappingRecord map = (MappingRecord)(m_nodeStack.peek());
            int depth = map.m_declarationDepth;
            if (depth < elemDepth)
                return;


            map = (MappingRecord) m_nodeStack.pop();
            final String prefix = map.m_prefix;
            popNamespace(prefix);
            if (saxHandler != null)
            {
                try
                {
                    saxHandler.endPrefixMapping(prefix);
                }
                catch (SAXException e)
                {
                    }
            }

        }
    }


    public String generateNextPrefix()
    {
        return "ns" + (count++);
    }



    public Object clone() throws CloneNotSupportedException {
        NamespaceMappings clone = new NamespaceMappings();
        clone.m_nodeStack = (Stack) m_nodeStack.clone();
        clone.m_namespaces = (HashMap) m_namespaces.clone();
        clone.count = count;
        return clone;

    }

    final void reset()
    {
        this.count = 0;
        this.m_namespaces.clear();
        this.m_nodeStack.clear();
        initNamespaces();
    }

    class MappingRecord {
        final String m_prefix;  final String m_uri;     final int m_declarationDepth;
        MappingRecord(String prefix, String uri, int depth) {
            m_prefix = prefix;
            m_uri = uri;
            m_declarationDepth = depth;

        }
    }

}
