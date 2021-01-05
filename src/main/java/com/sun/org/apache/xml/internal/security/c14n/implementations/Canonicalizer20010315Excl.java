

package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {

    private static final String XML_LANG_URI = Constants.XML_LANG_SPACE_SpecNS;
    private static final String XMLNS_URI = Constants.NamespaceSpecNS;


    private SortedSet<String> inclusiveNSSet;

    private final SortedSet<Attr> result = new TreeSet<Attr>(COMPARE);


    public Canonicalizer20010315Excl(boolean includeComments) {
        super(includeComments);
    }


    public byte[] engineCanonicalizeSubTree(Node rootNode)
        throws CanonicalizationException {
        return engineCanonicalizeSubTree(rootNode, "", null);
    }


    public byte[] engineCanonicalizeSubTree(
        Node rootNode, String inclusiveNamespaces
    ) throws CanonicalizationException {
        return engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, null);
    }


    public byte[] engineCanonicalizeSubTree(
        Node rootNode, String inclusiveNamespaces, Node excl
    ) throws CanonicalizationException{
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalizeSubTree(rootNode, excl);
    }


    public byte[] engineCanonicalize(
        XMLSignatureInput rootNode, String inclusiveNamespaces
    ) throws CanonicalizationException {
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalize(rootNode);
    }


    public byte[] engineCanonicalizeXPathNodeSet(
        Set<Node> xpathNodeSet, String inclusiveNamespaces
    ) throws CanonicalizationException {
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalizeXPathNodeSet(xpathNodeSet);
    }

    @Override
    protected Iterator<Attr> handleAttributesSubtree(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        final SortedSet<Attr> result = this.result;
        result.clear();

        SortedSet<String> visiblyUtilized = new TreeSet<String>();
        if (inclusiveNSSet != null && !inclusiveNSSet.isEmpty()) {
            visiblyUtilized.addAll(inclusiveNSSet);
        }

        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();

                if (!XMLNS_URI.equals(attribute.getNamespaceURI())) {
                    String prefix = attribute.getPrefix();
                    if (prefix != null && !(prefix.equals(XML) || prefix.equals(XMLNS))) {
                        visiblyUtilized.add(prefix);
                    }
                    result.add(attribute);
                } else if (!(XML.equals(NName) && XML_LANG_URI.equals(NNodeValue))
                    && ns.addMapping(NName, NNodeValue, attribute)
                    && C14nHelper.namespaceIsRelative(NNodeValue)) {
                    Object exArgs[] = {element.getTagName(), NName, attribute.getNodeValue()};
                    throw new CanonicalizationException(
                        "c14n.Canonicalizer.RelativeNamespace", exArgs
                    );
                }
            }
        }
        String prefix = null;
        if (element.getNamespaceURI() != null
            && !(element.getPrefix() == null || element.getPrefix().length() == 0)) {
            prefix = element.getPrefix();
        } else {
            prefix = XMLNS;
        }
        visiblyUtilized.add(prefix);

        for (String s : visiblyUtilized) {
            Attr key = ns.getMapping(s);
            if (key != null) {
                result.add(key);
            }
        }

        return result.iterator();
    }


    @Override
    protected final Iterator<Attr> handleAttributes(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        final SortedSet<Attr> result = this.result;
        result.clear();

        Set<String> visiblyUtilized = null;
        boolean isOutputElement = isVisibleDO(element, ns.getLevel()) == 1;
        if (isOutputElement) {
            visiblyUtilized = new TreeSet<String>();
            if (inclusiveNSSet != null && !inclusiveNSSet.isEmpty()) {
                visiblyUtilized.addAll(inclusiveNSSet);
            }
        }

        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);

                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();

                if (!XMLNS_URI.equals(attribute.getNamespaceURI())) {
                    if (isVisible(attribute) && isOutputElement) {
                        String prefix = attribute.getPrefix();
                        if (prefix != null && !(prefix.equals(XML) || prefix.equals(XMLNS))) {
                            visiblyUtilized.add(prefix);
                        }
                        result.add(attribute);
                    }
                } else if (isOutputElement && !isVisible(attribute) && !XMLNS.equals(NName)) {
                    ns.removeMappingIfNotRender(NName);
                } else {
                    if (!isOutputElement && isVisible(attribute)
                        && inclusiveNSSet.contains(NName)
                        && !ns.removeMappingIfRender(NName)) {
                        Node n = ns.addMappingAndRender(NName, NNodeValue, attribute);
                        if (n != null) {
                            result.add((Attr)n);
                            if (C14nHelper.namespaceIsRelative(attribute)) {
                                Object exArgs[] = { element.getTagName(), NName, attribute.getNodeValue() };
                                throw new CanonicalizationException(
                                    "c14n.Canonicalizer.RelativeNamespace", exArgs
                                );
                            }
                        }
                    }

                    if (ns.addMapping(NName, NNodeValue, attribute)
                        && C14nHelper.namespaceIsRelative(NNodeValue)) {
                        Object exArgs[] = { element.getTagName(), NName, attribute.getNodeValue() };
                        throw new CanonicalizationException(
                            "c14n.Canonicalizer.RelativeNamespace", exArgs
                        );
                    }
                }
            }
        }

        if (isOutputElement) {
            Attr xmlns = element.getAttributeNodeNS(XMLNS_URI, XMLNS);
            if (xmlns != null && !isVisible(xmlns)) {
                ns.addMapping(XMLNS, "", getNullNode(xmlns.getOwnerDocument()));
            }

            String prefix = null;
            if (element.getNamespaceURI() != null
                && !(element.getPrefix() == null || element.getPrefix().length() == 0)) {
                prefix = element.getPrefix();
            } else {
                prefix = XMLNS;
            }
            visiblyUtilized.add(prefix);

            for (String s : visiblyUtilized) {
                Attr key = ns.getMapping(s);
                if (key != null) {
                    result.add(key);
                }
            }
        }

        return result.iterator();
    }

    protected void circumventBugIfNeeded(XMLSignatureInput input)
        throws CanonicalizationException, ParserConfigurationException,
               IOException, SAXException {
        if (!input.isNeedsToBeExpanded() || inclusiveNSSet.isEmpty() || inclusiveNSSet.isEmpty()) {
            return;
        }
        Document doc = null;
        if (input.getSubNode() != null) {
            doc = XMLUtils.getOwnerDocument(input.getSubNode());
        } else {
            doc = XMLUtils.getOwnerDocument(input.getNodeSet());
        }
        XMLUtils.circumventBug2650(doc);
    }
}
