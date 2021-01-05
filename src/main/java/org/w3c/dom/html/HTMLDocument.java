



package org.w3c.dom.html;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public interface HTMLDocument extends Document {

    public String getTitle();
    public void setTitle(String title);


    public String getReferrer();


    public String getDomain();


    public String getURL();


    public HTMLElement getBody();
    public void setBody(HTMLElement body);


    public HTMLCollection getImages();


    public HTMLCollection getApplets();


    public HTMLCollection getLinks();


    public HTMLCollection getForms();


    public HTMLCollection getAnchors();


    public String getCookie();
    public void setCookie(String cookie);


    public void open();


    public void close();


    public void write(String text);


    public void writeln(String text);


    public NodeList getElementsByName(String elementName);

}
