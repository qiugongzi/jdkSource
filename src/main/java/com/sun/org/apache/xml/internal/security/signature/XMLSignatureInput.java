

package com.sun.org.apache.xml.internal.security.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerBase;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class XMLSignatureInput {



    private InputStream inputOctetStreamProxy = null;

    private Set<Node> inputNodeSet = null;

    private Node subNode = null;

    private Node excludeNode = null;

    private boolean excludeComments = false;

    private boolean isNodeSet = false;

    private byte[] bytes = null;


    private String mimeType = null;


    private String sourceURI = null;


    private List<NodeFilter> nodeFilters = new ArrayList<NodeFilter>();

    private boolean needsToBeExpanded = false;
    private OutputStream outputStream = null;

    private DocumentBuilderFactory dfactory;


    public XMLSignatureInput(byte[] inputOctets) {
        this.bytes = inputOctets;
    }


    public XMLSignatureInput(InputStream inputOctetStream)  {
        this.inputOctetStreamProxy = inputOctetStream;
    }


    public XMLSignatureInput(Node rootNode) {
        this.subNode = rootNode;
    }


    public XMLSignatureInput(Set<Node> inputNodeSet) {
        this.inputNodeSet = inputNodeSet;
    }


    public boolean isNeedsToBeExpanded() {
        return needsToBeExpanded;
    }


    public void setNeedsToBeExpanded(boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }


    public Set<Node> getNodeSet() throws CanonicalizationException, ParserConfigurationException,
        IOException, SAXException {
        return getNodeSet(false);
    }


    public Set<Node> getInputNodeSet() {
        return inputNodeSet;
    }


    public Set<Node> getNodeSet(boolean circumvent) throws ParserConfigurationException,
        IOException, SAXException, CanonicalizationException {
        if (inputNodeSet != null) {
            return inputNodeSet;
        }
        if (inputOctetStreamProxy == null && subNode != null) {
            if (circumvent) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(subNode));
            }
            inputNodeSet = new LinkedHashSet<Node>();
            XMLUtils.getSet(subNode, inputNodeSet, excludeNode, excludeComments);
            return inputNodeSet;
        } else if (isOctetStream()) {
            convertToNodes();
            Set<Node> result = new LinkedHashSet<Node>();
            XMLUtils.getSet(subNode, result, null, false);
            return result;
        }

        throw new RuntimeException("getNodeSet() called but no input data present");
    }


    public InputStream getOctetStream() throws IOException  {
        if (inputOctetStreamProxy != null) {
            return inputOctetStreamProxy;
        }

        if (bytes != null) {
            inputOctetStreamProxy = new ByteArrayInputStream(bytes);
            return inputOctetStreamProxy;
        }

        return null;
    }


    public InputStream getOctetStreamReal() {
        return inputOctetStreamProxy;
    }


    public byte[] getBytes() throws IOException, CanonicalizationException {
        byte[] inputBytes = getBytesFromInputStream();
        if (inputBytes != null) {
            return inputBytes;
        }
        Canonicalizer20010315OmitComments c14nizer = new Canonicalizer20010315OmitComments();
        bytes = c14nizer.engineCanonicalize(this);
        return bytes;
    }


    public boolean isNodeSet() {
        return ((inputOctetStreamProxy == null
            && inputNodeSet != null) || isNodeSet);
    }


    public boolean isElement() {
        return (inputOctetStreamProxy == null && subNode != null
            && inputNodeSet == null && !isNodeSet);
    }


    public boolean isOctetStream() {
        return ((inputOctetStreamProxy != null || bytes != null)
          && (inputNodeSet == null && subNode == null));
    }


    public boolean isOutputStreamSet() {
        return outputStream != null;
    }


    public boolean isByteArray() {
        return (bytes != null && (this.inputNodeSet == null && subNode == null));
    }


    public boolean isInitialized() {
        return isOctetStream() || isNodeSet();
    }


    public String getMIMEType() {
        return mimeType;
    }


    public void setMIMEType(String mimeType) {
        this.mimeType = mimeType;
    }


    public String getSourceURI() {
        return sourceURI;
    }


    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }


    public String toString() {
        if (isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + inputNodeSet.size()
                   + " nodes/" + getSourceURI();
        }
        if (isElement()) {
            return "XMLSignatureInput/Element/" + subNode
                + " exclude "+ excludeNode + " comments:"
                + excludeComments +"/" + getSourceURI();
        }
        try {
            return "XMLSignatureInput/OctetStream/" + getBytes().length
                   + " octets/" + getSourceURI();
        } catch (IOException iex) {
            return "XMLSignatureInput/OctetStream} catch (CanonicalizationException cex) {
            return "XMLSignatureInput/OctetStream}
    }


    public String getHTMLRepresentation() throws XMLSignatureException {
        XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this);
        return db.getHTMLRepresentation();
    }


    public String getHTMLRepresentation(Set<String> inclusiveNamespaces)
       throws XMLSignatureException {
        XMLSignatureInputDebugger db =
            new XMLSignatureInputDebugger(this, inclusiveNamespaces);
        return db.getHTMLRepresentation();
    }


    public Node getExcludeNode() {
        return excludeNode;
    }


    public void setExcludeNode(Node excludeNode) {
        this.excludeNode = excludeNode;
    }


    public Node getSubNode() {
        return subNode;
    }


    public boolean isExcludeComments() {
        return excludeComments;
    }


    public void setExcludeComments(boolean excludeComments) {
        this.excludeComments = excludeComments;
    }


    public void updateOutputStream(OutputStream diOs)
        throws CanonicalizationException, IOException {
        updateOutputStream(diOs, false);
    }

    public void updateOutputStream(OutputStream diOs, boolean c14n11)
        throws CanonicalizationException, IOException {
        if (diOs == outputStream) {
            return;
        }
        if (bytes != null) {
            diOs.write(bytes);
        } else if (inputOctetStreamProxy == null) {
            CanonicalizerBase c14nizer = null;
            if (c14n11) {
                c14nizer = new Canonicalizer11_OmitComments();
            } else {
                c14nizer = new Canonicalizer20010315OmitComments();
            }
            c14nizer.setWriter(diOs);
            c14nizer.engineCanonicalize(this);
        } else {
            byte[] buffer = new byte[4 * 1024];
            int bytesread = 0;
            try {
                while ((bytesread = inputOctetStreamProxy.read(buffer)) != -1) {
                    diOs.write(buffer, 0, bytesread);
                }
            } catch (IOException ex) {
                inputOctetStreamProxy.close();
                throw ex;
            }
        }
    }


    public void setOutputStream(OutputStream os) {
        outputStream = os;
    }

    private byte[] getBytesFromInputStream() throws IOException {
        if (bytes != null) {
            return bytes;
        }
        if (inputOctetStreamProxy == null) {
            return null;
        }
        try {
            bytes = JavaUtils.getBytesFromStream(inputOctetStreamProxy);
        } finally {
            inputOctetStreamProxy.close();
        }
        return bytes;
    }


    public void addNodeFilter(NodeFilter filter) {
        if (isOctetStream()) {
            try {
                convertToNodes();
            } catch (Exception e) {
                throw new XMLSecurityRuntimeException(
                    "signature.XMLSignatureInput.nodesetReference", e
                );
            }
        }
        nodeFilters.add(filter);
    }


    public List<NodeFilter> getNodeFilters() {
        return nodeFilters;
    }


    public void setNodeSet(boolean b) {
        isNodeSet = b;
    }

    void convertToNodes() throws CanonicalizationException,
        ParserConfigurationException, IOException, SAXException {
        if (dfactory == null) {
            dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            dfactory.setValidating(false);
            dfactory.setNamespaceAware(true);
        }
        DocumentBuilder db = dfactory.newDocumentBuilder();
        try {
            db.setErrorHandler(new com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler());

            Document doc = db.parse(this.getOctetStream());
            this.subNode = doc;
        } catch (SAXException ex) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            baos.write("<container>".getBytes("UTF-8"));
            baos.write(this.getBytes());
            baos.write("</container>".getBytes("UTF-8"));

            byte result[] = baos.toByteArray();
            Document document = db.parse(new ByteArrayInputStream(result));
            this.subNode = document.getDocumentElement().getFirstChild().getFirstChild();
        } finally {
            if (this.inputOctetStreamProxy != null) {
                this.inputOctetStreamProxy.close();
            }
            this.inputOctetStreamProxy = null;
            this.bytes = null;
        }
    }

}
