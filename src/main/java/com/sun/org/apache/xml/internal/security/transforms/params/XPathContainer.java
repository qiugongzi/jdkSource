

package com.sun.org.apache.xml.internal.security.transforms.params;


import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class XPathContainer extends SignatureElementProxy implements TransformParam {


    public XPathContainer(Document doc) {
        super(doc);
    }


    public void setXPath(String xpath) {
        if (this.constructionElement.getChildNodes() != null) {
            NodeList nl = this.constructionElement.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                this.constructionElement.removeChild(nl.item(i));
            }
        }

        Text xpathText = this.doc.createTextNode(xpath);
        this.constructionElement.appendChild(xpathText);
    }


    public String getXPath() {
        return this.getTextFromTextChild();
    }


    public String getBaseLocalName() {
        return Constants._TAG_XPATH;
    }
}
