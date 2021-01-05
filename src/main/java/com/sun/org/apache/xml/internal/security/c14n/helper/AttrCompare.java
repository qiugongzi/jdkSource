

package com.sun.org.apache.xml.internal.security.c14n.helper;

import com.sun.org.apache.xml.internal.security.utils.Constants;
import org.w3c.dom.Attr;
import java.io.Serializable;
import java.util.Comparator;


public class AttrCompare implements Comparator<Attr>, Serializable {

    private static final long serialVersionUID = -7113259629930576230L;
    private static final int ATTR0_BEFORE_ATTR1 = -1;
    private static final int ATTR1_BEFORE_ATTR0 = 1;
    private static final String XMLNS = Constants.NamespaceSpecNS;


    public int compare(Attr attr0, Attr attr1) {
        String namespaceURI0 = attr0.getNamespaceURI();
        String namespaceURI1 = attr1.getNamespaceURI();

        boolean isNamespaceAttr0 = XMLNS.equals(namespaceURI0);
        boolean isNamespaceAttr1 = XMLNS.equals(namespaceURI1);

        if (isNamespaceAttr0) {
            if (isNamespaceAttr1) {
                String localname0 = attr0.getLocalName();
                String localname1 = attr1.getLocalName();

                if ("xmlns".equals(localname0)) {
                    localname0 = "";
                }

                if ("xmlns".equals(localname1)) {
                    localname1 = "";
                }

                return localname0.compareTo(localname1);
            }
            return ATTR0_BEFORE_ATTR1;
        } else if (isNamespaceAttr1) {
            return ATTR1_BEFORE_ATTR0;
        }

        if (namespaceURI0 == null) {
            if (namespaceURI1 == null) {
                String name0 = attr0.getName();
                String name1 = attr1.getName();
                return name0.compareTo(name1);
            }
            return ATTR0_BEFORE_ATTR1;
        } else if (namespaceURI1 == null) {
            return ATTR1_BEFORE_ATTR0;
        }

        int a = namespaceURI0.compareTo(namespaceURI1);
        if (a != 0) {
            return a;
        }

        return (attr0.getLocalName()).compareTo(attr1.getLocalName());
    }
}
