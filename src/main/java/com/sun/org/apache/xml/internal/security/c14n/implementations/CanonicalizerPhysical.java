

package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;


public class CanonicalizerPhysical extends CanonicalizerBase {

    private final SortedSet<Attr> result = new TreeSet<Attr>(COMPARE);


    public CanonicalizerPhysical() {
        super(true);
    }


    public byte[] engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces)
        throws CanonicalizationException {


        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }


    public byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces)
        throws CanonicalizationException {


        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }


    @Override
    protected Iterator<Attr> handleAttributesSubtree(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        if (!element.hasAttributes()) {
            return null;
        }

        final SortedSet<Attr> result = this.result;
        result.clear();

        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();

            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                result.add(attribute);
            }
        }

        return result.iterator();
    }


    @Override
    protected Iterator<Attr> handleAttributes(Element element, NameSpaceSymbTable ns)
        throws CanonicalizationException {


        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    protected void circumventBugIfNeeded(XMLSignatureInput input)
        throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        }

    @Override
    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        }


    public final String engineGetURI() {
        return Canonicalizer.ALGO_ID_C14N_PHYSICAL;
    }


    public final boolean engineGetIncludeComments() {
        return true;
    }

    @Override
    protected void outputPItoWriter(ProcessingInstruction currentPI,
                                    OutputStream writer, int position) throws IOException {
        super.outputPItoWriter(currentPI, writer, NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT);
    }

    @Override
    protected void outputCommentToWriter(Comment currentComment,
                                         OutputStream writer, int position) throws IOException {
        super.outputCommentToWriter(currentComment, writer, NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT);
    }

}
