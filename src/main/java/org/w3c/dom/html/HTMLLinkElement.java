



package org.w3c.dom.html;


public interface HTMLLinkElement extends HTMLElement {

    public boolean getDisabled();
    public void setDisabled(boolean disabled);


    public String getCharset();
    public void setCharset(String charset);


    public String getHref();
    public void setHref(String href);


    public String getHreflang();
    public void setHreflang(String hreflang);


    public String getMedia();
    public void setMedia(String media);


    public String getRel();
    public void setRel(String rel);


    public String getRev();
    public void setRev(String rev);


    public String getTarget();
    public void setTarget(String target);


    public String getType();
    public void setType(String type);

}