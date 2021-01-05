

package org.xml.sax.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class NamespaceSupport
{


    public final static String XMLNS =
        "http:public final static String NSDECL =
        "http:private final static Enumeration EMPTY_ENUMERATION =
            Collections.enumeration(new ArrayList<String>());


    public NamespaceSupport ()
    {
        reset();
    }



    public void reset ()
    {
        contexts = new Context[32];
        namespaceDeclUris = false;
        contextPos = 0;
        contexts[contextPos] = currentContext = new Context();
        currentContext.declarePrefix("xml", XMLNS);
    }



    public void pushContext ()
    {
        int max = contexts.length;

        contextPos++;

                                if (contextPos >= max) {
            Context newContexts[] = new Context[max*2];
            System.arraycopy(contexts, 0, newContexts, 0, max);
            max *= 2;
            contexts = newContexts;
        }

                                currentContext = contexts[contextPos];
        if (currentContext == null) {
            contexts[contextPos] = currentContext = new Context();
        }

                                if (contextPos > 0) {
            currentContext.setParent(contexts[contextPos - 1]);
        }
    }



    public void popContext ()
    {
        contexts[contextPos].clear();
        contextPos--;
        if (contextPos < 0) {
            throw new EmptyStackException();
        }
        currentContext = contexts[contextPos];
    }



    public boolean declarePrefix (String prefix, String uri)
    {
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
            return false;
        } else {
            currentContext.declarePrefix(prefix, uri);
            return true;
        }
    }



    public String [] processName (String qName, String parts[],
                                  boolean isAttribute)
    {
        String myParts[] = currentContext.processName(qName, isAttribute);
        if (myParts == null) {
            return null;
        } else {
            parts[0] = myParts[0];
            parts[1] = myParts[1];
            parts[2] = myParts[2];
            return parts;
        }
    }



    public String getURI (String prefix)
    {
        return currentContext.getURI(prefix);
    }



    public Enumeration getPrefixes ()
    {
        return currentContext.getPrefixes();
    }



    public String getPrefix (String uri)
    {
        return currentContext.getPrefix(uri);
    }



    public Enumeration getPrefixes (String uri)
    {
        List<String> prefixes = new ArrayList<>();
        Enumeration allPrefixes = getPrefixes();
        while (allPrefixes.hasMoreElements()) {
            String prefix = (String)allPrefixes.nextElement();
            if (uri.equals(getURI(prefix))) {
                prefixes.add(prefix);
            }
        }
        return Collections.enumeration(prefixes);
    }



    public Enumeration getDeclaredPrefixes ()
    {
        return currentContext.getDeclaredPrefixes();
    }


    public void setNamespaceDeclUris (boolean value)
    {
        if (contextPos != 0)
            throw new IllegalStateException ();
        if (value == namespaceDeclUris)
            return;
        namespaceDeclUris = value;
        if (value)
            currentContext.declarePrefix ("xmlns", NSDECL);
        else {
            contexts[contextPos] = currentContext = new Context();
            currentContext.declarePrefix("xml", XMLNS);
        }
    }


    public boolean isNamespaceDeclUris ()
        { return namespaceDeclUris; }



    private Context contexts[];
    private Context currentContext;
    private int contextPos;
    private boolean namespaceDeclUris;


    final class Context {


        Context ()
        {
            copyTables();
        }



        void setParent (Context parent)
        {
            this.parent = parent;
            declarations = null;
            prefixTable = parent.prefixTable;
            uriTable = parent.uriTable;
            elementNameTable = parent.elementNameTable;
            attributeNameTable = parent.attributeNameTable;
            defaultNS = parent.defaultNS;
            declSeen = false;
        }


        void clear ()
        {
            parent = null;
            prefixTable = null;
            uriTable = null;
            elementNameTable = null;
            attributeNameTable = null;
            defaultNS = null;
        }



        void declarePrefix (String prefix, String uri)
        {
                                if (!declSeen) {
                copyTables();
            }
            if (declarations == null) {
                declarations = new ArrayList<>();
            }

            prefix = prefix.intern();
            uri = uri.intern();
            if ("".equals(prefix)) {
                if ("".equals(uri)) {
                    defaultNS = null;
                } else {
                    defaultNS = uri;
                }
            } else {
                prefixTable.put(prefix, uri);
                uriTable.put(uri, prefix); }
            declarations.add(prefix);
        }



        String [] processName (String qName, boolean isAttribute)
        {
            String name[];
            Map<String, String[]> table;

            if (isAttribute) {
                table = attributeNameTable;
            } else {
                table = elementNameTable;
            }

            name = (String[])table.get(qName);
            if (name != null) {
                return name;
            }

            name = new String[3];
            name[2] = qName.intern();
            int index = qName.indexOf(':');


            if (index == -1) {
                if (isAttribute) {
                    if (qName == "xmlns" && namespaceDeclUris)
                        name[0] = NSDECL;
                    else
                        name[0] = "";
                } else if (defaultNS == null) {
                    name[0] = "";
                } else {
                    name[0] = defaultNS;
                }
                name[1] = name[2];
            }

            else {
                String prefix = qName.substring(0, index);
                String local = qName.substring(index+1);
                String uri;
                if ("".equals(prefix)) {
                    uri = defaultNS;
                } else {
                    uri = (String)prefixTable.get(prefix);
                }
                if (uri == null
                        || (!isAttribute && "xmlns".equals (prefix))) {
                    return null;
                }
                name[0] = uri;
                name[1] = local.intern();
            }

            table.put(name[2], name);
            return name;
        }



        String getURI (String prefix)
        {
            if ("".equals(prefix)) {
                return defaultNS;
            } else if (prefixTable == null) {
                return null;
            } else {
                return (String)prefixTable.get(prefix);
            }
        }



        String getPrefix (String uri)
        {
            if (uriTable == null) {
                return null;
            } else {
                return (String)uriTable.get(uri);
            }
        }



        Enumeration getDeclaredPrefixes ()
        {
            if (declarations == null) {
                return EMPTY_ENUMERATION;
            } else {
                return Collections.enumeration(declarations);
            }
        }


        Enumeration getPrefixes ()
        {
            if (prefixTable == null) {
                return EMPTY_ENUMERATION;
            } else {
                return Collections.enumeration(prefixTable.keySet());
            }
        }



        private void copyTables ()
        {
            if (prefixTable != null) {
                prefixTable = new HashMap<>(prefixTable);
            } else {
                prefixTable = new HashMap<>();
            }
            if (uriTable != null) {
                uriTable = new HashMap<>(uriTable);
            } else {
                uriTable = new HashMap<>();
            }
            elementNameTable = new HashMap<>();
            attributeNameTable = new HashMap<>();
            declSeen = true;
        }



        Map<String, String> prefixTable;
        Map<String, String> uriTable;
        Map<String, String[]> elementNameTable;
        Map<String, String[]> attributeNameTable;
        String defaultNS = null;



        private List<String> declarations = null;
        private boolean declSeen = false;
        private Context parent = null;
    }
}

