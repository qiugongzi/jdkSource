

package com.sun.org.apache.xml.internal.security.transforms.params;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class InclusiveNamespaces extends ElementProxy implements TransformParam {


    public static final String _TAG_EC_INCLUSIVENAMESPACES =
        "InclusiveNamespaces";


    public static final String _ATT_EC_PREFIXLIST = "PrefixList";


    public static final String ExclusiveCanonicalizationNamespace =
        "http:public InclusiveNamespaces(Document doc, String prefixList) {
        this(doc, InclusiveNamespaces.prefixStr2Set(prefixList));
    }


    public InclusiveNamespaces(Document doc, Set<String> prefixes) {
        super(doc);

        SortedSet<String> prefixList = null;
        if (prefixes instanceof SortedSet<?>) {
            prefixList = (SortedSet<String>)prefixes;
        } else {
            prefixList = new TreeSet<String>(prefixes);
        }

        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixList) {
            if (prefix.equals("xmlns")) {
                sb.append("#default ");
            } else {
                sb.append(prefix + " ");
            }
        }

        this.constructionElement.setAttributeNS(
            null, InclusiveNamespaces._ATT_EC_PREFIXLIST, sb.toString().trim());
    }


    public InclusiveNamespaces(Element element, String BaseURI)
        throws XMLSecurityException {
        super(element, BaseURI);
    }


    public String getInclusiveNamespaces() {
        return this.constructionElement.getAttributeNS(null, InclusiveNamespaces._ATT_EC_PREFIXLIST);
    }


    public static SortedSet<String> prefixStr2Set(String inclusiveNamespaces) {
        SortedSet<String> prefixes = new TreeSet<String>();

        if ((inclusiveNamespaces == null) || (inclusiveNamespaces.length() == 0)) {
            return prefixes;
        }

        String[] tokens = inclusiveNamespaces.split("\\s");
        for (String prefix : tokens) {
            if (prefix.equals("#default")) {
                prefixes.add("xmlns");
            } else {
                prefixes.add(prefix);
            }
        }

        return prefixes;
    }


    public String getBaseNamespace() {
        return InclusiveNamespaces.ExclusiveCanonicalizationNamespace;
    }


    public String getBaseLocalName() {
        return InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES;
    }
}
