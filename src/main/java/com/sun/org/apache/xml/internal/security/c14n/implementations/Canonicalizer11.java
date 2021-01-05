

package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;


public abstract class Canonicalizer11 extends CanonicalizerBase {

    private static final String XMLNS_URI = Constants.NamespaceSpecNS;
    private static final String XML_LANG_URI = Constants.XML_LANG_SPACE_SpecNS;
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Canonicalizer11.class.getName());
    private final SortedSet<Attr> result = new TreeSet<Attr>(COMPARE);

    private boolean firstCall = true;

    private static class XmlAttrStack {
        static class XmlsStackElement {
            int level;
            boolean rendered = false;
            List<Attr> nodes = new ArrayList<Attr>();
        };

        int currentLevel = 0;
        int lastlevel = 0;
        XmlsStackElement cur;
        List<XmlsStackElement> levels = new ArrayList<XmlsStackElement>();

        void push(int level) {
            currentLevel = level;
            if (currentLevel == -1) {
                return;
            }
            cur = null;
            while (lastlevel >= currentLevel) {
                levels.remove(levels.size() - 1);
                int newSize = levels.size();
                if (newSize == 0) {
                    lastlevel = 0;
                    return;
                }
                lastlevel = (levels.get(newSize - 1)).level;
            }
        }

        void addXmlnsAttr(Attr n) {
            if (cur == null) {
                cur = new XmlsStackElement();
                cur.level = currentLevel;
                levels.add(cur);
                lastlevel = currentLevel;
            }
            cur.nodes.add(n);
        }

        void getXmlnsAttr(Collection<Attr> col) {
            int size = levels.size() - 1;
            if (cur == null) {
                cur = new XmlsStackElement();
                cur.level = currentLevel;
                lastlevel = currentLevel;
                levels.add(cur);
            }
            boolean parentRendered = false;
            XmlsStackElement e = null;
            if (size == -1) {
                parentRendered = true;
            } else {
                e = levels.get(size);
                if (e.rendered && e.level + 1 == currentLevel) {
                    parentRendered = true;
                }
            }
            if (parentRendered) {
                col.addAll(cur.nodes);
                cur.rendered = true;
                return;
            }

            Map<String, Attr> loa = new HashMap<String, Attr>();
            List<Attr> baseAttrs = new ArrayList<Attr>();
            boolean successiveOmitted = true;
            for (; size >= 0; size--) {
                e = levels.get(size);
                if (e.rendered) {
                    successiveOmitted = false;
                }
                Iterator<Attr> it = e.nodes.iterator();
                while (it.hasNext() && successiveOmitted) {
                    Attr n = it.next();
                    if (n.getLocalName().equals("base") && !e.rendered) {
                        baseAttrs.add(n);
                    } else if (!loa.containsKey(n.getName())) {
                        loa.put(n.getName(), n);
                    }
                }
            }
            if (!baseAttrs.isEmpty()) {
                Iterator<Attr> it = col.iterator();
                String base = null;
                Attr baseAttr = null;
                while (it.hasNext()) {
                    Attr n = it.next();
                    if (n.getLocalName().equals("base")) {
                        base = n.getValue();
                        baseAttr = n;
                        break;
                    }
                }
                it = baseAttrs.iterator();
                while (it.hasNext()) {
                    Attr n = it.next();
                    if (base == null) {
                        base = n.getValue();
                        baseAttr = n;
                    } else {
                        try {
                            base = joinURI(n.getValue(), base);
                        } catch (URISyntaxException ue) {
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, ue.getMessage(), ue);
                            }
                        }
                    }
                }
                if (base != null && base.length() != 0) {
                    baseAttr.setValue(base);
                    col.add(baseAttr);
                }
            }

            cur.rendered = true;
            col.addAll(loa.values());
        }
    };

    private XmlAttrStack xmlattrStack = new XmlAttrStack();


    public Canonicalizer11(boolean includeComments) {
        super(includeComments);
    }


    public byte[] engineCanonicalizeXPathNodeSet(
        Set<Node> xpathNodeSet, String inclusiveNamespaces
    ) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }


    public byte[] engineCanonicalizeSubTree(
        Node rootNode, String inclusiveNamespaces
    ) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }


    @Override
    protected Iterator<Attr> handleAttributesSubtree(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        if (!element.hasAttributes() && !firstCall) {
            return null;
        }
        final SortedSet<Attr> result = this.result;
        result.clear();

        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();

            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();

                if (!XMLNS_URI.equals(NUri)) {
                    result.add(attribute);
                } else if (!(XML.equals(NName) && XML_LANG_URI.equals(NValue))) {
                    Node n = ns.addMappingAndRender(NName, NValue, attribute);

                    if (n != null) {
                        result.add((Attr)n);
                        if (C14nHelper.namespaceIsRelative(attribute)) {
                            Object exArgs[] = {element.getTagName(), NName, attribute.getNodeValue()};
                            throw new CanonicalizationException(
                                "c14n.Canonicalizer.RelativeNamespace", exArgs
                            );
                        }
                    }
                }
            }
        }

        if (firstCall) {
            ns.getUnrenderedNodes(result);
            xmlattrStack.getXmlnsAttr(result);
            firstCall = false;
        }

        return result.iterator();
    }


    @Override
    protected Iterator<Attr> handleAttributes(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        xmlattrStack.push(ns.getLevel());
        boolean isRealVisible = isVisibleDO(element, ns.getLevel()) == 1;
        final SortedSet<Attr> result = this.result;
        result.clear();

        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();

            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();

                if (!XMLNS_URI.equals(NUri)) {
                    if (XML_LANG_URI.equals(NUri)) {
                        if (NName.equals("id")) {
                            if (isRealVisible) {
                                result.add(attribute);
                            }
                        } else {
                            xmlattrStack.addXmlnsAttr(attribute);
                        }
                    } else if (isRealVisible) {
                        result.add(attribute);
                    }
                } else if (!XML.equals(NName) || !XML_LANG_URI.equals(NValue)) {

                    if (isVisible(attribute))  {
                        if (isRealVisible || !ns.removeMappingIfRender(NName)) {
                            Node n = ns.addMappingAndRender(NName, NValue, attribute);
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
                    } else {
                        if (isRealVisible && !XMLNS.equals(NName)) {
                            ns.removeMapping(NName);
                        } else {
                            ns.addMapping(NName, NValue, attribute);
                        }
                    }
                }
            }
        }

        if (isRealVisible) {
            Attr xmlns = element.getAttributeNodeNS(XMLNS_URI, XMLNS);
            Node n = null;
            if (xmlns == null) {
                n = ns.getMapping(XMLNS);
            } else if (!isVisible(xmlns)) {
                n = ns.addMappingAndRender(
                        XMLNS, "", getNullNode(xmlns.getOwnerDocument()));
            }
            if (n != null) {
                result.add((Attr)n);
            }
            xmlattrStack.getXmlnsAttr(result);
            ns.getUnrenderedNodes(result);
        }

        return result.iterator();
    }

    protected void circumventBugIfNeeded(XMLSignatureInput input)
        throws CanonicalizationException, ParserConfigurationException,
        IOException, SAXException {
        if (!input.isNeedsToBeExpanded()) {
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

    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        if (!e.hasAttributes() && e.getNamespaceURI() == null) {
            return;
        }
        xmlattrStack.push(-1);
        NamedNodeMap attrs = e.getAttributes();
        int attrsLength = attrs.getLength();
        for (int i = 0; i < attrsLength; i++) {
            Attr attribute = (Attr) attrs.item(i);
            String NName = attribute.getLocalName();
            String NValue = attribute.getNodeValue();

            if (Constants.NamespaceSpecNS.equals(attribute.getNamespaceURI())) {
                if (!XML.equals(NName) || !Constants.XML_LANG_SPACE_SpecNS.equals(NValue)) {
                    ns.addMapping(NName, NValue, attribute);
                }
            } else if (!"id".equals(NName) && XML_LANG_URI.equals(attribute.getNamespaceURI())) {
                xmlattrStack.addXmlnsAttr(attribute);
            }
        }
        if (e.getNamespaceURI() != null) {
            String NName = e.getPrefix();
            String NValue = e.getNamespaceURI();
            String Name;
            if (NName == null || NName.equals("")) {
                NName = "xmlns";
                Name = "xmlns";
            } else {
                Name = "xmlns:" + NName;
            }
            Attr n = e.getOwnerDocument().createAttributeNS("http:n.setValue(NValue);
            ns.addMapping(NName, NValue, n);
        }
    }

    private static String joinURI(String baseURI, String relativeURI) throws URISyntaxException {
        String bscheme = null;
        String bauthority = null;
        String bpath = "";
        String bquery = null;

        if (baseURI != null) {
            if (baseURI.endsWith("..")) {
                baseURI = baseURI + "/";
            }
            URI base = new URI(baseURI);
            bscheme = base.getScheme();
            bauthority = base.getAuthority();
            bpath = base.getPath();
            bquery = base.getQuery();
        }

        URI r = new URI(relativeURI);
        String rscheme = r.getScheme();
        String rauthority = r.getAuthority();
        String rpath = r.getPath();
        String rquery = r.getQuery();

        String tscheme, tauthority, tpath, tquery;
        if (rscheme != null && rscheme.equals(bscheme)) {
            rscheme = null;
        }
        if (rscheme != null) {
            tscheme = rscheme;
            tauthority = rauthority;
            tpath = removeDotSegments(rpath);
            tquery = rquery;
        } else {
            if (rauthority != null) {
                tauthority = rauthority;
                tpath = removeDotSegments(rpath);
                tquery = rquery;
            } else {
                if (rpath.length() == 0) {
                    tpath = bpath;
                    if (rquery != null) {
                        tquery = rquery;
                    } else {
                        tquery = bquery;
                    }
                } else {
                    if (rpath.startsWith("/")) {
                        tpath = removeDotSegments(rpath);
                    } else {
                        if (bauthority != null && bpath.length() == 0) {
                            tpath = "/" + rpath;
                        } else {
                            int last = bpath.lastIndexOf('/');
                            if (last == -1) {
                                tpath = rpath;
                            } else {
                                tpath = bpath.substring(0, last+1) + rpath;
                            }
                        }
                        tpath = removeDotSegments(tpath);
                    }
                    tquery = rquery;
                }
                tauthority = bauthority;
            }
            tscheme = bscheme;
        }
        return new URI(tscheme, tauthority, tpath, tquery, null).toString();
    }

    private static String removeDotSegments(String path) {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER");
        }

        String input = path;
        while (input.indexOf("input = input.replaceAll("}

        StringBuilder output = new StringBuilder();

        if (input.charAt(0) == '/') {
            output.append("/");
            input = input.substring(1);
        }

        printStep("1 ", output.toString(), input);

        while (input.length() != 0) {
            if (input.startsWith("./")) {
                input = input.substring(2);
                printStep("2A", output.toString(), input);
            } else if (input.startsWith("../")) {
                input = input.substring(3);
                if (!output.toString().equals("/")) {
                    output.append("../");
                }
                printStep("2A", output.toString(), input);
                } else if (input.startsWith("/./")) {
                input = input.substring(2);
                printStep("2B", output.toString(), input);
            } else if (input.equals("/.")) {
                input = input.replaceFirst("/.", "/");
                printStep("2B", output.toString(), input);
                } else if (input.startsWith("/../")) {
                input = input.substring(3);
                if (output.length() == 0) {
                    output.append("/");
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    int index = output.lastIndexOf("/");
                    if (index == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index, output.length());
                    }
                }
                printStep("2C", output.toString(), input);
            } else if (input.equals("/..")) {
                input = input.replaceFirst("/..", "/");
                if (output.length() == 0) {
                    output.append("/");
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    int index = output.lastIndexOf("/");
                    if (index == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index, output.length());
                    }
                }
                printStep("2C", output.toString(), input);
                } else if (input.equals(".")) {
                input = "";
                printStep("2D", output.toString(), input);
            } else if (input.equals("..")) {
                if (!output.toString().equals("/")) {
                    output.append("..");
                }
                input = "";
                printStep("2D", output.toString(), input);
                } else {
                int end = -1;
                int begin = input.indexOf('/');
                if (begin == 0) {
                    end = input.indexOf('/', 1);
                } else {
                    end = begin;
                    begin = 0;
                }
                String segment;
                if (end == -1) {
                    segment = input.substring(begin);
                    input = "";
                } else {
                    segment = input.substring(begin, end);
                    input = input.substring(end);
                }
                output.append(segment);
                printStep("2E", output.toString(), input);
            }
        }

        if (output.toString().endsWith("..")) {
            output.append("/");
            printStep("3 ", output.toString(), input);
        }

        return output.toString();
    }

    private static void printStep(String step, String output, String input) {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, " " + step + ":   " + output);
            if (output.length() == 0) {
                log.log(java.util.logging.Level.FINE, "\t\t\t\t" + input);
            } else {
                log.log(java.util.logging.Level.FINE, "\t\t\t" + input);
            }
        }
    }
}
