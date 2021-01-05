

package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


@Deprecated
public abstract class ElementCheckerImpl implements ElementChecker {

    public boolean isNamespaceElement(Node el, String type, String ns) {
        if ((el == null) ||
            ns != el.getNamespaceURI() || !el.getLocalName().equals(type)){
            return false;
        }

        return true;
    }


    public static class InternedNsChecker extends ElementCheckerImpl {
        public void guaranteeThatElementInCorrectSpace(
            ElementProxy expected, Element actual
        ) throws XMLSecurityException {

            String expectedLocalname = expected.getBaseLocalName();
            String expectedNamespace = expected.getBaseNamespace();

            String localnameIS = actual.getLocalName();
            String namespaceIS = actual.getNamespaceURI();
            if ((expectedNamespace != namespaceIS) ||
                !expectedLocalname.equals(localnameIS)) {
                Object exArgs[] = { namespaceIS + ":" + localnameIS,
                                    expectedNamespace + ":" + expectedLocalname};
                throw new XMLSecurityException("xml.WrongElement", exArgs);
            }
        }
    }


    public static class FullChecker extends ElementCheckerImpl {

        public void guaranteeThatElementInCorrectSpace(
            ElementProxy expected, Element actual
        ) throws XMLSecurityException {
            String expectedLocalname = expected.getBaseLocalName();
            String expectedNamespace = expected.getBaseNamespace();

            String localnameIS = actual.getLocalName();
            String namespaceIS = actual.getNamespaceURI();
            if ((!expectedNamespace.equals(namespaceIS)) ||
                !expectedLocalname.equals(localnameIS) ) {
                Object exArgs[] = { namespaceIS + ":" + localnameIS,
                                    expectedNamespace + ":" + expectedLocalname};
                throw new XMLSecurityException("xml.WrongElement", exArgs);
            }
        }
    }


    public static class EmptyChecker extends ElementCheckerImpl {
        public void guaranteeThatElementInCorrectSpace(
            ElementProxy expected, Element actual
        ) throws XMLSecurityException {
            }
    }
}
