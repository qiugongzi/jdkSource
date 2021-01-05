

package javax.xml.crypto.dsig.dom;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import java.security.Key;
import org.w3c.dom.Node;


public class DOMValidateContext extends DOMCryptoContext
    implements XMLValidateContext {

    private Node node;


    public DOMValidateContext(KeySelector ks, Node node) {
        if (ks == null) {
            throw new NullPointerException("key selector is null");
        }
        init(node, ks);
    }


    public DOMValidateContext(Key validatingKey, Node node) {
        if (validatingKey == null) {
            throw new NullPointerException("validatingKey is null");
        }
        init(node, KeySelector.singletonKeySelector(validatingKey));
    }

    private void init(Node node, KeySelector ks) {
        if (node == null) {
            throw new NullPointerException("node is null");
        }

        this.node = node;
        super.setKeySelector(ks);
        if (System.getSecurityManager() != null) {
            super.setProperty("org.jcp.xml.dsig.secureValidation",
                              Boolean.TRUE);
        }
    }


    public void setNode(Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        this.node = node;
    }


    public Node getNode() {
        return node;
    }
}
