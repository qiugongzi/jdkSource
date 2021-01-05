



package org.w3c.dom.stylesheets;

import org.w3c.dom.Node;


public interface StyleSheet {

    public String getType();


    public boolean getDisabled();

    public void setDisabled(boolean disabled);


    public Node getOwnerNode();


    public StyleSheet getParentStyleSheet();


    public String getHref();


    public String getTitle();


    public MediaList getMedia();

}
