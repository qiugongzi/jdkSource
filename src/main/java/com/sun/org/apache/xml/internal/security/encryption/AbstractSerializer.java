

package com.sun.org.apache.xml.internal.security.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public abstract class AbstractSerializer implements Serializer {

    protected Canonicalizer canon;

    public void setCanonicalizer(Canonicalizer canon) {
        this.canon = canon;
    }


    public String serialize(Element element) throws Exception {
        return canonSerialize(element);
    }


    public byte[] serializeToByteArray(Element element) throws Exception {
        return canonSerializeToByteArray(element);
    }


    public String serialize(NodeList content) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        canon.setWriter(baos);
        canon.notReset();
        for (int i = 0; i < content.getLength(); i++) {
            canon.canonicalizeSubtree(content.item(i));
        }
        String ret = baos.toString("UTF-8");
        baos.reset();
        return ret;
    }


    public byte[] serializeToByteArray(NodeList content) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        canon.setWriter(baos);
        canon.notReset();
        for (int i = 0; i < content.getLength(); i++) {
            canon.canonicalizeSubtree(content.item(i));
        }
        return baos.toByteArray();
    }


    public String canonSerialize(Node node) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        canon.setWriter(baos);
        canon.notReset();
        canon.canonicalizeSubtree(node);
        String ret = baos.toString("UTF-8");
        baos.reset();
        return ret;
    }


    public byte[] canonSerializeToByteArray(Node node) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        canon.setWriter(baos);
        canon.notReset();
        canon.canonicalizeSubtree(node);
        return baos.toByteArray();
    }


    public abstract Node deserialize(String source, Node ctx) throws XMLEncryptionException;


    public abstract Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException;

    protected static byte[] createContext(byte[] source, Node ctx) throws XMLEncryptionException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
            outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");

            Map<String, String> storedNamespaces = new HashMap<String, String>();
            Node wk = ctx;
            while (wk != null) {
                NamedNodeMap atts = wk.getAttributes();
                if (atts != null) {
                    for (int i = 0; i < atts.getLength(); ++i) {
                        Node att = atts.item(i);
                        String nodeName = att.getNodeName();
                        if ((nodeName.equals("xmlns") || nodeName.startsWith("xmlns:"))
                                && !storedNamespaces.containsKey(att.getNodeName())) {
                            outputStreamWriter.write(" ");
                            outputStreamWriter.write(nodeName);
                            outputStreamWriter.write("=\"");
                            outputStreamWriter.write(att.getNodeValue());
                            outputStreamWriter.write("\"");
                            storedNamespaces.put(nodeName, att.getNodeValue());
                        }
                    }
                }
                wk = wk.getParentNode();
            }
            outputStreamWriter.write(">");
            outputStreamWriter.flush();
            byteArrayOutputStream.write(source);

            outputStreamWriter.write("</dummy>");
            outputStreamWriter.close();

            return byteArrayOutputStream.toByteArray();
        } catch (UnsupportedEncodingException e) {
            throw new XMLEncryptionException("empty", e);
        } catch (IOException e) {
            throw new XMLEncryptionException("empty", e);
        }
    }

    protected static String createContext(String source, Node ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");

        Map<String, String> storedNamespaces = new HashMap<String, String>();
        Node wk = ctx;
        while (wk != null) {
            NamedNodeMap atts = wk.getAttributes();
            if (atts != null) {
                for (int i = 0; i < atts.getLength(); ++i) {
                    Node att = atts.item(i);
                    String nodeName = att.getNodeName();
                    if ((nodeName.equals("xmlns") || nodeName.startsWith("xmlns:"))
                        && !storedNamespaces.containsKey(att.getNodeName())) {
                        sb.append(" " + nodeName + "=\"" + att.getNodeValue() + "\"");
                        storedNamespaces.put(nodeName, att.getNodeValue());
                    }
                }
            }
            wk = wk.getParentNode();
        }
        sb.append(">" + source + "</dummy>");
        return sb.toString();
    }

}
